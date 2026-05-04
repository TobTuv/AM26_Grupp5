package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import io.github.jumpyBirb.data.*;
import io.github.jumpyBirb.data.intro.Intro;
import io.github.jumpyBirb.game.*;
import io.github.jumpyBirb.graphics.GameAssets;
import io.github.jumpyBirb.graphics.GameRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

/**
 * Main game entry point for JumpyBirb.
 *
 * <p>
 * This class is the central coordinator of the game.
 * It does not contain all game logic itself anymore, but instead creates
 * and connects the other classes that handle specific responsibilities.
 *
 * <p>
 * Main is responsible for:
 * <ul>
 * <li>creating and setting up core game objects</li>
 * <li>running the update loop every frame</li>
 * <li>sending the current game state to the renderer</li>
 * <li>handling high-level game flow such as start, restart, and game over</li>
 * </ul>
 *
 * <p>
 * Main is NOT responsible for detailed logic in every area:
 * <ul>
 * <li>{@link Player} handles player movement</li>
 * <li>{@link Score} handles score logic</li>
 * <li>{@link ObstacleManager} handles spawning and updating obstacles</li>
 * <li>{@link ParallaxBackground} handles parallax movement and drawing</li>
 * <li>{@link GameRenderer} handles rendering</li>
 * <li>{@link GameAssets} handles textures</li>
 * </ul>
 *
 * <p>
 * Why this class exists in this form:
 * Earlier, almost everything was inside Main, which made the file very long
 * and hard to understand. The current version keeps Main as the "orchestrator"
 * of the game while moving detailed behavior into separate classes.
 *
 * <p>
 * Game flow:
 * <ul>
 * <li>{@code create()} runs once when the game starts</li>
 * <li>{@code render()} runs every frame</li>
 * <li>{@code update()} updates game logic</li>
 * <li>{@code renderer.draw(...)} renders the current state</li>
 * <li>{@code dispose()} cleans up resources when the game closes</li>
 * </ul>
 */
