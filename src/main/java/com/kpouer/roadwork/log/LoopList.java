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

import java.util.AbstractList;

/**
 * @author Matthieu Casanova
 */
public class LoopList<E> extends AbstractList<E> {
    private Object[] data;
    private int cursor;
    private int length;
    public LoopList(int count) {
        data = new Object[count];
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public E get(int index) {
        return (E) data[index % data.length];
    }

    @Override
    public boolean add(E e) {
        data[cursor] = e;
        length++;
        cursor = (cursor + 1) % data.length;
        return true;
    }

    @Override
    public void clear() {
        length = 0;
        cursor = 0;
        data = new Object[data.length];
    }
}
