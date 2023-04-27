package com.mygdx.game.Obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameCanvas;
import obstacle.BoxObstacle;
import util.FilmStrip;

public class Survivor extends Shadow implements GameObstacle {
    // Constants for the survivor
    /** How long the survivor must wait until it can lose a life again */
    private static final int   COOLDOWN = 200;
    /** How far forward the survivor can move */
    private static final float MOVE_SPEED = 70.0f;
    /** The font for interactable prompt*/
    protected BitmapFont displayFontInteract;
    /** The texture for the survivor. */
    protected TextureRegion stexture;
    /** The texture for the interact prompt. */
    protected TextureRegion itexture;

    // survivor variables
    /** survivor position */
    private Vector2 position;
    /** survivor velocity */
    private Vector2 velocity;
    /** Last survivor Velocity **/
    private Vector2 lastVelocity;
    /** zerovector for calculations **/
    private Vector2 zerovector;

    /** Whether survivor is alive or dead */
    private boolean isAlive;
    /** Whether survivor has been found and rescued by player */
    private boolean isFollowing;
    /** Whether survivor can be interacted with by player */
    private boolean isInteractable;
    /** Whether survivor is at the caravan */
    private boolean isRescued;
    /** The shadow of the survivor for collisions **/
    private Shadow shadow;
    /** Identifier to allow us to track the sensor in ContactListener */
    private final String sensorName;
    /** The physics shape of this object's sensor */
    private CircleShape sensorShape;
    /** Filmstrip for interact prompt. */
    protected Texture textureHeart;
    /** How fast we change frames (one frame per 10 calls to update) */
    private static final float ANIMATION_SPEED = 0.04f;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 2;
    /** Current animation frame for this shell */
    private float aframe;
    /** The number of lives this survivor has */
    private int lives;
    /** The number of frames until we can lose a life again */
    private int damageCooldown;
    Filter filter;
    private float scale;

    private boolean isTargetOfEnemy;

    /**
     * Create survivor at the given position.
     *
     * @param x The initial x-coordinate of the survivor
     * @param y The initial y-coordinate of the survivor
     * @param svalue The texture for the survivor
     * @param heart The texture for the heart interface
     *
     */
    public Survivor(int id, float x, float y, TextureRegion svalue, Texture heart, BitmapFont font, float scale) {
        super(x, y, svalue.getRegionWidth()*scale, svalue.getRegionHeight()*scale, ShadowShape.CIRCLE);
        setDensity(0);
        setFriction(0);
        setRestitution(0.1f);
        position = new Vector2(x,y);
        velocity = new Vector2();
        lastVelocity = new Vector2();
        zerovector = new Vector2(0,0);
        displayFontInteract = font;

        this.scale = scale;

        stexture = svalue;
        setTexture(stexture);
        // itexture = ivalue;
        isFollowing = false;
        isInteractable = false;
        isRescued = false;
        isAlive = true;
        isTargetOfEnemy = false;
        lives = 3;
        damageCooldown = 0;
        //shadow = new Shadow(position, 0, -10, 10);
        sensorName = "saveSensor";
        if (filter == null){
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }

        textureHeart = heart;
        aframe = 0.0f;
    }

    /**
     * Returns the x-coordinate of the survivor position
     *
     * @return the x-coordinate of the survivor position
     */
    public float getHeight() {
        return texture.getRegionHeight()*scale;
    }

    /**
     * Returns the y-coordinate of the survivor position
     *
     * @return the y-coordinate of the survivor position
     */
    public float getWidth() {
        return texture.getRegionWidth()*scale;
    }

    /**
     * Returns the x-coordinate of the survivor position
     *
     * @return the x-coordinate of the survivor position
     */
    public float getX() {
        return position.x;
    }

    /**
     * Sets the x-coordinate of the survivor position
     *
     * @param value the x-coordinate of the survivor position
     */
    public void setX(float value) {
        position.x = value;
    }

    /**
     * Returns the y-coordinate of the survivor position
     *
     * @return the y-coordinate of the survivor position
     */
    public float getY() {
        return position.y;
    }

    /**
     * Sets the y-coordinate of the survivor position
     *
     * @param value the y-coordinate of the survivor position
     */
    public void setY(float value) {
        position.y = value;
    }

