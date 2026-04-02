package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends ApplicationAdapter {

    ShapeRenderer shape;
    float playerY;
    float velocity;
    //add a list of pipes included the top and the bottom.
    List<Pipe> pipes;
    //float pipeSpeed = 200;
    float pipeTimer = 0;

    // pipe variables
    final float GAP = 150;
    final float PIPE_WIDTH = 50;
    final float MIN_PIPE_HEIGHT = 50;
    final float MAX_PIPE_HEIGHT = 250;
    float timeAlive = 0;
    float spawnInterval = 2f;

    final float GRAVITY = 800;
    final float JUMP_FORCE = 250;
    float currentJumpForce;

    float CEILING;
    float SCREEN_HEIGHT;
    float SCREEN_WIDTH;
    // Creat a player here
    float playerX = 100;
    float playerWidth = 30;
    float playerHeight = 30;

    // Let the player know to try moving with the space key or the mouse.
    SpriteBatch batch;
    BitmapFont font;
    boolean showTutorial = true;
    //

    enum PipeSpawnType {
        PAIR,
        TOP,
        BOTTOM
    }


    @Override
    public void create() {
        shape = new ShapeRenderer();
        // Let the player know to try moving with the space key or the mouse.
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        //
        playerY = 200;
        velocity = 0;
        pipes = new ArrayList<>();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        CEILING = SCREEN_HEIGHT;
    }

    @Override
    public void render() {
        update();
        draw();
    }

    void update() {
        float delta = Gdx.graphics.getDeltaTime();
        timeAlive += delta;

        // Let the player know to try moving with the space key or the mouse.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            showTutorial = false; // Turn off the tutorial as soon as the player takes action.

        // Hoppa
//        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            velocity = JUMP_FORCE;
//        }
        currentJumpForce = Math.max(150f, JUMP_FORCE - (timeAlive / 10f) * 5);
            velocity = currentJumpForce;
        }

        // Gravitation
        //float currentGravity = GRAVITY + (timeAlive * 10);
        velocity -= GRAVITY * delta;
        //velocity -= currentGravity * delta;

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
            resetGame();
        }
        if (playerY + playerHeight >= CEILING) {
            System.out.println("Game Over - Hit Ceiling");
            resetGame();
        }

        pipeTimer += delta;


        // Narrowing the safe distance (GAP)
        float currentGap = Math.max(90f, GAP - (timeAlive / 5f) * 2);
        // The gap decreases by 2 units every 5 seconds, with a minimum of 90.

//spawn
// When the game starts, adds a pipe pair to the list every 2 seconds, create a new spawn pipe..

        //The pipe spawn time gradually decreases as gameplay progresses(every 40s)
        // (the longer the game, the harder it gets), but it never falls below 1.3 seconds.
        spawnInterval = Math.max(1.3f, 2f - (timeAlive / 40f));
//        if (pipeTimer > 2f) {
        if (pipeTimer > spawnInterval) {
            pipeTimer = 0;
            spawnPipeObstacles(PipeSpawnType.PAIR, currentGap);
        }
        if (pipes != null && !pipes.isEmpty()) {
            Iterator<Pipe> iter = pipes.iterator();
// p.x = horizontal coordinate (x) of the pipe on the screen.

            while (iter.hasNext()) {
                Pipe p = iter.next();
//  Move the pipe left across the screen by decreasing its x-coordinate.
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
                    resetGame();
                    break;
                }
            }
        }
    }

    void resetGame() {
        playerY = 200;
        velocity = 0;
        pipes.clear();
        timeAlive = 0;
        pipeTimer = 0;
    }


    void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(playerX, playerY, playerWidth, playerHeight);
        for (Pipe p : pipes) {
            shape.rect(p.x, p.y, p.width, p.height);
        }
        shape.end();
// Let the player know to try moving with the space key or the mouse.
        if (showTutorial) {
            batch.begin();
            font.draw(batch, "Let's try to move with the space key or the mouse", 50, SCREEN_HEIGHT - 100,
                SCREEN_WIDTH - 100,
                com.badlogic.gdx.utils.Align.center, true);
            batch.end();
        }
        //
    }

    float getPipeSpeed() {
        return 200 + ((int) (timeAlive / 5)) * 20;
    }

    @Override
    public void dispose() {
        if (shape != null) shape.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }

    // For future sprints: we could break this method up into three separate, where PAIR also has a gapSize parameter,
    // and change the constant GAP to gapSize in the method.
    // changes GAP throw gapSize, add the parameter gapSize and use gapSize instead of GAP
    void spawnPipeObstacles(PipeSpawnType type, float currentGap) {
        if (type == PipeSpawnType.PAIR) {
            float gapStart = MIN_PIPE_HEIGHT + (float) (Math.random() * (SCREEN_HEIGHT - currentGap - 2 * MIN_PIPE_HEIGHT));

            pipes.add(new Pipe(SCREEN_WIDTH, 0, PIPE_WIDTH, gapStart));
            pipes.add(new Pipe(SCREEN_WIDTH, gapStart + currentGap, PIPE_WIDTH, SCREEN_HEIGHT - (gapStart + currentGap)));
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