public class Main extends ApplicationAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private OrthographicCamera camera;
    private Viewport viewport;

    private static final float GRAVITY = 30f;
    private static final float JUMP_FORCE = 6.0f;
    private static final float PLAYER_START_X = 3f;
    private static final float PLAYER_START_Y = 4.5f;
    private static final float PLAYER_WIDTH = 1.4f;
    private static final float PLAYER_HEIGHT = 0.8f;

    private static final float POD_START_X = 1.9f;
    private static final float POD_START_Y = 0f;
    private static final float POD_WIDTH = 3.7f;
    private static final float POD_HEIGHT = 4.8f;
    private static final float POD_SPEED = 2.5f;

    private static final float OBSTACLE_WIDTH = 1.8f;
    private static final float OBSTACLE_HEIGHT = 4.5f;
    private static final float MIN_OBSTACLE_HEIGHT = 4.5f;

    private SpriteBatch batch;
    private BitmapFont font;
    private GameAssets assets;
    private BitmapFont helpFont;
    private float startBlinkTimer = 0f;

    private Stage nameStage;
    private TextField nameField;
    private Skin skin;

    private Player player;
    private Score score;
    private ObstacleManager obstacleManager;
    private ParallaxBackground background;
    private GameRenderer renderer;
    private Menu menu;
    private Menu gameOverMenu;
    private Settings settings;
    private Credits credits;
    private String playerName = "Player";
    private boolean scoreSaved = false;
    private float dyingTimer = 0f;
    private static final float DYING_DURATION = 1.2f;
    private boolean sound = true;
    private boolean music = true;

    private boolean gameHasStarted = false;

    private InputGate inputGate = new InputGate();

    private AudioManager audio;

    private GameState gameState;
    private long finalScore = 0;
    private Intro intro;

    private float screenWidth;
    private float screenHeight;
    private float ceiling;

    private float podX;
    private float podY;

    /**
     * Total time the current run has been active.
     *
     * <p>
     * Used for difficulty scaling such as:
     * <ul>
     * <li>stronger/weaker jump tuning</li>
     * <li>faster obstacle movement</li>
     * <li>smaller obstacle gap</li>
     * <li>shorter spawn interval</li>
     * </ul>
     */
    private float timePlaying = 0f;

    /**
     * Runs once when the game is first created.
     *
     * <p>
     * This method creates the rendering objects, loads assets,
     * reads screen size, creates the game systems, and sets the game to START
     * state.
     */
    @Override
    public void create() {

        settings = new Settings();
        menu = new Menu(
            new String[]{"Start", "High Score", "Settings"},
            new GameState[]{GameState.RUNNING, GameState.HIGH_SCORE, GameState.SETTINGS}
        );
        credits = new Credits();

        gameOverMenu = new Menu(
            new String[]{"Play Again", "Settings"},
            new GameState[]{GameState.RUNNING, GameState.SETTINGS}
        );
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/bgothm.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 60;

        helpFont = generator.generateFont(parameter);
        generator.dispose();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(0.03f);
        assets = new GameAssets();

        intro = new Intro(assets.logoText);
        gameState = GameState.NAME_INPUT;
        inputGate.block(1f);

        skin = new Skin(Gdx.files.internal("uiskin.json")); // måste finnas i assets-foldern
        nameStage = new Stage(new ScreenViewport());

        nameField = new TextField("", skin);
        nameField.setMessageText(playerName.trim());
        nameField.setMaxLength(12);
        nameField.setSize(300, 50);
        nameField.setPosition(
            Gdx.graphics.getWidth() / 2f - 150,
            Gdx.graphics.getHeight() / 2f);
        nameField.setTextFieldFilter((textField, c) -> Character.isLetterOrDigit(c) || c == '_');

        nameStage.addActor(nameField);

        screenWidth = WORLD_WIDTH;
        screenHeight = WORLD_HEIGHT;
        ceiling = WORLD_HEIGHT;

        renderer = new GameRenderer(batch, font, assets, camera, screenWidth, screenHeight);
        player = new Player(PLAYER_START_X, PLAYER_START_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
        score = new Score();
        obstacleManager = new ObstacleManager(
            OBSTACLE_WIDTH, MIN_OBSTACLE_HEIGHT, OBSTACLE_HEIGHT, screenWidth, screenHeight, assets.topObstacles,
            assets.bottomObstacles);
        background = new ParallaxBackground(
            assets.parallax1,
            assets.parallax2,
            assets.parallax3,
            screenWidth,
            screenHeight);

        podX = POD_START_X;
        podY = POD_START_Y;

        audio = new AudioManager(assets);


    }

    /**
     * Runs once every frame.
     *
     * <p>
     * This is the main game loop.
     * First the game logic is updated, then the current state is rendered.
     */
    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        update();

        // UI ska ritas i pixel-space
        batch.setProjectionMatrix(
            new Matrix4().setToOrtho2D(
                0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()));

        font.getData().setScale(2f);
        font.setColor(Color.WHITE);

        batch.begin();

        switch (gameState) {

            case INTRO -> {
                batch.draw(assets.background, 0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight());
                batch.draw(assets.parallax1, 0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight());
                batch.draw(assets.parallax2, 0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight());
                batch.draw(assets.parallax3, 0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight());
                batch.draw(assets.parallax4, 0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight());
                intro.render(batch);
            }

            case START, MENU -> {
                batch.draw(assets.menuBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                menu.render(batch, font);
            }
            case SETTINGS -> {
                batch.draw(assets.menuBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                settings.render(batch, font, music, sound);
            }
        }

        batch.end();

        if (gameState == GameState.RUNNING || gameState == GameState.DYING) {
            renderer.draw(
                background,
                player,
                obstacleManager.getPairs(),
                score,
                gameState,
                finalScore,
                podX,
                podY,
                POD_WIDTH,
                POD_HEIGHT);
        }

        if (gameState == GameState.RUNNING && !gameHasStarted) {
            batch.begin();

            String msg = "Press SPACE to begin";

            GlyphLayout layout = new GlyphLayout();
            layout.setText(helpFont, msg);

            float x = Gdx.graphics.getWidth() - layout.width - 50;
            float y = Gdx.graphics.getHeight() / 2f;

            float alpha = (float)Math.abs(Math.sin(startBlinkTimer * 3));

            helpFont.setColor(1, 1, 1, alpha);
            helpFont.draw(batch, msg, x, y);
            helpFont.setColor(Color.WHITE);

            batch.end();
        }

        if (gameState == GameState.NAME_INPUT) {
            ScreenUtils.clear(Color.BLACK);

            batch.setProjectionMatrix(
                new Matrix4().setToOrtho2D(
                    0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()));

            batch.begin();
            batch.draw(assets.menuBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();

            Gdx.input.setInputProcessor(nameStage);

            nameStage.act(Gdx.graphics.getDeltaTime());
            nameStage.draw();

            batch.begin();
            font.draw(batch, "Enter your name:",
                Gdx.graphics.getWidth() / 2f - 150,
                Gdx.graphics.getHeight() / 2f + 80);
            font.draw(batch, "Press SPACE to continue",
                Gdx.graphics.getWidth() / 2f - 150,
                Gdx.graphics.getHeight() / 2f - 80);
            batch.end();

        }

        if (gameState == GameState.GAME_OVER) {

            batch.setProjectionMatrix(
                new Matrix4().setToOrtho2D(
                    0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()));

            // visa topp 5
            List<Highscore.Entry> top5 = Highscore.top(5);

            batch.begin();

            batch.draw(assets.gameOverBackground, 0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

            font.draw(batch, "HIGH SCORE", 100, 450);
            font.draw(batch, "Your score: " + finalScore, 100, 600);

            int y = 380;
            for (Highscore.Entry e : top5) {
                font.draw(batch, e.name + ": " + e.score, 100, y);
                y -= 40;
            }


            gameOverMenu.render(
                batch,
                font,
                350, 450
            );
            batch.end();

            GameState next = gameOverMenu.consumeNextState();
            if (next != null) {
                if (next == GameState.RUNNING) {
                    startGame();
                } else {
                    gameState = next;
                }
            }

            return;
        }

        if (gameState == GameState.HIGH_SCORE) {

            batch.setProjectionMatrix(
                new Matrix4().setToOrtho2D(
                    0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()));
            batch.begin();

            batch.draw(assets.menuBackground, 0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

            font.draw(batch, "HIGH SCORES", 100, 700);

            // Visa top 10 i menyn
            List<Highscore.Entry> top10 = Highscore.top(10);

            int y = 650;
            for (Highscore.Entry e : top10) {
                font.draw(batch, e.name + ": " + e.score, 100, y);
                y -= 40;
            }

            gameOverMenu.render(batch, font, 100, 100);

            batch.end();
            return;
        }

        if (gameState == GameState.SETTINGS) {

            batch.setProjectionMatrix(
                new Matrix4().setToOrtho2D(
                    0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()));

            batch.begin();

            batch.draw(assets.menuBackground, 0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

            settings.render(batch, font, music, sound);

            batch.end();

        }

        if (gameState == GameState.CREDITS) {
            batch.setProjectionMatrix(
                new Matrix4().setToOrtho2D(
                    0, 0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()));

            batch.begin();

            batch.draw(assets.menuBackground, 0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

            credits.render(batch);

            batch.end();
        }

    }

    /**
     * Updates one frame of game logic.
     *
     * <p>
     * Main update flow:
     * <ul>
     * <li>if the game is not running, only listen for start/restart input</li>
     * <li>if the game is running, process jump input</li>
     * <li>update background, player, score, obstacles, and platform</li>
     * <li>check for game over conditions</li>
     * </ul>
     */
    private void update() {
        float delta = Gdx.graphics.getDeltaTime();

        inputGate.update(delta);

        if (gameState == GameState.INTRO) {
            intro.update(delta);

            if (inputGate.canAcceptInput() && skipPressed()) {
                intro.skip();
            }

            if (intro.isFinished()) {
                gameState = GameState.MENU;
                inputGate.block(1f);
                audio.playMenuMusic();
            }

            return;

        }

        if (gameState == GameState.MENU) {
            if (inputGate.canAcceptInput()) {
                menu.update();
            }

            GameState next = menu.consumeNextState();
            if (next != null) {
                if (next == GameState.RUNNING) {
                    startGame();
                } else {
                    gameState = next;
                    inputGate.block(1f);
                }
            }
            return;
        }

        if (gameState == GameState.SETTINGS) {
            settings.update();

            GameState next = settings.consumeNextState();
            if (next != null) {
                gameState = next;
            }
            return;
        }

        if (gameState == GameState.NAME_INPUT) {

            Gdx.input.setInputProcessor(nameStage);

            nameStage.act(Gdx.graphics.getDeltaTime());
            nameStage.draw();

            // ⭐ 1. Ge fokus vid första tangent (MEN inte SPACE/ENTER/mouse)
            if (!nameField.hasKeyboardFocus() && Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {

                if (!Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                    && !Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                    && !Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

                    nameStage.setKeyboardFocus(nameField);
                }
            }

            // ⭐ 2. Confirm
            if (inputGate.canAcceptInput() && menuConfirmPressed()) {

                String input = nameField.getText().trim();

                if (input.isEmpty()) {
                    nameField.setMessageText("Player");
                    return;
                }

                playerName = input;

                Gdx.input.setInputProcessor(null);
                gameState = GameState.INTRO;
                inputGate.block(2f);
                audio.playIntroMusic();
            }
            return;
        }

        if (gameState == GameState.RUNNING && !gameHasStarted) {
            startBlinkTimer += delta;
        }

        if (gameState == GameState.HIGH_SCORE) {

            if (inputGate.canAcceptInput() && menuConfirmPressed()) {
                gameState = GameState.MENU;
                inputGate.block(1f);
            }

            return;
        }

        if (gameState == GameState.DYING) {
            dyingTimer += delta;

            if (dyingTimer >= DYING_DURATION) {

                if (!scoreSaved) {
                    Highscore.save(playerName, (int) finalScore);
                    scoreSaved = true;
                }

                gameState = GameState.GAME_OVER;
                inputGate.block(1f);
                audio.playMenuMusic();
            }

            return;
        }

        if (gameState == GameState.GAME_OVER) {

            gameOverMenu.update();

            GameState next = gameOverMenu.consumeNextState();
            if (next != null) {
                if (next == GameState.RUNNING) {
                    startGame();
                } else {
                    gameState = next;
                }
            }

            return;
        }

        if (gameState == GameState.RESET_SCORE) {
            Highscore.cleanHighScore();
            gameState = GameState.SETTINGS;
            return;
        }

        if (gameState == GameState.MUSIC) {
            if (music == true) {
                audio.muteMusic();
                music = false;
            } else {
                audio.unMuteMusic();
                music = true;
            }
            gameState = GameState.SETTINGS;
            return;
        }

        if (gameState == GameState.SOUND) {
            if (sound == true) {
                audio.muteSound();
                sound = false;
            } else {
                audio.unMuteSound();
                sound = true;
            }
            gameState = GameState.SETTINGS;
            return;
        }

        if (gameState == GameState.CREDITS) {
            credits.update();

            GameState next = credits.consumeNextState();
            if (next != null) {
                gameState = next;
                inputGate.block(0.5f);
            }

            return;
        }

        if (jumpPressed()) {
            player.jump(calculateJumpForce());
            gameHasStarted = true;
            audio.playJump();
        }

        if (!gameHasStarted) {
            return;
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
                player.getHeight())) {

            gameOver();
        }

    }

    /**
     * Checks if jump/start input was pressed this frame.
     *
     * <p>
     * Supports both:
     * <ul>
     * <li>space key</li>
     * <li>left mouse button</li>
     * </ul>
     *
     * @return true if input was pressed this frame
     */
    private boolean jumpPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    private boolean menuConfirmPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    private boolean skipPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    /**
     * Starts a new run.
     *
     * <p>
     * This resets the key game systems and state:
     * <ul>
     * <li>player position and velocity</li>
     * <li>score</li>
     * <li>obstacles</li>
     * <li>platform position</li>
     * <li>time-based difficulty scaling</li>
     * <li>final score display</li>
     * </ul>
     *
     * <p>
     * The player waits on the platform until the first jump input.
     */
    private void startGame() {
        player.reset(PLAYER_START_Y);
        score.reset();
        obstacleManager.reset();

        podX = POD_START_X;
        podY = POD_START_Y;
        timePlaying = 0f;
        finalScore = 0;

        scoreSaved = false;

        gameHasStarted = false;

        gameState = GameState.RUNNING;

        audio.playGameMusic();
    }

    private long getFinalScore() {
        return score.getVisualScore();
    }

    /**
     * Ends the current run and switches the game to GAME_OVER state.
     *
     * <p>
     * The current score is saved so it can still be displayed after the run ends.
     */


    private void gameOver() {
        if (gameState != GameState.DYING && gameState != GameState.GAME_OVER) {

            score.stopScore();
            finalScore = getFinalScore();

            gameState = GameState.DYING;
            dyingTimer = 0f;

            audio.stopJump();
            audio.playCrash();
        }
    }

    /**
     * Calculates the current jump force.
     *
     * <p>
     * As timePlaying increases, jump force is slightly reduced,
     * but never below 150.
     *
     * @return adjusted jump force
     */
    private float calculateJumpForce() {
        return Math.max(5f, JUMP_FORCE - (timePlaying / 10f) * 0.3f);
    }

    /**
     * Calculates current obstacle speed.
     *
     * <p>
     * Obstacle speed increases in steps over time to make the game harder.
     *
     * @return current obstacle movement speed
     */
    private float getObstacleSpeed() {
        return 4.0f + ((int) (timePlaying / 8)) * 0.8f;
    }

    /**
     * Updates the viewport when the game window is resized.
     * <p>
     * Ensures the game world keeps its aspect ratio and scales correctly
     * to different screen sizes.
     *
     * @param width  the new window width in pixels
     * @param height the new window width in pixels
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Cleans up resources when the game closes.
     *
     * <p>
     * Important:
     * LibGDX textures, SpriteBatch, and fonts must be disposed properly
     * to avoid memory leaks.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        assets.dispose();
        credits.dispose();
        intro.dispose();
    }
}
