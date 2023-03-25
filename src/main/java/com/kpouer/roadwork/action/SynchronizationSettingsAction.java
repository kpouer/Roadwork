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

import com.kpouer.hermes.Hermes;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.roadwork.service.SoftwareModel;
import com.kpouer.roadwork.ui.dialog.SynchronizationSettingsDialog;
import org.springframework.lang.Nullable;
import com.kpouer.themis.annotation.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Matthieu Casanova
 */
@Component
public class SynchronizationSettingsAction extends AbstractAction {
    private final SoftwareModel softwareModel;
    private final Config config;
    private final Hermes hermes;

    public SynchronizationSettingsAction(SoftwareModel softwareModel,
                                         Config config,
                                         Hermes hermes,
                                         LocalizationService localizationService) {
        super(localizationService.getMessage("action.synchronizationSettings"));
        this.softwareModel = softwareModel;
        this.config = config;
        this.hermes = hermes;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent e) {
        EventQueue.invokeLater(() -> {
            SynchronizationSettingsDialog dialog = new SynchronizationSettingsDialog(softwareModel, config, hermes);
            dialog.setLocationRelativeTo(softwareModel.getMainFrame());
            dialog.setVisible(true);
        });
    }
}
