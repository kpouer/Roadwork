/*
 * Copyright 2022-2023 Matthieu Casanova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kpouer.roadwork.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpouer.mapview.LatLng;
import com.kpouer.mapview.tile.TileServer;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.model.*;
import com.kpouer.roadwork.model.sync.Status;
import com.kpouer.roadwork.opendata.OpendataService;
import com.kpouer.roadwork.opendata.json.DefaultJsonService;
import com.kpouer.roadwork.opendata.json.model.ServiceDescriptor;
import com.kpouer.roadwork.service.exception.OpenDataException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static com.kpouer.roadwork.service.ResourceService.OPENDATA_JSON;
import static com.kpouer.roadwork.service.ResourceService.THIRDPARTY;

/**
 * @author Matthieu Casanova
 */
@AllArgsConstructor
@Slf4j
@Service
public class OpendataServiceManager {
    private static final String VERSION = "2";

    private final HttpService httpService;
    private final Config config;
    private final ApplicationContext applicationContext;
    private final SynchronizationService synchronizationService;
    private final ResourceService resourceService;

    /**
     * Retrieve service names
     *
     * @return a list of service names (json file names)
     */
    public List<String> getServices() {
        logger.info("getServices");
        var services = getDefaultServices();
        var thirdPartyServices = getThirdPartyServices();
        var allServices = new ArrayList<String>(services.size() + thirdPartyServices.size());
        allServices.addAll(services);
        allServices.addAll(thirdPartyServices);
        return allServices;
    }

    public List<String> getDefaultServices() {
        logger.info("getDefaultServices");
        var services = getServicesFromPath(OPENDATA_JSON);
        services.forEach(service -> logger.info("Default service : " + service));
        return services;
    }

    public List<String> getThirdPartyServices() {
        logger.info("getThirdPartyServices");
        var services = getServicesFromPath(THIRDPARTY);
        services.forEach(service -> logger.info("Third party service : " + service));
        return services;
    }

    /**
     * Retrieve service names
     *
     * @param folder the folder where the services descriptor are stored
     * @return a list of service names (json file names)
     */
    private List<String> getServicesFromPath(String folder) {
        var pathFolder = Path.of(folder);
        logger.info("getServicesFromPath {}", pathFolder.toAbsolutePath());
        try (var files = Files.list(pathFolder)) {
            var services = files
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::toFile)
                    .map(File::getName)
                    .toList();
            services.forEach(service -> logger.info("Disk service {}/{}", folder, service));
            return services;
        } catch (NoSuchFileException e) {
            logger.info("Path {} do not exist", pathFolder.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Unable to read opendata services", e);
        }
        return Collections.emptyList();
    }

    public Optional<RoadworkData> getData() throws RestClientException, IOException, OpenDataException {
        var roadworks = getRoadworks();
        roadworks.ifPresent(roadworkData -> {
            applyFinishedStatus(roadworkData);
            synchronizationService.synchronize(roadworkData);
        });
        return roadworks;
    }

    public void save(RoadworkData roadworkData) {
        logger.info("save {}", roadworkData.getSource());
        var objectMapper = new ObjectMapper();

        var savePath = getPath(roadworkData.getSource());
        try {
            Files.createDirectories(savePath.getParent());
            Files.write(savePath, objectMapper.writeValueAsBytes(roadworkData));
        } catch (IOException e) {
            logger.error("Unable to save cache to {}", savePath);
        }
    }

    /**
     * Returns the center of the current opendata service.
     *
     * @return some coordinates
     */
    public LatLng getCenter() {
        try {
            return getOpendataService().getMetadata().getCenter();
        } catch (OpenDataException e) {
            return new LatLng(48.85337, 2.34847);
        }
    }

    /**
     * Returns roadwork data.
     * If
     *
     * @return an optional that should contains Roadwork data
     * @throws RestClientException if a RestClientException happens
     */
    @NonNull
    private Optional<RoadworkData> getRoadworks() throws RestClientException, IOException, OpenDataException {
        var currentPath = getPath(config.getOpendataService());
        logger.info("getData {}", currentPath);
        var cachedDataOptional = loadCache(currentPath);
        if (cachedDataOptional.isEmpty()) {
            logger.info("There is no cached data");
            var newData = getOpendataService().getData();
            newData.ifPresent(this::save);
            return newData;
        }

        RoadworkData cachedRoadworkData = cachedDataOptional.get();
        if (cachedRoadworkData.getCreated() + 86400000 < System.currentTimeMillis()) {
            logger.info("Cache is obsolete {}", currentPath);
            Files.delete(currentPath);
            var newDataOptional = getOpendataService().getData();
            if (newDataOptional.isPresent()) {
                var newData = newDataOptional.get();
                var newRoadworks = newData.getRoadworks();
                logger.info("reloaded {} new roadworks", newRoadworks.size());
                for (var existingRoadwork : cachedRoadworkData) {
                    var newRoadwork = newRoadworks.get(existingRoadwork.getId());
                    if (newRoadwork != null) {
                        logger.info("Roadwork {} -> status {}", existingRoadwork.getId(), existingRoadwork.getSyncData().getStatus());
                        newRoadwork.getSyncData().copy(existingRoadwork.getSyncData());
                        existingRoadwork.updateMarker();
                    }
                }
                save(newData);
            }
            return newDataOptional;
        }

        return cachedDataOptional;
    }

