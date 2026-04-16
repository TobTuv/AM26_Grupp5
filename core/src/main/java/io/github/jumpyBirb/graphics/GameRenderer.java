package io.github.jumpyBirb.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jumpyBirb.data.Obstacle;
import io.github.jumpyBirb.data.Player;
import io.github.jumpyBirb.data.Score;
import io.github.jumpyBirb.game.GameState;
import io.github.jumpyBirb.game.ObstaclePair;
import io.github.jumpyBirb.game.ParallaxBackground;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;

import java.util.List;


/**
 * Responsible for drawing the current game state to the screen.
 *
 * <p>This class handles rendering only. It does not update game logic,
 * move objects, handle collisions, or process input.
 *
 * <p>Its job is to take the current state of the game and draw:
 * <ul>
 *     <li>background and parallax layers</li>
 *     <li>starting platform</li>
 *     <li>player</li>
 *     <li>obstacles</li>
 *     <li>score text</li>
 *     <li>game over text</li>
 * </ul>
 *
 * <p>Why this class exists:
 * Previously, drawing code was inside Main, mixed together with update logic,
 * input handling, and game state changes. By moving rendering into its own class,
 * the code becomes easier to read and easier to maintain.
 *
 * <p>This class is implemented as a record because it mainly stores a small set
 * of rendering-related dependencies:
 * <ul>
 *     <li>{@code SpriteBatch} for drawing</li>
 *     <li>{@code BitmapFont} for text</li>
 *     <li>{@code GameAssets} for textures</li>
 *     <li>{@code OrthographicCamera} for setting the viewport size</li>
 *     <li>screen size values for positioning and scaling</li>
 * </ul>
 *
 * <p>Important design note:
 * GameRenderer should only draw what it is told to draw.
 * It should not decide game rules such as when the game starts, when the player dies,
 * or how obstacles move.
 */
public record GameRenderer(SpriteBatch batch,
                           BitmapFont font,
                           GameAssets assets,
                           OrthographicCamera camera,
                           float worldWidth,
                           float worldHeight) {

    /**
     * Draws one full frame of the game.
     *
     * <p>This method:
     * <ul>
     *     <li>clears the screen</li>
     *     <li>draws the static background</li>
     *     <li>draws the parallax background layers</li>
     *     <li>draws the platform, player, and obstacles</li>
     *     <li>draws the current score</li>
     *     <li>draws game over text if the game is over</li>
     * </ul>
     *
     * <p>Everything passed in here should already have been updated elsewhere.
     * This method should only render the current state, not modify it.
     *
     * @param background parallax background system
     * @param player current player object
     * @param score score object used for displaying current score
     * @param gameState current game state
     * @param finalScore score to display on game over
     * @param podX x-position of the starting platform
     * @param podY y-position of the starting platform
     * @param podWidth width of the starting platform
     * @param podHeight height of the starting platform
     */
    public void draw(
        ParallaxBackground background,
        Player player,
        List<ObstaclePair> pairs,
        Score score,
        GameState gameState,
        double finalScore,
        float podX,
        float podY,
        float podWidth,
        float podHeight
    ) {
        // Clear the screen before drawing the next frame.
        ScreenUtils.clear(Color.BLACK);

        //describes where things in the game world should be rendered onto the screen.
        batch.setProjectionMatrix(camera.combined);

        // Begin one SpriteBatch drawing session.
        batch.begin();

        // Draw static base background first.
        batch.draw(assets.background, 0, 0, worldWidth, worldHeight);


        // Draw moving parallax layers on top of the base background.
        background.draw(batch);

        // Draw the starting platform.
        batch.draw(assets.pod, podX, podY, podWidth, podHeight);

        // Draw Obstacle
        for (ObstaclePair pair : pairs) {
            Obstacle bottom = pair.getBottom();
            Obstacle top = pair.getTop();

            batch.draw(
                assets.skyscraper,
                bottom.getX(),
                bottom.getY(),
                bottom.getWidth(),
                bottom.getHeight()
            );

            batch.draw(
                assets.skyscraper,
                top.getX(),
                top.getY(),
                top.getWidth(),
                top.getHeight()
            );


        }

        // Draw the player.
        batch.draw(assets.player, player.getX(), player.getY(), player.getWidth(), player.getHeight());

        // End the SpriteBatch drawing session.
        batch.end();

        //

        batch.setProjectionMatrix(
            new Matrix4().setToOrtho2D(
                0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
            )
        );

        //Start new batch for score
        batch.begin();

        //Set size and color on font
        font.setUseIntegerPositions(false);
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        // Draw the current score near the top of the screen.
        font.draw(batch, "Score: " + (int) score.getScore(),     250,
            com.badlogic.gdx.Gdx.graphics.getHeight() - 20
        );

        // If the game is over, draw a game over message and final score.
        if (gameState == GameState.GAME_OVER) {
            font.getData().setScale(2f);
            font.draw(batch,
                "GAME OVER!\nYour score: " + (int) finalScore,
                com.badlogic.gdx.Gdx.graphics.getWidth() / 2f - 120,
                com.badlogic.gdx.Gdx.graphics.getHeight() / 2f + 40 );
        }

        // End the score drawing session.
        batch.end();

    }
}
