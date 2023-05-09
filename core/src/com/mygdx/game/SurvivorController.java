package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.mygdx.game.Obstacles.Survivor;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.mygdx.game.Obstacles.Player;
import java.util.ArrayList;

public class SurvivorController {
    /**
     * Enumeration to encode the finite state machine.
     */
    private static enum FSMState {
        /** The survivor just spawned and is stationary */
        IDLE,
        /** The survivor has been rescued and is following the player */
        FOLLOW,
        /** The survivor has been rescued and is walking to the caravan without the player */
        FIND,
        /** The survivor is at the caravan */
        SAFE
    }

    // Instance Attributes
    /** The survivor being controlled by this AIController */
    private Survivor survivor;
    /** The player's position; used for pathfinding */
    private Vector2 playerPos;
    /** The survivor's current state in the FSM */
    public FSMState state;
    /** The caravan's position */
    private Vector2 caravanPos;
    /** The number of ticks since we started this controller */
    private long ticks;
    /** The level board as represented by a 2d array and contains False in [i][j] if there is no obstacle in that
     * tile (i, j) and True if there is */
    private boolean[][] board;
    /** The size of a board tile in pixels */
    private int tileSize;

    private int tileOffset;
    /** The target of this survivor; used for pathfinding */
    private Vector2 target;
    /** The tiles of this level; used for pathfinding */
    private Tile[][] tiles;
    /** The graph of tiles; used for pathfinding */
    private TileGraph tileGraph;
    /** The path of tiles from the start tile to the goal tile in survivor pathfinding */
    private GraphPath<Tile> tilePath;
    /** The starting tile of the survivor in pathfinding (current tile) */
    private Tile startTile;
    /** The goal tile of the survivor; used for pathfinding */
    private Tile goalTile;

    private Tile nextTile;

    private Vector2 goalLoc;

    private int moveTime;

