package com.mygdx.game.Obstacles;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameCanvas;
import obstacle.SimpleObstacle;

public class Shadow extends SimpleObstacle {
    /** Origin of the shadow relative to the owner's sprite **/
    private Vector2 origin;
    private Vector2 position;
    private float radius;

    public Shadow(Vector2 pos, float oX, float oY, float radius) {
        position = new Vector2(pos);
        origin = new Vector2(oX, oY);
        this.radius = radius;
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
    protected void createFixtures() {};

    /**
     * Release the fixtures for this body, reseting the shape
     *
     * This is the primary method to override for custom physics objects.
     */
    protected void releaseFixtures() {};

    /**
     * Draws the texture physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {};

    /**
     * Draws the outline of the physics body.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {};

}
