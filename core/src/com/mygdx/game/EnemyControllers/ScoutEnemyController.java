package com.mygdx.game.EnemyControllers;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Obstacles.Enemies.*;
import com.mygdx.game.Obstacles.Player;
import com.mygdx.game.Obstacles.Survivor;
import com.mygdx.game.Tile;
import com.mygdx.game.VineTileGraph;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;

public class ScoutEnemyController extends com.mygdx.game.EnemyControllers.EnemyController {
    protected FSMState state;
    private static float VINE_CHASE_DIST = 400f;
    private boolean[][] containsVine;
    private float[] patrolPointsHor;
    private float[] patrolPointsVert;
    private static final float PATROL_RANGE = 200f;
    private float dist;
    ScoutEnemy enemy;

    protected VineTileGraph vineTileGraph;

    protected GraphPath<Tile> tilePath;
    protected GraphPath<Tile> vineTilePath;

    protected long directionalTick;

    private final float SHOOT_VINE_RADIUS = 20f;

    Survivor survivorTarget;

    boolean followingSurvivor;

    public enum FSMState {
        /**
         * The enemy just spawned and is stationary
         */
        SPAWN,
        /**
         * The enemy is idle and stationary, either not aware of the player or on cooldown
         */
        IDLE,
        /**
         * The enemy is wandering but is not close enough to the player to prompt attack
         */
        PATROL,
        /**
         * The enemy has spotted the player and is chasing them
         */
        EXTENDVINE,
        /**
         * The enemy is attacking the player
         */
        ATTACK,
        /**
         * The enemy has been stunned by the player
         */
        STUNNED
    }

    private void clearContainsVine() {
        for (int r = 0; r < containsVine.length; r++) {
            for (int c = 0; c < containsVine[r].length; c++) {
                containsVine[r][c] = false;
            }
        }
    }

