package pl.iogreen.games.shmup.graphic.opengl;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Program {

    public final int objectId;
    private final Set<Shader> shaders = new HashSet<>();

    public final static int VERTEX_SIZE = 5;

    public Program(Shader... shaders) {
        //Create shader program
        objectId = glCreateProgram();
        for (Shader shader : shaders) {
            glAttachShader(objectId, shader.objectId);
            this.shaders.add(shader);
        }

        glLinkProgram(objectId);
        int status = glGetProgrami(objectId, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(objectId));
        }
        use();
    }

    public void use() {
        glUseProgram(objectId);
    }

    public void uniform(String name, float value) {
        int uniTime = glGetUniformLocation(objectId, name);
        glUniform1f(uniTime, value);
    }

    public void uniform(String name, Matrix4f model) {
        final int uniModel = glGetUniformLocation(objectId, name);
        final FloatBuffer modelFB = BufferUtils.createFloatBuffer(16);
        glUniformMatrix4fv(uniModel, false, model.get(modelFB));
    }

    public void close() {
        for (Shader shader : shaders) {
            glDetachShader(objectId, shader.objectId);
        }
        glDeleteProgram(objectId);
    }

    public void pointer(String pointerName, int size, int offset) {
        pointer(pointerName, size, VERTEX_SIZE, offset);
    }

    public void pointer(String pointerName, int size, int length, int offset) {
        int positionAttribute = glGetAttribLocation(objectId, pointerName);
        glEnableVertexAttribArray(positionAttribute);
        glVertexAttribPointer(positionAttribute, size, GL_FLOAT, false, floats(length), floats(offset));
    }

    private int floats(int i) {
        return i * java.lang.Float.BYTES;
    }

    public void stopUsing() {
        if (!glIsProgram(objectId)) {
            throw new IllegalArgumentException();
        }
        glUseProgram(0);
    }
}
