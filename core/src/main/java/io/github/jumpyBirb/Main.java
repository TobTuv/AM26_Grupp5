package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
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
    float pipeSpeed = 200;
    float pipeTimer = 0;

    final float GRAVITY = 600;
    final float JUMP_FORCE = 300;
    final float GAP = 150;
    float CEILING;
    float SCREEN_HEIGHT;
    float SCREEN_WIDTH;
    // Creat a player here
    float playerX = 100;
    float playerWidth = 30;
    float playerHeight = 30;

    @Override
    public void create() {
        shape = new ShapeRenderer();
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

            playerY = 200;
            velocity = 0;
            pipes.clear();
        }
        if (playerY + playerHeight >= CEILING) {
            System.out.println("Game Over - Hit Ceiling");

            playerY = 200;
            velocity = 0;
            pipes.clear();
        }

        pipeTimer += delta;

//spawn
// When the game starts, adds a pipe to the list every 2 seconds, create a new spawn pipe..
        if (pipeTimer > 2f) {
            pipeTimer = 0;
// Create a random vertical position for the gap between top and bottom pipes.
// Ensures the gap stays fully within the screen height.
            float gapStart = (float) (Math.random() * (SCREEN_HEIGHT - GAP));

// Bottom pipe
            pipes.add(new Pipe(SCREEN_WIDTH, 0, 50, gapStart));

// Pipe at the top
            pipes.add(new Pipe(SCREEN_WIDTH, gapStart + GAP, 50, SCREEN_HEIGHT - (gapStart + GAP)));
        }
        if (pipes != null && !pipes.isEmpty()) {
            Iterator<Pipe> iter = pipes.iterator();
// p.x = horizontal coordinate (x) of the pipe on the screen.

            while (iter.hasNext()) {
                Pipe p = iter.next();
//  Move the pipe left across the screen by decreasing its x-coordinate.
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
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}

