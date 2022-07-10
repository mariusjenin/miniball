package com.miniball.game.desktop;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.miniball.game.MiniBallGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width= 1280;
		config.height= 768;
		config.resizable=true;
		new LwjglApplication(new MiniBallGame(), config);
	}
}
