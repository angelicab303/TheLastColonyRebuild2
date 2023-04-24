/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do
 * anything until loading is complete. You know those loading screens with the inane tips
 * that want to be helpful?  That is asynchronous loading.
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package com.mygdx.game;

import assets.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import obstacle.Obstacle;
import util.Controllers;
import util.ScreenListener;
import util.XBoxController;

/**
 * Class that provides a main menu screen to start the game.
 */
public class MainMenuMode implements Screen, InputProcessor, ControllerListener {
    class Cloud {
        /** Texture to be used for the cloud */
        private Texture cloudTexture;
        /** x position of the cloud on screen */
        private float x;
        /** y position of the cloud on screen */
        private float y;
        /** Movement rate of cloud*/
        private float rate;
        /** Scaling of clouds */
        private float cloudScale;
        /** How much the cloud has moved from origin */
        private float movement;
        /** If the cloud is currently moving right */
        private boolean isMovingRight;
        /** How fast a large cloud moves across screen */
        private final float largeSpeed = 0.08f;
        /** How fast a medium cloud moves across screen */
        private final float mediumSpeed = 0.08f;
        /** How fast a small cloud moves across screen */
        private final float smallSpeed = 0.08f;
        /** Maximum distance for clouds to move right before moving back left */
        private float maxRight;

        public Cloud(Texture ctexture, float x, float y, float rate, float maxRight, float cScale){
            this.cloudTexture = ctexture;
            this.x = x;
            this.y = y;
            this.rate = rate;
            this.cloudScale = cScale;
            this.maxRight = maxRight;
            isMovingRight = true;
            movement = 0;
        }

        /** Updates the positioning of the clouds to move across screen */
        private void update(){
            // Move according to desired direction
            if (isMovingRight){
                movement += rate;
            }
            else {
                movement -= rate;
            }
            x += rate;

            // Check if reached maximum movement direction
            if (movement >= maxRight){
                isMovingRight = false;
                movement = 0;
                rate = -rate;
            }
            else if (movement <= -30){
                isMovingRight = true;
                movement = 0;
                rate = -rate;
            }
        }

        private void draw(GameCanvas canvas){
            canvas.draw(cloudTexture, Color.WHITE, cloudTexture.getWidth()/2, cloudTexture.getHeight()/2, x, y, 0, cloudScale, cloudScale);
        }

    }
    /** The assets to be loaded */
    private AssetDirectory assets;
    /** Background texture for start-up */
    private Texture background;
    /** Texture for play option */
    private Texture play;
    /** Texture for levels option */
    private Texture levels;
    /** Texture for settings option */
    private Texture settings;
    /** Texture for exit option */
    private Texture exit;
    /** Texture for small cloud */
    private Texture smallCloud;
    /** Texture for medium cloud */
    private Texture mediumCloud;
    /** Texture for large cloud */
    private Texture largeCloud;
    /** Texture for cursor*/
    private Texture cursor;
    /** Texture for title */
    private Texture title;
    /** Default budget for asset loader (do nothing but load 60 fps) */
    private static int DEFAULT_BUDGET = 15;
    /** Standard window size (for scaling) */
    private static int STANDARD_WIDTH  = 800;
    /** Standard window height (for scaling) */
    private static int STANDARD_HEIGHT = 700;

    /** Reference to GameCanvas created by the root */
    private GameCanvas canvas;
    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;
    /** Scaling factor for when the student changes the resolution. */
    private float scale;
    /** Scaling factor for the text. */
    private float textScale = 0.8f;
    /** The current state of the play button */
    private int   pressState;
    /** Whether or not this player mode is still active */
    private boolean active;
    /** Spacing for text from right of screen */
    private final float RIGHT_SPACING = 30.0f;
    /** Spacing for text from top of screen */
    private final float TOP_SPACING = 50.0f;
    /** Spacing for play from title */
    private final float TITLE_SPACING = 20.0f;
    /** Spacing for between options */
    private final float OPTION_SPACING = 10.0f;
    private float PlAY_Y;
    private float LEVELS_Y;
    private float SETTINGS_Y;
    private float _Y;
    private Array<Cloud> clouds;
    private final int NUM_CLOUDS = 5;
    private float centerX;
    private float centerY;


