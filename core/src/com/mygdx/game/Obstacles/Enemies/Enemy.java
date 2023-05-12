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

    /** Filmstrip for the enemy */
    protected FilmStrip[] animator;
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

    float height;
    float width;

    int behind;


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
    public void setRevealed (boolean revealed) {this.revealed = revealed; }

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
     * @param animator the collection of filmstrips (up, down, right, left, idle)
     * @param scale the scale to be drawn to
     */
    public Enemy (float x, float y, FilmStrip[] animator, float scale, float tileSize)
    {
        super(x, y, animator[0].getRegionWidth()*scale, animator[0].getRegionHeight()*scale, ShadowShape.CIRCLE);
        this.height = animator[0].getRegionHeight();
        this.width = animator[0].getRegionWidth();
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

        this.animator = animator;
        this.behind = 0;

        stunned = false;
        canAttack = true;
        revealed = true;

        if (filter == null){
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }

        currentAnimator = animator[IDLE];
        aframe = 0.0f;


    }
    /**
     *  Updates the direction that the enemy sprite faces.
     */
    public void updateDirection(float h, float v){
        if (v > 0){
            direction = Enemy.Direction.UP;
            currentAnimator = animator[UP];
        }
        else if (v < 0){
            direction = Enemy.Direction.DOWN;
            currentAnimator = animator[DOWN];
        }
        else if (h > 0){
            direction = Enemy.Direction.RIGHT;
            currentAnimator = animator[RIGHT];
        }
        else if (h < 0){
            direction = Enemy.Direction.LEFT;
            currentAnimator = animator[LEFT];
        }
        else{
            direction = Enemy.Direction.IDLE;
            currentAnimator = animator[IDLE];
        }
    }

    protected float getMoveSpeed(){
        return MOVE_SPEED;
    }

    protected float getAttackCooldown(){
        return ATTACK_COOLDOWN;
    }

    protected void calculateMovement(int action){
        float hVelocity = 0;
        float vVelocity = 0;
        switch (action){
            case 1:
                hVelocity = 1;
                break;
            case 2:
                hVelocity = -1;
                break;
            case 3:
                vVelocity = 1;
                break;
            case 4:
                vVelocity = -1;
                break;
            case 5:
                hVelocity = 1;
                vVelocity = 1;
                break;
            case 6:
                hVelocity = 1;
                vVelocity = -1;
                break;
            case 7:
                hVelocity = -1;
                vVelocity = 1;
                break;
            case 8:
                hVelocity = -1;
                vVelocity = -1;
                break;
        }
        velocity.x = hVelocity * getMoveSpeed();
        velocity.y = vVelocity * getMoveSpeed();
        //return new Vector2(hVelocity, vVelocity);
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
        if (damaged) {
            stunCooldown++;
            if (stunCooldown >= MAX_STUN_COOLDOWN)
            {
                stunCooldown = 0;
                damaged = false;
            }
        }
        if (isStunned())
        {
            body.setActive(false);
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
            attackTime++;
            if (attackTime >= getAttackCooldown() && !isStunned())
            {
                canAttack = true;
                attackTime = 0;
            }
        }

        if (!isStunned()) {
            // Determine how we are moving.
            setVX(velocity.x);
            setVY(velocity.y);
            //        position.add(velocity);
            //        setPosition(position);
            body.setLinearVelocity(velocity);
            body.applyLinearImpulse(velocity, body.getWorldCenter(), true);
            setX(body.getWorldCenter().x);
            setY(body.getWorldCenter().y);

            // Set enemy texture based on direction from movement
            updateDirection(velocity.x, velocity.y);
        }


        // Increase animation frame
        aframe += ANIMATION_SPEED;

        if (aframe >= NUM_ANIM_FRAMES) {
            aframe -= NUM_ANIM_FRAMES;
        }

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
            canvas.draw(currentAnimator, Color.CLEAR, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - width*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, 0.0f, scale, scale);
        }
        else if (isStunned())
        {
            canvas.draw(currentAnimator, Color.PINK, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - width*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, 0.0f, scale, scale);
        }
        else {
            canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x * drawScale.x - width * scale / 2, body.getWorldCenter().y * drawScale.y - height * scale / 2, 0.0f, scale, scale);
        }
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        //canvas.beginDebug();
        //canvas.drawPhysics(sensorShape, Color.RED, getX()*drawScale.x, getY()*drawScale.y, getAngle(), drawScale.x, drawScale.y);
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

    @Override
    public void incBehind(int inc){
        behind = inc;
    }

}
