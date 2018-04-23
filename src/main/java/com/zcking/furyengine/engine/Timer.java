package com.zcking.furyengine.engine;

/**
 * Main game timer, used by the engine. Allows for easy timing of game loops and
 * capturing time deltas between loops.
 */
public class Timer {

    private double lastLoopTime;

    /**
     * Initializes the timer.
     */
    public void init() {
        lastLoopTime = getTime();
    }

    /**
     * Gets the current time in seconds.
     * @return Current time (in seconds).
     */
    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    /**
     * Gets the time elapsed since the last game loop, in milliseconds.
     * @return Time elapsed, in milliseconds.
     */
    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    /**
     * Gets the time at which the last game loop completed.
     * @return Time when the last game loop finished.
     */
    public double getLastLoopTime() {
        return lastLoopTime;
    }

}
