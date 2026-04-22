package io.github.jumpyBirb.data;
public class Score {
    private double score = 1;
    private double visualScore = 0;
    private float timer = 0f;

    public void update(float delta, boolean running) {
        if (!running) return;

        timer += delta;

        while (timer >= 0.1f) {
            score *= 1.01;
            visualScore = (score - 1) * 100;
            timer -= 0.1f;
        }
    }

    public void reset() {
        score = 1;
        visualScore = 0;
        timer = 0f;
    }

    public void stopScore() {
        timer -= 0.1f;
    }

    public void restartScore() {
        timer += 0.1f;
    }

    public double getScore() {
        return visualScore;
    }
}
