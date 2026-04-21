package io.github.jumpyBirb.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import io.github.jumpyBirb.graphics.GameAssets;

public class AudioManager {
    private final Music menuMusic;
    private final Music gameMusic;
    private final Sound jumpSound;
    private final Sound crashSound;

    public AudioManager(GameAssets assets) {
        this.menuMusic = assets.menuMusic;
        this.gameMusic = assets.gameMusic;
        this.jumpSound = assets.jumpSound;
        this.crashSound = assets.crashSound;
    }

    public void playMenuMusic() {
        if (gameMusic.isPlaying()) {
            gameMusic.stop();
        }
        if (!menuMusic.isPlaying()) {
            menuMusic.play();
        }
    }

    public void playGameMusic() {
        if (menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (!gameMusic.isPlaying()) {
            gameMusic.play();
        }
    }

    public void playJump() {
        jumpSound.play(0.15f);
    }

    public void stopJump() {
        jumpSound.stop();
    }

    public void playCrash() {
        crashSound.play(0.4f);
    }
}

