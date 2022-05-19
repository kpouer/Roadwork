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

import com.kpouer.mapview.MapView;
import com.kpouer.roadwork.action.WmeAction;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.sync.Status;
import com.kpouer.roadwork.service.LocalizationService;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
@Component
public class DetailPanel extends JPanel {
    private final JTextField id;
    private final JTextField location;
    private final JTextField start;
    private final JTextField stop;
    private final JTextField road;
    private final JTextField locationDetails;
    private final JTextArea circulationDetail;
    private final JTextArea description;
    private final Config config;
    private final MapView mapView;
    private final Map<Status, JRadioButton> statusRadioButtons;
    private Roadwork roadwork;

    public DetailPanel(Config config, MapView mapView, LocalizationService localizationService, WmeAction wmeAction) {
        super(new MigLayout());
        this.config = config;
        this.mapView = mapView;
        add(id = new JTextField(40), "wrap, span 3");
        add(location = new JTextField(40), "span 2");
        JButton editButton;
        add(editButton = new JButton(wmeAction), "wrap");
        add(start = new JTextField(40));
        add(stop = new JTextField(40), "wrap, span 3");
        add(road = new JTextField(40), "wrap, span 3");
        add(locationDetails = new JTextField(40), "wrap, span 32");
        JScrollPane circulationDetailsScroll = new JScrollPane(circulationDetail = new JTextArea(3, 40));
        add(circulationDetailsScroll, "wrap, span 3");
        JScrollPane descriptionScroll = new JScrollPane(description = new JTextArea(3, 40));
        add(descriptionScroll, "wrap, span 3");
        id.setBorder(BorderFactory.createTitledBorder("id"));
        location.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("detailPanel.location")));
        start.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("detailPanel.start")));
        stop.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("detailPanel.end")));
        road.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("detailPanel.road")));
        locationDetails.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("detailPanel.locationDetail")));
        circulationDetailsScroll.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("detailPanel.impact")));
        circulationDetail.setLineWrap(true);
        description.setLineWrap(true);
        descriptionScroll.setBorder(BorderFactory.createTitledBorder(localizationService.getMessage("detailPanel.description")));

        statusRadioButtons = new EnumMap<>(Status.class);
        ButtonGroup statusGroup = new ButtonGroup();
        Status[] values = Status.values();
        for (int i = 0; i < values.length; i++) {
            Status status = values[i];
            JRadioButton radioButton = new JRadioButton(localizationService.getMessage("enum.status." + status));
            statusGroup.add(radioButton);
            radioButton.addActionListener(new StatusChangeAction(status));
            statusRadioButtons.put(status, radioButton);
            if (((i + 1) % 2) == 0) {
                add(radioButton, "wrap");
            } else {
                add(radioButton);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, -1);
    }

    public void setRoadwork(Roadwork roadwork) {
        this.roadwork = roadwork;
        id.setText(roadwork.getId());
        location.setText(roadwork.getLatitude() + "," + roadwork.getLongitude());
        SimpleDateFormat format = new SimpleDateFormat(config.getDatePattern());
        start.setText(format.format(new Date(roadwork.getStart())));
        stop.setText(format.format(new Date(roadwork.getEnd())));
        road.setText(roadwork.getRoad());
        road.setCaretPosition(0);
        locationDetails.setText(roadwork.getLocationDetails());
        locationDetails.setCaretPosition(0);
        circulationDetail.setText(roadwork.getImpactCirculationDetail());
        circulationDetail.setCaretPosition(0);
        description.setText(roadwork.getDescription());
        description.setWrapStyleWord(true);
        statusRadioButtons.get(roadwork.getSyncData().getStatus()).setSelected(true);
    }

    private class StatusChangeAction implements ActionListener {
        private final Status status;

        private StatusChangeAction(Status status) {
            this.status = status;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (roadwork != null) {
                roadwork.getSyncData().setStatus(status);
                roadwork.updateStatus(status);
                mapView.repaint();
            }
        }
    }
}