    @NonNull
    public OpendataService getOpendataService() throws OpenDataException {
        logger.debug("getOpendataService");
        var opendataService = config.getOpendataService();
        return getOpendataService(opendataService);
    }

    public TileServer getTileServer() {
        String tileServerName;
        try {
            tileServerName = getOpendataService().getMetadata().getTileServer();
        } catch (OpenDataException e) {
            return config.getWazeINTLTileServer();
        }
        try {
            return applicationContext.getBean(tileServerName + "TileServer", TileServer.class);
        } catch (BeansException e) {
            logger.error("Error getting tile server " + tileServerName, e);
        }
        return config.getWazeINTLTileServer();
    }

    /**
     * Returns the OpendataService for that name.
     * If it is json, it comes from the jar or third party,
     * Otherwise it is a hardcoded class.
     *
     * @param opendataService the service name
     * @return an instance of OpendataService
     */
    @NonNull
    public OpendataService getOpendataService(String opendataService) throws OpenDataException {
        logger.info("getOpendataService {}", opendataService);
        if (opendataService.endsWith(".json")) {
            return getJsonService(opendataService);
        }
        try {
            return applicationContext.getBean(opendataService, OpendataService.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new OpenDataException(e.getMessage(), e);
        }
    }

    @NonNull
    private DefaultJsonService getJsonService(String opendataService) throws OpenDataException {
        logger.info("getJsonService {}", opendataService);
        try {
            var objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            var opendataServicePath = resourceService.getResource(opendataService);
            if (opendataServicePath.isPresent()) {
                var serviceDescriptor = objectMapper.readValue(opendataServicePath.get(), ServiceDescriptor.class);
                return new DefaultJsonService(opendataService, httpService, serviceDescriptor);
            }
        } catch (IOException e) {
            throw new OpenDataException("Unable to find service " + opendataService, e);
        }
        throw new OpenDataException("Unable to find service " + opendataService);
    }

    @NonNull
    private Path getPath(String opendataService) {
        return Path.of(config.getDataPath(), opendataService + '.' + VERSION + ".json");
    }

    private Optional<RoadworkData> loadCache(Path cachePath) {
        if (Files.exists(cachePath)) {
            var objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                RoadworkData roadworkData = objectMapper.readValue(cachePath.toFile(), RoadworkData.class);
                logger.info("Cache loaded");
                return Optional.of(roadworkData);
            } catch (IOException e) {
                logger.error("Unable to load cache {} {}", cachePath, e);
            }
        } else {
            return getMigratedData();
        }

        return Optional.empty();
    }

    private Optional<RoadworkData> getMigratedData() {
        logger.info("There is no cache, checking if there is old data");
        var beansOfType = applicationContext.getBeansOfType(RoadworkMigrationService.class);
        var migrationServices = beansOfType.values();
        for (var migrationService : migrationServices) {
            var roadworkData = migrationService.migrateData();
            if (roadworkData.isPresent()) {
                logger.info("Migrating service {}", migrationService);
                migrationService.archive();
                save(roadworkData.get());
                return roadworkData;
            }
        }
        return Optional.empty();
    }

    private static void applyFinishedStatus(RoadworkData roadworkData) {
        roadworkData
                .getRoadworks()
                .values()
                .stream()
                .filter(Roadwork::isExpired)
                .map(Roadwork::getSyncData)
                .filter(Objects::nonNull)
                .forEach(syncData -> syncData.setStatus(Status.Finished));
    }

    public void deleteCache() {
        logger.info("deleteCache {}", config.getOpendataService());
        var currentPath = getPath(config.getOpendataService());
        try {
            Files.deleteIfExists(currentPath);
        } catch (IOException e) {
            logger.error("Error deleting cache", e);
        }
    }
}
