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
package com.kpouer.roadwork.service;

import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.configuration.UserSettings;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.model.sync.SyncData;
import com.kpouer.roadwork.ui.MainPanel;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.JComponent.getDefaultLocale;

/**
 * @author Matthieu Casanova
 */
@Service
public class SynchronizationService {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationService.class);

    private final UserSettings userSettings;
    private final MessageSource resourceBundle;
    private final SoftwareModel softwareModel;

    public SynchronizationService(Config config, MessageSource resourceBundle, SoftwareModel softwareModel) {
        userSettings = config.getUserSettings();
        this.resourceBundle = resourceBundle;
        this.softwareModel = softwareModel;
    }

    /**
     * Synchronize the data with the server
     *
     * @param roadworkData the data to synchronize. Status might be updated
     */
    public void synchronize(RoadworkData roadworkData) {
        if (userSettings.isSynchronizationEnabled()) {
            logger.info("synchronize");
            String url = getUrl(roadworkData.getSource());
            logger.info("Will synchronize with url {}", url);
            RestOperations restTemplate = new RestTemplate();
            try {
                Map<String, SyncData> body = new HashMap<>();
                for (Roadwork roadwork : roadworkData) {
                    body.put(roadwork.getId(), roadwork.getSyncData());
                }
                ParameterizedTypeReference<Map<String, SyncData>> responseType = new ParameterizedTypeReference<>() {
                };
                HttpEntity<Map<String, SyncData>> entity = new HttpEntity<>(body, createHeaders());
                ResponseEntity<Map<String, SyncData>> roadworkDataResponseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
                Map<String, SyncData> synchronizedData = roadworkDataResponseEntity.getBody();
                for (Map.Entry<String, SyncData> entry : synchronizedData.entrySet()) {
                    SyncData serverSyncData = entry.getValue();
                    Roadwork roadwork = roadworkData.getRoadwork(entry.getKey());
                    roadwork.getSyncData().copy(serverSyncData);
                    roadwork.updateMarker();
                }
            } catch (HttpClientErrorException.Unauthorized e) {
                logger.warn("Error posting to synchronization server, invalid credencials");
                JOptionPane.showMessageDialog(softwareModel.getMainFrame(),
                        resourceBundle.getMessage("dialog.synchronization.unauthorized.message", null, getDefaultLocale()),
                        resourceBundle.getMessage("dialog.synchronization.unauthorized.title", null, getDefaultLocale()),
                        JOptionPane.WARNING_MESSAGE);
            } catch (RestClientException e) {
                logger.error("Error posting to synchronization server", e);
            }
        }
    }

    @NotNull
    private String getUrl(String source) {
        String synchronizationTeam = userSettings.getSynchronizationTeam();
        String url = userSettings.getSynchronizationUrl();
        if (!url.endsWith("/")) {
            url += '/';
        }
        url += "setData/" + synchronizationTeam + '/' + source;
        return url;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = userSettings.getSynchronizationLogin() + ':' + userSettings.getSynchronizationPassword();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        return headers;
    }
}
