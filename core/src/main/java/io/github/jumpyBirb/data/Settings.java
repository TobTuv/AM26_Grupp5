package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.game.GameState;

public class Settings {

    private int settingsIndex = 0;
    private final BitmapFont font;

    private final String[] items = { "Reset High-Score", "Music ON/OFF", "Sound ON/OFF", "CREDITS", "Change name", "MENU" };
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
            case 0 -> nextState = GameState.RESET_SCORE;
            case 1 -> nextState = GameState.MUSIC;
            case 2 -> nextState = GameState.SOUND;
            case 3 -> nextState = GameState.CREDITS;
            case 4 -> nextState = GameState.NAME_INPUT;
            case 5 -> nextState = GameState.MENU;
        }
    }

    public void render(SpriteBatch batch, boolean music, boolean sound) {
        float startX = 100;
        float startY = Gdx.graphics.getHeight() - 200;
        float lineHeight = font.getLineHeight() + 10;

        String[] displayItems = {
            "Reset High-Score",
            "Music: " + (music ? "ON" : "OFF"),
            "Sound: " + (sound ? "ON" : "OFF"),
            "Credits",
            "Change Name",
            "Menu"
        };

        for (int i = 0; i < displayItems.length; i++) {
            String text = (i == settingsIndex) ? "> " + displayItems[i] : displayItems[i];
            font.draw(batch, text, startX, startY - i * lineHeight);
        }
    }
}
