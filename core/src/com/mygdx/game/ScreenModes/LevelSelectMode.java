package com.mygdx.game.ScreenModes;

import assets.AssetDirectory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameCanvas;
import util.ScreenListener;

public class LevelSelectMode implements Screen, InputProcessor, ControllerListener {
    /**
     * Class representing buttons or titles to be placed on screen.
     */
    class Text {
        // Class variables
        /** Texture for button */
        private Texture bTexture;
        /** X-position of button */
        private float x;
        /** Y-position of button */
        private float y;
        /** Whether the button is pressable */
        private boolean pressable;
        /**
         * Creates a single instance of a button.
         * @param bTexture The texture of the button
         * @param x The x-position of the texture on screen
         * @param y The y-position of the texture on screen
         */
        public Text (Texture bTexture, float x, float y, boolean p){
            this.bTexture = bTexture;
            this.x = x;
            this.y = y;
            this.pressable = p;
        }

        /**
         * Draws the texture for buttons or titles.
         * @param canvas
         */
        private void draw(GameCanvas canvas){
            Color color = Color.WHITE;
            if (!pressable) {
                canvas.draw(bTexture, Color.WHITE, 0, bTexture.getHeight(), x, y, 0, textScale, textScale);
            }
            else{
                Color tint = (pressState == 1 ? Color.GRAY : color);
                canvas.draw(bTexture, tint, 0, bTexture.getHeight(), x, y, 0, textScale, textScale);

            }
        }


    }
    /** The assets to be loaded */
    private AssetDirectory assets;
    /** Background texture for start-up */
    private Texture background;
    /** Stage for UI */
    private Stage stage;
    /** The button to click on */
    private Array<TextButton> buttons;
    /** Texture for level buttons */
    private Texture back;
    private Texture backDown;
    private Texture level1;
    private Texture level2;
    private Texture level1Down;
    private Texture level2Down;
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
    private float cursorScale = 0.15f;
    /** Texture for title */
    private Texture title;
    /** Default budget for asset loader (do nothing but load 60 fps) */
    private static int DEFAULT_BUDGET = 15;
    /** Standard window size (for scaling) */
    private static int STANDARD_WIDTH  = 800;
    /** Standard window height (for scaling) */
    private static int STANDARD_HEIGHT = 700;
    /** Time for menu assets to appear on screen after loading */
    private final float  APPEAR_TIME = 100.0f;
    /** Amount of appearing time passed */
    private float appearTime;
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
    /** Array of text */
    private Array<Text> text;

    /** Number of clouds to be drawn */
    private final int NUM_CLOUDS = 5;
    /** X center of play button */
    private float centerX;
    /** Y center of play button */
    private float centerY;
    /** Font to be used as placeholder for buttons */
    private BitmapFont nullFont;
    /** the state of which button was pressed */
    private int buttonState;
    /** Exit state for returning back to main menu */
    public static final int EXIT_MAIN = 0;
    /** Exit state for level 1 */
    public static final int EXIT_1 = 1;
    /** Exit state for level 2 */
    public static final int EXIT_2 = 2;


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
    public LevelSelectMode(GameCanvas canvas) {
        this.canvas  = canvas;

        // Compute the dimensions from the canvas
        resize(canvas.getWidth(),canvas.getHeight());

        pressState = 0;
        text = new Array<Text>();
        pressState = 0;
        // appearTime = 0;

        Gdx.input.setInputProcessor( this );
        stage = new Stage();
        buttonState = -1;
    }

