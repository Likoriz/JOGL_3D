package org.example.data;

public enum LightType {
    None(0),
    Directional(1),
    Point(2),
    Spot(3),
    Ambient(4);

    private final int value;

    LightType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
