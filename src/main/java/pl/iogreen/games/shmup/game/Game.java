package pl.iogreen.games.shmup.game;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.iogreen.games.shmup.game.utils.ShaderLoader;
import pl.iogreen.games.shmup.game.utils.Timer;
import pl.iogreen.games.shmup.graphic.Image;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Game extends GLFWKeyCallback {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private float angle = 0;
    private float step = 0.05f;

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
        angle = angle + alpha * step;
//        angle = 0;
        timer.updateFPSCount();
        Set<Integer> buffers = new HashSet<>();
        try {
            //Create VAO
            int vao = glGenVertexArrays();
            buffers.add(vao);
            glBindVertexArray(vao);

            //Create VBO
            FloatBuffer vertices = (FloatBuffer) BufferUtils.createFloatBuffer(4 * 8)
                    .put(
                            new float[]{
                                    //X   Y     Z   R   G   B   S   T
                                    -0.5f, 0.5f, 0f, 1f, 1f, 1f, 0f, 0f,
                                    0.5f, 0.5f, 0f, 1f, 1f, 1f, 1f, 0f,
                                    0.5f, -0.5f, 0f, 1f, 1f, 1f, 1f, 1f,
                                    -0.5f, -0.5f, 0f, 1f, 1f, 1f, 0f, 1f,
                            })
                    .flip();

            /* Generate Vertex Buffer Object */
            int vbo = glGenBuffers();
            buffers.add(vbo);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            IntBuffer elements = BufferUtils.createIntBuffer(2 * 3);
            elements.put(new int[]{0, 1, 2});
            elements.put(new int[]{2, 3, 0});
            elements.flip();

            /* Generate Element Buffer Object */
            int ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);

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

            /* Specify Position Pointer */
            int positionAttribute = glGetAttribLocation(shaderProgram, "position");
            glEnableVertexAttribArray(positionAttribute);
            glVertexAttribPointer(positionAttribute, 3, GL_FLOAT, false, floats(8), 0);

            /* Specify Color Pointer */
            int colorAttribute = glGetAttribLocation(shaderProgram, "color");
            glEnableVertexAttribArray(colorAttribute);
            glVertexAttribPointer(colorAttribute, 3, GL_FLOAT, false, floats(8), floats(3));

            /* Specify Texture Pointer */
            int textureAttribute = glGetAttribLocation(shaderProgram, "texcoord");
            glEnableVertexAttribArray(textureAttribute);
            glVertexAttribPointer(textureAttribute, 2, GL_FLOAT, false, floats(8), floats(6));

            int uniModel = glGetUniformLocation(shaderProgram, "model");
            final FloatBuffer modelFB = BufferUtils.createFloatBuffer(16);
            Matrix4f model = new Matrix4f().rotate(angle, 0f, 0f, 1f);
            glUniformMatrix4fv(uniModel, false, model.get(modelFB));

            int uniView = glGetUniformLocation(shaderProgram, "view");
            Matrix4f view = new Matrix4f();
            final FloatBuffer viewFB = BufferUtils.createFloatBuffer(16);
            glUniformMatrix4fv(uniView, false, view.get(viewFB));

            int uniProjection = glGetUniformLocation(shaderProgram, "projection");
            float ratio = 640f / 480f;
            Matrix4f projection = new Matrix4f().ortho(-ratio, ratio, -1f, 1f, -1f, 1f);
            final FloatBuffer projectionFB = BufferUtils.createFloatBuffer(16);
            glUniformMatrix4fv(uniProjection, false, projection.get(projectionFB));

            /* Generate Texture */
            glActiveTexture(GL_TEXTURE0);
            loadTexture("/graphics/sample.png");
            glUniform1i(glGetUniformLocation(shaderProgram, "firstTexture"), 0);

            glActiveTexture(GL_TEXTURE1);
            loadTexture("/graphics/sample2.png");
            glUniform1i(glGetUniformLocation(shaderProgram, "secondTexture"), 1);

            int uniTime = glGetUniformLocation(shaderProgram, "time");
            glUniform1f(uniTime, (float) Math.sin(angle));

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        } catch (Exception e) {
            LOG.error("Error", e);
        } finally {
//            glDeleteVertexArrays(vao);
//            glDeleteBuffers(vbo);
//            glDeleteShader(vertexShader);
//            glDeleteShader(fragmentShader);
//            glDeleteProgram(shaderProgram);

//            buffers.stream().forEach(GL15::glDeleteBuffers);
        }
    }

    private void loadTexture(String texturePath) throws IOException, URISyntaxException {
        Image image = Image.load(texturePath);

        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, (FloatBuffer) BufferUtils.createFloatBuffer(4).put(new float[]{1f, 0f, 0f, 1f}).flip());
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.width(), image.height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image.buffer());
    }

    private int floats(int i) {
        return i * java.lang.Float.BYTES;
    }
}
