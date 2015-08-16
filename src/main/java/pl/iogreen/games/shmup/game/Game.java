package pl.iogreen.games.shmup.game;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.iogreen.games.shmup.game.utils.Timer;
import pl.iogreen.games.shmup.graphic.opengl.Program;
import pl.iogreen.games.shmup.graphic.opengl.Shader;
import pl.iogreen.games.shmup.graphic.opengl.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Game extends GLFWKeyCallback {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private float angle = 0;
    private float step = 0.05f;
    private Program program;

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
        timer.updateFPSCount();
        try {
            //Create VAO
            int vao = glGenVertexArrays();
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
            program = new Program(
                    Shader.createVertexShader("/shaders/vertex.glsl"),
                    Shader.createFragmentShader("/shaders/fragment.glsl"));
            program.use();

            /* Specify Position Pointer */
            int positionAttribute = glGetAttribLocation(program.objectId, "position");
            glEnableVertexAttribArray(positionAttribute);
            glVertexAttribPointer(positionAttribute, 3, GL_FLOAT, false, floats(8), 0);

            /* Specify Color Pointer */
            int colorAttribute = glGetAttribLocation(program.objectId, "color");
            glEnableVertexAttribArray(colorAttribute);
            glVertexAttribPointer(colorAttribute, 3, GL_FLOAT, false, floats(8), floats(3));

            /* Specify Texture Pointer */
            int textureAttribute = glGetAttribLocation(program.objectId, "texcoord");
            glEnableVertexAttribArray(textureAttribute);
            glVertexAttribPointer(textureAttribute, 2, GL_FLOAT, false, floats(8), floats(6));

            /* Specify Uniform Model */
            int uniModel = glGetUniformLocation(program.objectId, "model");
            final FloatBuffer modelFB = BufferUtils.createFloatBuffer(16);
            Matrix4f model = new Matrix4f().rotate(angle, 0f, 0f, 1f);
            glUniformMatrix4fv(uniModel, false, model.get(modelFB));

            /* Specify Uniform View */
            int uniView = glGetUniformLocation(program.objectId, "view");
            Matrix4f view = new Matrix4f();
            final FloatBuffer viewFB = BufferUtils.createFloatBuffer(16);
            glUniformMatrix4fv(uniView, false, view.get(viewFB));

            /* Specify Uniform Projection */
            int uniProjection = glGetUniformLocation(program.objectId, "projection");
            float ratio = 640f / 480f;
            Matrix4f projection = new Matrix4f().ortho(-ratio, ratio, -1f, 1f, -1f, 1f);
            final FloatBuffer projectionFB = BufferUtils.createFloatBuffer(16);
            glUniformMatrix4fv(uniProjection, false, projection.get(projectionFB));

            /* Generate Texture */
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, new Texture("/graphics/sample.png").objectId);
            glUniform1i(glGetUniformLocation(program.objectId, "firstTexture"), 0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, new Texture("/graphics/sample2.png").objectId);
            glUniform1i(glGetUniformLocation(program.objectId, "secondTexture"), 1);

            int uniTime = glGetUniformLocation(program.objectId, "time");
            glUniform1f(uniTime, (float) Math.sin(angle));

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        } catch (Exception e) {
            LOG.error("Error", e);
        }
    }

    private int floats(int i) {
        return i * java.lang.Float.BYTES;
    }

    public void close() {
        program.close();
    }
}
