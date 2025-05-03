package org.example.engine;

import org.joml.Vector3f;

public class Light {
    //public LightType type;

    public Vector3f position;
    public Vector3f direction;
    public float cutOff;

    public Vector3f ambient;
    public Vector3f diffuse;
    public Vector3f specular;

    public float constant;
    public float linear;
    public float quadratic;
}
