package io.github.jumpyBirb.graphics;

import com.badlogic.gdx.graphics.Texture;

/**
 * Holds and manages all textures used in the game.
 *
 * <p>This class centralizes asset loading so that:
 * <ul>
 *     <li>textures are created in one place</li>
 *     <li>other classes (like Main or GameRenderer) can easily access them</li>
 *     <li>resource cleanup is handled consistently</li>
 * </ul>
 *
 * <p>Why this class exists:
 * Previously, textures were created directly in Main, which made it cluttered
 * and harder to manage. By moving them into this class, we separate resource
 * handling from game logic.
 *
 * <p>Important note:
 * Each Texture allocates memory (often on the GPU). Therefore, all textures
 * must be properly disposed when the game closes to avoid memory leaks.
 *
 * <p>Usage:
 * <ul>
 *     <li>Create one instance of GameAssets in Main</li>
 *     <li>Pass it to classes that need textures (e.g. GameRenderer)</li>
 *     <li>Call {@code dispose()} when the game shuts down</li>
 * </ul>
 *
 * <p>Possible future improvements:
 * <ul>
 *     <li>use LibGDX AssetManager for asynchronous loading</li>
 *     <li>group assets by type (UI, player, obstacles, etc.)</li>
 *     <li>add sounds/music here as well</li>
 * </ul>
 */
public class GameAssets {
    public final Texture background = new Texture("background.png");
    public final Texture player = new Texture("player.png");
    public final Texture skyscraper = new Texture("skyscraper.png");
    public final Texture pod = new Texture("podPlatform.png");
    public final Texture parallax1 = new Texture("clouds-parallax1-3000x1080.png");
    public final Texture parallax2 = new Texture("city-parallax2-1920x1080.png");
    public final Texture parallax3 = new Texture("smog-parallax3-3000x1080.png");

    /**
     * Releases all textures from memory.
     *
     * <p>This method MUST be called when the game closes.
     * Otherwise, memory (especially GPU memory) will leak.
     */
    public void dispose() {
        background.dispose();
        player.dispose();
        skyscraper.dispose();
        pod.dispose();
        parallax1.dispose();
        parallax2.dispose();
        parallax3.dispose();
    }
}
