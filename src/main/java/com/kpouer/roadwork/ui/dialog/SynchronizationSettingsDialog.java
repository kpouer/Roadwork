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
package com.kpouer.roadwork.ui.dialog;

import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.configuration.UserSettings;
import com.kpouer.roadwork.event.SynchronizationSettingsUpdated;
import com.kpouer.roadwork.service.SoftwareModel;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import javax.swing.*;
import java.awt.*;

/**
 * @author Matthieu Casanova
 */
public class SynchronizationSettingsDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationSettingsDialog.class);

    public SynchronizationSettingsDialog(SoftwareModel softwareModel,
                                         Config config,
                                         ApplicationEventPublisher applicationEventPublisher) {
        super(softwareModel.getMainFrame(), "Synchronization settings", true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout());
        UserSettings userSettings = config.getUserSettings();

        JCheckBox enableSynchronization = new JCheckBox("enable synchronization");
        JTextField urlField = new JTextField(40);
        JTextField teamField = new JTextField(40);
        JTextField loginField = new JTextField(40);
        JTextField passwordField = new JTextField(40);

        boolean synchronizationEnabled = userSettings.isSynchronizationEnabled();
        if (synchronizationEnabled) {
            enableSynchronization.setSelected(true);
        } else {
            loginField.setEnabled(false);
            passwordField.setEnabled(false);
            urlField.setEnabled(false);
            teamField.setEnabled(false);
        }
        urlField.setText(userSettings.getSynchronizationUrl());
        teamField.setText(userSettings.getSynchronizationTeam());
        loginField.setText(userSettings.getSynchronizationLogin());
        passwordField.setText(userSettings.getSynchronizationPassword());

        enableSynchronization.addActionListener(e -> {
            urlField.setEnabled(enableSynchronization.isSelected());
            teamField.setEnabled(enableSynchronization.isSelected());
            loginField.setEnabled(enableSynchronization.isSelected());
            passwordField.setEnabled(enableSynchronization.isSelected());
        });

        contentPane.add(enableSynchronization, "wrap, span 2");
        contentPane.add(new JLabel("Synchronization server :"));
        contentPane.add(urlField, "wrap");
        contentPane.add(new JLabel("Team :"));
        contentPane.add(teamField, "wrap");
        contentPane.add(new JLabel("Login :"));
        contentPane.add(loginField, "wrap");
        contentPane.add(new JLabel("Password :"));
        contentPane.add(passwordField, "wrap");

        JButton ok = new JButton("ok");
        contentPane.add(ok);

        ok.addActionListener(e -> {
            String synchronizationUrl = urlField.getText().trim();
            String synchronizationTeam = teamField.getText().trim();
            userSettings.setSynchronizationEnabled(enableSynchronization.isSelected());
            userSettings.setSynchronizationUrl(synchronizationUrl);
            userSettings.setSynchronizationTeam(synchronizationTeam);
            userSettings.setSynchronizationLogin(loginField.getText().trim());
            userSettings.setSynchronizationPassword(passwordField.getText().trim());
            applicationEventPublisher.publishEvent(new SynchronizationSettingsUpdated(this));
            dispose();
        });
        JButton cancel = new JButton("cancel");
        cancel.addActionListener(e -> dispose());
        contentPane.add(cancel);

        pack();
    }
}
