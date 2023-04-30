package com.mygdx.game.Obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.game.GameCanvas;

public class FloorTile {

    private Vector2 origin;
    private Vector2 position;
    private Vector2 drawScale;
    private float scale;
    private TextureRegion texture;


    public FloorTile(float x, float y, TextureRegion value, float scale) {
        origin = new Vector2(0, 0);
        position = new Vector2(x, y);
        setTexture(value);
        position = new Vector2(x, y);
        this.scale = scale;
        drawScale = new Vector2(1, 1);
    }

    /**
     * Sets the object texture for drawing purposes.
     *
     * In order for drawing to work properly, you MUST set the drawScale.
     * The drawScale converts the physics units to pixels.
     *
     * @param value  the object texture for drawing purposes.
     */
    public void setTexture(TextureRegion value) {
        texture = value;
        origin.set(texture.getRegionWidth()/2.0f, texture.getRegionHeight()/2.0f);
    }

    /**
     * Returns the x-coordinate of the tree position
     *
     * @return the x-coordinate of the tree position
     */
    public float getX() {
        return position.x;
    }

    /**
     * Sets the x-coordinate of the tree position
     *
     * @param value the x-coordinate of the tree position
     */
    public void setX(float value) {
        position.x = value;
    }

    /**
     * Returns the y-coordinate of the tree position
     *
     * @return the y-coordinate of the tree position
     */
    public float getY() {
        return position.y;
    }

    /**
     * Sets the y-coordinate of the tree position
     *
     * @param value the y-coordinate of the tree position
     */
    public void setY(float value) {
        position.y = value;
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
        return position;
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

}
