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
package com.kpouer.roadwork.ui.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Matthieu Casanova
 */
@Service
public class MenuService {
    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);
    private final ApplicationContext applicationContext;

    public MenuService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public JMenuBar loadMenu(Resource resource) throws IOException {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            ObjectMapper objectMapper = new ObjectMapper();
            MenuElement[] menuElement = objectMapper.readValue(reader, MenuElement[].class);

            JMenuBar menuBar = new JMenuBar();
            for (MenuElement element : menuElement) {
                menuBar.add(getComponent(element));
            }

            return menuBar;
        }
    }

    private JComponent getComponent(MenuElement element) {
        if (element.getChildren() != null) {
            JMenu menu = new JMenu(element.getName());
            for (MenuElement child : element.getChildren()) {
                menu.add(getComponent(child));
            }
            return menu;
        }
        String actionName = element.getAction();
        if (actionName == null) {
            JMenuItem jMenuItem = new JMenuItem("---");
            jMenuItem.setEnabled(false);
            return jMenuItem;
        }
        try {
            AbstractAction action = applicationContext.getBean(actionName, AbstractAction.class);
            return new JMenuItem(action);
        } catch (BeansException e) {
            logger.error("Unable to find menu named {}", actionName);
            JMenuItem jMenuItem = new JMenuItem(actionName);
            jMenuItem.setEnabled(false);
            return jMenuItem;
        }
    }
}
