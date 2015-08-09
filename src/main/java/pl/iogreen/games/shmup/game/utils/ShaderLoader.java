package pl.iogreen.games.shmup.game.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

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
}
