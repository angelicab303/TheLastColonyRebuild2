/*
 * GameplayController.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package com.mygdx.game;

import assets.AssetDirectory;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.EnemyControllers.EnemyController;
import com.mygdx.game.EnemyControllers.ScoutEnemyController;
import com.mygdx.game.Obstacles.*;
import com.mygdx.game.Obstacles.Enemies.*;
import com.badlogic.gdx.Screen;
import com.mygdx.game.UI.AirBar;
import com.mygdx.game.UI.Heart;
import obstacle.BoxObstacle;
import obstacle.Obstacle;
import util.*;

import java.util.Iterator;




/**
 * Gameplay specific controller for the platformer game.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop,
 * which
 * is much more scalable. However, we still want the assets themselves to be
 * static.
 * This is the purpose of our AssetState variable; it ensures that multiple
 * instances
 * place nicely with the static assets.
 */
public class GameplayController implements Screen {
	// *************************** Player, Enemy, and Survivor Textures
	// ***************************
	/** Texture assets for player avatar */
	private FilmStrip[][] playerDirectionTextures;
	/** Texture assets for survivor avatar */
	private FilmStrip[] survivorDirectionTextures;
	/** Texture asset for enemy avatar */
	private FilmStrip[][] enemyDirectionTextures;
	private Texture vineTextureVertical;
	private Texture vineTextureHorizontal;
	private Texture vineTextureLeftBottom;
	private Texture vineTextureLeftTop;
	private Texture vineTextureRightBottom;
	private Texture vineTextureRightTop;
	private Texture vineTextureHeadLeft;
	private Texture vineTextureHeadRight;
	private Texture vineTextureHeadDown;
	private Texture vineTextureHeadUp;
	private Texture vineTextureHeadLeftBottom;
	private Texture vineTextureHeadLeftTop;
	private Texture vineTextureHeadRightBottom;
	private Texture vineTextureHeadRightTop;
	private Texture vineTextureClosedLeft;
	private Texture vineTextureClosedRight;
	private Texture vineTextureClosedDown;
	private Texture vineTextureClosedUp;
	private Texture vineTextureClosedLeftBottom;
	private Texture vineTextureClosedLeftTop;
	private Texture vineTextureClosedRightBottom;
	private Texture vineTextureClosedRightTop;
	private Texture[] vineTextures;
	/** Texture asset for survivor avatar */
	private TextureRegion survivorTexture;
	/** Texture asset for interactable prompt in survivor avatar */
	private Texture survivorITexture;

	// *************************** Smog, Purified Air, and Air Bar Textures
	// ***************************
	/** Texture asset for the smog texture */
	private Texture smogTexture;
	private TextureRegion smogTexture2;
	/** Texture asset for the air bar texture */
	private Texture airBarTexture;
	/** Texture asset for the pure air texture */
	private Texture pureAirTexture;
	/** Texture asset for the toxic air texture */
	private Texture toxicAirTexture;
	/** Texture asset for the second smog texture */

	// *************************** Floor Textures ***************************
	private TextureRegion grassTexture;
	private TextureRegion dirtTexture;
	private TextureRegion dirtMushroomTexture;
	private TextureRegion rockTexture;
	private TextureRegion brickFloorTexture;
	private TextureRegion brickFloorCrackedTexture;
	private TextureRegion brickFloorCrackedTopTexture;

	// *************************** Wall Textures ***************************
	/** Texture asset for the cliff texture */
	private TextureRegion cliffTexture;
	/** Texture asset for the cliff texture */
	private TextureRegion cliffTexture2;
	private TextureRegion brickWallTexture;
	private TextureRegion brickWallTopOpenTexture;
	private TextureRegion brickWallSidesOpenTexture;
	private TextureRegion brickWallCrackedTexture;
	private TextureRegion brickWallTopTexture;
	private TextureRegion borderSmogTexture;

	// *************************** Caravan, Trees, and Mushroom Textures
	// ***************************
	/** Texture asset for the Caravan texture */
	private TextureRegion caravanTexture;
	/** Texture asset for the tree texture */
	private TextureRegion treeTexture;
	private TextureRegion treeTallTexture;
	private TextureRegion treeWideTexture;
	private TextureRegion treeBallTexture;
	private TextureRegion treeBallFadedTexture;
	/** Texture asset for the mushroom texture */
	private TextureRegion mushroomTexture;

	// *************************** UI Textures ***************************
	/** Texture asset for the full heart texture */
	private Texture fHeartTexture;
	/** Texture asset for the slashed heart texture */
	private Texture sHeartTexture;

	// *************************** END OF TEXTURES ***************************

	/** Texture for shrieker enemy */
	private Texture shriekerTexture;
	/** How zoomed in the camera is - for placing UI elements */
	private float cameraZoom;

	/** Enemy Controller */
	private Array<EnemyController> enemyControllers;

	/** The weapon fire sound. We only want to play once. */
	private Sound fireSound;
	private long fireId = -1;//


	/** The default sound volume */
	private float volume;

	// Physics objects for the game
	/** Physics constants for initialization */
	private JsonValue constants;

	private AssetDirectory directory;

	/** Reference to the player avatar */
	protected Player player;
	protected Weapon weapon;
	/** Reference to the caravan avatar */
	private Caravan caravan;
	// /** Reference to the smog bar asset */
	// private AirBar airBar;
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;
	private float SCALE = 0.1f;

