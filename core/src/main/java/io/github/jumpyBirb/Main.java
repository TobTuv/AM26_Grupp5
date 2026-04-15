package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jumpyBirb.data.Obstacle;
import io.github.jumpyBirb.data.Player;
import io.github.jumpyBirb.data.Score;
import io.github.jumpyBirb.game.GameState;
import io.github.jumpyBirb.game.ObstacleManager;
import io.github.jumpyBirb.game.ParallaxBackground;
import io.github.jumpyBirb.graphics.GameAssets;
import io.github.jumpyBirb.graphics.GameRenderer;

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
    private float timePlaying = 0f;

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
        obstacleManager.update(delta, timePlaying, getPipeSpeed());

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

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        batch.begin();

        batch.draw(assets.background, 0, 0, screenWidth, screenHeight);
        background.draw(batch);

        batch.draw(assets.pod, podX, podY, POD_WIDTH, POD_HEIGHT);
        batch.draw(assets.player, player.getX(), player.getY(), player.getWidth(), player.getHeight());

        for (Obstacle p : obstacleManager.getPipes()) {
            batch.draw(assets.skyscraper, p.getX(), p.getY(), p.getWidth(), p.getHeight());
        }

        font.draw(batch, "Score: " + (int) score.getScore(), 270, screenHeight - 10);

        if (gameState == GameState.GAME_OVER) {
            font.draw(batch,
                "GAME OVER!\nYour score: " + (int) finalScore,
                screenWidth / 2 - 80,
                screenHeight / 2);
        }

        batch.end();
    }

    private boolean inputPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    private void handleStartOrRestartInput() {
        if (inputPressed()) {
            startGame();
        }
    }

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

    private void gameOver() {
        finalScore = score.getScore();
        gameState = GameState.GAME_OVER;
    }

    private float calculateJumpForce() {
        return Math.max(150f, JUMP_FORCE - (timePlaying / 10f) * 5);
    }

    private float getPipeSpeed() {
        return 200 + ((int) (timePlaying / 5)) * 20;
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        assets.dispose();
    }
}
