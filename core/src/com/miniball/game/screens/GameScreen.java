package com.miniball.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.miniball.game.model.*;

public class GameScreen implements Screen {

    public final static boolean PARAMDEFAULT = false;
    public final static float WIDTHWORLD = 102.4f;
    public final static float HEIGHTWORLD = 76.8f;
    public final static int MATCHTIME = 20;
    private final Game game;
    private final boolean onePlayer;
    private final SpriteBatch spriteBatchScreen;
    private final SpriteBatch spriteBatch;
    private final World monde;
    private final MBTerrain terrain;
    private final MBPlayer j1;
    private final MBPlayer j2;
    private final MBJoystick joystick1;
    private final MBJoystick joystick2;
    private final MBBall ball;
    private final MBGoal goal1;
    private final MBGoal goal2;
    private final MBElementRectangulaire background;
    private final MBElementRectangulaire butMarque;
    private final MBText score1Txt;
    private final MBText score2Txt;
    private final MBText timerTxt;
    private final MBText affichResultMatchTxt1;
    private final MBText affichResultMatchTxt2;
    private final MBText affichResultMatchTxt3;
    private final MBText butTxt;
    private final Box2DDebugRenderer debugRenderer;
    private Timer timer;
    private OrthographicCamera camera, cameraScreen;
    private int valeurTimer;
    private int onGoalScreenTime;
    private int endMatchScreenTime;
    private Vector2 lastTouchJ1 = new Vector2();
    private Vector2 lastTouchJ2 = new Vector2();
    private IAPlayer ia;

