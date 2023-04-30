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
import box2dLight.RayHandler;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.Obstacles.*;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.Obstacles.Enemies.FloatingEnemy;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;

import com.mygdx.game.UI.AirBar;
import com.mygdx.game.UI.Heart;
import obstacle.BoxObstacle;
import obstacle.Obstacle;
import org.w3c.dom.Text;
import com.mygdx.game.EnemyControllers.*;
import com.mygdx.game.Obstacles.Enemies.*;

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
public class GameplayController extends WorldController {
	// *************************** Player, Enemy, and Survivor Textures
	// ***************************
	/** Texture assets for player avatar */
	private Texture playerTextureUp;
	private Texture playerTextureRight;
	private Texture playerTextureLeft;
	private Texture playerTextureDown;
	private Texture[] playerDirectionTextures;
	private Texture playerTextureIdle;
	/** Texture asset for enemy avatar */
	private Texture enemyTextureUp;
	private Texture enemyTextureRight;
	private Texture enemyTextureLeft;
	private Texture enemyTextureDown;

	private Texture[] enemyDirectionTextures;
	private Texture enemyTextureIdle;
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
	// private Player player;
	private Weapon weapon;
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
	private int maxLevels = 6;
	private int level;

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public GameplayController(GameCanvas canvas) {
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_GRAVITY);
		setDebug(false);
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
		tileGrid = new boolean[canvas.getWidth() / tileSize][canvas.getHeight() / tileSize];
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
		playerTextureUp = directory.getEntry("images:playerUp", Texture.class);
		playerTextureDown = directory.getEntry("images:playerDown", Texture.class);
		playerTextureRight = directory.getEntry("images:playerRight", Texture.class);
		playerTextureLeft = directory.getEntry("images:playerLeft", Texture.class);
		playerTextureIdle = directory.getEntry("images:playerIdle", Texture.class);
		enemyTextureUp = directory.getEntry("images:maskEnemyUp", Texture.class);
		enemyTextureDown = directory.getEntry("images:maskEnemyDown", Texture.class);
		enemyTextureRight = directory.getEntry("images:maskEnemyRight", Texture.class);
		enemyTextureLeft = directory.getEntry("images:maskEnemyLeft", Texture.class);
		enemyTextureIdle = directory.getEntry("images:maskEnemyIdle", Texture.class);
		shriekerTexture = directory.getEntry("images:plantEnemy", Texture.class);
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

		// Floor Textures
		grassTexture = new TextureRegion(directory.getEntry("tiles:4a_grass1", Texture.class));
		dirtTexture = new TextureRegion(directory.getEntry("tiles:4c_dirt1", Texture.class));
		dirtMushroomTexture = new TextureRegion(directory.getEntry("tiles:4c_dirt2", Texture.class));
		rockTexture = new TextureRegion(directory.getEntry("tiles:4b_rocks", Texture.class));
		brickFloorTexture = new TextureRegion(directory.getEntry("tiles:4d_brick1", Texture.class));
		brickFloorCrackedTexture = new TextureRegion(directory.getEntry("tiles:4d_brick3", Texture.class));
		brickFloorCrackedTopTexture = new TextureRegion(directory.getEntry("tiles:4d_brick2", Texture.class));

		// Wall Textures
		cliffTexture = new TextureRegion(directory.getEntry("images:cliff3", Texture.class));
		cliffTexture2 = new TextureRegion(directory.getEntry("images:cliff4", Texture.class));
		brickWallTexture = new TextureRegion(directory.getEntry("tiles:5a_brick1", Texture.class));
		brickWallTopOpenTexture = new TextureRegion(directory.getEntry("tiles:5a_brick4", Texture.class));
		brickWallSidesOpenTexture = new TextureRegion(directory.getEntry("tiles:5a_brick3", Texture.class));
		brickWallCrackedTexture = new TextureRegion(directory.getEntry("tiles:5a_brick2", Texture.class));
		brickWallTopTexture = new TextureRegion(directory.getEntry("tiles:5a_brick5", Texture.class));
		borderSmogTexture = new TextureRegion(directory.getEntry("tiles:5c_borderSmog", Texture.class));

