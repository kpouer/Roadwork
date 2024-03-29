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

import com.kpouer.hermes.Hermes;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.configuration.UserSettings;
import com.kpouer.roadwork.model.RoadworkData;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import com.kpouer.themis.Themis;
import com.kpouer.themis.annotation.Component;

/**
 * @author Matthieu Casanova
 */
@Component
@Slf4j
public class SynchronizationService {
    private final UserSettings userSettings;
    private final LocalizationService localizationService;
    private final SoftwareModel softwareModel;
    private final Hermes hermes;
    private final Themis themis;
    private final HttpService httpService;

    public SynchronizationService(Config config,
                                  LocalizationService localizationService,
                                  SoftwareModel softwareModel,
                                  Hermes hermes,
                                  Themis themis,
                                  HttpService httpService) {
        userSettings = config.getUserSettings();
        this.localizationService = localizationService;
        this.softwareModel = softwareModel;
        this.hermes = hermes;
        this.themis = themis;
        this.httpService = httpService;
    }

    /**
     * Synchronize the data with the server
     *
     * @param roadworkData the data to synchronize. Status might be updated
     */
    public void synchronize(RoadworkData roadworkData) {
        if (userSettings.isSynchronizationEnabled()) {
//            logger.info("synchronize");
//            var url = getUrl(roadworkData.getSource());
//            logger.info("Will synchronize with url {}", url);
//            try {
//                var body = new HashMap<String, SyncData>();
//                for (var roadwork : roadworkData) {
//                    body.put(roadwork.getId(), roadwork.getSyncData());
//                }
//                var synchronizedData = httpService.postJsonObject(url, body, createHeaders(), responseType);
//
//                for (var entry : synchronizedData.entrySet()) {
//                    var serverSyncData = entry.getValue();
//                    var roadwork = roadworkData.getRoadwork(entry.getKey());
//                    roadwork.getSyncData().copy(serverSyncData);
//                    roadwork.updateMarker();
//                }
//            } catch (HttpClientErrorException.Unauthorized e) {
//                logger.warn("Error posting to synchronization server, invalid credencials");
//                JOptionPane.showMessageDialog(softwareModel.getMainFrame(),
//                        localizationService.getMessage("dialog.synchronization.unauthorized.message"),
//                        localizationService.getMessage("dialog.synchronization.unauthorized.title"),
//                        JOptionPane.WARNING_MESSAGE);
//                themis.getComponentOfType(SynchronizationSettingsAction.class).actionPerformed(null);
//            } catch (RestClientException e) {
//                if (e.getCause() instanceof ConnectException) {
//                    logger.error("Unable to connect to synchronization server {}", url);
//                    userSettings.setSynchronizationEnabled(false);
//                    hermes.publish(new SynchronizationSettingsUpdated(this));
//                } else {
//                    logger.error("Error posting to synchronization server", e);
//                }
//            }
        }
    }

    @Nonnull
    private String getUrl(String source) {
        var synchronizationTeam = userSettings.getSynchronizationTeam();
        var url = userSettings.getSynchronizationUrl();
        if (!url.endsWith("/")) {
            url += '/';
        }
        url += "setData/" + synchronizationTeam + '/' + source;
        return url;
    }

//    private HttpHeaders createHeaders() {
//        var headers = new HttpHeaders();
//        var auth = userSettings.getSynchronizationLogin() + ':' + userSettings.getSynchronizationPassword();
//        var encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.US_ASCII));
//        var authHeader = "Basic " + encodedAuth;
//        headers.set("Authorization", authHeader);
//        return headers;
//    }
//
//    private static class MapParameterizedTypeReference extends ParameterizedTypeReference<Map<String, SyncData>> {
//    }
}
