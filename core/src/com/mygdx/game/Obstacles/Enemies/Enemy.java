package com.mygdx.game.Obstacles.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameCanvas;
import com.mygdx.game.Obstacles.GameObstacle;
import com.mygdx.game.Obstacles.Shadow;
import obstacle.BoxObstacle;
import util.FilmStrip;

/** Model class representing an enemy */

public class Enemy extends Shadow implements GameObstacle {
    /** Enum to encode the finite state machine
     *
     * Used for determining animation direction
     *
     */
    protected static enum EnemyType {
        /** Scout enemy */
        SCOUT,
        /** Chaser enemy */
        CHASER,
        /** Floating enemy */
        FLOATING,
        /** Shrieker enemy */
        SHRIEKER
    }
    private static enum Direction {
        /** The enemy is not moving */
        IDLE,
        /** The enemy is moving in the upwards direction */
        UP,
        /** The enemy is moving in the downwards direction */
        DOWN,
        /** The enemy is moving to the right */
        RIGHT,
        /** The enemy is moving to the left */
        LEFT
    }
    // Constants for the enemy

    /** How far forward the enemy can move */
    protected static final float MOVE_SPEED = 105.0f;
    /** Maximum amount of time an enemy can remain stunned (in frames) */
    protected final float MAX_TO_STUN_TIME = 5;
    protected final float MAX_STUN_COOLDOWN = 200;
    /** Maximum amount of time an enemy can remain stunned (in frames) */
    protected final float MAX_STUN_TIME = 500;
    /** Time enemy must wait before attacking again */
    protected final float ATTACK_COOLDOWN = 300;
    /** The textures for the enemy. */
    protected Texture textureIdle;
    protected Texture textureUp;
    protected Texture textureDown;
    protected Texture textureRight;
    protected Texture textureLeft;
    /** Current texture to be used for the enemy */
    protected Texture currentTexture;
    /** Filmstrip for the enemy */
    protected FilmStrip animatorUp;
    protected FilmStrip animatorDown;
    protected FilmStrip animatorRight;
    protected FilmStrip animatorLeft;
    protected FilmStrip animatorIdle;
    protected FilmStrip currentAnimator;



    // Enemy variables

    /** Enemy position */
    protected Vector2 position;
    /** Enemy velocity */
    protected Vector2 velocity;
    /** Last Enemy Velocity **/
    protected Vector2 lastVelocity;
    /** The enemy's current direction */
    protected Enemy.Direction direction;
    /** Whether this enemy is currently stunned */
    protected boolean stunned;
    /** Time this enemy has been stunned (in frames) */
    protected float toStunTime = 0;
    protected int stunCooldown = 0;
    protected boolean damaged;
    /** Time this enemy has been stunned (in frames) */
    protected float stunTime = 0;
    /** Whether this enemy can attack */
    protected boolean canAttack;
    /** Time that has passed since enemy last attacked */
    protected float attackTime = 0;
    /** Whether this enemy has been revealed */
    protected boolean revealed;
    /** The zero vector (used for position updates) */
    protected Vector2 zerovector;
    /** The physics shape of this object */
    private PolygonShape sensorShape;
    /** Scale of the object */
    protected float scale;
    /** How fast we change frames (one frame per 10 calls to update) */
    protected static final float ANIMATION_SPEED = 0.20f;
    /** The number of animation frames in our filmstrip */
    protected static final int   NUM_ANIM_FRAMES = 12;
    /** Current animation frame for this shell */
    protected float aframe;


    /**Filter for filtering */
    private static volatile Filter filter;

    public void incToStunTime(){
        toStunTime++;
        damaged = true;
    }

    /**
     * Returns whether this enemy is currently stunned
     *
     * @return the stun status of this enemy
     *
     */
    public boolean isStunned() { return stunned; }

    /**
     * Sets the stun state of this enemy
     *
     * @param stunned this enemy's new stun state
     */
    public void setStunned(boolean stunned) { this.stunned = stunned; }

    /**
     * Returns whether this enemy can currently attack
     *
     * @return whether this enemy can currently attack
     *
     */
    public boolean canAttack() { return canAttack; }

