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
package com.kpouer.roadwork;

import com.kpouer.roadwork.ui.MainPanel;
import com.kpouer.themis.ThemisImpl;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
@Slf4j
public class RoadworkApplication {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            logger.error("Unable to set the look and feel", e);
        }
        var themis = new ThemisImpl("com.kpouer");
        themis.getComponentOfType(MainPanel.class);
    }

    public static void start() {
        main(new String[0]);
    }
}
