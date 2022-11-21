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
import ch.qos.logback.core.testUtil.StringListAppender;
import com.kpouer.roadwork.event.ExceptionEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author Matthieu Casanova
 */
public class LoopListAppender extends StringListAppender<ILoggingEvent> {
    private final ApplicationEventPublisher applicationEventPublisher;

    public LoopListAppender(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        strList = new LoopList<>(1000);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        super.append(eventObject);
        if (eventObject.getThrowableProxy() != null) {
            applicationEventPublisher.publishEvent(new ExceptionEvent(eventObject));
        }
    }
}
