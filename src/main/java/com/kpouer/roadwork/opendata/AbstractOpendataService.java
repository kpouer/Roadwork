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
package com.kpouer.roadwork.opendata;

import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Matthieu Casanova
 */
public abstract class AbstractOpendataService<R, E extends OpendataResponse<R>> implements OpendataService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractOpendataService.class);

    private final LatLng location;
    private final String url;
    private final Class<E> responseType;

    protected AbstractOpendataService(LatLng location, String url, Class<E> responseType) {
        this.location = location;
        this.url = url;
        this.responseType = responseType;
    }

    @Override
    public LatLng getCenter() {
        return location;
    }

    @Override
    public Optional<RoadworkData> getData() {
        logger.info("getData from {} -> {}", url, responseType);
        RestTemplate restTemplate = new RestTemplate();
        E response = restTemplate.getForObject(url, responseType);
        if (response == null) {
            return Optional.empty();
        }
        return Optional.of(getRoadworkData(response));
    }

    protected RoadworkData getRoadworkData(E opendataResponse) {
        logger.info("getRoadworkData {}", opendataResponse);
        long deadline = System.currentTimeMillis() + 7 * 86400000;
        List<Roadwork> roadworks = opendataResponse.parallelStream()
                .map(this::getRoadwork)
                .filter(Objects::nonNull)
                .filter(roadwork -> roadwork.getEnd() < deadline
                )
                .toList();
        return new RoadworkData(getClass().getSimpleName(), roadworks);
    }

    @Nullable
    protected abstract Roadwork getRoadwork(R opendataRoadwork);
}