    public GameScreen(Game aGame, boolean op) {
        Box2D.init();
        debugRenderer = new Box2DDebugRenderer();

        //Récupère le jeu et set le mode ainsi que le mode de l'ia par défaut
        game = aGame;
        this.onePlayer = op;


        //Initialisation du temps
        setTimer();

        //on créé le SpriteBatch
        spriteBatch = new SpriteBatch();
        spriteBatchScreen = new SpriteBatch();

        //Création de la police
        BitmapFont policeScore;
        BitmapFont policeTimer;
        BitmapFont policeAffichResult;
        BitmapFont policeBut;
        if (PARAMDEFAULT) {
            policeScore = getPoliceAffichage(-1, null, null, -1, null);
            policeTimer = policeScore;
            policeAffichResult = policeScore;
        } else {
            policeScore = getPoliceAffichage(-1, Color.YELLOW, new Color(0f / 255f, 14f / 255f, 85f / 255f, 0.75f), 1, "fonts/LEMONMILK-Medium.otf");
            policeTimer = getPoliceAffichage(-1, new Color(0f / 255f, 14f / 255f, 85f / 255f, 0.75f), null, 0, "fonts/LEMONMILK-Medium.otf");
            policeAffichResult = getPoliceAffichage((int) (110 * Gdx.graphics.getHeight() / 614.4), Color.BLACK, null, 0, "fonts/yorkwhiteletter.ttf");
            policeBut = getPoliceAffichage((int) (150 * Gdx.graphics.getHeight() / 614.4), Color.BLACK, null, 0, "fonts/yorkwhiteletter.ttf");
        }

        //Création du monde
        monde = new World(new Vector2(0, 0), true);

        monde.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if ((fixtureA == ball.getFixture() && (fixtureB == j1.getFixture() || fixtureB == j2.getFixture())) ||
                        (fixtureB == ball.getFixture() && (fixtureA == j1.getFixture() || fixtureA == j2.getFixture()))) {
                    Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.ogg"));
                    sound.play(0.1f);
                } else if (onGoalScreenTime == 0 && endMatchScreenTime == 0) {
                    if (fixtureA == ball.getFixture() && fixtureB == goal1.getFixture()
                            || fixtureB == ball.getFixture() && fixtureA == goal1.getFixture()) {
                        Vector2 v1 = ball.getBody().getLinearVelocityFromLocalPoint(contact.getWorldManifold().getPoints()[0]);
                        Vector2 v2 = goal1.getBody().getLinearVelocityFromLocalPoint(contact.getWorldManifold().getPoints()[0]);
                        float directionX = v1.x - v2.x;
                        if (directionX < 0) {
                            butJoueur(j2);
                            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/goal.ogg"));
                            sound.play(0.3f);
                        }
                    } else if (fixtureA == ball.getFixture() && fixtureB == goal2.getFixture()
                            || fixtureB == ball.getFixture() && fixtureA == goal2.getFixture()) {
                        Vector2 v1 = ball.getBody().getLinearVelocityFromLocalPoint(contact.getWorldManifold().getPoints()[0]);
                        Vector2 v2 = goal2.getBody().getLinearVelocityFromLocalPoint(contact.getWorldManifold().getPoints()[0]);
                        float directionX = v1.x - v2.x;
                        if (directionX > 0) {
                            butJoueur(j1);
                            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/goal.ogg"));
                            sound.play(0.3f);
                        }
                    }
                }

            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

        //Création des modèles
        //modèles selon la taile de l'ecran
        joystick1 = new MBJoystick((float) 0.05 * Gdx.graphics.getWidth(), (float) (0.5 * Gdx.graphics.getHeight()), (float) (Gdx.graphics.getWidth() * 0.045), new Texture("images/Pad.png"));
        joystick2 = new MBJoystick((float) 0.95 * Gdx.graphics.getWidth(), (float) (0.5 * Gdx.graphics.getHeight()), (float) (Gdx.graphics.getWidth() * 0.045), new Texture("images/Pad.png"));
        background = new MBElementRectangulaire((float) (0.1 * Gdx.graphics.getWidth()), 0, new Texture("images/Terrain.png"), (float) (0.8 * Gdx.graphics.getWidth()), Gdx.graphics.getHeight());
        butMarque = new MBElementRectangulaire((float) (0.25 * Gdx.graphics.getWidth()), (float) (0.25 * Gdx.graphics.getHeight()), new Texture("images/But.bmp"), (float) (0.5 * Gdx.graphics.getWidth()), (float) (0.5 * Gdx.graphics.getHeight()));
        score1Txt = new MBText(0 + "", policeScore, (float) 0.25 * Gdx.graphics.getWidth(), (float) (0.98 * Gdx.graphics.getHeight()), 0, Align.center, false);
        timerTxt = new MBText(valeurTimer + "", policeTimer, (float) 0.5 * Gdx.graphics.getWidth(), (float) (0.98 * Gdx.graphics.getHeight()), 0, Align.center, false);
        score2Txt = new MBText(0 + "", policeScore, (float) 0.75 * Gdx.graphics.getWidth(), (float) (0.98 * Gdx.graphics.getHeight()), 0, Align.center, false);
        affichResultMatchTxt1 = new MBText("", policeAffichResult, (float) 0.5 * Gdx.graphics.getWidth(), (float) (0.77 * Gdx.graphics.getHeight() + policeAffichResult.getCapHeight() / 2), 0, Align.center, false);
        affichResultMatchTxt2 = new MBText("Match nul", policeAffichResult, (float) 0.5 * Gdx.graphics.getWidth(), (float) (0.5 * Gdx.graphics.getHeight() + policeAffichResult.getCapHeight() / 2), 0, Align.center, false);
        affichResultMatchTxt3 = new MBText("", policeAffichResult, (float) 0.5 * Gdx.graphics.getWidth(), (float) (0.23 * Gdx.graphics.getHeight() + policeAffichResult.getCapHeight() / 2), 0, Align.center, false);
        if (!PARAMDEFAULT) {
            butTxt = new MBText("\u00d5BUT\u00d4", policeBut, (float) 0.5 * Gdx.graphics.getWidth(), (float) (0.5 * Gdx.graphics.getHeight() + policeAffichResult.getCapHeight() / 2), 0, Align.center, false);
        } else {
            butTxt = null;
        }
        //modèles selon la taille du monde
        ball = new MBBall(getPosXInWorldByScreenXPercentage(0.5f), (float) (0.5 * HEIGHTWORLD), (float) (0.8 * WIDTHWORLD / 85), new Texture("images/Ballon.png"), monde);
        j1 = new MBPlayer(getPosXInWorldByScreenXPercentage(0.25f), (float) (0.5 * HEIGHTWORLD), (float) (0.8 * WIDTHWORLD / 40), new Texture("images/JoueurGauche.png"), monde);
        j2 = new MBPlayer(getPosXInWorldByScreenXPercentage(0.75f), (float) (0.5 * HEIGHTWORLD), (float) (0.8 * WIDTHWORLD / 40), new Texture("images/JoueurDroite.png"), monde);
        terrain = new MBTerrain(0, 0, "models/terrain.txt", WIDTHWORLD, HEIGHTWORLD, monde);
        goal1 = new MBGoal(0, 0, -ball.getRadius(), 1, "models/terrain.txt", WIDTHWORLD, HEIGHTWORLD, monde);
        goal2 = new MBGoal(0, 0, ball.getRadius(), 2, "models/terrain.txt", WIDTHWORLD, HEIGHTWORLD, monde);

        if(onePlayer){
           ia=new IAPlayer(ball,j1,j2);
        }

        putInputProcessors();

    }

