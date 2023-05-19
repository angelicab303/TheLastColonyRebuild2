package com.mygdx.game.Obstacles.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
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
        private int behind = 0;

        private int NUM_ANIM_FRAMES = 2;

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
        protected FilmStrip[] digStrips;
        private Direction direction;
        private boolean finishedGrowing;
        private float scale;
        private float aframe;
        protected FilmStrip currentAnimator;

        private static volatile Filter filter;

        protected boolean isStunned;
        public void setStunned(boolean stunned) {
            isStunned = stunned;
        }
        public VineTile(float x, float y, float width, float height, FilmStrip animator, float scale, Direction direction) {
            super(x, y, width, height);
            isStunned = false;
            currentAnimator = animator;
            this.direction = direction;
            this.scale = scale;
            bodyinfo.fixedRotation = true;
            bodyinfo.type = BodyDef.BodyType.StaticBody;

            filter = new Filter();
            filter.categoryBits = GameObstacle.CATEGORY_VINE;
            filter.maskBits = GameObstacle.MASK_VINE;

            aframe = 0.0f;
        }

        public boolean activatePhysics(World world) {
            if (!super.activatePhysics(world)) {
                return false;
            }
            setFilterData(filter);
            return true;
        }

        public void createFixtures(World world) {
            super.createFixtures();
            filter = new Filter();
            filter.categoryBits = GameObstacle.CATEGORY_VINE;
            filter.maskBits = GameObstacle.MASK_VINE;
        }

//        public void update() {
//            aframe += ANIMATION_SPEED;
//        }

        public void draw(GameCanvas canvas, float currFrame, float tileSize) {
            aframe = ((int)currFrame % 2 == 0 ? 0 : 1);
            currentAnimator.setFrame((int)aframe);
            canvas.draw(currentAnimator, Color.WHITE,body.getWorldCenter().x*drawScale.x, body.getWorldCenter().y*drawScale.y,
                    body.getWorldCenter().x + (getWidth() * drawScale.x),
                    body.getWorldCenter().y,
                    0.0f, scale, scale);
        }

        @Override
        public ObstacleType getType() {
            return ObstacleType.VINE;
        }

        @Override
        public short getCatagoricalBits() {
            return CATEGORY_VINE;
        }

        @Override
        public short getMaskBits() {
            return MASK_VINE;
        }

        @Override
        public void incBehind(int inc){
            behind += inc;
        }

        @Override
        public void drawDebug(GameCanvas canvas) {
            super.drawDebug(canvas);
        }
    }
    private boolean isChasingSurvivor;
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

    private Texture vineTextureClosedLeft;
    private Texture vineTextureClosedRight;
    private Texture vineTextureClosedDown;
    private Texture vineTextureClosedUp;
    private Texture vineTextureClosedLeftTop;
    private Texture vineTextureClosedLeftBottom;
    private Texture vineTextureClosedRightTop;
    private Texture vineTextureClosedRightBottom;
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

    private FilmStrip vineAnimatorClosedLeft;
    private FilmStrip vineAnimatorClosedRight;
    private FilmStrip vineAnimatorClosedDown;
    private FilmStrip vineAnimatorClosedUp;
    private FilmStrip vineAnimatorClosedLeftBottom;
    private FilmStrip vineAnimatorClosedLeftTop;
    private FilmStrip vineAnimatorClosedRightBottom;
    private FilmStrip vineAnimatorClosedRightTop;

    protected float aframevine;

    protected int vineStunTime;

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
        if (vines.size > 0) {
            VineTile.Direction d = vines.peek().direction;
            switch (d) {
                case IDLE:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorHorizontal;
                    break;
                case UP:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorClosedUp;
                    break;
                case DOWN:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorClosedDown;
                    break;
                case LEFT:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorClosedLeft;
                    break;
                case RIGHT:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorClosedRight;
                    break;
                case RIGHTDOWN:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorClosedLeftBottom;
                    break;
                case RIGHTUP:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorClosedLeftTop;
                    break;
                case LEFTDOWN:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorClosedRightBottom;
                    break;
                case LEFTUP:
                    vines.get(vines.size - 1).currentAnimator = vineAnimatorClosedRightTop;
                    break;
            }
        }
    }

    //    public void activateVinePhysics(World world) {vines.activatePhysics(world);}
