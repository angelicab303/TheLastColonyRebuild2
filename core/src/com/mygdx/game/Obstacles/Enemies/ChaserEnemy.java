package com.mygdx.game.Obstacles.Enemies;

import util.FilmStrip;

public class ChaserEnemy extends Enemy {
    private static final float MOVE_SPEED = 70.0f;
    /** Time enemy must wait before attacking again */
    private final float ATTACK_COOLDOWN = 100;

    public ChaserEnemy (float x, float y, FilmStrip[][] animator, float scale, float tileSize)
    {
        super(x, y, animator, scale, tileSize, false);
        isWakable = true;
    }

    @Override
    protected float getAttackCooldown() {
        return ATTACK_COOLDOWN;
    }

    @Override
    protected float getMoveSpeed() {
        return MOVE_SPEED;
    }

    /**
     * Updates the positions of enemies based on their chosen action.
     * Also updates the stun time of stunned enemies and the attack time of enemies on cooldown.
     * @param action the action that this enemy will take
     */
    @Override
    public void update(int action) {
        calculateMovement(action);
        super.update(action);
    }

    public Enemy.EnemyType getEnemyType() {
        return EnemyType.FLOATING;
    }
}
