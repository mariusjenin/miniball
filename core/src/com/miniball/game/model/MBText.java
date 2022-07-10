package com.miniball.game.model;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class MBText extends MBElement{
    private String text;
    private int align;
    private float targetWidth;
    private boolean wrap;
    private BitmapFont police;

    public MBText(String t,BitmapFont p,float x, float y, float tw,int a, boolean w) {
        super(x, y);
        this.text=t;
        this.targetWidth=tw;
        this.align=a;
        this.wrap=w;
        this.police=p;
    }

    public BitmapFont getPolice() {
        return police;
    }

    public int getAlign() {
        return align;
    }

    public String getText() {
        return text;
    }

    public boolean isWrap() {
        return wrap;
    }

    public float getTargetWidth() {
        return targetWidth;
    }

    public void dispose(){
        police.dispose();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPolice(BitmapFont police) {
        this.police = police;
    }
}
