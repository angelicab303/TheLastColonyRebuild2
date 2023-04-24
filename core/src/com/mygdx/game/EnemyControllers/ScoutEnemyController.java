package com.mygdx.game.EnemyControllers;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.Obstacles.Player;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;

public class ScoutEnemyController extends com.mygdx.game.EnemyControllers.EnemyController {
    private static enum FSMState {
        /** The enemy just spawned and is stationary */
        SPAWN,
        /** The enemy is idle and stationary, either not aware of the player or on cooldown */
        IDLE,
        /** The enemy is wandering but is not close enough to the player to prompt attack */
        WANDER,
        /** The enemy has spotted the player and is chasing them */
        CHASE,
        /** The enemy is attacking the player */
        ATTACK,
        /** The enemy has been stunned by the player */
        STUNNED
    }
    public ScoutEnemyController(boolean[][] board, int tileSize, int tileOffset, Enemy enemy, Player player, Array<ShriekerEnemy> shriekArr) {
        super(board, tileSize, tileOffset, enemy, player, shriekArr);
    }
}
