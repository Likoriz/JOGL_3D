package org.example;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
//import com.jogamp.opengl.util.texture.Texture;
import org.example.data.ModelTransform;
import org.example.engine.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

public class Main implements GLEventListener {
    public static GL4 gl;

    static int windowWidth = 1920;
    static int windowHeight = 1080;

    private Shader shader;
    private Shader lightShader;
    //private Shader backpackShader;
    private Shader journeyShader;
    private Shader quadShader;
    private Shader bloomShader;
    private Shader blurShader;
    private static Shader pulseShader;

    private static InputHandler inputHandler;

    public Color backgroundColor = Color.BLACK;

    //private static final Camera camera = new Camera(new Vector3f(0.0f, 0.0f, -2.0f));
    private static final Camera camera = new Camera(new Vector3f(0.0f, 10.0f, -25.0f));

    private long oldTime = System.currentTimeMillis();
    private long newTime;
    private float deltaTime;

    public static boolean wireframeMode = false;
    public boolean switchMode = false;

    public static void switchPolygonMode() {
        if (wireframeMode)
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
        else
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
    }

    static Vector2f waveOrigin = new Vector2f();
    static float waveRadius = -1.0f;
    static boolean spreadingColor = false;
    static boolean waveActive = false;
    static boolean waveStarted = false;

    public static void normalizeOrigin(int mouseX, int mouseY) {
        float normalizedX = (float)mouseX / (float)windowWidth;
        float normalizedY = 1.0f - ((float)mouseY / (float)windowHeight);

        waveOrigin.set(normalizedX, normalizedY);
    }

