/*
 * WorldController.java
 *
 * This is the most important new class in this lab.  This class serves as a combination 
 * of the CollisionController and GameplayController from the previous lab.  There is not 
 * much to do for collisions; Box2d takes care of all of that for us.  This controller 
 * invokes Box2d and then performs any after the fact modifications to the data 
 * (e.g. gameplay).
 *
 * If you study this class, and the contents of the edu.cornell.cs3152.physics.obstacles
 * package, you should be able to understand how the Physics engine works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package com.mygdx.game;

import assets.AssetDirectory;
import box2dLight.RayHandler;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Obstacles.Player;
import com.mygdx.game.Obstacles.PurifiedQueue;
import com.mygdx.game.Obstacles.ToxicQueue;
import com.mygdx.game.UI.AirBar;
import com.mygdx.game.UI.Heart;
import obstacle.Obstacle;
import util.PooledList;
import util.ScreenListener;

import java.util.Iterator;

/**
 * Base class for a world-specific controller.
 *
 *
 * A world has its own objects, assets, and input controller.  Thus this is 
 * really a mini-GameEngine in its own right.  The only thing that it does
 * not do is create a GameCanvas; that is shared with the main application.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public abstract class WorldController implements Screen {
	/** The texture for the background */
	protected TextureRegion backgroundTexture;
	/** The texture for walls and platforms */
	protected TextureRegion earthTile;
	/** The texture for the exit condition */
	protected TextureRegion goalTile;
	/** The font for giving messages to the player */
	protected BitmapFont displayFont;
	/** The font for giving sub-messages to the player */
	protected BitmapFont displayFontSub;
	/** The font for progress bar title */
	protected BitmapFont displayFontInteract;
	/** The actual assets to be loaded */
	private AssetDirectory assets;
	/** Texture atlas to support a progress bar */
	private Texture statusBar;

	// statusBar is a "texture atlas." Break it up into parts.
	/** Left cap to the status background (grey region) */
	private TextureRegion statusBkgLeft;
	/** Middle portion of the status background (grey region) */
	private TextureRegion statusBkgMiddle;
	/** Right cap to the status background (grey region) */
	private TextureRegion statusBkgRight;
	/** Left cap to the status forground (colored region) */
	private TextureRegion statusFrgLeft;
	/** Middle portion of the status forground (colored region) */
	private TextureRegion statusFrgMiddle;
	/** Right cap to the status forground (colored region) */
	private TextureRegion statusFrgRight;
	/** Texture asset for the full heart texture */
	protected Texture fHeartTexture;
	/** Texture asset for the slashed heart texture */
	protected Texture sHeartTexture;
	/** The width of the progress bar */
	private int width;
	/** The y-coordinate of the center of the progress bar */
	private int centerY;
	/** The x-coordinate of the center of the progress bar */
	private int centerX;
	/** The height of the canvas window (necessary since sprite origin != screen origin) */
	private int heightY;
	/** The x position of the progress bar */
	private int BAR_X = 120;
	/** The y position of the progress bar */
	private int BAR_Y = 420;
	/** The amount of padding from right edge of screen */
	private int BAR_PAD_X = 15;
	/** The amount of padding from upper edge of screen */
	private int BAR_PAD_Y = 50;
	/** The conversion from pixels to box2d units. 1 box2d meter = 16 pixels.
	 * Use meter units when calculating physics and handling box2d bodies.
	 * Use pixel units when drawing to screen! */
	protected final float PTM = 1.0f;

	/** Reference to the player avatar */
	protected Player player;
	/** Reference to the air bar asset */
	protected AirBar airBar;
	/** Heart list **/
	protected Array<Heart> heartArr;

	/** Reference to the weapon avatar */
	protected Weapon weapon;
	/** Amount of progress made in bar */
	protected float progress;
	/** Testing variable for progress */
	private boolean isIncrementing;
	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	/** Exit code for advancing to next level */
	public static final int EXIT_NEXT = 1;
	/** Exit code for jumping back to previous level */
	public static final int EXIT_PREV = 2;
    /** How many frames after winning/losing do we continue? */
	public static final int EXIT_COUNT = 120;

	/** The amount of time for a physics engine step. */
	public static final float WORLD_STEP = 1/60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;
	
	/** Width of the game world in Box2d units */
	protected static final float DEFAULT_WIDTH  = 32.0f;
	/** Height of the game world in Box2d units */
	protected static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down) */
	//WHO THE FRICK FORGOT TO TURN OFF GRAVITY YOU HAD ONE JOB
	protected static final float DEFAULT_GRAVITY = 0;
	
	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** All the objects in the world. */
	protected PooledList<Obstacle> objects  = new PooledList<Obstacle>();
	/** Queue for adding objects */
	protected PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
	/** Array for the objects that need to be drawn in order of y value (for perspective) **/
	protected Array<Obstacle> staticsAndPlayer;
	/** Input Controller **/
	public InputController input;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The Box2D world */
	protected World world;

	/** Collision Controller **/
	protected CollisionController collisionController;

	/** Shared memory pool for bullets. (MODEL CLASS) */
	protected PurifiedQueue purifiedAir;

	protected ToxicQueue toxicAir;

	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;
	
	/** Whether or not this is an active controller */
	private boolean active;
	/** Whether we have completed this level */
	private boolean complete;
	/** Whether we have failed at this world (and need a reset) */
	private boolean failed;
	/** Whether or not debug mode is active */
	private boolean debug;
	/** Countdown active for winning or losing */
	private int countdown;

	/**
	 * Returns true if debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @return true if debug mode is active.
	 */
	public boolean isDebug( ) {
		return debug;
	}

	/**
	 * Sets whether debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @param value whether debug mode is active.
	 */
	public void setDebug(boolean value) {
		debug = value;
	}

	/**
	 * Returns true if the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @return true if the level is completed.
	 */
	public boolean isComplete( ) {
		return complete;
	}

	/**
	 * Sets whether the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @param value whether the level is completed.
	 */
	public void setComplete(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		complete = value;
	}

	/**
	 * Returns true if the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @return true if the level is failed.
	 */
	public boolean isFailure( ) {
		return failed;
	}

	/**
	 * Sets whether the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @param value whether the level is failed.
	 */
	public void setFailure(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		failed = value;
	}
	
	/**
	 * Returns true if this is the active screen
	 *
	 * @return true if this is the active screen
	 */
	public boolean isActive( ) {
		return active;
	}

	/**
	 * Returns the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers
	 *
	 * @return the canvas associated with this controller
	 */
	public GameCanvas getCanvas() {
		return canvas;
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
		this.scale.x = canvas.getWidth()/bounds.getWidth();
		this.scale.y = canvas.getHeight()/bounds.getHeight();
	}
	
	/**
	 * Creates a new game world with the default values.
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 */
	protected WorldController() {
		this(new Rectangle(0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT), 
			 new Vector2(0,DEFAULT_GRAVITY));
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param width  	The width in Box2d coordinates
	 * @param height	The height in Box2d coordinates
	 * @param gravity	The downward gravity
	 */
	protected WorldController(float width, float height, float gravity) {
		this(new Rectangle(0,0,width,height), new Vector2(0,gravity));
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param bounds	The game bounds in Box2d coordinates
	 * @param gravity	The gravitational force on this Box2d world
	 */
	protected WorldController(Rectangle bounds, Vector2 gravity) {
		world = new World(gravity,false);
		this.bounds = new Rectangle(bounds);
		this.scale = new Vector2(1,1);
		complete = false;
		failed = false;
		debug  = false;
		active = false;
		countdown = -1;
	}
	
	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		objects = null;
		addQueue = null;
		bounds = null;
		scale  = null;
		world  = null;
		canvas = null;
	}

	/**
	 * Gather the assets for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory	Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {
		// Allocate the tiles
		earthTile = new TextureRegion(directory.getEntry( "shared:earth", Texture.class ));
		goalTile  = new TextureRegion(directory.getEntry( "shared:goal", Texture.class ));
		backgroundTexture = new TextureRegion(directory.getEntry("images:ice", Texture.class));
		displayFont = directory.getEntry( "shared:retro" ,BitmapFont.class);
		displayFontSub = directory.getEntry( "shared:retroSub" ,BitmapFont.class);
		displayFontInteract = directory.getEntry( "shared:light" ,BitmapFont.class);
	}

	/**
	 *
	 * Adds a physics object in to the insertion queue.
	 *
	 * Objects on the queue are added just before collision processing.  We do this to 
	 * control object creation.
	 *
	 * param obj The object to add
	 */
	public void addQueuedObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		addQueue.add(obj);
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * param obj The object to add
	 */
	protected void addObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);
		//obj.activatePhysics(world);
	}

	/**
	 * Returns true if the object is in bounds.
	 *
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
	 *
	 * @return true if the object is in bounds.
	 */
	public boolean inBounds(Obstacle obj) {
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
		boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
		return horiz && vert;
	}
	
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public abstract void reset();
	
	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * @param dt	Number of seconds since last animation frame
	 * 
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		
		input.readInput();
