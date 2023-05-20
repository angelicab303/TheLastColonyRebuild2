package com.mygdx.game.Obstacles.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.GameCanvas;
import util.FilmStrip;

public class FloatingEnemy extends Enemy {

    /** How far forward the enemy can move */
    private static final float MOVE_SPEED = 50.0f;
    /** Time enemy must wait before attacking again */
    private final float ATTACK_COOLDOWN = 100;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 7;
    /** How fast we change frames (one frame per 10 calls to update) */
    protected static final float ANIMATION_SPEED = 0.20f;
    protected static float WAKE_ANIMATION_SPEED = 0.20f;
//    private float aframe;


    public FloatingEnemy (float x, float y, FilmStrip[][] animator, float scale, float tileSize)
    {
        super(x, y, animator, scale, tileSize, false);
        isWakable = true;
        aframe = 0;
    }

    @Override
    protected float getAttackCooldown() {
        return ATTACK_COOLDOWN;
    }

    @Override
    protected float getMoveSpeed() {
        return MOVE_SPEED;
    }
    public void setAframe(int aframe){
        this.aframe = aframe;
    }
    public void setWakeAnimationSpeed(int time){
        WAKE_ANIMATION_SPEED = time;
    }

    /**
     * Updates the positions of enemies based on their chosen action.
     * Also updates the stun time of stunned enemies and the attack time of enemies on cooldown.
     * @param action the action that this enemy will take
     */
    @Override
    public void update(int action) {

        if (isWaking){
            if (wakeTime <= 1){
                aframe = 0;
            }
            if (wakeTime >= 100 && !wokeOnce){
                if (wakeTime > 0){
                    aframe += WAKE_ANIMATION_SPEED;
                }
            }
            else if (wokeOnce){
                WAKE_ANIMATION_SPEED = 0.5f;
                aframe += WAKE_ANIMATION_SPEED;
            }
            if (aframe >= NUM_ANIM_FRAMES){
                hasAwoken = true;
                aframe = 0;
            }
        }
        else if (isStunned()){
            if (stunTime <= 1){
                aframe = 0;
            }
//            if (stunTime >= MAX_STUN_TIME-2){
//                aframe = 0;
//            }
            if (aframe >= NUM_ANIM_FRAMES){
                aframe = NUM_ANIM_FRAMES-1;
            }
            else{
                aframe += ANIMATION_SPEED;
            }
        }
        else{
            aframe += ANIMATION_SPEED;
            if (aframe >= NUM_ANIM_FRAMES) {
                aframe -= NUM_ANIM_FRAMES;
            }
        }


        calculateMovement(action);
        super.update(action);
    }
    /**
     * Draws the enemy object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        super.draw(canvas, width*scale, height*scale);
        if (!hasAwoken && !wokeOnce && wakeTime <= 20){
            //System.out.println("Frame Skull");
            currentAnimator.setFrame(0);
        }
        else{
            System.out.println("aframe: " + aframe);
            currentAnimator.setFrame((int)aframe);
        }
//        System.out.println((body.getWorldCenter().x*drawScale.x - currentAnimator.getRegionWidth()*scale/2) + ", " + (body.getWorldCenter().y*drawScale.y- currentAnimator.getRegionHeight()*scale/2));
        if (stunCooldown > 0 && stunCooldown % 10 == 0)
        {
            canvas.draw(currentAnimator, Color.CLEAR, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - width*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, 0.0f, scale, scale);
        }
        else if (isStunned())
        {
            canvas.draw(currentAnimator, Color.PINK, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - width*scale/2, body.getWorldCenter().y*drawScale.y- height*scale/2, 0.0f, scale, scale);
            if(stunAnimation != null){
//                System.out.println(stunTime % 4);
                stunAnimation.setFrame(((int)stunTime/5) % 4); //4 frames of animation, 5 frame rate reduction
                canvas.draw(stunAnimation, Color.GRAY, origin.x, origin.y, body.getWorldCenter().x * drawScale.x - width*scale/2, body.getWorldCenter().y * drawScale.y, 0.0f, scale, scale);
            }
        }
        else {
            canvas.draw(currentAnimator, Color.WHITE, origin.x, origin.y, body.getWorldCenter().x * drawScale.x - width * scale / 2, body.getWorldCenter().y * drawScale.y - height * scale / 2, 0.0f, scale, scale);
        }
    }

    public Enemy.EnemyType getEnemyType() {
        return EnemyType.FLOATING;
    }
}
