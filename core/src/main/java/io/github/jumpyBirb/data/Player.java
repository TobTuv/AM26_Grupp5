package io.github.jumpyBirb.data;

/**
 * Represents the player character in the game.
 *
 * <p>The player is modeled as a simple rectangle with:
 * <ul>
 *     <li>a fixed horizontal position (x)</li>
 *     <li>a vertical position (y) that changes over time</li>
 *     <li>a size (width, height)</li>
 *     <li>a vertical velocity affected by gravity and jumps</li>
 * </ul>
 *
 * <p>This class is responsible for:
 * <ul>
 *     <li>handling movement (jumping + gravity)</li>
 *     <li>tracking position</li>
 *     <li>checking simple boundary collisions (top and bottom)</li>
 * </ul>
 *
 * <p>This class does NOT handle:
 * <ul>
 *     <li>input (keyboard/mouse) → handled in Main</li>
 *     <li>collision with obstacles → handled elsewhere</li>
 *     <li>rendering → handled by GameRenderer</li>
 * </ul>
 *
 * <p>Why this class exists:
 * Previously, all player logic was inside Main, which made the code large
 * and harder to maintain. By moving player-specific behavior into this class,
 * we isolate responsibility and make the code easier to understand and modify.
 */
public class Player {
    private final float x;
    private float y;
    private final float width;
    private final float height;
    private float velocity;

    /**
     * Creates a new player with a given position and size.
     *
     * @param x horizontal position (fixed)
     * @param y starting vertical position
     * @param width player width
     * @param height player height
     */
    public Player(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Applies an upward force to the player.
     *
     * <p>This is called when the player jumps (e.g. space or mouse click).
     * It directly sets the velocity upward.
     *
     * @param jumpForce how strong the jump is
     */
    public void jump(float jumpForce) {
        velocity = jumpForce;
    }

    /**
     * Updates the player's movement.
     *
     * <p>This method applies:
     * <ul>
     *     <li>gravity (pulling the player down)</li>
     *     <li>movement based on current velocity</li>
     * </ul>
     *
     * <p>Delta time is used so movement is frame-rate independent.
     *
     * @param delta time since last frame
     * @param gravity strength of gravity
     */
    public void update(float delta, float gravity) {
        velocity -= gravity * delta;
        y += velocity * delta;
    }

    /**
     * Resets the player to a starting state.
     *
     * <p>Used when starting or restarting the game.
     *
     * @param startY the vertical position to reset to
     */
    public void reset(float startY) {
        y = startY;
        velocity = 0;
    }

    /**
     * Checks if the player has reached or passed the top of the screen.
     *
     * @param ceiling the top boundary (screen height)
     * @return true if the player touches or goes above the ceiling
     */
    public boolean hitsTop(float ceiling) {
        return y + height >= ceiling;
    }

    /**
     * Checks if the player has reached or passed the bottom of the screen.
     *
     * @return true if the player touches or goes below y = 0
     */
    public boolean hitsBottom() {
        return y <= 0;
    }

    /**
     * Ensures the player does not go above the ceiling.
     *
     * <p>If the player exceeds the top boundary, they are clamped back down
     * so that the entire player stays visible on screen.
     *
     * <p>Velocity is reset to 0 to prevent continued upward movement.
     *
     * @param ceiling the top boundary
     */
    public void clampToCeiling(float ceiling) {
        if (y > ceiling - height) {
            y = ceiling - height;
            velocity = 0;
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }


}
