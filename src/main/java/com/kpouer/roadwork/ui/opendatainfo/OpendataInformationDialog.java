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
package com.kpouer.roadwork.ui.opendatainfo;

import com.kpouer.mapview.MapView;
import com.kpouer.mapview.marker.Circle;
import com.kpouer.mapview.tile.DefaultTileServer;
import com.kpouer.roadwork.opendata.OpendataService;
import com.kpouer.roadwork.opendata.json.model.Metadata;
import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.roadwork.service.exception.OpenDataException;
import com.kpouer.roadwork.service.OpendataServiceManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Matthieu Casanova
 */
public class OpendataInformationDialog extends JDialog {
    private final OpendataServiceManager opendataServiceManager;
    private final MapView mapView;
    private final OpendataInformationPanel opendataInformationPanel;

    public OpendataInformationDialog(JFrame parent,
                                     OpendataServiceManager opendataServiceManager,
                                     DefaultTileServer tileServer,
                                     LocalizationService localizationService) {
        super(parent);
        this.opendataServiceManager = opendataServiceManager;
        mapView = new MapView(tileServer);
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        addThirdParty();
        var licenceTree = buildLicenceTree();
        licenceTree.setPreferredSize(new Dimension(150, 0));
        opendataInformationPanel = new OpendataInformationPanel(localizationService);
        opendataInformationPanel.setPreferredSize(new Dimension(150, 0));
        licenceTree.addTreeSelectionListener(e -> {
            TreePath path = e.getPath();
            DefaultMutableTreeNode leaf = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = leaf.getUserObject();
            if (userObject instanceof Metadata metadata) {
                opendataInformationPanel.setMetadata(metadata);
            }
        });
        splitPane.setLeftComponent(new JScrollPane(licenceTree));
        var rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplit.setLeftComponent(opendataInformationPanel);
        rightSplit.setRightComponent(mapView);
        splitPane.setRightComponent(rightSplit);

        getContentPane().add(splitPane);
        mapView.setZoom(3);
        setSize(800, 480);
    }

    private void addThirdParty() {
        opendataServiceManager.getThirdPartyServices()
                .stream()
                .map(this::getOpendataService)
                .filter(Objects::nonNull)
                .map(OpendataService::getMetadata)
                .map(metadata -> buildCircle(metadata, Color.BLUE))
                .forEach(mapView::addMarker);
    }

    private OpendataService getOpendataService(String name) {
        try {
            return opendataServiceManager.getOpendataService(name);
        } catch (OpenDataException e) {
            return null;
        }
    }

    private JTree buildLicenceTree() {
        var services = opendataServiceManager.getDefaultServices();
        var root = new DefaultMutableTreeNode();
        var countryNodes = new HashMap<String, DefaultMutableTreeNode>();

        var nodes = services
                .parallelStream()
                .map(this::getOpendataService)
                .filter(Objects::nonNull)
                .map(OpendataService::getMetadata)
                .map(DefaultMutableTreeNode::new)
                .toList();

        for (var node : nodes) {
            var metadata = (Metadata) node.getUserObject();
            mapView.addMarker(buildCircle(metadata, Color.RED));

            var country = metadata.getCountry();
            var countryNode = countryNodes.computeIfAbsent(country, DefaultMutableTreeNode::new);
            countryNode.add(node);
        }
        countryNodes.keySet().stream().sorted()
                .map(countryNodes::get)
                .forEach(root::add);
        return new JTree(root);
    }

    private static Circle buildCircle(Metadata metadata, Color color) {
        return new Circle(metadata.getCenter().getLat(), metadata.getCenter().getLon(), 5, color);
    }
}
