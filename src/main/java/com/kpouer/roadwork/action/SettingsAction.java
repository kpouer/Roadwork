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

import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.roadwork.service.SoftwareModel;
import com.kpouer.roadwork.ui.dialog.SettingsDialog;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Matthieu Casanova
 */
@Component
public class SettingsAction extends AbstractAction {
    private final SoftwareModel softwareModel;
    private final Config config;
    private final ApplicationContext applicationContext;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SettingsAction(SoftwareModel softwareModel,
                          Config config,
                          ApplicationContext applicationContext,
                          ApplicationEventPublisher applicationEventPublisher,
                          LocalizationService localizationService) {
        super(localizationService.getMessage("action.settings"));
        this.softwareModel = softwareModel;
        this.config = config;
        this.applicationContext = applicationContext;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent e) {
        SettingsDialog settingsDialog = new SettingsDialog(softwareModel, applicationContext, applicationEventPublisher, config);
        settingsDialog.setLocationRelativeTo(softwareModel.getMainFrame());
        settingsDialog.setVisible(true);
    }
}
