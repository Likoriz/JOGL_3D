package org.example.engine;

public enum CameraMovement {
    CAM_FORWARD(1),
    CAM_BACKWARD(2),
    CAM_RIGHT(4),
    CAM_LEFT(8),
    CAM_UP(16),
    CAM_DOWN(32);

    private final int value;

    CameraMovement(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