    /**
     * Returns the asset directory produced by this loading screen
     *
     * This asset loader is NOT owned by this loading scene, so it persists even
     * after the scene is disposed.  It is your responsbility to unload the
     * assets in this directory.
     *
     * @return the asset directory produced by this loading screen
     */
    public AssetDirectory getAssets() {
        return assets;
    }
    /**
     * Sets the canvas associated with this controller
     *
     * The canvas is shared across all controllers.  Setting this value will compute
     * the drawing scale from the canvas size.
     *
     * @param canvas the canvas associated with this controller
     */
    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
//        this.scale.x = canvas.getWidth(); // /bounds.getWidth();
//        this.scale.y = canvas.getHeight(); // /bounds.getHeight();
    }

    /**
     * Creates a Main Menu with the default size and position.
     *
     * @param canvas 	The game canvas to draw to
     */
    public MainMenuMode(GameCanvas canvas) {
        this.canvas  = canvas;

        // Compute the dimensions from the canvas
        resize(canvas.getWidth(),canvas.getHeight());

        pressState = 0;

        clouds = new Array<Cloud>();
        pressState = 0;

        Gdx.input.setInputProcessor( this );
    }

    /**
     * Gather the assets for the main menu.
     *
     * This method extracts the asset variables from the given asset directory. It
     * should only be called after the asset directory is completed.
     *
     * @param directory	Reference to global asset manager.
     */
    public void gatherAssets(AssetDirectory directory) {
        // Allocate the main menu assets
        background = directory.getEntry("mainMenu:background", Texture.class);
        title = directory.getEntry("mainMenu:title", Texture.class);
        play = directory.getEntry("mainMenu:play", Texture.class);
        levels = directory.getEntry("mainMenu:levels", Texture.class);
        settings = directory.getEntry("mainMenu:settings", Texture.class);
        cursor = directory.getEntry("mainMenu:cursor", Texture.class);
        exit = directory.getEntry("mainMenu:exit", Texture.class);
        largeCloud = directory.getEntry("mainMenu:largeCloud", Texture.class);
        mediumCloud = directory.getEntry("mainMenu:mediumCloud", Texture.class);
        smallCloud = directory.getEntry("mainMenu:smallCloud", Texture.class);
    }
    /** Populates the menu with clouds */
    public void populateMenu(){
        // Initialize the clouds to be drawn on screen
        // Order: [large, med, med, small, small]
        clouds.add(new Cloud(largeCloud, canvas.getWidth()-95, 295, 0.05f, 50.0f, 0.9f));
        clouds.add(new Cloud(mediumCloud, clouds.get(0).x - 450, clouds.get(0).y - 90, 0.1f, 100.0f,0.6f));
        clouds.add(new Cloud(mediumCloud, clouds.get(1).x + 220, clouds.get(1).y + 360, 0.08f, 100.0f, 0.6f));
        clouds.add(new Cloud(smallCloud, clouds.get(1).x - 100, clouds.get(1).y, 0.12f, 150.0f,0.6f));
        clouds.add(new Cloud(smallCloud, clouds.get(2).x - 100, clouds.get(2).y+ 100, 0.12f, 150.0f,0.6f));
    }

    /**
     * Called when this screen should release all resources.
     */
    public void dispose() {
        assets.unloadAssets();
        assets.dispose();
    }
    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        populateMenu();
        pressState = 0;
    }

    /**
     * Update the status of this player mode.
     *
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     *
     * @param delta Number of seconds since last animation frame
     */
    private void update(float delta) {
        // Update cloud positions
        for (Cloud cloud : clouds){
            cloud.update();
        }
    }

    /**
     * Draw the status of this player mode.
     *
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     */
    private void draw() {
        canvas.begin();
        canvas.draw(background, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 4; i >= 0; i--) {
            clouds.get(i).draw(canvas);
        }

        canvas.draw(title, Color.WHITE, 0, title.getHeight(), RIGHT_SPACING, canvas.getHeight() - TOP_SPACING, 0, textScale, textScale);
        Color tint = (pressState == 1 ? Color.GRAY : Color.WHITE);
        canvas.draw(play, tint, 0, play.getHeight(), RIGHT_SPACING + 10, canvas.getHeight() - TOP_SPACING - 300, 0, textScale, textScale);
        centerX = RIGHT_SPACING + 10 + play.getWidth() / 2;
        centerY = canvas.getHeight() - TOP_SPACING - 300 - play.getHeight() / 2;

        canvas.end();
    }


    // ADDITIONAL SCREEN METHODS
    /**
     * Called when the Screen should render itself.
     *
     * We defer to the other methods update() and draw().  However, it is VERY important
     * that we only quit AFTER a draw.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void render(float delta) {
        if (active) {
            update(delta);
            draw();

            // We are are ready, notify our listener
            if (isReady() && listener != null) {
                listener.exitScreen(this, 0);
            }
        }
    }

    /**
     * Returns true if all assets are loaded and the player is ready to go.
     *
     * @return true if the player is ready to go
     */
    public boolean isReady() {
        return pressState == 2;
    }

    /**
     * Called when the Screen is resized.
     *
     * This can happen at any point during a non-paused state but will never happen
     * before a call to show().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    public void resize(int width, int height) {
        // Compute the drawing scale
        float sx = ((float)width)/STANDARD_WIDTH;
        float sy = ((float)height)/STANDARD_HEIGHT;
        scale = (sx < sy ? sx : sy);
    }

    /**
     * Called when the Screen is paused.
     *
     * This is usually when it's not active or visible on screen. An Application is
     * also paused before it is destroyed.
     */
    public void pause() {
        // TODO Auto-generated method stub

    }

    /**
     * Called when the Screen is resumed from a paused state.
     *
     * This is usually when it regains focus.
     */
    public void resume() {
        // TODO Auto-generated method stub

    }

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    public void show() {
        // Useless if called in outside animation loop
        active = true;
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
        // Useless if called in outside animation loop
        active = false;
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    // PROCESSING PLAYER INPUT
    /**
     * Called when the screen was touched or a mouse button was pressed.
     *
     * This method checks to see if the play button is available and if the click
     * is in the bounds of the play button.  If so, it signals the that the button
     * has been pressed and is currently down. Any mouse button is accepted.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pressState == 2) {
            return true;
        }
//
//        // Flip to match graphics coordinates
//        screenY = heightY-screenY;
//
//        // TODO: Fix scaling
//        // Play button is a rectangle.
        float radius = textScale*scale*play.getWidth()/2.0f;
        float dist = (screenX-centerX)*(screenX-centerX)+(screenY-centerY)*(screenY-centerY);
        if (dist < radius*radius) {
            pressState = 1;
        }
        return false;
    }

    /**
     * Called when a finger was lifted or a mouse button was released.
     *
     * This method checks to see if the play button is currently pressed down. If so,
     * it signals the that the player is ready to go.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pressState == 1) {
            pressState = 2;
            return false;
        }
        return true;
    }

    /**
     * Called when a button on the Controller was pressed.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * pressing (but not releasing) the play button.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonDown (Controller controller, int buttonCode) {
        if (pressState == 0) {
            ControllerMapping mapping = controller.getMapping();
            if (mapping != null && buttonCode == mapping.buttonStart ) {
                pressState = 1;
                return false;
            }
        }
        return true;
    }

    /**
     * Called when a button on the Controller was released.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * releasing the the play button after pressing it.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonUp (Controller controller, int buttonCode) {
        if (pressState == 1) {
            ControllerMapping mapping = controller.getMapping();
            if (mapping != null && buttonCode == mapping.buttonStart ) {
                pressState = 2;
                return false;
            }
        }
        return true;
    }

    // UNSUPPORTED METHODS FROM InputProcessor

    /**
     * Called when a key is pressed (UNSUPPORTED)
     *
     * @param keycode the key pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyDown(int keycode) {
        return true;
    }

    /**
     * Called when a key is typed (UNSUPPORTED)
     *
     * @param character the key typed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyTyped(char character) {
        return true;
    }

    /**
     * Called when a key is released (UNSUPPORTED)
     *
     * @param keycode the key released
     * @return whether to hand the event to other listeners.
     */
    public boolean keyUp(int keycode) {
        return true;
    }

    /**
     * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @return whether to hand the event to other listeners.
     */
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    /**
     * Called when the mouse wheel was scrolled. (UNSUPPORTED)
     *
     * @param dx the amount of horizontal scroll
     * @param dy the amount of vertical scroll
     *
     * @return whether to hand the event to other listeners.
     */
    public boolean scrolled(float dx, float dy) {
        return true;
    }

    /**
     * Called when the mouse or finger was dragged. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    // UNSUPPORTED METHODS FROM ControllerListener

    /**
     * Called when a controller is connected. (UNSUPPORTED)
     *
     * @param controller The game controller
     */
    public void connected (Controller controller) {}

    /**
     * Called when a controller is disconnected. (UNSUPPORTED)
     *
     * @param controller The game controller
     */
    public void disconnected (Controller controller) {}

    /**
     * Called when an axis on the Controller moved. (UNSUPPORTED)
     *
     * The axisCode is controller specific. The axis value is in the range [-1, 1].
     *
     * @param controller The game controller
     * @param axisCode 	The axis moved
     * @param value 	The axis value, -1 to 1
     * @return whether to hand the event to other listeners.
     */
    public boolean axisMoved (Controller controller, int axisCode, float value) {
        return true;
    }

}
