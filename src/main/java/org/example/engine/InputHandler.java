package org.example.engine;

import java.awt.event.*;

import org.example.Main;
import org.joml.Vector3f;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private boolean[] keys = new boolean[256];
    //private int mouseX, mouseY, lastMouseX, lastMouseY;
    //private boolean leftMouseButtonPressed = false;

    private final Main mainInstance;

    public InputHandler(Vector3f cameraPosition, Vector3f rotation, Main mainInstance) {
        this.mainInstance = mainInstance;
    }

    public void update() {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        //System.out.println(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
//        if (e.getButton() == MouseEvent.BUTTON1) {
//            leftMouseButtonPressed = true;
//        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        if (e.getButton() == MouseEvent.BUTTON1) {
//            leftMouseButtonPressed = false;
//        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        mouseX = e.getX();
//        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        mouseX = e.getX();
//        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float scrollAmount = e.getWheelRotation() * 0.1f;
        mainInstance.adjustCameraDistance(scrollAmount);
    }
}