    public static BitmapFont getPoliceAffichage(int s, Color c, Color bc, float bw, String strFont) {
        FreeTypeFontGenerator.FreeTypeFontParameter fParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //Size
        if (PARAMDEFAULT || s < 0) {
            fParams.size = (int) (60 * Gdx.graphics.getHeight() / 614.4);
        } else {
            fParams.size = s;
        }

        //Color
        if (PARAMDEFAULT || c == null) {
            fParams.color = new Color(1f, 1f, 0f, 1f);
        } else {
            fParams.color = c;
        }

        //BorderColor
        if (PARAMDEFAULT || bc == null) {
            fParams.borderColor = Color.BLACK;
        } else {
            fParams.borderColor = bc;
        }

        //BorderWidth
        if (PARAMDEFAULT || bw < 0) {
            fParams.borderWidth = (float) (3 * Gdx.graphics.getHeight() / 614.4);
        } else {
            fParams.borderWidth = (float) (bw * Gdx.graphics.getHeight() / 614.4);
        }

        //Genereate
        if (PARAMDEFAULT) {
            FreeTypeFontGenerator fGenDefault = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Comic_Sans_MS_Bold.ttf"));
            return fGenDefault.generateFont(fParams);
        } else {
            FreeTypeFontGenerator fGen = new FreeTypeFontGenerator(Gdx.files.internal(strFont));
            return fGen.generateFont(fParams);
        }
    }