		// Caravan, Trees, and Mushroom Textures
		caravanTexture = new TextureRegion(directory.getEntry("tiles:0_caravan", Texture.class));
		treeTexture = new TextureRegion(directory.getEntry("tiles:6a_mediumTree", Texture.class));
		treeTallTexture = new TextureRegion(directory.getEntry("tiles:6b_tallTree", Texture.class));
		treeWideTexture = new TextureRegion(directory.getEntry("images:tree2", Texture.class));
		treeBallTexture = new TextureRegion(directory.getEntry("tiles:6c_shortTree", Texture.class));
		treeBallFadedTexture = new TextureRegion(directory.getEntry("images:tree3", Texture.class));
		mushroomTexture = new TextureRegion(directory.getEntry("images:mushroom", Texture.class));

		// UI Textures
		fHeartTexture = directory.getEntry("images:fullHeart", Texture.class);
		sHeartTexture = directory.getEntry("images:slashedHeart", Texture.class);

		// Unnecessary atm?

		playerDirectionTextures = new Texture[] { playerTextureUp, playerTextureDown, playerTextureRight,
				playerTextureLeft, playerTextureIdle };
		enemyDirectionTextures = new Texture[] { enemyTextureUp, enemyTextureDown, enemyTextureRight, enemyTextureLeft };
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

		constants = directory.getEntry("platform:constants", JsonValue.class);

		treePos = new Array<Vector2>();
		float[][] treePositions = { { 2, 10 }, { 450, 125 }, { 80, 50 } };
		treePos = new Array<Vector2>(treePositions.length);
		for (float[] pos : treePositions) {
			Vector2 tPos = new Vector2(pos[0], pos[1]);
			treePos.add(tPos);
		}

