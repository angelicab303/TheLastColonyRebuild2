package com.mygdx.game;

import audio.*;
import audio.AudioSource;
import audio.MusicQueue;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.ScreenModes.LevelSelectMode;
import com.mygdx.game.ScreenModes.LoadingMode;
import com.mygdx.game.ScreenModes.MainMenuMode;
import com.mygdx.game.ScreenModes.PauseMenuMode;
import util.*;
import assets.*;

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
	/** Player mode for the level select menu */
	private LevelSelectMode levelSelect;
	/** Player mode for the pause menu */
	private PauseMenuMode pauseMenu;
	private int current;
	/** List of all WorldControllers */
	private GameplayController controller;
	/** Input Controller **/
	private InputController input = new InputController();

//	private AudioSource samples[];
//	private AudioEngine engine;
//	private MusicQueue music;

	public MyGdxGame () { }
	
	@Override
	public void create () {

		//batch = new SpriteBatch();
		//img = new Texture("badlogic.jpg");

		canvas  = new GameCanvas();
		loading = new LoadingMode("assets.json",canvas,1);
		levelSelect = new LevelSelectMode(canvas);
		pauseMenu = new PauseMenuMode(0, 0);
		//Gdx.graphics.setContinuousRendering(false);
		mainMenu = new MainMenuMode(canvas);

		 //Initialize the three game worlds
		 //controllers = new WorldController[1];
		controller = new GameplayController(canvas, pauseMenu);

//		samples = new AudioSource[1];

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
		controller.dispose();

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
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (screen == loading) {
			directory = loading.getAssets();
			// gather assets for game level
			controller.gatherAssets(directory);
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			// gather assets for level select menu
			levelSelect.gatherAssets(directory);
			levelSelect.setScreenListener(this);
			levelSelect.setCanvas(canvas);
			// gather assets for main menu
			mainMenu.gatherAssets(directory);
			mainMenu.setScreenListener(this);
			// gather assets for pause menu
			pauseMenu.gatherAssets(directory);

//			samples[0] = directory.getEntry( "The Last Colony - Title Screen.ogg", AudioSource.class );
//
//			AudioEngine engine = (AudioEngine)Gdx.audio;
//			music = engine.newMusicBuffer( false, 44100 );
//			music.addSource( samples[0] );
//			music.play();

			mainMenu.reset();
			mainMenu.setCanvas(canvas);

			setScreen(mainMenu);

			loading.dispose();
			loading = null;
		} else if (screen == mainMenu){
			// Need exit codes for main menu
			levelSelect.reset();
			setScreen(levelSelect);
		}
		else if (screen == levelSelect){
			// Need exit codes for level select
			if (exitCode == levelSelect.EXIT_MAIN){
				System.out.println("main menu repopulate from exit");
				mainMenu.reset();
				setScreen(mainMenu);
			}
			else{
				System.out.println("Set game screen from levelSelect");
				controller.reset(exitCode);
				setScreen(controller);
				pauseMenu.populateMenu();
			}
		}
		else if (screen == controller){
			if (exitCode == pauseMenu.EXIT_MAINMENU){
				System.out.println("Set main menu from game screen");

				pauseMenu.reset();
				mainMenu.reset();
				setScreen(mainMenu);
//				music.stop();
//				music.reset();
			}
		}
//		else if (exitCode == WorldController.EXIT_NEXT) {
//			current = (current+1) % controllers.length;
//			controllers[current].reset();
//			setScreen(controllers[current]);
//		} else if (exitCode == WorldController.EXIT_PREV) {
//			current = (current+controllers.length-1) % controllers.length;
//			controllers[current].reset();
//			setScreen(controllers[current]);
//		} else if (exitCode == WorldController.EXIT_QUIT) {
//			// We quit the main application
//			Gdx.app.exit();
//		}
	}

}