    /**
     * Sets whether this enemy can attack or not
     *
     * @param canAttack whether this enemy can attack or not
     */
    public void setAttack(boolean canAttack) { this.canAttack = canAttack; }

    /**
     * Returns whether this enemy is currently under smog
     *
     * @return whether this enemy is currently under smog
     *
     */
    public boolean isRevealed() { return revealed; }

    /**
     * Sets whether this enemy is under smog
     *
     * @param revealed whether this enemy is under smog
     */
    public void setRevealed (boolean revealed) { this.revealed = revealed; }

    /**
     * Returns the aframe of the animation.
     *
     * @return aframe
     */
    public float getAframe(){
        return aframe;
    }
    /**
     * Returns the scale of the animation.
     *
     * @return scale
     */
    public float getScale(){
        return scale;
    }

    /**
     * Initialize a standard enemy
     *
     * @param x the x-coordinate of this enemy
     * @param y the y-coordinate of this enemy
     * @param up the texture for upwards movement
     * @param down the texture for downwards movement
     * @param right the texture for rightwards movement
     * @param left the texture for leftwards movement
     * @param idle the texture for idle movement
     * @param scale the scale to be drawn to
     */
    public Enemy (float x, float y, Texture up, Texture down, Texture right, Texture left, Texture idle, float scale)
    {
        super(x, y, idle.getWidth()/NUM_ANIM_FRAMES*scale, idle.getHeight()*scale, ShadowShape.CIRCLE);
        //setTexture(value);
        setDensity(1);
        setFriction(0.1f);
        setRestitution(0.1f);
        setFixedRotation(true);
        position = new Vector2(x, y);
        velocity = new Vector2();
        lastVelocity = new Vector2();
        zerovector = new Vector2(0,0);
        this.scale = scale;

        textureUp = up;
        textureDown = down;
        textureRight = right;
        textureLeft = left;
        textureIdle = idle;
        currentTexture = textureIdle;

        stunned = false;
        canAttack = true;
        revealed = false;

        if (filter == null){
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }

        animatorUp = new FilmStrip(textureUp,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
        animatorDown = new FilmStrip(textureDown,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
        animatorRight = new FilmStrip(textureRight,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
        animatorLeft = new FilmStrip(textureLeft,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
        animatorIdle = new FilmStrip(textureIdle,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
        currentAnimator = animatorIdle;
        aframe = 0.0f;


    }
    /**
     *  Updates the direction that the enemy sprite faces.
     */
    public void updateDirection(float h, float v){
        if (v > 0){
            direction = Enemy.Direction.UP;
            currentAnimator = animatorUp;
        }
        else if (v < 0){
            direction = Enemy.Direction.DOWN;
            currentAnimator = animatorDown;
        }
        else if (h > 0){
            direction = Enemy.Direction.RIGHT;
            currentAnimator = animatorRight;
        }
        else if (h < 0){
            direction = Enemy.Direction.LEFT;
            currentAnimator = animatorLeft;
        }
        else{
            direction = Enemy.Direction.IDLE;
            currentAnimator = animatorIdle;

        }
    }

    /**
     * Updates the positions of enemies based on their chosen action.
     * Also updates the stun time of stunned enemies and the attack time of enemies on cooldown.
     * @param action the action that this enemy will take
     */
    public void update(int action)
    {
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
        if (isStunned())
        {
            damaged = false;
            stunCooldown = 0;
            canAttack = false;
            stunTime++;
            if (stunTime >= MAX_STUN_TIME)
            {
                canAttack = true;
                stunned = false;
                stunTime = 0;
                body.setActive(true);
            }
        }
        if (!canAttack())
        {
            body.setActive(false);
            attackTime++;
            if (attackTime >= ATTACK_COOLDOWN && !isStunned())
            {
                canAttack = true;
                attackTime = 0;
                body.setActive(true);
            }
        }

        // Determine how we are moving.
        float hVelocity = 0;
        float vVelocity = 0;
        if (action == 1)
        {
            hVelocity = 1;
        }
        else if (action == 2)
        {
            hVelocity = -1;
        }
        else if (action == 3)
        {
            vVelocity = 1;
        }
        else if (action == 4)
        {
            vVelocity = -1;
        }
        else if (action == 5)
        {
            hVelocity = 1;
            vVelocity = 1;
        }
        else if (action == 6)
        {
            hVelocity = 1;
            vVelocity = -1;
        }
        else if (action == 7)
        {
            hVelocity = -1;
            vVelocity = 1;
        }
        else if (action == 8)
        {
            hVelocity = -1;
            vVelocity = -1;
        }

        velocity.x = hVelocity * MOVE_SPEED;
        velocity.y = vVelocity * MOVE_SPEED;
        setVX(velocity.x);
        setVY(velocity.y);

        if (!velocity.equals(zerovector)) {
            lastVelocity  = velocity.cpy();
        }
//        position.add(velocity);
//        setPosition(position);
        body.setLinearVelocity(velocity);
        body.applyLinearImpulse(velocity, body.getWorldCenter(), true);
        setX(body.getWorldCenter().x);
        setY(body.getWorldCenter().y);

        // Set enemy texture based on direction from movement
        if (!isStunned()) {
            updateDirection(hVelocity, vVelocity);
        }

        // Increase animation frame
        aframe += ANIMATION_SPEED;

        if (aframe >= NUM_ANIM_FRAMES) {
            aframe -= NUM_ANIM_FRAMES;
        }
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method to keep your ship from spinning.
     *
     * @param world Box2D world to store body
     *
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }
        //Create a box to represent the player -- this will later be changed into the shadow but

        // Ground Sensor
        // -------------
        // We only allow the dude to jump when he's on the ground.
        // Double jumping is not allowed.
        //
        // To determine whether or not the dude is on the ground,
        // we create a thin sensor under his feet, which reports
        // collisions with the world but has no collision response.
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
        FixtureDef sensorDef = new FixtureDef();

        //TO DO: Make Json dependant
        //sensorDef.density = data.getFloat("density",0);
        sensorDef.density = 1;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        //TO DO: Make Json dependant
        //JsonValue sensorjv = data.get("sensor");
        //sensorShape.setAsBox(sensorjv.getFloat("shrink",0)*getWidth()/2.0f, sensorjv.getFloat("height",0), sensorCenter, 0.0f);
        sensorShape.setAsBox(getHeight(), getWidth());
        sensorDef.shape = sensorShape;

        // Ground sensor to represent our feet
        Fixture sensorFixture = body.createFixture( sensorDef );
//        sensorFixture.setUserData(getSensorName());


        body.setUserData(this);
        setFilterData(filter);

        setFilterData(filter);


        return true;
    }

    /**
     * Draws the enemy object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        currentAnimator.setFrame((int)aframe);
//        System.out.println((body.getWorldCenter().x*drawScale.x - currentAnimator.getRegionWidth()*scale/2) + ", " + (body.getWorldCenter().y*drawScale.y- currentAnimator.getRegionHeight()*scale/2));
        if (stunCooldown > 0 && stunCooldown % 10 == 0)
        {
            canvas.draw(currentAnimator, Color.CLEAR, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - currentAnimator.getRegionWidth()*scale/2, body.getWorldCenter().y*drawScale.y- currentAnimator.getRegionHeight()*scale/2, 0.0f, scale, scale);
        }
        else if (isStunned())
        {
            canvas.draw(currentAnimator, Color.PINK, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - currentAnimator.getRegionWidth()*scale/2, body.getWorldCenter().y*drawScale.y- currentAnimator.getRegionHeight()*scale/2, 0.0f, scale, scale);
        }
        else {
            canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x * drawScale.x - currentAnimator.getRegionWidth() * scale / 2, body.getWorldCenter().y * drawScale.y - currentAnimator.getRegionHeight() * scale / 2, 0.0f, scale, scale);
        }
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        //canvas.beginDebug();
        //canvas.drawPhysics(shape, Color.RED, getX()*drawScale.x, getY()*drawScale.y, getAngle(), drawScale.x, drawScale.y);
        //canvas.endDebug();
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
}
