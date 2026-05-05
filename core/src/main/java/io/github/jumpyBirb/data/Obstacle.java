package io.github.jumpyBirb.data;


import com.badlogic.gdx.graphics.Texture;

/**
 * Represents one obstacle in the game world.
 *
 * <p>An obstacle has a rectangular hitbox, a texture, and moves left over time.
 * It can also check collision against another rectangle.
 *
 * <p>This class only represents a single obstacle.
 * Spawning, difficulty, and obstacle groups are handled elsewhere.
 */
public class Obstacle {
    private float x;
    private final float y;
    private final float width;
    private final float height;
    private final Texture texture;

    public Obstacle(float x, float y, float width, float height, Texture texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }


    public void update(float delta, float speed) {
        x -= speed * delta;
    }


    public boolean isOffScreen() {
        return x + width < 0;
    }


    /**
     * Checks collision using axis-aligned rectangle overlap (AABB).
     *
     * @param otherX      x-position of the other rectangle
     * @param otherY      y-position of the other rectangle
     * @param otherWidth  width of the other rectangle
     * @param otherHeight height of the other rectangle
     * @return true if the rectangles overlap
     */
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
