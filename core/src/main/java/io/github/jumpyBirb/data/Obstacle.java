package io.github.jumpyBirb.data;


/**
 * Represents one obstacle in the game world.
 *
 * <p>An obstacle is currently a rectangular hitbox that:
 * <ul>
 *     <li>has a position (x, y)</li>
 *     <li>has a size (width, height)</li>
 *     <li>moves horizontally to the left over time</li>
 *     <li>can be removed when it has left the screen</li>
 *     <li>can check collision against another rectangle</li>
 * </ul>
 *
 * <p>This class only represents one single obstacle.
 * It does not handle spawning, lists of obstacles, or game difficulty.
 * That responsibility belongs in {@code ObstacleManager}.
 *
 * <p>Why this class exists:
 * Earlier, obstacle data and obstacle behavior were handled directly in Main.
 * This made Main large and harder to understand. By moving the logic for
 * a single obstacle into its own class, the code becomes easier to read,
 * easier to test, and easier to change later.
 *
 * <p>Current design:
 * <ul>
 *     <li>{@code x} changes over time because the obstacle moves left</li>
 *     <li>{@code y}, {@code width}, and {@code height} are fixed after creation</li>
 *     <li>collision is handled as a simple rectangle-vs-rectangle check</li>
 * </ul>
 *
 * <p>Possible future extensions:
 * <ul>
 *     <li>different obstacle types</li>
 *     <li>different movement patterns</li>
 *     <li>sprite/texture reference directly in the obstacle</li>
 *     <li>damage, score value, or special effects</li>
 * </ul>
 */

public class Obstacle {
    private float x;
    private final float y;
    private final float width;
    private final float height;

    /**
     * Creates one obstacle with a given position and size.
     *
     * @param x horizontal start position
     * @param y vertical start position
     * @param width obstacle width
     * @param height obstacle height
     */
    public Obstacle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Updates the obstacle position by moving it to the left.
     *
     * <p>The movement uses delta time so that movement speed stays consistent
     * regardless of frame rate.
     *
     * @param delta time since last frame
     * @param speed horizontal movement speed
     */
    public void update(float delta, float speed) {
        x -= speed * delta;
    }

    /**
     * Checks whether the obstacle has fully left the visible screen area.
     *
     * If the obstacle's right edge is left of x = 0, it is considered
     * off screen and can be removed from the obstacle list.
     *
     * @return true if the obstacle is no longer visible on screen
     */
    public boolean isOffScreen() {
        return x + width < 0;
    }


    /**
     * Checks collision between this obstacle and another rectangle.
     *
     * This is an axis-aligned bounding box -- something that we might change? -- collision check.
     * In practice, this means we compare this obstacle rectangle against
     * another rectangle, for example the player's hitbox.
     *
     * @param otherX x-position of the other rectangle
     * @param otherY y-position of the other rectangle
     * @param otherWidth width of the other rectangle
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
