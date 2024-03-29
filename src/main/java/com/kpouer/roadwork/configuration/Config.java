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
package com.kpouer.roadwork.configuration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpouer.hermes.Hermes;
import com.kpouer.mapview.MapView;
import com.kpouer.mapview.tile.DefaultTileServer;
import com.kpouer.mapview.tile.cache.ImageCacheImpl;
import com.kpouer.roadwork.log.LoopListAppender;
import com.kpouer.roadwork.service.SoftwareModel;
import com.kpouer.themis.annotation.Component;
import com.kpouer.themis.annotation.Qualifier;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
@Component(lazy = false)
@Slf4j
@Getter
@Setter
public class Config {
    public static final String DEFAULT_OPENDATA_SERVICE = "France-Paris.json";

    private List<String> logs;
    private int tilesSize = 256;
    private int minZoom = 1;
    private int maxZoom = 18;
    private int threadCount = 2;
    private String datePattern = "yyyy-MM-dd";
    private String dataPath;
    private UserSettings userSettings;
    private final SoftwareModel softwareModel;
    private int connectTimeout = 1000;
    private int connectionRequestTimeout = 1000;
    private int readTimeout = 300000;
    private Hermes hermes;

    public Config() {
        hermes = new Hermes();
        softwareModel = new SoftwareModel();
        configureLogger(hermes);
        logger.info("Config start");

        var userHome = System.getProperty("user.home");
        if (userHome == null) {
            dataPath = "data";
        } else {
            dataPath = userHome + "/.roadwork";
            migrateIfNecessary();
        }
        var userSettingsPath = getUserSettingsPath();
        if (Files.exists(userSettingsPath)) {
            var objectMapper = new ObjectMapper();
            try {
                userSettings = objectMapper.readValue(userSettingsPath.toFile(), UserSettings.class);
            } catch (IOException e) {
                logger.error("Error trying to read user settings");
                userSettings = new UserSettings();
            }
        } else {
            userSettings = new UserSettings();
        }
    }

    private void configureLogger(Hermes hermes) {
        var layout = new PatternLayout();
        layout.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        layout.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg");
        layout.start();
        var loopListAppender = new LoopListAppender(layout, hermes);
        logs = loopListAppender.getList();
        var rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        loopListAppender.start();
        ((ch.qos.logback.classic.Logger) rootLogger).addAppender(loopListAppender);
    }

    private void migrateIfNecessary() {
        if (!Files.exists(Path.of(dataPath))) {
            var oldData = Path.of("data");
            if (Files.exists(oldData)) {
                logger.info("migrate data");
                try {
                    Files.move(oldData, Path.of(dataPath));
                } catch (IOException e) {
                    logger.error("Unable to migrate data", e);
                }
            }
        }
    }

    @Nonnull
    private Path getUserSettingsPath() {
        return Path.of(dataPath, "userSettings.json");
    }

    @PreDestroy
    public void stop() {
        logger.info("stop");
        try {
            var bounds = softwareModel.getMainFrame().getBounds();
            userSettings.setFrameX(bounds.x);
            userSettings.setFrameY(bounds.y);
            userSettings.setFrameWidth(bounds.width);
            userSettings.setFrameHeight(bounds.height);
            var objectMapper = new ObjectMapper();
            var userSettingsPath = getUserSettingsPath();
            if (!Files.exists(userSettingsPath.getParent())) {
                Files.createDirectory(userSettingsPath.getParent());
            }
            objectMapper.writeValue(userSettingsPath.toFile(), userSettings);
        } catch (IOException e) {
            logger.error("Error while saving settings", e);
        }
    }

    public String getOpendataService() {
        String opendataService = userSettings.getOpendataService();
        if (opendataService != null && !opendataService.isBlank()) {
            return opendataService;
        }
        return DEFAULT_OPENDATA_SERVICE;
    }

    public void setOpendataService(String opendataService) {
        userSettings.setOpendataService(opendataService);
    }

    @Component("mapview")
    public MapView getMapView(@Qualifier("WazeINTLTileServer") DefaultTileServer tileServer) {
        return new MapView(tileServer);
    }

    @Component("OsmTileServer")
    public DefaultTileServer getOsmTileServer() {
        try {
            return new DefaultTileServer(tilesSize,
                    minZoom,
                    maxZoom,
                    threadCount,
                    new ImageCacheImpl("OSM", Path.of(dataPath, "cache").toString(), 1000),
                    "https://a.tile.openstreetmap.org/${z}/${x}/${y}.png",
                    "https://b.tile.openstreetmap.org/${z}/${x}/${y}.png",
                    "https://c.tile.openstreetmap.org/${z}/${x}/${y}.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Component("WazeINTLTileServer")
    public DefaultTileServer getWazeINTLTileServer() {
        try {
            return new DefaultTileServer(tilesSize,
                    minZoom,
                    maxZoom,
                    threadCount,
                    new ImageCacheImpl("Waze", Path.of(dataPath, "cache").toString(), 1000),
                    "https://worldtiles1.waze.com/tiles/${z}/${x}/${y}.png",
                    "https://worldtiles2.waze.com/tiles/${z}/${x}/${y}.png",
                    "https://worldtiles3.waze.com/tiles/${z}/${x}/${y}.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Component("WazeNATileServer")
    public DefaultTileServer getWazeNATileServer() {
        try {
            return new DefaultTileServer(tilesSize,
                    minZoom,
                    maxZoom,
                    threadCount,
                    new ImageCacheImpl("WazeNA", Path.of(dataPath, "cache").toString(), 1000),
                    "https://livemap-tiles1.waze.com/tiles/${z}/${x}/${y}.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Component("WazeILTileServer")
    public DefaultTileServer getWazeILTileServer() {
        try {
            return new DefaultTileServer(tilesSize,
                    minZoom,
                    maxZoom,
                    threadCount,
                    new ImageCacheImpl("WazeIL", Path.of(dataPath, "cache").toString(), 1000),
                    "https://il-livemap-tiles1.waze.com/tiles/${z}/${x}/${y}.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Component
    public List<String> logs() {
        return logs;
    }

    @Component
    public Hermes getHermes() {
        return hermes;
    }

    @Component
    public SoftwareModel getSoftwareModel() {
        return softwareModel;
    }
}