    private void putInputProcessors() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        //ajout de l'input Processor de j1
        InputProcessor j1InputProc = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.Q)
                    j1.setLeftDepl(true);
                if (keycode == Input.Keys.D)
                    j1.setRightDepl(true);
                if (keycode == Input.Keys.Z)
                    j1.setUpDepl(true);
                if (keycode == Input.Keys.S)
                    j1.setDownDepl(true);
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.Q)
                    j1.setLeftDepl(false);
                if (keycode == Input.Keys.D)
                    j1.setRightDepl(false);
                if (keycode == Input.Keys.Z)
                    j1.setUpDepl(false);
                if (keycode == Input.Keys.S)
                    j1.setDownDepl(false);
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if ((screenX < Gdx.graphics.getWidth() * 0.4 && screenX < Gdx.graphics.getWidth() * 0.5) || onePlayer) {
                    lastTouchJ1.set(screenX, screenY);
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if ((screenX < Gdx.graphics.getWidth() * 0.4 && screenX < Gdx.graphics.getWidth() * 0.5) || onePlayer) {
                    Vector2 newTouch = new Vector2(screenX, screenY);
                    // delta will now hold the difference between the last and the current touch positions
                    // delta.x > 0 means the touch moved to the right, delta.x < 0 means a move to the left
                    Vector2 delta;
                    delta = newTouch.cpy().sub(lastTouchJ1);
                    lastTouchJ1 = newTouch;
                    float speed = 0;
                    //La vitesse est différente sur Android et sur Desktop car on a tendance à faire des mouvements plus rapides sur Android
                    switch (Gdx.app.getType()) {
                        case Android:
                            speed = 100f;
                            break;
                        case Desktop:
                            speed = 200f;
                            break;
                    }
                    j1.getBody().applyForceToCenter(delta.x * speed, -delta.y * speed, true);
                }
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        };
        inputMultiplexer.addProcessor(j1InputProc);

        //ajout de l'input Processor de j2 si on est en mode 2 joueurs
        if (!onePlayer) {
            InputProcessor j2InputProc = new InputProcessor() {
                @Override
                public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.LEFT)
                        j2.setLeftDepl(true);
                    if (keycode == Input.Keys.RIGHT)
                        j2.setRightDepl(true);
                    if (keycode == Input.Keys.UP)
                        j2.setUpDepl(true);
                    if (keycode == Input.Keys.DOWN)
                        j2.setDownDepl(true);
                    return false;
                }

                @Override
                public boolean keyUp(int keycode) {
                    if (keycode == Input.Keys.LEFT)
                        j2.setLeftDepl(false);
                    if (keycode == Input.Keys.RIGHT)
                        j2.setRightDepl(false);
                    if (keycode == Input.Keys.UP)
                        j2.setUpDepl(false);
                    if (keycode == Input.Keys.DOWN)
                        j2.setDownDepl(false);
                    return false;
                }

                @Override
                public boolean keyTyped(char character) {
                    return false;
                }

                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if (screenX > Gdx.graphics.getWidth() * 0.6 && screenX > Gdx.graphics.getWidth() * 0.5) {
                        lastTouchJ2.set(screenX, screenY);
                    }
                    return false;
                }

                @Override
                public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                    return false;
                }

                @Override
                public boolean touchDragged(int screenX, int screenY, int pointer) {
                    if (screenX > Gdx.graphics.getWidth() * 0.6 && screenX > Gdx.graphics.getWidth() * 0.5) {
                        Vector2 newTouch = new Vector2(screenX, screenY);
                        // delta will now hold the difference between the last and the current touch positions
                        // delta.x > 0 means the touch moved to the right, delta.x < 0 means a move to the left
                        Vector2 delta;
                        delta = newTouch.cpy().sub(lastTouchJ2);
                        lastTouchJ2 = newTouch;
                        float speed = 0;
                        //La vitesse est différente sur Android et sur Desktop car on a tendance à faire des mouvements plus rapides sur Android
                        switch (Gdx.app.getType()) {
                            case Android:
                                speed = 100f;
                                break;
                            case Desktop:
                                speed = 200f;
                                break;
                        }
                        j2.getBody().applyForceToCenter(delta.x * speed, -delta.y * speed, true);
                    }
                    return false;
                }

                @Override
                public boolean mouseMoved(int screenX, int screenY) {
                    return false;
                }

                @Override
                public boolean scrolled(float amountX, float amountY) {
                    return false;
                }
            };
            inputMultiplexer.addProcessor(j2InputProc);

        }
        InputProcessor retMenuInputProc = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                int xGen = screenX * 886 / Gdx.graphics.getWidth();
                int yGen = screenY * 570 / Gdx.graphics.getHeight();
                if (xGen > 400 && xGen < 490 && yGen > 500 && yGen < 570) {
                    game.setScreen(new MenuScreen(game));
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        };

        inputMultiplexer.addProcessor(retMenuInputProc);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void setTimer() {
        valeurTimer = MATCHTIME;
        timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                stepTimer();
            }
        }, 1, 1);
    }

    public void reset(MBPlayer pl) {
        Vector2 posj1;
        Vector2 posj2;
        boolean newMatch = pl == null;
        if(onePlayer){
            ia.setPause(false);
        }
        if (newMatch || pl.equals(j1)) {
            posj1 = new Vector2(getPosXInWorldByScreenXPercentage(0.25f), HEIGHTWORLD / 2);
        } else {
            posj1 = new Vector2(getPosXInWorldByScreenXPercentage(0.375f), HEIGHTWORLD / 2);
        }
        if (newMatch || pl.equals(j2)) {
            posj2 = new Vector2(getPosXInWorldByScreenXPercentage(0.75f), HEIGHTWORLD / 2);
        } else {
            posj2 = new Vector2(getPosXInWorldByScreenXPercentage(0.625f), HEIGHTWORLD / 2);
        }
        ball.getBody().setTransform(getPosXInWorldByScreenXPercentage(0.5f), HEIGHTWORLD / 2, 0);
        ball.getBody().setLinearVelocity(0, 0);
        ball.getBody().setAngularVelocity(0);
        j1.getBody().setTransform(posj1.x, posj1.y, 0);
        j1.getBody().setLinearVelocity(0, 0);
        j1.getBody().setAngularVelocity(0);
        j2.getBody().setTransform(posj2.x, posj2.y, 0);
        j2.getBody().setLinearVelocity(0, 0);
        j1.getBody().setAngularVelocity(0);
        if (newMatch) {
            setTimer();
            j1.setScore(0);
            j2.setScore(0);
        }
    }

    public void stepTimer() {
        valeurTimer--;
        timerTxt.setText(valeurTimer + "");
        if (valeurTimer == 0) {
            timer.stop();
            endMatch();
        }
    }

    public void butJoueur(final MBPlayer pl) {
        pl.setScore(pl.getScore() + 1);
        final Timer goalTime = new Timer();
        onGoalScreenTime = 3;
            if(onePlayer){
            ia.setPause(true);
        }
        goalTime.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                onGoalScreenTime--;
                if (onGoalScreenTime <= 0) {
                    goalTime.stop();
                    reset(pl);
                }
            }
        }, 0, 1);
    }

    public void endMatch() {
        final Timer endMatchTimer = new Timer();
        endMatchScreenTime = 4;
        onGoalScreenTime = 0;
        endMatchTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (endMatchScreenTime <= 0) {
                    endMatchTimer.stop();
                    reset(null);
                } else {
                    endMatchScreenTime--;
                }
            }
        }, 0, 1);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        camera.update();
        cameraScreen.update();

        monde.step(Gdx.graphics.getDeltaTime(), 60, 1);

        j1.moove();
        if (!onePlayer) {
            j2.moove();
        }

        score1Txt.setText(j1.getScore() + "");
        score2Txt.setText(j2.getScore() + "");

        //Affichage fond noir
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Affichage selon la taille de l'écran
        spriteBatchScreen.getProjectionMatrix().set(cameraScreen.combined);
        spriteBatchScreen.begin();

        //-Affichage Terrain
        spriteBatchScreen.setColor(Color.WHITE);
        spriteBatchScreen.draw(background.getTexture(), (background.getPosX()), (background.getPosY()), (background.getWidth()), (background.getHeight()));

        //-Affichage du but marque
        if (PARAMDEFAULT && onGoalScreenTime > 0) {
            spriteBatchScreen.draw(butMarque.getTexture(), (butMarque.getPosX()), (butMarque.getPosY()), (butMarque.getWidth()), (butMarque.getHeight()));
        }

        //-Affichage Joystick gauche
        spriteBatchScreen.setColor(Color.YELLOW);
        spriteBatchScreen.draw(joystick1.getTexture(),
                (joystick1.getPosX() - (joystick1.getRadius())),
                (joystick1.getPosY() - (joystick1.getRadius())),
                (joystick1.getRadius() * 2),
                (joystick1.getRadius() * 2)
        );

        //-Affichage Joystick droit
        spriteBatchScreen.setColor(Color.CYAN);
        spriteBatchScreen.draw(joystick2.getTexture(),
                (joystick2.getPosX() - (joystick2.getRadius())),
                (joystick2.getPosY() - (joystick2.getRadius())),
                (joystick2.getRadius() * 2),
                (joystick2.getRadius() * 2)
        );
        spriteBatchScreen.end();

        //Affichage selon la taille du monde
        spriteBatch.getProjectionMatrix().set(camera.combined);
        spriteBatch.begin();

        //Affichage Joueur 1
        Sprite spriteJ1 = j1.generateSprite();
        spriteJ1.draw(spriteBatch);

        //Affichage Joueur 2
        Sprite spriteJ2 = j2.generateSprite();
        spriteJ2.draw(spriteBatch);

        //Affichage de la Balle
        Sprite spriteBall = ball.generateSprite();
        spriteBall.draw(spriteBatch);

        spriteBatch.end();

        //Affichage selon la taille de l'écran
        spriteBatchScreen.begin();
        //Affichage des textes

        score1Txt.getPolice().draw(spriteBatchScreen, score1Txt.getText(), (score1Txt.getPosX()), (score1Txt.getPosY()), (score1Txt.getTargetWidth()), score1Txt.getAlign(), score1Txt.isWrap());
        timerTxt.getPolice().draw(spriteBatchScreen, timerTxt.getText(), (timerTxt.getPosX()), (timerTxt.getPosY()), (timerTxt.getTargetWidth()), timerTxt.getAlign(), timerTxt.isWrap());
        score2Txt.getPolice().draw(spriteBatchScreen, score2Txt.getText(), (score2Txt.getPosX()), (score2Txt.getPosY()), (score2Txt.getTargetWidth()), score2Txt.getAlign(), score2Txt.isWrap());
        if (endMatchScreenTime > 0) {
            actualiserAffichEndMatch();
            affichResultMatchTxt1.getPolice().draw(spriteBatchScreen, affichResultMatchTxt1.getText(), (affichResultMatchTxt1.getPosX()), (affichResultMatchTxt1.getPosY()), (affichResultMatchTxt1.getTargetWidth()), affichResultMatchTxt1.getAlign(), affichResultMatchTxt1.isWrap());
            affichResultMatchTxt2.getPolice().draw(spriteBatchScreen, affichResultMatchTxt2.getText(), (affichResultMatchTxt2.getPosX()), (affichResultMatchTxt2.getPosY()), (affichResultMatchTxt2.getTargetWidth()), affichResultMatchTxt2.getAlign(), affichResultMatchTxt2.isWrap());
            affichResultMatchTxt3.getPolice().draw(spriteBatchScreen, affichResultMatchTxt3.getText(), (affichResultMatchTxt3.getPosX()), (affichResultMatchTxt3.getPosY()), (affichResultMatchTxt3.getTargetWidth()), affichResultMatchTxt3.getAlign(), affichResultMatchTxt3.isWrap());
        }
        if (!PARAMDEFAULT && onGoalScreenTime > 0) {
            butTxt.getPolice().draw(spriteBatchScreen, butTxt.getText(), (butTxt.getPosX()), (butTxt.getPosY()), (butTxt.getTargetWidth()), butTxt.getAlign(), butTxt.isWrap());
        }

        spriteBatchScreen.end();



