package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import util.*;
import assets.*;
import audio.*;
import math.*;
import obstacle.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends Game implements ScreenListener {
	//SpriteBatch batch;
	//Texture img;

	/** AssetManager to load game assets (textures, sounds, etc.) */
	AssetDirectory directory;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Player mode for the game proper (CONTROLLER CLASS) */
	/** Player mode for the main menu */
	private MainMenuMode mainMenu;
	private int current;
	/** List of all WorldControllers */
	private WorldController[] controllers;
	/** Input Controller **/
	private InputController input = new InputController();

	public MyGdxGame () { }
	
	@Override
	public void create () {

		//batch = new SpriteBatch();
		//img = new Texture("badlogic.jpg");

		canvas  = new GameCanvas();
		loading = new LoadingMode("assets.json",canvas,1);
		mainMenu = new MainMenuMode(canvas);

		 //Initialize the three game worlds
		 controllers = new WorldController[1];
		 controllers[0] = new GameplayController(canvas);

		 //Initialize the first game world
		//controllers[0] = new RocketController();

		current = 0;
		loading.setScreenListener(this);
		setScreen(loading);
	}
//
//	@Override
//	public void render () {
//		ScreenUtils.clear(1, 0, 0, 1);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
//	}
	
//	@Override
//	public void dispose () {
//		batch.dispose();
//		img.dispose();
//	}

	/**
	 * Called when the Application is destroyed.
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		for(int ii = 0; ii < controllers.length; ii++) {
			controllers[ii].dispose();
		}

		canvas.dispose();
		canvas = null;

		// Unload all of the resources
		// Unload all of the resources
		if (directory != null) {
			directory.unloadAssets();
			directory.dispose();
			directory = null;
		}
		super.dispose();
	}

	/**
	 * Called when the Application is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (screen == loading) {
			directory = loading.getAssets();
			controllers[0].gatherAssets(directory);
			controllers[0].setScreenListener(this);
			controllers[0].setCanvas(canvas);
			System.out.println("Gather assets for main menu");
			mainMenu.gatherAssets(directory);
			mainMenu.setScreenListener(this);

			mainMenu.reset();
			mainMenu.setCanvas(canvas);
			setScreen(mainMenu);

			loading.dispose();
			loading = null;
		} else if (screen == mainMenu){
			controllers[0].reset();
			setScreen(controllers[0]);
		}
		else if (exitCode == WorldController.EXIT_NEXT) {
			current = (current+1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_PREV) {
			current = (current+controllers.length-1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}
