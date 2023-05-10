package com.mygdx.game.EnemyControllers;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.Obstacles.Player;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;

import com.badlogic.gdx.utils.Array;

import com.mygdx.game.Tile;
import com.mygdx.game.TileGraph;


/** Controller class for enemy AI */
public class EnemyController {
    /** Enum to encode the finite state machine */

    protected enum FSMState {
        /** The enemy just spawned and is stationary */
        SPAWN,
        /** The enemy is idle and stationary, either not aware of the player or on cooldown */
        IDLE,
        /** The enemy has spotted the player and is chasing them */
        CHASE,
        /** The enemy is attacking the player */
        ATTACK,
        /** The enemy has been stunned by the player */
        STUNNED
    }
    /** The enemy controlled by this EnemyController */
    protected Enemy enemy;

    /** The enemy's current state */
    protected FSMState state;

    /** Distance to be alerted by the Shrieker */
    protected final float ALERT_DISTANCE = 600.0f;

    /** The array of shrieker enemies */
    private Array<ShriekerEnemy> shriekerArr;


    protected Vector2 target;


    /** Representation of the level board: board[i][j] is False if there is no
     * obstructing obstacle at this coordinate and is True if there is */
    protected boolean[][] board;

    /** The size of a board tile in pixels */
    protected int tileSize;

    protected int tileOffset;

    /** The number of ticks since starting this controller */
    protected long ticks;

    protected Tile[][] tiles;

    protected TileGraph tileGraph;

    protected GraphPath<Tile> tilePath;

    protected Tile startTile;

    protected Tile goalTile;

    protected Tile nextTile;

    protected Vector2 goalLoc;

    protected int prevAction;

    protected boolean firstMove;

    protected int moveTime;


    private boolean alertAllEnemies;


    protected Player player;


    public boolean allEnemiesAlerted() {
        return alertAllEnemies;
    }

    public void setAlertAllEnemies(boolean value) {
        alertAllEnemies = value;
    }

    /**
     * Initialize an EnemyController
     *
     * @param board representation of objects in the level
     * @param enemy the enemy controlled by this controller
     * @param player the player that will be the enemy's target
     *
     * */
    public EnemyController(boolean[][] board, int tileSize, int tileOffset, Enemy enemy, Player player, Array<ShriekerEnemy> shriekArr)
    {
        this.enemy = enemy;
        this.shriekerArr = shriekArr;
        this.board = board;
        this.tileSize = tileSize;
        this.tileOffset = tileOffset;
        this.player = player;
        target = new Vector2(player.getX(), player.getY());
//        target = player;
        firstMove = true;
        moveTime = 0;

        state = FSMState.IDLE;
        ticks = 0;
    }

    protected void initTiles(Vector2 enemyTarget) {
        tiles = new Tile[board.length][board[0].length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tiles[i][j] = new Tile(i, j, board[i][j]);
            }
        }

