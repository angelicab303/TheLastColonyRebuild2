package com.mygdx.game.Obstacles.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameCanvas;
import com.mygdx.game.Obstacles.GameObstacle;
import com.mygdx.game.VineTileGraph;
import obstacle.BoxObstacle;
import util.FilmStrip;

public class ScoutEnemy extends Enemy{
    public static class VineTile extends BoxObstacle implements GameObstacle{
        /** X coordinate of this tile */
        private float x;
        /** Y coordinate of this tile */
        private float y;

        public enum Direction {
            /** The enemy is not moving */
            IDLE,
            /** The enemy is moving in the upwards direction */
            UP,
            /** The enemy is moving in the downwards direction */
            DOWN,
            /** The enemy is moving to the right */
            RIGHT,
            /** The enemy is moving to the left */
            LEFT,
            LEFTUP,
            LEFTDOWN,
            RIGHTUP,
            RIGHTDOWN
        }
        /** The textures for the enemy. */

        public Texture currentTexture;
        private Direction direction;
        private boolean finishedGrowing;
        private float scale;
        private float aframe;
        protected FilmStrip currentAnimator;
        public VineTile(float x, float y, float width, float height, FilmStrip animator, float scale, Direction direction) {
            super(x, y, width, height);
//            this.currentTexture = texture;
            currentAnimator = animator;
            this.direction = direction;
            this.scale = scale;
            bodyinfo.fixedRotation = true;
            bodyinfo.type = BodyDef.BodyType.StaticBody;
            aframe = 0.0f;
        }

        public boolean activatePhysics(World world) {
            return super.activatePhysics(world);
        }

//        public void update() {
//            aframe += ANIMATION_SPEED;
//        }

        public void draw(GameCanvas canvas, float currFrame) {
            aframe = ((int)currFrame % 2 == 0 ? 0 : 1);
            currentAnimator.setFrame((int)aframe);
            canvas.draw(currentAnimator, Color.WHITE,body.getWorldCenter().x*drawScale.x, body.getWorldCenter().y*drawScale.y,
                    body.getWorldCenter().x*drawScale.x + (body.getWorldCenter().x*drawScale.x/ canvas.getWidth() * 80f),
                    body.getWorldCenter().y*drawScale.y + (body.getWorldCenter().y*drawScale.y/ canvas.getHeight() * 50f),
                    0.0f, scale, scale);
        }

        @Override
        public ObstacleType getType() {
            return ObstacleType.ENEMY;
        }

        @Override
        public short getCatagoricalBits() {
            return CATEGORY_ENEMY;
        }

        @Override
        public short getMaskBits() {
            return MASK_ENEMY;
        }

        @Override
        public void drawDebug(GameCanvas canvas) {
            super.drawDebug(canvas);
        }
    }
    private final float VINE_ANIMATION_SPEED = 0.1f;
    private final int NUM_ANIM_FRAMES_VINE = 2;
    private long vineTick;
    public boolean isExtendingVines;
    public boolean isExtendingVines(){return isExtendingVines;}
    public void setExtendingVines(boolean value){isExtendingVines = value;}
    public Array<VineTile> vines;
    private VineTile tempVineTile;
    private boolean finishedGrowing;
    private boolean vinesShrinking;
    private World world;
    private static final int MAX_VINES = 12;
    private Texture currentVineTexture;
    private Texture vineTextureVertical;
    private Texture vineTextureHorizontal;
    private Texture vineTextureLeftBottom;
    private Texture vineTextureLeftTop;
    private Texture vineTextureRightBottom;
    private Texture vineTextureRightTop;
    private Texture vineTextureHeadLeft;
    private Texture vineTextureHeadRight;
    private Texture vineTextureHeadDown;
    private Texture vineTextureHeadUp;
    private Texture vineTextureHeadLeftBottom;
    private Texture vineTextureHeadLeftTop;
    private Texture vineTextureHeadRightBottom;
    private Texture vineTextureHeadRightTop;
    private FilmStrip currentVineAnimator;
    private FilmStrip vineAnimatorVertical;
    private FilmStrip vineAnimatorHorizontal;
    private FilmStrip vineAnimatorLeftBottom;
    private FilmStrip vineAnimatorLeftTop;
    private FilmStrip vineAnimatorRightBottom;
    private FilmStrip vineAnimatorRightTop;
    private FilmStrip vineAnimatorHeadLeft;
    private FilmStrip vineAnimatorHeadRight;
    private FilmStrip vineAnimatorHeadDown;
    private FilmStrip vineAnimatorHeadUp;
    private FilmStrip vineAnimatorHeadLeftBottom;
    private FilmStrip vineAnimatorHeadLeftTop;
    private FilmStrip vineAnimatorHeadRightBottom;
    private FilmStrip vineAnimatorHeadRightTop;

