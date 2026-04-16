package io.github.jumpyBirb;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PipeManager {

    List<PipePair> pipes = new ArrayList<>();

    float pipeTimer = 0;

    float screenWidth;
    float screenHeight;

    float pipeWidth = 120;

    Texture pipeTexture;

    public PipeManager(float screenWidth, float screenHeight, Texture texture) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.pipeTexture = texture;
    }

    public void update(float delta, float speed, float gap, float timePlaying) {

        pipeTimer += delta;

        float spawnInterval = Math.max(1.3f, 2f - (timePlaying / 40f));

        if (pipeTimer > spawnInterval) {
            pipeTimer = 0;
            spawnPipePair(gap);
        }

        Iterator<PipePair> it = pipes.iterator();

        while (it.hasNext()) {
            PipePair pair = it.next();

            pair.update(speed, delta);

            if (pair.isOffScreen()) {
                it.remove();
            }
        }
    }

    private void spawnPipePair(float gap) {
        pipes.add(new PipePair(
            screenWidth,
            screenHeight,
            gap,
            pipeWidth,
            pipeTexture
        ));
    }

    public void draw(SpriteBatch batch) {
        for (PipePair p : pipes) {
            p.draw(batch);
        }
    }

    public boolean checkCollision(float x, float y, float w, float h) {
        for (PipePair p : pipes) {
            if (p.collides(x, y, w, h)) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        pipes.clear();
        pipeTimer = 0;
    }
}
