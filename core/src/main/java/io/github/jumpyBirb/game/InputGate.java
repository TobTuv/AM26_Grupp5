package io.github.jumpyBirb.game;

/**
 * Simple utility for temporarily blocking input.
 *
 * <p>This class is used to prevent input from being accepted
 * for a short period of time, for example:
 * <ul>
 *     <li>after state transitions</li>
 *     <li>to avoid accidental double input</li>
 *     <li>to give UI time to settle</li>
 * </ul>
 *
 * <p>Usage:
 * <ul>
 *     <li>call {@link #block(float)} to start a delay</li>
 *     <li>call {@link #update(float)} each frame</li>
 *     <li>check {@link #canAcceptInput()} before handling input</li>
 * </ul>
 */
public class InputGate {

    private float delay = 0f;

    public void update(float delta) {
        if (delay > 0) {
            delay -= delta;
        }
    }

    public boolean canAcceptInput() {
        return delay <= 0;
    }

    /**
     * Blocks input for a given number of seconds.
     *
     * @param seconds duration to block input
     */
    public void block(float seconds) {
        delay = seconds;
    }
}
