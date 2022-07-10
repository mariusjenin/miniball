package com.miniball.game.model;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miniball.game.screens.GameScreen;

import java.util.Vector;

public class MBBall extends MBElementCirculaire implements MBWithBody{

    private final BodyDef bodyDef;
    private final Body body;
    private final FixtureDef fixtureDef;
    private final Fixture fixture;

    public MBBall(float x, float y,float r,Texture t, World m) {
        super(x,y,r,t);
        this.radius= r;

        bodyDef=new BodyDef();
        bodyDef.type= BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        if(GameScreen.PARAMDEFAULT){
            bodyDef.linearDamping= (float) 1.5;
        } else {
            bodyDef.linearDamping= (float) 0.8;
        }
        bodyDef.angularDamping=0.5f;

        fixtureDef=new FixtureDef();
        CircleShape cs =new CircleShape();
        cs.setRadius(radius);
        fixtureDef.shape=cs;
        fixtureDef.density=1;
        fixtureDef.restitution= (float) 0.5;


        body = m.createBody(bodyDef);
        fixture=body.createFixture(fixtureDef);
    }

    @Override
    public float getPosX() {
        return body.getPosition().x;
    }

    @Override
    public float getPosY() {
        return body.getPosition().y;
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

    public Sprite generateSprite(){
        Sprite spr = new Sprite(texture);
        spr.setBounds( (getPosX() - (radius)),
                (getPosY() - (radius)),
                (radius * 2),
               (radius * 2));
        spr.setOriginCenter();

        //Enlever cette ligne pour retirer les rotations du joueur
        spr.rotate((float) Math.toDegrees(body.getAngle()));
        return spr;
    }

    @Override
    public void setPos(float posX, float posY) {
        super.setPos(posX, posY);
        body.setTransform(posX,posY,body.getAngle());
    }
}
