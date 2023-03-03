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
package com.kpouer.roadwork.ui.about;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.IOException;

/**
 * @author Matthieu Casanova
 */
public class OpensourceTableModel implements TableModel {
    private final Oss[] data;

    public OpensourceTableModel() {
        try {
            var objectMapper = new ObjectMapper();
            data = objectMapper.readValue(OpensourceTableModel.class.getResource("/com/kpouer/ui/oss.json"), Oss[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Oss get(int row) {
        return data[row];
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var oss = data[rowIndex];
        return switch (columnIndex) {
            case 0 -> oss.name();
            case 1 -> oss.licence();
            case 2 -> oss.url();
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
