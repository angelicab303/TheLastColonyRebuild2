package com.mygdx.game.EnemyControllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.Obstacles.Player;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;
import com.mygdx.game.Obstacles.Survivor;

public class ShriekerEnemyController extends EnemyController{
    /** Enum to encode the finite state machine */
    private static enum FSMState {
        /** The enemy just spawned and is stationary */
        SPAWN,
        /** The enemy is idle and stationary, either not aware of the player or on cooldown */
        IDLE,
        /** The enemy is waking up and preparing to shriek */
        WAKE,
        /** The enemy has fully woken up and is shrieking to alert other enemies*/
        SHRIEK,
        /** The enemy has been stunned by the player */
        STUNNED
    }
    /** The enemy's current state */
    protected ShriekerEnemyController.FSMState state;
    /** The shrieker controlled by this EnemyController */
    private ShriekerEnemy enemy;
    /** The distance the player must be within to trigger the shrieker */
    private final float WAKE_DISTANCE = 100.0f;
    /** Minimum distance the player must be at order for the shrieker to re-enter IDLE during WAKE */
    private final float MIN_DISTANCE = 200.0f;
    /**
     * Initialize an EnemyController
     *
     * @param board  representation of objects in the level
     * @param enemy  the enemy controlled by this controller
     * @param player the player that will be the enemy's target
     */
    public ShriekerEnemyController(boolean[][] board, int tileSize, int tileOffset, ShriekerEnemy enemy, Player player, Array<ShriekerEnemy> shriekArr) {
        super(board, tileSize, tileOffset, enemy, player, shriekArr);
        target = new Vector2(player.getX(), player.getY());
        state = FSMState.SPAWN;
        this.enemy = enemy;
        super.initTiles(target);
    }


    /** Returns an int value representing the enemy's next movement action:
     * 0 = no move, 1 = right, 2 = left, 3 = up, 4 = down,
     * 5 = up + right, 6 = down + right, 7 = up + left, 8 = down + left */
    public int getAction()
    {
        target.set(player.getX(), player.getY());
        ticks++;
        moveTime++;

        if (ticks % 10 == 0)
        {
            changeStateIfApplicable();
        }

        int action = 0;
        return action;
    }

    /** Changes the state encoding of this enemy */
    protected void changeStateIfApplicable()
    {

        switch(state) {
            case SPAWN:
                state = ShriekerEnemyController.FSMState.IDLE;
                break;
            case IDLE:
                boolean playerNear = false;
                Vector2 playerLoc = new Vector2(target.x, target.y);
                Vector2 shriekerLoc = new Vector2(enemy.getX(), enemy.getY());
                if (shriekerLoc.dst(playerLoc) <= WAKE_DISTANCE){
                    playerNear = true;
                }
                if (enemy.isStunned())
                {
                    state = ShriekerEnemyController.FSMState.STUNNED;
                }
                else if (enemy.isRevealed() && enemy.canWake() && playerNear)
                {
                    state = ShriekerEnemyController.FSMState.WAKE;
                    enemy.setWaking(true);
                }

                break;
            case WAKE:
                boolean playerFar = false;
                playerLoc = new Vector2(target.x, target.y);
                shriekerLoc = new Vector2(enemy.getX(), enemy.getY());
                if (shriekerLoc.dst(playerLoc) >= MIN_DISTANCE){
                    playerFar = true;
                    enemy.setWakeTime(0);
                }
                if (enemy.isStunned())
                {
                    state = ShriekerEnemyController.FSMState.STUNNED;
                }
                else if (enemy.canShriek())
                {
                    state = ShriekerEnemyController.FSMState.SHRIEK;
                    enemy.setShrieking(true);
                    enemy.setWaking(false);
                }
                else if (playerFar)
                {
                    enemy.setWaking(false);
                    state = ShriekerEnemyController.FSMState.IDLE;
                }
                break;
            case SHRIEK:
                if (enemy.isStunned())
                {
                    state = ShriekerEnemyController.FSMState.STUNNED;
                    enemy.setShrieking(false);
                }
                else if (!enemy.canShriek()){
                    state = ShriekerEnemyController.FSMState.IDLE;
                    enemy.setShrieking(false);

                }
                break;
            case STUNNED:
                if (!enemy.isStunned())
                {
                    state = ShriekerEnemyController.FSMState.IDLE;
                }
                break;
        }
    }
}


