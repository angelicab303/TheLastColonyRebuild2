package com.mygdx.game.Obstacles.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.GameCanvas;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import util.FilmStrip;

public class ShriekerEnemy extends Enemy {
    // Constants

    /** The physics shape of this object's sensor */
    private CircleShape sensorShape;
    /** Whether or not the shrieker is able to wake */
    private boolean canWake;
    /** Whether or not the shrieker is waking */
    private boolean isWaking;
    /** Time shrieker takes to fully wake */
    private float wakeTime = 0;
    /** Time shrieker must wait to shriek when waking */
    private float WAKE_TIME = 300;
    /** Whether or not the shrieker can alert other enemies */
    private boolean canShriek;
    /** Time shrieker must wait before being able to alert again */
    protected final float SHRIEK_TIME = 420;
    /** Time that has passed since shrieker last alerted */
    private float shriekTime = 0;
    /** Whether the shrieker is shrieking */
    private boolean isShrieking;
    /** If the shrieker recently shrieked */
    private boolean justShrieked;
    /** Cooldown time after shrieking */
    private int postShriekTime = 0;
    /** Time shrieker must wait to wake after shrieking */
    private float POST_SHRIEK_COOLDOWN = 1000;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 9;
    /** The textures for the enemy. */
    protected Texture textureIdle;
    protected Texture textureAlert;
    protected Texture textureShriek;
    /** Current texture to be used for the enemy */
    protected Texture currentTexture;
    /** Filmstrip for the enemy */
    protected FilmStrip animatorIdle;
    protected FilmStrip animatorAlert;
    protected FilmStrip animatorShriek;
    protected FilmStrip currentAnimator;
    private final float SHRIEKER_ANIMATION_SPEED = 0.1f;

    /**
     * Initialize a shrieker enemy: enemy that alerts other enemies to player location.
     *
     * @param x the x-coordinate of this enemy
     * @param y the y-coordinate of this enemy
     * @param animator the filstrips for this enemy
     * @param scale the scale used to draw for this enemy
     */
    public ShriekerEnemy(float x, float y, FilmStrip[] animator, float scale, float tileSize){
        super(x, y, animator, scale, tileSize, true);
        isShrieking = false;
        canShriek = false;
        justShrieked = false;
        canWake = true;
        currentTexture = textureIdle;

        setBodyType(BodyDef.BodyType.StaticBody);

        animatorAlert = animator[2];
        animatorShriek = animator[1];
        animatorIdle = animator[0];
        currentAnimator = animatorIdle;
        aframe = 0.0f;



    }

    /**
     * Sets whether the enemy is shrieking.
     * Used to alert other enemies to its location
     * @param waking
     */
    public void setWaking(boolean waking){
        this.isWaking = waking;
    }
    /**
     * Sets the wake time.
     * Used to alert other enemies to its location
     * @param wakeTime
     */
    public void setWakeTime(float wakeTime){
        this.wakeTime = wakeTime;
    }
    /**
     * Sets whether the enemy is shrieking.
     * Used to alert other enemies to its location
     * @param shrieking
     */
    public void setShrieking(boolean shrieking){
        this.isShrieking = shrieking;
    }
    /**
     * Sets whether the enemy can shriek.
     * Used to alert other enemies to its location
     * @param canShriek
     */
    public void setCanShriek(boolean canShriek){
        this.canShriek = canShriek;
    }

    /**
     * Returns whether the shrieker is able to shriek.
     * @return canShriek
     */
    public boolean canShriek(){
        return canShriek;
    }
    /**
     * Sets whether the enemy can wake.
     *
     * @param canWake
     */
    public void setCanWake(boolean canWake){
        this.canWake = canWake;
    }

    /**
     * Returns whether the shrieker is able to wake.
     * @return canWake
     */
    public boolean canWake(){
        return canWake;
    }

    /**
     * Updates the animation state of the Shrieker.
     *
     */
    public void updateAnimator(){
        if (isWaking){
            currentAnimator = animatorAlert;
            if (aframe > 3){
                aframe -= aframe;
            }
        }
        else if (isShrieking){
            currentAnimator = animatorShriek;
            if (aframe >= NUM_ANIM_FRAMES){
                aframe = 5;
            }
        }
        else currentAnimator = animatorIdle;
        if (aframe >= NUM_ANIM_FRAMES) {
            aframe -= NUM_ANIM_FRAMES;
        }
    }

