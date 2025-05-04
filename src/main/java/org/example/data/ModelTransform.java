package org.example.data;

import org.joml.Vector3f;

public class ModelTransform {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public ModelTransform() {
        position = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f();
    }

    public void setScale(float s) {
        scale.x = s;
        scale.y = s;
        scale.z = s;
    }
}