    /**
     * Creates a SurvivorController for the survivor with the given id.
     *
     * @param survivor The survivor controlled by this controller
     * @param board A 2d boolean array that is true in a position if it contains a game obstacle and false if not  (for pathfinding)
     * @param caravanPos The caravan (will be the target for the survivor when walking)
     */
    public SurvivorController(Survivor survivor, Vector2 caravanPos, Vector2 playerPos, boolean[][] board, boolean[][] smogBoard, int tileSize, int tileOffset) {
        this.survivor = survivor;
        this.board = board;
        this.playerPos = playerPos;
        this.caravanPos = caravanPos;
        this.tileSize = tileSize;
        this.tileOffset = tileOffset;

        state = FSMState.IDLE;
        ticks = 0;
        target = caravanPos;
        tiles = new Tile[board.length][board[0].length];
        moveTime = 0;

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
//                if (board[i][j])
//                {
//                    System.out.println(i + " " + j);
//                }
                tiles[i][j] = new Tile(i, j, (board[i][j] ||
                        smogBoard[i * 2][(j * 2)] ||
                        smogBoard[i * 2][(j * 2) + 1] ||
                        smogBoard[(i*2) + 1][(j * 2)] ||
                        smogBoard[(i*2) + 1][(j * 2) + 1]));
            }
        }

        startTile = tiles[(int) (survivor.getX() / tileSize)][(int) (survivor.getY() / tileSize)];
        goalTile = tiles[(int) (target.x / tileSize)][(int) (target.y / tileSize)];

        tileGraph = new TileGraph();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tileGraph.addTile(tiles[i][j]);
                if (i > 0) {
                    tileGraph.connectTiles(tiles[i][j], tiles[i - 1][j]);
                }
                if (i < tiles.length - 1) {
                    tileGraph.connectTiles(tiles[i][j], tiles[i + 1][j]);
                }
                if (j > 0) {
                    tileGraph.connectTiles(tiles[i][j], tiles[i][j - 1]);
                }
                if (j < tiles[0].length - 1) {
                    tileGraph.connectTiles(tiles[i][j], tiles[i][j + 1]);
                }
                if (i > 0 && j > 0 && !tiles[i - 1][j].isBlocked() && !tiles[i][j - 1].isBlocked()) {
                    tileGraph.connectTiles(tiles[i][j], tiles[i - 1][j - 1]);
                }
                if (i < tiles.length - 1 && j > 0 && !tiles[i + 1][j].isBlocked() && !tiles[i][j - 1].isBlocked()) {
                    tileGraph.connectTiles(tiles[i][j], tiles[i + 1][j - 1]);
                }
                if (i > 0 && j < tiles[0].length - 1 && !tiles[i - 1][j].isBlocked() && !tiles[i][j + 1].isBlocked()) {
                    tileGraph.connectTiles(tiles[i][j], tiles[i - 1][j + 1]);
                }
                if (i < tiles.length - 1 && j < tiles[0].length - 1 && !tiles[i + 1][j].isBlocked() && !tiles[i][j + 1].isBlocked()) {
                    tileGraph.connectTiles(tiles[i][j], tiles[i + 1][j + 1]);
                }
            }
        }
        tilePath = tileGraph.findPath(startTile, goalTile);

        nextTile = tilePath.get(1);

        float x = tilePath.get(1).getX() * tileSize + tileOffset;
        float y = tilePath.get(1).getY() * tileSize + tileOffset;
        goalLoc = new Vector2(x, y);
    }

        /** Returns an int value representing the survivor's next movement action:
         * 0 = no move, 1 = right, 2 = left, 3 = down, 4 = up */
        public int getAction () {
            ticks++;
            moveTime++;

            if (ticks % 10 == 0) {
                changeStateIfApplicable();
            }

            int action = 0;
            if (state == FSMState.FOLLOW || state == FSMState.FIND) {
                action = getMove();
            }
            if (state == FSMState.SAFE) {
                survivor.rescue();
            }
            return action;
        }

        /**
         * Change the state of the survivor if appropriate.
         */
        private void changeStateIfApplicable () {
            switch (state) {
                case IDLE:
                    // code for state change in spawn state
                    if (survivor.isFollowing()) {
                        state = FSMState.FOLLOW;
                    }
                    break;
                case FOLLOW:
                    // code for state change in follow state
                    if (survivor.getBody().getFixtureList().peek().testPoint(caravanPos.x, caravanPos.y)) {
                        state = FSMState.SAFE;
                    }
                    break;
                case FIND:
                    // code for state change in find state
                    break;
                case SAFE:
                    break;
                default:
                    // Unknown or unhandled state, should never get here
                    assert (false);
                    state = FSMState.IDLE; // If debugging is off
                    break;
            }
        }

        /**
         * Acquire a target to move to (and put it in field target).
         *
         * Here, target is a position vector representing the screen position of the game object (either player or caravan)
         * that this survivor must reach.
         */
        private void selectTarget () {
            switch (state) {
                case IDLE:
                    // no target if we are in IDLE state
                    break;
                case FOLLOW:
                    // the player is the target if we are in FOLLOW state
                    target = caravanPos;
                    break;
                case FIND:
                    // the caravan is the target if we are in TARGET state
                    target = caravanPos;
                    break;
                case SAFE:
                    break;
            }
        }

    /**
     * Returns the next move represented as an int based on the direction of the next tile in the survivor's path.
     * @return next move represented as an int
     */
    private int getMove() {
            startTile = tiles[(int) (survivor.getX()/ tileSize)][(int) (survivor.getY()/tileSize)];
            goalTile = tiles[(int) (target.x / tileSize)][(int) (target.y/tileSize)];

            tilePath = tileGraph.findPath(startTile, goalTile);

            Tile next;
            if (tilePath.getCount() > 1) {
                next = tilePath.get(1);
            } else if (tilePath.getCount() == 1) {
                next = tilePath.get(0);
            } else {
                return 0;
            }

            if (goalReached() || moveTime > 30) {
                moveTime = 0;
                nextTile = next;
                goalLoc = setGoal(nextTile);
            }

            //System.out.println(nextTile.getX() + " " + nextTile.getY());
            //System.out.println(nextTile.isBlocked());

            int action = 0;
            if (nextTile.getX() == startTile.getX() + 1 && nextTile.getY() == startTile.getY() + 1) {
                // up, right diagonal
                action = 5;
            } else if (nextTile.getX() == startTile.getX() + 1 && nextTile.getY() == startTile.getY() - 1) {
                // down, right diagonal
                action = 6;
            } else if (nextTile.getX() == startTile.getX() - 1 && nextTile.getY() == startTile.getY() + 1) {
                // up, left diagonal
                action = 7;
            } else if (nextTile.getX() == startTile.getX() - 1 && nextTile.getY() == startTile.getY() - 1) {
                // down, left diagonal
                action = 8;
            } else if (nextTile.getX() == startTile.getX() + 1) {
                // right
                action = 1;
            } else if (nextTile.getX() == startTile.getX() - 1) {
                // left
                action = 2;
            } else if (nextTile.getY() == startTile.getY() + 1) {
                // up
                action = 3;
            } else if (nextTile.getY() == startTile.getY() - 1) {
                // down
                action = 4;
            }
            else if (nextTile.getX() == startTile.getX() && nextTile.getY() == startTile.getY()) {
                if ((int) goalLoc.x > (int) survivor.getX() && (int) goalLoc.y > (int) survivor.getY()) {
                    action = 5;
                } else if ((int) goalLoc.x > (int) survivor.getX() && (int) goalLoc.y < (int) survivor.getY()) {
                    action = 6;
                } else if ((int) goalLoc.x < (int) survivor.getX() && (int) goalLoc.y > (int) survivor.getY()) {
                    action = 7;
                } else if ((int) goalLoc.x < (int) survivor.getX() && (int) goalLoc.y < (int) survivor.getY()) {
                    action = 8;
                } else if ((int) goalLoc.x > (int) survivor.getX()) {
                    action = 1;
                } else if ((int) goalLoc.x < (int) survivor.getX()) {
                    action = 2;
                } else if ((int) goalLoc.y > (int) survivor.getY()) {
                    action = 3;
                } else if ((int) goalLoc.y < (int) survivor.getY()) {
                    action = 4;
                }
            }
            return action;
        }

    Vector2 setGoal(Tile t)
    {
        float x = t.getX() * tileSize + tileOffset;
        float y = t.getY() * tileSize + tileOffset;
        return new Vector2(x, y);
    }

    protected boolean goalReached()
    {
        if (((int)goalLoc.x - (int)survivor.getX() <= 1 && (int)goalLoc.x - (int)survivor.getX() >= -1)
                && ((int)goalLoc.y - (int)survivor.getY() <= 1 && (int)goalLoc.y - (int)survivor.getY() >= -1))
        {
            survivor.setPosition(goalLoc.x, goalLoc.y);
            return true;
        }
        return false;
    }
}