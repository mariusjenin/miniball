package com.miniball.game.model;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public interface MBWithBody {
    FixtureDef getFixtureDef();
    Fixture getFixture();
    BodyDef getBodyDef();
    Body getBody();
    void setPos(float posX, float posY);
}
