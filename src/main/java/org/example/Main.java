package org.example;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import org.example.engine.Camera;
import org.example.engine.InputHandler;
import org.example.engine.ModelTransform;
import org.example.engine.Shader;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Main implements GLEventListener {
    private static GL4 gl;
    private Shader shader;
    private static InputHandler inputHandler;

    private final float[] cube = {
            //position			normal					texture				color
            -1.0f,-1.0f,-1.0f,	-1.0f,  0.0f,  0.0f,	0.0f, 0.0f,		0.0f, 1.0f, 0.0f,
            -1.0f,-1.0f, 1.0f,	-1.0f,  0.0f,  0.0f,	1.0f, 0.0f,		0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 1.0f,	-1.0f,  0.0f,  0.0f,	1.0f, 1.0f,		0.0f, 1.0f, 0.0f,
            -1.0f,-1.0f,-1.0f,	-1.0f,  0.0f,  0.0f,	0.0f, 0.0f,		0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 1.0f,	-1.0f,  0.0f,  0.0f,	1.0f, 1.0f,		0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f,-1.0f,	-1.0f,  0.0f,  0.0f,	0.0f, 1.0f,		0.0f, 1.0f, 0.0f,

            1.0f, 1.0f,-1.0f,	0.0f,  0.0f, -1.0f, 	0.0f, 1.0f,		1.0f, 0.0f, 0.0f,
            -1.0f,-1.0f,-1.0f,	0.0f,  0.0f, -1.0f, 	1.0f, 0.0f,		1.0f, 0.0f, 0.0f,
            -1.0f, 1.0f,-1.0f,	0.0f,  0.0f, -1.0f, 	1.0f, 1.0f,		1.0f, 0.0f, 0.0f,
            1.0f, 1.0f,-1.0f,	0.0f,  0.0f, -1.0f,		0.0f, 1.0f,		1.0f, 0.0f, 0.0f,
            1.0f,-1.0f,-1.0f,	0.0f,  0.0f, -1.0f,		0.0f, 0.0f,		1.0f, 0.0f, 0.0f,
            -1.0f,-1.0f,-1.0f,	0.0f,  0.0f, -1.0f,		1.0f, 0.0f,		1.0f, 0.0f, 0.0f,

            1.0f,-1.0f, 1.0f,	0.0f, -1.0f,  0.0f,		0.0f, 0.0f,		0.0f, 0.0f, 1.0f,
            -1.0f,-1.0f,-1.0f,	0.0f, -1.0f,  0.0f,		1.0f, 1.0f,		0.0f, 0.0f, 1.0f,
            1.0f,-1.0f,-1.0f,	0.0f, -1.0f,  0.0f,		0.0f, 1.0f,		0.0f, 0.0f, 1.0f,
            1.0f,-1.0f, 1.0f,	0.0f, -1.0f,  0.0f,		0.0f, 0.0f,		0.0f, 0.0f, 1.0f,
            -1.0f,-1.0f, 1.0f,	0.0f, -1.0f,  0.0f,		1.0f, 0.0f,		0.0f, 0.0f, 1.0f,
            -1.0f,-1.0f,-1.0f,	0.0f, -1.0f,  0.0f,		1.0f, 1.0f,		0.0f, 0.0f, 1.0f,

            -1.0f, 1.0f, 1.0f,	0.0f,  0.0f, 1.0f,		0.0f, 1.0f,		0.0f, 0.0f, 1.0f,
            -1.0f,-1.0f, 1.0f,	0.0f,  0.0f, 1.0f,		0.0f, 0.0f,		0.0f, 0.0f, 1.0f,
            1.0f,-1.0f, 1.0f,	0.0f,  0.0f, 1.0f,		1.0f, 0.0f,		0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f,	0.0f,  0.0f, 1.0f,		1.0f, 1.0f,		0.0f, 0.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,	0.0f,  0.0f, 1.0f,		0.0f, 1.0f,		0.0f, 0.0f, 1.0f,
            1.0f,-1.0f, 1.0f,	0.0f,  0.0f, 1.0f,		1.0f, 0.0f,		0.0f, 0.0f, 1.0f,

            1.0f, 1.0f, 1.0f,	1.0f,  0.0f,  0.0f,		0.0f, 1.0f,		1.0f, 0.0f, 0.0f,
            1.0f,-1.0f,-1.0f,	1.0f,  0.0f,  0.0f,		1.0f, 0.0f,		1.0f, 0.0f, 0.0f,
            1.0f, 1.0f,-1.0f,	1.0f,  0.0f,  0.0f,		1.0f, 1.0f,		1.0f, 0.0f, 0.0f,
            1.0f,-1.0f,-1.0f,	1.0f,  0.0f,  0.0f,		1.0f, 0.0f,		1.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 1.0f,	1.0f,  0.0f,  0.0f,		0.0f, 1.0f,		1.0f, 0.0f, 0.0f,
            1.0f,-1.0f, 1.0f,	1.0f,  0.0f,  0.0f,		0.0f, 0.0f,		1.0f, 0.0f, 0.0f,

            1.0f, 1.0f, 1.0f,	0.0f,  1.0f,  0.0f,		1.0f, 0.0f,		0.0f, 1.0f, 0.0f,
            1.0f, 1.0f,-1.0f,	0.0f,  1.0f,  0.0f,		1.0f, 1.0f,		0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f,-1.0f,	0.0f,  1.0f,  0.0f,		0.0f, 1.0f,		0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f,	0.0f,  1.0f,  0.0f,		1.0f, 0.0f,		0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f,-1.0f,	0.0f,  1.0f,  0.0f,		0.0f, 1.0f,		0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 1.0f,	0.0f,  1.0f,  0.0f,		0.0f, 0.0f,		0.0f, 1.0f, 0.0f
    };

    private final int cubeCount = 200;
    ModelTransform[] cubeTrans;

    private int vbo_polygon;
    private int vao_polygon;

    private Matrix4f model;
    private final Vector3f scale = new Vector3f(1.0f);
    private final Vector3f position = new Vector3f(0.0f);
    private final Vector3f rotation = new Vector3f(0.0f);

    private static Camera camera = new Camera(new Vector3f(0.0f, 0.0f, -2.0f));
    private long oldTime = System.currentTimeMillis();
    private long newTime;
    private float deltaTime;

    private Texture texture;
    public boolean wireframeMode = false;
    public boolean switchMode = false;

    public static void main(String[] args) throws AWTException {
        JFrame frame = new JFrame("JOGL 3D Window");

        GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setDoubleBuffered(true);

        GLCanvas canvas = new GLCanvas(capabilities);
        Main mainInstance = new Main();
        canvas.addGLEventListener(mainInstance);

        inputHandler = new InputHandler(mainInstance, camera);
        canvas.addKeyListener(inputHandler);
        canvas.addMouseListener(inputHandler);
        canvas.addMouseMotionListener(inputHandler);
        canvas.addMouseWheelListener(inputHandler);

        frame.getContentPane().add(canvas);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocusInWindow();

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    public void switchPolygonMode() {
        if (wireframeMode)
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
        else
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();
        System.out.println("Init OpenGL: " + gl);

        String vertexShaderPath = "shaders/vertexShader.glsl";
        String fragmentShaderPath = "shaders/fragmentShader.glsl";

        shader = new Shader(gl, vertexShaderPath, fragmentShaderPath);

        try {
            InputStream textureStream = getClass().getClassLoader().getResourceAsStream("images/old_01.png");
            if (textureStream == null) {
                System.err.println("Failed to load texture image: File not found");
                return;
            } else {
                System.out.println("Texture stream loaded successfully");
            }

            texture = TextureIO.newTexture(textureStream, true, "PNG");
            System.out.println("Texture loaded successfully: " + texture);

            gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getTextureObject());

            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            gl.glGenerateMipmap(GL.GL_TEXTURE_2D);

            textureStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cubeTrans = new ModelTransform[cubeCount];
        for (int i = 0; i < cubeCount; i++) {
            cubeTrans[i] = new ModelTransform();
        }

        Random rand = new Random();
        for (int i = 0; i < cubeCount; i++) {
            float scale = (rand.nextInt(6) + 1) / 20.0f;

            cubeTrans[i] = new ModelTransform();

            cubeTrans[i].position = new Vector3f((rand.nextInt(201) - 100) / 50.0f,(rand.nextInt(201) - 100) / 50.0f,(rand.nextInt(201) - 100) / 50.0f);
            cubeTrans[i].rotation = new Vector3f(rand.nextFloat() * 360.0f, rand.nextFloat() * 360.0f, rand.nextFloat() * 360.0f);
            cubeTrans[i].setScale(scale);

            if (cubeTrans[i].position.length() < 0.7f) {
                i--;
            }
        }

        int[] vao = new int[1];
        gl.glGenVertexArrays(1, vao, 0);
        vao_polygon = vao[0];

        int[] vbo = new int[1];
        gl.glGenBuffers(1, vbo, 0);
        vbo_polygon = vbo[0];

        gl.glBindVertexArray(vao_polygon);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo_polygon);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, (long) cube.length * Float.BYTES, FloatBuffer.wrap(cube), GL.GL_STATIC_DRAW);

        int stride = 11 * Float.BYTES;

        //position
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, stride, 0);
        gl.glEnableVertexAttribArray(0);

        //normal
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, stride, 3 * Float.BYTES);
        gl.glEnableVertexAttribArray(1);

        //texture coords
        gl.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, stride, 6 * Float.BYTES);
        gl.glEnableVertexAttribArray(2);

        //color
        gl.glVertexAttribPointer(3, 3, GL.GL_FLOAT, false, stride, 8 * Float.BYTES);
        gl.glEnableVertexAttribArray(3);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glFrontFace(gl.GL_CCW);
        switchPolygonMode();

        //model = new Matrix4f();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        newTime = System.currentTimeMillis();
        deltaTime = (newTime - oldTime) / 1000.0f;
        oldTime = newTime;

        inputHandler.update(deltaTime);

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        shader.use();

        if (switchMode) {
            switchPolygonMode();
            switchMode = false;
        }

        for (int i = 0; i < cubeCount; i++) {
            model = new Matrix4f();
            model.identity();

            model.translate(cubeTrans[i].position);
            cubeTrans[i].rotation.add(0.05f, 0.05f, 0.05f);
            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.x), new Vector3f(1.f, 0.f, 0.f));
            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.y), new Vector3f(0.f, 1.f, 0.f));
            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.z), new Vector3f(0.f, 0.f, 1.f));
            model.scale(cubeTrans[i].scale);

            Matrix4f pvm = camera.getProjectionMatrix().mul(camera.getViewMatrix()).mul(model);

            shader.setMatrix4f("pvm", pvm);
            shader.setBool("wireframeMode", wireframeMode);

            texture.bind(gl);
            gl.glBindVertexArray(vao_polygon);
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, cube.length / 11);
        }

