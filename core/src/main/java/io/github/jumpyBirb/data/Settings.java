package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.game.GameState;

public class Settings {

    private int settingsIndex = 0;
    private final BitmapFont font;
    private boolean inResolutionMenu = false;
    private int resolutionIndex = 0;
    private boolean resolutionChanged = false;

    public boolean consumeResolutionChanged() {
        boolean temp = resolutionChanged;
        resolutionChanged = false;
        return temp;
    }

    private final String[] resolutions = {
        "1280 x 720",
        "1920 x 1080",
        "FULLSCREEN"
    };


    private final String[] items = {"Reset High-Score", "Music ON/OFF", "Sound ON/OFF", "RESOLUTION", "FULLSCREEN", "CREDITS", "Change name", "MENU"};
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

        if (inResolutionMenu) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                resolutionIndex = (resolutionIndex + 1) % resolutions.length;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                resolutionIndex = (resolutionIndex - 1 + resolutions.length) % resolutions.length;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                applyResolution();
                inResolutionMenu = false;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                inResolutionMenu = false;
            }

            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            settingsIndex = (settingsIndex + 1) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            settingsIndex = (settingsIndex - 1 + items.length) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            select();
        }
    }

    private void select() {
        switch (settingsIndex) {
            case 0 -> nextState = GameState.CONFIRM_RESET;
            case 1 -> nextState = GameState.MUSIC;
            case 2 -> nextState = GameState.SOUND;

            case 3 -> inResolutionMenu = true;

            case 4 -> toggleFullscreen();
            case 5 -> nextState = GameState.CREDITS;
            case 6 -> nextState = GameState.NAME_INPUT;
            case 7 -> nextState = GameState.MENU;
        }
    }

    public void render(SpriteBatch batch, boolean music, boolean sound) {
        float startX = 100;
        float startY = Gdx.graphics.getHeight() - 200;
        float lineHeight = font.getLineHeight() + 10;
        String resolutionText = Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight();

        String[] displayItems = {
            "Reset High-Score",
            "Music: " + (music ? "ON" : "OFF"),
            "Sound: " + (sound ? "ON" : "OFF"),
            "Credits",
            "Resolution: " + resolutions[resolutionIndex],
            "Fullscreen: " + (Gdx.graphics.isFullscreen() ? "ON" : "OFF"),
            "Change Name",
            "Menu"
        };

        if (inResolutionMenu) {
            float x = 400;
            float y = Gdx.graphics.getHeight() - 200;
            float line = 40;

            for (int i = 0; i < resolutions.length; i++) {
                String text = (i == resolutionIndex)
                    ? "> " + resolutions[i]
                    : resolutions[i];

                font.draw(batch, text, x, y - i * line);
            }

            font.draw(batch, "SPACE = APPLY / ESC = BACK", x, y - 200);
            return;
        }

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

        resolutionChanged = true;
    }

    private void applyResolution() {

        switch (resolutionIndex) {

            case 0 -> {
                Gdx.graphics.setWindowedMode(1280, 720);
                resolutionChanged = true;
            }

            case 1 -> Gdx.graphics.setWindowedMode(1920, 1080);

            case 2 -> Gdx.graphics.setFullscreenMode(
                Gdx.graphics.getDisplayMode()
            );
        }
    }
}
