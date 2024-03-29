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
package com.kpouer.roadwork.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.kpouer.themis.annotation.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class HttpService {
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getUrl(String url) throws URISyntaxException, IOException, InterruptedException {
        logger.info("getUrl {}", url);
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(url))
                .version(HttpClient.Version.HTTP_2)
                .build();
        HttpResponse<String> response = httpClient
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public <E> E getJsonObject(String url, Class<E> responseType) throws URISyntaxException, IOException, InterruptedException, com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.databind.JsonMappingException {
        var json = getUrl(url);

        return objectMapper.readValue(json, responseType);
    }
}
