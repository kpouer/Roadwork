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

import com.kpouer.roadwork.service.SoftwareModel;
import com.kpouer.roadwork.ui.logpanel.LogPanelDialog;
import com.kpouer.themis.ApplicationContext;
import org.springframework.lang.Nullable;
import com.kpouer.themis.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Matthieu Casanova
 */
@Component
public class LogsPanelAction extends AbstractAction {

    private final SoftwareModel softwareModel;
    private final ApplicationContext applicationContext;

    public LogsPanelAction(SoftwareModel softwareModel, ApplicationContext applicationContext) {
        super("LogsPanel");
        this.softwareModel = softwareModel;
        this.applicationContext = applicationContext;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent e) {
        var dialog = applicationContext.getBean(LogPanelDialog.class);
        dialog.setLocationRelativeTo(softwareModel.getMainFrame());
        dialog.setVisible(true);
    }
}
