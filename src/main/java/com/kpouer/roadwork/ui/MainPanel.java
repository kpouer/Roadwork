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
import com.kpouer.mapview.marker.Marker;
import com.kpouer.roadwork.action.ExitAction;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.configuration.UserSettings;
import com.kpouer.roadwork.event.OpendataServiceUpdated;
import com.kpouer.roadwork.event.UserSettingsUpdated;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.model.sync.Status;
import com.kpouer.roadwork.service.OpendataServiceManager;
import com.kpouer.roadwork.service.SoftwareModel;
import com.kpouer.roadwork.ui.menu.MenuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Matthieu Casanova
 */
@Service
public class MainPanel extends JFrame implements GenericApplicationListener {
    private final MapView mapView;
    private final DetailPanel detailPanel;
    private final OpendataServiceManager opendataServiceManager;
    private final SoftwareModel softwareModel;
    private final Config config;

    public MainPanel(@Value("classpath:com/kpouer/ui/menu.json") Resource menu,
                     ExitAction exitAction,
                     MapView mapView,
                     MenuService menuService,
                     DetailPanel detailPanel,
                     OpendataServiceManager opendataServiceManager,
                     ToolbarPanel toolbarPanel,
                     SoftwareModel softwareModel,
                     Config config) throws IOException {
        super("Roadwork");
        this.config = config;

        softwareModel.setMainFrame(this);
        this.opendataServiceManager = opendataServiceManager;
        this.softwareModel = softwareModel;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                exitAction.actionPerformed(null);
            }
        });
//        getContentPane().add(menuService.loadMenu(menu), BorderLayout.PAGE_START);
        setJMenuBar(menuService.loadMenu(menu));
        getContentPane().add(toolbarPanel, BorderLayout.PAGE_START);
        this.detailPanel = detailPanel;
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.detailPanel, mapView);
        getContentPane().add(split);
        this.mapView = mapView;
        UserSettings userSettings = config.getUserSettings();
        if (userSettings.getFrameWidth() != 0 && userSettings.getFrameHeight() != 0) {
            setBounds(userSettings.getFrameX(), userSettings.getFrameY(), userSettings.getFrameWidth(), userSettings.getFrameHeight());
        } else {
            setSize(1024, 768);
        }
        setVisible(true);
        EventQueue.invokeLater(this::loadData);
    }

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        Class<?> rawClass = eventType.getRawClass();
        return rawClass != null &&
                (rawClass.isAssignableFrom(OpendataServiceUpdated.class) ||
                        rawClass.isAssignableFrom(UserSettingsUpdated.class));
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof OpendataServiceUpdated) {
            mapView.setCenter(opendataServiceManager.getCenter());
            loadData();
        } else if (event instanceof UserSettingsUpdated) {
            UserSettings userSettings = config.getUserSettings();
            mapView.removeAllMarkers();
            RoadworkData roadworkData = softwareModel.getRoadworkData();
            for (Roadwork roadwork : roadworkData) {
                if (!userSettings.isHideExpired() || roadwork.getSyncData().getStatus() != Status.Finished) {
                    mapView.addMarker(roadwork.getMarker());
                }
            }
            mapView.repaint();
        }
    }

    private void loadData() {
        try {
            mapView.removeAllMarkers();
            Optional<RoadworkData> roadworkDataOptional = opendataServiceManager.getData();
            roadworkDataOptional.ifPresent(this::setRoadworkData);
            mapView.fitToMarkers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setRoadworkData(RoadworkData roadworkData) {
        softwareModel.setRoadworkData(roadworkData);
        roadworkData.forEach(this::addMarker);
        mapView.repaint();
    }

    private void addMarker(Roadwork roadwork) {
        Marker marker = roadwork.getMarker();
        marker.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                softwareModel.setSelectedRoadwork(roadwork);
                detailPanel.setRoadwork(roadwork);
            }
        });
        mapView.addMarker(marker);
    }
}
