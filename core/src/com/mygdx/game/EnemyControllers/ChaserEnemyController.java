package com.mygdx.game.EnemyControllers;

import com.badlogic.gdx.math.Vector2;
//import com.mygdx.game.EnemyController;

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

    public ChaserEnemyController(boolean[][] board, int tileSize, int tileOffset, Enemy enemy, Player player, Array<ShriekerEnemy> shriekArr) {
        super(board, tileSize, tileOffset, enemy, player, shriekArr);
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
    }

    /**
     * Returns an int value representing the enemy's next movement action:
     * 0 = no move, 1 = right, 2 = left, 3 = up, 4 = down,
     * 5 = up + right, 6 = down + right, 7 = up + left, 8 = down + left
     */
    public int getAction() {
        selectTarget();
        return super.getAction();

    }

    protected void changeStateIfApplicable() {
        float dist = followingSurvivor ? Vector2.dst(survivorTarget.getX(), survivorTarget.getY(), enemy.getX(), enemy.getY()) :
        Vector2.dst(player.getX(), player.getY(), enemy.getX(), enemy.getY());
        switch (state) {
            case CHASE:
                if (enemy.isStunned())
                {
                    state = FSMState.STUNNED;
                }
                else if (!enemy.isRevealed() || !enemy.canAttack())
                {
                    state = FSMState.IDLE;
                }
                else if (dist < tileSize)
                {
                    state = FSMState.ATTACK;
                }
                break;
            case ATTACK:
                if (enemy.canAttack()) {
                    if (!followingSurvivor) {
                        player.setHealth(player.getHealth() - 1);
                        player.coolDown(false);
                    }
                    else {
                        if (survivorTarget.canLoseLife()) {
                            survivorTarget.loseLife();
                            survivorTarget.setTargetOfEnemy(false);
                            followingSurvivor = false;
                        }
//                    survivorTarget.coolDown(false);
                    }
                    enemy.setAttack(false);
                }
                state = FSMState.IDLE;
                break;
            default:
                super.changeStateIfApplicable();
                break;
        }
    }
}
