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
package com.kpouer.roadwork.opendata.json.model;

import com.kpouer.wkt.shape.Polygon;
import lombok.Data;

@Data
public class ServiceDescriptor {
    private Metadata metadata;
    private String id;
    private String latitude;
    private String longitude;
    private String polygon;
    private String road;
    private String description;
    private String locationDetails;
    private String impactCirculationDetail;
    private DateParser from;
    private DateParser to;
    private String roadworkArray;
    private String url;
}
