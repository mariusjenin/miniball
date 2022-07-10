package com.miniball.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;

public class LancementScreen implements Screen {

    private Game game;
    private Texture textureBg;
    private SpriteBatch spriteBatch;
    private int width;
    private int height;

    public LancementScreen(Game aGame) {
        game = aGame;
        width=Gdx.graphics.getWidth();
        height=Gdx.graphics.getHeight();
        spriteBatch=new SpriteBatch();
        textureBg = new Texture("images/Intro.jpg");

        //On lance l'écran MenuScreen au bout de 3 secondes
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new MenuScreen(game));
            }
        }, (float) 3);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        spriteBatch.draw(textureBg, 0, 0,width,height);
        spriteBatch.end();
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        textureBg.dispose();
        spriteBatch.dispose();
    }
}