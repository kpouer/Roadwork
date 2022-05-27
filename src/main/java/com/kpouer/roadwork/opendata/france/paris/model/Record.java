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
package com.kpouer.roadwork.opendata.france.paris.model;

import com.kpouer.roadwork.opendata.model.Geometry;

/**
 * @author Matthieu Casanova
 */
public class Record {
    private String recordid;
    private Fields fields;
    private Geometry geometry;
    private String record_timestamp;

    public String getRecordid() {
        return recordid;
    }

    public Fields getFields() {
        return fields;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getRecord_timestamp() {
        return record_timestamp;
    }
}
