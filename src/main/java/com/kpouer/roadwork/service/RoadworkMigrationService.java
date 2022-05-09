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

import com.kpouer.roadwork.model.RoadworkData;

import java.util.Optional;

/**
 * @author Matthieu Casanova
 */
interface RoadworkMigrationService {
    /**
     * Migrate the data to the latest version if they exist.
     *
     * @return migrated data
     */
    Optional<RoadworkData> migrateData();

    /**
     * Archive the cache file.
     */
    void archive();
}