    /**
     * Gather the assets for the level select menu.
     *
     * This method extracts the asset variables from the given asset directory. It
     * should only be called after the asset directory is completed.
     *
     * @param directory	Reference to global asset manager.
     */
    public void gatherAssets(AssetDirectory directory) {
        // Allocate the main menu assets
        background = directory.getEntry("mainMenu:background", Texture.class);
        title = directory.getEntry("levelSelect:title", Texture.class);
        level1 = directory.getEntry("levelSelect:1", Texture.class);
        level2 = directory.getEntry("levelSelect:2", Texture.class);
        level1Down = directory.getEntry("levelSelect:1Down", Texture.class);
        level2Down = directory.getEntry("levelSelect:2Down", Texture.class);
        back = directory.getEntry("levelSelect:back", Texture.class);
        backDown = directory.getEntry("levelSelect:backDown", Texture.class);
        nullFont = directory.getEntry("shared:retro" ,BitmapFont.class);
    }
    /** Populates the menu with clouds */
    public void populateMenu(){
        // Initialize the buttons/titles to be drawn on screen
        float startX = RIGHT_SPACING + 10;
        float startY = canvas.getHeight()*.05f;

        // Title
        text.add(new Text(title, RIGHT_SPACING, canvas.getHeight()*.85f, false));

        // Table for back button
        Table backTable = new Table();
        backTable.setPosition(startX-10, canvas.getHeight()*0.90f);
        backTable.setWidth(back.getWidth());
        backTable.setHeight(back.getHeight());
        backTable.setDebug(false);

        // Table for level select buttons
        Table tableLevels = new Table();
        //table.setFillParent(true);
        tableLevels.setPosition(startX, startY);
        tableLevels.setWidth(600.0f);
        tableLevels.setHeight(400.0f);
        tableLevels.setDebug(false);

        Gdx.input.setInputProcessor(stage);

        buttons = new Array<TextButton>();
        // Back button to return to main menu
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(back);
        textButtonStyle.down = new TextureRegionDrawable(backDown);
        textButtonStyle.checked = new TextureRegionDrawable(back);
        buttons.add(new TextButton("", textButtonStyle));
        backTable.add(buttons.get(0)).left().size(back.getWidth()*textScale, back.getHeight()*textScale);



        // Level 1
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(level1);
        textButtonStyle.down = new TextureRegionDrawable(level1Down);
        textButtonStyle.checked = new TextureRegionDrawable(level1);
        buttons.add(new TextButton("", textButtonStyle));
        tableLevels.add(buttons.get(1)).spaceBottom(20.0f).left().size(level1.getWidth()*textScale, level1.getHeight()*textScale);
        tableLevels.row();

        // Level 2
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(level2);
        textButtonStyle.down = new TextureRegionDrawable(level2Down);
        textButtonStyle.checked = new TextureRegionDrawable(level2);
        buttons.add(new TextButton("", textButtonStyle));
        tableLevels.add(buttons.get(2)).spaceBottom(20.0f).left().size(level2.getWidth()*textScale, level2.getHeight()*textScale);
        tableLevels.row();

        tableLevels.left().top();
        backTable.left().top();
        stage.addActor(tableLevels);
        stage.addActor(backTable);


        // Hook up the buttons
        // Back button
        buttons.get(0).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(0).isChecked()) {
                    buttonState = EXIT_MAIN;
                }
            };
        } );
        // Level 1 button
        buttons.get(1).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(1).isChecked()) {
                    buttonState = EXIT_1;
                }
            };
        } );
        buttons.get(2).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(2).isChecked()) {
                    buttonState = EXIT_2;
                }
            };
        } );

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
        buttonState = -1;
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
        for (Text t: text){
            t.draw(canvas);
        }

        // canvas.draw(level1, Color.WHITE, 0, 0, canvas.getWidth()*0.8f, canvas.getHeight()*0.5f);
        //Color color = Color.WHITE;
        // color.a  = appearTime/ APPEAR_TIME;
        // System.out.println(color.a);
        stage.draw();

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
            if (buttonState > -1 && listener != null) {
                System.out.println("exit from level select");
                listener.exitScreen(this, buttonState);
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

    public void setAssetPositions(){

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
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
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
        float radius = textScale*scale*level1.getWidth()/2.0f;
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
