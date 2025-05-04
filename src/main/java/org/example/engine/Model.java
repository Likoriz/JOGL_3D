package org.example.engine;

import com.jogamp.opengl.util.texture.TextureIO;
import org.example.data.Texture;
import org.example.data.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Vector;

import static org.example.Main.gl;
import static org.lwjgl.assimp.Assimp.*;

public class Model {
    public Vector<Texture> texturesLoaded;
    public Vector<Mesh> meshes;
    public String directory;
    public boolean gammaCorrection;
    public boolean isUVFlipped = true;
    public boolean gamma = false;

    public Model(String path) {
        loadModel(path, isUVFlipped);
    }

    public void draw(Shader shader) {
        for (int i = 0; i < meshes.size(); i++)
            meshes.get(i).draw(shader);
    }

    void loadModel(String path, boolean isUVFlipped) {
        int flags = aiProcess_Triangulate | aiProcess_GenSmoothNormals | aiProcess_CalcTangentSpace;

        if (isUVFlipped) {
            flags |= aiProcess_FlipUVs;
        }

        AIScene scene = aiImportFile(path, flags);

        if (scene == null) {
            System.err.println("ERROR ASSIMP: " + aiGetErrorString());
            return;
        }

        if ((scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) != 0) {
            System.err.println("ERROR ASSIMP: Scene incomplete!");
            aiReleaseImport(scene);
            return;
        }

        if (scene.mRootNode() == null) {
            System.err.println("ERROR ASSIMP: Root node is null!");
            aiReleaseImport(scene);
            return;
        }

        int slashIndex = path.lastIndexOf('/');
        if (slashIndex == -1)
            slashIndex = path.lastIndexOf('\\');
        directory = (slashIndex == -1) ? "" : path.substring(0, slashIndex);

        processNode(scene.mRootNode(), scene);
    }

    void processNode(AINode node, AIScene scene) {
        int numMeshes = node.mNumMeshes();
        IntBuffer meshIndices = node.mMeshes();

        for (int i = 0; i < numMeshes; i++) {
            int meshIndex = meshIndices.get(i);
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(meshIndex));
            meshes.add(processMesh(mesh, scene));
        }

        int numChildren = node.mNumChildren();
        PointerBuffer children = node.mChildren();

        for (int i = 0; i < numChildren; i++) {
            AINode child = AINode.create(children.get(i));
            processNode(child, scene);
        }
    }

    Mesh processMesh(AIMesh mesh, AIScene scene) {
        Vector<Vertex> vertices = new Vector<>();
        Vector<Integer> indices = new Vector<>();
        Vector<Texture> textures = new Vector<>();

        for (int i = 0; i < mesh.mNumVertices(); i++) {
            Vertex vertex = new Vertex();
            Vector3f vector;

            //Positions
            AIVector3D position = mesh.mVertices().get(i);
            vector = new Vector3f(position.x(), position.y(), position.z());
            vertex.position = vector;

            //Normals
            if (mesh.mNormals() != null) {
                AIVector3D normal = mesh.mNormals().get(i);
                vector = new Vector3f(normal.x(), normal.y(), normal.z());
                vertex.normal = vector;
            }

            //Texture Coordinates
            if (mesh.mTextureCoords(0) != null) {
                AIVector3D texCoord = mesh.mTextureCoords(0).get(i);
                vertex.texCoords = new Vector2f(texCoord.x(), texCoord.y());

                //Tangent
                if (mesh.mTangents() != null) {
                    AIVector3D tangent = mesh.mTangents().get(i);
                    vertex.tangent = new Vector3f(tangent.x(), tangent.y(), tangent.z());
                }

                //Bitangent
                if (mesh.mBitangents() != null) {
                    AIVector3D bitangent = mesh.mBitangents().get(i);
                    vertex.bitangent = new Vector3f(bitangent.x(), bitangent.y(), bitangent.z());
                }
            } else {
                vertex.texCoords = new Vector2f(0.0f, 0.0f);
            }

            vertices.add(vertex);
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }

        AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));

        //1. Diffuse maps
        Vector<Texture> diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE, "texture_diffuse");
        textures.addAll(diffuseMaps);

        //2. Specular maps
        Vector<Texture> specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR, "texture_specular");
        textures.addAll(specularMaps);

        //3. Normal maps
        Vector<Texture> normalMaps = loadMaterialTextures(material, aiTextureType_HEIGHT, "texture_normal");
        textures.addAll(normalMaps);

        //4. Height maps
        Vector<Texture> heightMaps = loadMaterialTextures(material, aiTextureType_AMBIENT, "texture_height");
        textures.addAll(heightMaps);

        return new Mesh(vertices, indices, textures);
    }

    Vector<Texture> loadMaterialTextures(AIMaterial mat, int type, String typeName) {
        Vector<Texture> textures = new Vector<>();
        int textureCount = aiGetMaterialTextureCount(mat, type);
        AIString path = AIString.calloc();

        for (int i = 0; i < textureCount; i++) {
            aiGetMaterialTexture(mat, type, i, path, null, null, null, null, null, (IntBuffer) null);
            String texPath = path.dataString();

            boolean skip = false;
            for (Texture loadedTex : texturesLoaded) {
                if (loadedTex.path.equals(texPath)) {
                    textures.add(loadedTex);
                    skip = true;
                    break;
                }
            }

            if (!skip) {
                Texture texture = new Texture();
                texture.id = textureFromFile(texPath, this.directory);
                texture.type = typeName;
                texture.path = texPath;
                textures.add(texture);
                texturesLoaded.add(texture);
            }
        }

        path.free();
        return textures;
    }

    int textureFromFile(String path, String directory) {
        String fullPath = directory + File.separator + path;
        try {
            com.jogamp.opengl.util.texture.Texture texture = TextureIO.newTexture(new File(fullPath), false);

            int textureID = texture.getTextureObject();

            gl.glBindTexture(gl.GL_TEXTURE_2D, textureID);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_REPEAT);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_REPEAT);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR_MIPMAP_LINEAR);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
            gl.glGenerateMipmap(gl.GL_TEXTURE_2D);

            return textureID;

        } catch (IOException e) {
            System.err.println("Failed to load texture: " + fullPath);
            e.printStackTrace();
            return 0;
        }
    }
}