    /**
     * Returns the number of remaining lives this survivor has
     *
     * @return the number of remaining lives this survivor has
     */
    public int getLives() {
        return lives;
    }

    /**
     * deducts the number of remaining lives this survivor has by 1.
     */
    public void loseLife() {
        lives = lives - 1;
        damageCooldown = COOLDOWN;
    }

    /**
     * Returns whether or not this survivor can lose a life.
     *
     * @return whether or not this survivor can lose a life.
     */
    public boolean canLoseLife() {
        return damageCooldown <= 0;
    }

    /**
     * Sets isAlive to false
     */
    public void die() {
        isAlive = false;
    }
    /**
     * Returns the position of the survivor.
     *
     * This method returns a reference to the underlying survivor position vector.
     * Changes to this object will change the position of the survivor.
     *
     * @return the position of this survivor.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Returns the x-coordinate of the survivor velocity
     *
     * @return the x-coordinate of the survivor velocity
     */
    public float getVX() {
        return velocity.x;
    }

    /**
     * Sets the x-coordinate of the survivor velocity
     *
     * @param value the x-coordinate of the survivor velocity
     */
    public void setVX(float value) {
        velocity.x = value;
    }

    /**
     * Returns the y-coordinate of the survivor velocity
     *
     * @return the y-coordinate of the survivor velocity
     */
    public float getVY() {
        return velocity.y;
    }

    /**
     * Sets the y-coordinate of the survivor velocity
     *
     * @param value the y-coordinate of the survivor velocity
     */
    public void setVY(float value) {
        velocity.y = value;
    }

    /**
     * Returns the velocity of the survivor.
     *
     * This method returns a reference to the underlying survivor velocity vector.
     * Changes to this object will change the velocity of the survivor.
     *
     * @return the velocity of the survivor.
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Returns the last non-zero velocity of the survivor.
     *
     * This method returns a reference to the underlying survivor last non-zero velocity vector.
     * Changes to this object will change the velocity of the survivor.
     *
     * @return the velocity of the survivor.
     */
    public Vector2 getLastVelocity() {
        return lastVelocity;
    }

    /**
     * Sets the shadow of the survivor.
     * This method sets the survivor shadow to the given Shadow object.
     * @return the shadow of the survivor.
     */
    public void setShadow(Shadow shadow) { this.shadow = shadow; }

    /**
     * Returns the shadow of the survivor.
     * This method returns a reference to the underlying survivor shadow.
     * Changes to this object will change the shadow of the survivor.
     * @return the shadow of the survivor.
     */
    public Shadow getShadow() { return shadow; }

    /**
     * Returns whether or not the survivor is alive.
     *
     * @return whether or not the survivor is alive
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Returns whether or not the survivor is following the player.
     *
     * @return whether or not the survivor is following the player
     */
    public boolean isFollowing() {
        return isFollowing;
    }

    /**
     * Returns whether or not the survivor is safe at the caravan.
     *
     * @return whether or not the survivor is safe at the caravan.
     */
    public boolean isRescued() {
        return isRescued;
    }

    public boolean isTargetOfEnemy() {return isTargetOfEnemy;}

    public void setTargetOfEnemy(boolean value) {isTargetOfEnemy = value;}

    /**
     * Sets the survivor to be rescued and isFollowing to false.
     */
    public void rescue() {
        this.isRescued = true;
        this.isFollowing = false;
    }

    /**
     * Sets whether the survivor can be interacted with.
     */
    public void setInteractable(Boolean interact) {
        this.isInteractable = interact;
    }

    /**
     * Returns whether or not the survivor is interactable.
     *
     * @return whether or not the survivor is interactable
     */
    public boolean isInteractable() {
        return isInteractable;
    }

    /**
     * Sets isFollowing to true.
     */
    public void follow() {
        isFollowing = true;
    }

    /**
     * Returns the java object represented by this survivor.
     *
     * @return UserData which in this case is the survivor object.
     */
    public Object getUserData() {
        return getBody().getUserData();
    }


