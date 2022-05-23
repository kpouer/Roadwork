package com.kpouer.roadwork.opendata.france.rennes.model;

import com.kpouer.roadwork.opendata.model.GeometryType;

import java.util.List;

public class Geometry{
    private List<Double[]> coordinates;
    private GeometryType type;

    public void setCoordinates(List<Double[]> coordinates){
        this.coordinates = coordinates;
    }

    public List<Double[]> getCoordinates(){
        return coordinates;
    }

    public void setType(GeometryType type){
        this.type = type;
    }

    public GeometryType getType(){
        return type;
    }
}