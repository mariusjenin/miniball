package com.miniball.game.model;

import com.badlogic.gdx.graphics.Texture;

public class MBElementRectangulaire extends MBElementTexture{
    protected float width;
    protected float height;

    public MBElementRectangulaire(float x, float y, Texture t,float w,float h) {
        super(x,y,t);
        this.width=w;
        this.height=h;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setBounds(float w,float h){
        this.width=w;
        this.height=h;
    }
}
