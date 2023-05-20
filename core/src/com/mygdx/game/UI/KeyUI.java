package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.GameCanvas;
import util.FilmStrip;

public class KeyUI {
    protected BitmapFont displayFont;
    private float xPos;
    /** Y position on screen */
    private float yPos;
    /** Filmstrip for bar */
    protected TextureRegion texture;
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

    private int count;

    public KeyUI(TextureRegion texture, int count, BitmapFont font) {
        this.texture = texture;
        this.count = count;
        displayFont = font;
    }

    public void draw(GameCanvas canvas) {
        // animator.setFrame((int)aframe);
        String message = count + "x";
        xPos = canvas.camera.position.x - (canvas.camera.viewportWidth*cameraZoom)/2.0f + (330.0f * cameraZoom);
        yPos = canvas.camera.position.y + ((canvas.camera.viewportHeight*cameraZoom)/2.0f) - (105.0f * cameraZoom);
        //yPos = canvas.camera.position.y + (canvas.camera.viewportHeight*cameraZoom)*0.8f/2.0f - (15.0f * cameraZoom) - texture.getRegionHeight()*scale;
        canvas.draw(texture, new Color(255, 255, 255, 0.75f), texture.getRegionWidth()*scale, texture.getRegionHeight()*scale/2, xPos, yPos, 0.0f, scale * 0.9f, scale * 0.9f);
        canvas.drawText(message, displayFont, xPos + (55.0f * cameraZoom), yPos + (17f * cameraZoom));
//        xPos = canvas.camera.position.x + (canvas.getWidth()*cameraZoom)/2.0f - (90.0f * cameraZoom) - spacing;
//        yPos = canvas.camera.position.y + (canvas.getHeight()*cameraZoom)/2.0f - (40.0f * cameraZoom) - texture.getHeight()*scale;
    }

    public void update(int newCount) {
        count = newCount;
    }
}
