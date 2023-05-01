package com.mygdx.game.Obstacles;

import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameCanvas;
import com.mygdx.game.InputController;
import com.mygdx.game.Lights;
import obstacle.BoxObstacle;
import obstacle.WheelObstacle;
import util.FilmStrip;

/**
 * A model class representing the player.
 *
 */
public class Player extends Shadow implements GameObstacle{
    /** Enum to encode the finite state machine */
    private static enum Direction {
        /** The player is not moving */
        IDLE,
        /** The player is moving in the upwards direction */
        UP,
        /** The player is moving in the downwards direction */
        DOWN,
        /** The player is moving to the right */
        RIGHT,
        /** The player is moving to the left */
        LEFT
    }
    // Constants for the player


    /** How long the player must wait until it can lose a life again */
    private static final int COOLDOWN = 200;

    /** The player's current direction */
    private Player.Direction direction;

    /** How far forward the player can move */
    private static final float MOVE_SPEED = 500.0f;
    /** The texture for the player. */
    protected FilmStrip textureUp;
    protected FilmStrip textureDown;
    protected FilmStrip textureRight;
    protected FilmStrip textureLeft;
    protected FilmStrip textureIdle;
    /** Current texture to be used for the player */
    protected FilmStrip currentTexture;

    protected InputController controller;

    // Player variables
    /** Player position */
    private Vector2 position;
    /** Player's previous position */
    private Vector2 prevPosition;
    /** PLayer velocity */
    private Vector2 velocity;
    /** Last Player Velocity **/
    private Vector2 lastVelocity;
    /** zerovector for calculations **/
    private Vector2 zerovector;

    /** Player visibility */
    private float sightDis = 200;

    /** Player health */
    private int health;

    /** Whether player is alive or dead */
    private boolean isAlive;
    /** The shadow of the player for collisions **/
    private Shadow shadow;
    /** The number of frames until we can lose a life again */
    private int damageCooldown;

    /**Filter for filtering */
    private static volatile Filter filter;
    /** Filmstrip for player */
    protected FilmStrip[] animator;
    protected FilmStrip currentAnimator;
    /** How fast we change frames (one frame per 10 calls to update) */
    private static final float ANIMATION_SPEED = 0.25f;
    /** How fast we change frames (one frame per 10 calls to update) */
    private static final float ANIMATION_SPEED_BLINK = 0.13f;
    /** Time until blink again */
    private int blinkTime;
    /** Time to wait to blink again */
    private final int MAX_BLINK_TIME = 100;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 9;
    /** Current animation frame for this shell */
    private float aframe;
    /** Scale of the object */
    private float scale;
    /** Max amount of health for the player */
    private int maxHealth;

    private float height;
    private float width;

    private Array<Survivor> survivorsFollowing;

    public void addToFollowing(Survivor survivor) {
        survivorsFollowing.add(survivor);
    }

    public void removeFromFollowing(Survivor survivor) {
        survivorsFollowing.removeValue(survivor, false);
    }

    public Array<Survivor> getSurvivorsFollowing() {
        return survivorsFollowing;
    }
    /**
     * Create player at the given position.
     *
     * @param x The initial x-coordinate of the player in box2d units
     * @param y The initial y-coordinate of the player in box2d units
     */
    public Player(float x, float y, FilmStrip[] player, InputController input, float scale, float tileSize) {
        super(x, y, 4.0f/3.0f*tileSize*scale, 4.0f/3.0f*tileSize*scale, ShadowShape.CIRCLE);
        this.height = 4.0f/3.0f*tileSize;
        this.width = 4.0f/3.0f*tileSize;
        // setTexture(value);
        setDensity(1);
        setFriction(0.1f);
        setRestitution(0.4f);
        setFixedRotation(true);
        position = new Vector2(x,y);
        velocity = new Vector2();
        lastVelocity = new Vector2();
        zerovector = new Vector2(0,0);
        health = 5;
        animator = player;
        currentTexture = textureRight;
        isAlive = true;
        controller = input;
        direction = Direction.IDLE;
        prevPosition = position;
        maxHealth = 5;
        blinkTime = 0;

        if (filter == null){
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }
        survivorsFollowing = new Array<>();

        //shadow = new Shadow(position, 0, -10, 10);


        currentAnimator = animator[IDLE];
        aframe = 0.0f;
        this.scale = scale;
    }


//    /**
//     * Returns the type of this object.
//     *
//     * We use this instead of runtime-typing for performance reasons.
//     *
//     * @return the type of this object.
//     */
//    public ObstacleType getType() { return ObstacleType.PLAYER; }

