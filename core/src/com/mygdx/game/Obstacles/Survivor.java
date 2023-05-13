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
    /** Enum to encode the finite state machine */
    private static enum Direction {
        /** The survivor is not moving */
        IDLE,
        /** The survivor is moving in the upwards direction */
        UP,
        /** The survivor is moving in the downwards direction */
        DOWN,
        /** The survivor is moving to the right */
        RIGHT,
        /** The survivor is moving to the left */
        LEFT
    }
    // Constants for the survivor
    /** How long the survivor must wait until it can lose a life again */
    private static final int   COOLDOWN = 200;
    /** The survivor's current direction */
    private Survivor.Direction direction;
    /** How far forward the survivor can move */
    private static final float MOVE_SPEED = 30.0f;
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

    private float DEFAULT_DETECTION_RADIUS = 45f;

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
    private static final float ANIMATION_SPEED = 0.2f;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_MOVE_ANIM_FRAMES = 9;
    private static final int   NUM_IDLE_ANIM_FRAMES = 6;
    /** Filmstrip for survivor */
    protected FilmStrip[] animator;
    protected FilmStrip currentAnimator;
    /** Current animation frame for this shell */
    private float aframe;
    /** The number of lives this survivor has */
    private int lives;
    /** The number of frames until we can lose a life again */
    private int damageCooldown;
    Filter filter;
    private float scale;

    private int behind;
    private Vector2 temp1;
    private Vector2 temp2;
    private boolean isTargetOfEnemy;
    private float height;
    private float width;
    private boolean safeInCaravan;

    protected boolean revealed;

    private Vector2[] smogDetectionVertices;
    private boolean[] directionVacant;

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean value) {
        revealed = value;
    }

    public boolean isSafeInCaravan() {
        return safeInCaravan;
    }

    public void setSafeInCaravan(boolean val) {
        safeInCaravan = val;
    }

    public Vector2[] getSmogDetectionVertices() {
        calculateDetectionRay();
        return smogDetectionVertices;
    }

    public boolean[] getDirectionVacant() {
        return directionVacant;
    }

    /**
     * FROM COLLISION CONTROLLER:
     *      If there IS NO collision between smog and a vertex:
     *      parameter index = index of the vertex, parameter value = true
     *
     *      If there IS collision between smog and a vertex:
     *      parameter index = index of the vertex, parameter value = false
     *
     *  Index 1 of smogDetection does NOT collide with smog => ACTION 1 => directionVacant[0] true
     *  Index 2 of smogDetection does NOT collide with smog => ACTION 5 => directionVacant[4] true
     *  Index 3 of smogDetection does NOT collide with smog => ACTION 3 => directionVacant[2] true
     *  Index 4 of smogDetection does NOT collide with smog => ACTION 7 => directionVacant[6] true
     *  Index 5 of smogDetection does NOT collide with smog => ACTION 2 => directionVacant[1] true
     *  Index 6 of smogDetection does NOT collide with smog => ACTION 8 => directionVacant[7] true
     *  Index 7 of smogDetection does NOT collide with smog => ACTION 4 => directionVacant[3] true
     *  Index 8 of smogDetection does NOT collide with smog => ACTION 6 => directionVacant[5] true
     * */
    public void setDirectionVacant(int index, boolean value) {
        int i = 0;
        switch(index) {
            case 1:
                i = 0;
                break;
            case 2:
                i = 4;
                break;
            case 3:
                i = 2;
                break;
            case 4:
                i = 6;
                break;
            case 5:
                i = 1;
                break;
            case 6:
                i = 7;
                break;
            case 7:
                i = 3;
                break;
            case 8:
                i = 5;
                break;
        }
        directionVacant[i] = value;
    }

    /**
     * Create survivor at the given position.
     *
     * @param x The initial x-coordinate of the survivor
     * @param y The initial y-coordinate of the survivor
     * @param svalue The texture for the survivor
     * @param heart The texture for the heart interface
     *
     */
    public Survivor(int id, float x, float y, FilmStrip[] survivor, Texture heart, BitmapFont font, float scale) {
        super(x, y, survivor[0].getRegionWidth()*scale, survivor[0].getRegionHeight()*scale, ShadowShape.CIRCLE);
        this.width = survivor[0].getRegionWidth();
        this.height = survivor[0].getRegionHeight();
        setDensity(0);
        setFriction(0);
        setRestitution(0.1f);
        position = new Vector2(x,y);
        velocity = new Vector2();
        lastVelocity = new Vector2();
        zerovector = new Vector2(0,0);
        displayFontInteract = font;
        safeInCaravan = false;
        this.scale = scale;
        revealed = false;
        smogDetectionVertices = new Vector2[9];
        directionVacant = new boolean[8];
        temp1 = new Vector2();
        temp2 = new Vector2();
        for(int i = 0; i <  9; i++){
            smogDetectionVertices[i] = new Vector2();
        }
        for(int i = 0; i < 8; i++) {
            directionVacant[i] = false;
        }

        //stexture = svalue;
        //setTexture(stexture);
        // itexture = ivalue;
        isFollowing = false;
        isInteractable = false;
        isRescued = false;
        isAlive = true;
        isTargetOfEnemy = false;
        behind = 0;
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
        animator = survivor;
        currentAnimator = animator[IDLE];
        direction = Survivor.Direction.IDLE;
        // currentTexture = textureRight;
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

    public void calculateDetectionRay(){
        // Start position for raycasts
        temp2.set(position);
//        temp2.scl(-1f);
        smogDetectionVertices[0].set(temp2);
        // End positions for raycasts

        float angle_change = 45f;
        float angle = 0f;
        temp1.set(DEFAULT_DETECTION_RADIUS, 0);
//        temp1.rotateDeg(absorbRange.x);
        temp1.rotateDeg(angle);
        for(int i = 0; i < 8; i ++){
            smogDetectionVertices[i+1].set(temp1.cpy().add(getPosition()));
            temp1.rotateDeg(angle_change);
        }
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
     *  Updates the direction that the survivor sprite faces.
     */
    public void updateDirection(float h, float v){
        if (h > 0){
            direction = Survivor.Direction.RIGHT;
            currentAnimator = animator[RIGHT];
        }
        else if (h < 0){
            direction = Survivor.Direction.LEFT;
            currentAnimator = animator[LEFT];
        }
        else if (v > 0){
            direction = Survivor.Direction.UP;
            currentAnimator = animator[UP];
        }
        else if (v < 0){
            direction = Survivor.Direction.DOWN;
            currentAnimator = animator[DOWN];
        }
        else{
            direction = Survivor.Direction.IDLE;
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
        //Checks if we are ded
        if (getLives() == 0) {
            this.die();
            //survivorArr.removeValue(survivorArr.get(i), false);
        }
        calculateDetectionRay();
        // If we are dead do nothing.
        if (!isAlive) {
            return;
        }

        if (isInteractable) {
            //updateInteractable();
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

        if (behind < 0){
            behind = 0;
        }

        if(behind > 0){
            setBehind(true);
        }
        else {
            setBehind(false);
        }

        // Set player texture based on direction from movement
        updateDirection(velocity.x, velocity.y);


        // Increase animation frame
        if (currentAnimator != animator[IDLE]){
            aframe += ANIMATION_SPEED;
            if (aframe >= NUM_MOVE_ANIM_FRAMES){
                aframe = 0;
            }
        }
        else{
            aframe += ANIMATION_SPEED;
            if (aframe >= NUM_IDLE_ANIM_FRAMES){
                aframe = 0;
            }
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

        //geometry.setUserData("survivor");
        setFilterData(filter);
        setAwake(true);
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
        currentAnimator.setFrame((int)aframe);
        if (!isRescued && isAlive) {
            super.draw(canvas, width*scale, height*scale);
            if (damageCooldown > 0) {
                if (damageCooldown % 10 == 0) {
                    canvas.draw(currentAnimator, Color.CLEAR, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - height*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, width*scale, height*scale);
                }
                else {
                    canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - height*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, width*scale, height*scale);
                }
            }
            else {
                canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - height*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, width*scale, height*scale);
            }
            //canvas.draw(stexture, Color.YELLOW, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
        }
        if (isInteractable && !isFollowing){
            float yspace = origin.x + 10;
            float xspace = 0; //(origin.x)/2;

            String message = "(E) Rescue";
            canvas.drawText(message, displayFontInteract, position.x - 16.0f, position.y + 38.0f);


            //canvas.draw(animator, Color.WHITE, animator.getRegionWidth()/2, 0, getX()+xspace, getY()+yspace, animator.getRegionWidth(), animator.getRegionHeight()) ;
        }
        if (isFollowing){
            float spacing = 0.0f;
            for (int i = 0; i < lives; i++){
                canvas.draw(textureHeart, Color.PURPLE, 0.0f, 0.0f, (getX() * drawScale.x - 20) + spacing, getY() * drawScale.y + currentAnimator.getRegionHeight()*scale/2 - 2, getAngle(), 0.1f, 0.1f);
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

        if (isInteractable) {
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
        super.drawDebug(canvas);
//        canvas.drawLine(Color.BLUE, position, smogDetectionVertices[0]);
        Vector2 ray_start = smogDetectionVertices[0];
        for(Vector2 r : smogDetectionVertices){
            if (!r.equals(ray_start)){
                canvas.drawLine(Color.PURPLE, ray_start, r);
            }
        }
        //canvas.drawPhysics(shape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
        canvas.drawPhysics(sensorShape, Color.BLUE, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
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

    @Override
    public void incBehind(int inc){
        behind += inc;
    }

}
