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

import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.service.SoftwareModel;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

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
    private final SoftwareModel softwareModel;

    public WmeAction(SoftwareModel softwareModel) {
        super("WME");
        this.softwareModel = softwareModel;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent e) {
        Roadwork selectedRoadwork = softwareModel.getSelectedRoadwork();
        if (selectedRoadwork != null) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI("https://waze.com/fr/editor?env=row&lat=" + selectedRoadwork.getLatitude() + "&lon=" + selectedRoadwork.getLongitude() + "&zoomLevel=19"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
