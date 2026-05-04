package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import io.github.jumpyBirb.game.GameState;

public class Credits {

    private GameState nextState = null;
    private final String[] lines;
    private final BitmapFont font;

    public Credits() {
        FileHandle file = Gdx.files.internal("credits.txt");
        String content = file.readString();
        lines = content.split("\\R");

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/bgothm.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 40;
        parameter.color = Color.WHITE;

        font = generator.generateFont(parameter);
        generator.dispose();
    }

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

    public void render(SpriteBatch batch) {
        float x = 100;
        float y = Gdx.graphics.getHeight() - 180;
        float lineHeight = 50;

        for (int i = 0; i < lines.length; i++) {
            font.draw(batch, lines[i], x, y - i * lineHeight);
        }

        font.draw(batch, "Press SPACE to go back", x, 80);
    }

    public void dispose() {
        font.dispose();
    }
}
