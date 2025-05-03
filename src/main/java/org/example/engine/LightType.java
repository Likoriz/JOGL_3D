package org.example.engine;

public enum LightType {
    Directional(1),
    Point(2),
    Spot(3);

    private final int value;

    LightType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