//        ShapeRenderer shapeRenderer = new ShapeRenderer();
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.circle(ball.getPosX(), ball.getPosY(), 0.3f);
//        shapeRenderer.circle(j2.getPosX(), j2.getPosY(), 0.3f);
//
//        shapeRenderer.setColor(Color.GREEN);
//        shapeRenderer.circle(ball.getPosX()+ ball.getRadius(), ball.getPosY()+ ball.getRadius(), 0.4f);
//        shapeRenderer.circle(ball.getPosX()- ball.getRadius(), ball.getPosY()+ ball.getRadius(), 0.4f);
//        shapeRenderer.circle(ball.getPosX()- ball.getRadius(), ball.getPosY()- ball.getRadius(), 0.4f);
//        shapeRenderer.circle(ball.getPosX()+ ball.getRadius(), ball.getPosY()- ball.getRadius(), 0.4f);
//        shapeRenderer.circle(j2.getPosX()+ j2.getRadius(), j2.getPosY()+ j2.getRadius(), 0.4f);
//        shapeRenderer.circle(j2.getPosX()- j2.getRadius(), j2.getPosY()+ j2.getRadius(), 0.4f);
//        shapeRenderer.circle(j2.getPosX()- j2.getRadius(), j2.getPosY()- j2.getRadius(), 0.4f);
//        shapeRenderer.circle(j2.getPosX()+ j2.getRadius(), j2.getPosY()- j2.getRadius(), 0.4f);
//
//        shapeRenderer.setColor(Color.BLACK);
//        shapeRenderer.end();

