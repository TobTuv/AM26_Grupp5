package io.github.jumpyBirb;

public class Score {
    private double score = 1;
    private double visualScore = score - 1;

    public Score(){
        this.score = 1;
        this.visualScore = 0;
    }

    public void resetScore(double score, double visualScore) {
       this.score = score;
       this.visualScore = visualScore;
    }

    public void addScore() {
        score = score * 1.01;
        visualScore = (score - 1) * 1000;
    }

    public double getScore() {
        return visualScore;
    }

}
