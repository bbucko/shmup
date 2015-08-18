package pl.iogreen.games.shmup.graphic;

import pl.iogreen.games.shmup.graphic.opengl.Program;

public interface Drawable {
    void prepare(Program program);

    void draw(Program program);
}
