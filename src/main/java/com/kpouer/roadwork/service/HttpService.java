/*
 * Copyright 2022-2024 Matthieu Casanova
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpouer.roadwork.model.sync.SyncData;
import lombok.extern.slf4j.Slf4j;
import com.kpouer.themis.annotation.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

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

    public <E> E postJsonObject(String url,
                                Map<String, SyncData> body,
                                Map<String, String> headers,
                                TypeReference<E> typeReference) throws IOException, InterruptedException, URISyntaxException {
        logger.info("postJsonObject");
        var httpRequestBuilder = HttpRequest.newBuilder(new URI(url))
            .version(HttpClient.Version.HTTP_2);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpRequestBuilder.header(entry.getKey(), entry.getValue());
        }
        httpRequestBuilder.header("Content-Type", "application/json");
        httpRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        var httpRequest = httpRequestBuilder.build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Error code " + response.statusCode());
        }
        return objectMapper.readValue(response.body(), typeReference);
    }
}
