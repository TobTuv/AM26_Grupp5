package io.github.jumpyBirb.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import io.github.jumpyBirb.data.Pipe;

public class PipeManager {
    private List<Pipe> obstacles = new ArrayList<>();
    private float pipeTimer = 0f;

    private final float pipeWidth;
    private final float minPipeHeight;
    private final float screenWidth;
    private final float screenHeight;

    public PipeManager(float pipeWidth, float minPipeHeight, float screenWidth, float screenHeight) {
        this.pipeWidth = pipeWidth;
        this.minPipeHeight = minPipeHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void update(float delta, float timePlaying, float speed) {
        pipeTimer += delta;

        float currentGap = Math.max(90f, 160f - (timePlaying / 5f) * 2);
        float spawnInterval = Math.max(1.3f, 2f - (timePlaying / 40f));

        if (pipeTimer > spawnInterval) {
            pipeTimer = 0;
            spawnPair(currentGap);
        }

        Iterator<Pipe> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe obstacle = iter.next();
            obstacle.update(delta, speed);

            if (obstacle.isOffScreen()) {
                iter.remove();
            }
        }
    }

    private void spawnPair(float gap) {
        float gapStart = minPipeHeight + (float) (Math.random() *
            (screenHeight - gap - 2 * minPipeHeight));

        obstacles.add(new Pipe(screenWidth, 0, pipeWidth, gapStart));
        obstacles.add(new Pipe(screenWidth, gapStart + gap, pipeWidth,
            screenHeight - (gapStart + gap)));
    }

    public boolean collidesWith(float playerX, float playerY, float playerWidth, float playerHeight) {
        for (Pipe obstacle : obstacles) {
            if (obstacle.collidesWith(playerX, playerY, playerWidth, playerHeight)) {
                return true;
            }
        }
        return false;
    }

    public List<Pipe> getPipes() {
        return obstacles;
    }

    public void reset() {
        obstacles.clear();
        pipeTimer = 0;
    }
}
