package com.mygdx.game.EnemyControllers;

import com.badlogic.gdx.math.Vector2;
//import com.mygdx.game.EnemyController;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.Obstacles.Player;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;

public class ChaserEnemyController extends com.mygdx.game.EnemyControllers.EnemyController {
    /** Enum to encode the finite state machine */
//    private static enum FSMState {
//        /** The enemy just spawned and is stationary */
//        SPAWN,
//        /** The enemy is idle and stationary, either not aware of the player or on cooldown */
//        IDLE,
//        /** The enemy has spotted the player and is chasing them */
//        CHASE,
//        /** The enemy is attacking the player */
//        ATTACK,
//        /** The enemy has been stunned by the player */
//        STUNNED
//    }
//
//    /** The enemy controlled by this EnemyController */
//    private Enemy enemy;
//
//    /** The enemy's current state */
//    private FSMState state;
//
//    /** The enemy's current target */
//    private Player target;
//
//    /** Representation of the level board: board[i][j] is False if there is no
//     * obstructing obstacle at this coordinate and is True if there is */
//    private boolean[][] board;
//
//    /** The size of a board tile in pixels */
//    private int tileSize;
//
//    private int tileOffset;
//
//    /** The number of ticks since starting this controller */
//    private long ticks;
//
//    private com.mygdx.game.Tile[][] tiles;
//
//    private com.mygdx.game.TileGraph tileGraph;
//
//    private GraphPath<com.mygdx.game.Tile> tilePath;
//
//    private com.mygdx.game.Tile startTile;
//
//    private com.mygdx.game.Tile goalTile;
//
//    private Vector2 goalLoc;
//
//    private int prevAction;
//
//    private boolean firstMove;
//
//    private int moveTime;

    /**
     * Initialize an EnemyController
     *
     * @param board  representation of objects in the level
     * @param enemy  the enemy controlled by this controller
     * @param player the player that will be the enemy's target
     */
    public ChaserEnemyController(boolean[][] board, int tileSize, int tileOffset, Enemy enemy, Player player, Array<ShriekerEnemy> shriekArr) {
        super(board, tileSize, tileOffset, enemy, player, shriekArr);
        target = new Vector2(player.getX(), player.getY());
        super.initTiles(target);
    }
//        this.enemy = enemy;
//        this.board = board;
//        this.tileSize = tileSize;
//        this.tileOffset = tileOffset;
//        target = player;
//        firstMove = true;
//        moveTime = 0;
//
//        state = FSMState.IDLE;
//        ticks = 0;
//
//        tiles = new com.mygdx.game.Tile[board.length][board[0].length];
//        for (int i = 0; i < tiles.length; i++) {
//            for (int j = 0; j < tiles[0].length; j++) {
//                tiles[i][j] = new com.mygdx.game.Tile(i, j, board[i][j]);
//            }
//        }
//
//        startTile = tiles[(int)(enemy.getX() / tileSize)][(int)(enemy.getY() / tileSize)];
//        goalTile = tiles[(int)(target.getX() / tileSize)][(int)(target.getY() / tileSize)];
//
//        tileGraph = new TileGraph();
//        for (int i = 0; i < tiles.length; i++) {
//            for (int j = 0; j < tiles[0].length; j++) {
//                tileGraph.addTile(tiles[i][j]);
//                if (i > 0) {
//                    tileGraph.connectTiles(tiles[i][j], tiles[i - 1][j]);
//                }
//                if (i < tiles.length - 1) {
//                    tileGraph.connectTiles(tiles[i][j], tiles[i + 1][j]);
//                }
//                if (j > 0) {
//                    tileGraph.connectTiles(tiles[i][j], tiles[i][j - 1]);
//                }
//                if (j < tiles[0].length - 1) {
//                    tileGraph.connectTiles(tiles[i][j], tiles[i][j + 1]);
//                }
//                if (i > 0 && j > 0 && !tiles[i-1][j].isBlocked() && !tiles[i][j-1].isBlocked())
//                {
//                    tileGraph.connectTiles(tiles[i][j], tiles[i - 1][j - 1]);
//                }
//                if (i < tiles.length - 1 && j > 0 && !tiles[i+1][j].isBlocked() && !tiles[i][j-1].isBlocked())
//                {
//                    tileGraph.connectTiles(tiles[i][j], tiles[i + 1][j - 1]);
//                }
//                if (i > 0 && j < tiles[0].length - 1 && !tiles[i-1][j].isBlocked() && !tiles[i][j+1].isBlocked())
//                {
//                    tileGraph.connectTiles(tiles[i][j], tiles[i - 1][j + 1]);
//                }
//                if (i < tiles.length - 1 && j < tiles[0].length - 1 && !tiles[i+1][j].isBlocked() && !tiles[i][j+1].isBlocked())
//                {
//                    tileGraph.connectTiles(tiles[i][j], tiles[i + 1][j + 1]);
//                }
//            }
//        }
//
//        tilePath = tileGraph.findPath(startTile, goalTile);
//
//        float x = tilePath.get(1).getX() * tileSize + tileOffset;
//        float y = tilePath.get(1).getY() * tileSize + tileOffset;
//        goalLoc = new Vector2(x, y);
//        if ((int)goalLoc.x > (int)target.getX())
//        {
//            prevAction = 1;
//        }
//        else if ((int)goalLoc.x < (int)target.getX())
//        {
//            prevAction = 2;
//        }
//        else if ((int)goalLoc.y > (int)target.getY())
//        {
//            prevAction = 3;
//        }
//        else if ((int)goalLoc.y < (int)target.getY())
//        {
//            prevAction = 4;
//        }
//    }