    public static void triggerPulse(int mouseX, int mouseY) {
        waveStarted = true;
        waveRadius = 0.0f;
        spreadingColor = !spreadingColor;
        waveActive = true;

        normalizeOrigin(mouseX, mouseY);
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

    //private final int cubeCount = 200;
    //ModelTransform[] cubeTrans;
    //Material[] cubeMaterial;
    //int[] cubeMat;

    private int vbo_polygon;
    private int vao_polygon;

    private final Matrix4f model = new Matrix4f();
    //private Model backpack;
    private Model character;

    //private Texture texture;

    public Light flashLight;
    public Light redLamp;
    public Light blueLamp;
    public Light sunLight;
    ModelTransform lightTrans;
    Vector<Light> lights;
    int totalLights = 4;

    private int hdrFBO;
    private final int[] colorBuffers = new int[2];
    private int rboDepth;

    private int quadVAO = 0;
    private int quadVBO;
    private final float[] quadVertices = {
            // positions         // texCoords
            -1.0f,  1.0f, 0.0f,   0.0f, 1.0f, // top-left
            -1.0f, -1.0f, 0.0f,   0.0f, 0.0f, // bottom-left
            1.0f, -1.0f, 0.0f,   1.0f, 0.0f, // bottom-right

            -1.0f,  1.0f, 0.0f,   0.0f, 1.0f, // top-left
            1.0f, -1.0f, 0.0f,   1.0f, 0.0f, // bottom-right
            1.0f,  1.0f, 0.0f,   1.0f, 1.0f  // top-right
    };

    private final int[] pingpongFBO = new int[2];
    private final int[] pingpongColorBuffers = new int[2];

    private int pulseFBO;
    private int pulseColorBuffer;

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
        frame.setSize(1920, 1080);
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

//        String backpackVertexShaderPath = "shaders/backpack.vert";
//        String backpackFragmentShaderPath = "shaders/backpack.frag";
//
//        backpackShader = new Shader(gl, backpackVertexShaderPath, backpackFragmentShaderPath);

        String journeyVertexShaderPath = "shaders/journey.vert";
        String journeyFragmentShaderPath = "shaders/journey.frag";

        journeyShader = new Shader(gl, journeyVertexShaderPath, journeyFragmentShaderPath);

        String quadVertexShaderPath = "shaders/quad.vert";
        String quadFragmentShaderPath = "shaders/quad.frag";

        quadShader = new Shader(gl, quadVertexShaderPath, quadFragmentShaderPath);

        String blurVertexShaderPath = "shaders/quad.vert";
        String blurFragmentShaderPath = "shaders/blur.frag";

        blurShader = new Shader(gl, blurVertexShaderPath, blurFragmentShaderPath);

        String bloomVertexShaderPath = "shaders/quad.vert";
        String bloomFragmentShaderPath = "shaders/bloom.frag";

        bloomShader = new Shader(gl, bloomVertexShaderPath, bloomFragmentShaderPath);

        String pulseVertexShaderPath = "shaders/quad.vert";
        String pulseFragmentShaderPath = "shaders/pulse.frag";

        pulseShader = new Shader(gl, pulseVertexShaderPath, pulseFragmentShaderPath);

//        try {
//            InputStream textureStream = getClass().getClassLoader().getResourceAsStream("images/old_01.png");
//            if (textureStream == null) {
//                System.err.println("Failed to load texture image: File not found");
//                return;
//            } else {
//                System.out.println("Texture stream loaded successfully");
//            }
//
//            texture = TextureIO.newTexture(textureStream, true, "PNG");
//            System.out.println("Texture loaded successfully: " + texture);
//
//            gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getTextureObject());
//
//            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
//            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
//            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
//            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
//
//            gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
//
//            textureStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        cubeMaterial = new Material[3];
//        for (int i = 0; i < 3; i++)
//            cubeMaterial[i] = new Material();
//        //Pearl
//        cubeMaterial[0].ambient = new Vector3f(0.25f, 0.20725f, 0.20725f);
//        cubeMaterial[0].diffuse = new Vector3f(1f, 0.829f, 0.829f);
//        cubeMaterial[0].specular = new Vector3f(0.296648f, 0.296648f, 0.296648f);
//        cubeMaterial[0].shininess = 12.f;
//        //Chrome
//        cubeMaterial[1].ambient = new Vector3f(0.25f, 0.25f, 0.25f);
//        cubeMaterial[1].diffuse = new Vector3f(0.4f, 0.4f, 0.4f);
//        cubeMaterial[1].specular = new Vector3f(0.774597f, 0.774597f, 0.774597f);
//        cubeMaterial[1].shininess = 77.f;
//        //Ruby
//        cubeMaterial[2].ambient = new Vector3f(0.1745f, 0.01175f, 0.01175f);
//        cubeMaterial[2].diffuse = new Vector3f(0.61424f, 0.04136f, 0.04136f);
//        cubeMaterial[2].specular = new Vector3f(0.727811f, 0.626959f, 0.626959f);
//        cubeMaterial[2].shininess = 77.f;
//
//        cubeMat = new int[cubeCount];
//
//        cubeTrans = new ModelTransform[cubeCount];
//        Random rand = new Random();
//        for (int i = 0; i < cubeCount; i++) {
//            float scale = (rand.nextInt(6) + 1) / 20.0f;
//
//            cubeTrans[i] = new ModelTransform();
//
//            cubeTrans[i].position = new Vector3f((rand.nextInt(201) - 100) / 50.0f,(rand.nextInt(201) - 100) / 50.0f,(rand.nextInt(201) - 100) / 50.0f);
//            cubeTrans[i].rotation = new Vector3f(rand.nextFloat() * 360.0f, rand.nextFloat() * 360.0f, rand.nextFloat() * 360.0f);
//            cubeTrans[i].setScale(scale);
//
//            cubeMat[i] = rand.nextInt(3);
//
//            if (cubeTrans[i].position.length() < 0.7f) {
//                i--;
//            }
//        }

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

        //backpack = new Model("models/backpack/backpack.obj", false);
        character = new Model("models/journey/Jorney_clothes_v3.obj", true);

        //LIGHTS
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

        //FRAME BUFFER
        int [] temp = new int[1];
        gl.glGenFramebuffers(1, temp, 0);
        hdrFBO = temp[0];
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, hdrFBO);

