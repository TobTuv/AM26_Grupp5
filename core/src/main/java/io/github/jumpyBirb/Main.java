package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.data.Player;
import io.github.jumpyBirb.data.Score;
import io.github.jumpyBirb.game.GameState;
import io.github.jumpyBirb.game.ObstacleManager;
import io.github.jumpyBirb.game.ParallaxBackground;
import io.github.jumpyBirb.graphics.GameAssets;
import io.github.jumpyBirb.graphics.GameRenderer;

/**
 * Main game entry point for JumpyBirb.
 *
 * <p>This class is the central coordinator of the game.
 * It does not contain all game logic itself anymore, but instead creates
 * and connects the other classes that handle specific responsibilities.
 *
 * <p>Main is responsible for:
 * <ul>
 *     <li>creating and setting up core game objects</li>
 *     <li>running the update loop every frame</li>
 *     <li>sending the current game state to the renderer</li>
 *     <li>handling high-level game flow such as start, restart, and game over</li>
 * </ul>
 *
 * <p>Main is NOT responsible for detailed logic in every area:
 * <ul>
 *     <li>{@link Player} handles player movement</li>
 *     <li>{@link Score} handles score logic</li>
 *     <li>{@link ObstacleManager} handles spawning and updating obstacles</li>
 *     <li>{@link ParallaxBackground} handles parallax movement and drawing</li>
 *     <li>{@link GameRenderer} handles rendering</li>
 *     <li>{@link GameAssets} handles textures</li>
 * </ul>
 *
 * <p>Why this class exists in this form:
 * Earlier, almost everything was inside Main, which made the file very long
 * and hard to understand. The current version keeps Main as the "orchestrator"
 * of the game while moving detailed behavior into separate classes.
 *
 * <p>Game flow:
 * <ul>
 *     <li>{@code create()} runs once when the game starts</li>
 *     <li>{@code render()} runs every frame</li>
 *     <li>{@code update()} updates game logic</li>
 *     <li>{@code renderer.draw(...)} renders the current state</li>
 *     <li>{@code dispose()} cleans up resources when the game closes</li>
 * </ul>
 */
public class Main extends ApplicationAdapter {

    private static final float GRAVITY = 800f;
    private static final float JUMP_FORCE = 250f;
    private static final float PLAYER_START_X = 100f;
    private static final float PLAYER_START_Y = 200f;
    private static final float PLAYER_WIDTH = 90f;
    private static final float PLAYER_HEIGHT = 50f;

    private static final float POD_START_X = 65f;
    private static final float POD_START_Y = 0f;
    private static final float POD_WIDTH = 190f;
    private static final float POD_HEIGHT = 215f;
    private static final float POD_SPEED = 150f;

    private static final float PIPE_WIDTH = 120f;
    private static final float MIN_PIPE_HEIGHT = 50f;

    private SpriteBatch batch;
    private BitmapFont font;
    private GameAssets assets;

    private Player player;
    private Score score;
    private ObstacleManager obstacleManager;
    private ParallaxBackground background;
    private GameRenderer renderer;

    private GameState gameState;
    private double finalScore = 0;

    private float screenWidth;
    private float screenHeight;
    private float ceiling;

    private float podX;
    private float podY;

    /**
     * Total time the current run has been active.
     *
     * <p>Used for difficulty scaling such as:
     * <ul>
     *     <li>stronger/weaker jump tuning</li>
     *     <li>faster obstacle movement</li>
     *     <li>smaller obstacle gap</li>
     *     <li>shorter spawn interval</li>
     * </ul>
     */
    private float timePlaying = 0f;


