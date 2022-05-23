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
package com.kpouer.roadwork.action;

import com.kpouer.mapview.MapView;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.configuration.UserSettings;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.roadwork.service.OpendataServiceManager;
import com.kpouer.roadwork.service.SoftwareModel;
import com.kpouer.roadwork.service.SynchronizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Matthieu Casanova
 */
@Component
public class SynchronizeAction extends AbstractAction {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizeAction.class);

    private final SynchronizationService synchronizationService;
    private final Config config;
    private final MapView mapView;
    private final SoftwareModel softwareModel;
    private final OpendataServiceManager opendataServiceManager;

    public SynchronizeAction(SynchronizationService synchronizationService,
                             Config config,
                             MapView mapView,
                             SoftwareModel softwareModel,
                             LocalizationService localizationService,
                             OpendataServiceManager opendataServiceManager) {
        super(localizationService.getMessage("action.synchronize"));
        this.synchronizationService = synchronizationService;
        this.config = config;
        this.mapView = mapView;
        this.softwareModel = softwareModel;
        this.opendataServiceManager = opendataServiceManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("synchronize");
        UserSettings userSettings = config.getUserSettings();
        if (userSettings.isSynchronizationEnabled()) {
            RoadworkData roadworkData = softwareModel.getRoadworkData();
            synchronizationService.synchronize(roadworkData);
            opendataServiceManager.save(roadworkData);
            mapView.repaint();
        }
    }
}