//    public void setAttackPoint(float x, float y) {attackPoint.x = x; attackPoint.y = y;}
    public void addVineTile(float x, float y, int direction) {
        VineTile.Direction d;
        d = VineTile.Direction.RIGHT;
        currentVineAnimator = vineAnimatorHorizontal;
        if ((vines.size > 0 && !vines.get(vines.size - 1).isStunned) || vines.isEmpty()) {
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
        tempVineTile = new VineTile(x, y, currentVineTexture.getWidth()*scale, currentVineTexture.getHeight()*scale/2, currentVineAnimator, scale, d);
        tempVineTile.activatePhysics(world);
        tempVineTile.createFixtures(world);
        if (vines.size < MAX_VINES && !vinesShrinking) {
            vines.add(tempVineTile);
        }
        else {
            canAttack = false;
            if (vines.size >= MAX_VINES) {
                vinesShrinking = true;
            }
            if (vines.size == 0) {
                vinesShrinking = false;
                canAttack = true;
            }
//            shrinkVines();
//            vines.clear();
        }
        }
    }
//
//    private void updateVines() {
//        for(int i = 0; i < vines.size; i++){
//            vines.get(i).update();
//        }
//    }
    // Variables for scout
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 7;
    private static final int   ATTACK_NUM_ANIM_FRAMES = 14;
    /** How fast we change frames (one frame per 10 calls to update) */
    protected static final float ANIMATION_SPEED = 0.20f;
    protected static float WAKE_ANIMATION_SPEED = 0.20f;

    /**
     * Initialize a standard enemy
     *
     * @param x     the x-coordinate of this enemy
     * @param y     the y-coordinate of this enemy
     * @param animator
     * @param scale
     */
    public ScoutEnemy(float x, float y, FilmStrip[][] animator, Texture[] vineTextures, float scale, float tileSize, World world) {
        super(x, y, animator, scale, tileSize, false);
        vines = new Array<>();
        this.world = world;
        vineTick = 0;
        isChasingSurvivor = false;
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

        vineTextureClosedLeft = vineTextures[14];
        vineTextureClosedRight = vineTextures[15];
        vineTextureClosedDown = vineTextures[16];
        vineTextureClosedUp = vineTextures[17];
        vineTextureClosedLeftTop = vineTextures[18];
        vineTextureClosedLeftBottom = vineTextures[19];
        vineTextureClosedRightTop = vineTextures[20];
        vineTextureClosedRightBottom = vineTextures[21];

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

        vineAnimatorClosedLeft = new FilmStrip(vineTextureClosedLeft,NUM_ANIM_FRAMES_VINE,1,NUM_ANIM_FRAMES_VINE);
        vineAnimatorClosedRight = new FilmStrip(vineTextureClosedRight,NUM_ANIM_FRAMES_VINE,1,NUM_ANIM_FRAMES_VINE);
        vineAnimatorClosedDown = new FilmStrip(vineTextureClosedDown,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorClosedUp = new FilmStrip(vineTextureClosedUp,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorClosedLeftBottom = new FilmStrip(vineTextureClosedLeftBottom,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorClosedLeftTop = new FilmStrip(vineTextureClosedLeftTop,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorClosedRightBottom = new FilmStrip(vineTextureClosedRightBottom,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);
        vineAnimatorClosedRightTop = new FilmStrip(vineTextureClosedRightTop,1,NUM_ANIM_FRAMES_VINE,NUM_ANIM_FRAMES_VINE);


        isExtendingVines = false;
        aframevine = 0.0f;
        if (aframevine >= NUM_ANIM_FRAMES_VINE) {
            aframevine -= NUM_ANIM_FRAMES_VINE;
        }
    }

    @Override
    public void update(int action)
    {
        if (isAttacking){
            System.out.println("Scout is attacking");
            System.out.println("Aframes: " + aframe);
            if (aframe >= ATTACK_NUM_ANIM_FRAMES) {
                aframe = 7;
            }
            aframe += ANIMATION_SPEED;
        }
        else if (isStunned()){
            if (stunTime <= 1){
                aframe = 0;
            }
//            if (stunTime >= MAX_STUN_TIME-2){
//                aframe = 0;
//            }
            if (aframe >= NUM_ANIM_FRAMES){
                aframe = NUM_ANIM_FRAMES-1;
            }
            else{
                aframe += ANIMATION_SPEED;
            }
        }
        else{
            aframe += ANIMATION_SPEED;
            if (aframe >= NUM_ANIM_FRAMES) {
                aframe -= NUM_ANIM_FRAMES;
            }
        }


        ////////////////////////////////////////////////////////////////////
        if(toStunTime >= MAX_TO_STUN_TIME){
            toStunTime = 0;
            this.setStunned(true);
        }
//        if(!vines.isEmpty() && vines.peek().isStunned) {
//            isExtendingVines = false;
//            vineStunTime++;
//        }
//        if(vineStunTime > MAX_TO_STUN_TIME) {
//            vineStunTime = 0;
//            vinesShrinking = true;
//        }
        aframevine += VINE_ANIMATION_SPEED;
        vineTick++;
        if(!isExtendingVines) {
            calculateMovement(action);
            super.update(action);
        }
        else {
            calculateMovement(0);
            super.update(0);
//            updateVines();
        }
        if ((vines.size > 0 && vines.peek().isStunned) || stunned) {
            vinesShrinking = true;
            stunned = true;
        }
        if(vinesShrinking && vineTick>10) {
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
        if (!hasAwoken && !wokeOnce && wakeTime <= 20){
            //System.out.println("Frame Skull");
            currentAnimator.setFrame(0);
        }
        else{
            currentAnimator.setFrame((int)aframe);
        }
        currentAnimator.setFrame((int)aframe);
////        System.out.println((body.getWorldCenter().x*drawScale.x - currentAnimator.getRegionWidth()*scale/2) + ", " + (body.getWorldCenter().y*drawScale.y- currentAnimator.getRegionHeight()*scale/2));
        if (stunCooldown > 0 && stunCooldown % 10 == 0)
        {
            canvas.draw(currentAnimator, Color.CLEAR, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - width*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, 0.0f, scale, scale);
        }
        else if (isStunned())
        {
            canvas.draw(currentAnimator, Color.PINK, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - width*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, 0.0f, scale, scale);
            if(stunAnimation != null){
//                System.out.println(stunTime % 4);
                stunAnimation.setFrame(((int)stunTime/5) % 4); //4 frames of animation, 5 frame rate reduction
                canvas.draw(stunAnimation, Color.GRAY, origin.x, origin.y, body.getWorldCenter().x * drawScale.x - width*scale/2, body.getWorldCenter().y * drawScale.y, 0.0f, scale, scale);
            }
        }
        else {
            canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x * drawScale.x - width * scale / 2, body.getWorldCenter().y * drawScale.y - height * scale / 2, 0.0f, scale, scale);
        if(isExtendingVines) {
            if (vines.size > 0) {
                for (int i = 0; i < vines.size; i++) {
                    vines.get(i).draw(canvas, aframevine, width);
                }
            }
        }
        super.draw(canvas);
    }}

    @Override
    public void drawDebug(GameCanvas canvas) {
        for(int i = 0; i < vines.size; i++) {
            vines.get(i).drawDebug(canvas);
        }
        super.drawDebug(canvas);
    }

    public Enemy.EnemyType getEnemyType() {
        return EnemyType.SCOUT;
    }
}