    /**
     * Runs once when the game is first created.
     *
     * <p>This method creates the rendering objects, loads assets,
     * reads screen size, creates the game systems, and sets the game to START state.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        assets = new GameAssets();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        ceiling = screenHeight;

        renderer = new GameRenderer(batch, font, assets, screenWidth, screenHeight);
        player = new Player(PLAYER_START_X, PLAYER_START_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
        score = new Score();
        obstacleManager = new ObstacleManager(PIPE_WIDTH, MIN_PIPE_HEIGHT, screenWidth, screenHeight);
        background = new ParallaxBackground(
            assets.parallax1,
            assets.parallax2,
            assets.parallax3,
            screenWidth,
            screenHeight
        );

        podX = POD_START_X;
        podY = POD_START_Y;

        gameState = GameState.START;
    }

    /**
     * Runs once every frame.
     *
     * <p>This is the main game loop.
     * First the game logic is updated, then the current state is rendered.
     */
    @Override
    public void render() {
        update();
        renderer.draw(
            background,
            player,
            obstacleManager.getPipes(),
            score,
            gameState,
            finalScore,
            podX,
            podY,
            POD_WIDTH,
            POD_HEIGHT
        );
    }

    /**
     * Updates one frame of game logic.
     *
     * <p>Main update flow:
     * <ul>
     *     <li>if the game is not running, only listen for start/restart input</li>
     *     <li>if the game is running, process jump input</li>
     *     <li>update background, player, score, obstacles, and platform</li>
     *     <li>check for game over conditions</li>
     * </ul>
     */
    private void update() {
        float delta = Gdx.graphics.getDeltaTime();

        if (gameState != GameState.RUNNING) {
            handleStartOrRestartInput();
            return;
        }

        if (inputPressed()) {
            player.jump(calculateJumpForce());
        }

        background.update(delta);
        player.update(delta, GRAVITY);
        player.clampToCeiling(ceiling);

        podX -= POD_SPEED * delta;
        timePlaying += delta;

        score.update(delta, true);
        obstacleManager.update(delta, timePlaying, getObstacleSpeed());

        if (player.hitsBottom() || player.hitsTop(ceiling) ||
            obstacleManager.collidesWith(
                player.getX(),
                player.getY(),
                player.getWidth(),
                player.getHeight()
            )) {
            gameOver();
        }
    }

    /**
     * Checks if jump/start input was pressed this frame.
     *
     * <p>Supports both:
     * <ul>
     *     <li>space key</li>
     *     <li>left mouse button</li>
     * </ul>
     *
     * @return true if input was pressed this frame
     */
    private boolean inputPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    /**
     * Handles input when the game is not currently running.
     *
     * <p>If input is pressed at the start screen or after game over,
     * a new run begins.
     */
    private void handleStartOrRestartInput() {
        if (inputPressed()) {
            startGame();
        }
    }

    /**
     * Starts a new run.
     *
     * <p>This resets the key game systems and state:
     * <ul>
     *     <li>player position and velocity</li>
     *     <li>score</li>
     *     <li>obstacles</li>
     *     <li>platform position</li>
     *     <li>time-based difficulty scaling</li>
     *     <li>final score display</li>
     * </ul>
     *
     * <p>The player is also given an immediate jump to begin the run.
     */
    private void startGame() {
        player.reset(PLAYER_START_Y);
        score.reset();
        obstacleManager.reset();

        podX = POD_START_X;
        podY = POD_START_Y;
        timePlaying = 0f;
        finalScore = 0;

        gameState = GameState.RUNNING;
        player.jump(calculateJumpForce());
    }

    /**
     * Ends the current run and switches the game to GAME_OVER state.
     *
     * <p>The current score is saved so it can still be displayed after the run ends.
     */
    private void gameOver() {
        finalScore = score.getScore();
        gameState = GameState.GAME_OVER;
    }

    /**
     * Calculates the current jump force.
     *
     * <p>As timePlaying increases, jump force is slightly reduced,
     * but never below 150.
     *
     * @return adjusted jump force
     */
    private float calculateJumpForce() {
        return Math.max(150f, JUMP_FORCE - (timePlaying / 10f) * 5);
    }

    /**
     * Calculates current obstacle speed.
     *
     * <p>Obstacle speed increases in steps over time to make the game harder.
     *
     * @return current obstacle movement speed
     */
    private float getObstacleSpeed() {
        return 200 + ((int) (timePlaying / 5)) * 20;
    }

    /**
     * Cleans up resources when the game closes.
     *
     * <p>Important:
     * LibGDX textures, SpriteBatch, and fonts must be disposed properly
     * to avoid memory leaks.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        assets.dispose();
    }
}