	/** Size of each tile in pixels **/
	private int tileSize = 32;
	/** Offset for the tiles on the end of the screen **/
	private int tileOffset = tileSize / 2;
	/** Size of each smog tile in pixels **/
	private int smogTileSize = 16;
	/** Offset for the smog tiles on the end of the screen **/
	private int smogTileOffset = smogTileSize / 2;
	/** Tile Grid for storing whether there is an obstacle in the location **/
	private boolean[][] tileGrid;
	/** Smog Grid used for storing whether there is smog in this tile **/
	private boolean[][] smogGrid;
	/** Purified Air used for collisions with enemies **/
	private boolean[][] airGrid;

	/** Smog list **/
	private Array<Smog> smogArr;
	/** Enemy list */
	private Array<Enemy> enemyArr;

	/** ShriekerEnemy list */
	private Array<ShriekerEnemy> shriekerArr;
	/** Survivor list **/
	private Array<Survivor> survivorArr;
	/** List of all tree positions **/
	private Array<Vector2> treePos;
	/** survivor controller list **/
	private Array<SurvivorController> survivorControllers;

	/** Mark set to handle more sophisticated collision callbacks */
	// protected ObjectSet<Fixture> sensorFixtures;
	// Moved the masks to GameObstacle if you are looking for them -V
	int numRescued;
	/** Used for playtesting, player is invincible */
	private boolean isInvincible = false;
	private boolean pausing = false;
	private boolean unpausing = false;
	private boolean paused = false;
	private int curLevel = 0;
	private int maxLevels = 7;
	private int level;


	//************* From world controller ******************

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
	protected AssetDirectory assets;
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
	/** The width of the progress bar */
	private int width;
	/** The y-coordinate of the center of the progress bar */
	private int centerY;
	/** The x-coordinate of the center of the progress bar */
	private int centerX;
	/**
	 * The height of the canvas window (necessary since sprite origin != screen
	 * origin)
	 */
	private int heightY;
	/** The x position of the progress bar */
	private int BAR_X = 120;
	/** The y position of the progress bar */
	private int BAR_Y = 420;
	/** The amount of padding from right edge of screen */
	private int BAR_PAD_X = 15;
	/** The amount of padding from upper edge of screen */
	private int BAR_PAD_Y = 50;
	/**
	 * The conversion from pixels to box2d units. 1 box2d meter = 16 pixels.
	 * Use meter units when calculating physics and handling box2d bodies.
	 * Use pixel units when drawing to screen!
	 */
	protected final float PTM = 1.0f;

	/** Reference to the air bar asset */
	protected AirBar airBar;
	/** Heart list **/
	protected Array<Heart> heartArr;

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
	public static final float WORLD_STEP = 1 / 60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;

	/** Width of the game world in Box2d units */
	protected static final float DEFAULT_WIDTH = 32.0f;
	/** Height of the game world in Box2d units */
	protected static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down) */
	// WHO THE FRICK FORGOT TO TURN OFF GRAVITY YOU HAD ONE JOB
	protected static final float DEFAULT_GRAVITY = 0;

	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** All the objects in the world. */
	protected PooledList<Obstacle> objects = new PooledList<Obstacle>();
	/** All the movable objects in the world. */
	protected PooledList<Obstacle> movObjects = new PooledList<Obstacle>();
	/** All of the smog objects in the world **DO NOT COMBINE, NEEDED FOR CORRECT RENDERING */
	protected PooledList<Smog> smogs = new PooledList<Smog>();
	protected Array<FloorTile> floorArr = new Array<FloorTile>();
	/** Queue for adding objects */
	protected PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
	/**
	 * Array for the objects that need to be drawn in order of y value (for
	 * perspective)
	 **/
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
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates. The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param bounds  The game bounds in Box2d coordinates
	 * @param gravity The gravitational force on this Box2d world
	 */
	protected GameplayController(Rectangle bounds, Vector2 gravity) {

		world = new World(gravity, false);
		this.bounds = new Rectangle(bounds);
		this.scale = new Vector2(1, 1);
		complete = false;
		failed = false;
		debug = false;
		active = false;
		countdown = -1;
		setDebug(false);
	}

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public GameplayController(GameCanvas canvas) {
		this(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT), new Vector2(0, DEFAULT_GRAVITY));
		setComplete(false);
		setFailure(false);
		// world.setContactListener(this);
		collisionController = new CollisionController(world, canvas.getWidth(), canvas.getHeight());
		//
		// sensorFixtures = new ObjectSet<Fixture>();

		input = new InputController();

		canvas.createLights(world);

		enemyControllers = new Array<EnemyController>();
		survivorControllers = new Array<SurvivorController>();
