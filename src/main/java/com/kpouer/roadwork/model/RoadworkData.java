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
package com.kpouer.roadwork.model;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class RoadworkData implements Iterable<Roadwork> {
    /**
     * The name of the opendata source
     */
    private String source;
    private Map<String, Roadwork> roadworks;
    /**
     * The date of the last update
     */
    private long created;

    public RoadworkData() {
    }

    public RoadworkData(String source, Collection<Roadwork> roadworks) {
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

    @NotNull
    @Override
    public Iterator<Roadwork> iterator() {
        return roadworks.values().iterator();
    }

    public Map<String, Roadwork> getRoadworks() {
        return roadworks;
    }

    public void setRoadworks(Map<String, Roadwork> roadworks) {
        this.roadworks = roadworks;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public Roadwork getRoadwork(String id) {
        return roadworks.get(id);
    }
}
