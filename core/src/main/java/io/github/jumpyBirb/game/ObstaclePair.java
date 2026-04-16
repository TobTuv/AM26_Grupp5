package io.github.jumpyBirb.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.data.Obstacle;

public class ObstaclePair {

    Obstacle top;
    Obstacle bottom;

    Texture texture;

    public ObstaclePair(float x, float screenHeight, float gap, float width, Texture texture) {
        this.texture = texture;

        float gapStart = (float) (Math.random() * (screenHeight - gap - 100)) + 50;

        bottom = new Obstacle(x, 0, width, gapStart);

        top = new Obstacle(
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
        drawObstacle(batch, bottom, false); // bottom
        drawObstacle(batch, top, true);     // top
    }

    private void drawObstacle(SpriteBatch batch, Obstacle o, boolean isTop) {

        float tileHeight = texture.getHeight();

        for (float yOffset = 0; yOffset < o.getHeight(); yOffset += tileHeight) {

            float drawHeight = Math.min(tileHeight, o.getHeight() - yOffset);

            float drawY;

            if (isTop) {
                drawY = o.getY() + o.getHeight() - yOffset - drawHeight;
            } else {
                // draw
                drawY = o.getY() + yOffset;
            }

            int srcY;

            if (isTop) {
                // top obstacle from the bottom of the graphic.
                srcY = texture.getHeight() - (int) drawHeight;
            } else {
                //bottom obstacle from the top of the graphic.
                srcY = 0;
            }

            batch.draw(
                texture,
                o.getX(),
                drawY,
                o.getWidth(),
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

    public boolean collides(float ox, float oy, float ow, float oh) {
        return collide(bottom, ox, oy, ow, oh) ||
            collide(top, ox, oy, ow, oh);
    }

    private boolean collide(Obstacle o, float ox, float oy, float ow, float oh) {
        return ox < o.getX() + o.getWidth() &&
            ox + ow > o.getX() &&
            oy < o.getY() + o.getHeight() &&
            oy + oh > o.getY();
    }
}
