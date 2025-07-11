package org.example.engine;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

import org.example.Main;
import org.example.data.CameraMovement;

import javax.swing.*;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private final boolean[] keys = new boolean[256];
    public float lastMouseX;
    public float lastMouseY;

    private final Main mainInstance;
    private final Camera camera;

    public InputHandler(Main mainInstance, Camera camera) {
        this.mainInstance = mainInstance;
        this.camera = camera;

        Arrays.fill(keys, false);
    }

    public void update(float deltaTime) {
        int dir = getMovementDirection();
        camera.move(dir, deltaTime);
    }

    private int getMovementDirection() {
        int dir = 0;
        if (keys[KeyEvent.VK_W])
            dir |= CameraMovement.CAM_FORWARD.getValue();
        if (keys[KeyEvent.VK_S])
            dir |= CameraMovement.CAM_BACKWARD.getValue();
        if (keys[KeyEvent.VK_A])
            dir |= CameraMovement.CAM_LEFT.getValue();
        if (keys[KeyEvent.VK_D])
            dir |= CameraMovement.CAM_RIGHT.getValue();
        if (keys[KeyEvent.VK_Q])
            dir |= CameraMovement.CAM_UP.getValue();
        if (keys[KeyEvent.VK_E])
            dir |= CameraMovement.CAM_DOWN.getValue();

        return dir;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();

        if (SwingUtilities.isLeftMouseButton(e))
            Main.triggerPulse((int) lastMouseX, (int) lastMouseY);
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        float mouseX = e.getX();
        float mouseY = e.getY();

        if (SwingUtilities.isRightMouseButton(e)) {
            float offsetX = mouseX - lastMouseX;
            float offsetY = lastMouseY - mouseY;

            lastMouseX = mouseX;
            lastMouseY = mouseY;

            camera.rotate(offsetX, offsetY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;

        switch(e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                Main.wireframeMode = !(Main.wireframeMode);
                mainInstance.switchMode = true;
                break;
            case KeyEvent.VK_NUMPAD0:
                mainInstance.backgroundColor = Color.WHITE;
                break;
            case KeyEvent.VK_NUMPAD1:
                mainInstance.backgroundColor = Color.BLACK;
                break;
            case KeyEvent.VK_NUMPAD2:
                mainInstance.backgroundColor = Color.RED;
                break;
            case KeyEvent.VK_NUMPAD3:
                mainInstance.backgroundColor = Color.GREEN;
                break;
            case KeyEvent.VK_NUMPAD4:
                mainInstance.backgroundColor = Color.BLUE;
                break;
            case KeyEvent.VK_NUMPAD5:
                mainInstance.backgroundColor = new Color(140, 204, 217);
                break;
            case KeyEvent.VK_1:
                if (mainInstance.sunLight.isLightOn())
                    mainInstance.sunLight.turnOff();
                else
                    mainInstance.sunLight.turnOn();
                break;
            case KeyEvent.VK_2:
                if (mainInstance.redLamp.isLightOn())
                    mainInstance.redLamp.turnOff();
                else
                    mainInstance.redLamp.turnOn();
                break;
            case KeyEvent.VK_3:
                if (mainInstance.blueLamp.isLightOn())
                    mainInstance.blueLamp.turnOff();
                else
                    mainInstance.blueLamp.turnOn();
                break;
            case KeyEvent.VK_F:
                if (mainInstance.flashLight.isLightOn())
                    mainInstance.flashLight.turnOff();
                else
                    mainInstance.flashLight.turnOn();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float scroll = e.getWheelRotation();
        camera.changeFOV(scroll);
    }
}