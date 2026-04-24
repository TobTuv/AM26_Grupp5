package io.github.jumpyBirb.data.intro;

public class TextWriter {
    private final String fullText;
    private final float secondsPerCharacter;

    private int currentIndex = 0;
    private float timer = 0f;

    public TextWriter(String fullText, float secondsPerCharacter) {
        this.fullText = fullText;
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
        return fullText.substring(0, currentIndex);
    }

    public boolean isFinished() {
        return currentIndex >= fullText.length();
    }

    public void skipToEnd() {
        currentIndex = fullText.length();
    }

    public void reset() {
        currentIndex = 0;
        timer = 0f;
    }
}