    /**
     * Updates the positions of enemies based on their chosen action.
     * Also updates the stun time of stunned enemies and the attack time of enemies on cooldown.
     * @param action the action that this enemy will take
     */
    public void update(int action)
    {
        if(toStunTime >= MAX_TO_STUN_TIME){
            toStunTime = 0;
            this.setStunned(true);
        }
        body.setAwake(true);
        if (damaged)
        {
            stunCooldown++;
            if (stunCooldown >= MAX_STUN_COOLDOWN)
            {
                stunCooldown = 0;
                damaged = false;
            }
        }
//        super.update(action);
        // Count down for when the shrieker can wake again after shrieking
        if (justShrieked){
            postShriekTime++;
            if (postShriekTime >= POST_SHRIEK_COOLDOWN){
                canWake = true;
                justShrieked = false;
                postShriekTime = 0;
            }

            else {
                canWake = false;
            }
        }

        if (isStunned())
        {
            canShriek = false;
            canWake = false;
            isShrieking = false;
            isWaking = false;
            stunTime++;
            wakeTime = 0;
            shriekTime = 0;
            if (stunTime >= MAX_STUN_TIME)
            {
                canWake = true;
                stunned = false;
                stunTime = 0;
                body.setActive(true);
            }
        }
        if (isWaking){
            wakeTime++;
            if (wakeTime >= WAKE_TIME){
                canShriek = true;
                canWake = false;
                wakeTime = 0;
            }
        }
        if (isShrieking){
            shriekTime++;
            if (shriekTime >= SHRIEK_TIME){
                canShriek = false;
                justShrieked = true;
                shriekTime = 0;
            }
        }
        // Set enemy texture based on direction from movement
        if (!isStunned()) {
            // updateDirection(hVelocity, vVelocity);
        }
        // Increase animation frame
        aframe += SHRIEKER_ANIMATION_SPEED;

        // Update animator
        updateAnimator();

        if (behind < 0){
            behind = 0;
        }

        if(behind > 0){
            setBehind(true);
        }
        else {
            setBehind(false);
        }
    }
    /**
     * Returns whether the shrieker is shrieking.
     * Used to alert other enemies to its location.
     * @return
     */
    public boolean getShrieking(){
        return isShrieking;
    }
    @Override
    protected void createFixtures() {
        super.createFixtures();
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = 0;
        sensorDef.isSensor = true;
        sensorShape = new CircleShape();
        sensorShape.setRadius(30f);
        sensorDef.shape = sensorShape;

        body.createFixture(sensorDef);
    }

    /**
     * Method used to draw the shrieker enemy.
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas){
        currentAnimator.setFrame((int)getAframe());
        if (stunCooldown > 0 && stunCooldown % 10 == 0)
        {
            canvas.draw(currentAnimator, Color.CLEAR, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - currentAnimator.getRegionWidth()*scale/2, body.getWorldCenter().y*drawScale.y- currentAnimator.getRegionHeight()*scale/2, 0.0f, scale, scale);
        }
        else if (isStunned()){
            canvas.draw(currentAnimator, Color.PINK, origin.x, origin.y, body.getWorldCenter().x * drawScale.x - currentAnimator.getRegionWidth() * getScale() / 2, body.getWorldCenter().y * drawScale.y - currentAnimator.getRegionHeight() * getScale() / 2, 0.0f, getScale(), getScale());
        }
        else if (justShrieked){
            canvas.draw(currentAnimator, Color.GRAY, origin.x, origin.y, body.getWorldCenter().x * drawScale.x - currentAnimator.getRegionWidth() * getScale() / 2, body.getWorldCenter().y * drawScale.y - currentAnimator.getRegionHeight() * getScale() / 2, 0.0f, getScale(), getScale());

        }
        else {
            canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - currentAnimator.getRegionWidth()*scale/2, body.getWorldCenter().y*drawScale.y- currentAnimator.getRegionHeight()*scale/2, 0.0f, scale, scale);
        }
    }
    @Override
    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        //canvas.drawPhysics(shape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
        canvas.drawPhysics(sensorShape, Color.BLUE, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
    }

    public Enemy.EnemyType getEnemyType() {
        return EnemyType.SHRIEKER;
    }
}
