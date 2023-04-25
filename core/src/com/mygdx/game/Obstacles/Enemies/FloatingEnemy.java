package com.mygdx.game.Obstacles.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.GameCanvas;
import util.FilmStrip;

public class FloatingEnemy extends Enemy {

    /** How far forward the enemy can move */
    private static final float MOVE_SPEED = 70.0f;
    /** Time enemy must wait before attacking again */
    private final float ATTACK_COOLDOWN = 200;

    public FloatingEnemy (float x, float y, Texture up, Texture down, Texture right, Texture left, Texture idle, float scale)
    {
        super(x, y, up, down, right, left, idle, scale);
    }

    /**
     * Updates the positions of enemies based on their chosen action.
     * Also updates the stun time of stunned enemies and the attack time of enemies on cooldown.
     * @param action the action that this enemy will take
     */
    @Override
    public void update(int action)
    {
        if(toStunTime >= MAX_TO_STUN_TIME){
            toStunTime = 0;
            this.setStunned(true);
        }
        body.setAwake(true);
        if (isStunned())
        {
            body.setActive(false);
            canAttack = false;
            stunTime++;
            if (stunTime >= MAX_STUN_TIME)
            {
                canAttack = true;
                stunned = false;
                stunTime = 0;
                body.setActive(true);
            }
        }
        if (!canAttack())
        {
            attackTime++;
            if (attackTime >= ATTACK_COOLDOWN && !isStunned())
            {
                canAttack = true;
                attackTime = 0;
            }
        }

        // Determine how we are moving.
        float hVelocity = 0;
        float vVelocity = 0;
        if (action == 1)
        {
            hVelocity = 1;
        }
        else if (action == 2)
        {
            hVelocity = -1;
        }
        else if (action == 3)
        {
            vVelocity = 1;
        }
        else if (action == 4)
        {
            vVelocity = -1;
        }
        else if (action == 5)
        {
            hVelocity = 1;
            vVelocity = 1;
        }
        else if (action == 6)
        {
            hVelocity = 1;
            vVelocity = -1;
        }
        else if (action == 7)
        {
            hVelocity = -1;
            vVelocity = 1;
        }
        else if (action == 8)
        {
            hVelocity = -1;
            vVelocity = -1;
        }

        velocity.x = hVelocity * MOVE_SPEED;
        velocity.y = vVelocity * MOVE_SPEED;
        setVX(velocity.x);
        setVY(velocity.y);

        if (!velocity.equals(zerovector)) {
            lastVelocity  = velocity.cpy();
        }
//        position.add(velocity);
//        setPosition(position);
        body.setLinearVelocity(velocity);
        body.applyLinearImpulse(velocity, body.getWorldCenter(), true);
        setX(body.getWorldCenter().x);
        setY(body.getWorldCenter().y);

        // Set enemy texture based on direction from movement
        if (!isStunned()) {
            updateDirection(hVelocity, vVelocity);
        }

        // Increase animation frame
        aframe += ANIMATION_SPEED;

        if (aframe >= NUM_ANIM_FRAMES) {
            aframe -= NUM_ANIM_FRAMES;
        }
    }

    /**
     * Draws the enemy object.
     *
     * @param canvas Drawing context
     */
    @Override
    public void draw(GameCanvas canvas) {
        currentAnimator.setFrame((int)getAframe());
        canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - currentAnimator.getRegionWidth()*getScale()/2, body.getWorldCenter().y*drawScale.y- currentAnimator.getRegionHeight()*getScale()/2, 0.0f, getScale(), getScale());
    }
}
