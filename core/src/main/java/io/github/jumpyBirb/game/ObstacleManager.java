package io.github.jumpyBirb.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import io.github.jumpyBirb.data.Obstacle;

/**
 * Manages all obstacles currently active in the game.
 *
 * <p>This class is responsible for obstacle-related game logic, such as:
 * <p>Each obstacle is assigned a random texture when spawned.
 * Separate texture pools are used for top and bottom obstacles,
 * allowing visual variation while keeping their roles distinct.
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
 *     <li>spawning new obstacle pairs over time with randomized textures</li>
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
    private final List<ObstaclePair> obstaclePairs = new ArrayList<>();

    public List<Obstacle> getAllObstacles() {
        List<Obstacle> all = new ArrayList<>();
        for (ObstaclePair pair : obstaclePairs) {
            all.addAll(pair.getObstacles());
        }
        return all;
    }

    /**
     * Returns all obstacle pairs currently active in the game.
     *
     * <p>This is primarily used by the renderer so it can draw
     * top and bottom obstacles differently (e.g., cropping textures
     * from different directions).
     *
     * @return list of active obstacle pairs
     */
    public List<ObstaclePair> getPairs() {
        return obstaclePairs;
    }

    private float obstacleTimer = 0f;

    private final float obstacleWidth;
    private final float minObstacleHeight;
    private final float obstacleHeight;
    private final float screenWidth;
    private final float screenHeight;
    private final List<Texture> topTextures;
    private final List<Texture> bottomTextures;

    /**
     * Creates an obstacle manager with screen information and obstacle settings.
     *
     * @param obstacleWidth     width of each spawned obstacle
     * @param minObstacleHeight minimum obstacle height allowed
     * @param screenWidth       width of the game screen
     * @param screenHeight      height of the game screen
     * @param topTextures       list of textures used for top obstacles
     * @param bottomTextures    list of textures used for bottom obstacles
     */
    public ObstacleManager(float obstacleWidth, float minObstacleHeight,
                           float obstacleHeight, float screenWidth, float screenHeight,
                           List<Texture> topTextures, List<Texture> bottomTextures) {

        this.obstacleWidth = obstacleWidth;
        this.minObstacleHeight = minObstacleHeight;
        this.obstacleHeight = obstacleHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.topTextures = topTextures;
        this.bottomTextures = bottomTextures;
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
     * @param delta       time since last frame
     * @param timePlaying total time the current run has been active
     * @param speed       horizontal movement speed of obstacles
     */
    public void update(float delta, float timePlaying, float speed) {
        obstacleTimer += delta;

        float currentGap = Math.max(1.3f, 3.2f - (timePlaying / 20f) * 0.1f);
        float spawnInterval = Math.max(1f, 2f - (timePlaying / 30f));

        if (obstacleTimer > spawnInterval) {
            obstacleTimer = 0;
            spawnPair(currentGap);
        }

        Iterator<ObstaclePair> iter = obstaclePairs.iterator();
        while (iter.hasNext()) {
            ObstaclePair pair = iter.next();

            pair.update(delta, speed);

            if (pair.isOffScreen()) {
                iter.remove();
            }
        }
    }

    /**
     * Spawns a pair of obstacles (top + bottom) with a vertical gap.
     *
     * <p>The bottom obstacle grows upward from the bottom of the screen.
     * The top obstacle grows downward from the top of the screen.
     *
     * <p>The gap position is randomized, but constrained so both obstacles
     * have at least the minimum height.
     *
     * <p>This method ensures both obstacles in a pair share the same X position,
     * so they move together as a single gameplay unit.
     *
     * <p>Each obstacle in the pair is also assigned a random texture
     *  from the corresponding texture list (top or bottom).
     *
     * @param gap vertical gap size the player can pass through
     */
    private void spawnPair(float gap) {
        float gapStart = minObstacleHeight + (float) (Math.random() *
            (screenHeight - gap - 2 * minObstacleHeight));

        Texture bottomTex = bottomTextures.get(
            (int) (Math.random() * bottomTextures.size())
        );

        Texture topTex = topTextures.get(
            (int) (Math.random() * topTextures.size())
        );

        Obstacle bottom = new Obstacle(
            screenWidth,
            gapStart - obstacleHeight,
            obstacleWidth,
            obstacleHeight,
            bottomTex
        );

        Obstacle top = new Obstacle(
            screenWidth,
            gapStart + gap,
            obstacleWidth,
            obstacleHeight,
            topTex
        );

        obstaclePairs.add(new ObstaclePair(top, bottom));
    }

    /**
     * Checks whether the player collides with any active obstacle.
     * <p>
     * The player is passed in as a rectangle (x, y, width, height).
     * Each obstacle checks rectangular overlap using simple AABB collision.
     *
     * @return true if the player collides with at least one obstacle
     */
    public boolean collidesWith(float x, float y, float w, float h) {
        for (ObstaclePair pair : obstaclePairs) {
            if (pair.collidesWith(x, y, w, h)) {
                return true;
            }
        }
        return false;
    }

    public List<ObstaclePair> getObstacles() {
        return obstaclePairs;
    }

    /**
     * Resets obstacle state for a new game.
     * <p>
     * All active obstacles are removed and the spawn timer is reset.
     */
    public void reset() {
        obstaclePairs.clear();
        obstacleTimer = 0;
    }
}
