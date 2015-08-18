package pl.iogreen.games.shmup.graphic.opengl;

import org.apache.commons.imaging.ImageReadException;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.iogreen.games.shmup.graphic.Image;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

public class Texture {

    private static final Logger LOG = LoggerFactory.getLogger(Texture.class);

    final public int objectId;

    public Texture(String texturePath) {
        objectId = glGenTextures();

        try (final Image image = Image.load(texturePath)) {

            glBindTexture(GL_TEXTURE_2D, objectId);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, (FloatBuffer) BufferUtils.createFloatBuffer(4).put(new float[]{1f, 0f, 0f, 1f}).flip());

            if (image.comp() == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.width(), image.height(), 0, GL_RGB, GL_UNSIGNED_BYTE, image.buffer());
            } else {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width(), image.height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image.buffer());
            }

            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
        } catch (ImageReadException | IOException | URISyntaxException e) {
            LOG.error("Ignored error: {}", e);
        }
    }
}
