package io.github.jumpyBirb;

public class Pipe {
    float x, y, width, height;

    public Pipe(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(float speed, float delta) {
        x -= speed * delta;
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }
}
