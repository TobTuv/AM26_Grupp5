package io.github.jumpyBirb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Main extends ApplicationAdapter {

    ShapeRenderer shape;

    float playerY;
    float velocity;

    final float GRAVITY = 600;
    final float JUMP_FORCE = 300;
    final float CEILING = 400;

    @Override
    public void create() {
        shape = new ShapeRenderer();
        playerY = 200;
        velocity = 0;
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
        if (playerY < 0) {
            System.out.println("Game Over");
            playerY = 200;
            velocity = 0;
        }
    }

    void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(100, playerY, 50, 50); // x, y, width, height
        shape.end();
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}
