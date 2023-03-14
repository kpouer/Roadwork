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

import com.kpouer.mapview.tile.DefaultTileServer;
import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.roadwork.service.OpendataServiceManager;
import com.kpouer.roadwork.service.SoftwareModel;
import com.kpouer.roadwork.ui.opendatainfo.OpendataInformationDialog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import com.kpouer.themis.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Matthieu Casanova
 */
@Component
public class OpendataInformationAction extends AbstractAction {
    private final SoftwareModel softwareModel;
    private final LocalizationService localizationService;
    private final OpendataServiceManager opendataServiceManager;
    private final DefaultTileServer tileServer;

    public OpendataInformationAction(SoftwareModel softwareModel,
                                     LocalizationService localizationService,
                                     OpendataServiceManager opendataServiceManager,
                                     @Qualifier("WazeINTLTileServer") DefaultTileServer tileServer) {
        super(localizationService.getMessage("action.opendataInformationAction"));
        this.softwareModel = softwareModel;
        this.localizationService = localizationService;
        this.opendataServiceManager = opendataServiceManager;
        this.tileServer = tileServer;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent e) {
        OpendataInformationDialog dialog = new OpendataInformationDialog(softwareModel.getMainFrame(),
                opendataServiceManager,
                tileServer,
                localizationService);
        dialog.setLocationRelativeTo(softwareModel.getMainFrame());
        dialog.setVisible(true);
    }
}
