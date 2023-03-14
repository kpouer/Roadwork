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
package com.kpouer.roadwork.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import com.kpouer.hermes.Hermes;
import com.kpouer.roadwork.event.ExceptionEvent;

import java.util.Arrays;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class LoopListAppender extends AppenderBase<ILoggingEvent> {
    private final Hermes hermes;
    private final List<String> list;
    private final Layout<ILoggingEvent> layout;

    public LoopListAppender(Layout<ILoggingEvent> layout, Hermes hermes) {
        this.layout = layout;
        this.hermes = hermes;
        list = new LoopList<>(1000);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        var res = layout.doLayout(eventObject);
        String[] tokens = res.split("\n");
        list.addAll(Arrays.asList(tokens));
        if (eventObject.getThrowableProxy() != null) {
            hermes.publish(new ExceptionEvent(eventObject));
        }
    }

    public List<String> getList() {
        return list;
    }
}
