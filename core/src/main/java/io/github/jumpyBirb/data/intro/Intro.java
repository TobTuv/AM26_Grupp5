package io.github.jumpyBirb.data.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

public class Intro {

    private TextWriter text;
    private BitmapFont font;
    private GlyphLayout layout;

    private Texture logo;

    private IntroPhase phase = IntroPhase.TEXT;
    private float phaseTimer = 0f;

    private static final float WAIT_AFTER_TEXT_TIME = 1f;
    private static final float LOGO_TIME = 20f;
    private static final float LOGO_GROW_TIME = 1f;

    public Intro(Texture logo) {
        FileHandle file = Gdx.files.internal("intro.txt");
        String content = file.readString();

        this.logo = logo;

        text = new TextWriter(content, 0.05f);
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
        switch (phase) {

            case TEXT -> {
                text.update(delta);

                if (text.isFinished()) {
                    phase = IntroPhase.WAIT_AFTER_TEXT;
                    phaseTimer = 0f;
                }
            }

            case WAIT_AFTER_TEXT -> {
                phaseTimer += delta;

                if (phaseTimer >= WAIT_AFTER_TEXT_TIME) {
                    phase = IntroPhase.LOGO;
                    phaseTimer = 0f;
                }
            }

            case LOGO -> {
                phaseTimer += delta;

                if (phaseTimer >= LOGO_TIME) {
                    phase = IntroPhase.FINISHED;
                }
            }

            case FINISHED -> {
                // inget
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (phase == IntroPhase.TEXT || phase == IntroPhase.WAIT_AFTER_TEXT) {
            renderText(batch);
        }

        if (phase == IntroPhase.LOGO) {
            renderLogo(batch);
        }
    }

    private void renderText(SpriteBatch batch) {
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

    private void renderLogo(SpriteBatch batch) {
        float progress = phaseTimer / LOGO_GROW_TIME;
        progress = Math.min(progress, 1f);

        float maxWidth = 735f;
        float maxHeight = 555f;

        float width = maxWidth * progress;
        float height = maxHeight * progress;

        float x = Gdx.graphics.getWidth() / 2f - width / 2f;
        float y = Gdx.graphics.getHeight() / 2f - height / 2f;

        batch.draw(logo, x, y, width, height);
    }

    public void skip() {
        if (phase == IntroPhase.TEXT) {
            text.skipToEnd();
        } else if (phase == IntroPhase.LOGO) {
            phase = IntroPhase.FINISHED;
        }
    }

    public boolean isFinished() {
        return phase == IntroPhase.FINISHED;
    }

    public void dispose() {
        font.dispose();
    }

    private enum IntroPhase {
        TEXT,
        WAIT_AFTER_TEXT,
        LOGO,
        FINISHED
    }
}


