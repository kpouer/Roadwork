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
package com.kpouer.roadwork.opendata.france.avignon.model;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.kpouer.roadwork.opendata.OpendataResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * @author Matthieu Casanova
 */
public class AvignonOpendataResponse implements OpendataResponse<Feature> {
    private Feature[] features;

    public Feature[] getFeatures() {
        return features;
    }

    public void setFeatures(Feature[] features) {
        this.features = features;
    }

    @NotNull
    @Override
    public Iterator<Feature> iterator() {
        return new ArrayIterator<>(features);
    }
}