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

import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.roadwork.service.OpendataServiceManager;
import com.kpouer.roadwork.service.SoftwareModel;
import jakarta.annotation.Nullable;
import com.kpouer.themis.annotation.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Matthieu Casanova
 */
@Component
public class ExitAction extends AbstractAction {

    private final OpendataServiceManager opendataServiceManager;
    private final SoftwareModel softwareModel;

    public ExitAction(OpendataServiceManager opendataServiceManager,
                      SoftwareModel softwareModel,
                      LocalizationService localizationService) {
        super(localizationService.getMessage("action.exit"));
        this.opendataServiceManager = opendataServiceManager;
        this.softwareModel = softwareModel;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent e) {
        if (softwareModel.getRoadworkData() != null) {
            opendataServiceManager.save(softwareModel.getRoadworkData());
        }

        System.exit(0);
    }
}
