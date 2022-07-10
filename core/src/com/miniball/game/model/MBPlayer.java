package com.miniball.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miniball.game.screens.GameScreen;

public class MBPlayer extends MBElementCirculaire implements MBWithBody{

    private BodyDef bodyDef;
    private Body body;
    private FixtureDef fixtureDef;
    private Fixture fixture;
    private boolean leftDepl;
    private boolean rightDepl;
    private boolean upDepl;
    private boolean downDepl;
    private int score;
    private float mouvement;

    public MBPlayer(float x, float y, float r, Texture t, World m) {
        super(x,y,r,t);
        this.radius= r;
        this.score=0;
        this.mouvement=1400;

        bodyDef=new BodyDef();
        if(GameScreen.PARAMDEFAULT){
            bodyDef.linearDamping= (float) 1.5;
        } else {
            bodyDef.linearDamping= (float) 1.2;
        }
        bodyDef.angularDamping=0.5f;
        bodyDef.position.set(x,y);
        bodyDef.type= BodyDef.BodyType.DynamicBody;

        fixtureDef=new FixtureDef();
        Ellipse shape = new Ellipse(x,y, (float) (0.8*r),r);
        CircleShape cs =new CircleShape();
        cs.setRadius(radius);
        fixtureDef.shape=cs;
        if(GameScreen.PARAMDEFAULT){
            fixtureDef.restitution= (float) 0.25;
        } else {
            fixtureDef.restitution= (float) 0.7;
        }
        fixtureDef.density=1;

        body = m.createBody(bodyDef);
        fixture=body.createFixture(fixtureDef);
        leftDepl=false;
        rightDepl=false;
        upDepl=false;
        downDepl=false;
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
    public void setPos(float posX, float posY) {
        super.setPos(posX, posY);
        body.setTransform(posX,posY,body.getAngle());
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

    public boolean isDownDepl() {
        return downDepl;
    }

    public boolean isLeftDepl() {
        return leftDepl;
    }

    public boolean isRightDepl() {
        return rightDepl;
    }

    public boolean isUpDepl() {
        return upDepl;
    }

    public void setLeftDepl(boolean leftDepl) {
        this.leftDepl = leftDepl;
    }

    public void setRightDepl(boolean rightDepl) {
        this.rightDepl = rightDepl;
    }

    public void setDownDepl(boolean downDepl) {
        this.downDepl = downDepl;
    }

    public void setUpDepl(boolean upDepl) {
        this.upDepl = upDepl;
    }

    public void moove(){
        if (leftDepl){
            body.applyForceToCenter(body.getLinearVelocity().x-mouvement,body.getLinearVelocity().y,true);
        }
        if (rightDepl){
            body.applyForceToCenter(body.getLinearVelocity().x+mouvement,body.getLinearVelocity().y,true);
        }
        if (upDepl){
            body.applyForceToCenter(body.getLinearVelocity().x,body.getLinearVelocity().y+mouvement,true);
        }
        if (downDepl){
            body.applyForceToCenter(body.getLinearVelocity().x,body.getLinearVelocity().y-mouvement,true);
        }
    }

    public Sprite generateSprite(){
        Sprite spr = new Sprite(texture);
        spr.setBounds((getPosX() - (radius)),
                (getPosY() - (radius)),
                (radius * 2),
                (radius * 2));
        spr.setOriginCenter();

        //Enlever cette ligne pour retirer les rotations du joueur
        spr.rotate((float) Math.toDegrees(body.getAngle()));
        return spr;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int n){
        score=n;
    }

    public void setOnlyLeftDepl() {
        setDepl(false,false,false,true);
    }

    public void setOnlyRightDepl() {
        setDepl(false,false,true,false);
    }

    public void setOnlyDownDepl() {
        setDepl(false,true,false,false);
    }

    public void setOnlyUpDepl() {
        setDepl(true,false,false,false);
    }
    public void setDepl(boolean up,boolean down,boolean right,boolean left){
        this.leftDepl = left;
        this.rightDepl = right;
        this.upDepl = up;
        this.downDepl = down;
    }

    public void setMouvement(float mouvement) {
        this.mouvement = mouvement;
    }

    public float getMouvement() {
        return mouvement;
    }
}
