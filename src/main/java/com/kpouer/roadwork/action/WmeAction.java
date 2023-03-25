/*
 * Copyright 2022-2023 Matthieu Casanova
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

import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.service.exception.OpenDataException;
import com.kpouer.roadwork.service.OpendataServiceManager;
import com.kpouer.roadwork.service.SoftwareModel;
import jakarta.annotation.Nullable;
import com.kpouer.themis.annotation.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Matthieu Casanova
 */
@Component
public class WmeAction extends AbstractAction {
    public static final String DEFAULT_WME_URL = "https://waze.com/fr/editor?env=row&lat=${lat}&&lon=${lon}&zoomLevel=19";

    private final SoftwareModel softwareModel;
    private final OpendataServiceManager opendataServiceManager;

    public WmeAction(SoftwareModel softwareModel,
                     OpendataServiceManager opendataServiceManager) {
        super("WME");
        this.softwareModel = softwareModel;
        this.opendataServiceManager = opendataServiceManager;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent e) {
        var selectedRoadwork = softwareModel.getRoadwork();
        if (selectedRoadwork != null) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(getWmeUrl(selectedRoadwork));
                } catch (IOException | URISyntaxException | OpenDataException ex) {
                    JOptionPane.showMessageDialog(softwareModel.getMainFrame(), ex.getMessage());
                }
            }
        }
    }

    private URI getWmeUrl(Roadwork roadwork) throws URISyntaxException, OpenDataException {
        var url = opendataServiceManager.getOpendataService().getMetadata().getEditorPattern();
        if (url == null) {
            url = DEFAULT_WME_URL;
        }
        return new URI(url
                .replace("${lat}", String.valueOf(roadwork.getLatitude()))
                .replace("${lon}", String.valueOf(roadwork.getLongitude())));
    }
}