    public ScoutEnemyController(boolean[][] board, int tileSize, int tileOffset, ScoutEnemy enemy, Player player) {
        super(board, tileSize, tileOffset, enemy, player);
        this.enemy = enemy;
        state = FSMState.IDLE;
        // change later
        patrolPointsHor = new float[2];
        patrolPointsVert = new float[2];
        patrolPointsHor[0] = enemy.getX() - PATROL_RANGE / 2;
        patrolPointsHor[1] = enemy.getX() + PATROL_RANGE / 2;
        patrolPointsVert[0] = enemy.getY() - PATROL_RANGE / 2;
        patrolPointsVert[1] = enemy.getY() + PATROL_RANGE / 2;

        target = new Vector2(patrolPointsHor[1], enemy.getY());
        dist = Vector2.dst(player.getX(), player.getY(), enemy.getX(), enemy.getY());
        super.initTiles(target);
        initVineTiles(target);
        containsVine = new boolean[board.length][board[0].length];
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                containsVine[r][c] = false;
            }
        }
        followingSurvivor = false;
        survivorTarget = null;
    }

    protected void initVineTiles(Vector2 enemyTarget) {
        tiles = new Tile[board.length][board[0].length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tiles[i][j] = new Tile(i, j, board[i][j]);
            }
        }

        startTile = tiles[(int) (enemy.getX() / tileSize)][(int) (enemy.getY() / tileSize)];
        goalTile = tiles[(int) (enemyTarget.x / tileSize)][(int) (enemyTarget.y / tileSize)];

        vineTileGraph = new VineTileGraph();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                vineTileGraph.addTile(tiles[i][j]);
                if (i > 0) {
                    vineTileGraph.connectTiles(tiles[i][j], tiles[i - 1][j]);
                }
                if (i < tiles.length - 1) {
                    vineTileGraph.connectTiles(tiles[i][j], tiles[i + 1][j]);
                }
                if (j > 0) {
                    vineTileGraph.connectTiles(tiles[i][j], tiles[i][j - 1]);
                }
                if (j < tiles[0].length - 1) {
                    vineTileGraph.connectTiles(tiles[i][j], tiles[i][j + 1]);
                }
            }
        }

        vineTilePath = vineTileGraph.findPath(startTile, goalTile);

        float x = vineTilePath.get(1).getX() * tileSize + tileOffset;
        float y = vineTilePath.get(1).getY() * tileSize + tileOffset;
        goalLoc = new Vector2(x, y);
    }


    protected void changeStateIfApplicable() {
        dist = Vector2.dst(enemy.getX(), enemy.getY(), target.x, target.y);
        alertAllEnemies = false;
        ShriekerEnemy activeShrieker = null;
        for (ShriekerEnemy shrieker : shriekerArr){
            if (shrieker.getShrieking()){
                alertAllEnemies = true;
                activeShrieker = shrieker;
            }
        }
        switch (state) {
            case SPAWN:
                state = FSMState.IDLE;
                break;
            case IDLE:
                if (enemy.isStunned()) {
                    state = FSMState.STUNNED;
                } else if (ticks > 50) {
                    state = FSMState.PATROL;
                }
                else if (alertAllEnemies)
                {
                    Vector2 enemyLoc = new Vector2(enemy.getX(), enemy.getY());
                    Vector2 shriekerLoc = new Vector2(activeShrieker.getX(), activeShrieker.getY());
                    if (enemyLoc.dst(shriekerLoc) <= ALERT_DISTANCE){
                        state = FSMState.EXTENDVINE;
                        target = new Vector2 (player.getX(), player.getY());
                    }
                }
                break;
            case PATROL:
                if (enemy.isStunned() && !enemy.isExtendingVines()) {
                    state = FSMState.STUNNED;
                } else if (enemy.canAttack() && dist < VINE_CHASE_DIST && enemy.isRevealed())
                        /*(player.getX() <= patrolPointsHor[1] && player.getX() >= patrolPointsHor[0] &&
                                player.getY() <= patrolPointsVert[1] && player.getY() >= patrolPointsVert[0]))*/ {
                    enemy.setShrinkVines(true);
                    clearContainsVine();
                    state = FSMState.EXTENDVINE;
                    enemy.setAttacking(true);
                }
                break;
            case EXTENDVINE:
                if (!enemy.canAttack() && !enemy.isExtendingVines) {
                    enemy.setShrinkVines(true);
                    clearContainsVine();
                    state = FSMState.PATROL;
                    enemy.setAttacking(false);
                } //else if (enemyTile.equals(targetTile) && enemy.canAttack() && player.canLoseLife()) {
                else if (containsVine[(int) (player.getX() / tileSize)][(int) (player.getY() / tileSize)] && enemy.canAttack() && player.canLoseLife()) {
//                    enemy.setShrinkVines(true);
                    state = FSMState.ATTACK;
                }
                break;
            case ATTACK:
                if (!enemy.isExtendingVines) {
                    state = FSMState.PATROL;
                    enemy.setAttacking(false);
                }
                break;
            case STUNNED:
                if (!enemy.isStunned()) {
                    state = FSMState.PATROL;
                } else {
                    state = FSMState.IDLE;
                }
                break;
        }
    }

    private void selectTarget() {
        if (!player.getSurvivorsFollowing().isEmpty()) {
            for (int i = 0; i < player.getSurvivorsFollowing().size; i++) {
                if (!player.getSurvivorsFollowing().get(i).isTargetOfEnemy() /*&& player.getSurvivorsFollowing().get(i).canLoseLife()*/) {
                    target.x = player.getSurvivorsFollowing().get(i).getX();
                    target.y = player.getSurvivorsFollowing().get(i).getY();
                    survivorTarget = player.getSurvivorsFollowing().get(i);
                    followingSurvivor = true;
                    player.getSurvivorsFollowing().get(i).setTargetOfEnemy(true);
                }
            }
        } else {
            target.x = player.getX();
            target.y = player.getY();
        }
    }

    public FSMState getScoutState() { return state; }

    public int getAction() {
//        if (enemy.isExtendingVines) {
//            System.out.println(state);
//        }
//        System.out.println(state);
        ticks++;
        moveTime++;
        directionalTick++;

        if (ticks % 10 == 0) {
            changeStateIfApplicable();
        }

        int action = 0;
        if (state == FSMState.PATROL) {
            if (directionalTick % 150 == 0) {
                if (prevAction == 1) {
                    action = 2;
                } else {
                    action = 1;
                }
                directionalTick = 0;
            } else {
                action = prevAction;
            }
        } else if (state == FSMState.EXTENDVINE) {
            enemy.setExtendingVines(true);
            selectTarget();

            if (firstMove) {
                action = getMoveVine();
                firstMove = false;
            } else {
                if (goalReached() || moveTime > 30) {
                    moveTime = 0;
                    action = getMoveVine();
                } else {
                    action = prevAction;
                }
            }
            if (enemy.areVinesShrinking() && followingSurvivor) {
                survivorTarget.setTargetOfEnemy(false);
                followingSurvivor = false;
            }
        } else if (state == FSMState.ATTACK) {
            if (enemy.canAttack()) {
                if (!followingSurvivor) {
                    player.setHealth(player.getHealth() - 1);
                    player.coolDown(false);
                }
                else {
                    if (survivorTarget.canLoseLife()) {
                        survivorTarget.loseLife();
//                        survivorTarget.setTargetOfEnemy(false);
//                        followingSurvivor = false;
                    }
                    survivorTarget.setTargetOfEnemy(false);
                    followingSurvivor = false;
//                    survivorTarget.coolDown(false);
                }
//                enemy.setShrinkVines(true);
                enemy.setAttack(false);
                enemy.setShrinkVines(true);
                clearContainsVine();
            }
        }
        prevAction = action;
        return action;
    }

    protected int getMoveVine() {
//        selectTarget();
        if (enemy.vines.isEmpty()) {
            clearContainsVine();
            startTile = tiles[(int) (enemy.getX() / tileSize)][(int) (enemy.getY() / tileSize)];
        } else {
            startTile = tiles[(int) ((enemy.vines.get(enemy.vines.size - 1).getX() / tileSize))][(int) (enemy.vines.get(enemy.vines.size - 1).getY() / tileSize)];
        }
        goalTile = tiles[(int) (target.x / tileSize)][(int) (target.y / tileSize)];

        vineTilePath = vineTileGraph.findPath(startTile, goalTile);
        Tile nextTile;
        if (vineTilePath.getCount() > 1) {
            nextTile = vineTilePath.get(1);
        } else if (vineTilePath.getCount() == 1) {
            nextTile = vineTilePath.get(0);
        } else {
            clearContainsVine();
            return 0;
        }

        goalLoc = setGoal(nextTile);

        int action = 0;
        if (enemy.vines.isEmpty()) {
            if ((int) goalLoc.x > (int) enemy.getX()) {
                // right
                action = 1;
            } else if ((int) goalLoc.x < (int) enemy.getX()) {
                // left
                action = 2;
            } else if ((int) goalLoc.y > (int) enemy.getY()) {
                // up
                action = 3;
            } else if ((int) goalLoc.y < (int) enemy.getY()) {
                // down
                action = 4;
            }
        } else {
            if ((int) (goalLoc.x / tileSize) > (int) ((enemy.vines.get(enemy.vines.size - 1).getX() / tileSize))) {
                // right
                action = 1;
            } else if ((int) (goalLoc.x / tileSize) < (int) ((enemy.vines.get(enemy.vines.size - 1).getX() / tileSize))) {
                // left
                action = 2;
            } else if ((int) (goalLoc.y / tileSize) > (int) ((enemy.vines.get(enemy.vines.size - 1).getY() / tileSize))) {
                // up
                action = 3;
            } else if ((int) (goalLoc.y / tileSize) < (int) ((enemy.vines.get(enemy.vines.size - 1).getY() / tileSize))) {
                // down
                action = 4;
            }
        }
//        System.out.println("size: " + enemy.vines.size);
//        System.out.println("next move: " + action);
//        System.out.println("can't add vine?: " + containsVine[(int) nextTile.getX()][(int) nextTile.getY()]);
        if (!containsVine[(int) nextTile.getX()][(int) nextTile.getY()]) {
            enemy.addVineTile(nextTile.getX() * tileSize + (tileSize / 2), nextTile.getY() * tileSize + (tileSize / 2), action);
            containsVine[(int) nextTile.getX()][(int) nextTile.getY()] = true;
        }
        else {
            enemy.setShrinkVines(true);
        }
        return action;
    }
}