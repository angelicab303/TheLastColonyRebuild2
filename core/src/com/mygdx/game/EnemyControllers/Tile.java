package com.mygdx.game.EnemyControllers;

public class Tile {

    /** X coordinate of this tile */
    private float x;
    /** Y coordinate of this tile */
    private float y;
    /** Whether this tile is blocked by an obstacle */
    private boolean isBlocked;
    /** Index of this tile in the A* algorithm */
    private int index;

    public Tile (float x, float y, boolean blocked) {
        this.x = x;
        this.y = y;
        isBlocked = blocked;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isBlocked() { return isBlocked; }

    public int getIndex() {
        return index;
    }

    public void setIndex (int index) {
        this.index = index;
    }
}
