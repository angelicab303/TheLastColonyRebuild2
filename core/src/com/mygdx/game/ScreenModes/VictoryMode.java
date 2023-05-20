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

public class VictoryMode implements Screen, InputProcessor, ControllerListener {

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
    private Texture egg;
    private Texture goldEgg;
    private Texture nextLevel;
    private Texture nextLevelDown;
    private Texture retry;
    private Texture retryDown;
    private Texture victory;


    private Texture textBox;
    private Texture empty;


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
    private float titleScale = 0.55f;
    /** Scaling factor for the text. */
    private float textScale = 0.6f;
    private float eggScale = 1.0f;
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
    private Array<VictoryMode.Text> text;
    /** X center of play button */
    private float centerX;
    /** Y center of play button */
    private float centerY;
    /** Font to be used as placeholder for buttons */
    private BitmapFont nullFont;
    /** the state of which button was pressed (0=none, 1=play, 2=levels, 3=settings, 4=exit) */
    private int buttonState;
    private boolean populated = false;
    private float sliderScales = 0.5f;
    public final int EXIT_RETRY = 1;
    public final int EXIT_NEXT_LEVEL = 2;


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
    public VictoryMode(GameCanvas canvas) {
        this.canvas  = canvas;

        // Compute the dimensions from the canvas
        resize(canvas.getWidth(),canvas.getHeight());

        pressState = 0;


        text = new Array<VictoryMode.Text>();
        pressState = 0;
        appearTime = 0;

        Gdx.input.setInputProcessor(this);
        stage = new Stage();
        table = new Table();
        buttonState = 0;



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
        title = directory.getEntry("victory:victory", Texture.class);
        egg = directory.getEntry("victory:egg", Texture.class);
        goldEgg = directory.getEntry("victory:goldEgg", Texture.class);
        nextLevel = directory.getEntry("victory:nextLevel", Texture.class);
        nextLevelDown = directory.getEntry("victory:nextLevelDown", Texture.class);
        retry = directory.getEntry("victory:retry", Texture.class);
        retryDown = directory.getEntry("victory:retryDown", Texture.class);
        empty = directory.getEntry("settings:textBoxEmpty" ,Texture.class);
        nullFont = directory.getEntry("shared:retro" ,BitmapFont.class);

    }
    /** Populates the menu with clouds */
    public void populateMenu(){
        System.out.println("Settings Menu populated");
        // Initialize the clouds to be drawn on screen
        // Order: [large, med, med, small, small]
        float startX = RIGHT_SPACING + 30;
        float startY = canvas.getHeight()*.05f;
        if (!populated){
            text.add(new VictoryMode.Text(title, RIGHT_SPACING+290, canvas.getHeight()*.95f, false));
            //text.add(new VictoryMode.Text(egg, RIGHT_SPACING, canvas.getHeight()*.85f, false));
        }


        // Initialize the buttons/titles to be drawn on screen
        tables = new Array<Table>();


        // playSkin.addRegions(new TextureAtlas(play));

        float table1X = RIGHT_SPACING + 530;
        float table1Y = canvas.getHeight()*.2f;
        //table.setFillParent(true);
        Table table1 = new Table();
        tables.add(table1);
        table1.setPosition(table1X, table1Y);
        table1.setWidth(600.0f);
        table1.setHeight(400.0f);
        table1.setDebug(false);

        float table2X = startX;
        float table2Y = table1Y - 270;
        //table.setFillParent(true);
        Table table2 = new Table();
        tables.add(table2);
        table2.setPosition(RIGHT_SPACING+290, table2Y-100);
        table2.setWidth(600.0f);
        table2.setHeight(400.0f);
        table2.setDebug(false);



        Gdx.input.setInputProcessor(stage);
        buttons = new Array<TextButton>();


        // Egg image
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(egg);
        textButtonStyle.down = new TextureRegionDrawable(egg);
        textButtonStyle.checked = new TextureRegionDrawable(egg);
        buttons.add(new TextButton("", textButtonStyle));
        table1.add(buttons.get(0)).left().size(egg.getWidth()*eggScale, egg.getHeight()*eggScale);

        // table 2
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(retry);
        textButtonStyle.down = new TextureRegionDrawable(retryDown);
        textButtonStyle.checked = new TextureRegionDrawable(retry);
        buttons.add(new TextButton("", textButtonStyle));
        table2.add(buttons.get(1)).left().spaceRight(400).size(retry.getWidth()*textScale, retry.getHeight()*textScale);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(nextLevel);
        textButtonStyle.down = new TextureRegionDrawable(nextLevelDown);
        textButtonStyle.checked = new TextureRegionDrawable(nextLevel);
        buttons.add(new TextButton("", textButtonStyle));
        table2.add(buttons.get(2)).left().size(nextLevel.getWidth()*textScale, nextLevel.getHeight()*textScale);



        table1.left().top();
        stage.addActor(table1);
        table2.left().top();
        stage.addActor(table2);



        // Hook up the buttons
        // Retry button
        buttons.get(1).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(1).isChecked()) {
                    buttons.get(1).setChecked(false);
                    buttonState = EXIT_RETRY;
                }
            };
        } );
        buttons.get(2).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(2).isChecked()) {
                    buttons.get(2).setChecked(false);
                    buttonState = EXIT_NEXT_LEVEL;
                    canvas.camera.zoom = 1.0f;
                    canvas.camera.position.x = canvas.getWidth()/2;
                    canvas.camera.position.y = canvas.getHeight()/2;
                    canvas.camera.update();
                }
            };
        } );


        populated = true;



    }
    /**
     * Adds a button to a table.
     * @param table table used to hold all pause buttons
     * @param up the up texture for the button
     * @param down the down texture for the button
     * @param num the number of the button in table order
     */
    private void addButton(Table table, Texture up, Texture down, int num){
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(up);
        textButtonStyle.checked = new TextureRegionDrawable(up);
        textButtonStyle.down = new TextureRegionDrawable(down);
        buttons.add(new TextButton("", textButtonStyle));
        table.add(buttons.get(num)).spaceBottom(50f).spaceRight(150f).left().size(up.getWidth()*textScale, up.getHeight()*textScale);
    }
    private void addButtonHorizontal(Table table, Texture up, Texture down, int num, String text, float scale){
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(up);
        textButtonStyle.checked = new TextureRegionDrawable(up);
        textButtonStyle.down = new TextureRegionDrawable(down);
        buttons.add(new TextButton(text, textButtonStyle));
        table.add(buttons.get(num)).spaceRight(40).top().left().size(up.getWidth()*scale, up.getHeight()*scale);

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

        // Draw buttons/title
        for (VictoryMode.Text t: text) {
            System.out.println("text array size: " + text.size);
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
