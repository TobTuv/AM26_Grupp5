package io.github.jumpyBirb.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import io.github.jumpyBirb.graphics.GameAssets;

public class AudioManager {
    private final Music introMusic;
    private final Music menuMusic;
    private final Music gameMusic;
    private final Sound jumpSound;
    private final Sound crashSound;

    public AudioManager(GameAssets assets) {
        this.introMusic = assets.introMusic;
        this.menuMusic = assets.menuMusic;
        this.gameMusic = assets.gameMusic;
        this.jumpSound = assets.jumpSound;
        this.crashSound = assets.crashSound;
    }

    public void playIntroMusic() {
        stopAllMusic();
        introMusic.play();
    }

    public void playMenuMusic() {
        stopAllMusic();
        menuMusic.play();
    }

    public void playGameMusic() {
        stopAllMusic();
        gameMusic.play();
    }

    public void playJump() {
        jumpSound.play(0.09f);
    }

    public void stopJump() {
        jumpSound.stop();
    }

    public void playCrash() {
        crashSound.play(0.25f);
    }

    private void stopAllMusic() {
        introMusic.stop();
        menuMusic.stop();
        gameMusic.stop();
    }
}
