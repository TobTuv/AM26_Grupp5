package io.github.jumpyBirb.data;

/**
 * Handles score calculation during gameplay.
 *
 * <p>The score increases over time using exponential growth
 * and is converted into a rounded integer value for display.
 *
 * <p>Score updates are time-based and independent of frame rate.
 */
public class Score {
    private double score = 1;
    private long visualScore = 0;
    private float timer = 0f;

    public long getVisualScore() {
        return visualScore;
    }

    public void update(float delta, boolean running) {
        if (!running) return;

        timer += delta;

        while (timer >= 0.1f) {
            score *= 1.01;
            visualScore = Math.round((score - 1) * 1000);
            timer -= 0.1f;
        }
    }

    public void reset() {
        score = 1;
        visualScore = 0;
        timer = 0f;
    }
}
