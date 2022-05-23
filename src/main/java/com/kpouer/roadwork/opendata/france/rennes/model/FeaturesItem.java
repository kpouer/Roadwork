package com.kpouer.roadwork.opendata.france.rennes.model;

public class FeaturesItem{
    private Geometry geometry;
    private Properties properties;

    public void setGeometry(Geometry geometry){
        this.geometry = geometry;
    }

    public Geometry getGeometry(){
        return geometry;
    }

    public void setProperties(Properties properties){
        this.properties = properties;
    }

    public Properties getProperties(){
        return properties;
    }
}
