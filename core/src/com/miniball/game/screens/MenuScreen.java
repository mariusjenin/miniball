package com.miniball.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {

    private Game game;
    private SpriteBatch spriteBatch;
    private Texture textureBg;
    private Texture textureMenu;
    private OrthographicCamera centeredCamera;
    private int width;
    private int height;

    public MenuScreen(Game aGame) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        game = aGame;
        spriteBatch = new SpriteBatch();
        textureBg = new Texture("images/Intro.jpg");
        textureMenu = new Texture("images/Menu.jpg");
        centeredCamera = new OrthographicCamera(width, height);
        centeredCamera.position.set(width / 2f, height / 2f, 0);
        centeredCamera.update();

        putInputProcessor();
    }

    private void putInputProcessor() {
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//                game.setScreen(new GameScreen(game));
                int xGen= screenX*886/width;
                int yGen= screenY*570/height;
                if(xGen > 301 && xGen<585 && yGen>85 && yGen < 485){
                    //Clic sur le Menu
                    if(yGen<195){
                        game.setScreen(new GameScreen(game,true));
                    }else if(yGen<295){
                        game.setScreen(new GameScreen(game,false));
                    }else if(yGen>395){
                        Gdx.app.exit();
                    }
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        spriteBatch.draw(textureBg, 0, 0, width, height);

        //On affiche le menu qui est resizé pour etre bien adapté à l'écran
        spriteBatch.draw(textureMenu,
                centeredCamera.position.x - (textureMenu.getWidth() / 2f * width / 886),
                centeredCamera.position.y - (textureMenu.getHeight() / 2f * height / 570),
                textureMenu.getWidth() * width / 886f,
                textureMenu.getHeight() * height / 570f
        );
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