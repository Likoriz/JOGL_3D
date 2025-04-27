package org.example.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.example.engine.Camera_Movement.*;

public class Camera {
    final float YAW = 90.0f;
    final float PITCH = 0.0f;

    final float SPEED = 2.5f;
    final float SENSITIVITY = 0.1f;

    final float FOV = 45.0f;
    final float ZNEAR = 0.1f;
    final float ZFAR = 1000.f;
    final float ASPECTRATIO = 16.f/9.f;

    public Vector3f position = new Vector3f();
    Vector3f front = new Vector3f();
    Vector3f up = new Vector3f();
    Vector3f right = new Vector3f();
    Vector3f worldUp = new Vector3f();

    float yaw;
    float pitch;
    float speed;
    float sensitivity;
    float fov;
    float zNear;
    float zFar;
    float aspectRatio;

    public Camera(Vector3f position) {
        fov = FOV;
        zNear = ZNEAR;
        zFar = ZFAR;
        aspectRatio = ASPECTRATIO;
        speed = SPEED;
        sensitivity = SENSITIVITY;
        yaw = YAW;
        pitch = PITCH;
        front = new Vector3f(0.0f, 0.0f, -1.0f);

        up = new Vector3f(0.0f, 1.0f, 0.0f);

        this.position = position;
        this.worldUp = up;

        updateCameraVectors();
    }

    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        fov = FOV;
        zNear = ZNEAR;
        zFar = ZFAR;
        aspectRatio = ASPECTRATIO;
        speed = SPEED;
        sensitivity = SENSITIVITY;
        front = new Vector3f(0.0f, 0.0f, -1.0f);

        this.position = new Vector3f(posX, posY, posZ);
        this.worldUp = new Vector3f(upX, upY, upZ);
        this.yaw = yaw;
        this.pitch = pitch;

        updateCameraVectors();
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, new Vector3f(position).add(front), up);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective((float)(Math.toRadians(fov)), aspectRatio, zNear, zFar);
    }

    public void move(int dirs, float deltaTime) {
        float velocity = speed * deltaTime;
        Vector3f direction = new Vector3f(0.f, 0.f, 0.f);

        direction.z = ((dirs & CAM_FORWARD.getValue()) != 0 ? 1 : 0) - ((dirs & CAM_BACKWARD.getValue()) != 0 ? 1 : 0);
        direction.x = ((dirs & CAM_RIGHT.getValue()) != 0 ? 1 : 0) - ((dirs & CAM_LEFT.getValue()) != 0 ? 1 : 0);
        direction.y = ((dirs & CAM_UP.getValue()) != 0 ? 1 : 0) - ((dirs & CAM_DOWN.getValue()) != 0 ? 1 : 0);

        if (direction.lengthSquared() != 0.0f)
        direction.normalize();

        position.add(new Vector3f(front).mul(velocity * direction.z));
        position.add(new Vector3f(right).mul(velocity * direction.x));
        position.add(new Vector3f(up).mul(velocity * direction.y));

        updateCameraVectors();
    }


    public void rotate(float xOffset, float yOffset) {
        xOffset *= sensitivity;
        yOffset *= sensitivity;

        yaw += xOffset;
        pitch += yOffset;

        boolean constrainPitch = true;

        if (constrainPitch) {
            if (pitch > 89.0f)
                pitch = 89.0f;

            if (pitch < -89.0f)
                pitch = -89.0f;
        }

        updateCameraVectors();
    }

    public void changeFOV(float value) {
        fov -= value;

        if (fov < 1.0f)
            fov = 1.0f;

        if (fov > 120.0f)
            fov = 120.0f;

        updateCameraVectors();
    }

    public void updateCameraVectors() {
        this.front.x = (float)(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        this.front.y = (float)(Math.sin(Math.toRadians(pitch)));
        this.front.z = (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));

        this.front.normalize();

        this.right = new Vector3f(this.front).cross(worldUp).normalize();
        this.up = new Vector3f(this.right).cross(this.front).normalize();
    }
}
