package com.mygdx.game;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;

public class TileConnection implements Connection<Tile> {

    private Tile fromTile;

    private Tile toTile;

    private float cost;

    public TileConnection (Tile fromTile, Tile toTile) {
        this.fromTile = fromTile;
        this.toTile = toTile;
        //cost = Math.abs((fromTile.getX() - toTile.getX()) + (fromTile.getY() - toTile.getY()));
        if (fromTile.isBlocked() || toTile.isBlocked()) {
            cost = Integer.MAX_VALUE;
        }
        else {
            cost = Vector2.dst(fromTile.getX(), fromTile.getY(), toTile.getX(), toTile.getY());
        }
    }


    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Tile getFromNode() {
        return fromTile;
    }

    @Override
    public Tile getToNode() {
        return toTile;
    }
}