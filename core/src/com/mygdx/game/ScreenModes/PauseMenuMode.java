package com.mygdx.game.ScreenModes;

import assets.AssetDirectory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameCanvas;
import util.FilmStrip;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

public class PauseMenuMode {
    /*
        Screen Class representing the pause menu.
     */
    /** X position on screen */
    private float xPos;
    /** Y position on screen */
    private float yPos;
    /** Texture for background */
    private Texture background;
    private Texture hurry;
    private Texture resume;
    private Texture resumeDown;
    private Texture settings;
    private Texture settingsDown;
    private Texture exit;
    private Texture exitDown;
    private Texture restart;
    private Texture restartDown;
    /** Table for pause menu buttons and text */
    private Table pauseTable;
    /** Stage for UI */
    private Stage stage;
    /** the state of which button was pressed */
    private int buttonState;

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
    private Array<TextButton> buttons;
    /** Font to be used as placeholder for buttons */
    private BitmapFont nullFont;
    private boolean loaded = false;
    /** Scaling factor for the buttons. */
    private float buttonScale = 0.8f;
    private float titleScale = 0.5f;
    private boolean populated = false;
    /** Exit state for returning back to game */
    public static final int EXIT_GAME = 1;
    /** Exit state for going to settings */
    public static final int EXIT_SETTINGS = 2;
    /** Exit state for returning back to game */
    public static final int EXIT_MAINMENU = 3;
    /** Exit state for returning back to game */
    public static final int EXIT_RESTART = 4;

    private Sound select;


