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

import com.kpouer.roadwork.action.SynchronizationSettingsAction;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.configuration.UserSettings;
import com.kpouer.roadwork.event.SynchronizationSettingsUpdated;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.model.sync.SyncData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
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
@Slf4j
public class SynchronizationService {
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
            var url = getUrl(roadworkData.getSource());
            logger.info("Will synchronize with url {}", url);
            var restTemplate = new RestTemplate();
            try {
                var body = new HashMap<String, SyncData>();
                for (var roadwork : roadworkData) {
                    body.put(roadwork.getId(), roadwork.getSyncData());
                }
                var responseType = new MapParameterizedTypeReference();
                var entity = new HttpEntity<Map<String, SyncData>>(body, createHeaders());
                var roadworkDataResponseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
                var synchronizedData = roadworkDataResponseEntity.getBody();
                if (synchronizedData == null) {
                    throw new RestClientException("No body in response");
                }
                for (var entry : synchronizedData.entrySet()) {
                    var serverSyncData = entry.getValue();
                    var roadwork = roadworkData.getRoadwork(entry.getKey());
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
                    logger.error("Unable to connect to synchronization server {}", url);
                    userSettings.setSynchronizationEnabled(false);
                    applicationEventPublisher.publishEvent(new SynchronizationSettingsUpdated(this));
                } else {
                    logger.error("Error posting to synchronization server", e);
                }
            }
        }
    }

    @NonNull
    private String getUrl(String source) {
        var synchronizationTeam = userSettings.getSynchronizationTeam();
        var url = userSettings.getSynchronizationUrl();
        if (!url.endsWith("/")) {
            url += '/';
        }
        url += "setData/" + synchronizationTeam + '/' + source;
        return url;
    }

    private HttpHeaders createHeaders() {
        var headers = new HttpHeaders();
        var auth = userSettings.getSynchronizationLogin() + ':' + userSettings.getSynchronizationPassword();
        var encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.US_ASCII));
        var authHeader = "Basic " + encodedAuth;
        headers.set("Authorization", authHeader);
        return headers;
    }

    private static class MapParameterizedTypeReference extends ParameterizedTypeReference<Map<String, SyncData>> {
    }
}
