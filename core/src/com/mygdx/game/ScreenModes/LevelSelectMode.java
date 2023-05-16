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
import com.mygdx.game.InputController;
import util.FilmStrip;
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
        /** Whether the text is empty */
        private boolean isEmpty;
        private float scale;
        /** How much time has passed */
        private float time;
        /**
         * Creates a single instance of a button.
         * @param bTexture The texture of the button
         * @param x The x-position of the texture on screen
         * @param y The y-position of the texture on screen
         */
        public Text (Texture bTexture, float x, float y, boolean e, float scale){
            this.bTexture = bTexture;
            this.x = x;
            this.y = y;
            this.isEmpty = e;
            this.scale = scale;
            time = 0;
        }

        /**
         * Draws the texture for buttons or titles.
         * @param canvas
         */
        private void draw(GameCanvas canvas){
            Color color = Color.WHITE;

            time++;
            if (time >= 50){
                color = Color.LIGHT_GRAY;
            }
            if (time >= 100){
                time = 0;
            }
            if (menuCaravan.isMoving){
                time = 0;
            }
            if (!menuCaravan.isMoving || isEmpty) {
                canvas.draw(bTexture, color, 0, bTexture.getHeight(), x, y, 0, scale, scale);
            }
        }


    }
    /**
     * Class representing the caravan to be placed on screen.
     */
    class MenuCaravan {
        // Class variables
        /** Texture for button */
        private Texture texture;
        /** X-position of button */
        private float x;
        /** Y-position of button */
        private float y;
        /** Animator for the caravan */
        private FilmStrip animator;
        private final int NUM_ANIM_FRAMES = 3;
        /** How fast we change frames (one frame per 10 calls to update) */
        private static final float ANIMATION_SPEED = 0.1f;
        /** Current animation frame for this shell */
        private float aframe;
        private float scale = 0.2f;
        /** Starting x position of the caravan */
        private float startX;
        /** The goal for the caravan to move to */
        private float goalX;
        /** Whether the caravan is currently moving */
        private boolean isMoving;
        /** The current level the caravan is at */
        private int currLevel;
        /** Whether the caravan is moving right or left */
        private boolean movingRight;
        /** How fast the caravan moves */
        private float moveSpeed = 2.0f;
        /** Whether we are just loading in the screen */
        private boolean loadingIn;


        /**
         * Creates a single instance of a button.
         * @param texture The texture of the button
         * @param x The x-position of the texture on screen
         * @param y The y-position of the texture on screen
         */
        public MenuCaravan (Texture texture, float x, float y){
            this.texture = texture;
            startX = x;
            this.y = y;
            this.x = -150;
            goalX = 0;
            isMoving = false;
            currLevel = 0;
            movingRight = true;
            loadingIn = true;

            animator = new FilmStrip(texture, 1, NUM_ANIM_FRAMES);
            aframe = 0;
        }

        private void update(int currLevel){
            if (loadingIn){
                loadingIn = false;
                isMoving = true;
                goalX = startX;
            }
            if (this.currLevel != currLevel){
                isMoving = true;
                if (currLevel > this.currLevel){
                    movingRight = true;
                }
                else{
                    movingRight = false;
                }
                this.currLevel = currLevel;
                goalX = startX + currLevel*220;
            }
            if (isMoving){
                if (movingRight){
                    if (this.x >= goalX){
                        isMoving = false;
                    }
                    this.x += moveSpeed;
                }
                else{
                    if (this.x <= goalX){
                        isMoving = false;
                    }
                    this.x -= moveSpeed;
                }

            }
            // Update animation frames
            aframe += ANIMATION_SPEED;
            if (aframe >= NUM_ANIM_FRAMES){
                aframe = 0;
            }
        }

        /**
         * Draws the texture for buttons or titles.
         * @param canvas
         */
        private void draw(GameCanvas canvas){
            animator.setFrame((int)aframe);
            canvas.draw(animator, Color.WHITE, 0, texture.getHeight(), x, y, 0, scale, scale);
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
    private Texture mushroom;
    private Texture mushroomDown;
    private Texture smogWall;
    private Texture caravan;
    private Texture empty;
    private float cursorScale = 0.15f;
    /** Texture for title */
    private Texture title;
    /** Texture for enter prompt */
    private Texture enter;
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
    /** Scaling factor for the buttons (mushrooms). */
    private float buttonScale = 0.4f;
    /** The current state of the play button */
    private int   pressState;
    /** Whether or not this player mode is still active */
    private boolean active;
    /** Spacing for text from right of screen */
    private final float RIGHT_SPACING = 50.0f;
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
    /** Exit state for level 0 */
    public static final int EXIT_0 = 1;
    /** Exit state for level 1 */
    public static final int EXIT_1 = 2;

    /** checks if graphics have been loaded */
    private boolean loaded = false;
    /** All tables used for UI */
    private Array<Table> tables;
    private boolean populated = false;
    /** Represents the current level that is unlocked */
    private int unlocked = 1;
    private int currLevel;
    private int numLevels = 6;
    private MenuCaravan menuCaravan;
    /** Input Controller **/
    public InputController input;
    /** Whether we are ready to switch screens */
    private boolean isReady;


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
     * Creates a Level Select with the default size and position.
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
        input = new InputController();
        isReady = false;

        Gdx.input.setInputProcessor( this );
        stage = new Stage();
        buttonState = 1;
        currLevel = 0;
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
        background = directory.getEntry("levelSelect:background", Texture.class);
        title = directory.getEntry("images:empty", Texture.class);
        level1 = directory.getEntry("levelSelect:1", Texture.class);
        level2 = directory.getEntry("levelSelect:2", Texture.class);
        level1Down = directory.getEntry("levelSelect:1Down", Texture.class);
        level2Down = directory.getEntry("levelSelect:2Down", Texture.class);
        back = directory.getEntry("levelSelect:back", Texture.class);
        backDown = directory.getEntry("levelSelect:backDown", Texture.class);
        nullFont = directory.getEntry("shared:retro" ,BitmapFont.class);
        mushroom = directory.getEntry("levelSelect:mushroom", Texture.class);
        mushroomDown = directory.getEntry("levelSelect:mushroomDown", Texture.class);
        caravan = directory.getEntry("levelSelect:caravan", Texture.class);
        smogWall = directory.getEntry("levelSelect:smogWall", Texture.class);
        empty = directory.getEntry("images:empty", Texture.class);
        enter = directory.getEntry("levelSelect:enter", Texture.class);
        loaded = true;
    }
    /** Populates the menu with clouds */
    public void populateMenu(){
        // Initialize the buttons/titles to be drawn on screen
        float startX = RIGHT_SPACING + 6;
        float startY = canvas.getHeight()*.05f;

        tables = new Array<Table>();

        // Title
        if (!populated){
            text.add(new Text(enter, RIGHT_SPACING-30, canvas.getHeight()*.1f, false, 0.7f));
            text.add(new Text(empty, RIGHT_SPACING-30, canvas.getHeight()*.1f, true, 0.7f));
        }

        // Table for back button
        Table backTable = new Table();
        tables.add(backTable);
        backTable.setPosition(startX-10, canvas.getHeight()*0.90f);
        backTable.setWidth(back.getWidth());
        backTable.setHeight(back.getHeight());
        backTable.setDebug(false);

        // Table for level select buttons
        Table tableLevels = new Table();
        tables.add(tableLevels);
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



        // Levels
        for (int i = 0; i < numLevels; i++){
            addLevel(tableLevels, mushroom, mushroomDown, i);
        }


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
                    isReady = true;
                }
            };
        } );
        // Check clicks for level buttons
        // Level 0 button
        buttons.get(1).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(1).isChecked()) {
                    buttons.get(1).setChecked(false);
                    if (0 <= unlocked){
                        currLevel = 0;
                        buttonState = EXIT_0;
                    }
                }
            };
        } );
        buttons.get(2).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(2).isChecked()) {
                    buttons.get(2).setChecked(false);
                    currLevel = 1;
                    if (1 <= unlocked){
                        buttonState = EXIT_1;
                    }
                }
            };
        } );

        if (!populated){
            populated = true;
        }

        // Add caravan
        menuCaravan = new MenuCaravan(caravan, tables.get(1).getX()+buttons.get(0).getX()-25, tables.get(1).getHeight()+buttons.get(0).getY()+40);


    }

    /**
     * Adds a level button to the level table.
     * @param tableLevels table used to hold all level buttons
     * @param up the up texture for the button
     * @param down the down texture for the button
     * @param level the level number represented by the button
     */
    private void addLevel(Table tableLevels, Texture up, Texture down, int level){
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        if (level <= unlocked){
            textButtonStyle.up   = new TextureRegionDrawable(up);
            textButtonStyle.checked = new TextureRegionDrawable(up);
        }
        else{
            textButtonStyle.up   = new TextureRegionDrawable(down);
            textButtonStyle.checked = new TextureRegionDrawable(down);
        }
        textButtonStyle.down = new TextureRegionDrawable(down);
        buttons.add(new TextButton("", textButtonStyle));
        tableLevels.add(buttons.get(level+1)).spaceRight(92.0f).left().size(up.getWidth()*buttonScale, up.getHeight()*buttonScale);
        //tableLevels.row();
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
                // t = new Table();
            }
        }
        Gdx.input.setInputProcessor(null);
        populateMenu();
        buttonState = 1;
        isReady = false;
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
        menuCaravan.update(currLevel);

        input.readInput();
        if (input.didPressEnter()){
            if (!menuCaravan.isMoving){
                isReady = true;
            }
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
        for (Text t: text){
            t.draw(canvas);
        }

        // canvas.draw(level1, Color.WHITE, 0, 0, canvas.getWidth()*0.8f, canvas.getHeight()*0.5f);
        //Color color = Color.WHITE;
        // color.a  = appearTime/ APPEAR_TIME;
        // System.out.println(color.a);
        stage.draw();
        menuCaravan.draw(canvas);

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
                // System.out.println("exit from level select");
                if (isReady){
                    listener.exitScreen(this, buttonState);
                }
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
        if(!loaded){
            return false;
        }
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
