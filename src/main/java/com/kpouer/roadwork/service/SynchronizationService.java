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

import com.kpouer.roadwork.action.SynchronizationSettingsAction;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.configuration.UserSettings;
import com.kpouer.roadwork.event.SynchronizationSettingsUpdated;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.model.sync.SyncData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
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
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
@Service
public class SynchronizationService {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationService.class);

    private final UserSettings userSettings;
    private final LocalizationService localizationService;
    private final SoftwareModel softwareModel;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationContext applicationContext;

    public SynchronizationService(Config config,
                                  LocalizationService localizationService,
                                  SoftwareModel softwareModel,
                                  ApplicationEventPublisher applicationEventPublisher,
                                  ApplicationContext applicationContext) {
        userSettings = config.getUserSettings();
        this.localizationService = localizationService;
        this.softwareModel = softwareModel;
        this.applicationEventPublisher = applicationEventPublisher;
        this.applicationContext = applicationContext;
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
                        localizationService.getMessage("dialog.synchronization.unauthorized.message"),
                        localizationService.getMessage("dialog.synchronization.unauthorized.title"),
                        JOptionPane.WARNING_MESSAGE);
                applicationContext.getBean(SynchronizationSettingsAction.class).actionPerformed(null);
            } catch (RestClientException e) {
                if (e.getCause() instanceof ConnectException) {
                    logger.error("Unable to connect to synchronization server " + url);
                    userSettings.setSynchronizationEnabled(false);
                    applicationEventPublisher.publishEvent(new SynchronizationSettingsUpdated(this));
                } else {
                    logger.error("Error posting to synchronization server", e);
                }
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
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + encodedAuth;
        headers.set("Authorization", authHeader);
        return headers;
    }
}
