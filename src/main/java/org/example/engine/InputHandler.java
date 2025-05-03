package org.example.engine;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

import org.example.Main;


public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private boolean[] keys = new boolean[256];
    private float lastMouseX, lastMouseY;
    private float offsetX = 0, offsetY = 0;

    private final Main mainInstance;
    private Camera camera;

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
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        float mouseX = e.getX();
        float mouseY = e.getY();

        offsetX = mouseX - lastMouseX;
        offsetY = lastMouseY - mouseY;

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        camera.rotate(offsetX, offsetY);
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;

        switch(e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                mainInstance.wireframeMode = !(mainInstance.wireframeMode);
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
                mainInstance.lightType = 1;
                break;
            case KeyEvent.VK_2:
                mainInstance.lightType = 2;
                break;
            case KeyEvent.VK_3:
                mainInstance.lightType = 3;
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