//		tileGrid = new boolean[canvas.getWidth() / tileSize][canvas.getHeight() / tileSize];
		cameraZoom = 0.4f;
		numRescued = 0;

		level = 1;
	}

	/**
	 * Gather the assets for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {
		this.directory = directory;

		// Player, Enemy, and Survivor Textures
		survivorTexture = new TextureRegion(directory.getEntry("images:survivorSprite", Texture.class));
		survivorITexture = directory.getEntry("images:sInteract", Texture.class);

		// Smog, Purified Air, and Air Bar Textures
		mushroomTexture = new TextureRegion(directory.getEntry("images:mushroom", Texture.class));
		smogTexture = directory.getEntry("images:testSmog", Texture.class);
		toxicAirTexture = directory.getEntry("images:testSmog", Texture.class);
		pureAirTexture = directory.getEntry("images:weaponProjectile", Texture.class);
		smogTexture2 = new TextureRegion(directory.getEntry("images:smog2", Texture.class));
		airBarTexture = directory.getEntry("images:airBar", Texture.class);
		// pureAirTexture = new TextureRegion(directory.getEntry("images:smog1",
		// Texture.class));
		pureAirTexture = directory.getEntry("images:testSmog", Texture.class);

		// UI Textures
		fHeartTexture = directory.getEntry("images:fullHeart", Texture.class);
		sHeartTexture = directory.getEntry("images:slashedHeart", Texture.class);

		// Unnecessary atm?

		playerDirectionTextures = importPlayerFilmstrip();
		survivorDirectionTextures = importCharacterFilmstrip("survivorP");
		enemyDirectionTextures = importEnemyFilmstrips();
		vineTextureVertical = directory.getEntry("images:vineVertical", Texture.class);
		vineTextureHorizontal = directory.getEntry("images:vineHorizontal", Texture.class);
		vineTextureLeftBottom = directory.getEntry("images:vineBottomLeft", Texture.class);
		vineTextureLeftTop = directory.getEntry("images:vineTopLeft", Texture.class);
		vineTextureRightBottom = directory.getEntry("images:vineBottomRight", Texture.class);
		vineTextureRightTop = directory.getEntry("images:vineTopRight", Texture.class);
		vineTextureHeadLeft = directory.getEntry("images:vineStraightHeadLeft", Texture.class);
		vineTextureHeadRight = directory.getEntry("images:vineStraightHeadRight", Texture.class);
		vineTextureHeadDown = directory.getEntry("images:vineStraightHeadDown", Texture.class);
		vineTextureHeadUp = directory.getEntry("images:vineStraightHeadUp", Texture.class);
		vineTextureHeadLeftBottom = directory.getEntry("images:vineCornerHeadLeftDown", Texture.class);
		vineTextureHeadLeftTop = directory.getEntry("images:vineCornerHeadLeftUp", Texture.class);
		vineTextureHeadRightBottom = directory.getEntry("images:vineCornerHeadRightDown", Texture.class);
		vineTextureHeadRightTop = directory.getEntry("images:vineCornerHeadRightUp", Texture.class);

		vineTextureClosedLeft = directory.getEntry("images:vineStraightClosedLeft", Texture.class);
		vineTextureClosedRight = directory.getEntry("images:vineStraightClosedRight", Texture.class);
		vineTextureClosedDown = directory.getEntry("images:vineStraightClosedDown", Texture.class);
		vineTextureClosedUp = directory.getEntry("images:vineStraightClosedUp", Texture.class);
		vineTextureClosedLeftBottom = directory.getEntry("images:vineCornerClosedLeftDown", Texture.class);
		vineTextureClosedLeftTop = directory.getEntry("images:vineCornerClosedLeftUp", Texture.class);
		vineTextureClosedRightBottom = directory.getEntry("images:vineCornerClosedRightDown", Texture.class);
		vineTextureClosedRightTop = directory.getEntry("images:vineCornerClosedRightUp", Texture.class);

		vineTextures = new Texture[22];
		vineTextures[0] = vineTextureVertical;
		vineTextures[1] = vineTextureHorizontal;
		vineTextures[2] = vineTextureLeftBottom;
		vineTextures[3] = vineTextureLeftTop;
		vineTextures[4] = vineTextureRightBottom;
		vineTextures[5] = vineTextureRightTop;
		vineTextures[6] = vineTextureHeadLeft;
		vineTextures[7] = vineTextureHeadRight;
		vineTextures[8] = vineTextureHeadDown;
		vineTextures[9] = vineTextureHeadUp;
		vineTextures[10] = vineTextureHeadLeftBottom;
		vineTextures[11] = vineTextureHeadLeftTop;
		vineTextures[12] = vineTextureHeadRightBottom;
		vineTextures[13] = vineTextureHeadRightTop;
		vineTextures[14] = vineTextureClosedLeft;
		vineTextures[15] = vineTextureClosedRight;
		vineTextures[16] = vineTextureClosedDown;
		vineTextures[17] = vineTextureClosedUp;
		vineTextures[18] = vineTextureClosedLeftTop;
		vineTextures[19] = vineTextureClosedLeftBottom;
		vineTextures[20] = vineTextureClosedRightTop;
		vineTextures[21] = vineTextureClosedRightBottom;

		fireSound = directory.getEntry("platform:pew", Sound.class);

		displayFont = directory.getEntry("shared:retro", BitmapFont.class);
		displayFontSub = directory.getEntry("shared:retroSub", BitmapFont.class);
		displayFontInteract = directory.getEntry("shared:light", BitmapFont.class);

		constants = directory.getEntry("platform:constants", JsonValue.class);
	}

	/**
	 * Returns a 2D array of all player filmstrips.
	 * Each entry represents the filmstrips for the direction of the player
	 *
	 * Examples:
	 * playerFilmStrip[0][1] = down direction for idle animation
	 * playerFilmStrip[0][0] = up direction for idle animation
	 * playerFilmStrip[1][0] = up direction for movement animation
	 *
	 * @return 2D array of all filmstrips for the player
	 */
	private FilmStrip[][] importPlayerFilmstrip(){
		FilmStrip[][] playerFilmStrip = new FilmStrip[4][4];
		//String[] directions = {"Up", "Down", "Right", "Left"};
		String[] actions = {"Movement", "Idle", "IdleAttack", "WalkAttack"};
		for (int i = 0; i < 4; i++){
			FilmStrip up = directory.getEntry("images:player" + actions[i] + "Up.fire", FilmStrip.class );
			FilmStrip down = directory.getEntry("images:player" + actions[i] + "Down.fire", FilmStrip.class );
			FilmStrip right = directory.getEntry("images:player" + actions[i] + "Right.fire", FilmStrip.class );
			FilmStrip left = directory.getEntry("images:player" + actions[i] + "Left.fire", FilmStrip.class );
			playerFilmStrip[i] = new FilmStrip[] {up, down, right, left};
		}
		return playerFilmStrip;
	}
	private FilmStrip[] importCharacterFilmstrip(String str){
		FilmStrip up = directory.getEntry("images:" + str + "Up.fire", FilmStrip.class );
		FilmStrip down = directory.getEntry("images:" + str + "Down.fire", FilmStrip.class );
		FilmStrip right = directory.getEntry("images:" + str + "Right.fire", FilmStrip.class );
		FilmStrip left = directory.getEntry("images:" + str + "Left.fire", FilmStrip.class );
		FilmStrip idle = directory.getEntry("images:" + str + "Idle.fire", FilmStrip.class );
		return new FilmStrip[] {up, down, right, left, idle };
	}

	/**
	 * Returns a 2D array of all enemy filmstrips to be passed into the json loader.
	 * [0]: Shrieker enemy
	 * [1]: Floating enemy
	 * [2]: Chaser enemy
	 * [3]: Scout enemy
	 * @return 2D array of enemy filmstrips
	 */
	private FilmStrip[][] importEnemyFilmstrips(){
		FilmStrip[][] enemyStrips = new FilmStrip[4][5];

		FilmStrip shriekIdle = directory.getEntry("images:shriekerIdle.fire", FilmStrip.class );
		FilmStrip shriekShriek = directory.getEntry("images:shriekerShriek.fire", FilmStrip.class );
		System.out.println("Two assets loaded");
		FilmStrip shriekTransform = directory.getEntry("images:shriekerTransform.fire", FilmStrip.class );
		enemyStrips[0] = new FilmStrip[] {shriekIdle, shriekShriek, shriekTransform};

		String[] enemyNames = new String[] {"maskEnemy"};

		for (int i = 1; i < 2; i++){
			FilmStrip up = directory.getEntry("images:" + enemyNames[i-1] + "Up.fire", FilmStrip.class );
			FilmStrip down = directory.getEntry("images:" + enemyNames[i-1] + "Down.fire", FilmStrip.class );
			FilmStrip right = directory.getEntry("images:" + enemyNames[i-1] + "Right.fire", FilmStrip.class );
			FilmStrip left = directory.getEntry("images:" + enemyNames[i-1] + "Left.fire", FilmStrip.class );
			FilmStrip idle = directory.getEntry("images:" + enemyNames[i-1] + "Idle.fire", FilmStrip.class );
			enemyStrips[i] = new FilmStrip[] {up, down, right, left, idle };
		}
		return enemyStrips;
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity());

		for (Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		for (Obstacle obj : smogs) {
			obj.deactivatePhysics(world);
		}
		for (Obstacle obj : movObjects) {
			obj.deactivatePhysics(world);
		}
		enemyControllers.clear();
		survivorControllers.clear();
		smogs.clear();
		objects.clear();
		movObjects.clear();
		addQueue.clear();
		canvas.disposeLights();
		world.dispose();

		world = new World(gravity, false);
		collisionController.setContactListener(world);
		canvas.createLights(world);
		setComplete(false);
		setFailure(false);
		// System.out.println(1);
		populateLevel(curLevel);
		numRescued = 0;


		// System.out.println(2);
	}

	public void reset(int level){
		this.level = level;
		reset();
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void nextLevel() {
		if (curLevel >= maxLevels-1) {
			curLevel = -1;
		}
		curLevel++;
		reset(curLevel);
	}

	private void populateLevel(int level) {
		// Populate the level using the JSON Reader

		staticsAndPlayer = new Array<Obstacle>();

		// *************************** STATIC OBSTACLES ***************************
		int[] startingBox = { 6, 4 };

		// Arrays used to find tiles to place smog at
		tileGrid = new boolean[canvas.getWidth() / tileSize][canvas.getHeight() / tileSize];
		boolean[][] smogTiles = new boolean[canvas.getWidth() / tileSize][canvas.getHeight() / tileSize];
		boolean[][] smogLocations = new boolean[canvas.getWidth() / smogTileSize][canvas.getHeight() / smogTileSize];
		smogGrid = new boolean[canvas.getWidth() * smogTileSize][canvas.getHeight() * smogTileSize];


		// Testing tiles array:
		// System.out.println("Canvas width: " + canvas.getWidth() + "\tTile Size: " +
		// tileSize + "\tNumTiles: " + canvas.getWidth() / tileSize);
		// System.out.println("First element of tiles: " + tiles[0][0]);

		// TO DO: update visuals for purified smog
		purifiedAir = new PurifiedQueue(pureAirTexture, world, SCALE);
		toxicAir = new ToxicQueue(toxicAirTexture, world, SCALE);

		// Setting the size of the tiles
		Shadow.setSize(32f);

//		canvas.setSize(32 * tileSize, 26 * tileSize);
//		System.out.println("Width: " + canvas.getWidth() + "\t\tHeight: " + canvas.getHeight());
		// Here we will instantiate the objects in the level using the JSONLevelReader.
		JSONLevelReader reader = new JSONLevelReader(directory, bounds, world, level, canvas.camera, input,
				objects, movObjects, floorArr, SCALE, tileGrid, smogTiles, smogGrid, tileSize, tileOffset, smogTileSize, smogTileOffset,
				playerDirectionTextures, survivorDirectionTextures, enemyDirectionTextures, toxicAir, survivorITexture,
				displayFontInteract, fHeartTexture, player, weapon);

//		if (caravan.getX() < 400f) {
//			int i = 0;
//		}
		// System.out.println("Canvas width: " + canvas.getWidth() + "\tTile Size: " +
		// tileSize + "\tNumTiles: " + canvas.getWidth() / tileSize);
		// System.out.println("First element of tiles: " + tiles[0][0]);



		objects = reader.getObjects();
		movObjects = reader.getMovObjects();
		floorArr = reader.getFloorArr();
		tileGrid = reader.getTileGrid();
		smogTiles = reader.getSmogTiles();
		smogGrid = reader.getSmogGrid();
//		canvas.camera = reader.getCamera();
		caravan = reader.getCaravan();
		player = reader.getPlayer();
		weapon = reader.getWeapon();
		survivorArr = reader.getSurvivors();
		enemyArr = reader.getEnemies();
		survivorControllers = reader.getSurvivorControllers();
		enemyControllers = reader.getEnemyControllers();

//		for (int i = 0; i < tileGrid.length; i++)
//		{
//			for (int j = 0; j < tileGrid[0].length; j++)
//			{
//				if (tileGrid[i][j])
//				{
//					System.out.println(i + " " + j);
//				}
//			}
//		}

		if (isInvincible) {
			player.setHealth(10000);
		}
		// Gives ammo in debug mode
		if (isDebug()) {
			weapon.setNumAmmo(1000);
		}

		// *************************** SMOG OBSTACLES ***************************

		// Starting Area:
		// for (int i = playerLocation[0] - startingBox[0]; i < playerLocation[0] +
		// startingBox[0]; i++) {
		// for (int j = playerLocation[1] - startingBox[1]; j < playerLocation[1] +
		// startingBox[1]; j++) {
		// tiles[i][j] = true;
		// }
		// }

		// Instantiate the smog array:
		smogArr = new Array<Smog>();
		// Determine where the smog is and log it in smogLocations:
		for (int i = 0; i < smogTiles.length; i++) {
			for (int j = 0; j < smogTiles[0].length; j++) {
				if (smogTiles[i][j]) {
					smogLocations[2 * i][2 * j] = true;
					smogLocations[2 * i + 1][2 * j] = true;
					smogLocations[2 * i][2 * j + 1] = true;
					smogLocations[2 * i + 1][2 * j + 1] = true;
				}
			}
		}

		// Instantiate smog at tile locations with primary and secondary grid offsets:
		Smog smogT;
		Smog smogTO; // Smog Temp Offset
		// For categories and masking:
		int column = smogLocations[0].length;
		Smog[] tempSmogArray = new Smog[column];
		Smog[] tempSmogArrayO= new Smog[column];


		for (int i = 0; i < column; i++) {
			for (int j = 0; j < column; j++) {
				if (smogLocations[i][j] && Vector2.dst(i * smogTileSize + smogTileOffset, j * smogTileSize + smogTileOffset, player.getX(), player.getY()) > 90) {
					// Primary Grid
					// Later get data from json file
					float maxFrame = 4;
					float minFrame = 0;
					float frameNum = (float) (Math.random() * (maxFrame - minFrame + 1) + minFrame);
					smogT = new Smog(i * smogTileSize + smogTileOffset, j * smogTileSize + smogTileOffset, smogTexture, frameNum,
							SCALE);
					smogT.setAwake(true);
					smogArr.add(smogT);
					addSmog(smogT);

					if( j > 0 ) {
						smogT.addNeighboringSmog(tempSmogArray[j - 1], smogT);
					}

					smogT.addNeighboringSmog(tempSmogArray[j], smogT);
					smogT.addNeighboringSmog(tempSmogArrayO[j], smogT);

					if (j < column - 1){
						smogT.addNeighboringSmog(tempSmogArrayO[j + 1], smogT);
					}

					tempSmogArray[j] = smogT;
				}

				else {
					tempSmogArray[j] = null;
				}
			}

			for (int j = 0; j < smogLocations[0].length; j++) {
				if (smogLocations[i][j] && Vector2.dst(i * smogTileSize + smogTileOffset, j * smogTileSize + smogTileOffset, player.getX(), player.getY()) > 90) {

					// Secondary Grid
					float maxFrame = 4;
					float minFrame = 0;
					float frameNum = (float) (Math.random() * (maxFrame - minFrame + 1) + minFrame);
					smogTO = new Smog(i * smogTileSize + smogTileSize, j * smogTileSize + smogTileSize, smogTexture, frameNum,
							SCALE);
					smogTO.setAwake(true);
					smogArr.add(smogTO);
					addSmog(smogTO);

					if( j > 0 ){
						smogTO.addNeighboringSmog(tempSmogArray[j-1], smogTO);
						smogTO.addNeighboringSmog(tempSmogArrayO[j-1], smogTO);
					}

					smogTO.addNeighboringSmog(tempSmogArray[j], smogTO);
					smogTO.addNeighboringSmog(tempSmogArrayO[j], smogTO);

					tempSmogArrayO[j] = smogTO;
				}
				else {
					tempSmogArrayO[j] = null;
				}
			}


		}
		airBar = new AirBar(airBarTexture, weapon.getMaxNumAmmo(), weapon.getNumAmmo(), canvas);

		// Hearts
		int numLives = player.getHealth();
		heartArr = new Array<Heart>(numLives);
		float heartX = canvas.camera.position.x + (canvas.getWidth() * cameraZoom) / 2.0f - (30.0f * cameraZoom);
		float heartY = canvas.camera.position.y + (canvas.getHeight() * cameraZoom) / 2.0f - (30.0f * cameraZoom);
		float spacing = 0.0f;

		for (int i = 0; i < numLives; i++) {
			if (i > 0) {
				spacing += 13.0f;
			}
			Heart tempHeart = new Heart(fHeartTexture, heartX, heartY, spacing);
			heartArr.add(tempHeart);
		}
	}

	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode. If not, the update proceeds
	 * normally.
	 *
	 * @param dt Number of seconds since last animation frame
	 *
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		input.readInput();
		// if (listener == null) {
		// return true;
		// }

		// // Toggle debug
		if (input.didDebug()) {
			debug = !debug;
		}

		if (player.getHealth() <= 0) {
			setFailure(true);
		}

		// Handle resets
		if (input.didReset()) {
			reset();
		}

		// Handle NextLevel input
		if (input.isNextLevel()) {
			nextLevel();
		}

		return true;
	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class
	 * WorldController.
	 * This method is called after input is read, but before collisions are
	 * resolved.
	 * The very last thing that it should do is apply forces to the appropriate
	 * objects.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void update(float dt) {
		// Read input
		input.readInput();

		if (input.didPause()) {
			if (!paused && !unpausing) {
				paused = true;
				pausing = true;
				for (int i = 0; i < enemyArr.size; i++) {
					enemyArr.get(i).setBodyType(BodyDef.BodyType.StaticBody);
				}
				for (int i = 0; i < survivorArr.size; i++) {
					survivorArr.get(i).setActive(false);
				}
				for (int i = 0; i < purifiedAir.getQueue().length; i++) {
					purifiedAir.getQueue()[i].setActive(false);
				}
				for (int i = 0; i < toxicAir.getQueue().length; i++) {
					toxicAir.getQueue()[i].setActive(false);
				}
				return;
			} else if (paused && !pausing) {
				paused = false;
				unpausing = true;
				for (int i = 0; i < enemyArr.size; i++) {
					enemyArr.get(i).setBodyType(BodyDef.BodyType.DynamicBody);
				}
				for (int i = 0; i < survivorArr.size; i++) {
					survivorArr.get(i).setActive(true);
				}
				for (int i = 0; i < purifiedAir.getQueue().length; i++) {
					purifiedAir.getQueue()[i].setActive(true);
				}
				for (int i = 0; i < toxicAir.getQueue().length; i++) {
					toxicAir.getQueue()[i].setActive(true);
				}
			}
		} else {
			pausing = false;
			unpausing = false;
			if (paused) {
				return;
			}
		}

		if (input.isNextLevel()) {
			reset(level + 1);
		}

		if (input.didPressAbsorb()) {
			weapon.setAbsorbing(true);
		} else {
			weapon.setAbsorbing(false);
		}

		if (input.didPressFire()) {
			weapon.setFiring(true);
		} else {
			weapon.setFiring(false);
		}

		// Update player and weapon position
		player.update();
		if (player.getX() < 20) {
			player.setPosition(20, player.getBody().getPosition().y);
		}
		if (player.getX() >= tileGrid.length * tileSize - 20) {
			player.setPosition(tileGrid.length * tileSize - 20, player.getBody().getPosition().y);
		}
		if (player.getY() < 20) {
			player.setPosition(player.getBody().getPosition().x, 20);
		}
		if (player.getY() >= tileGrid[0].length * tileSize - 20) {
			player.setPosition(player.getBody().getPosition().x, tileGrid[0].length * tileSize - 20);
		}

		// Update UI elements
		airBar.update(weapon.getNumAmmo());

		weapon.update(player.getPosition(), canvas.unproject(input.getMousePos()), input.getShootDir());
		// Check if the weapon is firing

		if (weapon.fire()) {
			purifiedAir.attack(weapon.getBullets(), weapon.getPosition(), weapon.getImpulses());
		}
		purifiedAir.update();
		toxicAir.update();

		// Process Collisions
		collisionController.update(world, player, weapon);
		// This detects and resolves all collisions between the weapon sensor and any
		// collided smog tiles
		// if (weapon.isFiring()) {
		// collisionController.processCollisions(player, weapon, smogArr);
		// }
		// collisionController.processCollisions(player,weapon,smogArr);

		// Update smog animations
		for (Smog obj : smogs) {
			obj.update();
			//Trying out recycling
			/*
			if (obj.isFaded()) {
				obj.markRemoved(true);
			}

			 */
		}
		/*
		 * for (Cliff obj : cliffArr) {
		 * obj.update();
		 * }
		 *
		 *
		 */
		// Update enemy positions and states
		for (int i = 0; i < enemyArr.size; i++) {
			enemyArr.get(i).update(enemyControllers.get(i).getAction());
			if (enemyArr.get(i).getX() < 20) {
				enemyArr.get(i).setPosition(20, enemyArr.get(i).getBody().getPosition().y);
			}
			if (enemyArr.get(i).getX() >= tileGrid.length * tileSize - 20) {
				enemyArr.get(i).setPosition(tileGrid.length * tileSize - 20, enemyArr.get(i).getBody().getPosition().y);
			}
			if (enemyArr.get(i).getY() < 20) {
				enemyArr.get(i).setPosition(enemyArr.get(i).getBody().getPosition().x, 20);
			}
			if (enemyArr.get(i).getY() >= tileGrid[0].length * tileSize - 20) {
				enemyArr.get(i).setPosition(enemyArr.get(i).getBody().getPosition().x, tileGrid[0].length * tileSize - 20);
			}
		}

		// This section will be handled by collisionController in the future with the
		// exception of obj.update(..)s and
		// removing objects from arrays once they are deactivated:
		// -------------------------------------------------------------------------------------------------------------
		// Update survivor positions and states
		for (int i = 0; i < survivorArr.size; i++) {
			//System.out.println(caravan.getCurrentCapacity() + " " + caravan.getMaxCapacity());
			if (!survivorArr.get(i).isRescued()) {
				// This will be handled by collisionController in the future
				survivorArr.get(i).update(survivorControllers.get(i).getAction());
				if (survivorArr.get(i).getX() < 20) {
					survivorArr.get(i).setPosition(20, survivorArr.get(i).getBody().getPosition().y);
				}
				if (survivorArr.get(i).getX() >= tileGrid.length * tileSize - 20) {
					survivorArr.get(i).setPosition(tileGrid.length * tileSize - 20, survivorArr.get(i).getBody().getPosition().y);
				}
				if (survivorArr.get(i).getY() < 20) {
					survivorArr.get(i).setPosition(survivorArr.get(i).getBody().getPosition().x, 20);
				}
				if (survivorArr.get(i).getY() >= tileGrid[0].length * tileSize - 20) {
					survivorArr.get(i).setPosition(survivorArr.get(i).getBody().getPosition().x, tileGrid[0].length * tileSize - 20);
				}
				survivorArr.get(i).update();
				if (survivorArr.get(i).isInteractable() && input.didCollectSurvivor()) {
					survivorArr.get(i).setInteractable(false);
					if (!survivorArr.get(i).isFollowing()) {
						player.addToFollowing(survivorArr.get(i));
					}
					survivorArr.get(i).follow();
				}
				if (!survivorArr.get(i).isAlive()) {
					player.removeFromFollowing(survivorArr.get(i));
					setFailure(true);
				}
				if (survivorArr.get(i).isRescued()) {
					player.removeFromFollowing(survivorArr.get(i));
					survivorArr.get(i).deactivatePhysics(world);
					//survivorArr.removeIndex(i);
					numRescued++;
					caravan.incrCap();
					caravan.setInteractable(false);
				}
			}

		}
		caravan.update();
		// Update caravan state
		if (caravan.getBody().getFixtureList().first().testPoint(player.getPosition())) {
			caravan.setInteractable(true);
		}
		else {
			caravan.setInteractable(false);
		}
//		if (caravan.isInteractable() && input.didDropSurvivors()) {
		if (caravan.getCurrentCapacity() == caravan.getMaxCapacity()) {
			setComplete(true);
		}
//			for (int i = 0; i < survivorArr.size; i++) {
//				if (survivorArr.get(i).isFollowing()) {
//					survivorArr.get(i).rescue();
//					player.removeFromFollowing(survivorArr.get(i));
//					survivorArr.get(i).deactivatePhysics(world);
//					survivorArr.removeIndex(i);
//					numRescued++;
//					caravan.incrCap();
//					caravan.setInteractable(false);
//				}
//			}
//			caravan.setInteractable(false);
//		}
		// -------------------------------------------------------------------------------------------------------------

		// Update Ammo Progress Bar
		// Update SmogBar

		progress = weapon.getNumAmmo() / (Weapon.MAX_AMMO_CAPACITY * 1.0f);

		// Sort the player into the list of statics and player
		// for (int i = 0; i < staticsAndPlayer.size; i++) {
		// if (staticsAndPlayer.get(i).getType() == GameObstacle.ObstacleType.PLAYER) {
		// staticsAndPlayer.removeIndex(i);
		// break;
		// }
		// }
		// Removed player, now insert into correct position
		boolean insertedPlayer = false;
		for (int i = 0; i < staticsAndPlayer.size; i++) {
			if (player.getPosition().y > staticsAndPlayer.get(i).getY()) {
				staticsAndPlayer.insert(i, player);
				break;
			}
		}
		if (!insertedPlayer) {
			staticsAndPlayer.add(player);
		}

		// Update hearts
		for (int i = player.getMaxHealth() - 1; i >= 0; i--) {
			if (player.getHealth() <= i) {
				heartArr.get(i).setTexture(sHeartTexture);
			}
		}

	}

	//*** from worldcontroller **//



	/**
	 * Returns true if debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @return true if debug mode is active.
	 */
	public boolean isDebug() {
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
	public boolean isComplete() {
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
	public boolean isFailure() {
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
	public boolean isActive() {
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
	 * The canvas is shared across all controllers. Setting this value will compute
	 * the drawing scale from the canvas size.
	 *
	 * @param canvas the canvas associated with this controller
	 */
	public void setCanvas(GameCanvas canvas) {
		this.canvas = canvas;
		this.scale.x = canvas.getWidth() / bounds.getWidth();
		this.scale.y = canvas.getHeight() / bounds.getHeight();
	}

	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		for (Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		for (Obstacle obj : smogs) {
			obj.deactivatePhysics(world);
		}
		smogs.clear();
		objects.clear();
		movObjects.clear();
		addQueue.clear();
		world.dispose();
		smogs = null;
		objects = null;
		movObjects = null;
		addQueue = null;
		bounds = null;
		scale = null;
		world = null;
		canvas = null;
	}

	/**
	 *
	 * Adds a physics object in to the insertion queue.
	 *
	 * Objects on the queue are added just before collision processing. We do this
	 * to
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
	public void addObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);
		obj.activatePhysics(world);
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * param obj The object to add
	 */
	public void addSmog(Smog obj) {
		assert inBounds(obj) : "Object is not in bounds";
		smogs.add(obj);
		obj.activatePhysics(world);
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
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x + bounds.width);
		boolean vert = (bounds.y <= obj.getY() && obj.getY() <= bounds.y + bounds.height);
		return horiz && vert;
	}




	/**
	 * Processes physics
	 *
	 * Once the update phase is over, but before we draw, we are ready to handle
	 * physics. The primary method is the step() method in world. This
	 * implementation
	 * works for all applications and should not need to be overwritten.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void postUpdate(float dt) {
		// Add any objects created by actions
		while (!addQueue.isEmpty()) {
			addObject(addQueue.poll());
		}

		// Turn the physics engine crank.
		world.step(WORLD_STEP, WORLD_VELOC, WORLD_POSIT);

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
		iterator = movObjects.entryIterator();
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
		Iterator<PooledList<Smog>.Entry>iterator2 = smogs.entryIterator();
		while (iterator2.hasNext()) {
			PooledList<Smog>.Entry entry = iterator2.next();
			Smog obj = entry.getValue();
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
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself. It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void draw(float dt) {
//		System.out.println("Started Drawing");
		canvas.clear();

		if (isActive()) {
			canvas.begin(player.getX(), player.getY(), tileGrid.length * tileSize, tileGrid[0].length * tileSize);
		} else {
			canvas.begin();
		}
//		System.out.println("Started canvas");
		//canvas.draw(backgroundTexture, Color.BROWN, 0, 0, canvas.getWidth(), canvas.getHeight());

		for (FloorTile flr : floorArr) {
			flr.draw(canvas);
		}

		for(Obstacle obj : movObjects){
			if(obj.getBehind()){
				obj.draw(canvas);
			}
		}

		for (Obstacle obj : objects) {
			obj.draw(canvas);
		}

		for(Obstacle obj : movObjects){
			if(!obj.getBehind()){
				obj.draw(canvas);
			}
		}

		for (Obstacle obj : smogs){
			obj.draw(canvas);
		}



		// END remove
		// drawBar();
		canvas.end();


		canvas.renderLights();

		// Top pass

		canvas.begin();

		purifiedAir.draw(canvas);
		toxicAir.draw(canvas);

		// Draw air bar
		airBar.draw(canvas);

		// Draw hearts
		for (int i = 0; i < heartArr.size; i++) {
			heartArr.get(i).draw(canvas);
		}

		String message = "ÆIR: ";
		// canvas.drawText(message, displayFontBar, BAR_X - (width/2) + 5, BAR_Y + 38);
		// Remove later, testing progress movement for now
		if (isIncrementing) {
			progress += 0.01;
			if (progress > 1.0) {
				isIncrementing = false;
			}
		} else {
			progress -= 0.01;
			if (progress < 0) {
				isIncrementing = true;
			}
		}

		if (debug) {
			canvas.beginDebug();
			for (Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			for (Obstacle obj : movObjects) {
				obj.drawDebug(canvas);
			}
			for (Obstacle obj : smogs) {
				obj.drawDebug(canvas);
			}
			toxicAir.drawDebug(canvas);

			weapon.draw(canvas);
			canvas.endDebug();
		}
		if (paused) {
			displayFont.setColor(Color.GRAY);
			canvas.drawText("PAUSED", displayFont, canvas.camera.position.x - 150f, canvas.camera.position.y + 25f);
		}
		canvas.end();

		// Final message
		if (complete && !failed) {
			displayFont.setColor(Color.YELLOW);
			canvas.begin(); // DO NOT SCALE
			canvas.drawText("VICTORY!", displayFont, canvas.camera.position.x - 195, canvas.camera.position.y);

			canvas.drawText("Press 'N' for next level", displayFontSub, canvas.camera.position.x - 170, canvas.camera.position.y - 100);
			canvas.end();
		} else if (failed) {
			displayFont.setColor(Color.RED);
			canvas.begin(); // DO NOT SCALE
			canvas.drawText("FAILURE!", displayFont, canvas.camera.position.x - 195, canvas.camera.position.y);
			canvas.drawText("Press 'R' to restart", displayFontSub, canvas.camera.position.x - 120, canvas.camera.position.y - 100);

			canvas.end();
		}
//		System.out.println("Finished first draw");
	}

	/**
	 * Method to ensure that a sound asset is only played once.
	 *
	 * Every time you play a sound asset, it makes a new instance of that sound.
	 * If you play the sounds to close together, you will have overlapping copies.
	 * To prevent that, you must stop the sound before you play it again. That
	 * is the purpose of this method. It stops the current instance playing (if
	 * any) and then returns the id of the new instance for tracking.
	 *
	 * @param sound   The sound asset to play
	 * @param soundId The previously playing sound instance
	 *
	 * @return the new sound instance for this asset.
	 */
	public long playSound(Sound sound, long soundId) {
		return playSound(sound, soundId, 1.0f);
	}

	/**
	 * Method to ensure that a sound asset is only played once.
	 *
	 * Every time you play a sound asset, it makes a new instance of that sound.
	 * If you play the sounds to close together, you will have overlapping copies.
	 * To prevent that, you must stop the sound before you play it again. That
	 * is the purpose of this method. It stops the current instance playing (if
	 * any) and then returns the id of the new instance for tracking.
	 *
	 * @param sound   The sound asset to play
	 * @param soundId The previously playing sound instance
	 * @param volume  The sound volume
	 *
	 * @return the new sound instance for this asset.
	 */
	public long playSound(Sound sound, long soundId, float volume) {
		if (soundId != -1) {
			sound.stop(soundId);
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
	 * We defer to the other methods update() and draw(). However, it is VERY
	 * important
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