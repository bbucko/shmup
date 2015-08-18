package pl.iogreen.games.shmup.graphic;

import org.lwjgl.BufferUtils;
import pl.iogreen.games.shmup.graphic.opengl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Player implements Drawable {

    int vao;
    int vbo;
    int ebo;

    public float x = 0f;
    public float y = 0f;
    public float z = 0;

    @Override
    public void prepare(Program program) {
        //Create VAO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        //Create VBO
        FloatBuffer vertices = (FloatBuffer) BufferUtils.createFloatBuffer(4 * 8)
//                .put(
//                        new float[]{
//                                x - 0.5f, y + 0.5f, z + 1f, 1f, 1f, 1f, 0f, 0f,
//                                x + 0.5f, y + 0.5f, z + 1f, 1f, 1f, 1f, 1f, 0f,
//                                x + 0.5f, y - 0.5f, z + 1f, 1f, 1f, 1f, 1f, 1f,
//                                x - 0.5f, y - 0.5f, z + 1f, 1f, 1f, 1f, 0f, 1f,
//                        })
                .put(
                        new float[]{
                                -1f, 1f, 1f, 1f, 1f, 1f, 0f, 0f,
                                1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f,
                                1f, -1f, 1f, 1f, 1f, 1f, 1f, 1f,
                                -1f, -1f, 1f, 1f, 1f, 1f, 0f, 1f,
                        })
                .flip();

            /* Generate Vertex Buffer Object */
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        IntBuffer elements = BufferUtils.createIntBuffer(2 * 3);
        elements.put(new int[]{0, 1, 2});
        elements.put(new int[]{2, 3, 0});
        elements.flip();

        program.pointer("position", 3, 0);
        program.pointer("color", 3, 3);
        program.pointer("texcoord", 2, 6);

        /* Generate Element Buffer Object */
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
    }

    @Override
    public void draw() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindVertexArray(vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

}
