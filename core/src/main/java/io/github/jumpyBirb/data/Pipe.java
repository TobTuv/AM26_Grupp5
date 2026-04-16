package io.github.jumpyBirb.data;

public class Pipe {
    private float x;
    private final float y;
    private final float width;
    private final float height;

    public Pipe(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(float delta, float speed) {
        x -= speed * delta;
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public boolean collidesWith(float otherX, float otherY, float otherWidth, float otherHeight) {
        return otherX < x + width &&
            otherX + otherWidth > x &&
            otherY < y + height &&
            otherY + otherHeight > y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
