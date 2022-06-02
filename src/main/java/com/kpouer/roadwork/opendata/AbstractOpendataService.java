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

import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.opendata.json.model.Metadata;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author Matthieu Casanova
 */
public abstract class AbstractOpendataService<R, E extends OpendataResponse<R>> implements OpendataService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractOpendataService.class);

    private final Class<E> responseType;
    private final RestTemplate restTemplate;
    private final Metadata metadata;

    protected AbstractOpendataService(Metadata metadata, Class<E> responseType, RestTemplate restTemplate) {
        this.metadata = metadata;
        this.responseType = responseType;
        this.restTemplate = restTemplate;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public Optional<RoadworkData> getData() throws RestClientException {
        logger.info("getData from {} -> {}", metadata.getUrl(), responseType);

        // because some opendata service do not return Json content type
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);

        restTemplate.setMessageConverters(messageConverters);
        E response = restTemplate.getForObject(metadata.getUrl(), responseType);
        if (response == null) {
            logger.debug("No data");
            return Optional.empty();
        }
        logger.info("Data retrieved");
        return Optional.of(getRoadworkData(response));
    }

    protected RoadworkData getRoadworkData(E opendataResponse) {
        logger.info("getRoadworkData {}", opendataResponse);
        long deadline = System.currentTimeMillis() + 7 * 86400000;
        List<Roadwork> roadworks = opendataResponse.parallelStream()
                .map(this::getRoadwork)
                .filter(Objects::nonNull)
                .filter(roadwork -> roadwork.getEnd() == 0 || roadwork.getEnd() > deadline
                )
                .toList();
        return new RoadworkData(getClass().getSimpleName(), roadworks);
    }

    @Nullable
    protected abstract Roadwork getRoadwork(R opendataRoadwork);
}
