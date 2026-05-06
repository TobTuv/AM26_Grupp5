package io.github.jumpyBirb.data.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Align;
import io.github.jumpyBirb.game.InputGate;

public class Intro {

    private final TextWriter text;
    private final BitmapFont font;
    private final GlyphLayout layout;

    private final Texture logoText;

    private IntroPhase phase = IntroPhase.TEXT;
    private float phaseTimer = 0f;
    private float blinkTimer = 0f;
    private boolean waitingForSecondClick = false;

    private static final float WAIT_AFTER_TEXT_TIME = 1f;
    private static final float LOGO_TIME = 20f;
    private static final float LOGO_GROW_TIME = 1f;
    private static final float LOGO_SKIP_DELAY = 3f;

    private final InputGate inputGate = new InputGate();

    public Intro(Texture logoText, BitmapFont font) {
        FileHandle file = Gdx.files.internal("intro.txt");
        String content = file.readString();

        this.logoText = logoText;
        this.font = font;

        text = new TextWriter(content, 0.04f);
        layout = new GlyphLayout();
    }

    public void update(float delta) {
        inputGate.update(delta);

        switch (phase) {

            case TEXT:
                text.update(delta);
                phaseTimer += delta;
                blinkTimer += delta;

                if (text.isFinished()) {
                    phase = IntroPhase.WAIT_AFTER_TEXT;
                    phaseTimer = 0f;
                }
                break;

            case WAIT_AFTER_TEXT:
                phaseTimer += delta;

                if (phaseTimer >= WAIT_AFTER_TEXT_TIME) {
                    phase = IntroPhase.LOGO;
                    phaseTimer = 0f;
                    blinkTimer = 0f;
                }
                break;

            case LOGO:
                phaseTimer += delta;
                blinkTimer += delta;

                if (phaseTimer >= LOGO_TIME) {
                    phase = IntroPhase.FINISHED;
                }
                break;

            case FINISHED:
                break;

            default:
                break;
        }
    }

    public void render(SpriteBatch batch, float uiWidth, float uiHeight) {
        if (phase == IntroPhase.TEXT || phase == IntroPhase.WAIT_AFTER_TEXT) {
            renderText(batch, uiWidth, uiHeight);
        }

        if (phase == IntroPhase.LOGO) {
            renderLogo(batch, uiWidth, uiHeight);
        }
    }

    private void renderText(SpriteBatch batch, float uiWidth, float uiHeight) {
        float x = 80;
        float y = uiHeight - 100;
        float width = uiWidth - 160;

        layout.setText(
            font,
            text.getVisibleText(),
            Color.WHITE,
            width,
            Align.left,
            true
        );

        float alpha = Math.min(phaseTimer / 1.5f, 1f);

        font.setColor(1, 1, 1, alpha);
        font.draw(batch, layout, x, y);
        font.setColor(Color.WHITE);

        if (waitingForSecondClick) {

            String msg = "Press SPACE to skip";

            layout.setText(font, msg);

            float textX = uiWidth / 2f - layout.width / 2f;
            float textY = 60;

            float blinkAlpha = Math.abs((float)Math.sin(blinkTimer * 3));

            font.setColor(1, 1, 1, blinkAlpha);
            font.draw(batch, msg, textX, textY);
            font.setColor(Color.WHITE);
        }
    }

    private void renderLogo(SpriteBatch batch, float uiWidth, float uiHeight) {
        float progress = phaseTimer / LOGO_GROW_TIME;
        progress = Math.min(progress, 1f);

        float maxWidth = 920f;
        float maxHeight = 453f;

        float width = maxWidth * progress;
        float height = maxHeight * progress;

        float logoX = uiWidth / 2f - width / 2f;
        float logoY = uiHeight / 2f - height / 2f;

        batch.draw(logoText, logoX, logoY, width, height);

        if (phaseTimer >= LOGO_SKIP_DELAY) {

            String msg = "Press SPACE to begin";

            layout.setText(font, msg);

            float textX = uiWidth / 2f - layout.width / 2f;
            float textY = 100;

            float blinkTime = phaseTimer - LOGO_SKIP_DELAY;
            float alpha = (float)Math.abs(Math.sin(blinkTime * 3));

            font.setColor(1, 1, 1, alpha);
            font.draw(batch, msg, textX, textY);
            font.setColor(Color.WHITE);
        }
    }

    public void skip() {
        if (!inputGate.canAcceptInput()) {
            return;
        }

        if (phase == IntroPhase.TEXT) {


            if (!text.isFinished()) {

                if (!waitingForSecondClick) {

                    waitingForSecondClick = true;
                    blinkTimer = 0f;
                    inputGate.block(0.2f);
                } else {

                    text.skipToEnd();
                    waitingForSecondClick = false;

                    phase = IntroPhase.WAIT_AFTER_TEXT;
                    phaseTimer = 0f;

                    inputGate.block(0.2f);
                }

                return;
            }
        } else if (phase == IntroPhase.LOGO && phaseTimer >= LOGO_SKIP_DELAY) {
            phase = IntroPhase.FINISHED;
        }
    }
    public boolean isFinished() {
        return phase == IntroPhase.FINISHED;
    }

    private enum IntroPhase {
        TEXT,
        WAIT_AFTER_TEXT,
        LOGO,
        FINISHED
    }
}


