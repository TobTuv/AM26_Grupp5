package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.game.GameState;

public class Credits {

    private GameState nextState = null;

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            nextState = GameState.SETTINGS;
        }
    }

    public GameState consumeNextState() {
        GameState state = nextState;
        nextState = null;
        return state;
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        float x = 100;
        float y = Gdx.graphics.getHeight() - 180;
        float lineHeight = 50;

        FileHandle file = Gdx.files.internal("intro.txt");
        String content = file.readString();


    }
}