    /**
     * Returns the max health of the player
     *
     * @return the max health of the player
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    /**
     * Returns the x-coordinate of the player position
     *
     * @return the x-coordinate of the player position
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns the y-coordinate of the player position
     *
     * @return the y-coordinate of the player position
     */
    public float getWidth() {
        return width;
    }
    public int getHealth(){
        return health;
    }
    public void setHealth(int health){
        this.health = health;
    }

    public boolean canLoseLife() {
        return damageCooldown <= 0;
    }

    /**
     * Returns the x-coordinate of the player position
     *
     * @return the x-coordinate of the player position
     */
    public float getX() {
        return position.x;
    }

    /**
     * Sets the x-coordinate of the player position
     *
     * @param value the x-coordinate of the player position
     */
    public void setX(float value) {
        position.x = value;
    }

    /**
     * Returns the y-coordinate of the player position
     *
     * @return the y-coordinate of the player position
     */
    public float getY() {
        return position.y;
    }

    /**
     * Sets the y-coordinate of the player position
     *
     * @param value the y-coordinate of the player position
     */
    public void setY(float value) {
        position.y = value;
    }

    /**
     * Returns the position of the player.
     *
     * This method returns a reference to the underlying player position vector.
     * Changes to this object will change the position of the player.
     *
     * @return the position of this player.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Returns the x-coordinate of the player velocity
     *
     * @return the x-coordinate of the player velocity
     */
    public float getVX() {
        return velocity.x;
    }

    /**
     * Sets the x-coordinate of the player velocity
     *
     * @param value the x-coordinate of the player velocity
     */
    public void setVX(float value) {
        velocity.x = value;
    }

    /**
     * Returns the y-coordinate of the player velocity
     *
     * @return the y-coordinate of the player velocity
     */
    public float getVY() {
        return velocity.y;
    }

    /**
     * Sets the y-coordinate of the player velocity
     *
     * @param value the y-coordinate of the player velocity
     */
    public void setVY(float value) {
        velocity.y = value;
    }

    /**
     * Returns the velocity of the player.
     *
     * This method returns a reference to the underlying player velocity vector.
     * Changes to this object will change the velocity of the player.
     *
     * @return the velocity of the player.
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Returns the last non-zero velocity of the player.
     *
     * This method returns a reference to the underlying player last non-zero velocity vector.
     * Changes to this object will change the velocity of the player.
     *
     * @return the velocity of the player.
     */
    public Vector2 getLastVelocity() {
        return lastVelocity;
    }

    /**
     * Sets the shadow of the player.
     * This method sets the player shadow to the given Shadow object.
     * @return the shadow of the player.
     */
    public void setShadow(Shadow shadow) { this.shadow = shadow; }

    /**
     * Returns the shadow of the player.
     * This method returns a reference to the underlying player shadow.
     * Changes to this object will change the shadow of the player.
     * @return the shadow of the player.
     */
    public Shadow getShadow() { return shadow; }

