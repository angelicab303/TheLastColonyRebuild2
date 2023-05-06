package com.mygdx.game.Obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
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

    /** Filter for filtering */
    private static volatile Filter filter;
    private float scale;

    /**
     * Create a cliff at the given position.
     *
     * @param x The initial x-coordinate of the tree
     * @param y The initial y-coordinate of the tree
     */
    public Obstacles(float x, float y, TextureRegion value, float scale) {
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

        if (filter == null) {
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }
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
        body.setLinearVelocity(velocity);
        body.applyLinearImpulse(velocity, position, true);
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

        setFilterData(filter);
        setActive(false);
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
}
