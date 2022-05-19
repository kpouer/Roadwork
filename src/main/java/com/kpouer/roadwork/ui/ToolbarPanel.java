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
package com.kpouer.roadwork.ui;

import com.kpouer.roadwork.action.SynchronizeAction;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.event.OpendataServiceUpdated;
import com.kpouer.roadwork.event.SynchronizationSettingsUpdated;
import com.kpouer.roadwork.event.UserSettingsUpdated;
import com.kpouer.roadwork.opendata.OpendataService;
import com.kpouer.roadwork.service.LocalizationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * @author Matthieu Casanova
 */
@Component
public class ToolbarPanel extends JPanel implements ApplicationListener<SynchronizationSettingsUpdated> {

    private final JButton synchronizeButton;
    private final Config config;

    public ToolbarPanel(ApplicationContext applicationContext,
                        Config config,
                        ApplicationEventPublisher applicationEventPublisher,
                        LocalizationService localizationService) {
        super(new BorderLayout());
        this.config = config;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        String[] opendataServices = applicationContext.getBeanNamesForType(OpendataService.class);
        JComboBox<String> opendataServiceComboBox = new JComboBox<>(opendataServices);
        opendataServiceComboBox.setSelectedItem(config.getOpendataService());
        opendataServiceComboBox.addActionListener(e -> {
            String selectedItem = (String) opendataServiceComboBox.getSelectedItem();
            assert selectedItem != null;
            OpendataServiceUpdated event = new OpendataServiceUpdated(this, config.getOpendataService(), selectedItem);
            config.setOpendataService(selectedItem);
            applicationEventPublisher.publishEvent(event);
        });
        panel.add(opendataServiceComboBox);
        synchronizeButton = new JButton(applicationContext.getBean(SynchronizeAction.class));
        synchronizeButton.setEnabled(config.getUserSettings().isSynchronizationEnabled());
        panel.add(synchronizeButton);
        JCheckBox hideExpired = new JCheckBox(localizationService.getMessage("toolbarPanel.hideExpired"));
        hideExpired.addActionListener(e -> {
            config.getUserSettings().setHideExpired(hideExpired.isSelected());
            applicationEventPublisher.publishEvent(new UserSettingsUpdated(this));
        });
        panel.add(hideExpired);
        add(panel);
    }

    @Override
    public void onApplicationEvent(SynchronizationSettingsUpdated event) {
        synchronizeButton.setEnabled(config.getUserSettings().isSynchronizationEnabled());
    }
}