    /**
     * Returns whether or not the player is alive.
     *
     * @return whether or not the player is alive
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method.
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

        setFilterData(filter);
        //attachLightToPlayer(Lights.createPointLight(Color.WHITE, sightDis, 0,0));
        return true;
    }

    public void attachLightToPlayer(Light light){
        light.attachToBody(body);
    }

    /**
     *  Updates the direction that the player sprite faces.
     */
    public void updateDirection(float h, float v){
        if (h > 0){
            direction = Direction.RIGHT;
            currentAnimator = animator[RIGHT];
        }
        else if (h < 0){
            direction = Direction.LEFT;
            currentAnimator = animator[LEFT];
        }
        else if (v > 0){
            direction = Direction.UP;
            currentAnimator = animator[UP];
        }
        else if (v < 0){
            direction = Direction.DOWN;
            currentAnimator = animator[DOWN];
        }
        else{
            direction = Direction.IDLE;
            currentAnimator = animator[IDLE];
        }
    }


    /**
     * Updates this ship position (and weapons fire) according to the control code.
     *
     * This method updates the velocity and the weapon status, but it does not change
     * the position or create photons.  The later interact with other objects (position
     * can cause collisions) so they are processed in a controller.  Method in a model
     * object should only modify state of that specific object and no others.
     *
     */
    public void update() {
        // If we are dead do nothing.
        if (!isAlive) {
            return;
        }

        if (health <= 0)
        {
            isAlive = false;
        }

        coolDown(true);

        // Determine how we are moving.
        float hVelocity = controller.getHorizontal();
        float vVelocity = controller.getVertical();
//        boolean isMoving = hVelocity + vVelocity != 0;

        velocity.x = hVelocity * MOVE_SPEED;
        velocity.y = vVelocity * MOVE_SPEED;

        if (!velocity.equals(zerovector)) {
            lastVelocity  = velocity.cpy();
        }
        //position.add(velocity);
        //setPosition(position);
        body.setLinearVelocity(velocity);
        body.applyLinearImpulse(velocity, body.getWorldCenter(), true);
        setX(body.getWorldCenter().x);
        setY(body.getWorldCenter().y);

        // Set player texture based on direction from movement
        updateDirection(hVelocity, vVelocity);


        // Increase animation frame
        if (currentAnimator != animator[IDLE]){
            aframe += ANIMATION_SPEED;
        }
        else{
            aframe += ANIMATION_SPEED_BLINK;
        }

        if (aframe >= NUM_ANIM_FRAMES-1) {
            if (currentAnimator != animator[IDLE]){
                aframe -= NUM_ANIM_FRAMES-1;
            }
            else{
                aframe = NUM_ANIM_FRAMES-1;
                blinkTime++;
                if (blinkTime >= MAX_BLINK_TIME){
                    aframe -= NUM_ANIM_FRAMES-1;
                    blinkTime = 0;
                }
            }
        }
//        Filter filter = body.getFixtureList().get(0).getFilterData();

    }

    /**
     * Reset or cool down the survivor's damage intake (i.e. discretized life loss).
     *
     * If flag is true, the survivor will cool down by one animation frame.  Otherwise
     * it will reset to its maximum cooldown.
     *
     * @param flag whether to cooldown or reset
     */
    public void coolDown(boolean flag) {
        if (flag && damageCooldown > 0) {
            damageCooldown--;
        } else if (!flag) {
            damageCooldown = COOLDOWN;
        }
    }


    /**
     * Draws the player object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
//        canvas.draw(texture, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x*drawScale.x, body.getWorldCenter().y*drawScale.y, getAngle(), 1, 1);
//        canvas.draw(texture, getX(), getY());
//        if (direction == Direction.IDLE){
//            currentAnimator.setFrame(0);
//        }
//        else{
//            currentAnimator.setFrame((int)aframe);
//        }
        currentAnimator.setFrame((int)aframe);

        if (isAlive)
        {
            if (damageCooldown > 0 && damageCooldown % 10 == 0) {
                canvas.draw(currentAnimator, Color.CLEAR, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - height*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, width*scale, height*scale);
            }
            else {
                canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - width*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, width*scale, height*scale);
            }
        }


    }


    @Override
    public ObstacleType getType() {
        return ObstacleType.PLAYER;
    }

    @Override
    public short getCatagoricalBits() {
        return CATEGORY_PLAYER;
    }

    @Override
    public short getMaskBits() {
        return MASK_PLAYER;
    }
}
