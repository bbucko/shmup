package pl.iogreen.games.shmup.graphic;

import org.apache.commons.io.IOUtils;
import org.immutables.value.Value;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

@Value.Immutable
public abstract class Image {
    public abstract int width();

    public abstract int height();

    public abstract ByteBuffer buffer();

    private static final boolean USE_STB = true;

    public static Image load(String path) throws IOException, URISyntaxException {
        return USE_STB ? useSTB(path) : useImageIO(path);
    }

    private static Image useImageIO(String path) throws URISyntaxException, IOException {
        final File input = new File(Image.class.getResource(path).toURI());
        final BufferedImage read = ImageIO.read(input);
        final BufferedImage image = flipImage(read);

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        final ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                /* Pixel as RGBA: 0xAARRGGBB */
                int pixel = pixels[y * width + x];

                /* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
                buffer.put((byte) ((pixel >> 16) & 0xFF));

                /* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
                buffer.put((byte) ((pixel >> 8) & 0xFF));

                /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                buffer.put((byte) (pixel & 0xFF));

                /* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        /* Do not forget to flip the buffer! */
        buffer.flip();
        return ImmutableImage.builder()
                .width(width)
                .height(height)
                .buffer(buffer).build();
    }

    private static Image useSTB(String path) throws IOException {
        final byte[] imageBytes = IOUtils.toByteArray(Image.class.getResourceAsStream(path));
        final ByteBuffer imageBuffer = ByteBuffer.allocateDirect(imageBytes.length);
        imageBuffer.put(imageBytes).flip();

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        // Use info to read image metadata without decoding the entire image.
        // We don't need this for this demo, just testing the API.
        if (stbi_info_from_memory(imageBuffer, w, h, comp) == 0)
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
        return ImmutableImage.builder()
                .width(w.get(0))
                .height(h.get(0))
                .buffer(stbi_load_from_memory(imageBuffer, w, h, comp, 0)).build();
    }

    private static BufferedImage flipImage(BufferedImage image) {
        AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
        transform.translate(0, -image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(image, null);
    }
}