        startTile = tiles[(int)(enemy.getX() / tileSize)][(int)(enemy.getY() / tileSize)];
        goalTile = tiles[(int)(enemyTarget.x / tileSize)][(int)(enemyTarget.y / tileSize)];

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
                if (i > 0 && j > 0 && !tiles[i-1][j].isBlocked() && !tiles[i][j-1].isBlocked())
                {
                    tileGraph.connectTiles(tiles[i][j], tiles[i - 1][j - 1]);
                }
                if (i < tiles.length - 1 && j > 0 && !tiles[i+1][j].isBlocked() && !tiles[i][j-1].isBlocked())
                {
                    tileGraph.connectTiles(tiles[i][j], tiles[i + 1][j - 1]);
                }
                if (i > 0 && j < tiles[0].length - 1 && !tiles[i-1][j].isBlocked() && !tiles[i][j+1].isBlocked())
                {
                    tileGraph.connectTiles(tiles[i][j], tiles[i - 1][j + 1]);
                }
                if (i < tiles.length - 1 && j < tiles[0].length - 1 && !tiles[i+1][j].isBlocked() && !tiles[i][j+1].isBlocked())
                {
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
//    /**
//     * Initialize an EnemyController with shrieker enemies
//     *
//     * @param board representation of objects in the level
//     * @param enemy the enemy controlled by this controller
//     * @param player the player that will be the enemy's target
//     *
//     * */
//    public EnemyController(boolean[][] board, int tileSize, int tileOffset, Enemy enemy, Player player, Array<ShriekerEnemy> shriekArr)
//    {
//        this(board, tileSize, tileOffset, enemy, player);
//        this.shriekerArr = shriekArr;
//    }

    /** Returns an int value representing the enemy's next movement action:
     * 0 = no move, 1 = right, 2 = left, 3 = up, 4 = down,
     * 5 = up + right, 6 = down + right, 7 = up + left, 8 = down + left */
    public int getAction()
    {
        ticks++;
        moveTime++;
        //System.out.println(moveTime);

        if (ticks % 10 == 0)
        {
            changeStateIfApplicable();
        }

        int action = 0;
        //System.out.println(target.getX() + " " + target.getY());
        //System.out.println(goalLoc.y + " " + enemy.getY());
        //System.out.println(((int)goalLoc.x == (int)enemy.getX()) + " " + ((int)goalLoc.y == (int)enemy.getY()));
        if (state == FSMState.CHASE)
        {
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
            action = getMove();
        }

        if (state == FSMState.ATTACK)
        {
//            enemy.setAttack(false);
        }

        //prevAction = action;
        //System.out.println(action);
        return action;
    }

    protected void chooseTarget() {
        if(player.getSurvivorsFollowing().isEmpty()) {
            target.x = player.getX();
            target.y = player.getY();
        } else {
            for (int i = 0; i < player.getSurvivorsFollowing().size; i++) {
                if (!player.getSurvivorsFollowing().get(i).isTargetOfEnemy() && player.getSurvivorsFollowing().get(i).canLoseLife()) {
                    target.x = player.getSurvivorsFollowing().get(i).getX();
                    target.y = player.getSurvivorsFollowing().get(i).getY();
//                    survivorTarget = player.getSurvivorsFollowing().get(i);
//                    followingSurvivor = true;
                    player.getSurvivorsFollowing().get(i).setTargetOfEnemy(true);
                }
            }
        }
    }
    /** Changes the state encoding of this enemy */
    protected void changeStateIfApplicable()
    {
        chooseTarget();
        Tile enemyTile = tiles[(int) (enemy.getX() / tileSize)][(int) (enemy.getY() / tileSize)];
        Tile targetTile = tiles[(int) (target.x / tileSize)][(int) (target.y / tileSize)];
        alertAllEnemies = false;
        ShriekerEnemy activeShrieker = null;
        for (ShriekerEnemy shrieker : shriekerArr){
            if (shrieker.getShrieking()){
                alertAllEnemies = true;
                activeShrieker = shrieker;
            }
        }

        switch(state) {
            case SPAWN:
                state = FSMState.IDLE;
                break;
            case IDLE:
                if (enemy.isStunned())
                {
                    state = FSMState.STUNNED;
                }
                else if (enemy.isRevealed() && enemy.canAttack())
                {
                    state = FSMState.CHASE;
                }
                else if (enemyTile.equals(targetTile) && enemy.canAttack() && player.canLoseLife())
                {
                    state = FSMState.ATTACK;
                }
                else if (alertAllEnemies)
                {
                    Vector2 enemyLoc = new Vector2(enemy.getX(), enemy.getY());
                    Vector2 shriekerLoc = new Vector2(activeShrieker.getX(), activeShrieker.getY());
                    if (enemyLoc.dst(shriekerLoc) <= ALERT_DISTANCE){
                        state = FSMState.CHASE;
                        target = new Vector2 (player.getX(), player.getY());
                    }
                }
                break;
            case CHASE:
                if (enemy.isStunned())
                {
                    state = FSMState.STUNNED;
                }
                else if (!enemy.isRevealed() || !enemy.canAttack())
                {
                    state = FSMState.IDLE;
                }
                else if (enemyTile.equals(targetTile) && enemy.canAttack() && player.canLoseLife())
                {
                    state = FSMState.ATTACK;
                }
                break;
            case ATTACK:
                player.setHealth(player.getHealth() - 1);
                player.coolDown(false);
                state = FSMState.IDLE;
                break;
            case STUNNED:
                if (!enemy.isStunned())
                {
                    if (enemy.canAttack())
                    {
                        if (enemy.isRevealed())
                        {
                            // Find out if the enemy is near enough to the player to hear them
                            // Enemies should chase the player when they can hear them,
                            //  but they should hear differently in and out of smog
                            //  and if they were just chasing the player (stunned), then they should
                            //  know where they were going and continue to search for some time period
                            state = FSMState.CHASE;
                        }
                        else { state = FSMState.IDLE; }
                    }
                    else {
                        state = FSMState.IDLE;
                    }
                }
                break;
        }
    }

    /** Gets the enemy's next move using BFS */
     protected int getMove() {
        startTile = tiles[(int) (enemy.getX() / tileSize)][(int) (enemy.getY() / tileSize)];
        goalTile = tiles[(int) (target.x / tileSize)][(int) (target.y / tileSize)];

        tilePath = tileGraph.findPath(startTile, goalTile);

//        for (Tile t : tilePath)
//        {
//            System.out.println(t.getX() + " " + t.getY());
//        }
        Tile next;
        if (tilePath.getCount() > 1) {
            next = tilePath.get(1);
        }
        else if (tilePath.getCount() == 1){
            next = tilePath.get(0);
        }
        else { return 0; }

        if (goalReached() || moveTime > 30) {
            moveTime = 0;
            nextTile = next;
            goalLoc = setGoal(nextTile);
        }

        //System.out.println(startTile.getX() + " " + startTile.getY());
        //System.out.println(goalTile.getX() + " " + goalTile.getY());
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
             if ((int) goalLoc.x > (int) enemy.getX() && (int) goalLoc.y > (int) enemy.getY()) {
                 action = 5;
             } else if ((int) goalLoc.x > (int) enemy.getX() && (int) goalLoc.y < (int) enemy.getY()) {
                 action = 6;
             } else if ((int) goalLoc.x < (int) enemy.getX() && (int) goalLoc.y > (int) enemy.getY()) {
                 action = 7;
             } else if ((int) goalLoc.x < (int) enemy.getX() && (int) goalLoc.y < (int) enemy.getY()) {
                 action = 8;
             } else if ((int) goalLoc.x > (int) enemy.getX()) {
                 action = 1;
             } else if ((int) goalLoc.x < (int) enemy.getX()) {
                 action = 2;
             } else if ((int) goalLoc.y > (int) enemy.getY()) {
                 action = 3;
             } else if ((int) goalLoc.y < (int) enemy.getY()) {
                 action = 4;
             }
         }
//        if (nextTile.getX() == startTile.getX() + 1) {
//            action = 1;
//        } else if (nextTile.getX() == startTile.getX() - 1) {
//            action = 2;
//        } else if (nextTile.getY() == startTile.getY() + 1) {
//            action = 3;
//        } else if (nextTile.getY() == startTile.getY() - 1) {
//            action = 4;
//        }
        //System.out.println(action);
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
        if (((int)goalLoc.x - (int)enemy.getX() <= 1 && (int)goalLoc.x - (int)enemy.getX() >= -1)
                && ((int)goalLoc.y - (int)enemy.getY() <= 1 && (int)goalLoc.y - (int)enemy.getY() >= -1))
        {
            enemy.setPosition(goalLoc.x, goalLoc.y);
            return true;
        }
        return false;
    }

}
