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
package com.kpouer.roadwork.ui.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpouer.roadwork.service.LocalizationService;
import com.kpouer.themis.Themis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import com.kpouer.themis.annotation.Component;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Matthieu Casanova
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MenuService {
    private final Themis themis;
    private final LocalizationService localizationService;

    public JMenuBar loadMenu() throws IOException {
        try (var reader = new InputStreamReader(getClass().getResourceAsStream("/com/kpouer/roadwork/ui/menu.json"), UTF_8)) {
            var objectMapper = new ObjectMapper();
            var menuElement = objectMapper.readValue(reader, MenuElement[].class);

            var menuBar = new JMenuBar();
            for (var element : menuElement) {
                menuBar.add(getComponent(element));
            }

            return menuBar;
        }
    }

    private JComponent getComponent(MenuElement element) {
        if (element.getChildren() != null) {
            var menu = new JMenu(localizationService.getMessage("menu." + element.getName()));
            for (var child : element.getChildren()) {
                menu.add(getComponent(child));
            }
            return menu;
        }
        var actionName = element.getAction();
        if (actionName == null) {
            var jMenuItem = new JMenuItem("---");
            jMenuItem.setEnabled(false);
            return jMenuItem;
        }
        try {
            var action = themis.getComponentOfType(actionName, AbstractAction.class);
            return new JMenuItem(action);
        } catch (BeansException e) {
            logger.error("Unable to find menu named {}", actionName);
            var jMenuItem = new JMenuItem(actionName);
            jMenuItem.setEnabled(false);
            return jMenuItem;
        }
    }
}
