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
package com.kpouer.roadwork.ui.about;

import com.kpouer.roadwork.service.SoftwareModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Matthieu Casanova
 */
public class AboutDialog extends JDialog {
    public AboutDialog(SoftwareModel softwareModel) {
        super(softwareModel.getMainFrame());
        getContentPane().setLayout(new MigLayout());
        JLabel roadworkTitle = new JLabel("Roadwork");
        roadworkTitle.setFont(roadworkTitle.getFont().deriveFont(20.0f));

        getContentPane().add(roadworkTitle, "wrap");
        getContentPane().add(new JLabel("Open source software"), "wrap");
        OpensourceTableModel ossModel = new OpensourceTableModel();
        JTable ossTable = new JTable(ossModel);
        ossTable.setTableHeader(null);
        ossTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = ossTable.columnAtPoint(e.getPoint());
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) && column == 2) {
                    int row = ossTable.rowAtPoint(e.getPoint());
                    try {
                        String url = ossModel.get(row).url();
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                super.mouseClicked(e);
            }
        });
        getContentPane().add(new JScrollPane(ossTable));
        pack();
    }
}
