package io.github.jumpyBirb.graphics;

import com.badlogic.gdx.graphics.Texture;

public class GameAssets {
    public final Texture background = new Texture("background.png");
    public final Texture player = new Texture("player.png");
    public final Texture skyscraper = new Texture("skyscraper.png");
    public final Texture pod = new Texture("podPlatform.png");
    public final Texture parallax1 = new Texture("clouds-parallax1-3000x1080.png");
    public final Texture parallax2 = new Texture("city-parallax2-1920x1080.png");
    public final Texture parallax3 = new Texture("smog-parallax3-3000x1080.png");

    public void dispose() {
        background.dispose();
        player.dispose();
        skyscraper.dispose();
        pod.dispose();
        parallax1.dispose();
        parallax2.dispose();
        parallax3.dispose();
    }
}