//		if (listener == null) {
//			return true;
//		}

//		// Toggle debug
		if (input.didDebug()) {
			debug = !debug;
		}
//
//		// Handle resets
//		if (input.didReset()) {
//			reset();
//		}

		// We can use this for switching levels:
//		// Now it is time to maybe switch screens.
//		if (input.didExit()) {
//			pause();
//			listener.exitScreen(this, EXIT_QUIT);
//			return false;
//		} else if (input.didAdvance()) {
//			pause();
//			listener.exitScreen(this, EXIT_NEXT);
//			return false;
//		} else if (input.didRetreat()) {
//			pause();
//			listener.exitScreen(this, EXIT_PREV);
//			return false;
//		} else if (countdown > 0) {
//			countdown--;
//		} else if (countdown == 0) {
//			if (failed) {
//				reset();
//			} else if (complete) {
//				pause();
//				listener.exitScreen(this, EXIT_NEXT);
//				return false;
//			}
//		}
		return true;
	}
	
	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */
	public abstract void update(float dt);
	
	/**
	 * Processes physics
	 *
	 * Once the update phase is over, but before we draw, we are ready to handle
	 * physics.  The primary method is the step() method in world.  This implementation
	 * works for all applications and should not need to be overwritten.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */
	public void postUpdate(float dt) {
		// Add any objects created by actions
		while (!addQueue.isEmpty()) {
			addObject(addQueue.poll());
		}
		
		// Turn the physics engine crank.
		world.step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);

		// Garbage collect the deleted objects.
		// Note how we use the linked list nodes to delete O(1) in place.
		// This is O(n) without copying.
		Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
		while (iterator.hasNext()) {
			PooledList<Obstacle>.Entry entry = iterator.next();
			Obstacle obj = entry.getValue();
			if (obj.isRemoved()) {
				obj.deactivatePhysics(world);
				entry.remove();
			} else {
				// Note that update is called last!
				obj.update(dt);
			}
		}
		canvas.updateLights();
	}

	/**
	 * Draw the progress bar for the smog ammo.
	 */
	public void drawBar() {
		// Figure out how to place bar appropriately on the screen
		width = (int)(0.50f*canvas.getWidth())/2;
		centerY = canvas.getHeight() - statusBar.getHeight();
		BAR_Y = centerY;
		centerX = width/2 + BAR_PAD_X;
		BAR_X = centerX;
		heightY = canvas.getHeight();
		float scale = 0.5f;
		// Progress should be between 0.0f and 1.0f
		//progress = 0.25f;

		canvas.draw(statusBkgLeft,   Color.WHITE, centerX-width/2, centerY,
				scale*statusBkgLeft.getRegionWidth(), scale*statusBkgLeft.getRegionHeight());
		canvas.draw(statusBkgMiddle, Color.WHITE,centerX-width/2+scale*statusBkgLeft.getRegionWidth(), centerY,
				width-scale*(statusBkgRight.getRegionWidth()+statusBkgLeft.getRegionWidth()),
				scale*statusBkgMiddle.getRegionHeight());
		canvas.draw(statusBkgRight,  Color.WHITE,centerX+width/2-scale*statusBkgRight.getRegionWidth(), centerY,
				scale*statusBkgRight.getRegionWidth(), scale*statusBkgRight.getRegionHeight());

		if (progress > 0) {
			canvas.draw(statusFrgLeft,   Color.CYAN,centerX-width/2, centerY,
					scale*statusFrgLeft.getRegionWidth(), scale*statusFrgLeft.getRegionHeight());
			float span = (progress*2)*(width-scale*(statusFrgLeft.getRegionWidth()+statusFrgRight.getRegionWidth()))/2.0f;
			canvas.draw(statusFrgMiddle, Color.CYAN,centerX-width/2+scale*statusFrgLeft.getRegionWidth(), centerY,
					span, scale*statusFrgMiddle.getRegionHeight());
			canvas.draw(statusFrgRight,  Color.CYAN,centerX-width/2+scale*statusFrgLeft.getRegionWidth()+span, centerY,
					scale*statusFrgRight.getRegionWidth(), scale*statusFrgRight.getRegionHeight());
		}
	}
	
	/**
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself.  It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */
	public void draw(float dt) {
		canvas.clear();

		if (isActive())
		{
			canvas.begin(player.getX(), player.getY());
		}
		else {
			canvas.begin();
		}
		canvas.draw(backgroundTexture, Color.BROWN, 0, 0, canvas.getWidth(), canvas.getHeight());
		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}

		// END remove
		//drawBar();
		canvas.end();


		canvas.renderLights();

		//Top pass

		canvas.begin();
		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}

		purifiedAir.draw(canvas);
		toxicAir.draw(canvas);

		// Draw air bar
		airBar.draw(canvas);

		// Draw hearts
		for (int i = 0; i < heartArr.size; i++){
			heartArr.get(i).draw(canvas);
		}


		// Draw statics and the player in order of when they appear
