package com.miniball.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.miniball.game.screens.GameScreen;


public class IAPlayer {
    private final MBPlayer j2;
    private final MBPlayer j1;
    private final MBBall ball;
    private boolean pause;
    private final Timer timer;

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void stop(){
        timer.stop();
    }

    public IAPlayer(MBBall ball, MBPlayer j1, MBPlayer j2) {
        this.j2 = j2;
        this.j1=j1;
        this.ball = ball;
        this.pause = false;
        j2.setLeftDepl(false);
        j2.setDownDepl(false);
        j2.setRightDepl(false);
        j2.setUpDepl(false);
        timer = new Timer();
        timer.scheduleTask(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                if (!pause) {
                    refreshDepl();
                } else {
                    IAPlayer.this.j2.setDepl(false, false, false, false);
                }
            }
        }, 1, 0.02f);
    }

    public void refreshDepl() {
        float radBall = ball.getRadius();
        float radj2 = j2.getRadius();
        float xBall = ball.getPosX();
        float yBall = ball.getPosY();
        float xJ2 = j2.getPosX();
        float yJ2 = j2.getPosY();
        float distXBallJ2 = (xBall - xJ2);
        float distYBallJ2 = (yBall - yJ2);
        float mouvement = j2.getMouvement();
        float marge = 2;
        boolean moove = true;

       if (yBall + radBall < yJ2 + radj2 + 1 && yBall - radBall > yJ2 - radj2 - 1 && xBall < xJ2) {
            //       -
            //   o  ( )
            //       -
            j2.setOnlyLeftDepl();
        } else if (xBall < xJ2 && ((distXBallJ2 < distYBallJ2 + marge && distXBallJ2 > distYBallJ2 - marge) || (distYBallJ2 < distXBallJ2 + marge && distYBallJ2 > distXBallJ2 - marge))) {
            if (yBall < yJ2) {
                //     ( )
                //    /
                //   o
                //
                j2.setDepl(false, true, false, true);
            } else {
                //
                //   o
                //    \
                //     ( )
                j2.setDepl(true, false, false, true);
            }
        } else  {
           boolean maxDistIsY = Math.abs(distXBallJ2) < Math.abs(distYBallJ2);
           if (yBall > GameScreen.HEIGHTWORLD / 2) {
               if (yJ2 > yBall && xJ2 > xBall) {
                   //       ( )
                   //    o
                   //
                   if (maxDistIsY) {
                       float mult = distXBallJ2 / distYBallJ2;
                       j2.getBody().applyForceToCenter(mult * mouvement,  - mouvement, true);
                   } else {
                       float mult = distYBallJ2 / distXBallJ2;
                       j2.getBody().applyForceToCenter( - mouvement,  mult * mouvement, true);
                   }
                   moove = false; //On ne moove pas car on a dejà fait un mouvement custom
               } else if (yJ2 < yBall && xJ2 - radj2 > xBall + radBall) {
                   //
                   //    o
                   //       ( )
                   j2.setOnlyUpDepl();
               } else if (yJ2 - radj2 > yBall + radBall && xJ2 < xBall) {
                   // ( )
                   //     o
                   //
                   j2.setOnlyRightDepl();
               } else {
                   //  ^
                   //  |  o
                   // ( )->
                   if (yJ2 + radj2 < yBall - radBall) {
                       //
                       //  ___0___
                       // ( )( )( )
                       j2.setDepl(true,false,true,false);
                   } else if (xJ2 + radj2 < xBall - radBall) {
                       // ( )|
                       // (-)| o
                       // ( )|
                       j2.setOnlyDownDepl();
                   }
               }
           } else {
               if (yJ2 < yBall && xJ2 > xBall) {
                   //
                   //    o
                   //       ( )
                   if (maxDistIsY) {
                       float mult = distXBallJ2 / distYBallJ2;
                       float signe = 1;
                       if (distYBallJ2 < 0) {
                           signe = -1;
                       }
                       j2.getBody().applyForceToCenter( mult * mouvement,  signe * mouvement, true);
                   } else {
                       float mult = distYBallJ2 / distXBallJ2;
                       j2.getBody().applyForceToCenter( - mouvement,  mult * mouvement, true);
                   }
                   moove = false; //On ne moove pas car on a dejà fait un mouvement custom
               } else if (yJ2 > yBall && xJ2 - radj2 > xBall + radBall) {
                   //       ( )
                   //    o
                   //
                   j2.setOnlyDownDepl();
               } else if (yJ2 + radj2 < yBall - radBall && xJ2 < xBall) {
                   //
                   //     o
                   // ( )
                   j2.setOnlyRightDepl();
               } else {
                   // ( )->
                   //  |  o
                   //  v
                   if (yJ2 - radj2 > yBall + radBall) {
                       // ( )( )( )
                       //  ---o---
                       //
                       j2.setDepl(false,true,true,false);
                   } else if (xJ2 + radj2 < xBall - radBall) {
                       // ( )|
                       // (-)| o
                       // ( )|
                       j2.setOnlyUpDepl();
                   }
               }
           }
       }
        if (moove) {
            j2.moove();
        }
    }
}
