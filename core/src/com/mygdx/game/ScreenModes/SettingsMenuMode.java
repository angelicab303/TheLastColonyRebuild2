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

public class SettingsMenuMode implements Screen, InputProcessor, ControllerListener {
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
                canvas.draw(bTexture, Color.WHITE, 0, bTexture.getHeight(), x, y, 0, titleScale, titleScale);
            }
            else{
                Color tint = (pressState == 1 ? Color.GRAY : color);
                canvas.draw(bTexture, tint, 0, bTexture.getHeight(), x, y, 0, titleScale, titleScale);

            }
        }


    }
    /** The assets to be loaded */
    private AssetDirectory assets;
    /** Background texture for start-up */
    private Texture background;
    /** Stage for UI */
    private Stage stage;
    private Table table;
    /** The button to click on */
    private Array<TextButton> buttons;
    /** All tables used for UI */
    private Array<Table> tables;
    /** Texture for exit option */
    private Texture back;
    private Texture backDown;
    private Texture absorb;
    private Texture cloud1;
    private Texture cloud2;
    private Texture cloud3;
    private Texture fireWeapon;
    private Texture keyBindings;
    private Texture music;
    private Texture soundEffects;

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
    /** Scaling factor for the title. */
    private float titleScale = 0.8f;
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
    /** Array of text */
    private Array<SettingsMenuMode.Text> text;
    /** Array for all clouds */
    private Array<SettingsMenuMode.Cloud> clouds;
    /** Number of clouds to be drawn */
    private final int NUM_CLOUDS = 5;
    /** X center of play button */
    private float centerX;
    /** Y center of play button */
    private float centerY;
    /** Font to be used as placeholder for buttons */
    private BitmapFont nullFont;
    /** the state of which button was pressed (0=none, 1=play, 2=levels, 3=settings, 4=exit) */
    private int buttonState;
    private boolean populated = false;
    private boolean fromGame;
    public final int EXIT_MAIN_MENU = 1;
    public final int EXIT_GAME = 2;


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
    public SettingsMenuMode(GameCanvas canvas) {
        this.canvas  = canvas;

        // Compute the dimensions from the canvas
        resize(canvas.getWidth(),canvas.getHeight());

        pressState = 0;

        clouds = new Array<SettingsMenuMode.Cloud>();
        text = new Array<SettingsMenuMode.Text>();
        pressState = 0;
        appearTime = 0;

        Gdx.input.setInputProcessor(this);
        stage = new Stage();
        table = new Table();
        buttonState = 0;

        fromGame = false;


    }

    public void setFromGame(boolean fromGame){
        this.fromGame = fromGame;
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
        background = directory.getEntry("settings:background", Texture.class);
        title = directory.getEntry("settings:settings", Texture.class);
        absorb = directory.getEntry("settings:absorb", Texture.class);
        fireWeapon = directory.getEntry("settings:fireWeapon", Texture.class);
        keyBindings = directory.getEntry("settings:keyBindings", Texture.class);
        music = directory.getEntry("settings:music", Texture.class);
        soundEffects = directory.getEntry("settings:soundEffects", Texture.class);
        back = directory.getEntry("levelSelect:back", Texture.class);
        backDown = directory.getEntry("levelSelect:backDown", Texture.class);
        cloud1 = directory.getEntry("settings:cloud1", Texture.class);
        cloud2 = directory.getEntry("settings:cloud2", Texture.class);
        cloud3 = directory.getEntry("settings:cloud3", Texture.class);
        nullFont = directory.getEntry("shared:retro" ,BitmapFont.class);
    }
    /** Populates the menu with clouds */
    public void populateMenu(){
        System.out.println("Settings Menu populated");
        // Initialize the clouds to be drawn on screen
        // Order: [large, med, med, small, small]
        float startX = RIGHT_SPACING + 10;
        float startY = canvas.getHeight()*.05f;
        if (!populated){
//            clouds.add(new MainMenuMode.Cloud(largeCloud, canvas.getWidth()*0.95f, canvas.getHeight()*0.32f, 0.05f, 50.0f, 0.9f));
//            clouds.add(new MainMenuMode.Cloud(mediumCloud, clouds.get(0).x - 450, clouds.get(0).y - 90, 0.1f, 100.0f,0.6f));
//            clouds.add(new MainMenuMode.Cloud(mediumCloud, clouds.get(1).x + 220, clouds.get(1).y + 360, 0.08f, 100.0f, 0.6f));
//            clouds.add(new MainMenuMode.Cloud(smallCloud, clouds.get(1).x - 100, clouds.get(1).y, 0.12f, 150.0f,0.6f));
//            clouds.add(new MainMenuMode.Cloud(smallCloud, clouds.get(2).x - 100, clouds.get(2).y+ 100, 0.12f, 150.0f,0.6f));

            text.add(new SettingsMenuMode.Text(title, RIGHT_SPACING, canvas.getHeight()*.85f, false));
        }


        // Initialize the buttons/titles to be drawn on screen
        tables = new Array<Table>();


        // Table for back button
        Table backTable = new Table();
        tables.add(backTable);
        backTable.setPosition(startX-10, canvas.getHeight()*0.90f);
        backTable.setWidth(back.getWidth());
        backTable.setHeight(back.getHeight());
        backTable.setDebug(false);

        // playSkin.addRegions(new TextureAtlas(play));

        //table.setFillParent(true);
        table.setPosition(startX, startY);
        table.setWidth(600.0f);
        table.setHeight(400.0f);
        table.setDebug(false);

        Gdx.input.setInputProcessor(stage);
        buttons = new Array<TextButton>();


        // Back button
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(back);
        textButtonStyle.down = new TextureRegionDrawable(backDown);
        textButtonStyle.checked = new TextureRegionDrawable(back);
        buttons.add(new TextButton("", textButtonStyle));
        backTable.add(buttons.get(0)).left().size(back.getWidth()*textScale, back.getHeight()*textScale);
//        table.row();
//        // Settings button
//        textButtonStyle = new TextButton.TextButtonStyle();
//        textButtonStyle.font = nullFont;
//        textButtonStyle.up   = new TextureRegionDrawable(settings);
//        textButtonStyle.down = new TextureRegionDrawable(settingsDown);
//        textButtonStyle.checked = new TextureRegionDrawable(settings);
//        buttons.add(new TextButton("", textButtonStyle));
//        table.add(buttons.get(1)).spaceBottom(40.0f).left().size(settings.getWidth()*textScale, settings.getHeight()*textScale);
//        table.row();
//        // Exit button
//        textButtonStyle = new TextButton.TextButtonStyle();
//        textButtonStyle.font = nullFont;
//        textButtonStyle.up   = new TextureRegionDrawable(exit);
//        textButtonStyle.down = new TextureRegionDrawable(exitDown);
//        textButtonStyle.checked = new TextureRegionDrawable(exit);
//        buttons.add(new TextButton("", textButtonStyle));
//        table.add(buttons.get(2)).spaceBottom(40.0f).left().size(exit.getWidth()*textScale, exit.getHeight()*textScale);


        table.left().top();
        stage.addActor(table);
        backTable.left().top();
        stage.addActor(backTable);



        // Hook up the buttons
        // Back button
        buttons.get(0).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(0).isChecked()) {
                    buttons.get(0).setChecked(false);
                    if (fromGame){
                        buttonState = 2;
                        setFromGame(false);
                    }
                    else{
                        buttonState = 1;
                    }
                    System.out.println("Back buttons pressed");
                }
            };
        } );

        populated = true;



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
        stage.clear();
        if (tables != null){
            for (Table t : tables){
                t.clearActions();
                t.clearListeners();
                t.clear();
                stage = new Stage();
            }
        }
        Gdx.input.setInputProcessor(null);
        populateMenu();
        buttonState = 0;
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
        stage.act();
        if (appearTime <= APPEAR_TIME)
        {
            appearTime+= 0.3;
            if (appearTime > APPEAR_TIME){
                appearTime = APPEAR_TIME;
            }

        }

//        for (MainMenuMode.Cloud cloud : clouds){
//            cloud.update();
//        }
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
        Color color = Color.WHITE;
        // color.a  = appearTime/ APPEAR_TIME;
        // System.out.println(color.a);

        // Draw clouds
//        for (int i = NUM_CLOUDS-1; i >= 0; i--) {
//            clouds.get(i).draw(canvas);
//        }

        // Draw buttons/title
        for (SettingsMenuMode.Text t: text) {
            t.draw(canvas);
        }

        // Draw the stage of UI elements
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
            if (buttonState > 0 && listener != null) {
                System.out.println(buttonState);
                System.out.println("Go to level select screen");
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
////        // Flip to match graphics coordinates
////        screenY = heightY-screenY;
////
////        // TODO: Fix scaling
////        // Play button is a rectangle.
////        float radius = textScale*scale*play.getWidth()/2.0f;
//        float dist = (screenX-centerX)*(screenX-centerX)+(screenY-centerY)*(screenY-centerY);
//        if (dist < radius*radius) {
//            pressState = 1;
//        }
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
