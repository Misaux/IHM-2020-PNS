package com.TD3.bateau;

import org.osmdroid.util.GeoPoint;

public class Beacon {
    private String name;
    private GeoPoint location;
    private double waterTemperature;
    private double airTemperature;
    private Wind wind;
    private double depth;

    public Beacon(String name, GeoPoint location, double waterTemperature, double airTemperature, Wind wind, double depth) {
        this.name = name;
        this.location = location;
        this.waterTemperature = waterTemperature;
        this.airTemperature = airTemperature;
        this.wind = wind;
        this.depth = depth;
    }

    public String getName() {
        return name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public double getWaterTemperature() {
        return waterTemperature;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public Wind getWind() {
        return wind;
    }

    public double getDepth() {
        return depth;
    }

}
