package com.miniball.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class MBJoystick extends MBElementCirculaire{

    public MBJoystick(float x, float y,float r,Texture t) {
        super(x,y,r,t);
        this.radius= r;
    }
}
