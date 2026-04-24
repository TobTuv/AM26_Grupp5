package io.github.jumpyBirb.data.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

public class Intro {

    private TextWriter text;
    private BitmapFont font;
    private GlyphLayout layout;

    public Intro() {
        FileHandle file = Gdx.files.internal("intro.txt");
        String content = file.readString();

        text = new TextWriter(content, 0.04f);
        layout = new GlyphLayout();

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/bgothm.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 20;
        parameter.color = Color.WHITE;

        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public void update(float delta) {
        text.update(delta);
    }

    public void render(SpriteBatch batch) {
        float x = 80;
        float y = Gdx.graphics.getHeight() - 100;
        float width = Gdx.graphics.getWidth() - 160;

        layout.setText(
            font,
            text.getVisibleText(),
            Color.WHITE,
            width,
            Align.left,
            true
        );

        font.draw(batch, layout, x, y);
    }

    public void skip() {
        text.skipToEnd();
    }

    public boolean isFinished() {
        return text.isFinished();
    }

    public void dispose() {
        font.dispose();
    }
}
