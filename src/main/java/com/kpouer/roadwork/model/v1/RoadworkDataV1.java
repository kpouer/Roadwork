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
package com.kpouer.roadwork.model.v1;

import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import jakarta.annotation.Nonnull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class RoadworkDataV1 implements Iterable<RoadworkV1> {
    /**
     * The name of the opendata source
     */
    private String source;
    private Map<String, RoadworkV1> roadworks;
    /**
     * The date of the last update
     */
    private long created;

    public RoadworkDataV1() {
    }

    public RoadworkDataV1(String source, Collection<RoadworkV1> roadworks) {
        this.source = source;
        this.roadworks = new HashMap<>(roadworks.size());
        roadworks.forEach(roadwork -> this.roadworks.put(roadwork.getId(), roadwork));
        created = System.currentTimeMillis();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Nonnull
    @Override
    public Iterator<RoadworkV1> iterator() {
        return roadworks.values().iterator();
    }

    public Map<String, RoadworkV1> getRoadworks() {
        return roadworks;
    }

    public void setRoadworks(Map<String, RoadworkV1> roadworks) {
        this.roadworks = roadworks;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public RoadworkV1 getRoadwork(String id) {
        return roadworks.get(id);
    }

    public RoadworkData toRoadWork() {
        RoadworkData roadworkData = new RoadworkData();
        Map<String, Roadwork> roadworks = new HashMap<>();
        roadworkData.setRoadworks(roadworks);
        roadworkData.setSource(source);
        roadworkData.setCreated(created);
        for (Map.Entry<String, RoadworkV1> entry : this.roadworks.entrySet()) {
            String key = entry.getKey();
            RoadworkV1 roadworkV1 = entry.getValue();
            Roadwork roadwork = roadworkV1.toRoadwork();
            roadworks.put(key, roadwork);
        }
        return roadworkData;
    }
}
