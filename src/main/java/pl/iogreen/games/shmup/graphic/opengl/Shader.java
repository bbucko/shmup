package pl.iogreen.games.shmup.graphic.opengl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    public final int objectId;

    private Shader(String path, int vertexType) {
        objectId = glCreateShader(vertexType);
        glShaderSource(objectId, loadShader(path));
        glCompileShader(objectId);
        int status = glGetShaderi(objectId, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(objectId));
        }
    }

    public static Shader createVertexShader(String path) {
        return new Shader(path, GL_VERTEX_SHADER);
    }

    public static Shader createFragmentShader(String path) {
        return new Shader(path, GL_FRAGMENT_SHADER);
    }

    public static CharSequence loadShader(String path) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            final URL resource = Shader.class.getResource(path);
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

}
