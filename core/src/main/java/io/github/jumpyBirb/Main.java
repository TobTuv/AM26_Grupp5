package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Main extends ApplicationAdapter {

    // Start of score
    private Boolean start;
    private Boolean alive;
    private double finalScore = 0;

    //all assests goes here befor batch;
    Texture backgroundTexture;
    Texture parallaxOneTexture;
    Texture parallaxTwoTexture;
    Texture parallaxThreeTexture;
    Texture bikerTexture;
    Texture skyScraperTexture;
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
    // add a list of pipes included the top and the bottom.
    List<Pipe> pipes;
    float pipeTimer = 0;

    float podSpeed = 150;

    // pipe variables
    final float GAP = 150;
    final float PIPE_WIDTH = 80;
    final float MIN_PIPE_HEIGHT = 50;
    final float MAX_PIPE_HEIGHT = 250;
    float timeAlive = 0; // use for score
    float timePlaying = 0; // use for harder things.
    float spawnInterval = 2f;


    final float GRAVITY = 800;
    final float JUMP_FORCE = 250;
    float currentJumpForce;

    float CEILING;
    float SCREEN_HEIGHT;
    float SCREEN_WIDTH;

    // Creat a player here
    float playerX = 100;
    float playerWidth = 100;
    float playerHeight = 60;

    // creat a ledged where we start
    float podX = 90;
    float podY = 0;
    float podWidth = 170;
    float podHeight = 240;

    enum PipeSpawnType {
        PAIR,
        TOP,
        BOTTOM
    }

    Score score = new Score();

    @Override
    public void create() {

        backgroundTexture = new Texture("background.png");
        bikerTexture = new Texture("player.png");
        skyScraperTexture = new Texture("skyscraper.png");
        podPlatformTexture = new Texture("podPlatform.png");
        parallaxOneTexture = new Texture("clouds-parallax1-3000x1080.png");
        parallaxTwoTexture = new Texture("city-parallax2-3000x1080.png");
        parallaxThreeTexture = new Texture("smog-parallax3-3000x1080.png");
        font = new BitmapFont();
        batch = new SpriteBatch();


        playerY = 200;
        velocity = 0;
        pipes = new ArrayList<>();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        CEILING = SCREEN_HEIGHT;

        // Start game alive
        alive = true;
        start = true;
    }

    @Override
    public void render() {

        update();
        draw();
        scoreCount();
    }

    void update() {

        float delta = Gdx.graphics.getDeltaTime();

        //parallax speed and position settings
        if (!start && alive) {
            parallaxOneX -= parallaxOneSpeed * delta;
            parallaxTwoX -= parallaxTwoSpeed * delta;
            parallaxThreeX -= parallaxThreeSpeed * delta;

            if (parallaxOneX <= -SCREEN_WIDTH) {
                parallaxOneX = 0;
            }
            if (parallaxTwoX <= -SCREEN_WIDTH) {
                parallaxTwoX = 0;
            }
            if (parallaxThreeX <= -SCREEN_WIDTH) {
                parallaxThreeX = 0;
            }
        }

        timeAlive += delta;
        timePlaying += delta;

        // Start game and start again after game over
        if (!alive || start) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

                //should this just be the resetGame()-method?
                alive = true;

                score.resetScore(1, 0);
                playerY = 200;
                velocity = 0;
                pipes.clear();
                timeAlive = 0;
                pipeTimer = 0;
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

        // Tak
        if (playerY > CEILING) {
            playerY = CEILING;
            velocity = 0;
        }

        // moves tha starting pedestal
        podX -= podSpeed * delta;

        // Game over (under skärmen)
        if (playerY <= 0) {
            System.out.println("Game Over - Fell");
            System.out.printf("Your score: %d%n", (int) score.getScore());
            resetGame();
        }
        if (playerY + playerHeight >= CEILING) {
            System.out.println("Game Over - Hit Ceiling");
            System.out.printf("Your score: %d%n", (int) score.getScore());
            resetGame();
        }

        pipeTimer += delta;

        // Narrowing the safe distance (GAP)
        float currentGap = Math.max(90f, GAP - (timePlaying / 5f) * 2);

        // spawn
        // When the game starts, adds a pipe pair to the list every 2 seconds, create a
        // new spawn pipe.
        spawnInterval = Math.max(1.3f, 2f - (timePlaying / 40f));
        if (pipeTimer > spawnInterval) {
            pipeTimer = 0;
            spawnPipeObstacles(PipeSpawnType.PAIR, currentGap);
        }
        if (pipes != null && !pipes.isEmpty()) {
            Iterator<Pipe> iter = pipes.iterator();
            // p.x = horizontal coordinate (x) of the pipe on the screen.

            while (iter.hasNext()) {
                Pipe p = iter.next();
                // Move the pipe left across the screen by decreasing its x-coordinate.
                // Using '-=' ensures the pipe moves from right to left over time.
                // delta = time between 2 frames.
                p.x -= getPipeSpeed() * delta;
                if (p.x + p.width < 0) {
                    iter.remove();
                }
            }
        }

        // Game over when the box hit the pipe.

        if (pipes != null && !pipes.isEmpty()) {
            for (Pipe p : pipes) {
                if (playerX < p.x + p.width &&
                    playerX + playerWidth > p.x &&
                    playerY < p.y + p.height &&
                    playerY + playerHeight > p.y) {
                    System.out.println("Game Over - Hit Pipe");
                    System.out.printf("Your score: %d%n", (int) score.getScore());
                    resetGame();
                    break;
                }
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
        for (Pipe p : pipes) {
            batch.draw(skyScraperTexture, p.x, p.y, p.width, p.height);
        }
        font.draw(batch, "Score: " + (int) score.getScore(), 270, SCREEN_HEIGHT - 10);
        if (!alive) {
            font.draw(batch, "GAME OVER!\nYour score: " + (int) finalScore, SCREEN_WIDTH / 2 - 30, SCREEN_HEIGHT / 2);
        }
        batch.end();
    }

    float getPipeSpeed() {
        return 200 + ((int) (timePlaying / 5)) * 20;
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

    void resetGame() {
        playerY = 200;
        velocity = 0;
        pipes.clear();

        podX = 90;
        podY = 0;

        pipeTimer = 0;
        timePlaying = 0;
        timeAlive = 0;
        score.resetScore(1, 0);
        alive = false;
    }

    void spawnPipeObstacles(PipeSpawnType type, float currentGap) {
        if (type == PipeSpawnType.PAIR) {
            float gapStart = MIN_PIPE_HEIGHT + (float) (Math.random() * (SCREEN_HEIGHT - currentGap - 2 * MIN_PIPE_HEIGHT));

            pipes.add(new Pipe(SCREEN_WIDTH, 0, PIPE_WIDTH, gapStart));
            pipes.add(new Pipe(SCREEN_WIDTH, gapStart + currentGap, PIPE_WIDTH, SCREEN_HEIGHT - (gapStart + currentGap)));
        }
    }

    public void scoreCount() {
        float delta = Gdx.graphics.getDeltaTime();
        timeAlive += delta;

        while (timeAlive >= 0.1f) {

            if (!alive) {
                score.resetScore(1, 0);
                break;
            } else if (start) {
                score.resetScore(1, 0);
                break;
            }

            finalScore = score.getScore();
            score.addScore();
            timeAlive -= 0.1f;
        }
    }
}
