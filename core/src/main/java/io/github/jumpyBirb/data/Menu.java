package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.game.GameState;

public class Menu {

    private int menuIndex = 0;

    private final String[] items = { "Start", "High Score", "Settings" };
    private GameState nextState = null;

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
            menuIndex = (menuIndex + 1) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            menuIndex = (menuIndex - 1 + items.length) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            select();
        }
    }

    private void select() {
        switch (menuIndex) {
            case 0 -> nextState = GameState.NAME_INPUT;
            case 1 -> nextState = GameState.HIGH_SCORE;
            case 2 -> nextState = GameState.SETTINGS;
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        float startX = 100;
        float startY = Gdx.graphics.getHeight() - 200;
        float lineHeight = 60;

        for (int i = 0; i < items.length; i++) {
            String text = (i == menuIndex) ? "> " + items[i] : items[i];
            font.draw(batch, text, startX, startY - i * lineHeight);
        }
    }
}