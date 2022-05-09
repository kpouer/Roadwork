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
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.model.v1.RoadworkDataV1;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Migration service serve to migrate old data to newer format.
 *
 * @author Matthieu Casanova
 */
@Service
public class V1RoadworkCacheService implements RoadworkMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(V1RoadworkCacheService.class);

    private final Config config;

    public V1RoadworkCacheService(Config config) {
        this.config = config;
    }

    @Override
    public Optional<RoadworkData> migrateData() {
        Path pathV1 = getPath(config.getOpendataService());
        if (Files.exists(pathV1)) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                RoadworkDataV1 roadworkDataV1 = objectMapper.readValue(pathV1.toFile(), RoadworkDataV1.class);
                RoadworkData roadworkData = roadworkDataV1.toRoadWork();
                return Optional.of(roadworkData);
            } catch (IOException e) {
                logger.error("Unable to read data from {} with {}", pathV1, this);
            }
        }
        return Optional.empty();
    }

    @Override
    public void archive() {
        String opendataService = config.getOpendataService();
        Path pathV1 = getPath(opendataService);
        if (Files.exists(pathV1)) {
            Path archivePath = Path.of(config.getDataPath(), opendataService + ".json.bak");
            if (!Files.exists(archivePath)) {
                logger.info("Archiving {} to {}", pathV1, archivePath);
                if (!pathV1.toFile().renameTo(archivePath.toFile())) {
                    logger.error("Error renaming archive");
                }
            } else {
                logger.error("Unable to archive to {}, file already exists", archivePath);
            }
        }
    }

    @NotNull
    private Path getPath(String opendataService) {
        return Path.of(config.getDataPath(), opendataService + ".json");
    }
}
