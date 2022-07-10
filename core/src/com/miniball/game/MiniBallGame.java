package com.miniball.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.miniball.game.screens.*;

public class MiniBallGame extends Game {


	public void create () {
		this.setScreen(new LancementScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void render () {
		super.render();
	}


	public void dispose () {
	}
}