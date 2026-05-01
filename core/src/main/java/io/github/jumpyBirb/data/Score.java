package io.github.jumpyBirb.data;

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
            //Temporarily reduced the issue by rounding the visual score,
            // but not sure ot sure if this is the best long-term solution.
            // Suggestion that saves only one finalScore and uses it consistently for both display and saving.
            visualScore = Math.round((score - 1) * 1000);
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

}
