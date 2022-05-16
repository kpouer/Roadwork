package com.kpouer.roadwork.opendata.france.avignon.model;

import com.kpouer.roadwork.opendata.model.GeometryType;

public class Geometry {
    private GeometryType type;
    private double[][][][] coordinates;

    public GeometryType getType() {
        return type;
    }

    public void setType(GeometryType type) {
        this.type = type;
    }

    public double[][][][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][][][] coordinates) {
        this.coordinates = coordinates;
    }
}
