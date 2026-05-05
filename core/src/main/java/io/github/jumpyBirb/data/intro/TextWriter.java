package io.github.jumpyBirb.data.intro;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Reveals text gradually over time (typewriter effect).
 *
 * <p>The text is wrapped before it starts appearing, so words do not jump
 * between lines while the text is being revealed.
 *
 * <p>Each update reveals more characters based on {@code secondsPerCharacter}.
 * The full text can also be skipped instantly.
 */
public class TextWriter {
    private final String wrappedText;
    private final float secondsPerCharacter;

    private int currentIndex = 0;
    private float timer = 0f;

    public TextWriter(String fullText, BitmapFont font, float maxWidth, float secondsPerCharacter) {
        this.wrappedText = wrapText(fullText, font, maxWidth);
        this.secondsPerCharacter = secondsPerCharacter;
    }

    public void update(float delta) {
        if (isFinished()) {
            return;
        }

        timer += delta;

        while (timer >= secondsPerCharacter && !isFinished()) {
            currentIndex++;
            timer -= secondsPerCharacter;
        }
    }

    public String getVisibleText() {
        int safeIndex = Math.min(currentIndex, wrappedText.length());
        return wrappedText.substring(0, safeIndex);
    }

    public boolean isFinished() {
        return currentIndex >= wrappedText.length();
    }

    public void skipToEnd() {
        currentIndex = wrappedText.length();
    }

    private String wrapText(String text, BitmapFont font, float maxWidth) {
        GlyphLayout layout = new GlyphLayout();
        List<String> lines = new ArrayList<>();

        StringBuilder currentLine = new StringBuilder();

        for (String word : text.split(" ")) {
            String testLine;

            if (currentLine.isEmpty()) {
                testLine = word;
            } else {
                testLine = currentLine + " " + word;
            }

            layout.setText(font, testLine);

            if (layout.width > maxWidth && !currentLine.isEmpty()) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return String.join("\n", lines);
    }
}
