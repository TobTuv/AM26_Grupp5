package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.game.GameState;

import java.util.Arrays;
import java.util.Collections;

public class Credits {

    private GameState nextState = null;
    private final String[] lines;
    private final BitmapFont creditFont;
    private final BitmapFont menuFont;
    private final GlyphLayout layout = new GlyphLayout();
    private float scrollY;
    private static final float LINE_HEIGHT = 50f;
    private static final float END_Y = 500f;
    private static final float SCROLL_SPEED = 80f;

    public Credits(BitmapFont creditFont, BitmapFont menuFont) {
        FileHandle file = Gdx.files.internal("credits.txt");
        String content = file.readString();

        lines = content.split("\\R");
        scrollY = -lines.length * 50;

        this.creditFont = creditFont;
        this.menuFont = menuFont;

        Collections.reverse(Arrays.asList(lines));
    }

    public void reset() {
        scrollY = -lines.length * LINE_HEIGHT;
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();

        scrollY += SCROLL_SPEED * delta;

        if (scrollY > END_Y) {
            scrollY = END_Y;
        }

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

    public void render(SpriteBatch batch, float startX, float rightX) {
        for (int i = 0; i < lines.length; i++) {
            float y = scrollY + i * LINE_HEIGHT;
            creditFont.draw(batch, lines[i], startX, y);
        }

        String backText = "> settings";

        layout.setText(menuFont, backText);
        float xUiText = rightX - layout.width;

        menuFont.draw(batch, backText, xUiText, 100);
    }
}
