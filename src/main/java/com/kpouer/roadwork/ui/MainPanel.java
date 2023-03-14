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
package com.kpouer.roadwork.ui;

import com.kpouer.hermes.Hermes;
import com.kpouer.hermes.Listener;
import com.kpouer.mapview.MapView;
import com.kpouer.roadwork.action.ExitAction;
import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.event.OpendataServiceUpdated;
import com.kpouer.roadwork.event.UserSettingsUpdated;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.model.sync.Status;
import com.kpouer.roadwork.service.exception.OpenDataException;
import com.kpouer.roadwork.service.OpendataServiceManager;
import com.kpouer.roadwork.service.SoftwareModel;
import com.kpouer.roadwork.ui.menu.MenuService;
import lombok.extern.slf4j.Slf4j;
import com.kpouer.themis.Component;
import org.springframework.web.client.RestClientException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;

import static com.kpouer.roadwork.configuration.Config.*;

/**
 * @author Matthieu Casanova
 */
@Slf4j
@Component
public class MainPanel extends JFrame {

    private final MapView mapView;
    private final DetailPanel detailPanel;
    private final OpendataServiceManager opendataServiceManager;
    private final SoftwareModel softwareModel;
    private final Config config;
    private final Hermes hermes;

    public MainPanel(ExitAction exitAction,
                     MapView mapView,
                     MenuService menuService,
                     DetailPanel detailPanel,
                     OpendataServiceManager opendataServiceManager,
                     ToolbarPanel toolbarPanel,
                     SoftwareModel softwareModel,
                     Config config,
                     Hermes hermes) throws IOException {
        super("Roadwork");
        this.hermes = hermes;
        if ("Mac OS X".equals(System.getProperty("os.name"))) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
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
        setJMenuBar(menuService.loadMenu());
        getContentPane().add(toolbarPanel, BorderLayout.PAGE_START);
        this.detailPanel = detailPanel;
        var split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.detailPanel, mapView);
        getContentPane().add(split);
        this.mapView = mapView;
        var userSettings = config.getUserSettings();
        if (userSettings.getFrameWidth() != 0 && userSettings.getFrameHeight() != 0) {
            setBounds(userSettings.getFrameX(), userSettings.getFrameY(), userSettings.getFrameWidth(), userSettings.getFrameHeight());
        } else {
            setSize(1024, 768);
        }
        setVisible(true);
        EventQueue.invokeLater(this::loadData);
        hermes.subscribe(this);
    }

    @Listener
    public void onOpendataServiceUpdated(OpendataServiceUpdated event) {
        loadData();
    }

    @Listener
    public void onUserSettingsUpdated(UserSettingsUpdated event) {
        var userSettings = config.getUserSettings();
        mapView.removeAllMarkers();
        var roadworkData = softwareModel.getRoadworkData();
        for (var roadwork : roadworkData) {
            if (!userSettings.isHideExpired() || roadwork.getSyncData().getStatus() != Status.Finished) {
                var markers = roadwork.getMarker();
                Arrays.stream(markers).forEach(mapView::addMarker);
            }
        }
        mapView.repaint();
    }

    private void loadData() {
        try {
            mapView.setTileServer(opendataServiceManager.getTileServer());
            mapView.setCenter(opendataServiceManager.getCenter());
            mapView.removeAllMarkers();
            var roadworkDataOptional = opendataServiceManager.getData();
            roadworkDataOptional.ifPresent(this::setRoadworkData);
        } catch (OpenDataException e) {
            logger.error("Opendata exception", e);
            resetCurrentOpendataService();
            EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
        } catch (IOException | RestClientException e) {
            logger.error("Error retrieving data", e);
            EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error retrieving data", "Error", JOptionPane.ERROR_MESSAGE));
        }
    }

    private void resetCurrentOpendataService() {
        logger.info("resetCurrentOpendataService");
        if (!DEFAULT_OPENDATA_SERVICE.equals(config.getOpendataService())) {
            var event = new OpendataServiceUpdated(this, config.getOpendataService(), DEFAULT_OPENDATA_SERVICE);
            config.setOpendataService(DEFAULT_OPENDATA_SERVICE);
            hermes.publish(event);
        }
    }

    private void setRoadworkData(RoadworkData roadworkData) {
        softwareModel.setRoadworkData(roadworkData);
        roadworkData.forEach(this::addMarker);
        mapView.fitToMarkers();
        mapView.repaint();
    }

    private void addMarker(Roadwork roadwork) {
        var mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                softwareModel.setRoadwork(roadwork);
                detailPanel.setRoadwork(roadwork);
            }
        };
        var markers = roadwork.getMarker();
        Arrays.stream(markers).forEach(marker -> {
                    marker.addMouseListener(mouseAdapter);
                    mapView.addMarker(marker);
                }
        );
    }
}
