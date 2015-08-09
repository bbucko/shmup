package pl.iogreen.games.shmup.game.utils;

public class Timer {

    public long timeCount = 0l;
    private long lastLoopTime = System.nanoTime();

    private int fps;
    private int ups;

    private int fpsCount = 0;
    private int upsCount = 0;

    public long tick() {
        final long delta = System.nanoTime() - lastLoopTime;
        timeCount += delta;
        lastLoopTime = System.nanoTime();
        return delta;
    }

    public int updateFPSCount() {
        return fpsCount++;
    }

    public int updateUPSCount() {
        return upsCount++;
    }

    private void zero() {
        fps = fpsCount;
        ups = upsCount;
        fpsCount = 0;
        upsCount = 0;
        timeCount = 0;
    }

    public int fps() {
        return fps;
    }

    public int ups() {
        return ups;
    }

    public boolean frameElapsed() {
        final boolean elapsed = timeCount > 1000000000l;
        if (elapsed) {
            zero();
        }
        return elapsed;
    }


}