//        position.x = (float)(0.8f * Math.cos(System.currentTimeMillis() * 0.001));
//        position.y = (float)(0.8f * Math.sin(System.currentTimeMillis() * 0.001));
//
//        scale.x = 0.3f;
//        scale.y = 0.3f;
//        scale.z = 0.3f;
//
//        model.identity();
//        model.translate(position);
//        rotation.add(0.01f, 0.01f, 0.01f);
//        model.rotateX(rotation.x).rotateY(rotation.y).rotateZ(rotation.z);
//        model.scale(scale);
//
//        Matrix4f pvm = camera.getProjectionMatrix().mul(camera.getViewMatrix()).mul(model);
//
//        shader.setMatrix4f("pvm", pvm);
//        shader.setBool("wireframeMode", wireframeMode);
//
//        texture.bind(gl);
//        gl.glBindVertexArray(vao_polygon);
//        gl.glDrawArrays(GL.GL_TRIANGLES, 0, cube.length / 11);
//
//        model.identity();
//        model.scale(0.25f);
//
//        Matrix4f pvm1 = camera.getProjectionMatrix().mul(camera.getViewMatrix()).mul(model);
//
//        shader.setMatrix4f("pvm", pvm1);
//        shader.setBool("wireframeMode", wireframeMode);
//
//        texture.bind(gl);
//        gl.glBindVertexArray(vao_polygon);
//        gl.glDrawArrays(GL.GL_TRIANGLES, 0, cube.length / 11);
//
//        model.identity();
//        position.y = (float)(1 + 0.8f * Math.cos(System.currentTimeMillis() * 0.001));
//        position.x = (float)(1 + 0.8f * Math.sin(System.currentTimeMillis() * 0.001));
//        model.translate(position);
//        model.scale(0.2f);
//
//        Matrix4f pvm2 = camera.getProjectionMatrix().mul(camera.getViewMatrix()).mul(model);
//
//        shader.setMatrix4f("pvm", pvm2);
//        shader.setBool("wireframeMode", wireframeMode);
//
//        texture.bind(gl);
//        gl.glBindVertexArray(vao_polygon);
//        gl.glDrawArrays(GL.GL_TRIANGLES, 0, cube.length / 11);
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