package pl.iogreen.games.shmup.game.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderLoader {
    public static CharSequence loadShader(String path) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            final URL resource = ShaderLoader.class.getResource(path);
            final BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()));

            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return shaderSource;
    }

    public static int createVertexShader(String path) {
        return loadShader(path, GL_VERTEX_SHADER);
    }

    public static int createFragmentShader(String path) {
        return loadShader(path, GL_FRAGMENT_SHADER);
    }

    private static int loadShader(String path, int vertexType) {
        int aShader = glCreateShader(vertexType);
        glShaderSource(aShader, ShaderLoader.loadShader(path));
        glCompileShader(aShader);
        int status = glGetShaderi(aShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(aShader));
        }
        return aShader;
    }
}
