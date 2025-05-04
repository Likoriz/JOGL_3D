package org.example;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import org.example.data.Material;
import org.example.data.ModelTransform;
import org.example.engine.*;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Vector;

public class Main implements GLEventListener {
    public static GL4 gl;

    private Shader shader;
    private Shader lightShader;

    private static InputHandler inputHandler;

    public Color backgroundColor = Color.BLACK;

    //public int lightType = 2;

    private static Camera camera = new Camera(new Vector3f(0.0f, 0.0f, -2.0f));

    private long oldTime = System.currentTimeMillis();
    private long newTime;
    private float deltaTime;

    public static boolean wireframeMode = false;
    public boolean switchMode = false;

    public static void switchPolygonMode() {
        if (wireframeMode)
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
        else
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
    }

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
    Material[] cubeMaterial;
    int[] cubeMat;

    private int vbo_polygon;
    private int vao_polygon;

    private Matrix4f model = new Matrix4f();

    private Texture texture;

    //DirectionalLight light1;
    //PointLight light2;
    //Light light3;

    public Light flashLight;
    public Light redLamp;
    public Light blueLamp;
    public Light sunLight;
    ModelTransform lightTrans;
    Vector<Light> lights;
    int totalLights = 4;
    int activeLights = 0;

    public static void main(String[] args) throws AWTException {
        JFrame frame = new JFrame("JOGL 3D Window");

        GLProfile profile = GLProfile.get(GLProfile.GL4);

        if (profile == null) {
            System.out.println("Error: Couldn't find available profile!");
            return;
        }

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

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();
        System.out.println("Init OpenGL: " + gl);

        String vertexShaderPath = "shaders/vertexShader.vert";
        String fragmentShaderPath = "shaders/fragmentShader.frag";

        shader = new Shader(gl, vertexShaderPath, fragmentShaderPath);

        String lightVertexShaderPath = "shaders/light.vert";
        String lightFragmentShaderPath = "shaders/light.frag";

        lightShader = new Shader(gl, lightVertexShaderPath, lightFragmentShaderPath);

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

//        light1 = new DirectionalLight();
//        light1.direction = new Vector3f(-1.0f, -1.0f, -1.0f);
//        light1.ambient = new Vector3f(0.3f, 0.3f, 0.3f);
//        light1.diffuse = new Vector3f(0.6f, 0.85f, 1.0f);
//        light1.specular = new Vector3f(0.06f, 0.085f, 0.1f);
//
//        light2 = new PointLight();
//        light2.position = new Vector3f(0.0f, 0.0f, 0.0f);
//        light2.ambient = new Vector3f(0.2f, 0.2f, 0.2f);
//        light2.diffuse = new Vector3f(1.0f, 1.0f, 1.0f);
//        light2.specular = new Vector3f(3.0f, 3.0f, 3.0f);
//        light2.constant = 1.0f;
//        light2.linear = 0.14f;
//        light2.quadratic = 0.12f;
//
//        light3 = new Light();
//        light3.position = new Vector3f(-3.0f, -3.0f, -3.0f);
//        light3.direction = new Vector3f(1.0f, 1.0f, 1.0f);
//        light3.cutOff = (float) Math.toRadians(10.0f);
//        light3.ambient = new Vector3f(0.2f, 0.2f, 0.2f);
//        light3.diffuse = new Vector3f(1.0f, 1.0f, 1.0f);
//        light3.specular = new Vector3f(3.0f, 3.0f, 3.0f);
//        light3.constant = 0.9f;
//        light3.linear = 0.1f;
//        light3.quadratic = 0.09f;

        cubeMaterial = new Material[3];
        for (int i = 0; i < 3; i++)
            cubeMaterial[i] = new Material();
        //Pearl
        cubeMaterial[0].ambient = new Vector3f(0.25f, 0.20725f, 0.20725f);
        cubeMaterial[0].diffuse = new Vector3f(1f, 0.829f, 0.829f);
        cubeMaterial[0].specular = new Vector3f(0.296648f, 0.296648f, 0.296648f);
        cubeMaterial[0].shininess = 12.f;
        //Chrome
        cubeMaterial[1].ambient = new Vector3f(0.25f, 0.25f, 0.25f);
        cubeMaterial[1].diffuse = new Vector3f(0.4f, 0.4f, 0.4f);
        cubeMaterial[1].specular = new Vector3f(0.774597f, 0.774597f, 0.774597f);
        cubeMaterial[1].shininess = 77.f;
        //Ruby
        cubeMaterial[2].ambient = new Vector3f(0.1745f, 0.01175f, 0.01175f);
        cubeMaterial[2].diffuse = new Vector3f(0.61424f, 0.04136f, 0.04136f);
        cubeMaterial[2].specular = new Vector3f(0.727811f, 0.626959f, 0.626959f);
        cubeMaterial[2].shininess = 77.f;

        cubeMat = new int[cubeCount];

        cubeTrans = new ModelTransform[cubeCount];
        Random rand = new Random();
        for (int i = 0; i < cubeCount; i++) {
            float scale = (rand.nextInt(6) + 1) / 20.0f;

            cubeTrans[i] = new ModelTransform();

            cubeTrans[i].position = new Vector3f((rand.nextInt(201) - 100) / 50.0f,(rand.nextInt(201) - 100) / 50.0f,(rand.nextInt(201) - 100) / 50.0f);
            cubeTrans[i].rotation = new Vector3f(rand.nextFloat() * 360.0f, rand.nextFloat() * 360.0f, rand.nextFloat() * 360.0f);
            cubeTrans[i].setScale(scale);

            cubeMat[i] = rand.nextInt(3);

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

        //Position
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, stride, 0);
        gl.glEnableVertexAttribArray(0);

        //Normal
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, stride, 3 * Float.BYTES);
        gl.glEnableVertexAttribArray(1);

