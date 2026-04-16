package io.github.jumpyBirb;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PipePair {

    Pipe top;
    Pipe bottom;

    Texture texture;

    public PipePair(float x, float screenHeight, float gap, float width, Texture texture) {
        this.texture = texture;

        float gapStart = (float) (Math.random() * (screenHeight - gap - 100)) + 50;

        bottom = new Pipe(x, 0, width, gapStart);

        top = new Pipe(
            x,
            gapStart + gap,
            width,
            screenHeight - (gapStart + gap)
        );
    }

    public void update(float speed, float delta) {
        top.move(speed, delta);
        bottom.move(speed, delta);
    }

    public void draw(SpriteBatch batch) {
        drawPipe(batch, bottom, false); // bottom
        drawPipe(batch, top, true);     // top
    }

    private void drawPipe(SpriteBatch batch, Pipe p, boolean isTop) {

        float tileHeight = texture.getHeight();

        for (float yOffset = 0; yOffset < p.height; yOffset += tileHeight) {

            float drawHeight = Math.min(tileHeight, p.height - yOffset);

            float drawY;

            if (isTop) {
                // vẽ từ trên xuống
                drawY = p.y + p.height - yOffset - drawHeight;
            } else {
                // draw
                drawY = p.y + yOffset;
            }

            int srcY;

            if (isTop) {
                // top pipe from the bottom of the graphic.
                srcY = texture.getHeight() - (int) drawHeight;
            } else {
                //bottom pipe from the top of the graphic.
                srcY = 0;
            }

            batch.draw(
                texture,
                p.x,
                drawY,
                p.width,
                drawHeight,
                0,
                srcY,
                texture.getWidth(),
                (int) drawHeight,
                false,
                false
            );
        }
    }

    public boolean isOffScreen() {
        return top.isOffScreen() && bottom.isOffScreen();
    }

    public boolean collides(float px, float py, float pw, float ph) {
        return collide(bottom, px, py, pw, ph) ||
            collide(top, px, py, pw, ph);
    }

    private boolean collide(Pipe p, float px, float py, float pw, float ph) {
        return px < p.x + p.width &&
            px + pw > p.x &&
            py < p.y + p.height &&
            py + ph > p.y;
    }
}
