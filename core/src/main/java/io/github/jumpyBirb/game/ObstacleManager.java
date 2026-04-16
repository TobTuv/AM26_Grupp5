package io.github.jumpyBirb.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObstacleManager {

    List<ObstaclePair> obstacles = new ArrayList<>();

    float obstacleTimer = 0;

    float screenWidth;
    float screenHeight;

    float obstacleWidth = 120;

    Texture obstacleTexture;

    public List<ObstaclePair> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<ObstaclePair> obstacles) {
        this.obstacles = obstacles;
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(float screenWidth) {
        this.screenWidth = screenWidth;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(float screenHeight) {
        this.screenHeight = screenHeight;
    }

    public ObstacleManager(float screenWidth, float screenHeight, Texture texture) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.obstacleTexture = texture;
    }

    public void update(float delta, float speed, float gap, float timePlaying) {

        obstacleTimer += delta;

        float spawnInterval = Math.max(1.3f, 2f - (timePlaying / 40f));

        if (obstacleTimer > spawnInterval) {
            obstacleTimer = 0;
            spawnObstaclePair(gap);
        }

        Iterator<ObstaclePair> it = obstacles.iterator();

        while (it.hasNext()) {
            ObstaclePair pair = it.next();

            pair.update(speed, delta);

            if (pair.isOffScreen()) {
                it.remove();
            }
        }
    }

    private void spawnObstaclePair(float gap) {
        obstacles.add(new ObstaclePair(
            screenWidth,
            screenHeight,
            gap,
            obstacleWidth,
            obstacleTexture
        ));
    }

    public void draw(SpriteBatch batch) {
        for (ObstaclePair o : obstacles) {
            o.draw(batch);
        }
    }

    public boolean checkCollision(float x, float y, float w, float h) {
        for (ObstaclePair o : obstacles) {
            if (o.collides(x, y, w, h)) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        obstacles.clear();
        obstacleTimer = 0;
    }
}
