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

import com.kpouer.mapview.LatLng;
import lombok.*;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
    private String country;
    private LatLng center;
    private String sourceUrl;
    private String url;
    private String name;
    private String producer;
    private String licenceName;
    private String licenceUrl;
    private Locale locale;
    private Map<String, String> urlParams;
    private String tileServer = "WazeINTL";
    private String editorPattern = "https://waze.com/fr/editor?env=row&lat=${lat}&lon=${lon}&zoomLevel=19";

    @Override
    public String toString() {
        return name;
    }
}
