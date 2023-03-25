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
package com.kpouer.roadwork.ui.logpanel;

import com.kpouer.roadwork.ui.MainPanel;
import lombok.RequiredArgsConstructor;
import com.kpouer.themis.annotation.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
@Component(singleton = false)
public class LogPanelDialog extends JDialog {
    public LogPanelDialog(MainPanel parent, List<String> logs) {
        super(parent);
        var list = new JList<>(new StringAbstractListModel(logs));
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        add(new JScrollPane(list));
        setSize(1024, 768);
        var toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        var copy = new JButton("Copy");
        toolbarPanel.add(copy);
        copy.addActionListener(e -> {
            var builder = new StringBuilder(10000);
            list.getSelectedValuesList().forEach(line -> builder.append(line).append('\n'));
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(builder.toString()), null);
        });
        getContentPane().add(toolbarPanel, BorderLayout.NORTH);
    }

    @RequiredArgsConstructor
    private static class StringAbstractListModel extends AbstractListModel<String> {
        private final List<String> logs;

        @Override
        public int getSize() {
            return logs.size();
        }

        @Override
        public String getElementAt(int index) {
            return logs.get(index);
        }
    }
}
