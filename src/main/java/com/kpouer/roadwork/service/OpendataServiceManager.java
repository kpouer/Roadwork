/*
 * Copyright 2022 Matthieu Casanova
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
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.model.sync.Status;
import com.kpouer.roadwork.opendata.OpendataService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author Matthieu Casanova
 */
@Service
public class OpendataServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(OpendataServiceManager.class);
    private static final String VERSION = "2";

    private final Config config;
    private final ApplicationContext applicationContext;
    private final SynchronizationService synchronizationService;

    public OpendataServiceManager(Config config, ApplicationContext applicationContext, SynchronizationService synchronizationService) {
        this.config = config;
        this.applicationContext = applicationContext;
        this.synchronizationService = synchronizationService;
    }

    public Optional<RoadworkData> getData() throws IOException {
        Optional<RoadworkData> roadworks = getRoadworks();
        roadworks.ifPresent(roadworkData -> {
            applyFinishedStatus(roadworkData);
            synchronizationService.synchronize(roadworkData);
        });
        return roadworks;
    }

    public void save(RoadworkData roadworkData) {
        logger.info("save {}", roadworkData.getSource());
        ObjectMapper objectMapper = new ObjectMapper();

        Path savePath = getPath(roadworkData.getSource());
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
        return getOpendataService().getCenter();
    }

    /**
     * Returns roadwork data.
     * If
     *
     * @return an optional that should contains Roadwork data
     * @throws IOException if an exception happens
     */
    @NotNull
    private Optional<RoadworkData> getRoadworks() throws IOException {
        Path currentPath = getPath(config.getOpendataService());
        logger.info("getData {}", currentPath);
        Optional<RoadworkData> cachedDataOptional = loadCache(currentPath);
        if (cachedDataOptional.isEmpty()) {
            logger.info("There is no cached data");
            Optional<RoadworkData> newData = getOpendataService().getData();
            newData.ifPresent(this::save);
            return newData;
        }

        RoadworkData cachedRoadworkData = cachedDataOptional.get();
        if (cachedRoadworkData.getCreated() + 86400000 < System.currentTimeMillis()) {
            logger.info("Cache is obsolete {}", currentPath);
            Files.delete(currentPath);
            Optional<RoadworkData> newDataOptional = getOpendataService().getData();
            if (newDataOptional.isPresent()) {
                RoadworkData newData = newDataOptional.get();
                Map<String, Roadwork> newRoadworks = newData.getRoadworks();
                logger.info("reloaded {} new roadworks", newRoadworks.size());
                for (Roadwork existingRoadwork : cachedRoadworkData) {
                    Roadwork newRoadwork = newRoadworks.get(existingRoadwork.getId());
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

    @NotNull
    private OpendataService getOpendataService() {
        return applicationContext.getBean(config.getOpendataService(), OpendataService.class);
    }

    @NotNull
    private Path getPath(String opendataService) {
        return Path.of(config.getDataPath(), opendataService + '.' + VERSION + ".json");
    }

    private Optional<RoadworkData> loadCache(Path cachePath) {
        if (Files.exists(cachePath)) {
            ObjectMapper objectMapper = new ObjectMapper();
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
        Map<String, RoadworkMigrationService> beansOfType = applicationContext.getBeansOfType(RoadworkMigrationService.class);
        Collection<RoadworkMigrationService> migrationServices = beansOfType.values();
        for (RoadworkMigrationService migrationService : migrationServices) {
            logger.info("Will try migration service {}", migrationService);
            Optional<RoadworkData> roadworkData = migrationService.migrateData();
            if (roadworkData.isPresent()) {
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
                .forEach(roadwork -> roadwork
                        .getSyncData()
                        .setStatus(Status.Finished));
    }
}
