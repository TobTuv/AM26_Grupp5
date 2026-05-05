package io.github.jumpyBirb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.core.GameState;

/**
 * Handles a simple selectable menu.
 *
 * <p>The menu stores a list of labels and matching game states.
 * It handles navigation input, renders the selected option,
 * and exposes the selected state through {@code consumeNextState()}.
 */
public class Menu {

    private int menuIndex = 0;
    private final String[] items;
    private final GameState[] states;
    private GameState nextState = null;
    private final BitmapFont font;

    public Menu(String[] items, GameState[] states, BitmapFont font) {
        this.items = items;
        this.states = states;
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
            menuIndex = (menuIndex + 1) % items.length;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            menuIndex = (menuIndex + 1) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            menuIndex = (menuIndex - 1 + items.length) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            select();
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            select();
        }
    }

    private void select() {
        nextState = states[menuIndex];
    }

    public void render(SpriteBatch batch, float startX, float startY) {
        float lineHeight = font.getLineHeight() + 10;

        for (int i = 0; i < items.length; i++) {
            String text = (i == menuIndex) ? "> " + items[i] : items[i];
            font.draw(batch, text, startX, startY - i * lineHeight);
        }
    }
}
