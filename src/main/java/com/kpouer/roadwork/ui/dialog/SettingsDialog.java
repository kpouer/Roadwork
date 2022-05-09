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
import com.kpouer.roadwork.event.OpendataServiceUpdated;
import com.kpouer.roadwork.opendata.OpendataService;
import com.kpouer.roadwork.service.SoftwareModel;
import net.miginfocom.swing.MigLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public class SettingsDialog extends JDialog {
    private final ApplicationContext applicationContext;

    public SettingsDialog(SoftwareModel softwareModel,
                          ApplicationContext applicationContext,
                          ApplicationEventPublisher applicationEventPublisher,
                          Config config) {
        super(softwareModel.getMainFrame(), "Settings", true);
        this.applicationContext = applicationContext;
        getContentPane().setLayout(new MigLayout());
        getContentPane().add(new JLabel("Source :"));
        JComboBox<String> opendataServiceComboBox = getOpendataServiceComboBox();
        opendataServiceComboBox.setSelectedItem(config.getOpendataService());
        getContentPane().add(opendataServiceComboBox, "wrap");
        JButton ok = new JButton("ok");
        getContentPane().add(ok);
        ok.addActionListener(e -> {
            String selectedItem = (String) opendataServiceComboBox.getSelectedItem();
            assert selectedItem != null;
            OpendataServiceUpdated event = new OpendataServiceUpdated(this, config.getOpendataService(), selectedItem);
            config.setOpendataService(selectedItem);
            dispose();
            applicationEventPublisher.publishEvent(event);
        });
        JButton cancel = new JButton("cancel");
        cancel.addActionListener(e -> dispose());
        getContentPane().add(cancel);

        pack();
    }

    private JComboBox<String> getOpendataServiceComboBox() {
        String[] opendataServices = applicationContext.getBeanNamesForType(OpendataService.class);
        return new JComboBox<>(opendataServices);
    }
}
