package io.github.jumpyBirb.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import io.github.jumpyBirb.assets.GameAssets;

/**
 * Handles all audio playback in the game.
 *
 * <p>This class centralizes control of background music and sound effects.
 * It also keeps track of whether music and sound effects are enabled.
 *
 * <p>Only one music track should play at a time, so starting a new music track
 * stops the others first.
 */
public class AudioManager {
    private final Music introMusic;
    private final Music menuMusic;
    private final Music gameMusic;
    private final Sound jumpSound;
    private final Sound crashSound;
    private boolean sound = true;
    private boolean music = true;

    public AudioManager(GameAssets assets) {
        this.introMusic = assets.introMusic;
        this.menuMusic = assets.menuMusic;
        this.gameMusic = assets.gameMusic;
        this.jumpSound = assets.jumpSound;
        this.crashSound = assets.crashSound;
    }

    public void playIntroMusic() {
        if (!music) return;

        stopAllMusic();
        introMusic.play();
    }

    public void playMenuMusic() {
        if (!music) return;

        stopAllMusic();
        menuMusic.play();
    }

    public void playGameMusic() {
        if (!music) return;

        stopAllMusic();
        gameMusic.play();
    }

    public void playJump() {
        if (!sound) return;

        jumpSound.play(0.09f);
    }

    public void stopJump() {
            jumpSound.stop();
    }

    public void playCrash() {
        if (!sound) return;

        crashSound.play(0.25f);
    }

    private void stopAllMusic() {
            introMusic.stop();
            menuMusic.stop();
            gameMusic.stop();
    }

    public void muteSound() {
        sound = false;
    }

    public void muteMusic() {
        stopAllMusic();
        music = false;
    }

    public void unMuteSound() {
        sound = true;
    }

    public void unMuteMusic() {
        music = true;
        playMenuMusic();
    }
}
