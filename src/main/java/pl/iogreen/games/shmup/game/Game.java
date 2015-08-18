package pl.iogreen.games.shmup.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.iogreen.games.shmup.Shmup;
import pl.iogreen.games.shmup.game.utils.Timer;
import pl.iogreen.games.shmup.graphic.Player;
import pl.iogreen.games.shmup.graphic.opengl.Program;
import pl.iogreen.games.shmup.graphic.opengl.Shader;
import pl.iogreen.games.shmup.graphic.opengl.Texture;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class Game extends GLFWKeyCallback {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private static final float RATIO = (float) (Shmup.SCREEN_WIDTH / Shmup.SCREEN_HEIGHT);

    private float angle = 0;
    private float time = 0;

    private Program program;

    private final Timer timer;
    private final Player player;

    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f().ortho(-RATIO, RATIO, -1f, 1f, -1f, 1f);

    public boolean stillAlive = true;

    public Game(Timer timer) {
        this.timer = timer;
        this.player = new Player();
    }

    @Override
    public void invoke(
            long window,
            int key,
            int scanCode,
            int action,
            int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            stillAlive = false;
        }
    }

    public void init() {
        program = new Program(
                Shader.createVertexShader("/shaders/vertex/simple.glsl"),
                Shader.createFragmentShader("/shaders/fragment/simple.glsl")
        );
        program.uniform("model", modelMatrix);
        program.uniform("view", viewMatrix);
        program.uniform("projection", projectionMatrix);
        program.uniform("time", time);

        viewMatrix.lookAt(new Vector3f(3, 3, 3), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        projectionMatrix.perspective((float) Math.toRadians(50), RATIO, 0.1f, 10.0f);

        /* Generate Texture */
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, new Texture("/graphics/wooden-crate.jpg").objectId);
        glUniform1i(glGetUniformLocation(program.objectId, "firstTexture"), 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, new Texture("/graphics/sample2.png").objectId);
        glUniform1i(glGetUniformLocation(program.objectId, "secondTexture"), 1);
        program.stopUsing();
    }

    private void updateState() {

    }

    private void updateUniforms() {
        program.uniform("model", modelMatrix);
        program.uniform("view", viewMatrix);
        program.uniform("projection", projectionMatrix);
        program.uniform("time", time);
    }

    public void update(long tick) {
        timer.updateUPSCount();
        updateState();
    }

    public void render(float alpha) {
        timer.updateFPSCount();
        program.use();
        updateUniforms();

        try {
            player.prepare(program);
            player.draw(program);
        } catch (Exception e) {
            LOG.error("Error", e);
        } finally {
            program.stopUsing();
        }
    }

    public void close() {
        release();
        program.close();
    }
}