    protected float aframevine;

    public boolean areVinesShrinking() {
        return vinesShrinking;
    }

    public void setShrinkVines(boolean value) {
        vinesShrinking = value;
    }
    public void shrinkVines() {

        if (vines.size > 0) {
            vines.pop();
        }
    }

//    public void activateVinePhysics(World world) {vines.activatePhysics(world);}
//    public void setAttackPoint(float x, float y) {attackPoint.x = x; attackPoint.y = y;}
    public void addVineTile(float x, float y, int direction) {
        VineTile.Direction d;
        d = VineTile.Direction.RIGHT;
        currentVineAnimator = vineAnimatorHorizontal;
        switch(direction) {
            case(0):
                d = VineTile.Direction.RIGHT;
                currentVineAnimator = vineAnimatorHeadRight;
                break;
            case(1):
                d = VineTile.Direction.RIGHT;
                currentVineAnimator = vineAnimatorHeadRight;
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.DOWN) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorLeftBottom;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.UP) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorLeftTop;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFT) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorHorizontal;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.RIGHT) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorHorizontal;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.RIGHTUP) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorLeftTop;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.RIGHTDOWN) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorLeftBottom;
                }
                break;
            case(2):
                d = VineTile.Direction.LEFT;
                currentVineAnimator = vineAnimatorHeadLeft;
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.DOWN) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightBottom;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.UP) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightTop;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFT) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorHorizontal;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.RIGHT) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorHorizontal;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFTUP) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightTop;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFTDOWN) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightBottom;
                }
                break;
            case(3):
                d = VineTile.Direction.UP;
                currentVineAnimator = vineAnimatorHeadUp;
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.DOWN) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorVertical;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.UP) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorVertical;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFT) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorLeftBottom;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.RIGHT) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightBottom;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFTUP) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightTop;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFTDOWN) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightBottom;
                }
                break;
            case(4):
                d = VineTile.Direction.DOWN;
                currentVineAnimator = vineAnimatorHeadDown;
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.DOWN) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorVertical;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.UP) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorVertical;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFT) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorLeftTop;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.RIGHT) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightTop;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFTUP) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightTop;
                }
                if (vines.size > 0 && vines.get(vines.size - 1).direction == VineTile.Direction.LEFTDOWN) {
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorRightBottom;
                }
                break;
        }
        switch(d) {
            case IDLE:
                currentVineAnimator = vineAnimatorHorizontal;
                break;
            case UP:
                currentVineAnimator = vineAnimatorHeadUp;
                break;
            case DOWN:
                currentVineAnimator = vineAnimatorHeadDown;
                break;
            case LEFT:
                currentVineAnimator = vineAnimatorHeadLeft;
                break;
            case RIGHT:
                currentVineAnimator = vineAnimatorHeadRight;
                break;
            case RIGHTDOWN:
                currentVineAnimator = vineAnimatorHeadLeftBottom;
                break;
            case RIGHTUP:
                currentVineAnimator = vineAnimatorHeadLeftTop;
                break;
            case LEFTDOWN:
                currentVineAnimator = vineAnimatorHeadRightBottom;
                break;
            case LEFTUP:
                currentVineAnimator = vineAnimatorHeadRightTop;
                break;
        }
        tempVineTile = new VineTile(x, y, currentVineTexture.getWidth()/2*scale, currentVineTexture.getHeight()*scale, currentVineAnimator, scale, d);
        tempVineTile.activatePhysics(world);
        if (vines.size < MAX_VINES && !vinesShrinking) {
            vines.add(tempVineTile);
        }
        else {
            canAttack = false;
            if(vines.size >= MAX_VINES) {
                vinesShrinking = true;
            }
            if(vines.size == 0) {
                vinesShrinking = false;
                canAttack = true;
            }
//            shrinkVines();
//            vines.clear();
        }
    }