    /**
     * Updates the interactable prompt that appears above the survivor when the player is near.
     */
    private void updateInteractable(){
        // Increase animation frame
        aframe += ANIMATION_SPEED;

        if (aframe >= NUM_ANIM_FRAMES) {
            aframe -= NUM_ANIM_FRAMES;
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
        //Checks if we are ded
        if (getLives() == 0) {
            this.die();
            //survivorArr.removeValue(survivorArr.get(i), false);
        }

        // If we are dead do nothing.
        if (!isAlive) {
            return;
        }
        //System.out.println("Updating");

        if (isInteractable) {
            //System.out.println("Updating");
            updateInteractable();
        }
        damageCooldown--;
        // Determine how we are moving.

//        velocity.x = hVelocity * MOVE_SPEED;
//        velocity.y = vVelocity * MOVE_SPEED;
//
//        if (!velocity.equals(zerovector)) {
//            lastVelocity  = velocity.cpy();
//        }
//        position.add(velocity);

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

        //geometry.setUserData("survivor");
        setFilterData(filter);

        getBody().setUserData(this);
        return true;
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
     * Draws the survivor object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (!isRescued && isAlive) {
            if (damageCooldown > 0) {
                if (damageCooldown % 10 == 0) {
                    canvas.draw(stexture, Color.CLEAR, origin.x, origin.y, body.getWorldCenter().x*drawScale.x, body.getWorldCenter().y*drawScale.y, getAngle(), scale, scale);
                }
                else {
                    canvas.draw(stexture, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x*drawScale.x, body.getWorldCenter().y*drawScale.y, getAngle(), scale, scale);
                }
            }
            else {
                canvas.draw(stexture, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x*drawScale.x, body.getWorldCenter().y*drawScale.y, getAngle(), scale, scale);
            }
            //canvas.draw(stexture, Color.YELLOW, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
        }
        if (isInteractable){
            float yspace = origin.x + 10;
            float xspace = 0; //(origin.x)/2;

            String message = "(E) Rescue";
            canvas.drawText(message, displayFontInteract, position.x - 16.0f, position.y + 38.0f);


            //canvas.draw(animator, Color.WHITE, animator.getRegionWidth()/2, 0, getX()+xspace, getY()+yspace, animator.getRegionWidth(), animator.getRegionHeight()) ;
        }
        if (isFollowing){
            float spacing = 0.0f;
            for (int i = 0; i < lives; i++){
                canvas.draw(textureHeart, Color.BLUE, 0.0f, 0.0f, (getX() * drawScale.x - 10) + spacing, getY() * drawScale.y + texture.getRegionHeight()*scale/2 - 2, getAngle(), 0.05f, 0.05f);
                spacing += 8.0f;
            }
        }
    }

    /**
     * Updates the positions of enemies based on their chosen action.
     * Also updates the stun time of stunned enemies and the attack time of enemies on cooldown.
     * @param action the action that this enemy will take
     */
    public void update(int action)
    {
        if (!isAlive || isRescued) {
            return;
        }

        //System.out.println(body.getFixtureList().size);

        if (isInteractable) {
            updateInteractable();
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

        body.setLinearVelocity(velocity);
        body.applyLinearImpulse(velocity, body.getWorldCenter(), true);
        setX(body.getWorldCenter().x);
        setY(body.getWorldCenter().y);
    }


    @Override
    public void drawDebug(GameCanvas canvas) {
        if (getBody() != null) {
            super.drawDebug(canvas);
            //canvas.drawPhysics(shape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
            canvas.drawPhysics(sensorShape, Color.BLUE, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
        }
    }

    @Override
    protected void createFixtures() {
        super.createFixtures();
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.filter.categoryBits = getCatagoricalBits();
        sensorDef.filter.maskBits = getMaskBits();
        sensorDef.density = 0;
        sensorDef.isSensor = true;
        sensorShape = new CircleShape();
        sensorShape.setRadius(30f);
        sensorDef.shape = sensorShape;

        body.createFixture(sensorDef);
    }

    @Override
    protected void releaseFixtures() {
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.SURVIVOR;
    }

    @Override
    public short getCatagoricalBits() {
        return CATEGORY_SURVIVOR;
    }

    @Override
    public short getMaskBits() {
        return MASK_SURVIVOR;
    }
}
