package pl.iogreen.games.shmup.graphic;

import org.lwjgl.BufferUtils;
import pl.iogreen.games.shmup.graphic.opengl.Program;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
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
        FloatBuffer vertices = (FloatBuffer) BufferUtils.createFloatBuffer(6 * 6 * Program.VERTEX_SIZE)
                .put(
                        new float[]{
                                //  X     Y     Z       U     V
                                // bottom
                                -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
                                1.0f, -1.0f, -1.0f, 1.0f, 0.0f,
                                -1.0f, -1.0f, 1.0f, 0.0f, 1.0f,
                                1.0f, -1.0f, -1.0f, 1.0f, 0.0f,
                                1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                                -1.0f, -1.0f, 1.0f, 0.0f, 1.0f,

                                // top
                                -1.0f, 1.0f, -1.0f, 0.0f, 0.0f,
                                -1.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                                1.0f, 1.0f, -1.0f, 1.0f, 0.0f,
                                1.0f, 1.0f, -1.0f, 1.0f, 0.0f,
                                -1.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                                1.0f, 1.0f, 1.0f, 1.0f, 1.0f,

                                // front
                                -1.0f, -1.0f, 1.0f, 1.0f, 0.0f,
                                1.0f, -1.0f, 1.0f, 0.0f, 0.0f,
                                -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                                1.0f, -1.0f, 1.0f, 0.0f, 0.0f,
                                1.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                                -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,

                                // back
                                -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
                                -1.0f, 1.0f, -1.0f, 0.0f, 1.0f,
                                1.0f, -1.0f, -1.0f, 1.0f, 0.0f,
                                1.0f, -1.0f, -1.0f, 1.0f, 0.0f,
                                -1.0f, 1.0f, -1.0f, 0.0f, 1.0f,
                                1.0f, 1.0f, -1.0f, 1.0f, 1.0f,

                                // left
                                -1.0f, -1.0f, 1.0f, 0.0f, 1.0f,
                                -1.0f, 1.0f, -1.0f, 1.0f, 0.0f,
                                -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
                                -1.0f, -1.0f, 1.0f, 0.0f, 1.0f,
                                -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                                -1.0f, 1.0f, -1.0f, 1.0f, 0.0f,

                                // right
                                1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                                1.0f, -1.0f, -1.0f, 1.0f, 0.0f,
                                1.0f, 1.0f, -1.0f, 0.0f, 0.0f,
                                1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                                1.0f, 1.0f, -1.0f, 0.0f, 0.0f,
                                1.0f, 1.0f, 1.0f, 0.0f, 1.0f
                        })
                .flip();

        /* Generate Vertex Buffer Object */
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        program.pointer("position", 3, 0);
        program.pointer("texcoord", 2, 3);
    }

    @Override
    public void draw(Program program) {
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glDrawArrays(GL_TRIANGLES, 0, 6 * 2 * 3);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

}
