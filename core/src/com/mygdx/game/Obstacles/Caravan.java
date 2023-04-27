package com.mygdx.game.Obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameCanvas;
import util.FilmStrip;

public class Caravan extends obstacle.BoxObstacle implements GameObstacle {
    /** The texture for the caravan. */
    protected TextureRegion ctexture;
    /** The font for interactable prompt*/
    protected BitmapFont displayFontInteract;
    /** The caravan's position */
    private Vector2 position;
    /** The current number of survivors this caravan contains */
    private int currentCapacity;
    /** The maximum number of survivors this caravan can contain */
    private int maxCapacity;
    /** Filmstrip for interact prompt. */
    protected FilmStrip animator;
    /** How fast we change frames (one frame per 10 calls to update) */
    private static final float ANIMATION_SPEED = 0.04f;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 2;
    /** Current animation frame for the interact prompt */
    private float aframe;
    /** Whether caravan can be interacted with by player */
    private boolean isInteractable;
    /** Identifier to allow us to track the sensor in ContactListener */
    private final String sensorName;
    /** The physics shape of this object's sensor */
    private CircleShape sensorShape;
    /** Filmstrip for interact prompt. */

    /**Filter for filtering */
    private static volatile Filter filter;

    /** Scale factor */
    private float scale;

    /**
     * Create a caravan at the given position.
     *
     * @param x The initial x-coordinate of the caravan
     * @param y The initial y-coordinate of the caravan
     */
    public Caravan(float x, float y, int maxCapacity, TextureRegion cvalue, Texture ivalue, float scale, BitmapFont font) {
        super(x, y, cvalue.getRegionWidth()*scale, cvalue.getRegionHeight()*scale);
        setDensity(1);
        setFriction(0);
        setRestitution(0.1f);
        position = new Vector2(x,y);
        ctexture = cvalue;
        setTexture(ctexture);
        currentCapacity = 0;
        this.maxCapacity = maxCapacity;
        isInteractable = false;
        sensorName = "saveSensor";
        this.scale = scale;
        displayFontInteract = font;

        if (filter == null){
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }

        animator = new FilmStrip(ivalue,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
        aframe = 0.0f;
    }
    /**
     * Returns the x-coordinate of the caravan position
     *
     * @return the x-coordinate of the caravan position
     */
    public float getX() {
        return position.x;
    }

    /**
     * Sets the x-coordinate of the caravan position
     *
     * @param value the x-coordinate of the caravan position
     */
    public void setX(float value) {
        position.x = value;
    }

    /**
     * Returns the y-coordinate of the caravan position
     *
     * @return the y-coordinate of the caravan position
     */
    public float getY() {
        return position.y;
    }

    /**
     * Sets the y-coordinate of the caravan position
     *
     * @param value the y-coordinate of the caravan position
     */
    public void setY(float value) {
        position.y = value;
    }

    /**
     * Returns the position of the caravan.
     *
     * This method returns a reference to the underlying player position vector.
     * Changes to this object will change the position of the caravan.
     *
     * @return the position of this caravan.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Returns the current capacity of the caravan.
     *
     * @return the current capacity of this caravan.
     */
    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void incrCap() {currentCapacity += 1;}
    /**
     * Sets the current capacity to value
     */
    public void setCurrentCapacity(int value) {
        currentCapacity = value;
    }

    /**
     * Sets whether the caravan can be interacted with.
     */
    public void setInteractable(Boolean interact) {
        this.isInteractable = interact;
    }

    /**
     * Returns whether or not the caravan is interactable.
     *
     * @return whether or not the caravan is interactable
     */
    public boolean isInteractable() {
        return isInteractable;
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
//        FixtureDef sensorDef = new FixtureDef();
//        sensorDef.density = 0;
//        sensorDef.isSensor = true;
//        sensorShape = new CircleShape();
//        sensorShape.setRadius(65f);
//        sensorDef.shape = sensorShape;

        setFilterData(filter);
        //getBody().setUserData(this);

        return true;
    }

    /**
     * This method updates caravan to be interactable (i.e. give player option to drop off survivors) when player
     * is close enough to the caravan.
     */
    public void update() {
        if (isInteractable) {
            //System.out.println("Updating");
            updateInteractable();
        }
    }

    /**
     * Updates the interactable prompt that appears above the caravan when the player is near.
     */
    private void updateInteractable(){
        // Increase animation frame
        aframe += ANIMATION_SPEED;

        if (aframe >= NUM_ANIM_FRAMES) {
            aframe -= NUM_ANIM_FRAMES;
        }
    }

    /**
     * Draws a caravan object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE, texture.getRegionWidth()*scale, texture.getRegionHeight()*scale, getX() - (texture.getRegionWidth()*scale/2), getY() - (texture.getRegionHeight()*scale/2), 0.0f, scale, scale);
        if (isInteractable){
            float yspace = 5;
            float xspace = 0; //(origin.x)/2;
            animator.setFrame((int)aframe);

            String message = "(E) Dropoff";
            canvas.drawText(message, displayFontInteract, position.x-texture.getRegionWidth()*scale/2, position.y + texture.getRegionHeight()*scale+5);
            message = currentCapacity + " of " + maxCapacity + " collected";
            canvas.drawText(message, displayFontInteract, position.x-texture.getRegionWidth()*scale/2, position.y + texture.getRegionHeight()*scale+5-10);
            //canvas.draw(animator, Color.WHITE, animator.getRegionWidth()/2, 0, getX()+xspace, getY()+yspace, animator.getRegionWidth(), animator.getRegionHeight()) ;
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
        sensorShape.setRadius(65f);
        sensorDef.shape = sensorShape;

        body.createFixture(sensorDef);
    }

    @Override
    protected void releaseFixtures() {

    }

    @Override
    public void drawDebug(GameCanvas canvas) {
        if (getBody() != null) {
            super.drawDebug(canvas);
            //canvas.drawPhysics(shape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
            canvas.drawPhysics(sensorShape, Color.BLUE, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
        }
    }

    public ObstacleType getType() {
        return ObstacleType.CARAVAN;
    }

    @Override
    public short getCatagoricalBits() {
        return CATEGORY_CARAVAN;
    }

    @Override
    public short getMaskBits() {
        return 0x0;
    }
}
