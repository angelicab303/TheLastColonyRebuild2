package com.mygdx.game.Obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameCanvas;
import obstacle.BoxObstacle;

public class Obstacles extends Shadow implements GameObstacle {
    // Variables for this class
    /** The texture for the cliff. */
    // protected TextureRegion texture;
    /** Cliff position */
    private Vector2 position;
    /** Cliff velocity */
    private Vector2 velocity;

    private boolean unlocked = false;

    /** Filter for filtering */
    private static volatile Filter filter;
    private float scale;

    private boolean isBelow;

    private boolean isDoor;

    PolygonShape sensorShape;

    /**
     * Create a cliff at the given position.
     *
     * @param x The initial x-coordinate of the tree
     * @param y The initial y-coordinate of the tree
     */
    public Obstacles(float x, float y, TextureRegion value, float scale, boolean isDoor) {
        super(x, y, value.getRegionWidth() * scale, value.getRegionHeight() * scale, ShadowShape.SQUARE);
        setTexture(value);
        setBodyType(BodyDef.BodyType.StaticBody);
        // setDimension(value.getRegionWidth()*scale, value.getRegionHeight()*scale);
        setDensity(1);
        setFriction(0.3f);
        setRestitution(0.1f);
        position = new Vector2(x, y);
        velocity = new Vector2(0.0f, 0.0f);
        this.scale = scale;
        this.isBelow = false;
        this.isDoor = isDoor;

        if (filter == null) {
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }
    }


    public boolean getIsDoor(){
        return isDoor;
    }

    public void setIsDoor(boolean isDoor){
        this.isDoor = isDoor;
    }

    public void unlock(){
        System.out.println(isDoor);
        unlocked = true;
    }

    /**
     * Returns the x-coordinate of the tree position
     *
     * @return the x-coordinate of the tree position
     */
    public float getX() {
        return super.getX();
    }

    /**
     * Sets the x-coordinate of the tree position
     *
     * @param value the x-coordinate of the tree position
     */
    public void setX(float value) {
        super.setX(value);
    }

    /**
     * Returns the y-coordinate of the tree position
     *
     * @return the y-coordinate of the tree position
     */
    public float getY() {
        return super.getY();
    }

    /**
     * Sets the y-coordinate of the tree position
     *
     * @param value the y-coordinate of the tree position
     */
    public void setY(float value) {
        super.setY(value);
    }

    /**
     * Returns the position of the tree.
     *
     * This method returns a reference to the underlying player position vector.
     * Changes to this object will change the position of the tree.
     *
     * @return the position of this tree.
     */
    public Vector2 getPosition() {
        return super.getPosition();
    }
    public Vector2 getOrigin() { return origin; }
    public void setOrigin(Vector2 origin) { this.origin = origin; }

    /**
     * Updates this ship position (and weapons fire) according to the control code.
     *
     * This method updates the velocity and the weapon status, but it does not
     * change
     * the position or create photons. The later interact with other objects
     * (position
     * can cause collisions) so they are processed in a controller. Method in a
     * model
     * object should only modify state of that specific object and no others.
     *
     */
    public void update() {
        if(unlocked && isActive()){
            System.out.println("isunlocking");
            setActive(false);
            //markRemoved(true);
        }


        // Filter filter = body.getFixtureList().get(0).getFilterData();
        // System.out.println("Cliff filter- cat bits:" + filter.categoryBits + ", mask
        // bits: " + filter.maskBits);

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



        float width = texture.getRegionWidth() * scale;
        float height = texture.getRegionHeight() * scale;


        Vector2 sensorCenter = new Vector2(0, 0);
        FixtureDef sensorDef = new FixtureDef();

        //TO DO: Make Json dependant
        //sensorDef.density = data.getFloat("density",0);
        sensorDef.density = 1;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        //TO DO: Make Json dependant
        //JsonValue sensorjv = data.get("sensor");
        //sensorShape.setAsBox(sensorjv.getFloat("shrink",0)*getWidth()/2.0f, sensorjv.getFloat("height",0), sensorCenter, 0.0f);
        sensorShape.setAsBox(width/2, height/2+5, sensorCenter, 0f);
        sensorDef.shape = sensorShape;

        // Ground sensor to represent our feet
        Fixture sensorFixture = body.createFixture( sensorDef );
//        sensorFixture.setUserData(getSensorName());


        setFilterData(filter);


        //body.setAwake(true);



        body.setUserData(this);

        return true;
    }

    /**
     * Draws a cliff object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        // canvas.draw(texture, getX(), getY());
        float width = texture.getRegionWidth() * scale;
        float height = texture.getRegionHeight() * scale;
        canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, 0.0f, scale,
                scale);
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        //canvas.beginDebug();
        canvas.drawPhysics(sensorShape, Color.RED, getX()*drawScale.x, getY()*drawScale.y, getAngle(), drawScale.x, drawScale.y);
        //canvas.endDebug();
    }

    public void setBehind(boolean bool){
        this.isBelow = true;
    }

    public boolean getBehind(){
        return isBelow;
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.OBSTACLE;
    }

    @Override
    public short getCatagoricalBits() {
        return CATEGORY_ENV;
    }

    @Override
    public short getMaskBits() {
        return MASK_ENV;
    }

    @Override
    public void incBehind(int inc) {

    }
}
