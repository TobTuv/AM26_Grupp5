package io.github.jumpyBirb.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ParallaxBackground {
    private final Texture layer1;
    private final Texture layer2;
    private final Texture layer3;

    private float x1, x2, x3;
    private float speed1, speed2, speed3;

    private float screenWidth;
    private float screenHeight;

    public ParallaxBackground(Texture layer1, Texture layer2, Texture layer3,
                              float screenWidth, float screenHeight) {
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        speed1 = 20f;
        speed2 = 40f;
        speed3 = 80f;
    }

    public void update(float delta) {
        x1 -= speed1 * delta;
        x2 -= speed2 * delta;
        x3 -= speed3 * delta;

        if (x1 <= -screenWidth) x1 = 0;
        if (x2 <= -screenWidth) x2 = 0;
        if (x3 <= -screenWidth) x3 = 0;
    }

    public void draw(SpriteBatch batch) {
        drawLayer(batch, layer1, x1);
        drawLayer(batch, layer2, x2);
        drawLayer(batch, layer3, x3);
    }

    private void drawLayer(SpriteBatch batch, Texture texture, float x) {
        batch.draw(texture, x, 0, screenWidth, screenHeight);
        batch.draw(texture, x + screenWidth, 0, screenWidth, screenHeight);
    }
}
