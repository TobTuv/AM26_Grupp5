package io.github.jumpyBirb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.core.GameState;

/**
 * Handles the settings menu.
 *
 * <p>This class manages:
 * <ul>
 *     <li>menu navigation (up/down/select)</li>
 *     <li>triggering actions (e.g. reset score, music/sound, fullscreen)</li>
 *     <li>returning the next {@link GameState} when a menu option is selected</li>
 * </ul>
 *
 * <p>Most options are exposed via {@code consumeNextState()},
 * which allows Main to decide what to do.
 * Fullscreen is toggled directly in this class.
 *
 * <p>Usage:
 * <ul>
 *     <li>{@code update()} handles input</li>
 *     <li>{@code render(...)} draws the menu</li>
 *     <li>{@code consumeNextState()} returns the selected action</li>
 * </ul>
 */
public class Settings {

    private int settingsIndex = 0;
    private final BitmapFont font;
    private final String[] items = {"Reset High-Score", "Music ON/OFF", "Sound ON/OFF", "FULLSCREEN ON/OFF", "CREDITS", "Change name", "MENU"};
    private GameState nextState = null;

    public Settings(BitmapFont font) {
        this.font = font;
    }

    public void update() {
        handleInput();
    }

    public GameState consumeNextState() {
        GameState state = nextState;
        nextState = null;
        return state;
    }

    private void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            settingsIndex = (settingsIndex + 1) % items.length;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            settingsIndex = (settingsIndex + 1) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            settingsIndex = (settingsIndex - 1 + items.length) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            select();
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            select();
        }
    }

    private void select() {
        switch (settingsIndex) {
            case 0 -> nextState = GameState.CONFIRM_RESET;
            case 1 -> nextState = GameState.MUSIC;
            case 2 -> nextState = GameState.SOUND;
            case 3 -> toggleFullscreen();
            case 4 -> nextState = GameState.CREDITS;
            case 5 -> nextState = GameState.NAME_INPUT;
            case 6 -> nextState = GameState.MENU;
        }
    }

    public void render(SpriteBatch batch, float startX, float startY, boolean music, boolean sound) {

        float lineHeight = font.getLineHeight() + 10;

        String[] displayItems = {
            "Reset High-Score",
            "Music: " + (music ? "ON" : "OFF"),
            "Sound: " + (sound ? "ON" : "OFF"),
            "Fullscreen: " + (Gdx.graphics.isFullscreen() ? "ON" : "OFF"),
            "Credits",
            "Change Name",
            "Menu"
        };

        for (int i = 0; i < displayItems.length; i++) {
            String text = (i == settingsIndex) ? "> " + displayItems[i] : displayItems[i];
            font.draw(batch, text, startX, startY - i * lineHeight);
        }
    }

    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(1280, 720);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }
}
