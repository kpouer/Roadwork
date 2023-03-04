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

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

@Service
@Slf4j
public class HttpService {
    private final HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();

    public String getUrl(String url) throws RestClientException {
        logger.info("getUrl {}", url);
        try (var httpClient = HttpClients.createDefault()) {
            var getMethod = new HttpGet(url);

            return httpClient.execute(getMethod, responseHandler);
        } catch (IOException e) {
            throw new RestClientException("Error retrieving " + url, e);
        }
    }
}
