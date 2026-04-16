package io.github.jumpyBirb.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jumpyBirb.data.Player;
import io.github.jumpyBirb.data.Score;
import io.github.jumpyBirb.game.GameState;
import io.github.jumpyBirb.game.ParallaxBackground;
import io.github.jumpyBirb.game.ObstacleManager;

public record GameRenderer(SpriteBatch batch, BitmapFont font, GameAssets assets, float screenWidth,
                           float screenHeight) {

    public void draw(
        ParallaxBackground background,
        Player player,
        Score score,
        GameState gameState,
        double finalScore,
        float podX,
        float podY,
        float podWidth,
        float podHeight,
        ObstacleManager obstacleManager
    ) {
        ScreenUtils.clear(Color.BLACK);

        batch.begin();

        batch.draw(assets.background, 0, 0, screenWidth, screenHeight);
        background.draw(batch);
        obstacleManager.draw(batch);

        batch.draw(assets.pod, podX, podY, podWidth, podHeight);
        batch.draw(assets.player, player.getX(), player.getY(), player.getWidth(), player.getHeight());

        font.draw(batch, "Score: " + (int) score.getScore(), 270, screenHeight - 10);

        if (gameState == GameState.GAME_OVER) {
            font.draw(batch,
                "GAME OVER!\nYour score: " + (int) finalScore,
                screenWidth / 2 - 80,
                screenHeight / 2);
        }

        batch.end();
    }
}
