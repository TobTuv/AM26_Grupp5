package io.github.jumpyBirb.game;

import io.github.jumpyBirb.data.Obstacle;

import java.util.List;

/**
 * Represents a pair of obstacles that form a gap the player must pass through.
 *
 * <p>An obstacle pair consists of:
 * <ul>
 *     <li>a bottom obstacle growing upward from the ground</li>
 *     <li>a top obstacle growing downward from the ceiling</li>
 * </ul>
 *
 * <p>Both obstacles in the pair share the same horizontal position and move
 * together as a single gameplay unit.
 *
 * <p>This class is responsible for:
 * <ul>
 *     <li>updating both obstacles' movement</li>
 *     <li>checking collision with a given bounding box</li>
 *     <li>determining when the pair is off screen</li>
 *     <li>tracking whether the player has passed the pair (for scoring)</li>
 * </ul>
 *
 * <p>Why this class exists:
 * Previously, top and bottom obstacles were handled as separate objects,
 * which made it harder to manage them as a logical unit. By grouping them
 * into a single class, the code becomes cleaner and enables features like
 * scoring when passing a pair.
 *
 * <p>Note:
 * This class does not handle spawning or difficulty scaling.
 * That responsibility belongs to {@code ObstacleManager}.
 */

public class ObstaclePair {

    private final Obstacle top;
    private final Obstacle bottom;

    private boolean passed = false;

    /**
     * Creates a new obstacle pair with a top and bottom obstacle.
     *
     * @param top the top obstacle (extends downward)
     * @param bottom the bottom obstacle (extends upward)
     */
    public ObstaclePair(Obstacle top, Obstacle bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public void update(float delta, float speed) {
        top.update(delta, speed);
        bottom.update(delta, speed);
    }

    public boolean isOffScreen() {
        return top.isOffScreen();
    }

    public boolean collidesWith(float x, float y, float w, float h) {
        return top.collidesWith(x, y, w, h)
            || bottom.collidesWith(x, y, w, h);
    }

    /**
     * Returns both obstacles in this pair as a list.
     *
     * <p>Mainly useful for iteration when both obstacles should be treated
     * uniformly (e.g. rendering or bulk operations).
     */
    public List<Obstacle> getObstacles() {
        return List.of(top, bottom);
    }

    public Obstacle getTop() {
        return top;
    }

    public Obstacle getBottom() {
        return bottom;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
