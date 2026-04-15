package io.github.jumpyBirb.data;

public class Player {
    private final float x;
    private float y;
    private final float width;
    private final float height;
    private float velocity;

    public Player(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void jump(float jumpForce) {
        velocity = jumpForce;
    }

    public void update(float delta, float gravity) {
        velocity -= gravity * delta;
        y += velocity * delta;
    }

    public void reset(float startY) {
        y = startY;
        velocity = 0;
    }

    public boolean hitsTop(float ceiling) {
        return y + height >= ceiling;
    }

    public boolean hitsBottom() {
        return y <= 0;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void clampToCeiling(float ceiling) {
        if (y > ceiling - height) {
            y = ceiling - height;
            velocity = 0;
        }
    }
}