    public PauseMenuMode(float x, float y){
        //this.background = background;
        //this.spacing = spacing;
        xPos = x;
        yPos = y;
//        this.hurry = texts.get(0);
//        this.resume = texts.get(1);
//        this.resumeDown = texts.get(2);
//        this.settings = texts.get(3);
//        this.settingsDown = texts.get(4);
//        this.exit = texts.get(5);
//        this.exitDown = texts.get(6);

        stage = new Stage();
        buttonState = 0;

//        animator = new FilmStrip(value,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
//        aframe = 0.0f;

    }
    public void gatherAssets(AssetDirectory directory) {
        // Allocate the main menu assets
        background = directory.getEntry("pauseMenu:background", Texture.class);
        hurry = directory.getEntry("pauseMenu:hurry", Texture.class);
        resume = directory.getEntry("pauseMenu:resume", Texture.class);
        resumeDown = directory.getEntry("pauseMenu:resumeDown", Texture.class);
        settings = directory.getEntry("pauseMenu:settings", Texture.class);
        settingsDown = directory.getEntry("pauseMenu:settingsDown", Texture.class);
        exit = directory.getEntry("pauseMenu:exit", Texture.class);
        exitDown = directory.getEntry("pauseMenu:exitDown", Texture.class);
        restart = directory.getEntry("pauseMenu:restart", Texture.class);
        restartDown = directory.getEntry("pauseMenu:restartDown", Texture.class);
        nullFont = directory.getEntry("shared:retro" ,BitmapFont.class);
        select = directory.getEntry("sounds:select", Sound.class);
        loaded = true;
    }
    public void setMenuPosition(float x, float y){
        this.xPos = x;
        this.yPos = y;
        //pauseTable.setPosition(xPos, yPos);
    }
    /** Populates the pause menu */
    public void populateMenu(){
        // Initialize the buttons/titles to be drawn on screen
//        float startX = RIGHT_SPACING + 6;
//        float startY = canvas.getHeight()*.05f;

        // Title
//        if (!populated){
//            text.add(new LevelSelectMode.Text(enter, RIGHT_SPACING-30, canvas.getHeight()*.1f, false, 0.7f));
//            text.add(new LevelSelectMode.Text(empty, RIGHT_SPACING-30, canvas.getHeight()*.1f, true, 0.7f));
//        }

        // Table for back button
//        Table backTable = new Table();
//        tables.add(backTable);
//        backTable.setPosition(startX-10, canvas.getHeight()*0.90f);
//        backTable.setWidth(back.getWidth());
//        backTable.setHeight(back.getHeight());
//        backTable.setDebug(false);

        // Table for pause select buttons
//        Table tableLevels = new Table();
//        tables.add(tableLevels);
        //table.setFillParent(true);
        //pauseTable.setPosition(xPos, yPos);
        //pauseTable.setWidth(300.0f);
        //pauseTable.setHeight(400.0f);
//        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
//        Color color = new Color(0,0,0,0.01f);
//        bgPixmap.setColor(color);
//        bgPixmap.fill();
//        TextureRegionDrawable drawableBackground = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        //pauseTable.setBackground(drawableBackground);
//        Image ibackground = new Image(background);
//        stage.addActor(ibackground);
        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.center();
        pauseTable.setDebug(false);
        pauseTable.addAction(alpha(0.0f));
        pauseTable.addAction(fadeIn(1f));

        Gdx.input.setInputProcessor(stage);

        buttons = new Array<TextButton>();



        // Buttons
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(hurry);
        textButtonStyle.checked = new TextureRegionDrawable(hurry);
        textButtonStyle.down = new TextureRegionDrawable(hurry);
        buttons.add(new TextButton("", textButtonStyle));
        pauseTable.add(buttons.get(0)).spaceBottom(50f).center().size(hurry.getWidth()*titleScale, hurry.getHeight()*titleScale);
        pauseTable.row();

        addButton(pauseTable, resume, resumeDown, 1);
        addButton(pauseTable, restart, restartDown, 2);
        addButton(pauseTable, settings, settingsDown, 3);
        addButton(pauseTable, exit, exitDown, 4);


        //pauseTable.center().top();
        stage.addActor(pauseTable);


        // Hook up the buttons
        // Resume button
        buttons.get(1).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(1).isChecked()) {
                    buttons.get(1).setChecked(false);
                    buttonState = EXIT_GAME;
                    select.play();
//                    isReady = true;
                }
            };
        } );
        // Check clicks for level buttons
        // Restart button
        buttons.get(2).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(2).isChecked()) {
                    buttonState = EXIT_RESTART;
                    buttons.get(2).setChecked(false);
                    select.play();
                }
            };
        } );
        // Settings button
        buttons.get(3).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(3).isChecked()) {
                    buttons.get(3).setChecked(false);
                    System.out.println("Go to settings from pause menu");
                    buttonState = EXIT_SETTINGS;
                    select.play();
                }
            };
        } );
        // Exit button
        buttons.get(4).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttons.get(4).isChecked()) {
                    buttons.get(4).setChecked(false);
                    buttonState = EXIT_MAINMENU;
                    select.play();
                }
            };
        } );

        if (!populated){
            populated = true;
        }


    }
    /**
     * Adds a button to the pause table.
     * @param pauseTable table used to hold all pause buttons
     * @param up the up texture for the button
     * @param down the down texture for the button
     * @param num the number of the button in table order
     */
    private void addButton(Table pauseTable, Texture up, Texture down, int num){
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = nullFont;
        textButtonStyle.up   = new TextureRegionDrawable(up);
        textButtonStyle.checked = new TextureRegionDrawable(up);
        textButtonStyle.down = new TextureRegionDrawable(down);
        buttons.add(new TextButton("", textButtonStyle));
        pauseTable.add(buttons.get(num)).spaceBottom(30f).center().size(up.getWidth()*buttonScale, up.getHeight()*buttonScale);
        pauseTable.row();
    }

    public int getButtonState(){
        return buttonState;
    }
    public void resetButtonState(){
        this.buttonState = 0;
    }

    /**
     *  Updates the progression of the smog bar depending on how much smog was collected
     *  by the player.
     */
    public void update() {
        stage.act();

        // Increase animation frame
//        aframe += ANIMATION_SPEED;
//
//        if (aframe >= NUM_ANIM_FRAMES) {
//            aframe -= NUM_ANIM_FRAMES;
//        }
    }
    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        stage.clear();
        stage = new Stage();
        pauseTable.clearActions();
        pauseTable.clearListeners();
        pauseTable.clear();

        Gdx.input.setInputProcessor(null);
        populateMenu();
        buttonState = 0;
    }

    /**
     * Draws the heart
     * @param canvas
     */
    public void draw(GameCanvas canvas) {
        stage.draw();
    }


}
