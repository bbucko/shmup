package pl.iogreen.games.shmup;

import org.apache.commons.imaging.ImageReadException;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.iogreen.games.shmup.game.Game;
import pl.iogreen.games.shmup.game.utils.Timer;
import pl.iogreen.games.shmup.graphic.Image;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Shmup {

    private static final Logger LOG = LoggerFactory.getLogger(Shmup.class);

    static public final int SCREEN_WIDTH = 800;
    static public final int SCREEN_HEIGHT = 600; //480

    // We need to strongly reference callback instances.
    private final GLFWErrorCallback errorCallback;

    // The window handle
    private long window;

    private final Game game;
    private final Timer timer;

    long accumulator = 0;

    long interval = Timer.SECONDS_IN_NANOSECOND / Timer.TARGET_UPS;

    public Shmup() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = new GLFWErrorCallback() {
            private final Logger errorLogger = LoggerFactory.getLogger("ErrorLogger");

            @Override
            public void invoke(int error, long description) {
                errorLogger.error("[{}] Error occurred: {}", error, description);
            }
        });

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        timer = new Timer();
        game = new Game(timer);

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4); //OpenGL Major
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1); //OpenGL Minor
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        window = glfwCreateWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, game);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    public void run() {
        try {
            final GLContext glContext = GLContext.createFromCurrent();

            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);

            LOG.info("Hello LWJGL {}!", Sys.getVersion());
            LOG.info("OpenGL Version: {} ({})", GL11.glGetString(GL11.GL_VERSION), glContext.getCapabilities().OpenGL41);

            game.init();

            while (game.stillAlive) {
                final long tick = timer.tick();
                accumulator += tick;

                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glfwPollEvents();
                while (accumulator >= interval) {
                    game.update(interval);
                    accumulator = accumulator - interval;
                }

                game.render((float) accumulator / interval);

                glfwSwapBuffers(window);

                if (timer.frameElapsed()) {
                    LOG.debug("FPS: {} UPS: {}", timer.fps(), timer.ups());
                }
            }

            glfwSetWindowShouldClose(window, GL11.GL_TRUE);
        } finally {
            glfwDestroyWindow(window);
            GLFW.glfwTerminate();

            game.close();
            errorCallback.release();
        }
    }

    public static void main(String[] args) {
        SharedLibraryLoader.load();
        new Shmup().run();
    }
}
