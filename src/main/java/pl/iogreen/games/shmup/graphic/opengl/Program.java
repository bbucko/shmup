package pl.iogreen.games.shmup.graphic.opengl;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Program {

    public final int objectId;
    private final Set<Shader> shaders = new HashSet<>();

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
    }

    public void use() {
        glUseProgram(objectId);
    }

    public void close() {
        for (Shader shader : shaders) {
            glDetachShader(objectId, shader.objectId);
        }
        glDeleteProgram(objectId);
    }
}
