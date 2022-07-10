package com.miniball.game.model;

import com.badlogic.gdx.graphics.Texture;

public abstract class MBElement {

    protected float posX;
    protected float posY;

    public MBElement(float x, float y) {
        this.posX=x;
        this.posY=y;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPos(float posX,float posY) {
        this.posX = posX;
        this.posY = posY;
    }
}