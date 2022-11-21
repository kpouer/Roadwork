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

import com.kpouer.roadwork.action.LogsPanelAction;
import com.kpouer.roadwork.action.ReloadAction;
import com.kpouer.roadwork.action.SynchronizeAction;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.event.ExceptionEvent;
import com.kpouer.roadwork.event.OpendataServiceUpdated;
import com.kpouer.roadwork.event.SynchronizationSettingsUpdated;
import com.kpouer.roadwork.event.UserSettingsUpdated;
import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.roadwork.service.OpendataServiceManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * @author Matthieu Casanova
 */
@Component
public class ToolbarPanel extends JPanel implements ApplicationListener<ApplicationEvent> {

    private final JButton synchronizeButton;
    private final Config config;
    private final OpendataServiceManager opendataServiceManager;
    private final JButton logsPanelButton;
    private final Color defaultBackground;
    private final JComboBox<Object> opendataServiceComboBox;

    public ToolbarPanel(ApplicationContext applicationContext,
                        Config config,
                        ApplicationEventPublisher applicationEventPublisher,
                        LocalizationService localizationService,
                        OpendataServiceManager opendataServiceManager) {
        super(new BorderLayout());
        this.config = config;
        this.opendataServiceManager = opendataServiceManager;
        var panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        opendataServiceComboBox = new JComboBox<>();
        opendataServiceComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                var listCellRendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                var serviceName = String.valueOf(value);
                var key = "opendataService." + value;
                var localizedText = localizationService.getMessage(key);
                if (localizedText != key) {
                    setText(localizedText);
                } else {
                    if (serviceName.endsWith("Service")) {
                        setText(serviceName.substring(0, serviceName.length() - "Service".length()));
                    } else {
                        int minusIndex = serviceName.indexOf('-');
                        serviceName = serviceName.substring(minusIndex + 1, serviceName.length() - ".json".length());
                        setText(serviceName);
                    }
                }
                return listCellRendererComponent;
            }
        });
        opendataServiceComboBox.addActionListener(e -> {
            var selectedItem = (String) opendataServiceComboBox.getSelectedItem();
            assert selectedItem != null;
            var event = new OpendataServiceUpdated(this, config.getOpendataService(), selectedItem);
            config.setOpendataService(selectedItem);
            applicationEventPublisher.publishEvent(event);
        });
        panel.add(opendataServiceComboBox);
        synchronizeButton = new JButton(applicationContext.getBean(SynchronizeAction.class));
        synchronizeButton.setEnabled(config.getUserSettings().isSynchronizationEnabled());
        panel.add(new JButton(applicationContext.getBean(ReloadAction.class)));
        panel.add(synchronizeButton);
        var hideExpired = new JCheckBox(localizationService.getMessage("toolbarPanel.hideExpired"));
        hideExpired.addActionListener(e -> {
            config.getUserSettings().setHideExpired(hideExpired.isSelected());
            applicationEventPublisher.publishEvent(new UserSettingsUpdated(this));
        });
        panel.add(hideExpired);
        logsPanelButton = new JButton(applicationContext.getBean(LogsPanelAction.class));
        defaultBackground = logsPanelButton.getBackground();
        logsPanelButton.addActionListener(e -> logsPanelButton.setBackground(defaultBackground));
        panel.add(logsPanelButton);
        add(panel);
    }

    @PostConstruct
    public void init() {
        var serviceNames = opendataServiceManager.getServices().toArray(new String[0]);
        opendataServiceComboBox.setModel(new DefaultComboBoxModel<>(serviceNames));
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SynchronizationSettingsUpdated) {
            synchronizeButton.setEnabled(config.getUserSettings().isSynchronizationEnabled());
        } else if (event instanceof ExceptionEvent) {
            logsPanelButton.setBackground(Color.RED);
        }
    }
}
