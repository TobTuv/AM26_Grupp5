package io.github.jumpyBirb;

public class Score {
    private double score = 1;
    private double visualScore = score - 1;

    public void resetScore() {
        score = 1;
        visualScore = score - 1;
    }

    public void addScore() {
        score = score * 1.01;
        visualScore = (score - 1) * 1000;
    }

    public double getScore() {
        return visualScore;
    }

}