        /**
         * Returns an int value representing the enemy's next movement action:
         * 0 = no move, 1 = right, 2 = left, 3 = up, 4 = down,
         * 5 = up + right, 6 = down + right, 7 = up + left, 8 = down + left
         */
        public int getAction() {
            target.set(player.getX(), player.getY());
            return super.getAction();
        }
    }
//        ticks++;
//        moveTime++;
//        //System.out.println(moveTime);
//
//        if (ticks % 10 == 0)
//        {
//            changeStateIfApplicable();
//        }
//
//        int action = 0;
//        //System.out.println(target.getX() + " " + target.getY());
//        //System.out.println(goalLoc.y + " " + enemy.getY());
//        //System.out.println(((int)goalLoc.x == (int)enemy.getX()) + " " + ((int)goalLoc.y == (int)enemy.getY()));
//        if (state == FSMState.CHASE)
//        {
//            if (firstMove)
//            {
//                action = getMove();
//                firstMove = false;
//            }
//            else {
//                if (goalReached() || moveTime > 30) {
//                    moveTime = 0;
//                    action = getMove();
//                } else {
//                    action = prevAction;
//                }
//            }
//        }
//
//        if (state == FSMState.ATTACK)
//        {
//            enemy.setAttack(false);
//        }
//
//        prevAction = action;
//        //System.out.println(action);
//        return action;
//    }

    /** Changes the state encoding of this enemy */
//    protected void changeStateIfApplicable()
//    {
//        super.changeStateIfApplicable();
//        com.mygdx.game.Tile enemyTile = tiles[(int) (enemy.getX() / tileSize)][(int) (enemy.getY() / tileSize)];
//        com.mygdx.game.Tile targetTile = tiles[(int) (target.getX() / tileSize)][(int) (target.getY() / tileSize)];
//        switch(state) {
//            case SPAWN:
//                state = FSMState.IDLE;
//            case IDLE:
//                if (enemy.isStunned())
//                {
//                    state = FSMState.STUNNED;
//                }
//                else if (enemy.isRevealed() && enemy.canAttack())
//                {
//                    state = FSMState.CHASE;
//                }
//                else if (enemyTile.equals(targetTile) && enemy.canAttack() && target.canLoseLife())
//                {
//                    state = FSMState.ATTACK;
//                }
//                break;
//            case CHASE:
//                if (enemy.isStunned())
//                {
//                    state = FSMState.STUNNED;
//                }
//                else if (!enemy.isRevealed() || !enemy.canAttack())
//                {
//                    state = FSMState.IDLE;
//                }
//                else if (enemyTile.equals(targetTile) && enemy.canAttack() && target.canLoseLife())
//                {
//                    state = FSMState.ATTACK;
//                }
//                break;
//            case ATTACK:
//                target.setHealth(target.getHealth() - 1);
//                target.coolDown(false);
//                state = FSMState.IDLE;
//                break;
//            case STUNNED:
//                if (!enemy.isStunned())
//                {
//                    if (enemy.canAttack())
//                    {
//                        if (enemy.isRevealed())
//                        {
//                            state = FSMState.CHASE;
//                        }
//                        else { state = FSMState.IDLE; }
//                    }
//                    else {
//                        state = FSMState.IDLE;
//                    }
//                }
//                break;
//        }
//    }

    /** Gets the enemy's next move using BFS */