        //COLOR BUFFER
        gl.glGenTextures(2, colorBuffers, 0);
        for (int i = 0; i < 2; i++) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, colorBuffers[i]);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA16F, 1920, 1080, 0, GL.GL_RGBA, GL.GL_FLOAT, null);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0 + i, GL.GL_TEXTURE_2D, colorBuffers[i], 0);
        }

        //RENDER BUFFER
        gl.glGenRenderbuffers(1, temp, 0);
        rboDepth = temp[0];
        gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, rboDepth);
        gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL2ES2.GL_DEPTH_COMPONENT, 1920, 1080);
        gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL. GL_RENDERBUFFER, rboDepth);

        IntBuffer attachments = IntBuffer.wrap(new int[] {
                GL4.GL_COLOR_ATTACHMENT0, GL4.GL_COLOR_ATTACHMENT1
        });
        gl.glDrawBuffers(2, attachments);

        if (gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER) != GL.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("ERROR::FRAME BUFFER:: Frame buffer is not complete!");
        }

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);

        //BLUR FRAME BUFFERS
        gl.glGenFramebuffers(2, pingpongFBO, 0);
        gl.glGenTextures(2, pingpongColorBuffers, 0);

        for (int i = 0; i < 2; i++) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, pingpongColorBuffers[i]);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA16F, windowWidth, windowHeight, 0, GL.GL_RGBA, GL.GL_FLOAT, null);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, pingpongFBO[i]);
            gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, pingpongColorBuffers[i], 0);

            if (gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER) != GL.GL_FRAMEBUFFER_COMPLETE) {
                System.err.println("ERROR::BLUR FRAME BUFFER:: Blur Frame buffer is not complete!");
            }
        }

        //PULSE FRAME BUFFER
        int[] fbo = new int[1];
        gl.glGenFramebuffers(1, fbo, 0);
        pulseFBO = fbo[0];
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, pulseFBO);

        int[] tex = new int[1];
        gl.glGenTextures(1, tex, 0);
        pulseColorBuffer = tex[0];
        gl.glBindTexture(GL.GL_TEXTURE_2D, pulseColorBuffer);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA16F, windowWidth, windowHeight, 0, GL.GL_RGBA, GL.GL_FLOAT, null);

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, pulseColorBuffer, 0);

        if (gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER) != GL.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("ERROR::PULSE FRAME BUFFER:: Pulse Frame buffer is not complete!");
        }

        gl.glEnable(GL.GL_DEPTH_TEST);
        //gl.glEnable(GL.GL_CULL_FACE);
        gl.glFrontFace(GL.GL_CCW);
        switchPolygonMode();
    }

    public void renderQuad() {
        gl.glViewport(0, 0, 1920, 1080);

        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        //SCREEN QUAD
        if (quadVAO == 0) {
            int[] vaoQuad = new int[1];
            gl.glGenVertexArrays(1, vaoQuad, 0);
            quadVAO = vaoQuad[0];

            int[] vboQuad = new int[1];
            gl.glGenBuffers(1, vboQuad, 0);
            quadVBO = vboQuad[0];

            gl.glBindVertexArray(quadVAO);

            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, quadVBO);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, (long) quadVertices.length * Float.BYTES, FloatBuffer.wrap(quadVertices), GL.GL_STATIC_DRAW);

            int strideQuad = 5 * Float.BYTES;

            //Position
            gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, strideQuad, 0);
            gl.glEnableVertexAttribArray(0);

            //Texture Coords
            gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, strideQuad, 3 * Float.BYTES);
            gl.glEnableVertexAttribArray(1);
        }

        gl.glBindVertexArray(quadVAO);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 6);

        gl.glBindVertexArray(0);

        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    public void renderModel() {
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

//        for (int i = 0; i < cubeCount; i++) {
//            model.identity();
//
//            model.translate(cubeTrans[i].position);
//            cubeTrans[i].rotation.add(0.05f, 0.05f, 0.05f);
//            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.x), new Vector3f(1.f, 0.f, 0.f));
//            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.y), new Vector3f(0.f, 1.f, 0.f));
//            model.rotate((float) Math.toRadians(cubeTrans[i].rotation.z), new Vector3f(0.f, 0.f, 1.f));
//            model.scale(cubeTrans[i].scale);
//
//            shader.setMatrix4f("model", model);
//
//            shader.setVec3("material.ambient", cubeMaterial[cubeMat[i]].ambient);
//            shader.setVec3("material.diffuse", cubeMaterial[cubeMat[i]].diffuse);
//            shader.setVec3("material.specular", cubeMaterial[cubeMat[i]].specular);
//            shader.setFloat("material.shininess", cubeMaterial[cubeMat[i]].shininess);
//
//            texture.bind(gl);
//            gl.glBindVertexArray(vao_polygon);
//            gl.glDrawArrays(GL.GL_TRIANGLES, 0, cube.length / 11);
//        }

        //DRAWING LAMPS


        //DRAWING BACKPACK
//        model.identity();
//        model.translate(new Vector3f(0.0f, 0.0f, 0.0f));
//        model.scale(new Vector3f(0.1f, 0.1f, 0.1f));
//        backpackShader.use();
//        backpackShader.setMatrix4f("pv", pv);
//        backpackShader.setMatrix4f("model", model);
//        backpackShader.setFloat("shininess", 64.0f);
//        backpackShader.setVec3("viewPos", camera.position);
//
//        activeLights = 0;
//        for (Light light : lights)
//            activeLights += light.putInShader(backpackShader, activeLights);
//
//        backpackShader.setInt("lights_count", activeLights);

        //backpack.draw(backpackShader);
        
        renderLights(pv);

        model.identity();
        model.translate(new Vector3f(0.0f, 0.0f, 0.0f));
        model.scale(new Vector3f(0.1f, 0.1f, 0.1f));
        journeyShader.use();
        journeyShader.setMatrix4f("pv", pv);
        journeyShader.setMatrix4f("model", model);
        journeyShader.setFloat("shininess", 64.0f);
        journeyShader.setVec3("viewPos", camera.position);

        activeLights = 0;
        for (Light light : lights)
            activeLights += light.putInShader(journeyShader, activeLights);

        journeyShader.setInt("lights_count", activeLights);

        character.draw(journeyShader);
    }

    public void renderLights(Matrix4f pv) {
        flashLight.position = new Vector3f(camera.position).sub(new Vector3f(camera.up).mul(0.3f));
        flashLight.direction = camera.front;

        redLamp.position.x = 4.0f * (float)(0.8f * Math.sin(System.currentTimeMillis() * 0.001));;
        redLamp.position.z = 0.2f;
        redLamp.position.y = 4.0f * (float)(0.8f * Math.cos(System.currentTimeMillis() * 0.001));;

        blueLamp.position.x = 0.2f;
        blueLamp.position.z = 4.0f * (float)(0.8f * Math.cos(System.currentTimeMillis() * 0.001));
        blueLamp.position.y = 4.0f * (float)(0.8f * Math.sin(System.currentTimeMillis() * 0.001));

        lightShader.use();
        lightShader.setMatrix4f("pv", pv);
        gl.glBindVertexArray(vao_polygon);

        //Red lamp
        lightTrans.position = redLamp.position;
        model.identity();
        model.translate(lightTrans.position);
        model.scale(lightTrans.scale);
        lightShader.setMatrix4f("model", model);
        lightShader.setVec3("lightColor", new Vector3f(10.0f, 0.0f, 0.0f));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);

        // Blue Lamp
        lightTrans.position = blueLamp.position;
        model.identity();
        model.translate(lightTrans.position);
        model.scale(lightTrans.scale);
        lightShader.setMatrix4f("model", model);
        lightShader.setVec3("lightColor", new Vector3f(0.0f, 0.0f, 15.0f));
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        newTime = System.currentTimeMillis();
        deltaTime = (newTime - oldTime) / 1000.0f;
        oldTime = newTime;

        inputHandler.update(deltaTime);

        if (switchMode) {
            switchPolygonMode();
            switchMode = false;
        }

        if (!wireframeMode) {
            //ORIGINAL
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, hdrFBO);
            gl.glClearColor(
                    backgroundColor.getRed() / 255f,
                    backgroundColor.getGreen() / 255f,
                    backgroundColor.getBlue() / 255f,
                    backgroundColor.getAlpha() / 255f
            );
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            renderModel();

            //BLUR
            boolean horizontal = true, firstIteration = true;
            int amount = 10;

            blurShader.use();
            blurShader.setInt("image", 0);

            for (int i = 0; i < amount; i++) {
                gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, pingpongFBO[horizontal ? 1 : 0]);
                blurShader.setInt("horizontal", horizontal ? 1 : 0);
                gl.glBindTexture(GL.GL_TEXTURE_2D, firstIteration ? colorBuffers[1] : pingpongColorBuffers[!horizontal ? 1 : 0]);
                renderQuad();
                horizontal = !horizontal;
                if (firstIteration)
                    firstIteration = false;
            }

            //BLOOM
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, pulseFBO);
            bloomShader.use();
            bloomShader.setInt("scene", 0);
            bloomShader.setInt("bloomBlur", 1);
            bloomShader.setInt("bloom", 1);
            bloomShader.setFloat("bloomStrength", 0.5f);
            bloomShader.setFloat("exposure", 1.0f);

            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, colorBuffers[0]);
            gl.glActiveTexture(GL.GL_TEXTURE1);
            gl.glBindTexture(GL.GL_TEXTURE_2D, pingpongColorBuffers[!horizontal ? 1 : 0]);

            renderQuad();

            //MONOCHROME
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
            gl.glClearColor(
                    backgroundColor.getRed() / 255f,
                    backgroundColor.getGreen() / 255f,
                    backgroundColor.getBlue() / 255f,
                    backgroundColor.getAlpha() / 255f
            );
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            pulseShader.use();

            if (waveActive) {
                waveRadius += deltaTime * 0.5f;
                if (waveRadius > 1.5f) {
                    waveActive = false;
                }
            }

            normalizeOrigin((int) inputHandler.lastMouseX, (int) inputHandler.lastMouseY);
            pulseShader.setVec2("waveOrigin", waveOrigin);
            pulseShader.setFloat("waveRadius", waveRadius);
            pulseShader.setBool("spreadingColor", spreadingColor);
            pulseShader.setFloat("edgeSoftness", 0.05f);
            pulseShader.setInt("scene", 0);

            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, pulseColorBuffer);
            renderQuad();
        }
        else {
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
            gl.glClearColor(
                    backgroundColor.getRed() / 255f,
                    backgroundColor.getGreen() / 255f,
                    backgroundColor.getBlue() / 255f,
                    backgroundColor.getAlpha() / 255f
            );
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            renderModel();
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Dispose OpenGL");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        gl.glViewport(x, y, width, height);

        windowWidth = width;
        windowHeight = height;
    }
}