package org.example;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import org.example.engine.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Main implements GLEventListener {

    private static GL4 gl;
    private Shader shader;

    private float polygon[] = {
            -1.0f, 1.0f, -1.0f,     1.0f, 0.0f, 0.0f,
            1.0f, 1.0f, -1.0f,      0.5f, 0.5f, 0.0f,
            1.0f, 1.0f, 1.0f,       0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 1.0f,      0.0f, 0.5f, 0.5f,
            -1.0f, -1.0f, -1.0f,    0.0f, 0.0f, 1.0f,
            1.0f, -1.0f, -1.0f,     0.5f, 0.0f, 0.5f,
            1.0f, -1.0f, 1.0f,      0.5f, 0.5f, 0.5f,
            -1.0f, -1.0f, 1.0f,     1.0f, 1.0f, 1.0f
    };

    private int[] indices = {
        0, 1, 3,
        1, 2, 3,
        0, 4, 1,
        1, 4, 5,
        0, 3, 7,
        0, 7, 4,
        1, 6, 2,
        1, 5, 6,
        2, 7, 3,
        2, 6, 7,
        4, 7, 5,
        5, 7, 6
};

    private int vbo_polygon;
    private int vao_polygon;
    private int ebo_polygon;

    Matrix4f model;
    Vector3f scale = new Vector3f(1.0f);
    Vector3f position = new Vector3f(0.0f);
    Vector3f rotation = new Vector3f(0.0f);

    Matrix4f camera;
    Vector3f cameraPosition = new Vector3f(0.0f, 0.0f, 0.4f);
    Vector3f cameraTarget = new Vector3f(0.0f, 0.0f, 0.0f);
    Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);

    Matrix4f projection;

    public static void main(String[] args) {
        JFrame frame = new JFrame("JOGL 3D Window");

        GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setDoubleBuffered(true);

        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(new Main());
        frame.getContentPane().add(canvas);

        frame.setSize(900, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocus();

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();
        System.out.println("Init OpenGL: " + gl);

        String vertexShaderPath = "shaders/vertexShader.glsl";
        String fragmentShaderPath = "shaders/fragmentShader.glsl";

        shader = new Shader(gl, vertexShaderPath, fragmentShaderPath);

        int[] vao = new int[1];
        gl.glGenVertexArrays(1, vao, 0);
        vao_polygon = vao[0];

        int[] vbo = new int[1];
        gl.glGenBuffers(1, vbo, 0);
        vbo_polygon = vbo[0];

        int[] ebo = new int[1];
        gl.glGenBuffers(1, ebo, 0);
        ebo_polygon = ebo[0];

        gl.glBindVertexArray(vao_polygon);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo_polygon);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, polygon.length * Float.BYTES, FloatBuffer.wrap(polygon), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo_polygon);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * Integer.BYTES, IntBuffer.wrap(indices), GL.GL_STATIC_DRAW);

        int stride = 6 * Float.BYTES;

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, stride, 0);

        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, stride, 3 * Float.BYTES);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(0);

        model = new Matrix4f();

        camera = new Matrix4f();
        camera.lookAt(cameraPosition, cameraTarget, cameraUp);

        projection = new Matrix4f();
        projection.perspective(45.f , 1.f, 0.01f, 100.f);

        gl.glEnable(gl.GL_DEPTH_TEST);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        shader.use();

        camera.identity();
        cameraPosition.set(5.0f * Math.cos(System.currentTimeMillis() * 0.001), 0.f, 5.0f * Math.sin(System.currentTimeMillis() * 0.001));
        cameraTarget.set(0.f, 0.f, 0.f);
        cameraUp.set(0.f, 1.f, 0.f);
        camera.lookAt(cameraPosition, cameraTarget, cameraUp);

        projection.identity();
        projection.perspective(45.f , 1.f, 0.01f, 100.f);

        position.x = (float)(0.8f * Math.cos(System.currentTimeMillis() * 0.001));
        position.y = (float)(0.8f * Math.sin(System.currentTimeMillis() * 0.001));

        scale.x = 0.3f;
        scale.y = 0.3f;
        scale.z = 0.3f;

        model.identity();
        model.translate(position);
        rotation.x += 0.01f;
        model.rotateX(rotation.x);
        rotation.y += 0.01f;
        model.rotateY(rotation.y);
        rotation.z += 0.01f;
        model.rotateZ(rotation.z);
        model.scale(scale);

        Matrix4f pvm = new Matrix4f(projection).mul(camera).mul(model);

        shader.setMatrix4f("pvm", pvm);

        gl.glBindVertexArray(vao_polygon);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Dispose OpenGL");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }
}