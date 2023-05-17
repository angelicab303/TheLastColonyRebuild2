package com.mygdx.game.ScreenModes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameCanvas;
import util.FilmStrip;

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
    /** Table for pause menu buttons and text */
    private Table pauseTable;
    /** Stage for UI */
    private Stage stage;

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


    public PauseMenuMode(Texture background, Array<Texture> texts, float x, float y){
        this.background = background;
        this.spacing = spacing;
        xPos = x;
        yPos = y;
        this.hurry = texts.get(0);
        this.resume = texts.get(1);
        this.resumeDown = texts.get(2);
        this.settings = texts.get(3);
        this.settingsDown = texts.get(4);
        this.exit = texts.get(5);
        this.exitDown = texts.get(6);

        stage = new Stage();
        pauseTable = new Table();

//        animator = new FilmStrip(value,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
//        aframe = 0.0f;

    }
    /** Populates the pause menu */
    public void populateMenu(){
        // Initialize the buttons/titles to be drawn on screen
        float startX = RIGHT_SPACING + 6;
        float startY = canvas.getHeight()*.05f;

        // Title
        if (!populated){
            text.add(new LevelSelectMode.Text(enter, RIGHT_SPACING-30, canvas.getHeight()*.1f, false, 0.7f));
            text.add(new LevelSelectMode.Text(empty, RIGHT_SPACING-30, canvas.getHeight()*.1f, true, 0.7f));
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
        menuCaravan = new LevelSelectMode.MenuCaravan(caravan, tables.get(1).getX()+buttons.get(0).getX()-25, tables.get(1).getHeight()+buttons.get(0).getY()+40);


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
        xPos = canvas.camera.position.x - (canvas.camera.viewportWidth*cameraZoom)/2.0f + (33.0f * cameraZoom) + spacing;
        yPos = canvas.camera.position.y + (canvas.camera.viewportHeight*cameraZoom)*0.8f/2.0f - (15.0f * cameraZoom) - texture.getHeight()*scale;
        canvas.draw(texture, new Color(255, 255, 255, 0.75f), texture.getWidth()*scale, texture.getHeight()*scale/2, xPos, yPos, 0.0f, scale*0.5f, scale*0.5f);
//        xPos = canvas.camera.position.x + (canvas.getWidth()*cameraZoom)/2.0f - (90.0f * cameraZoom) - spacing;
//        yPos = canvas.camera.position.y + (canvas.getHeight()*cameraZoom)/2.0f - (40.0f * cameraZoom) - texture.getHeight()*scale;
    }
}
