package com.mygdx.game.Obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameCanvas;
import obstacle.SimpleObstacle;

public class Shadow extends SimpleObstacle{
    private Vector2 position;

    /** The width and height of the box */
    private Vector2 dimension;

    protected static float size;
    CircleShape circleShape;
    PolygonShape boxShape;

    protected Shape shape;

    protected FixtureDef fixture;
    //FixtureDef boxFixture;

    /** A cache value for the fixture (for resizing) */
    private Fixture geometry;

    /** Cache of the polygon vertices (for resizing) */
    private static float[] vertices;

    private boolean isBelow;

    ShadowShape shadowShape;

    private Vector2 temp;

    public enum ShadowShape{
        SQUARE,
        CIRCLE
    }

    public Shadow(float x, float y, float width, float height, ShadowShape shape) {
        super(x,y);
        position = new Vector2(x,y);
        dimension = new Vector2(width, height);
        origin = new Vector2(0,(size-height)/2);
        geometry = null;
        this.shadowShape = shape;
        this.fixture = new FixtureDef();
        temp = new Vector2();

        isBelow = false;

        //creates the fixtures according to this size
        circleShape = new CircleShape();
        boxShape = new PolygonShape();

        resize(width, size);
    }

    /** Sets the default tile size, will be the diameter if circular or the side length if square
     * Will affect all obstacles */
    public static void setSize(float new_size){
        size = new_size;

        vertices = new float[8];
    }

    private void resize(float size){
        circleShape.setRadius(size/2.0f);
        resize(size, size);
    }

    /**
     * Helper function for resizing the box
     *
     * Reset the polygon vertices in the shape to match the dimension.
     */
    private void transform(float offset) {
        // Make the box with the center in the center
        vertices[1] += offset;
        vertices[3] +=  offset;
        vertices[5] +=  offset;
        vertices[7] += offset;
        boxShape.set(vertices);
    }


    /**
     * Helper function for resizing the box
     *
     * Reset the polygon vertices in the shape to match the dimension.
     */
    private void resize(float width, float height) {
        circleShape.setRadius(size/2.0f/((float)Math.sqrt(2.5)));
        // Make the box with the center in the center
        vertices[0] = -width/2.0f;
        vertices[1] = 0;//-height/2.0f;
        vertices[2] = -width/2.0f;
        vertices[3] =  height/2.0f;
        vertices[4] =  width/2.0f;
        vertices[5] =  height/2.0f;
        vertices[6] =  width/2.0f;
        vertices[7] = 0;//-height/2.0f;
        boxShape.set(vertices);
    }

    /**
     * Returns the box width
     *
     * @return the box width
     */
    public float getWidth() {
        return dimension.x;
    }

    /**
     * Returns the box height
     *
     * @return the box height
     */
    public float getHeight() {
        return dimension.y;
    }

    public float getXCoord(){
        return super.getX();
    }

    public float getYCoord(){
        float offset = size - dimension.y;
        return super.getY()+offset;

    }

    /**
     * Returns the type of this object.
     * We use this instead of runtime-typing for performance reasons.
     * @return the type of this object.
     */
    //public ObstacleType getType() { return ObstacleType.SHADOW; }

    /**
     * Create new fixtures for this body, defining the shape
     *
     * This is the primary method to override for custom physics objects
     */
    protected void createFixtures() {
        if (body == null) {
            return;
        }

        releaseFixtures();

        //dimension.y is width
        float offset = size - dimension.y;
        temp.set(0,offset);


        if (shadowShape.equals(ShadowShape.CIRCLE)){
            (circleShape).setPosition(temp);
            fixture.shape = circleShape;
        }
        else {
            transform(offset);
            shape = boxShape;
            fixture.shape = shape;

        }




        // Create the fixture
        //fixture.shape = shape;
        geometry = body.createFixture(fixture);
        markDirty(false);

    };

    /**
     * Release the fixtures for this body, reseting the shape
     *
     * This is the primary method to override for custom physics objects.
     */
    protected void releaseFixtures() {
        if (geometry != null) {
            body.destroyFixture(geometry);
            geometry = null;
        }
    };

    /**
     * Draws the texture physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {

    };

    /**
     * Draws the outline of the physics body.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {

        if(shadowShape.equals(ShadowShape.CIRCLE)){
            canvas.drawPhysics(circleShape, Color.YELLOW,getXCoord(),getYCoord(),drawScale.x,drawScale.y);
        }
        else if(shadowShape.equals(ShadowShape.SQUARE)){
            canvas.drawPhysics(boxShape,Color.YELLOW,getXCoord(),getYCoord(),getAngle(),drawScale.x,drawScale.y);
        }

    }

    public void setBehind(boolean bool){
        this.isBelow = bool;
    }

    public boolean getBehind(){
        return isBelow;
    }

    ;

}
