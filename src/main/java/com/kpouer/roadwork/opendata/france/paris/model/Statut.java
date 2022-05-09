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
package com.kpouer.roadwork.opendata.france.paris.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Matthieu Casanova
 */
public enum Statut {
    AVenir(1),
    EnCours(2),
    Suspendu(3),
    Prolongé(4),
    Terminé(5);

    private int value;

    Statut(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