        //Texture coords
        gl.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, stride, 6 * Float.BYTES);
        gl.glEnableVertexAttribArray(2);

        //Color
        gl.glVertexAttribPointer(3, 3, GL.GL_FLOAT, false, stride, 8 * Float.BYTES);
        gl.glEnableVertexAttribArray(3);

        //LIGHTS INITIALIZATION
        lightTrans = new ModelTransform();
        float scale = 0.1f;

        lightTrans.position = new Vector3f(0.0f, 0.0f, 0.0f);
        lightTrans.rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        lightTrans.setScale(scale);

        lights = new Vector<>();

        redLamp = new Light("LampRed", true);
        redLamp.initLikePointLight(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(1.0f, 0.2f, 0.2f), new Vector3f(1.0f, 0.2f, 0.2f), 1.0f, 0.1f, 0.09f);
        lights.add(redLamp);

        blueLamp = new Light("LampBlue", true);
        blueLamp.initLikePointLight(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(0.2f, 0.2f, 1.0f), new Vector3f(1.0f, 0.2f, 1.0f), 1.0f, 0.1f, 0.09f);
        lights.add(blueLamp);

        sunLight = new Light("Sun", true);
        sunLight.initLikeDirectionalLight(new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.0f, 0.0f, 0.0f));
        lights.add(sunLight);

        flashLight = new Light("FlashLight", true);
        flashLight.initLikeSpotLight(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), (float) Math.toRadians(10.f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.7f, 0.7f, 0.6f), new Vector3f(0.8f, 0.8f, 0.6f), 1.0f, 0.1f, 0.09f);
        lights.add(flashLight);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glFrontFace(gl.GL_CCW);
        switchPolygonMode();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        model.identity();

        newTime = System.currentTimeMillis();
        deltaTime = (newTime - oldTime) / 1000.0f;
        oldTime = newTime;

        inputHandler.update(deltaTime);

        flashLight.position = new Vector3f(camera.position).sub(new Vector3f(camera.up).mul(0.3f));
        flashLight.direction = camera.front;

        redLamp.position.x = 4.0f * (float)(0.8f * Math.sin(System.currentTimeMillis() * 0.001));;
        redLamp.position.z = 0.2f;
        redLamp.position.y = 4.0f * (float)(0.8f * Math.cos(System.currentTimeMillis() * 0.001));;

        blueLamp.position.x = 0.2f;
        blueLamp.position.z = 4.0f * (float)(0.8f * Math.cos(System.currentTimeMillis() * 0.001));
        blueLamp.position.y = 4.0f * (float)(0.8f * Math.sin(System.currentTimeMillis() * 0.001));

        gl.glClearColor(backgroundColor.getRed() / 255f, backgroundColor.getGreen() / 255f, backgroundColor.getBlue() / 255f, backgroundColor.getAlpha() / 255f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (switchMode) {
            switchPolygonMode();
            switchMode = false;
        }

        //LIGHT
        //lightShader.use();

        //light1.position.x = 4.0f * (float)(0.8f * Math.cos(System.currentTimeMillis() * 0.001));
        //light1.position.z = 4.0f * (float)(0.8f * Math.sin(System.currentTimeMillis() * 0.001));
        //lightTrans.position = light2.position;

        //light3.position = new Vector3f(camera.position).sub(new Vector3f(camera.up).mul(0.3f));
        //light3.direction = camera.front;

        //model.translate(lightTrans.position);
        //model.scale(lightTrans.scale);

        //Matrix4f lp = camera.getProjectionMatrix();
        //Matrix4f lv = camera.getViewMatrix();
        //Matrix4f lpv = lp.mul(lv);

        //lightShader.setMatrix4f("pv", lpv);
        //lightShader.setMatrix4f("model", model);

//        switch (lightType) {
//            case 1:
//                lightShader.setVec3("lightColor", light1.specular);
//                break;
//            case 2:
//                lightShader.setVec3("lightColor", light2.specular);
//                break;
//            case 3:
//                lightShader.setVec3("lightColor", light3.specular);
//                break;
//        }

        Matrix4f p = camera.getProjectionMatrix();
        Matrix4f v = camera.getViewMatrix();
        Matrix4f pv = p.mul(v);

        shader.use();
        shader.setMatrix4f("pv", pv);
        shader.setBool("wireframeMode", wireframeMode);
        shader.setVec3("viewPos", camera.position);

        int activeLights = 0;
        for (int i = 0; i < totalLights; i++)
            activeLights += lights.get(i).putInShader(shader, activeLights);

        shader.setInt("lights_count", activeLights);

//        texture.bind(gl);
//        gl.glBindVertexArray(vao_polygon);
//        gl.glDrawArrays(GL.GL_TRIANGLES, 0, cube.length / 11);
//
//        shader.use();
//
//        shader.setInt("light.type", lightType);
//
//        shader.setBool("wireframeMode", wireframeMode);
//        shader.setVec3("viewPos", camera.position);
//
//        Matrix4f p = camera.getProjectionMatrix();
//        Matrix4f v = camera.getViewMatrix();
//        Matrix4f pv = p.mul(v);
//
//        shader.setMatrix4f("pv", pv);
//
//        switch (lightType) {
//            case 1: //DIRECTIONAL LIGHT
//                shader.setVec3("light.position", light1.direction);
//
//                shader.setVec3("light.ambient", light1.ambient);
//                shader.setVec3("light.diffuse", light1.diffuse);
//                shader.setVec3("light.specular", light1.specular);
//
//                break;
//            case 2: //POINT LIGHT
//                shader.setVec3("light.position", light2.position);
//
//                shader.setVec3("light.ambient", light2.ambient);
//                shader.setVec3("light.diffuse", light2.diffuse);
//                shader.setVec3("light.specular", light2.specular);
//
//                shader.setFloat("light.constant", light2.constant);
//                shader.setFloat("light.linear", light2.linear);
//                shader.setFloat("light.quadratic", light2.quadratic);
//
//                break;
//            case 3:
//                shader.setVec3("light.position", light3.position);
//                shader.setVec3("light.direction", light3.direction);
//                shader.setFloat("light.cutOff", light3.cutOff);
//
//                shader.setVec3("light.ambient", light3.ambient);
//                shader.setVec3("light.diffuse", light3.diffuse);
//                shader.setVec3("light.specular", light3.specular);
//
//                shader.setFloat("light.constant", light3.constant);
//                shader.setFloat("light.linear", light3.linear);
//                shader.setFloat("light.quadratic", light3.quadratic);
//
//                break;
//        }

        //CUBES
        for (int i = 0; i < cubeCount; i++) {
            model.identity();

            model.translate(cubeTrans[i].position);
            cubeTrans[i].rotation.add(0.05f, 0.05f, 0.05f);
            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.x), new Vector3f(1.f, 0.f, 0.f));
            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.y), new Vector3f(0.f, 1.f, 0.f));
            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.z), new Vector3f(0.f, 0.f, 1.f));
            model.scale(cubeTrans[i].scale);

            shader.setMatrix4f("model", model);

            shader.setVec3("material.ambient", cubeMaterial[cubeMat[i]].ambient);
            shader.setVec3("material.diffuse", cubeMaterial[cubeMat[i]].diffuse);
            shader.setVec3("material.specular", cubeMaterial[cubeMat[i]].specular);
            shader.setFloat("material.shininess", cubeMaterial[cubeMat[i]].shininess);

            texture.bind(gl);
            gl.glBindVertexArray(vao_polygon);
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, cube.length / 11);
        }

        //DRAWING LAMPS
        lightShader.use();
        lightShader.setMatrix4f("pv", pv);
        gl.glBindVertexArray(vao_polygon);

        //Red lamp
        lightTrans.position = redLamp.position;
        model.identity();
        model.translate(lightTrans.position);
        model.scale(lightTrans.scale);
        lightShader.setMatrix4f("model", model);
        lightShader.setVec3("lightColor", new Vector3f(1.0f, 0.2f, 0.2f));
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, 36);

        // Blue Lamp
        lightTrans.position = blueLamp.position;
        model.identity();
        model.translate(lightTrans.position);
        model.scale(lightTrans.scale);
        lightShader.setMatrix4f("model", model);
        lightShader.setVec3("lightColor", new Vector3f(0.2f, 0.2f, 1.0f));
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, 36);
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
//TODO: Перевести источники света на массив