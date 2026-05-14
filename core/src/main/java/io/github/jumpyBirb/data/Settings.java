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

            case 0:
                nextState = GameState.CONFIRM_RESET;
                break;

            case 1:
                nextState = GameState.MUSIC;
                break;

            case 2:
                nextState = GameState.SOUND;
                break;

            case 3:
                toggleFullscreen();
                break;

            case 4:
                nextState = GameState.CREDITS;
                break;

            case 5:
                nextState = GameState.NAME_INPUT;
                break;

            case 6:
                nextState = GameState.MENU;
                break;

            default:
                nextState = null;
                break;
        }
    }

    public void render(SpriteBatch batch, float startX, float startY, boolean music, boolean sound) {

        float lineHeight = font.getLineHeight() + 10;

        String[] displayItems = {
            "Reset High-Score",
            "Music: " + (music ? "ON" : "OFF"),
            "Sound: " + (sound ? "ON" : "OFF"),
            "Fullscreen ON/OFF ",
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
