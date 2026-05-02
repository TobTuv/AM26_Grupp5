package io.github.jumpyBirb.game;

import com.badlogic.gdx.graphics.Texture;
import io.github.jumpyBirb.Main;
import io.github.jumpyBirb.data.Obstacle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ObstacleManagerTest {

    private ObstacleManager createManagerWithObstacle() {
        ObstacleManager manager = new ObstacleManager(50f,
            50f,
            200f,
            800f,
            600f,
            new ArrayList<>(),
            new ArrayList<>()
        );
        Texture dummy = null;
        Obstacle obstacle = new Obstacle(
            100, 100,   // x, y
            50, 200,    // width, height
            dummy
        );
        ObstaclePair pair = new ObstaclePair(obstacle, obstacle);

        manager.getPairs().add(pair);
        return manager;
    }

    @Test
    void updateShouldNotCrash() {
        ObstacleManager manager = createManagerWithObstacle();

        assertDoesNotThrow(() ->
            manager.update(0.16f, 1f, 200f)
        );
    }

    @Test
    void shouldDetectCollision() {

        ObstacleManager manager = createManagerWithObstacle();

        // player hits obstacle (100,100)
        boolean result = manager.collidesWith(100, 100, 10, 10);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenNoCollision() {

        ObstacleManager manager = createManagerWithObstacle();
        boolean result = manager.collidesWith(500, 500, 10, 10);

        assertFalse(result);
    }

}
