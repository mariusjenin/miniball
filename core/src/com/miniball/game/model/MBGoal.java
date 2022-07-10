package com.miniball.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MBGoal extends MBElement implements MBWithBody {

    private final BodyDef bodyDef;
    private final Body body;
    private final FixtureDef fixtureDef;
    private final Fixture fixture;

    public MBGoal(float x, float y, float dec,int n, String f,float w, float h,World m) {
        super(x, y);

        bodyDef=new BodyDef();
        bodyDef.position.set(x,y);
        bodyDef.type= BodyDef.BodyType.StaticBody;

        fixtureDef=new FixtureDef();


        ChainShape shape = new ChainShape();
        FileHandle file = Gdx.files.internal(f);
        String text = file.readString();
        String[] arrayCoords = text.split("\\s+");
        int numCoords;
        if(n==1){
            numCoords=26;
        } else{
            numCoords=10;
        }
        Vector2 pt1=new Vector2((float) ((w*Float.parseFloat(arrayCoords[numCoords])/100)-(0.1*w)+dec), (float) (h*Float.parseFloat(arrayCoords[numCoords+1])/100.0));
        Vector2 pt2=new Vector2((float) ((w*Float.parseFloat(arrayCoords[numCoords])/100)-(0.1*w)+dec), (float) (h*Float.parseFloat(arrayCoords[numCoords+7])/100.0));
        Vector2[] vects= new Vector2[]{pt1,pt2};

        shape.createChain(vects);
        fixtureDef.shape=shape;
        fixtureDef.isSensor=true;

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
