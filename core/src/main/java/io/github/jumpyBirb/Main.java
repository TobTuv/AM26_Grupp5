package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Main extends ApplicationAdapter {

    // Start of score
    private boolean start;
    private boolean alive;
    private double finalScore = 0;

    //all assests goes here before batch;
    Texture backgroundTexture;
    Texture parallaxOneTexture;
    Texture parallaxTwoTexture;
    Texture parallaxThreeTexture;
    Texture bikerTexture;
    Texture skyScraperTexture;
    Texture flyingSkyScraperTextureOne;
    Texture podPlatformTexture;
    Sound jumpSound;
    Music music;
    Music introMusic;
    private BitmapFont font;

    //Parallax settings
    float parallaxOneX = 0;
    float parallaxTwoX = 0;
    float parallaxThreeX = 0;

    float parallaxOneSpeed = 20f;
    float parallaxTwoSpeed = 40f;
    float parallaxThreeSpeed = 80f;

    private SpriteBatch batch;

    float playerY;
    float velocity;

    float podSpeed = 150;
    float timeAlive = 0; // use for score
    float timePlaying = 0; // use for harder things.

    final float GRAVITY = 800;
    final float JUMP_FORCE = 250;
    float currentJumpForce;

    float SCREEN_WIDTH;
    float SCREEN_HEIGHT;
    float CEILING;

    // Creat a player here
    float playerX = 100;
    float playerWidth = 90;
    float playerHeight = 50;

    // creat a ledged where we start
    float podX = 65;
    float podY = 0;
    float podWidth = 190;
    float podHeight = 215;

    // Pipe system (NEW)
    PipeManager pipeManager;

    // Difficulty
    final float BASE_GAP = 160;

    Score score = new Score();

    @Override
    public void create() {

        backgroundTexture = new Texture("background.png");
        bikerTexture = new Texture("player.png");
        skyScraperTexture = new Texture("skyscraper.png");

        podPlatformTexture = new Texture("podPlatform.png");
        parallaxOneTexture = new Texture("clouds-parallax1-3000x1080.png");
        parallaxTwoTexture = new Texture("city-parallax2-1920x1080.png");
        parallaxThreeTexture = new Texture("smog-parallax3-3000x1080.png");
        font = new BitmapFont();
        batch = new SpriteBatch();

        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        CEILING = SCREEN_HEIGHT;

        pipeManager = new PipeManager(SCREEN_WIDTH, SCREEN_HEIGHT, skyScraperTexture);

        playerY = 200;
        velocity = 0;

        start = true;
        alive = true;
    }

    @Override
    public void render() {
        update();
        draw();
        scoreCount();
    }

    void update() {

        float delta = Gdx.graphics.getDeltaTime();

        if (!start && alive) {
            timePlaying += delta;

            // Parallax
            parallaxOneX -= parallaxOneSpeed * delta;
            parallaxTwoX -= parallaxTwoSpeed * delta;
            parallaxThreeX -= parallaxThreeSpeed * delta;

            if (parallaxOneX <= -SCREEN_WIDTH) parallaxOneX = 0;
            if (parallaxTwoX <= -SCREEN_WIDTH) parallaxTwoX = 0;
            if (parallaxThreeX <= -SCREEN_WIDTH) parallaxThreeX = 0;

            // Start game and start again after game over
            if (!alive || start) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

                    //should this just be the resetGame()-method?
                    alive = true;

                    score.resetScore(1, 0);
                    playerY = 200;
                    velocity = 0;
                    timeAlive = 0;
                    timeAlive = 0;
                    timePlaying = 0;
                    start = false;
                    currentJumpForce = Math.max(150f, JUMP_FORCE - (timePlaying / 10f) * 5);
                    velocity = currentJumpForce;
                }

                return;
            }

            // Hoppa med båda space och left click.
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                currentJumpForce = Math.max(150f, JUMP_FORCE - (timePlaying / 10f) * 5);
                velocity = currentJumpForce;
            }

            // Gravitation
            velocity -= GRAVITY * delta;

            // Flytta spelaren
            playerY += velocity * delta;

            //Tak
            if (playerY > CEILING) {
                playerY = CEILING;
                velocity = 0;
            }

            // moves tha starting pedestal
            podX -= podSpeed * delta;

            // Narrowing the safe distance (GAP)
            float currentGap = Math.max(90f, BASE_GAP - (timePlaying / 5f) * 2);

            // PIPE UPDATE (NEW SYSTEM)
            pipeManager.update(delta, getPipeSpeed(), currentGap, timePlaying);

            // COLLISION PIPE (NEW SYSTEM)
            if (pipeManager.checkCollision(playerX, playerY, playerWidth, playerHeight)) {
                System.out.println("Game Over - Hit Pipe");
                resetGame();
            }

            // Death
            if (playerY <= 0) {
                System.out.println("Game Over - Fell");
                resetGame();
            }
        }

        // Start / restart
        if (!alive || start) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

                alive = true;
                start = false;

                resetGameState();
            }
        }
    }

    void draw() {

        ScreenUtils.clear(Color.BLACK);

        batch.begin();

        batch.draw(backgroundTexture, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.draw(parallaxOneTexture, parallaxOneX, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.draw(parallaxOneTexture, parallaxOneX + SCREEN_WIDTH, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.draw(parallaxTwoTexture, parallaxTwoX, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.draw(parallaxTwoTexture, parallaxTwoX + SCREEN_WIDTH, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.draw(parallaxThreeTexture, parallaxThreeX, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.draw(parallaxThreeTexture, parallaxThreeX + SCREEN_WIDTH, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.draw(podPlatformTexture, podX, podY, podWidth, podHeight);
        batch.draw(bikerTexture, playerX, playerY, playerWidth, playerHeight);

        // PIPE DRAW (NEW SYSTEM)
        pipeManager.draw(batch);

        font.draw(batch, "Score: " + (int) score.getScore(), 270, SCREEN_HEIGHT - 10);

        if (!alive) {
            font.draw(batch,
                "GAME OVER!\nYour score: " + (int) finalScore,
                SCREEN_WIDTH / 2 - 30,
                SCREEN_HEIGHT / 2);
        }

        batch.end();
    }

    float getPipeSpeed() {
        return 200 + ((int) (timePlaying / 5)) * 20;
    }

    void resetGame() {
        playerY = 200;
        velocity = 0;

        pipeManager.reset();

        podX = 65;
        timePlaying = 0;
        score.resetScore(1, 0);
        alive = false;
    }

    void resetGameState() {
        playerY = 200;
        velocity = 0;

        pipeManager.reset();

        podX = 65;
        timePlaying = 0;

        score.resetScore(1, 0);
    }

    public void scoreCount() {
        float delta = Gdx.graphics.getDeltaTime();

        if (!alive || start) {
            return;
        }

        timePlaying += delta;

        while (timePlaying >= 0.1f) {
            finalScore = score.getScore();
            score.addScore();
            timePlaying -= 0.1f;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();

        backgroundTexture.dispose();
        bikerTexture.dispose();
        skyScraperTexture.dispose();
        podPlatformTexture.dispose();

        parallaxOneTexture.dispose();
        parallaxTwoTexture.dispose();
        parallaxThreeTexture.dispose();
    }
}
