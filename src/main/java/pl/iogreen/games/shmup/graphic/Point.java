package pl.iogreen.games.shmup.graphic;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Point {
    public abstract float x();

    public abstract float y();

    public abstract float z();

    public abstract float r();

    public abstract float g();

    public abstract float b();

    public abstract float s();

    public abstract float t();

    public float[] toArr() {
        return new float[]{x(), y(), z(), r(), g(), b(), s(), t()};
    }
}
