package io.github.jumpyBirb.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import io.github.jumpyBirb.data.Obstacle;

/**
 * Manages all obstacles currently active in the game.
 *
 * <p>This class is responsible for obstacle-related game logic, such as:
 * <ul>
 *     <li>storing all active obstacles</li>
 *     <li>spawning new obstacle pairs over time</li>
 *     <li>moving obstacles to the left</li>
 *     <li>removing obstacles that leave the screen</li>
 *     <li>checking collision between the player and obstacles</li>
 * </ul>
 *
 * <p>This class does NOT represent one obstacle.
 * One single obstacle is represented by the {@link Obstacle} class.
 * The purpose of this class is instead to manage many obstacles together.
 *
 * <p>Why this class exists:
 * Earlier, all obstacle logic was handled directly inside Main. That made Main
 * large and harder to understand. By moving obstacle management into its own class,
 * the code becomes easier to read, easier to maintain, and easier to expand later.
 *
 * <p>Current behavior:
 * <ul>
 *     <li>Obstacles spawn in top/bottom pairs with a gap in between</li>
 *     <li>the gap becomes smaller over time</li>
 *     <li>the time between spawns becomes shorter over time</li>
 *     <li>all active obstacles move left every frame</li>
 *     <li>obstacles are removed once they are fully off screen</li>
 * </ul>
 *
 * <p>Possible future extensions:
 * <ul>
 *     <li>different obstacle types</li>
 *     <li>randomized obstacle patterns beyond simple pairs</li>
 *     <li>difficulty settings</li>
 *     <li>score triggers when passing obstacles</li>
 * </ul>
 */
public class ObstacleManager {
    private List<Obstacle> obstacles = new ArrayList<>();
    private float pipeTimer = 0f;

    private final float pipeWidth;
    private final float minPipeHeight;
    private final float screenWidth;
    private final float screenHeight;

    /**
     * Creates an obstacle manager with screen information and obstacle settings.
     *
     * @param pipeWidth width of each spawned obstacle
     * @param minPipeHeight minimum obstacle height allowed
     * @param screenWidth width of the game screen
     * @param screenHeight height of the game screen
     */
    public ObstacleManager(float pipeWidth, float minPipeHeight, float screenWidth, float screenHeight) {
        this.pipeWidth = pipeWidth;
        this.minPipeHeight = minPipeHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }


    /**
     * Updates all obstacle logic for one frame.
     *
     * <p>This method does three main things:
     * <ul>
     *     <li>updates the spawn timer</li>
     *     <li>spawns new obstacle pairs when needed</li>
     *     <li>moves all existing obstacles and removes old ones</li>
     * </ul>
     *
     * <p>Difficulty scaling is also handled here:
     * <ul>
     *     <li>the safe gap becomes smaller over time</li>
     *     <li>new obstacles spawn more frequently over time</li>
     * </ul>
     *
     * @param delta time since last frame
     * @param timePlaying total time the current run has been active
     * @param speed horizontal movement speed of obstacles
     */
    public void update(float delta, float timePlaying, float speed) {
        pipeTimer += delta;

        float currentGap = Math.max(90f, 160f - (timePlaying / 5f) * 2);
        float spawnInterval = Math.max(1.3f, 2f - (timePlaying / 40f));

        if (pipeTimer > spawnInterval) {
            pipeTimer = 0;
            spawnPair(currentGap);
        }

        Iterator<Obstacle> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Obstacle obstacle = iter.next();
            obstacle.update(delta, speed);

            if (obstacle.isOffScreen()) {
                iter.remove();
            }
        }
    }

    /**
     * Spawns a pair of obstacles with a vertical gap between them.
     *
     * The first obstacle starts at the bottom of the screen and goes upward.
     * The second obstacle starts above the gap and continues to the top of the screen.
     *
     * The vertical start of the gap is randomized, but constrained so that
     * both top and bottom obstacles have at least the minimum allowed height.
     *
     * @param gap the vertical gap size the player can pass through
     */
    private void spawnPair(float gap) {
        float gapStart = minPipeHeight + (float) (Math.random() *
            (screenHeight - gap - 2 * minPipeHeight));

        obstacles.add(new Obstacle(screenWidth, 0, pipeWidth, gapStart));
        obstacles.add(new Obstacle(screenWidth, gapStart + gap, pipeWidth,
            screenHeight - (gapStart + gap)));
    }

    /**
     * Checks whether the player collides with any active obstacle.
     *
     * The player is passed in as a rectangle (x, y, width, height).
     * Each obstacle checks rectangular overlap using simple AABB collision.
     *
     * @param playerX player's x-position
     * @param playerY player's y-position
     * @param playerWidth player's width
     * @param playerHeight player's height
     * @return true if the player collides with at least one obstacle
     */
    public boolean collidesWith(float playerX, float playerY, float playerWidth, float playerHeight) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.collidesWith(playerX, playerY, playerWidth, playerHeight)) {
                return true;
            }
        }
        return false;
    }

    public List<Obstacle> getPipes() {
        return obstacles;
    }

    /**
     * Resets obstacle state for a new game.
     *
     * All active obstacles are removed and the spawn timer is reset.
     */
    public void reset() {
        obstacles.clear();
        pipeTimer = 0;
    }
}
