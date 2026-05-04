package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.game.GameState;

public class Credits {

    private GameState nextState = null;
    private final String[] lines;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private float scrollY;
    private static final float SCROLL_SPEED = 40f;

    public Credits(BitmapFont font) {
        FileHandle file = Gdx.files.internal("credits.txt");
        String content = file.readString();

        lines = content.split("\\R");
        scrollY = -lines.length * 50;

        this.font = font;
    }

    public void reset() {
        scrollY = -lines.length * 50;
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        scrollY += SCROLL_SPEED * delta;

        float totalHeight = lines.length * 50;

        if (scrollY > Gdx.graphics.getHeight() + totalHeight) {
            scrollY = Gdx.graphics.getHeight() + totalHeight;
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

    public void render(SpriteBatch batch) {
        float x = Gdx.graphics.getWidth() / 2f; // center
        float lineHeight = 50;

        for (int i = 0; i < lines.length; i++) {
            float y = scrollY + i * lineHeight;

            layout.setText(font, lines[i]);
            float centeredX = x - layout.width / 2f;

            font.draw(batch, lines[i], centeredX, y);
        }

        font.draw(batch, "Press SPACE to go back", 100, 80);
    }
}
