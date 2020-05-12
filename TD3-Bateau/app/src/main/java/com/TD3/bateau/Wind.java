package com.TD3.bateau;

public class Wind {
    private String direction;
    private double strength;

    public Wind(String direction, double strength) {
        this.direction = direction;
        this.strength = strength;
    }

    @Override
    public String toString() {
        return  direction + ", Force " + strength;
    }
}
