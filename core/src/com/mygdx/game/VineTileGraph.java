package com.mygdx.game;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.Tile;
import com.mygdx.game.EnemyControllers.*;

public class VineTileGraph implements IndexedGraph<Tile> {
    VineTileHeuristic tileHeuristic = new VineTileHeuristic();
    Array<Tile> tiles = new Array<>();
    Array<VineTileConnection> connections = new Array<>();
    ObjectMap<Tile, Array<Connection<Tile>>> connectionsMap = new ObjectMap<>();

    private int numTiles = 0;

    public void addTile (Tile tile) {
        tile.setIndex(numTiles);
        numTiles++;

        tiles.add(tile);
    }

    public void connectTiles (Tile fromTile, Tile toTile) {
        VineTileConnection connection = new VineTileConnection(fromTile, toTile);
        if (!connectionsMap.containsKey(fromTile)) {
            connectionsMap.put(fromTile, new Array<Connection<Tile>>());
        }
        connectionsMap.get(fromTile).add(connection);
        connections.add(connection);
    }

    public GraphPath<Tile> findPath(Tile startTile, Tile goalTile) {
        GraphPath<Tile> tilePath = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startTile, goalTile, tileHeuristic, tilePath);
        return tilePath;
    }

    @Override
    public int getIndex(Tile tile) {
        return tile.getIndex();
    }

    @Override
    public int getNodeCount() {
        return numTiles;
    }

    @Override
    public Array<Connection<Tile>> getConnections(Tile fromTile) {
        if (connectionsMap.containsKey(fromTile)) {
            return connectionsMap.get(fromTile);
        }
        else {
            return new Array<>(0);
        }
    }
}
