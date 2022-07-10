package com.miniball.game.model;

import com.badlogic.gdx.graphics.Texture;

public abstract class MBElementCirculaire extends MBElementTexture{

    protected float radius;

    public MBElementCirculaire(float x, float y, float r,Texture t) {
        super(x,y,t);
        this.radius=r;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
