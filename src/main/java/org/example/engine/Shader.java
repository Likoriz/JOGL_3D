package org.example.engine;

import com.jogamp.opengl.*;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Shader {
    private int programID;
    private GL4 gl;

    public Shader(GL4 gl, String vertexShaderPath, String fragmentShaderPath) {
        this.gl = gl;
        String vertexShaderSource = readShaderFromFile(vertexShaderPath);
        String fragmentShaderSource = readShaderFromFile(fragmentShaderPath);

        int vertexShader = compileShader(GL4.GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GL4.GL_FRAGMENT_SHADER, fragmentShaderSource);

        linkProgram(vertexShader, fragmentShader);
    }

    private String readShaderFromFile(String shaderPath) {
        StringBuilder shaderSource = new StringBuilder();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(shaderPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;

            while ((line = reader.readLine()) != null)
                shaderSource.append(line).append("\n");

        } catch (IOException | NullPointerException e) {
            System.err.println("Error reading shader file: " + shaderPath);
            e.printStackTrace();
        }
        return shaderSource.toString();
    }


    private int compileShader(int type, String source) {
        int shader = gl.glCreateShader(type);
        gl.glShaderSource(shader,1 , new String[]{source}, null);
        gl.glCompileShader(shader);

        int[] success = new int[1];
        gl.glGetShaderiv(shader, GL4.GL_COMPILE_STATUS, success, 0);

        if (success[0] == 0) {
            byte[] log = new byte[512];
            gl.glGetShaderInfoLog(shader, 512, null, 0, log, 0);
            System.out.println("Shader compilation error: " + new String(log));
            return 0;
        }

        return shader;
    }

    private void linkProgram(int vertexShader, int fragmentShader) {
        programID = gl.glCreateProgram();
        gl.glAttachShader(programID, vertexShader);
        gl.glAttachShader(programID, fragmentShader);
        gl.glLinkProgram(programID);

        int[] success = new int[1];
        gl.glGetProgramiv(programID, GL4.GL_LINK_STATUS, success, 0);

        if (success[0] == 0) {
            byte[] log = new byte[512];
            gl.glGetProgramInfoLog(programID, 512, null, 0, log, 0);
            System.out.println("Program linking error: " + new String(log));
        }

        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);
    }

    public void use() {
        gl.glUseProgram(programID);
    }

    public int getProgramID() {
        return programID;
    }

    private void setBool(String name, boolean value) {
        gl.glUniform1i(gl.glGetUniformLocation(programID, name), Boolean.compare(value, false));
    }

    private void setInt(String name, int value) {
        gl.glUniform1i(gl.glGetUniformLocation(programID, name), value);
    }

    private void setFloat(String name, float value) {
        gl.glUniform1f(gl.glGetUniformLocation(programID, name), value);
    }

    public void setFloatVec(String name, float[] vec, int vecSize) {
         switch(vecSize) {
             case 1:
                 gl.glUniform1f(gl.glGetUniformLocation(programID, name), vec[0]);
                 break;
             case 2:
                 gl.glUniform2f(gl.glGetUniformLocation(programID, name), vec[0], vec[1]);
                 break;
             case 3:
                 gl.glUniform3f(gl.glGetUniformLocation(programID, name), vec[0], vec[1], vec[2]);
                 break;
             case 4:
                 gl.glUniform4f(gl.glGetUniformLocation(programID, name), vec[0], vec[1], vec[2], vec[3]);
                 break;
             default:
                 System.out.println("Shader failure: No such uniform vector size!");
         }
    }

    public void setMatrix4f(String name, Matrix4f model) {
        FloatBuffer modelBuffer = ByteBuffer.allocateDirect(16 * Float.BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        model.get(modelBuffer);
        modelBuffer.flip();

        int transformLoc = gl.glGetUniformLocation(programID, name);
        gl.glUniformMatrix4fv(transformLoc, 1, false, modelBuffer);
    }
}