//        debugRenderer.render(monde, camera.combined);
    }

    private void actualiserAffichEndMatch() {
        if (j1.getScore() == j2.getScore()) {
            affichResultMatchTxt1.setText("");
            affichResultMatchTxt3.setText("");
            if (PARAMDEFAULT) {
                affichResultMatchTxt2.setText("Match nul");
            } else {
                affichResultMatchTxt2.setText("\u00a5MATCH'NUL\u00d4");
            }
        } else {
            if (PARAMDEFAULT) {
                affichResultMatchTxt1.setText("Victoire");
                affichResultMatchTxt2.setText("du");
                if (j1.getScore() > j2.getScore()) {
                    affichResultMatchTxt3.setText("Joueur 1");
                } else {
                    affichResultMatchTxt3.setText("Joueur 2");
                }
            } else {
                affichResultMatchTxt1.setText("ÌVICTOIREÍ");
                affichResultMatchTxt2.setText("!DU!");
                if (j1.getScore() > j2.getScore()) {
                    affichResultMatchTxt3.setText("\u00d0JOUEUR'1\u00d1");
                } else {
                    affichResultMatchTxt3.setText("\u00d0JOUEUR'2\u00d1");
                }
            }
        }
    }

    public static float getPosXInWorldByScreenXPercentage(float f) {
        return (float) (-(0.1 * WIDTHWORLD) + ((f - 0.1) * WIDTHWORLD / 0.8));
    }

    @Override
    public void resize(int w, int h) {
        //Camera de l'Ecran
        cameraScreen = new OrthographicCamera((float) Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraScreen.position.set((float) (Gdx.graphics.getWidth() / 2), (float) (Gdx.graphics.getHeight() * 0.5), 0);

        //Camera du monde
        camera = new OrthographicCamera((float) (WIDTHWORLD / 0.8), HEIGHTWORLD);
        camera.position.set((float) ((WIDTHWORLD / 2) - (0.1 * WIDTHWORLD)), HEIGHTWORLD / 2, 0);

        joystick1.setPos((float) 0.05 * Gdx.graphics.getWidth(), (float) (0.5 * Gdx.graphics.getHeight()));
        joystick1.setRadius((float) (Gdx.graphics.getWidth() * 0.045));
        joystick2.setPos((float) 0.95 * Gdx.graphics.getWidth(), (float) (0.5 * Gdx.graphics.getHeight()));
        joystick2.setRadius((float) (Gdx.graphics.getWidth() * 0.045));
        background.setPos((float) (0.1 * Gdx.graphics.getWidth()), 0);
        background.setBounds((float) (0.8 * Gdx.graphics.getWidth()), Gdx.graphics.getHeight());
        BitmapFont policeScore;
        BitmapFont policeTimer;
        BitmapFont policeAffichResult;
        BitmapFont policeBut;
        if (PARAMDEFAULT) {
            policeScore = getPoliceAffichage(-1, null, null, -1, null);
            policeTimer = policeScore;
            policeAffichResult = policeScore;
        } else {
            policeScore = getPoliceAffichage(-1, Color.YELLOW, new Color(0f / 255f, 14f / 255f, 85f / 255f, 0.75f), 1, "fonts/LEMONMILK-Medium.otf");
            policeTimer = getPoliceAffichage(-1, new Color(0f / 255f, 14f / 255f, 85f / 255f, 0.75f), null, 0, "fonts/LEMONMILK-Medium.otf");
            policeAffichResult = getPoliceAffichage((int) (110 * Gdx.graphics.getHeight() / 614.4), Color.BLACK, null, 0, "fonts/yorkwhiteletter.ttf");
            policeBut = getPoliceAffichage((int) (150 * Gdx.graphics.getHeight() / 614.4), Color.BLACK, null, 0, "fonts/yorkwhiteletter.ttf");
        }
        score1Txt.setPolice(policeScore);
        score1Txt.setPos((float) 0.25 * Gdx.graphics.getWidth(), (float) (0.98 * Gdx.graphics.getHeight()));
        timerTxt.setPolice(policeTimer);
        timerTxt.setPos((float) 0.5 * Gdx.graphics.getWidth(), (float) (0.98 * Gdx.graphics.getHeight()));
        score2Txt.setPolice(policeScore);
        score2Txt.setPos((float) 0.75 * Gdx.graphics.getWidth(), (float) (0.98 * Gdx.graphics.getHeight()));
        affichResultMatchTxt1.setPolice(policeAffichResult);
        affichResultMatchTxt1.setPos((float) 0.5 * Gdx.graphics.getWidth(), (float) (0.77 * Gdx.graphics.getHeight() + policeAffichResult.getCapHeight() / 2));
        affichResultMatchTxt2.setPolice(policeAffichResult);
        affichResultMatchTxt2.setPos((float) 0.5 * Gdx.graphics.getWidth(), (float) (0.5 * Gdx.graphics.getHeight() + policeAffichResult.getCapHeight() / 2));
        affichResultMatchTxt3.setPolice(policeAffichResult);
        affichResultMatchTxt3.setPos((float) 0.5 * Gdx.graphics.getWidth(), (float) (0.23 * Gdx.graphics.getHeight() + policeAffichResult.getCapHeight() / 2));
        if (!PARAMDEFAULT) {
            butTxt.setPolice(policeBut);
            butTxt.setPos((float) 0.5 * Gdx.graphics.getWidth(), (float) (0.5 * Gdx.graphics.getHeight() + policeBut.getCapHeight() / 2));
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        spriteBatchScreen.dispose();
        monde.dispose();
        j1.dispose();
        j2.dispose();
        joystick1.dispose();
        joystick2.dispose();
        ball.dispose();
        background.dispose();
        score1Txt.dispose();
        score2Txt.dispose();
        timerTxt.dispose();
        game.dispose();
        terrain.dispose();
        debugRenderer.dispose();
        if(onePlayer){
            ia.stop();
        }
    }
}