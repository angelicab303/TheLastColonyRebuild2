package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameCanvas;
import util.FilmStrip;

public class AirBar {
    /*
        UI Class representing smog absorption progress.
     */
    /** X position on screen */
    private float xPos;
    /** Y position on screen */
    private float yPos;
    /** Filmstrip for bar */
    protected FilmStrip animator;
    /** How fast we change frames (one frame per 10 calls to update) */
    private static final float ANIMATION_SPEED = 0.02f;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 50;
    /** Current animation frame for this shell */
    private float aframe;
    /** Camera zoom factor */
    private float cameraZoom = 0.4f;
    /** Max ammo capacity */
    private float maxAmmo;
    /** Current ammo capacity */
    private float currentAmmo;


    public AirBar(Texture value, float maxAmmo, float currAmmo){
//        this.xPos = x;
//        this.yPos = y;

        animator = new FilmStrip(value,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
        aframe = 0.0f;

        this.maxAmmo = maxAmmo;
        this.currentAmmo = currAmmo;
    }

    /**
     *  Updates the progression of the smog bar depending on how much smog was collected
     *  by the player.
     */
    public void update(float progress) {
        // Update amount of progress
        //System.out.println(progress);
        aframe = progress/(maxAmmo/NUM_ANIM_FRAMES);
        if (aframe == 50){
            aframe = 49;
        }


    }

    public void draw(GameCanvas canvas) {
        int frame = (int)aframe;
        if (frame < 0) {
            frame = 0;
        }
        animator.setFrame(frame);
        // System.out.println((int)aframe);
        xPos = canvas.camera.position.x - (canvas.getWidth()*cameraZoom)/2.0f + (30.0f * cameraZoom);
        yPos = canvas.camera.position.y + (canvas.getHeight()*cameraZoom)/2.0f - (30.0f * cameraZoom);
        canvas.draw(animator, new Color(255, 255, 255, 0.75f), 0.0f, animator.getRegionHeight(), xPos, yPos, 0.0f, 0.25f, 0.25f) ;
//        xPos = canvas.camera.position.x - (canvas.getWidth()*cameraZoom)/2.0f + (30.0f * cameraZoom);
//        yPos = canvas.camera.position.y + (canvas.getHeight()*cameraZoom)/2.0f - (30.0f * cameraZoom);
    }
}
