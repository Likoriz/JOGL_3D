package org.example.engine;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import org.example.data.Texture;
import org.example.data.Vertex;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import static org.example.Main.gl;

public class Mesh {
    public Vector<Vertex> vertices;
    public Vector<Integer> indices;
    public Vector<Texture> textures;
    public int VAO;

    int VBO, EBO;

    public Mesh(Vector<Vertex> vertices, Vector<Integer> indices, Vector<Texture> textures) {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;

        setupMesh();
    }

    public void draw(Shader shader) {
        int diffuseNr = 1;
        int specularNr = 1;
        int normalNr = 1;
        int heightNr = 1;

        for (int i = 0; i < textures.size(); i++) {
            gl.glActiveTexture(GL4.GL_TEXTURE0 + i);

            Texture texture = textures.get(i);
            String name = texture.type;
            String number = "";

            if (name.equals("texture_diffuse"))
                number = Integer.toString(diffuseNr++);
            else if (name.equals("texture_specular"))
                number = Integer.toString(specularNr++);
            else if (name.equals("texture_normal"))
                number = Integer.toString(normalNr++);
            else if (name.equals("texture_height"))
                number = Integer.toString(heightNr++);

            int location = gl.glGetUniformLocation(shader.getProgramID(), (name + number));
            gl.glUniform1i(location, i);
            gl.glBindTexture(GL4.GL_TEXTURE_2D, texture.id);
        }

        gl.glBindVertexArray(VAO);
        gl.glDrawElements(GL4.GL_TRIANGLES, indices.size(), GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
    }

    void setupMesh() {
        //Vertices buffers
        FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(vertices.size() * 14);

        for (Vertex v : vertices) {
            vertexBuffer.put(v.position.x).put(v.position.y).put(v.position.z);
            vertexBuffer.put(v.normal.x).put(v.normal.y).put(v.normal.z);
            vertexBuffer.put(v.texCoords.x).put(v.texCoords.y);
            vertexBuffer.put(v.tangent.x).put(v.tangent.y).put(v.tangent.z);
            vertexBuffer.put(v.bitangent.x).put(v.bitangent.y).put(v.bitangent.z);
        }
        vertexBuffer.flip();

        //Indices buffers
        IntBuffer indexBuffer = Buffers.newDirectIntBuffer(indices.size());
        for (int i : indices) {
            indexBuffer.put(i);
        }
        indexBuffer.flip();

        int[] vaos = new int[1];
        int[] vbos = new int[1];
        int[] ebos = new int[1];

        gl.glGenVertexArrays(1, vaos, 0);
        gl.glGenBuffers(1, vbos, 0);
        gl.glGenBuffers(1, ebos, 0);

        VAO = vaos[0];
        VBO = vbos[0];
        EBO = ebos[0];

        gl.glBindVertexArray(VAO);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, VBO);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, EBO);
        gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Integer.BYTES, indexBuffer, GL4.GL_STATIC_DRAW);

        int stride = 14 * Float.BYTES;

        //Vertex positions
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, stride, 0);
        gl.glEnableVertexAttribArray(0);

        //Vertex normals
        gl.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, stride, 3 * Float.BYTES);
        gl.glEnableVertexAttribArray(1);

        //Vertex texCoords
        gl.glVertexAttribPointer(2, 2, GL4.GL_FLOAT, false, stride, 6 * Float.BYTES);
        gl.glEnableVertexAttribArray(2);

        //Vertex tangents
        gl.glVertexAttribPointer(3, 3, GL4.GL_FLOAT, false, stride, 8 * Float.BYTES);
        gl.glEnableVertexAttribArray(3);

        //Vertex bitangents
        gl.glVertexAttribPointer(4, 3, GL4.GL_FLOAT, false, stride, 11 * Float.BYTES);
        gl.glEnableVertexAttribArray(4);

        gl.glBindVertexArray(0);
    }
}
