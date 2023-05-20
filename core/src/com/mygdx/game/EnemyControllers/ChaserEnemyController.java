package com.mygdx.game.EnemyControllers;

import com.badlogic.gdx.math.Vector2;
//import com.mygdx.game.EnemyController;

import com.mygdx.game.Obstacles.Enemies.ChaserEnemy;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.Obstacles.Player;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.Obstacles.Player;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;
import com.mygdx.game.Obstacles.Survivor;


public class ChaserEnemyController extends com.mygdx.game.EnemyControllers.EnemyController {
    /** Enum to encode the finite state machine */
    /**
     * Initialize an EnemyController
     *
     * @param board  representation of objects in the level
     * @param enemy  the enemy controlled by this controller
     * @param player the player that will be the enemy's target
     */
    Survivor survivorTarget;

    boolean followingSurvivor;

    boolean startedChasing;

    boolean startedAttacking;


    public ChaserEnemyController(boolean[][] board, int tileSize, int tileOffset, ChaserEnemy enemy, Player player) {
        super(board, tileSize, tileOffset, enemy, player);
        target = new Vector2(player.getX(), player.getY());
        super.initTiles(target);
        followingSurvivor = false;
        survivorTarget = null;
    }

    private void selectTarget() {
        target.x = player.getX();
        target.y = player.getY();
        if (!player.getSurvivorsFollowing().isEmpty()) {
            for (int i = 0; i < player.getSurvivorsFollowing().size; i++) {
                if (!player.getSurvivorsFollowing().get(i).isTargetOfEnemy()) {
                    target.x = player.getSurvivorsFollowing().get(i).getX();
                    target.y = player.getSurvivorsFollowing().get(i).getY();
                    survivorTarget = player.getSurvivorsFollowing().get(i);
                    followingSurvivor = true;
                    player.getSurvivorsFollowing().get(i).setTargetOfEnemy(true);
                }
            }
        }
        else {
            target.x = player.getX();
            target.y = player.getY();
        }
    }

    public boolean startedChasing() { return startedChasing; }

    public boolean startedAttacking() { return startedAttacking; }

    /**
     * Returns an int value representing the enemy's next movement action:
     * 0 = no move, 1 = right, 2 = left, 3 = up, 4 = down,
     * 5 = up + right, 6 = down + right, 7 = up + left, 8 = down + left
     */
    public int getAction() {
        if (state == FSMState.CHASE) {
            selectTarget();
        }
        ticks++;
        moveTime++;
        startedChasing = false;
        startedAttacking = false;

        if (ticks % 10 == 0)
        {
            changeStateIfApplicable();
        }

        int action = 0;
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
        return action;
    }

    protected void changeStateIfApplicable() {
//        float dist = followingSurvivor ? Vector2.dst(survivorTarget.getX(), survivorTarget.getY(), enemy.getX(), enemy.getY()) :
//        Vector2.dst(player.getX(), player.getY(), enemy.getX(), enemy.getY());
        selectTarget();
        float dist = Vector2.dst(target.x, target.y, enemy.getX(), enemy.getY());
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
                if (enemy.isStunned())
                {
                    state = FSMState.STUNNED;
                    enemy.setHasAwoken(true);
                }
                else if (enemy.isRevealed() && enemy.canAttack())
                {
                    state = FSMState.WAKE;
                    enemy.setWaking(true);
                    enemy.setHasAwoken(true);
//                    if (enemy.canAttack())
//                    {
//                        state = FSMState.ATTACK;
//                    }
                }
                else if (alertAllEnemies)
                {
                    Vector2 enemyLoc = new Vector2(enemy.getX(), enemy.getY());
                    Vector2 shriekerLoc = new Vector2(activeShrieker.getX(), activeShrieker.getY());
                    if (enemyLoc.dst(shriekerLoc) <= ALERT_DISTANCE){
                        state = FSMState.WAKE;
                        enemy.setWaking(true);
                        enemy.setHasAwoken(true);
                        target = new Vector2 (player.getX(), player.getY());
                    }
                }
                break;
            case WAKE:
                if (enemy.getHasAwoken()){
                    state = FSMState.IDLE;
                }
                break;
            case IDLE:
                if (enemy.isStunned())
                {
                    state = FSMState.STUNNED;
                }
                else if (enemy.isRevealed() && enemy.canAttack())
                {
                    startedChasing = true;
                    state = FSMState.CHASE;
//                    if (enemy.canAttack())
//                    {
//                        state = FSMState.ATTACK;
//                    }
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
                else if (!enemy.canAttack())
                {
                    state = FSMState.IDLE;
                }
                else if (dist < tileSize)
                {
                    startedAttacking = true;
                    state = FSMState.ATTACK;
                    enemy.setAttacking(true);
                }
                break;
            case ATTACK:
                if (enemy.canAttack()) {
                    if (!followingSurvivor && player.canLoseLife()) {
                        player.setHealth(player.getHealth() - 1);
                        player.coolDown(false);
                    }
                    else {
                        if (followingSurvivor && survivorTarget.canLoseLife()) {
                            survivorTarget.loseLife();
//                            survivorTarget.setTargetOfEnemy(false);
//                            followingSurvivor = false;
                            survivorTarget.setTargetOfEnemy(false);
                            followingSurvivor = false;
                        }
//                        survivorTarget.setTargetOfEnemy(false);
//                        followingSurvivor = false;
//                    survivorTarget.coolDown(false);
                    }
                    enemy.setAttack(false);
                }
                state = FSMState.IDLE;
                enemy.setAttacking(false);
                break;
            default:
                super.changeStateIfApplicable();
                break;
        }
    }
}
