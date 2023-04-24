package com.mygdx.game;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class TileHeuristic implements Heuristic<Tile> {
    @Override
    public float estimate(Tile currentTile, Tile goalTile) {
        //return Math.abs((currentTile.getX() - goalTile.getX()) + (currentTile.getY()) - goalTile.getY());
        return Vector2.dst(currentTile.getX(), currentTile.getY(), goalTile.getX(), goalTile.getY());
    }
}
