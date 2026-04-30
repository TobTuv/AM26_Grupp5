package io.github.jumpyBirb.game;

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

    public void block(float seconds) {
        delay = seconds;
    }
}
