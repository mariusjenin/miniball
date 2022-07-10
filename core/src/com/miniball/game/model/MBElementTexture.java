package com.miniball.game.model;

import com.badlogic.gdx.graphics.Texture;

public abstract class MBElementTexture extends MBElement{

    protected Texture texture;

    public MBElementTexture(float x, float y,Texture t) {
        super(x,y);
        this.texture=t;
    }

    public Texture getTexture() {
        return texture;
    }

    public void dispose(){
        texture.dispose();
    }

}
