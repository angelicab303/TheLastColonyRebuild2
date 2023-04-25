package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.GameCanvas;
import util.FilmStrip;

public class Heart {
    /*
        UI Class representing player lives
     */
    /** X position on screen */
    private float xPos;
    /** Y position on screen */
    private float yPos;
    /** Filmstrip for bar */
    protected Texture texture;
    protected FilmStrip animator;
    /** How fast we change frames (one frame per 10 calls to update) */
    private static final float ANIMATION_SPEED = 0.02f;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 1;
    /** Current animation frame for this shell */
    private float aframe;
    /** Camera zoom factor */
    private float cameraZoom = 0.4f;
    private float scale = 0.1f;
    /** Spacing between hearts */
    private float spacing;


    public Heart(Texture value, float x, float y, float spacing){
        texture = value;
        this.spacing = spacing;
//        xPos = x;
//        yPos = y;

//        animator = new FilmStrip(value,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
//        aframe = 0.0f;

    }

    /**
     * Changes the texture of the heart sprite
     * @param value the texture to be set
     */
    public void setTexture(Texture value){
        texture = value;
    }

    /**
     *  Updates the progression of the smog bar depending on how much smog was collected
     *  by the player.
     */
    public void update() {

        // Increase animation frame
//        aframe += ANIMATION_SPEED;
//
//        if (aframe >= NUM_ANIM_FRAMES) {
//            aframe -= NUM_ANIM_FRAMES;
//        }
    }

    /**
     * Draws the heart
     * @param canvas
     */
    public void draw(GameCanvas canvas) {
        // animator.setFrame((int)aframe);
        xPos = canvas.camera.position.x - (canvas.getWidth()*cameraZoom)/2.0f + (30.0f * cameraZoom) + spacing;
        yPos = canvas.camera.position.y + (canvas.getHeight()*cameraZoom)*0.8f/2.0f - (40.0f * cameraZoom) - texture.getHeight()*scale;
        canvas.draw(texture, new Color(255, 255, 255, 0.75f), texture.getWidth()*scale, texture.getHeight()*scale/2, xPos, yPos, 0.0f, scale, scale);
//        xPos = canvas.camera.position.x + (canvas.getWidth()*cameraZoom)/2.0f - (90.0f * cameraZoom) - spacing;
//        yPos = canvas.camera.position.y + (canvas.getHeight()*cameraZoom)/2.0f - (40.0f * cameraZoom) - texture.getHeight()*scale;
    }

}
