package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import groovyjarjarantlr4.v4.codegen.model.dbg;

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

    private SpriteBatch batch;
    private BitmapFont font;

    private Score score = new Score();
    private float timeAccumulator = 0;

    public void scoreCount() {
        float delta = Gdx.graphics.getDeltaTime();
        timeAccumulator += delta;

        while (timeAccumulator >= 0.1f) {
            if (!alive || start){
                break;
            }
            finalScore = score.getScore();
            score.addScore();
            timeAccumulator -= 0.1f;
        }

    }

    // End of score

    ShapeRenderer shape;
    float playerY;
    float velocity;
    // add a list of pipes included the top and the bottom.
    List<Pipe> pipes;
    float pipeSpeed = 200;
    float pipeTimer = 0;

    // pipe variables
    final float GAP = 150;
    final float PIPE_WIDTH = 50;
    final float MIN_PIPE_HEIGHT = 50;
    final float MAX_PIPE_HEIGHT = 250;
    float timeAlive = 0;

    final float GRAVITY = 800;
    final float JUMP_FORCE = 250;

    float CEILING;
    float SCREEN_HEIGHT;
    float SCREEN_WIDTH;
    // Creat a player here
    float playerX = 100;
    float playerWidth = 30;
    float playerHeight = 30;

    enum PipeSpawnType {
        PAIR,
        TOP,
        BOTTOM
    }

    @Override
    public void create() {
        shape = new ShapeRenderer();
        playerY = 200;
        velocity = 0;
        pipes = new ArrayList<>();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        CEILING = SCREEN_HEIGHT;

        // Start game alive
        alive = true;
        start = true;

        // For score
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    public void render() {
        update();
        draw();
        scoreCount();

        if (!alive){
            batch.begin();
            font.draw(batch, "GAME OVER!\nYour score: " + (int) finalScore, SCREEN_WIDTH/2 - 30, SCREEN_HEIGHT/2);
            batch.end();
        }
    }

    void update() {

        float delta = Gdx.graphics.getDeltaTime();
        timeAlive += delta;

        // Start game and start again after game over
        if (!alive || start) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                alive = true;
                start = false;
                score.resetScore();
                playerY = 200;
                velocity = 0;
                pipes.clear();
                timeAlive = 0;
                pipeTimer = 0;
            }
            return;
        }

        // Hoppa
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            velocity = JUMP_FORCE;
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

        // Game over (under skärmen)
        if (playerY <= 0) {
            System.out.println("Game Over - Fell");
            System.out.printf("Your score: %d%n", (int) score.getScore());
            score.resetScore();
            alive = false;

            playerY = 200;
            velocity = 0;
            pipes.clear();
        }
        if (playerY + playerHeight >= CEILING) {
            System.out.println("Game Over - Hit Ceiling");
            System.out.printf("Your score: %d%n", (int) score.getScore());
            score.resetScore();
            alive = false;

            playerY = 200;
            velocity = 0;
            pipes.clear();
        }

        pipeTimer += delta;

        // spawn
        // When the game starts, adds a pipe pair to the list every 2 seconds, create a
        // new spawn pipe..
        if (pipeTimer > 2f) {
            pipeTimer = 0;
            spawnPipeObstacles(PipeSpawnType.PAIR);
        }
        if (pipes != null && !pipes.isEmpty()) {
            Iterator<Pipe> iter = pipes.iterator();
            // p.x = horizontal coordinate (x) of the pipe on the screen.

            while (iter.hasNext()) {
                Pipe p = iter.next();
                // Move the pipe left across the screen by decreasing its x-coordinate.
                // Using '-=' ensures the pipe moves from right to left over time.
                // delta = time between 2 frames.
                p.x -= pipeSpeed * delta;
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
                    score.resetScore();
                    alive = false;

                    playerY = 200;
                    velocity = 0;
                    pipes.clear();
                    break;
                }
            }
        }
    }

    void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(playerX, playerY, playerWidth, playerHeight);
        for (Pipe p : pipes) {
            shape.rect(p.x, p.y, p.width, p.height);
        }
        shape.end();

        // For score
        batch.begin();
        font.draw(batch, "Score: " + (int) score.getScore(), 270, SCREEN_HEIGHT - 10);
        batch.end();
    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();
    }

    // For future sprints: we could break this method up into three separate, where
    // PAIR also has a gapSize parameter,
    // and change the constant GAP to gapSize in the method.
    void spawnPipeObstacles(PipeSpawnType type) {
        if (type == PipeSpawnType.PAIR) {
            float gapStart = MIN_PIPE_HEIGHT + (float) (Math.random() * (SCREEN_HEIGHT - GAP - 2 * MIN_PIPE_HEIGHT));

            pipes.add(new Pipe(SCREEN_WIDTH, 0, PIPE_WIDTH, gapStart));
            pipes.add(new Pipe(SCREEN_WIDTH, gapStart + GAP, PIPE_WIDTH, SCREEN_HEIGHT - (gapStart + GAP)));
        }

        if (type == PipeSpawnType.TOP) {
            float height = MIN_PIPE_HEIGHT + (float) (Math.random() * (MAX_PIPE_HEIGHT - MIN_PIPE_HEIGHT));
            float y = SCREEN_HEIGHT - height;
            pipes.add(new Pipe(SCREEN_WIDTH, y, PIPE_WIDTH, height));
        }

        if (type == PipeSpawnType.BOTTOM) {
            float height = MIN_PIPE_HEIGHT + (float) (Math.random() * (MAX_PIPE_HEIGHT - MIN_PIPE_HEIGHT));
            pipes.add(new Pipe(SCREEN_WIDTH, 0, PIPE_WIDTH, height));
        }
    }
}
