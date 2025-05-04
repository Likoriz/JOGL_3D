package org.example.engine;

import org.example.data.LightType;
import org.joml.Vector3f;

public class Light {
    String name;
    boolean active;

    public LightType type;

    public Vector3f position;
    public Vector3f direction;
    public float cutOff;

    public Vector3f ambient;
    public Vector3f diffuse;
    public Vector3f specular;

    public float constant;
    public float linear;
    public float quadratic;

    public static final Light noneLight = new Light("NONE", false, LightType.None, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0, 0, 0);

    public Light(String name, boolean active) {
        Light base = noneLight;

        this.type = base.type;
        this.position = new Vector3f(base.position);
        this.direction = new Vector3f(base.direction);
        this.cutOff = base.cutOff;
        this.ambient = new Vector3f(base.ambient);
        this.diffuse = new Vector3f(base.diffuse);
        this.specular = new Vector3f(base.specular);
        this.constant = base.constant;
        this.linear = base.linear;
        this.quadratic = base.quadratic;

        if (name == null)
            this.name = "NONE";

        this.active = active;
    }

    public Light(String name, boolean active, LightType type, Vector3f position, Vector3f direction, float cutOff, Vector3f ambient, Vector3f diffuse, Vector3f specular, float constant, float linear, float quadratic) {
        this.name = name;
        this.active = active;
        this.type = type;
        this.position = position;
        this.direction = direction;
        this.cutOff = cutOff;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
    }

    public void initLikePointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular, float constant, float linear, float quadratic) {
        type = LightType.Point;
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
    }

    public void initLikeSpotLight(Vector3f position, Vector3f direction, float cutOff, Vector3f ambient, Vector3f diffuse, Vector3f specular, float constant, float linear, float quadratic) {
        type = LightType.Spot;
        this.position = position;
        this.direction = direction;
        this.cutOff = cutOff;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
    }

    public void initLikeDirectionalLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        type = LightType.Directional;
        this.direction = direction;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    public void initLikeAmbientLight(Vector3f ambient) {
        type = LightType.Ambient;
        this.ambient = ambient;
    }

    public boolean isLightOn() {
        return type.getValue() > LightType.None.getValue() && active;
    }

    public void turnOn() {
        active = true;
    }

    public void turnOff() {
        active = false;
    }

    //Returns 1 if light is put to shader and 0 if is not
    public int putInShader(Shader shader, int lightNumber) {
        if (!isLightOn()) return 0;

        String num = Integer.toString(lightNumber);

        switch (this.type)
        {
            case LightType.Directional:
                shader.setInt	("light[" + num + "].type", type.getValue());
                shader.setVec3	("light[" + num + "].direction",direction);
                shader.setVec3	("light[" + num + "].ambient",	ambient);
                shader.setVec3	("light[" + num + "].diffuse",	diffuse);
                shader.setVec3	("light[" + num + "].specular", specular);
                break;
            case LightType.Point:
                shader.setInt	("light[" + num + "].type", type.getValue());
                shader.setVec3	("light[" + num + "].position", position);
                shader.setVec3	("light[" + num + "].ambient",	ambient);
                shader.setVec3	("light[" + num + "].diffuse",	diffuse);
                shader.setVec3	("light[" + num + "].specular", specular);
                shader.setFloat("light[" + num + "].constant", constant);
                shader.setFloat("light[" + num + "].linear",	linear);
                shader.setFloat("light[" + num + "].quadratic",quadratic);
                break;
            case LightType.Spot:
                shader.setInt	("light[" + num + "].type", type.getValue());
                shader.setVec3	("light[" + num + "].position", position);
                shader.setVec3	("light[" + num + "].direction",direction);
                shader.setFloat("light[" + num + "].cutOff",	cutOff);
                shader.setVec3	("light[" + num + "].ambient",	ambient);
                shader.setVec3	("light[" + num + "].diffuse",	diffuse);
                shader.setVec3	("light[" + num + "].specular", specular);
                shader.setFloat("light[" + num + "].constant", constant);
                shader.setFloat("light[" + num + "].linear",	linear);
                shader.setFloat("light[" + num + "].quadratic",quadratic);
                break;
            case LightType.Ambient:
                shader.setInt	("light[" + num + "].type", type.getValue());
                shader.setVec3	("light[" + num + "].ambient",	ambient);
                break;
        }
        return 1;
    }
}
