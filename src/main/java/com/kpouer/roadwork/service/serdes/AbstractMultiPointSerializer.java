/*
 * Copyright 2023 Matthieu Casanova
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
package com.kpouer.roadwork.service.serdes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kpouer.wkt.shape.AbstractMultiPointShape;
import com.kpouer.themis.Component;

import java.io.IOException;

@Component
public class AbstractMultiPointSerializer extends ShapeSerializer<AbstractMultiPointShape> {
    public AbstractMultiPointSerializer() {
        super(AbstractMultiPointShape.class);
    }

    @Override
    public void serialize(AbstractMultiPointShape value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("xpoints");
        gen.writeArray(value.getXpoints(), 0, value.getNpoints());
        gen.writeFieldName("ypoints");
        gen.writeArray(value.getYpoints(), 0, value.getNpoints());
        gen.writeNumberField("npoints", value.getNpoints());
        gen.writeBooleanField("closed", value.isClosed());

        gen.writeEndObject();
    }
}
