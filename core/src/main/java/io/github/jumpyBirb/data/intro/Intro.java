package io.github.jumpyBirb.data.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

public class Intro {

    private final TextWriter text;
    private final BitmapFont font;
    private final GlyphLayout layout;

    private final Texture logoText;

    private IntroPhase phase = IntroPhase.TEXT;
    private float phaseTimer = 0f;
    private float blinkTimer = 0f;


    private static final float WAIT_AFTER_TEXT_TIME = 1f;
    private static final float LOGO_TIME = 20f;
    private static final float LOGO_GROW_TIME = 1f;
    private static final float LOGO_SKIP_DELAY = 3f;

    public Intro(Texture logoText) {
        FileHandle file = Gdx.files.internal("intro.txt");
        String content = file.readString();

        this.logoText = logoText;

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
                    blinkTimer = 0f;
                }
            }

            case LOGO -> {
                phaseTimer += delta;
                blinkTimer += delta;

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

        float maxWidth = 920f;
        float maxHeight = 453f;

        float width = maxWidth * progress;
        float height = maxHeight * progress;

        float logoX = Gdx.graphics.getWidth() / 2f - width / 2f;
        float logoY = Gdx.graphics.getHeight() / 2f - height / 2f;

        batch.draw(logoText, logoX, logoY, width, height);

        if (phaseTimer >= LOGO_SKIP_DELAY) {

            String msg = "Press SPACE to skip";

            layout.setText(font, msg);

            float textX = Gdx.graphics.getWidth() / 2f - layout.width / 2f;
            float textY = 100;

            float alpha = (float)Math.abs(Math.sin(blinkTimer * 3));

            font.setColor(1, 1, 1, alpha);
            font.draw(batch, msg, textX, textY);
            font.setColor(Color.WHITE);
        }
    }

    public void skip() {
        if (phase == IntroPhase.TEXT) {
            text.skipToEnd();
        } else if (phase == IntroPhase.LOGO && phaseTimer >= LOGO_SKIP_DELAY) {
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