		super.gatherAssets(directory);
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
		enemyControllers.clear();
		survivorControllers.clear();
		objects.clear();
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

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void nextLevel() {
		Vector2 gravity = new Vector2(world.getGravity());

		for (Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		enemyControllers.clear();
		survivorControllers.clear();
		objects.clear();
		addQueue.clear();
		canvas.disposeLights();
		world.dispose();

		world = new World(gravity, false);
		collisionController.setContactListener(world);
		canvas.createLights(world);
		setComplete(false);
		setFailure(false);

		if (curLevel >= maxLevels-1) {
			curLevel = -1;
		}
		populateLevel(curLevel + 1);
		curLevel++;
	}

	private void populateLevel(int level) {
		// Populate the level using the JSON Reader

		staticsAndPlayer = new Array<Obstacle>();

		// *************************** STATIC OBSTACLES ***************************
		int[] startingBox = { 6, 4 };

		// Arrays used to find tiles to place smog at
		boolean[][] tiles = new boolean[canvas.getWidth() / tileSize][canvas.getHeight() / tileSize];
		boolean[][] smogLocations = new boolean[canvas.getWidth() / smogTileSize][canvas.getHeight() / smogTileSize];

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
				objects, floorArr, SCALE, tileGrid, tileSize, tileOffset, smogTileSize, smogTileOffset,
				playerDirectionTextures, enemyDirectionTextures, enemyTextureIdle, toxicAir,
				survivorITexture, displayFontInteract, fHeartTexture, player, weapon);

		// System.out.println("Canvas width: " + canvas.getWidth() + "\tTile Size: " +
		// tileSize + "\tNumTiles: " + canvas.getWidth() / tileSize);
		// System.out.println("First element of tiles: " + tiles[0][0]);



		objects = reader.getObjects();
		tileGrid = reader.getTileGrid();
//		canvas.camera = reader.getCamera();
		caravan = reader.getCaravan();
		player = reader.getPlayer();
		weapon = reader.getWeapon();
		survivorArr = reader.getSurvivors();
		enemyArr = reader.getEnemies();
		survivorControllers = reader.getSurvivorControllers();
		enemyControllers = reader.getEnemyControllers();

		// *************************** CARAVAN, PLAYER, AND WEAPON ***************************
		// Instantiate the caravan:
		// caravan = new Caravan(caravanLocation[0] * tileSize + tileOffset,
		// caravanLocation[1] * tileSize + tileOffset,
		// 5, caravanTexture, survivorITexture, SCALE * 2, displayFontInteract);
		// addObject(caravan);
		// caravan.activatePhysics(world);

		// Instantiate the player:
		// player = new Player(playerLocation[0] * tileSize + tileOffset,
		// playerLocation[1] * tileSize + tileOffset, playerTextureUp,
		// playerTextureDown, playerTextureRight, playerTextureLeft, playerTextureIdle,
		// input, SCALE);
		if (isInvincible) {
			player.setHealth(10000);
		}
		// addObject(player);
		// player.activatePhysics(world);
		// player.setAwake(true);

		// Instantiate the weapon:
		// weapon = new Weapon(player.getPosition().x, player.getPosition().y);

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
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (!tiles[i][j]) {
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
		for (int i = 0; i < smogLocations.length; i++) {
			for (int j = 0; j < smogLocations[0].length; j++) {
				if (smogLocations[i][j]) {
					// Primary Grid
					// Later get data from json file
					float maxFrame = 4;
					float minFrame = 0;
					float frameNum = (float) (Math.random() * (maxFrame - minFrame + 1) + minFrame);
					smogT = new Smog(i * smogTileSize + smogTileOffset, j * smogTileSize + smogTileOffset, smogTexture, frameNum,
							SCALE);
					smogT.setAwake(true);
					smogT.setBodyType(BodyDef.BodyType.StaticBody);
					smogArr.add(smogT);
					addObject(smogT);
					smogT.activatePhysics(world);
					// Secondary Grid
					frameNum = (float) (Math.random() * (maxFrame - minFrame + 1) + minFrame);
					smogTO = new Smog(i * smogTileSize + smogTileSize, j * smogTileSize + smogTileSize, smogTexture, frameNum,
							SCALE);
					smogTO.setAwake(true);
					smogTO.setBodyType(BodyDef.BodyType.StaticBody);
					smogArr.add(smogTO);
					addObject(smogTO);
					smogTO.activatePhysics(world);
				}
			}
		}
		// System.out.println("Finished smog instantiation");

		// AirBar Creation
		// float barX = player.getX() - (canvas.getWidth()*cameraZoom)/2.0f + (30.0f *
		// cameraZoom);
		// float barY = player.getY() + (canvas.getHeight()*cameraZoom)/2.0f - (30.0f *
		// cameraZoom);
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
		// if (!super.preUpdate(dt)) {
		// return false;
		// }
		//
		// if (!isFailure() && player.getY() < -1) {
		// setFailure(true);
		// return false;
		// }

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
			if (level == 1) {
				level = 2;
				reset();
			} else {
				level = 1;
				reset();
			}
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
		if (player.getX() >= canvas.getWidth() - 20) {
			player.setPosition(canvas.getWidth() - 20, player.getBody().getPosition().y);
		}
		if (player.getY() < 20) {
			player.setPosition(player.getBody().getPosition().x, 20);
		}
		if (player.getY() >= canvas.getHeight() - 20) {
			player.setPosition(player.getBody().getPosition().x, canvas.getHeight() - 20);
		}

		// Update UI elements
		airBar.update(weapon.getNumAmmo());

		weapon.update(player.getPosition(), canvas.unproject(input.getMousePos()), input.getShootDir());
		// Check if the weapon is firing

		if (weapon.fire()) {
			purifiedAir.attack(weapon.getBullets(), weapon.getPosition(), weapon.getImpulses());
			weapon.incrementAmmo(-weapon.getBullets());
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
		for (Smog obj : smogArr) {
			obj.update();
			if (obj.isFaded()) {
				obj.markRemoved(true);
			}
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
			if (enemyArr.get(i).getX() >= canvas.getWidth() - 20) {
				enemyArr.get(i).setPosition(canvas.getWidth() - 20, enemyArr.get(i).getBody().getPosition().y);
			}
			if (enemyArr.get(i).getY() < 20) {
				enemyArr.get(i).setPosition(enemyArr.get(i).getBody().getPosition().x, 20);
			}
			if (enemyArr.get(i).getY() >= canvas.getHeight() - 20) {
				enemyArr.get(i).setPosition(enemyArr.get(i).getBody().getPosition().x, canvas.getHeight() - 20);
			}
		}

		// This section will be handled by collisionController in the future with the
		// exception of obj.update(..)s and
		// removing objects from arrays once they are deactivated:
		// -------------------------------------------------------------------------------------------------------------
		// Update survivor positions and states
		for (int i = 0; i < survivorArr.size; i++) {
			// This will be handled by collisionController in the future
			survivorArr.get(i).update(survivorControllers.get(i).getAction());
			if (survivorArr.get(i).getX() < 20) {
				survivorArr.get(i).setPosition(20, survivorArr.get(i).getBody().getPosition().y);
			}
			if (survivorArr.get(i).getX() >= canvas.getWidth() - 20) {
				survivorArr.get(i).setPosition(canvas.getWidth() - 20, survivorArr.get(i).getBody().getPosition().y);
			}
			if (survivorArr.get(i).getY() < 20) {
				survivorArr.get(i).setPosition(survivorArr.get(i).getBody().getPosition().x, 20);
			}
			if (survivorArr.get(i).getY() >= canvas.getHeight() - 20) {
				survivorArr.get(i).setPosition(survivorArr.get(i).getBody().getPosition().x, canvas.getHeight() - 20);
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
		}
		caravan.update();
		// Update caravan state
		if (caravan.getBody().getFixtureList().first().testPoint(player.getPosition())) {
			caravan.setInteractable(true);
		} else {
			caravan.setInteractable(false);
		}
		if (caravan.isInteractable() && input.didDropSurvivors()) {
			if (caravan.getCurrentCapacity() == caravan.getMaxCapacity()) {
				setComplete(true);
			}
			for (int i = 0; i < survivorArr.size; i++) {
				if (survivorArr.get(i).isFollowing()) {
					survivorArr.get(i).rescue();
					player.removeFromFollowing(survivorArr.get(i));
					survivorArr.get(i).deactivatePhysics(world);
					survivorArr.removeIndex(i);
					numRescued++;
					caravan.incrCap();
					caravan.setInteractable(false);
				}
			}
			caravan.setInteractable(false);
		}
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

	/**
	 * Add a new bullet to the world and send it in the right direction.
	 */
	// private void createBullet() {
	// JsonValue bulletjv = constants.get("bullet");
	// float offset = bulletjv.getFloat("offset",0);
	// offset *= (player.isFacingRight() ? 1 : -1);
	// float radius = bulletTexture.getRegionWidth()/(2.0f*scale.x);
	// WheelObstacle bullet = new WheelObstacle(player.getX()+offset, player.getY(),
	// radius);
	//
	// bullet.setName("bullet");
	// bullet.setDensity(bulletjv.getFloat("density", 0));
	// bullet.setDrawScale(scale);
	// bullet.setTexture(bulletTexture);
	// bullet.setBullet(true);
	// bullet.setGravityScale(0);
	//
	// // Compute position and velocity
	// float speed = bulletjv.getFloat( "speed", 0 );
	// speed *= (player.isFacingRight() ? 1 : -1);
	// bullet.setVX(speed);
	// addQueuedObject(bullet);
	//
	// fireId = playSound( fireSound, fireId );
	// }

	/**
	 * Remove a new bullet from the world.
	 *
	 * @param bullet the bullet to remove
	 */
	// public void removeBullet(Obstacle bullet) {
	// bullet.markRemoved(true);
	// plopId = playSound( plopSound, plopId );
	// }

	/**
	 * Called when the Screen is paused.
	 *
	 * We need this method to stop all sounds when we pause.
	 * Pausing happens when we switch game modes.
	 */
	// public void pause() {
	// jumpSound.stop(jumpId);
	// plopSound.stop(plopId);
	// fireSound.stop(fireId);
	// }
}