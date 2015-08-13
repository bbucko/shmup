package pl.iogreen.games.shmup.game;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL15;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.iogreen.games.shmup.game.utils.ShaderLoader;
import pl.iogreen.games.shmup.game.utils.Timer;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Game extends GLFWKeyCallback {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);
    private final static int FLOAT_SIZE = 4;

    private final Timer timer;

    public boolean stillAlive = true;

    public Game(Timer timer) {
        this.timer = timer;
    }

    @Override
    public void invoke(long window, int key, int scanCode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            stillAlive = false;
        }
    }

    public void update(long tick) {
        timer.updateUPSCount();
    }

    public void render(float alpha) {
        timer.updateFPSCount();
        Set<Integer> buffers = new HashSet<>();
        try {
            //Create VAO
            int vao = glGenVertexArrays();
            buffers.add(vao);
            glBindVertexArray(vao);

            //Create VBO
            FloatBuffer vertices = (FloatBuffer) BufferUtils.createFloatBuffer(5 * 6)
                    .put(
                            new float[]{
                                    0.0f, 0.0f, 0f, 1f, 1f, // Vertex 1 (X, Y)
                                    0.5f, 0.0f, 1f, 0f, 1f, // Vertex 2 (X, Y)
                                    0.25f, 0.5f, 1f, 1f, 0f, // Vertex 3 (X, Y)
                            })
                    .put(
                            new float[]{
                                    0.0f, 0.0f, 1f, 1f, 0f, // Vertex 1 (X, Y)
                                    -0.5f, 0.0f, 1f, 0f, 1f,// Vertex 2 (X, Y)
                                    -0.25f, 0.5f, 0f, 1f, 1f,// Vertex 3 (X, Y)
                            })
                    .flip();

            int vbo = glGenBuffers();
            buffers.add(vbo);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            //Create shader program
            int shaderProgram = glCreateProgram();
            glAttachShader(shaderProgram, ShaderLoader.createVertexShader("/shaders/vertex.glsl"));
            glAttachShader(shaderProgram, ShaderLoader.createFragmentShader("/shaders/fragment.glsl"));
            glBindFragDataLocation(shaderProgram, 0, "fragColor");
            glLinkProgram(shaderProgram);
            int status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
            if (status != GL_TRUE) {
                throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
            }

            glUseProgram(shaderProgram);

            int positionAttribute = glGetAttribLocation(shaderProgram, "position");
            glEnableVertexAttribArray(positionAttribute);
            glVertexAttribPointer(positionAttribute, 2, GL_FLOAT, false, floats(5), 0);

            int colorAttribute = glGetAttribLocation(shaderProgram, "color");
            glEnableVertexAttribArray(colorAttribute);
            glVertexAttribPointer(colorAttribute, 3, GL_FLOAT, false, floats(5), floats(2));

            glDrawArrays(GL_TRIANGLES, 0, 3);
            glDrawArrays(GL_TRIANGLES, 3, 3);
        } catch (Exception e) {
            LOG.error("Error", e);
        } finally {
//            glDeleteVertexArrays(vao);
//            glDeleteBuffers(vbo);
//            glDeleteShader(vertexShader);
//            glDeleteShader(fragmentShader);
//            glDeleteProgram(shaderProgram);

            buffers.stream().forEach(GL15::glDeleteBuffers);
        }
    }

    private int floats(int i) {
        return i * java.lang.Float.BYTES;
    }
}
