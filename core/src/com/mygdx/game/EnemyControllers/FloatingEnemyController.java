package com.mygdx.game.EnemyControllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Obstacles.Enemies.FloatingEnemy;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;
import com.mygdx.game.Obstacles.Player;
import com.mygdx.game.Obstacles.Survivor;
import com.mygdx.game.Obstacles.ToxicQueue;
import com.mygdx.game.Tile;

public class FloatingEnemyController extends EnemyController {
    Survivor survivorTarget;

    boolean shootingSurvivor;

    private ToxicQueue toxicQueue;

    final Vector2 angle = new Vector2();

    public FloatingEnemyController(boolean[][] board, int tileSize, int tileOffset, FloatingEnemy enemy, Player player, ToxicQueue toxicQueue) {
        super(board, tileSize, tileOffset, enemy, player);
        this.toxicQueue = toxicQueue;
        target = new Vector2(player.getX(), player.getY());
        super.initTiles(target);
        shootingSurvivor = false;
        survivorTarget = null;
    }

    @Override
    public int getAction()
    {
        selectTarget();
//        target = new Vector2(player.getX(), player.getY());
        return super.getAction();
    }

    private void selectTarget() {
        target.x = player.getX();
        target.y = player.getY();
//        if (!player.getSurvivorsFollowing().isEmpty()) {
//            for (int i = 0; i < player.getSurvivorsFollowing().size; i++) {
//                if (!player.getSurvivorsFollowing().get(i).isTargetOfEnemy()) {
//                    target.x = player.getSurvivorsFollowing().get(i).getX();
//                    target.y = player.getSurvivorsFollowing().get(i).getY();
//                    survivorTarget = player.getSurvivorsFollowing().get(i);
//                    shootingSurvivor = true;
//                    player.getSurvivorsFollowing().get(i).setTargetOfEnemy(true);
//                }
//            }
//        }
    }

    @Override
    protected void changeStateIfApplicable()
    {
        Tile enemyTile = tiles[(int) (enemy.getX() / tileSize)][(int) (enemy.getY() / tileSize)];
        Tile targetTile = tiles[(int) (target.x / tileSize)][(int) (target.y / tileSize)];
        Vector2 enemyPos = new Vector2(enemy.getX(), enemy.getY());
        switch(state) {
            case SPAWN:
                state = FSMState.IDLE;
                break;
            case IDLE:
                if (enemy.isStunned())
                {
                    state = FSMState.STUNNED;
                }
                else if (enemy.isRevealed())
                {
                    state = FSMState.CHASE;
                    if (enemy.canAttack())
                    {
                        state = FSMState.ATTACK;
                    }
                }
                break;
            case CHASE:
                if (enemy.isStunned())
                {
                    state = FSMState.STUNNED;
                }
                else if (enemy.canAttack())
                {
                    state = FSMState.ATTACK;
                }
                break;
            case ATTACK:
                angle.set(target.cpy().sub(enemyPos));
                Vector2 attackPos = angle.cpy().scl(2);
                toxicQueue.attack(1, enemy.getBody().getWorldCenter(), attackPos);
                enemy.setAttack(false);
                state = FSMState.CHASE;
                break;
            case STUNNED:
                if (!enemy.isStunned())
                {
                    if (enemy.canAttack()) {
                        state = FSMState.ATTACK;
                    }
                    else {
                        state = FSMState.CHASE;
                    }
                }
                break;
        }
    }
    
}