//    int getMove() {
//        super.getMove();
//        startTile = tiles[(int) (enemy.getX() / tileSize)][(int) (enemy.getY() / tileSize)];
//        goalTile = tiles[(int) (target.getX() / tileSize)][(int) (target.getY() / tileSize)];
//
//        tilePath = tileGraph.findPath(startTile, goalTile);
//
////        for (Tile t : tilePath)
////        {
////            System.out.println(t.getX() + " " + t.getY());
////        }
//        com.mygdx.game.Tile nextTile;
//        if (tilePath.getCount() > 1) {
//            nextTile = tilePath.get(1);
//        }
//        else if (tilePath.getCount() == 1){
//            nextTile = tilePath.get(0);
//        }
//        else { return 0; }
//
//        goalLoc = setGoal(nextTile);
//
//        //System.out.println(startTile.getX() + " " + startTile.getY());
//        //System.out.println(goalTile.getX() + " " + goalTile.getY());
//        //System.out.println(nextTile.getX() + " " + nextTile.getY());
//        //System.out.println(nextTile.isBlocked());
//
//        int action = 0;
//        if ((int)goalLoc.x > (int)enemy.getX() && (int)goalLoc.y > (int)enemy.getY())
//        {
//            action = 5;
//        }
//        else if ((int)goalLoc.x > (int)enemy.getX() && (int)goalLoc.y < (int)enemy.getY())
//        {
//            action = 6;
//        }
//        else if ((int)goalLoc.x < (int)enemy.getX() && (int)goalLoc.y > (int)enemy.getY())
//        {
//            action = 7;
//        }
//        else if ((int)goalLoc.x < (int)enemy.getX() && (int)goalLoc.y < (int)enemy.getY())
//        {
//            action = 8;
//        }
//        else if ((int)goalLoc.x > (int)enemy.getX())
//        {
//            action = 1;
//        }
//        else if ((int)goalLoc.x < (int)enemy.getX())
//        {
//            action = 2;
//        }
//        else if ((int)goalLoc.y > (int)enemy.getY())
//        {
//            action = 3;
//        }
//        else if ((int)goalLoc.y < (int)enemy.getY())
//        {
//            action = 4;
//        }
////        if (nextTile.getX() == startTile.getX() + 1) {
////            action = 1;
////        } else if (nextTile.getX() == startTile.getX() - 1) {
////            action = 2;
////        } else if (nextTile.getY() == startTile.getY() + 1) {
////            action = 3;
////        } else if (nextTile.getY() == startTile.getY() - 1) {
////            action = 4;
////        }
//        //System.out.println(action);
//        return action;
//    }

//    private Vector2 setGoal(Tile t)
//    {
//        float x = t.getX() * tileSize + tileOffset;
//        float y = t.getY() * tileSize + tileOffset;
//        return new Vector2(x, y);
//    }
//
//    private boolean goalReached()
//    {
//        if (((int)goalLoc.x - (int)enemy.getX() <= 1 && (int)goalLoc.x - (int)enemy.getX() >= -1)
//                && ((int)goalLoc.y - (int)enemy.getY() <= 1 && (int)goalLoc.y - (int)enemy.getY() >= -1))
//        {
//            enemy.setPosition(goalLoc.x, goalLoc.y);
//            return true;
//        }
//        return false;
//    }
//}
