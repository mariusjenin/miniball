package com.miniball.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miniball.game.screens.GameScreen;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MBTerrain extends MBElementRectangulaire implements  MBWithBody{
    private BodyDef bodyDef;
    private Body body;
    private FixtureDef fixtureDef;
    private Fixture fixture;

    public MBTerrain(float x, float y, String f,float w,float h, World m) {
        super(x, y, null,w,h);

        bodyDef=new BodyDef();
        bodyDef.type= BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x,y);

        fixtureDef=new FixtureDef();
        if(GameScreen.PARAMDEFAULT){
            fixtureDef.restitution=0.7f;
        } else {
            fixtureDef.restitution=0.9f;
        }
        fixtureDef.friction=1f;
        ChainShape shape = new ChainShape();
        FileHandle file = Gdx.files.internal(f);
        String text = file.readString();
        String[] arrayCoords = text.split("\\s+");
        Vector2[] vects =new Vector2[arrayCoords.length/2];
        int i=0;
        boolean n=false;
        String coordsX="";
        for (String coords: arrayCoords) {
            if(!n){
                coordsX=coords;
            } else {
                vects[i]=new Vector2((float) ((w*Float.parseFloat(coordsX)/100)-(0.1*w)), h*Float.parseFloat(coords)/100);
                i++;
            }

            n=!n;
        }
        shape.createChain(vects);
        fixtureDef.shape=shape;

        body = m.createBody(bodyDef);
        fixture=body.createFixture(fixtureDef);
    }


    @Override
    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }

    @Override
    public Fixture getFixture() {
        return fixture;
    }

    @Override
    public BodyDef getBodyDef() {
        return bodyDef;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void setPos(float posX, float posY) {
        super.setPos(posX, posY);
        body.setTransform(posX,posY,body.getAngle());
    }
}