//
//    private void updateVines() {
//        for(int i = 0; i < vines.size; i++){
//            vines.get(i).update();
//        }
//    }

    /**
     * Initialize a standard enemy
     *
     * @param x     the x-coordinate of this enemy
     * @param y     the y-coordinate of this enemy
     * @param up
     * @param down
     * @param right
     * @param left
     * @param idle
     * @param scale
     */
    public ScoutEnemy(float x, float y, Texture up, Texture down, Texture right, Texture left, Texture idle, Texture[] vineTextures, float scale, World world) {
        super(x, y, up, down, right, left, idle, scale);
        vines = new Array<>();
        this.world = world;
        vineTick = 0;
        vineTextureVertical = vineTextures[0];
        vineTextureHorizontal = vineTextures[1];
        vineTextureLeftBottom = vineTextures[2];
        vineTextureLeftTop = vineTextures[3];
        vineTextureRightBottom = vineTextures[4];
        vineTextureRightTop = vineTextures[5];
        vineTextureHeadLeft = vineTextures[6];
        vineTextureHeadRight = vineTextures[7];
        vineTextureHeadDown = vineTextures[8];
        vineTextureHeadUp = vineTextures[9];
        vineTextureHeadLeftBottom = vineTextures[10];
        vineTextureHeadLeftTop = vineTextures[11];
        vineTextureHeadRightBottom = vineTextures[12];
        vineTextureHeadRightTop = vineTextures[13];
        currentVineTexture = vineTextureHorizontal;

        vineAnimatorVertical = new FilmStrip(vineTextureVertical,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHorizontal = new FilmStrip(vineTextureHorizontal,NUM_ANIM_FRAMES_VINE,1,NUM_ANIM_FRAMES_VINE);
        vineAnimatorLeftBottom = new FilmStrip(vineTextureLeftBottom,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorLeftTop = new FilmStrip(vineTextureLeftTop,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorRightBottom = new FilmStrip(vineTextureRightBottom,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorRightTop = new FilmStrip(vineTextureRightTop,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHeadLeft = new FilmStrip(vineTextureHeadLeft,NUM_ANIM_FRAMES_VINE,1,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHeadRight = new FilmStrip(vineTextureHeadRight,NUM_ANIM_FRAMES_VINE,1,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHeadDown = new FilmStrip(vineTextureHeadDown,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHeadUp = new FilmStrip(vineTextureHeadUp,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHeadLeftBottom = new FilmStrip(vineTextureHeadLeftBottom,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHeadLeftTop = new FilmStrip(vineTextureHeadLeftTop,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHeadRightBottom = new FilmStrip(vineTextureHeadRightBottom,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorHeadRightTop = new FilmStrip(vineTextureHeadRightTop,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        currentVineAnimator = new FilmStrip(currentVineTexture,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);

        isExtendingVines = false;
        aframevine = 0.0f;
        if (aframevine >= NUM_ANIM_FRAMES_VINE) {
            aframevine -= NUM_ANIM_FRAMES_VINE;
        }
    }

    public void update(int action)
    {
        if(toStunTime >= MAX_TO_STUN_TIME){
            toStunTime = 0;
            this.setStunned(true);
        }
        aframevine += VINE_ANIMATION_SPEED;
        vineTick++;
        if(!isExtendingVines) {
            super.update(action);
        }
        else {
            super.update(0);
//            updateVines();
        }
        if(vinesShrinking && vineTick>20) {
            vineTick = 0;
            shrinkVines();
        }
        if (vines.size == 0) {
            vinesShrinking = false;
            isExtendingVines = false;
        }
    }

    @Override
    public boolean activatePhysics(World world) {
        return super.activatePhysics(world);
    }

    @Override
    public void draw(GameCanvas canvas) {
        if(isExtendingVines) {
            if (vines.size > 0) {
                for (int i = 0; i < vines.size; i++) {
                    vines.get(i).draw(canvas, aframevine);
                }
            }
        }
        super.draw(canvas);
    }
}
