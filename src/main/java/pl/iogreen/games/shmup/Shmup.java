package pl.iogreen.games.shmup;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.iogreen.games.shmup.game.Game;
import pl.iogreen.games.shmup.game.utils.Timer;

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

    public Shmup() {
        timer = new Timer();
        game = new Game(timer);
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        // glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

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

            LOG.info("Hello LWJGL {}!", Sys.getVersion());
            LOG.info("OpenGL Version: {} ({})", GL11.glGetString(GL11.GL_VERSION), glContext.getCapabilities().OpenGL41);

            long accumulator = 0;
            float alpha;

            long interval = Timer.SECONDS_IN_NANOSECOND / Timer.TARGET_UPS;

            while (game.stillAlive) {
                final long tick = timer.tick();
                accumulator += tick;

                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
                while (accumulator >= interval) {
                    game.update(tick);
                    accumulator = accumulator - interval;
                }

                alpha = (float) accumulator / interval;
                game.render(alpha);

                glfwSwapBuffers(window); // swap the color buffers

                if (timer.frameElapsed()) {
                    LOG.info("FPS: {} UPS: {}", timer.fps(), timer.ups());
                }
            }

            glfwSetWindowShouldClose(window, GL11.GL_TRUE); // We will detect this in our rendering loop

            // Release window and window callbacks
            glfwDestroyWindow(window);
            game.release();
        } finally {
            // Terminate GLFW and release the GLFW errorfun
            glfwTerminate();
            errorCallback.release();
        }
    }

    public static void main(String[] args) {
        SharedLibraryLoader.load();
        new Shmup().run();
    }
}
