package io.github.jumpyBirb.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Handles the parallax background effect in the game.
 *
 * <p>Parallax scrolling means that multiple background layers move at different speeds
 * to create a sense of depth. Objects that are "far away" move slower, while objects
 * that are "closer" move faster.
 *
 * <p>This class manages:
 * <ul>
 *     <li>three background layers (textures)</li>
 *     <li>their horizontal positions</li>
 *     <li>their movement speeds</li>
 *     <li>looping the background seamlessly</li>
 * </ul>
 *
 * <p>Why this class exists:
 * Previously, all parallax logic (positions, speeds, drawing) was inside Main.
 * That made Main large and harder to understand. By moving this into its own class,
 * we isolate background behavior and make the code easier to maintain.
 *
 * <p>How it works:
 * <ul>
 *     <li>Each layer has its own horizontal position (x1, x2, x3)</li>
 *     <li>Each layer moves left every frame at its own speed</li>
 *     <li>When a layer has completely moved off screen, it is reset to create a loop</li>
 *     <li>Each layer is drawn twice to ensure seamless scrolling</li>
 * </ul>
 *
 * <p>Layer speeds:
 * <ul>
 *     <li>layer1 → slowest (furthest away)</li>
 *     <li>layer2 → medium</li>
 *     <li>layer3 → fastest (closest to player)</li>
 * </ul>
 *
 * <p>Note:
 * This class handles both updating and rendering. That makes it a mix of game logic
 * and graphics, which is acceptable here for simplicity, but could be split later
 * if needed.
 */
public class ParallaxBackground {
    private final Texture layer1;
    private final Texture layer2;
    private final Texture layer3;

    private float x1, x2, x3;
    private float speed1, speed2, speed3;

    private float screenWidth;
    private float screenHeight;

    /**
     * Creates a parallax background with three layers.
     *
     * @param layer1 furthest background layer (slowest)
     * @param layer2 middle layer
     * @param layer3 closest layer (fastest)
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public ParallaxBackground(Texture layer1, Texture layer2, Texture layer3,
                              float screenWidth, float screenHeight) {
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        speed1 = 20f;
        speed2 = 40f;
        speed3 = 80f;
    }

    /**
     * Updates the position of each background layer.
     *
     * <p>Each layer moves left based on its speed and delta time.
     * When a layer has fully moved off screen, it resets to x = 0
     * to create a seamless looping effect.
     *
     * @param delta time since last frame
     */
    public void update(float delta) {
        x1 -= speed1 * delta;
        x2 -= speed2 * delta;
        x3 -= speed3 * delta;

        if (x1 <= -screenWidth) x1 = 0;
        if (x2 <= -screenWidth) x2 = 0;
        if (x3 <= -screenWidth) x3 = 0;
    }

    /**
     * Draws all background layers.
     *
     * <p>Each layer is drawn twice:
     * <ul>
     *     <li>once at its current position</li>
     *     <li>once offset by screen width</li>
     * </ul>
     *
     * <p>This ensures that when one image scrolls off screen,
     * the next one is already in place, creating a seamless loop.
     *
     * @param batch SpriteBatch used for rendering
     */
    public void draw(SpriteBatch batch) {
        drawLayer(batch, layer1, x1);
        drawLayer(batch, layer2, x2);
        drawLayer(batch, layer3, x3);
    }

    /**
     * Draws a single background layer twice to create a continuous scrolling effect.
     *
     * @param batch SpriteBatch used for rendering
     * @param texture the texture to draw
     * @param x current horizontal position of the layer
     */
    private void drawLayer(SpriteBatch batch, Texture texture, float x) {
        batch.draw(texture, x, 0, screenWidth, screenHeight);
        batch.draw(texture, x + screenWidth, 0, screenWidth, screenHeight);
    }
}