//		for (Obstacle obj : staticsAndPlayer) {
//			obj.draw(canvas);
//		}


		// Get amount of ammo from weapon
//		for(Obstacle obj : objects) {
//			if obj.getType() == Obstacle
//		}
		String message = "ÆIR: ";
		// canvas.drawText(message, displayFontBar, BAR_X - (width/2) + 5, BAR_Y + 38);
		// Remove later, testing progress movement for now
		if (isIncrementing){
			progress += 0.01;
			if (progress > 1.0){
				isIncrementing = false;
			}
		}
		else{
			progress -= 0.01;
			if (progress < 0){
				isIncrementing = true;
			}
		}
		
		if (debug) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}

			weapon.draw(canvas);
			canvas.endDebug();
		}
		canvas.end();


		
		// Final message
		if (complete && !failed) {
			displayFont.setColor(Color.YELLOW);
			canvas.begin(); // DO NOT SCALE
			canvas.drawText("VICTORY!", displayFont, player.getX()-195, player.getY());

			canvas.drawText("Press 'R' to restart", displayFontSub, player.getX()-120, player.getY() - 100);
			canvas.end();
		} else if (failed) {
			displayFont.setColor(Color.RED);
			canvas.begin(); // DO NOT SCALE
			canvas.drawText("FAILURE!", displayFont, player.getX()-195, player.getY());
			canvas.drawText("Press 'R' to restart", displayFontSub, player.getX()-120, player.getY() - 100);

			canvas.end();
		}
	}

	/**
	 * Method to ensure that a sound asset is only played once.
	 *
	 * Every time you play a sound asset, it makes a new instance of that sound.
	 * If you play the sounds to close together, you will have overlapping copies.
	 * To prevent that, you must stop the sound before you play it again.  That
	 * is the purpose of this method.  It stops the current instance playing (if
	 * any) and then returns the id of the new instance for tracking.
	 *
	 * @param sound		The sound asset to play
	 * @param soundId	The previously playing sound instance
	 *
	 * @return the new sound instance for this asset.
	 */
	public long playSound(Sound sound, long soundId) {
		return playSound( sound, soundId, 1.0f );
	}


	/**
	 * Method to ensure that a sound asset is only played once.
	 *
	 * Every time you play a sound asset, it makes a new instance of that sound.
	 * If you play the sounds to close together, you will have overlapping copies.
	 * To prevent that, you must stop the sound before you play it again.  That
	 * is the purpose of this method.  It stops the current instance playing (if
	 * any) and then returns the id of the new instance for tracking.
	 *
	 * @param sound		The sound asset to play
	 * @param soundId	The previously playing sound instance
	 * @param volume	The sound volume
	 *
	 * @return the new sound instance for this asset.
	 */
	public long playSound(Sound sound, long soundId, float volume) {
		if (soundId != -1) {
			sound.stop( soundId );
		}
		return sound.play(volume);
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
		// IGNORE FOR NOW
	}

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
			if (preUpdate(delta)) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);
			}
			draw(delta);
		}
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

}