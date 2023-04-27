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
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;

import com.mygdx.game.UI.AirBar;
import com.mygdx.game.UI.Heart;
import obstacle.BoxObstacle;
import obstacle.Obstacle;
import com.mygdx.game.EnemyControllers.*;
import com.mygdx.game.Obstacles.Enemies.*;

/**
 * Gameplay specific controller for the platformer game.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GameplayController extends WorldController{
	/** Texture assets for player avatar */
	private Texture playerTextureUp;
	private Texture playerTextureRight;
	private Texture playerTextureLeft;
	private Texture playerTextureDown;
	private Texture playerTextureIdle;
	/** Texture asset for enemy avatar */
	/** Texture assets for player avatar */
	private Texture enemyTextureUp;
	private Texture enemyTextureRight;
	private Texture enemyTextureLeft;
	private Texture enemyTextureDown;
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
	/** Texture asset for the smog texture */
	private Texture smogTexture;
	/** Texture asset for the air bar texture */
	private Texture airBarTexture;
	/** Texture asset for the pure air texture */
	private Texture pureAirTexture;
	/** Texture asset for the toxic air texture */
	private Texture toxicAirTexture;
	/** Texture asset for the second smog texture */
	private TextureRegion smogTexture2;
	/** Texture asset for the cliff texture */
	private TextureRegion cliffTexture;
	/** Texture asset for the cliff texture */
	private TextureRegion cliffTexture2;
	/** Texture asset for the mushroom texture */
	private TextureRegion mushroomTexture;
	/** Texture asset for the tree texture */
	private TextureRegion treeTexture;
	/** Texture asset for the Caravan texture */
	private TextureRegion caravanTexture;
	/** Texture asset for the full heart texture */
	private Texture fHeartTexture;
	/** Texture asset for the slashed heart texture */
	private Texture sHeartTexture;
	/** Texture for shrieker enemy */
	private Texture shriekerTexture;
	/** How zoomed in the camera is - for placing UI elements */
	private float cameraZoom;

	/** Enemy Controller */
	private Array<EnemyController> enemyControllers;

	/** The weapon fire sound.  We only want to play once. */
	private Sound fireSound;
	private long fireId = -1;//

	// *************************** Floor Textures ***************************
	private TextureRegion grassTexture;
	private TextureRegion dirtTexture;
	private TextureRegion dirtMushroomTexture;
	private TextureRegion rockTexture;
	private TextureRegion brickFloorTexture;
	private TextureRegion brickFloorCrackedTexture;
	private TextureRegion brickFloorCrackedTopTexture;

	private TextureRegion brickWallTexture;
	private TextureRegion brickWallTopOpenTexture;
	private TextureRegion brickWallSidesOpenTexture;
	private TextureRegion brickWallCrackedTexture;
	private TextureRegion brickWallTopTexture;
	private TextureRegion borderSmogTexture;

	private TextureRegion treeTallTexture;
	private TextureRegion treeBallTexture;

	/** The default sound volume */
	private float volume;

	// Physics objects for the game
	/** Physics constants for initialization */
	private JsonValue constants;
	/** Reference to the player avatar */
	//private Player player;
	/** Reference to the caravan avatar */
	private Caravan caravan;
//	/** Reference to the smog bar asset */
//	private AirBar airBar;
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
	//protected ObjectSet<Fixture> sensorFixtures;
	// Moved the masks to GameObstacle if you are looking for them -V
	int numRescued;
	int numSurvivorsTotal;
	/** Used for playtesting, player is invincible */
	private boolean isInvincible = false;
	private boolean pausing = false;
	private boolean unpausing = false;
	private boolean paused = false;

	private int level;

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public GameplayController(GameCanvas canvas) {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		//world.setContactListener(this);
		collisionController = new CollisionController(world, canvas.getWidth(), canvas.getHeight());
		//
		//sensorFixtures = new ObjectSet<Fixture>();

		input = new InputController();



		canvas.createLights(world);

		enemyControllers = new Array<EnemyController>();
		survivorControllers = new Array<SurvivorController>();
		tileGrid = new boolean[canvas.getWidth()/tileSize][canvas.getHeight()/tileSize];
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
	 * @param directory	Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {
		playerTextureUp = directory.getEntry("images:playerUp",Texture.class);
		playerTextureDown = directory.getEntry("images:playerDown",Texture.class);
		playerTextureRight = directory.getEntry("images:playerRight",Texture.class);
		playerTextureLeft = directory.getEntry("images:playerLeft",Texture.class);
		playerTextureIdle = directory.getEntry("images:playerIdle",Texture.class);
		enemyTextureUp = directory.getEntry("images:maskEnemyUp",Texture.class);
		enemyTextureDown = directory.getEntry("images:maskEnemyDown",Texture.class);
		enemyTextureRight = directory.getEntry("images:maskEnemyRight",Texture.class);
		enemyTextureLeft = directory.getEntry("images:maskEnemyLeft",Texture.class);
		enemyTextureIdle = directory.getEntry("images:maskEnemyIdle",Texture.class);
		shriekerTexture = directory.getEntry("images:plantEnemy",Texture.class);
		survivorTexture = new TextureRegion(directory.getEntry("images:survivorSprite", Texture.class));
		survivorITexture = directory.getEntry("images:sInteract", Texture.class);
		cliffTexture = new TextureRegion(directory.getEntry("images:cliff3",Texture.class));
		cliffTexture2 = new TextureRegion(directory.getEntry("images:cliff4",Texture.class));
		mushroomTexture = new TextureRegion(directory.getEntry("images:mushroom",Texture.class));
		treeTexture = new TextureRegion(directory.getEntry("tiles:6a_mediumTree",Texture.class));
		smogTexture = directory.getEntry("images:testSmog", Texture.class);
		airBarTexture = directory.getEntry("images:airBar", Texture.class);
		// pureAirTexture = new TextureRegion(directory.getEntry("images:smog1", Texture.class));
		pureAirTexture = directory.getEntry("images:testSmog", Texture.class);
		toxicAirTexture = directory.getEntry("images:testSmog", Texture.class);
		pureAirTexture = directory.getEntry("images:weaponProjectile", Texture.class);
		smogTexture2 = new TextureRegion(directory.getEntry("images:smog2", Texture.class));
		caravanTexture = new TextureRegion(directory.getEntry("tiles:0_caravan", Texture.class));
		fHeartTexture = directory.getEntry("images:fullHeart", Texture.class);
		sHeartTexture = directory.getEntry("images:slashedHeart", Texture.class);

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

		// Floor Textures
		grassTexture = new TextureRegion(directory.getEntry("tiles:4a_grass1", Texture.class));
		dirtTexture = new TextureRegion(directory.getEntry("tiles:4c_dirt1", Texture.class));
		dirtMushroomTexture = new TextureRegion(directory.getEntry("tiles:4c_dirt2", Texture.class));
		rockTexture = new TextureRegion(directory.getEntry("tiles:4b_rocks", Texture.class));
		brickFloorTexture = new TextureRegion(directory.getEntry("tiles:4d_brick1", Texture.class));
		brickFloorCrackedTexture = new TextureRegion(directory.getEntry("tiles:4d_brick3", Texture.class));
		brickFloorCrackedTopTexture = new TextureRegion(directory.getEntry("tiles:4d_brick2", Texture.class));

		brickWallTexture = new TextureRegion(directory.getEntry("tiles:5a_brick1", Texture.class));
		brickWallTopOpenTexture = new TextureRegion(directory.getEntry("tiles:5a_brick4", Texture.class));
		brickWallSidesOpenTexture = new TextureRegion(directory.getEntry("tiles:5a_brick3", Texture.class));
		brickWallCrackedTexture = new TextureRegion(directory.getEntry("tiles:5a_brick2", Texture.class));
		brickWallTopTexture = new TextureRegion(directory.getEntry("tiles:5a_brick5", Texture.class));
		borderSmogTexture = new TextureRegion(directory.getEntry("tiles:5c_borderSmog", Texture.class));

		treeTallTexture = new TextureRegion(directory.getEntry("tiles:6b_tallTree", Texture.class));
		treeBallTexture = new TextureRegion(directory.getEntry("tiles:6c_shortTree", Texture.class));

		fireSound = directory.getEntry( "platform:pew", Sound.class );

		constants = directory.getEntry( "platform:constants", JsonValue.class );

		treePos = new Array<Vector2>();
		float[][] treePositions = {{2,10}, {450, 125}, {80, 50}};
		treePos = new Array<Vector2>(treePositions.length);
		for (float[] pos : treePositions){
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
		Vector2 gravity = new Vector2(world.getGravity() );
		numRescued = 0;

		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		enemyControllers.clear();
		survivorControllers.clear();
		objects.clear();
		addQueue.clear();
		canvas.disposeLights();
		world.dispose();

		world = new World(gravity,false);
		collisionController.setContactListener(world);
		canvas.createLights(world);
		setComplete(false);
		setFailure(false);
		if (level == 1) {
			populateLevel1();
		}
		else {
			populateLevel2();
		}
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel1() {
		// Populate the level with static obstacles such as cliffs, trees, and rivers
		// Populate the level with smog
		// Populate the level with the player and weapon

		staticsAndPlayer = new Array<Obstacle>();

		// *************************** STATIC OBSTACLES ***************************

		// Current static locations for each object:
		int[][] wallLocations = {{1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {6, 1}, {7, 1}, {8, 1}, {9, 1}, {10, 1}, {11, 1}, {12, 1}, {13, 1}, {14, 1}, {15, 1}, {16, 1}, {17, 1}, {18, 1}, {19, 1}, {20, 1}, {21, 1}, {22, 1}, {23, 1}, {24, 1}, {25, 1}, {26, 1}, {27, 1}, {28, 1}, {29, 1}, {30, 1}, {1, 2,}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, {1, 9}, {1, 10}, {1, 11}, {1, 12}, {1, 13}, {2, 13}, {3, 13}, {4, 13}, {5, 13}, {6, 13}, {10, 13}, {11, 13}, {12, 13}, {13, 13}, {14, 13}, {15, 13}, {16, 13}, {17, 13}, {18, 13}, {18, 7}, {18, 8}, {18, 9}, {18, 10}, {18, 11}, {18, 12}, {19, 7}, {20, 7}, {21, 7}, {22, 7}, {23, 7}, {24, 7}, {25, 7}, {26, 7}, {27, 7}, {30, 2}, {30, 3}, {30, 4}, {30, 5}, {30, 6}, {30, 7}, {23, 2}, {23, 3}, {23, 4}, {23, 5}, {23, 6}, {24, 2}, {24, 3}, {24, 4}, {24, 5}, {24, 6}, {5, 5}, {5, 6}, {5, 7}, {5, 8}, {5, 9}, {14, 4}, {14, 5}, {14, 6}, {14, 7}, {14, 8}, {13, 14}, {13, 15}, {13, 16}, {13, 17}, {13, 18}, {13, 19}, {5, 19}, {6, 19}, {7, 19}, {8, 19}, {9, 19}, {10, 19}, {11, 19}, {12, 19}};
		int[][] treeLocations = {{3, 4}, {10, 2}, {20, 17}, {21, 8}};
		int[][] enemyLocations = {{3, 5}, {11, 4}, {17, 16}, {22, 11}};
		int[] playerLocation = {24, 19};
		int[] caravanLocation = {27, 19};
		int[][] survivorLocations = {{20, 4}, {26, 4}};
		int[] startingBox = {6, 4};
		int[][] mushroomLocations = {{7, 13}, {7, 14}, {7, 15}, {7, 16}, {7, 17}, {9, 13}, {9, 14}, {9, 15}, {9, 16}, {9, 17}, {2, 21}, {15, 21}, {19, 19}, {23, 15}, {25, 12}, {28, 10}, {29, 7}};

		// Arrays used to find tiles to place smog at
		boolean[][] tiles = new boolean[canvas.getWidth() / tileSize][canvas.getHeight() / tileSize];
		boolean[][] smogLocations = new boolean[canvas.getWidth() / smogTileSize][canvas.getHeight() / smogTileSize];

		// Testing tiles array:
//		System.out.println("Canvas width: " + canvas.getWidth() + "\tTile Size: " + tileSize + "\tNumTiles: " + canvas.getWidth() / tileSize);
//		System.out.println("First element of tiles: " + tiles[0][0]);

		//Setting the size of the tiles
		Shadow.setSize(tileSize*2/3);


		// Instantiate the cliffs:
		// Array<Cliff> cliffArr = new Array<>();
		Array<Obstacles> obstaclesArr = new Array<>();
		Obstacles cliffTemp;
		for (int i = 0; i < wallLocations.length; i++) {
			cliffTemp = new Obstacles(wallLocations[i][0] * tileSize + tileOffset, wallLocations[i][1] * tileSize + tileOffset, cliffTexture, SCALE);
			obstaclesArr.add(cliffTemp);
			//cliffTemp.setAwake(true);
			cliffTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(cliffTemp);
			cliffTemp.activatePhysics(world);
			tiles[wallLocations[i][0]][wallLocations[i][1]] = true;
			tileGrid[wallLocations[i][0]][wallLocations[i][1]] = true;
		}

		// Instantiate the trees:

		Obstacles treeTemp;
		for (int i = 0; i < treeLocations.length; i++) {
			treeTemp = new Obstacles(treeLocations[i][0] * tileSize + tileOffset, treeLocations[i][1] * tileSize + tileOffset, treeTexture, SCALE);
			obstaclesArr.add(treeTemp);
			addObject(treeTemp);
			treeTemp.activatePhysics(world);
			tiles[treeLocations[i][0]][treeLocations[i][1]] = true;
			tileGrid[treeLocations[i][0]][treeLocations[i][1]] = true;
		}
		// Instantiate the caravan:
		caravan = new Caravan(caravanLocation[0] * tileSize + tileOffset, caravanLocation[1] * tileSize + tileOffset, 5, caravanTexture, survivorITexture, SCALE, displayFontInteract);
		addObject(caravan);
		caravan.activatePhysics(world);
		// *************************** PLAYER AND WEAPON ***************************
		// Instantiate the player:
		player = new Player(playerLocation[0]*tileSize + tileOffset, playerLocation[1]*tileSize + tileOffset, playerTextureUp, playerTextureDown, playerTextureRight, playerTextureLeft, playerTextureIdle, input, SCALE);
		if (isInvincible){
			player.setHealth(10000);
		}

		addObject(player);
		player.activatePhysics(world);
		player.setAwake(true);

		// Instantiate the weapon:
		weapon = new Weapon(player.getPosition().x, player.getPosition().y);
		//player.attachLightToPlayer(weapon.getAbsorbSensor());


		//Gives ammo in debug mode
		if (isDebug()){
			weapon.setNumAmmo(1000);
		}


		//TO DO: update visuals for purified smog
		purifiedAir = new PurifiedQueue(pureAirTexture, world, SCALE);
		toxicAir = new ToxicQueue(toxicAirTexture, world, SCALE);

		// Instantiate the enemies:
		enemyArr = new Array<Enemy>();
		shriekerArr = new Array<ShriekerEnemy>();
		// Shrieker enemies
		int numShriekers = 1;
		for (int i = 0; i < numShriekers; i++){
			ShriekerEnemy shriekTemp = new ShriekerEnemy(player.getX() + 200, player.getY() - 50, enemyTextureUp, enemyTextureDown, enemyTextureRight, enemyTextureLeft, enemyTextureIdle, SCALE);
			enemyArr.add(shriekTemp);
			shriekerArr.add(shriekTemp);
			shriekTemp.activatePhysics(world);
			addObject(shriekTemp);
			enemyControllers.add(new ShriekerEnemyController(tileGrid, tileSize, tileOffset, shriekTemp, player, shriekerArr));
		}

		for (int i = 0; i < enemyLocations.length; i++) {
			ScoutEnemy enemyTemp = new ScoutEnemy(enemyLocations[i][0] * tileSize + tileOffset, enemyLocations[i][1] * tileSize + tileOffset, enemyTextureUp, enemyTextureDown, enemyTextureRight, enemyTextureLeft, enemyTextureIdle, vineTextures,SCALE, world);
			enemyArr.add(enemyTemp);
			enemyTemp.activatePhysics(world);
			addObject(enemyTemp);

			enemyControllers.add(new ScoutEnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr));
		}
		// Chaser enemies
		int numChasers = 1;
		for (int i = 0; i < numChasers; i++){
			Enemy chaserTemp = new Enemy(player.getX() + 200, player.getY() + 100, enemyTextureUp, enemyTextureDown, enemyTextureRight, enemyTextureLeft, enemyTextureIdle, SCALE);
			enemyArr.add(chaserTemp);
			chaserTemp.activatePhysics(world);
			addObject(chaserTemp);
			enemyControllers.add(new ChaserEnemyController(tileGrid, tileSize, tileOffset, chaserTemp, player, shriekerArr));
		}



		// *************************** SURVIVORS ***************************
		survivorArr = new Array<>();
		Survivor survivorTemp;
		// Instantiate the survivors:
		for (int i = 0; i < survivorLocations.length; i++) {
			//Survivor survivorTemp = new Survivor(i, survivorLocations[i][0] * tileSize, survivorLocations[i][1] * tileSize, survivorTexture);
			survivorTemp = new Survivor(i, survivorLocations[i][0] * tileSize + tileOffset, survivorLocations[i][1] * tileSize + tileOffset, survivorTexture, fHeartTexture, displayFontInteract, SCALE);
			survivorTemp.activatePhysics(world);

//			System.out.println(collisionController.getGameObstacleType(survivorTemp.getUserData()));
			survivorArr.add(survivorTemp);
			addObject(survivorTemp);
			survivorControllers.add(new SurvivorController(survivorTemp, caravan.getPosition(), player.getPosition(), tileGrid, tileSize, tileOffset));
		}

		numSurvivorsTotal =survivorArr.size;
		// *************************** SMOG OBSTACLES ***************************

		// Starting Area:
		for (int i = playerLocation[0] - startingBox[0]; i < playerLocation[0] + startingBox[0]; i++) {
			for (int j = playerLocation[1] - startingBox[1]; j < playerLocation[1] + startingBox[1]; j++) {
				tiles[i][j] = true;
			}
		}

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
					float frameNum = (float)(Math.random()*(maxFrame-minFrame+1)+minFrame);
					smogT = new Smog(i * smogTileSize + smogTileOffset, j * smogTileSize + smogTileOffset, smogTexture, frameNum, SCALE);
					smogT.setAwake(true);
					smogT.setBodyType(BodyDef.BodyType.StaticBody);
					smogArr.add(smogT);
					addObject(smogT);
					smogT.activatePhysics(world);
					//Secondary Grid
					frameNum = (float)(Math.random()*(maxFrame-minFrame+1)+minFrame);
					smogTO = new Smog(i * smogTileSize + smogTileSize, j * smogTileSize + smogTileSize, smogTexture, frameNum, SCALE);
					smogTO.setAwake(true);
					smogTO.setBodyType(BodyDef.BodyType.StaticBody);
					smogArr.add(smogTO);
					addObject(smogTO);
					smogTO.activatePhysics(world);
				}
			}
		}

		// AirBar Creation
		// float barX = player.getX() - (canvas.getWidth()*cameraZoom)/2.0f + (30.0f * cameraZoom);
		// float barY = player.getY() + (canvas.getHeight()*cameraZoom)/2.0f - (30.0f * cameraZoom);
		airBar = new AirBar(airBarTexture, weapon.getMaxNumAmmo(), weapon.getNumAmmo(), canvas);

		// Hearts
		int numLives = player.getHealth();
		heartArr = new Array<Heart>(numLives);
		float heartX = canvas.camera.position.x + (canvas.getWidth()*cameraZoom)/2.0f - (30.0f * cameraZoom);
		float heartY = canvas.camera.position.y + (canvas.getHeight()*cameraZoom)/2.0f - (30.0f * cameraZoom);
		float spacing = 0.0f;

		for (int i = 0; i < numLives; i++){
			if (i > 0){
				spacing += 13.0f;
			}
			Heart tempHeart = new Heart(fHeartTexture, heartX, heartY, spacing);
			heartArr.add(tempHeart);
		}





		// ############## ONLY FOR PLACING SMOG AT ARBITRARY POINTS ACROSS THE GRID ##############
		// We want the smog to be dispersed over a small area to start
		// Small Smog
//		int smogXBeginning = canvas.getWidth() / 3;
//		int smogXEnd = canvas.getWidth() * 2 / 3;
//		int smogYBeginning = canvas.getHeight() / 7;
//		int smogYEnd = canvas.getHeight() * 3 / 7;
//		int step = 20;
		// Full Screen Smog
//		int smogXBeginning = canvas.getWidth() / 10;
//		int smogXEnd = canvas.getWidth() * 9 / 10;
//		int smogYBeginning = canvas.getHeight() / 10;
//		int smogYEnd = canvas.getHeight() * 9 / 10;
//		int step = 15;
//
//		Smog smogT = new Smog(canvas.getWidth() / 3, canvas.getHeight() / 3, smogTexture);
//		for (int i = smogXBeginning; i < smogXEnd; i += step) {
//			for (int j = smogYBeginning; j < smogYEnd; j += step) {
//				smogT = new Smog(i, j, smogTexture);
//				//smogT.setTextureRegion(smogTexture);
//				addObject(smogT);
//				smogArr.add(smogT);
//			}
//		}


		// *************************** SORTING / DRAWING ORDER ***************************
		// Sort statics and player in order of descending y value for position.
//		Array<Obstacle> tempArr = new Array<>();
//		float maxY = 0;
//		int maxYind = 0;
//		for (int i = 0; i < staticsAndPlayer.size; i++) {
//			for (int j = 0; j < staticsAndPlayer.size; j++) {
//				if (!tempArr.contains(staticsAndPlayer.get(i), false) && staticsAndPlayer.get(i).getPosition().y > maxY) {
//					maxY = staticsAndPlayer.get(i).getPosition().y;
//					maxYind = i;
//				}
//			}
//			tempArr.add(staticsAndPlayer.get(maxYind));
//			maxY = 0;
//			maxYind = 0;
//		}
//		staticsAndPlayer = tempArr;


		// *************************** EXTRAS / NOTES ***************************
		// Create single smog tile
//		Smog smog = new Smog(canvas.getWidth() / 3, canvas.getHeight() / 3);
//		smog.setTextureRegion(smogTexture);
//		addObject(smog);

	}

	private void populateLevel2() {
		// Populate the level with static obstacles such as cliffs, trees, and rivers
		// Populate the level with smog
		// Populate the level with the player and weapon

		staticsAndPlayer = new Array<Obstacle>();

		// *************************** STATIC OBSTACLES ***************************

		// Current static locations for each object:
		int[][] wallLocations = { { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 }, { 5, 1 }, { 6, 1 }, { 7, 1 }, { 8, 1 }, { 9, 1 },
				{ 10, 1 }, { 11, 1 }, { 12, 1 }, { 13, 1 }, { 14, 1 }, { 15, 1 }, { 16, 1 }, { 17, 1 }, { 18, 1 }, { 19, 1 },
				{ 20, 1 }, { 21, 1 }, { 22, 1 }, { 23, 1 }, { 24, 1 }, { 25, 1 }, { 26, 1 }, { 27, 1 }, { 28, 1 }, { 29, 1 },
				{ 30, 1 }, { 1, 2, }, { 1, 3 }, { 1, 4 }, { 1, 5 }, { 1, 6 }, { 1, 7 }, { 1, 8 }, { 1, 9 }, { 1, 10 },
				{ 1, 11 }, { 1, 12 }, { 1, 13 }, { 2, 13 }, { 3, 13 }, { 4, 13 }, { 5, 13 }, { 6, 13 }, { 10, 13 }, { 11, 13 },
				{ 12, 13 }, { 13, 13 }, { 14, 13 }, { 15, 13 }, { 16, 13 }, { 17, 13 }, { 18, 13 }, { 18, 7 }, { 18, 8 },
				{ 18, 9 }, { 18, 10 }, { 18, 11 }, { 18, 12 }, { 19, 7 }, { 20, 7 }, { 21, 7 }, { 22, 7 }, { 23, 7 }, { 24, 7 },
				{ 25, 7 }, { 26, 7 }, { 27, 7 }, { 30, 2 }, { 30, 3 }, { 30, 4 }, { 30, 5 }, { 30, 6 }, { 30, 7 }, { 23, 2 },
				{ 23, 3 }, { 23, 4 }, { 23, 5 }, { 23, 6 }, { 24, 2 }, { 24, 3 }, { 24, 4 }, { 24, 5 }, { 24, 6 }, { 5, 5 },
				{ 5, 6 }, { 5, 7 }, { 5, 8 }, { 5, 9 }, { 14, 4 }, { 14, 5 }, { 14, 6 }, { 14, 7 }, { 14, 8 }, { 13, 14 },
				{ 13, 15 }, { 13, 16 }, { 13, 17 }, { 13, 18 }, { 13, 19 }, { 5, 19 }, { 6, 19 }, { 7, 19 }, { 8, 19 },
				{ 9, 19 }, { 10, 19 }, { 11, 19 }, { 12, 19 } };

		// Floor locations
		int[][] grassLocations = { { 3, 3 }, { 3, 4 }, { 3, 5 }, { 3, 6 }, { 3, 7 }, { 3, 8 }, { 3, 9 }, { 3, 10 },
				{ 3, 11 }, { 3, 12 }, { 3, 13 }, { 4, 3 }, { 4, 4 }, { 4, 5 }, { 4, 6 }, { 4, 7 }, { 4, 8 }, { 4, 9 },
				{ 4, 10 }, { 4, 11 }, { 4, 12 }, { 4, 13 }, { 4, 15 }, { 5, 3 }, { 5, 10 }, { 5, 11 }, { 5, 12 }, { 5, 13 },
				{ 5, 15 }, { 5, 16 }, { 6, 3 }, { 6, 4 }, { 6, 5 }, { 6, 6 }, { 6, 7 }, { 6, 8 }, { 6, 9 }, { 6, 10 },
				{ 6, 11 }, { 6, 12 }, { 6, 13 }, { 6, 14 }, { 6, 15 }, { 6, 16 }, { 6, 17 }, { 7, 3 }, { 7, 4 }, { 7, 5 },
				{ 7, 6 }, { 7, 7 }, { 7, 8 }, { 7, 9 }, { 7, 10 }, { 7, 11 }, { 7, 12 }, { 7, 13 }, { 7, 14 }, { 7, 15 },
				{ 7, 16 }, { 8, 3 }, { 8, 4 }, { 8, 5 }, { 8, 6 }, { 8, 7 }, { 8, 8 }, { 8, 9 }, { 8, 10 }, { 8, 11 },
				{ 8, 12 }, { 8, 13 }, { 8, 15 }, { 8, 16 }, { 9, 3 }, { 9, 4 }, { 9, 5 }, { 9, 6 }, { 9, 7 }, { 9, 8 },
				{ 9, 9 }, { 9, 10 }, { 9, 11 }, { 9, 12 }, { 9, 13 }, { 9, 15 }, { 10, 3 }, { 10, 4 }, { 10, 5 }, { 10, 6 },
				{ 10, 7 }, { 10, 8 }, { 10, 9 }, { 10, 10 }, { 10, 11 }, { 10, 12 }, { 10, 13 }, { 11, 5 }, { 11, 6 },
				{ 11, 7 }, { 11, 8 }, { 11, 9 }, { 11, 10 }, { 11, 11 }, { 11, 12 }, { 12, 5 }, { 12, 6 }, { 12, 7 }, { 12, 8 },
				{ 12, 9 }, { 12, 10 }, { 12, 11 }, { 12, 12 }, { 12, 13 }, { 13, 11 }, { 13, 12 }, { 13, 13 }, { 14, 5 },
				{ 14, 6 }, { 14, 7 }, { 14, 8 }, { 14, 9 }, { 14, 10 }, { 14, 11 }, { 14, 12 }, { 14, 13 }, { 15, 5 },
				{ 15, 6 }, { 15, 7 }, { 15, 8 }, { 15, 9 }, { 15, 10 }, { 15, 11 }, { 15, 12 }, { 15, 13 }, { 16, 5 },
				{ 16, 6 }, { 16, 7 }, { 16, 8 }, { 16, 9 }, { 16, 10 }, { 16, 11 }, { 16, 12 }, { 16, 13 }, { 17, 5 },
				{ 17, 6 }, { 17, 7 }, { 17, 8 }, { 17, 9 }, { 17, 10 }, { 17, 11 }, { 17, 12 }, { 17, 13 }, { 18, 5 },
				{ 19, 5 }, { 19, 6 }, { 26, 3 }, { 26, 4 }, { 26, 5 }, { 26, 6 }, { 27, 3 }, { 27, 4 }, { 27, 5 }, { 27, 6 },
				{ 27, 8 }, { 28, 3 }, { 28, 4 }, { 28, 5 }, { 28, 6 }, { 28, 7 }, { 28, 8 }, { 28, 9 }, { 29, 3 }, { 29, 4 },
				{ 29, 5 }, { 29, 6 }, { 29, 7 }, { 29, 8 }, { 29, 9 }, { 29, 10 } };
		int[][] rockLocations = { { 12, 15 }, { 13, 15 }, { 14, 15 }, { 15, 15 }, { 16, 15 }, { 13, 16 }, { 14, 16 },
				{ 15, 16 }, { 16, 16 }, { 17, 16 }, { 13, 17 }, { 14, 17 }, { 15, 17 }, { 14, 18 }, { 15, 18 }, { 16, 18 },
				{ 15, 19 } };
		int[][] dirtLocations = { { 2, 15 }, { 2, 16 }, { 2, 17 }, { 2, 18 }, { 2, 19 }, { 2, 20 }, { 2, 21 }, { 2, 22 },
				{ 2, 23 }, { 3, 16 }, { 3, 17 }, { 3, 18 }, { 3, 21 }, { 3, 22 }, { 3, 23 }, { 4, 17 }, { 4, 18 }, { 4, 19 },
				{ 4, 21 }, { 4, 22 }, { 4, 23 }, { 5, 18 }, { 5, 19 }, { 5, 21 }, { 5, 22 }, { 5, 23 }, { 6, 18 }, { 6, 19 },
				{ 6, 21 }, { 6, 22 }, { 6, 23 }, { 7, 17 }, { 7, 18 }, { 7, 19 }, { 7, 21 }, { 7, 23 }, { 8, 17 }, { 8, 18 },
				{ 8, 19 }, { 8, 21 }, { 8, 22 }, { 8, 23 }, { 9, 16 }, { 9, 17 }, { 9, 18 }, { 9, 19 }, { 9, 21 }, { 9, 22 },
				{ 9, 23 }, { 10, 15 }, { 10, 16 }, { 10, 17 }, { 10, 18 }, { 10, 19 }, { 10, 21 }, { 10, 22 }, { 10, 23 },
				{ 11, 21 }, { 11, 22 }, { 11, 23 }, { 12, 16 }, { 12, 17 }, { 12, 18 }, { 12, 19 }, { 12, 20 }, { 12, 21 },
				{ 12, 22 }, { 12, 23 }, { 13, 18 }, { 13, 19 }, { 13, 20 }, { 13, 22 }, { 13, 23 }, { 14, 19 }, { 14, 20 },
				{ 14, 21 }, { 14, 22 }, { 14, 23 }, { 15, 20 }, { 15, 21 }, { 15, 22 }, { 15, 23 }, { 16, 17 }, { 16, 19 },
				{ 16, 20 }, { 16, 21 }, { 16, 22 }, { 16, 23 }, { 17, 15 }, { 17, 17 }, { 17, 18 }, { 17, 19 }, { 17, 20 },
				{ 17, 21 }, { 17, 22 }, { 17, 23 }, { 18, 15 }, { 18, 17 }, { 18, 18 }, { 18, 19 }, { 18, 20 }, { 18, 21 },
				{ 18, 22 }, { 18, 23 }, { 19, 8 }, { 19, 9 }, { 19, 10 }, { 19, 11 }, { 19, 12 }, { 19, 13 }, { 19, 14 },
				{ 19, 15 }, { 19, 16 }, { 19, 17 }, { 19, 18 }, { 19, 19 }, { 19, 20 }, { 19, 21 }, { 19, 22 }, { 19, 23 },
				{ 20, 8 }, { 20, 9 }, { 20, 10 }, { 20, 11 }, { 20, 12 }, { 20, 13 }, { 20, 14 }, { 20, 15 }, { 20, 16 },
				{ 20, 17 }, { 20, 18 }, { 20, 19 }, { 20, 20 }, { 20, 21 }, { 20, 22 }, { 20, 23 }, { 21, 8 }, { 21, 10 },
				{ 21, 11 }, { 21, 12 }, { 21, 13 }, { 21, 14 }, { 21, 15 }, { 21, 16 }, { 21, 17 }, { 21, 18 }, { 21, 19 },
				{ 21, 20 }, { 21, 21 }, { 21, 22 }, { 22, 8 }, { 22, 9 }, { 22, 10 }, { 22, 11 }, { 22, 12 }, { 22, 13 },
				{ 22, 14 }, { 22, 15 }, { 22, 16 }, { 22, 17 }, { 22, 18 }, { 22, 19 }, { 22, 20 }, { 22, 21 }, { 22, 22 },
				{ 23, 8 }, { 23, 9 }, { 23, 10 }, { 23, 11 }, { 23, 12 }, { 23, 13 }, { 23, 14 }, { 23, 15 }, { 23, 16 },
				{ 23, 17 }, { 23, 18 }, { 23, 19 }, { 23, 20 }, { 23, 21 }, { 24, 8 }, { 24, 9 }, { 24, 10 }, { 24, 11 },
				{ 24, 12 }, { 24, 14 }, { 24, 15 }, { 24, 16 }, { 24, 17 }, { 24, 18 }, { 24, 19 }, { 24, 20 }, { 25, 8 },
				{ 25, 9 }, { 25, 10 }, { 25, 11 }, { 25, 12 }, { 25, 13 }, { 25, 14 }, { 25, 15 }, { 25, 16 }, { 25, 18 },
				{ 25, 19 }, { 26, 9 }, { 26, 10 }, { 26, 11 }, { 26, 12 }, { 26, 13 }, { 26, 14 }, { 26, 15 }, { 26, 16 },
				{ 26, 17 }, { 26, 18 }, { 27, 10 }, { 27, 11 }, { 27, 12 }, { 27, 13 }, { 27, 14 }, { 27, 15 }, { 27, 16 },
				{ 27, 17 }, { 28, 11 }, { 28, 12 }, { 28, 13 }, { 28, 14 }, { 28, 15 }, { 28, 16 }, { 29, 12 }, { 29, 13 },
				{ 29, 14 }, { 29, 15 }, };
		int[][] dirtMushroomLocations = { { 3, 15 }, { 4, 16 }, { 5, 17 }, { 3, 19 }, { 3, 20 }, { 7, 22 }, { 11, 23 },
				{ 13, 21 }, { 18, 16 }, { 21, 9 }, { 24, 13 }, { 25, 17 }, { 26, 8 }, { 27, 9 }, { 28, 10 }, { 29, 11 } };
		int[][] brickFloorLocations = { { 11, 3 }, { 11, 4 }, { 12, 4 }, { 13, 3 }, { 13, 4 }, { 14, 3 }, { 14, 4 },
				{ 15, 4 }, { 16, 3 }, { 16, 4 }, { 17, 3 }, { 17, 4 }, { 18, 3 }, { 18, 4 }, { 19, 3 }, { 20, 3 }, { 20, 4 },
				{ 20, 6 }, { 21, 3 }, { 21, 4 }, { 21, 5 }, { 21, 6 }, { 22, 4 }, { 22, 5 }, { 22, 6 }, { 23, 3 }, { 23, 4 },
				{ 23, 6 }, { 24, 21 }, { 24, 22 }, { 24, 23 }, { 25, 20 }, { 25, 21 }, { 25, 22 }, { 25, 23 }, { 26, 20 },
				{ 26, 21 }, { 26, 22 }, { 26, 23 }, { 27, 19 }, { 27, 20 }, { 27, 21 }, { 27, 22 }, { 27, 23 }, { 28, 18 },
				{ 28, 19 }, { 28, 20 }, { 28, 21 }, { 28, 23 }, { 29, 16 }, { 29, 17 }, { 29, 18 }, { 29, 19 }, { 29, 20 },
				{ 29, 21 }, { 29, 22 }, { 29, 23 } };
		int[][] brickFloorCrackedLocations = { { 15, 3 }, { 19, 4 }, { 23, 23 }, { 28, 22 } };
		int[][] brickFloorCrackedTopLocations = { { 12, 3 }, { 22, 3 }, { 20, 5 }, { 23, 5 }, { 21, 23 }, { 22, 23 },
				{ 23, 22 }, { 26, 19 }, { 27, 18 }, { 28, 17 } };

		// Wall locations
		int[][] brickWallLocations = { { 4, 20 }, { 8, 14 } };
		int[][] brickWallTopOpenLocations = { { 2, 14 }, { 5, 9 }, { 11, 20 }, { 13, 10 }, { 18, 14 }, { 19, 7 },
				{ 20, 7 } };
		int[][] brickWallSidesOpenLocations = { { 3, 2 }, { 4, 2 }, { 5, 2 }, { 6, 2 }, { 7, 2 }, { 9, 2 }, { 10, 2 },
				{ 11, 2 }, { 12, 2 }, { 13, 2 }, { 14, 2 }, { 15, 2 }, { 16, 2 }, { 17, 2 }, { 18, 2 }, { 20, 2 }, { 21, 2 },
				{ 22, 2 }, { 23, 2 }, { 26, 2 }, { 27, 2 }, { 28, 2 }, { 29, 2 }, { 4, 14 }, { 9, 14 }, { 10, 14 }, { 12, 14 },
				{ 13, 14 }, { 14, 14 }, { 15, 14 }, { 16, 14 }, { 17, 14 }, { 5, 20 }, { 6, 20 }, { 7, 20 }, { 8, 20 },
				{ 9, 20 }, { 10, 20 }, { 19, 7 }, { 20, 7 }, { 22, 7 }, { 23, 7 }, { 26, 7 } };
		int[][] brickWallCrackedLocations = { { 3, 14 }, { 5, 14 }, { 8, 2 }, { 19, 2 }, { 21, 7 }, { 27, 7 }, { 30, 7 } };
		int[][] brickWallTopLocations = { { 2, 1 }, { 2, 2 }, { 2, 3 }, { 2, 4 }, { 2, 5 }, { 2, 6 }, { 2, 7 }, { 2, 8 },
				{ 2, 9 }, { 2, 10 }, { 2, 11 }, { 2, 12 }, { 2, 13 }, { 5, 4 }, { 5, 5 }, { 5, 6 }, { 5, 7 }, { 5, 8 },
				{ 11, 13 }, { 11, 14 }, { 11, 15 }, { 11, 16 }, { 11, 17 }, { 11, 18 }, { 11, 19 }, { 13, 5 }, { 13, 6 },
				{ 13, 7 }, { 13, 8 }, { 13, 9 }, { 18, 6 }, { 18, 7 }, { 18, 8 }, { 18, 9 }, { 18, 10 }, { 18, 11 }, { 18, 12 },
				{ 18, 13 }, { 24, 1 }, { 24, 2 }, { 24, 3 }, { 24, 4 }, { 24, 5 }, { 24, 6 }, { 25, 1 }, { 25, 2 }, { 25, 3 },
				{ 25, 4 }, { 25, 5 }, { 25, 6 }, { 30, 1 }, { 30, 2 }, { 30, 3 }, { 30, 4 }, { 30, 5 }, { 30, 6 } };
		int[][] borderSmogLocations = { { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 }, { 5, 1 }, { 6, 1 }, { 7, 1 }, { 8, 1 },
				{ 9, 1 }, { 10, 1 }, { 11, 1 }, { 12, 1 }, { 13, 1 }, { 14, 1 }, { 15, 1 }, { 16, 1 }, { 17, 1 }, { 18, 1 },
				{ 19, 1 }, { 20, 1 }, { 21, 1 }, { 22, 1 }, { 23, 1 }, { 24, 1 }, { 25, 1 }, { 26, 1 }, { 27, 1 }, { 28, 1 },
				{ 29, 1 }, { 30, 1 },
				{ 1, 2 }, { 1, 3 }, { 1, 4 }, { 1, 5 }, { 1, 6 }, { 1, 7 }, { 1, 8 }, { 1, 9 }, { 1, 10 }, { 1, 11 }, { 1, 12 },
				{ 1, 13 }, { 1, 14 }, { 1, 15 }, { 1, 16 }, { 1, 17 }, { 1, 18 }, { 1, 19 }, { 1, 20 }, { 1, 21 }, { 1, 22 },
				{ 1, 23 },
				{ 30, 2 }, { 30, 3 }, { 30, 4 }, { 30, 5 }, { 30, 6 }, { 30, 7 }, { 30, 8 }, { 30, 9 }, { 30, 10 }, { 30, 11 },
				{ 30, 12 }, { 30, 13 }, { 30, 14 }, { 30, 15 }, { 30, 16 }, { 30, 17 }, { 30, 18 }, { 30, 19 }, { 30, 20 },
				{ 30, 21 }, { 30, 22 }, { 30, 23 },
				{ 1, 24 }, { 2, 24 }, { 3, 24 }, { 4, 24 }, { 5, 24 }, { 6, 24 }, { 7, 24 }, { 8, 24 }, { 9, 24 }, { 10, 24 },
				{ 11, 24 }, { 12, 24 }, { 13, 24 }, { 14, 24 }, { 15, 24 }, { 16, 24 }, { 17, 24 }, { 18, 24 }, { 19, 24 },
				{ 20, 24 }, { 21, 24 }, { 22, 24 }, { 23, 24 }, { 24, 24 }, { 25, 24 }, { 26, 24 }, { 27, 24 }, { 28, 24 },
				{ 29, 24 }, { 30, 24 } };

		// Tree locations
		int[][] treeLocations = { { 5, 21 }, { 6, 5 }, { 10, 23 }, { 12, 17 }, { 14, 18 }, { 14, 19 }, { 15, 18 },
				{ 15, 19 }, { 16, 19 }, { 17, 8 }, { 17, 19 }, { 18, 19 }, { 19, 8 }, { 19, 19 }, { 20, 14 }, { 20, 23 },
				{ 21, 15 }, { 21, 22 }, { 22, 10 }, { 23, 21 }, { 24, 8 }, { 26, 14 }, { 26, 15 }, { 26, 18 }, { 27, 13 },
				{ 28, 13 }, { 28, 14 }, { 28, 15 }, { 28, 16 }, { 29, 13 }, { 29, 14 } };
		int[][] treeTallLocations = { { 3, 3 }, { 5, 12 }, { 5, 15 }, { 8, 12 }, { 8, 15 }, { 14, 5 }, { 21, 8 },
				{ 23, 10 }, { 27, 14 }, { 27, 16 }, { 28, 4 } };
		int[][] treeBallLocations = { { 8, 8 }, { 12, 10 }, { 12, 18 }, { 13, 18 }, { 19, 12 }, { 19, 14 }, { 20, 8 },
				{ 20, 9 }, { 20, 12 }, { 22, 8 }, { 22, 22 }, { 23, 8 }, { 23, 9 }, { 23, 11 }, { 24, 9 }, { 24, 11 },
				{ 25, 8 }, { 25, 9 }, { 25, 11 }, { 27, 17 } };

		// Caravan, Player, Survivors, Enemies locations
		int[][] enemyLocations = { { 3, 5 }, { 9, 17 }, { 10, 9 }, { 11, 3 }, { 12, 20 }, { 20, 13 } };
		int[] playerLocation = { 26, 21 };
		int[] caravanLocation = { 26, 23 };
		int[][] survivorLocations = { { 21, 4 }, { 26, 4 } };
		int[] startingBox = { 6, 4 };

		int[][] mushroomLocations = { { 7, 13 }, { 7, 14 }, { 7, 15 }, { 7, 16 }, { 7, 17 }, { 9, 13 }, { 9, 14 },
				{ 9, 15 }, { 9, 16 }, { 9, 17 }, { 2, 21 }, { 15, 21 }, { 19, 19 }, { 23, 15 }, { 25, 12 }, { 28, 10 },
				{ 29, 7 } };

		// Arrays used to find tiles to place smog at
		boolean[][] tiles = new boolean[canvas.getWidth() / tileSize][canvas.getHeight() / tileSize];
		boolean[][] smogLocations = new boolean[canvas.getWidth() / smogTileSize][canvas.getHeight() / smogTileSize];

		// Testing tiles array:
		// System.out.println("Canvas width: " + canvas.getWidth() + "\tTile Size: " +
		// tileSize + "\tNumTiles: " + canvas.getWidth() / tileSize);
		// System.out.println("First element of tiles: " + tiles[0][0]);

		// Here we will instantiate the objects in the level using the JSONLevelReader.
		// JSONLevelReader reader = new JSONLevelReader(directory, bounds, world, input,
		// objects, SCALE, tileGrid, tileSize, tileOffset, smogTileSize, smogTileOffset,
		// playerDirectionTextures, enemyDirectionTextures, enemyTextureIdle,
		// survivorITexture, displayFontInteract, fHeartTexture, player, weapon);

		// Put this back in assets.json
		// "tiles:tileset": "tiles/LastColonyTilesetCorrect.json",

		// System.out.println("Canvas width: " + canvas.getWidth() + "\tTile Size: " +
		// tileSize + "\tNumTiles: " + canvas.getWidth() / tileSize);
		// System.out.println("First element of tiles: " + tiles[0][0]);

		// Setting the size of the tiles
		Shadow.setSize(32f);

		// caravan = reader.getCaravan();
		// player = reader.getPlayer();
		// weapon = reader.getWeapon();
		// survivorArr = reader.getSurvivors();
		// enemyArr = reader.getEnemies();
		// survivorControllers = reader.getSurvivorControllers();
		// enemyControllers = reader.getEnemyControllers();

		// Reverse Height:
		Array<int[][]> locations = new Array<int[][]>();
		locations.add(survivorLocations);
		locations.add(enemyLocations);
		locations.add(grassLocations);
		locations.add(rockLocations);
		locations.add(dirtLocations);
		locations.add(dirtMushroomLocations);
		locations.add(brickFloorLocations);
		locations.add(brickFloorCrackedLocations);
		locations.add(brickFloorCrackedTopLocations);
		locations.add(brickWallLocations);
		locations.add(brickWallTopOpenLocations);
		locations.add(brickWallSidesOpenLocations);
		locations.add(brickWallCrackedLocations);
		locations.add(brickWallTopLocations);
		locations.add(borderSmogLocations);
		locations.add(treeLocations);
		locations.add(treeTallLocations);
		locations.add(treeBallLocations);

		playerLocation = new int[] { playerLocation[0], canvas.getHeight() / tileSize - playerLocation[1] };
		caravanLocation = new int[] { caravanLocation[0], canvas.getHeight() / tileSize - caravanLocation[1] };
		for (int i = 0; i < locations.size; i++) {
			for (int j = 0; j < locations.get(i).length; j++) {
				locations.get(i)[j] = new int[] { locations.get(i)[j][0],
						canvas.getHeight() / tileSize - locations.get(i)[j][1] };
			}
		}

		// Instantiate the floors:
		Array<Obstacles> floorsArr = new Array<>();
		Obstacles floorTemp;
		// Take out smog for any floor tile types?
		// We need a tile for just the indoors or we need to manually place it as a
		// layer in tiled when the json reader is ready.
		for (int i = 0; i < grassLocations.length; i++) {
			floorTemp = new Obstacles(grassLocations[i][0] * tileSize + tileOffset,
					grassLocations[i][1] * tileSize + tileOffset, grassTexture, SCALE);
			floorsArr.add(floorTemp);
			floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(floorTemp);
		}
		for (int i = 0; i < rockLocations.length; i++) {
			floorTemp = new Obstacles(rockLocations[i][0] * tileSize + tileOffset,
					rockLocations[i][1] * tileSize + tileOffset, rockTexture, SCALE);
			floorsArr.add(floorTemp);
			floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(floorTemp);
		}
		for (int i = 0; i < dirtLocations.length; i++) {
			floorTemp = new Obstacles(dirtLocations[i][0] * tileSize + tileOffset,
					dirtLocations[i][1] * tileSize + tileOffset, dirtTexture, SCALE);
			floorsArr.add(floorTemp);
			floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(floorTemp);
		}
		for (int i = 0; i < dirtMushroomLocations.length; i++) {
			floorTemp = new Obstacles(dirtMushroomLocations[i][0] * tileSize + tileOffset,
					dirtMushroomLocations[i][1] * tileSize + tileOffset, dirtMushroomTexture, SCALE);
			floorsArr.add(floorTemp);
			floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(floorTemp);
		}
		for (int i = 0; i < brickFloorLocations.length; i++) {
			floorTemp = new Obstacles(brickFloorLocations[i][0] * tileSize + tileOffset,
					brickFloorLocations[i][1] * tileSize + tileOffset, brickFloorTexture, SCALE);
			floorsArr.add(floorTemp);
			floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(floorTemp);
		}
		for (int i = 0; i < brickFloorCrackedLocations.length; i++) {
			floorTemp = new Obstacles(brickFloorCrackedLocations[i][0] * tileSize + tileOffset,
					brickFloorCrackedLocations[i][1] * tileSize + tileOffset, brickFloorCrackedTexture, SCALE);
			floorsArr.add(floorTemp);
			floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(floorTemp);
		}
		for (int i = 0; i < brickFloorCrackedTopLocations.length; i++) {
			floorTemp = new Obstacles(brickFloorCrackedTopLocations[i][0] * tileSize + tileOffset,
					brickFloorCrackedTopLocations[i][1] * tileSize + tileOffset, brickFloorCrackedTopTexture, SCALE);
			floorsArr.add(floorTemp);
			floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(floorTemp);
		}

		// Instantiate the walls:
		Array<Obstacles> obstaclesArr = new Array<>();
		Obstacles wallTemp;
		for (int i = 0; i < brickWallLocations.length; i++) {
			wallTemp = new Obstacles(brickWallLocations[i][0] * tileSize + tileOffset,
					brickWallLocations[i][1] * tileSize + tileOffset, brickWallTexture, SCALE);
			wallTemp.setTexture(brickWallTexture, 4f);
			obstaclesArr.add(wallTemp);
			wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(wallTemp);
			wallTemp.activatePhysics(world);

			tiles[brickWallLocations[i][0]][brickWallLocations[i][1]] = true;
			tileGrid[brickWallLocations[i][0]][brickWallLocations[i][1]] = true;
		}
		for (int i = 0; i < brickWallTopOpenLocations.length; i++) {
			wallTemp = new Obstacles(brickWallTopOpenLocations[i][0] * tileSize + tileOffset,
					brickWallTopOpenLocations[i][1] * tileSize + tileOffset, brickWallTopOpenTexture, SCALE);
			wallTemp.setTexture(brickWallTopOpenTexture, 4f);
			obstaclesArr.add(wallTemp);
			wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(wallTemp);
			wallTemp.activatePhysics(world);

			tiles[brickWallTopOpenLocations[i][0]][brickWallTopOpenLocations[i][1]] = true;
			tileGrid[brickWallTopOpenLocations[i][0]][brickWallTopOpenLocations[i][1]] = true;
		}
		for (int i = 0; i < brickWallSidesOpenLocations.length; i++) {
			wallTemp = new Obstacles(brickWallSidesOpenLocations[i][0] * tileSize + tileOffset,
					brickWallSidesOpenLocations[i][1] * tileSize + tileOffset, brickWallSidesOpenTexture, SCALE);
			wallTemp.setTexture(brickWallSidesOpenTexture, 4f);
			obstaclesArr.add(wallTemp);
			wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(wallTemp);
			wallTemp.activatePhysics(world);

			tiles[brickWallSidesOpenLocations[i][0]][brickWallSidesOpenLocations[i][1]] = true;
			tileGrid[brickWallSidesOpenLocations[i][0]][brickWallSidesOpenLocations[i][1]] = true;
		}
		for (int i = 0; i < brickWallCrackedLocations.length; i++) {
			wallTemp = new Obstacles(brickWallCrackedLocations[i][0] * tileSize + tileOffset,
					brickWallCrackedLocations[i][1] * tileSize + tileOffset, brickWallCrackedTexture, SCALE);
			wallTemp.setTexture(brickWallCrackedTexture, 4f);
			obstaclesArr.add(wallTemp);
			wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(wallTemp);
			wallTemp.activatePhysics(world);

			tiles[brickWallCrackedLocations[i][0]][brickWallCrackedLocations[i][1]] = true;
			tileGrid[brickWallCrackedLocations[i][0]][brickWallCrackedLocations[i][1]] = true;
		}
		for (int i = 0; i < brickWallTopLocations.length; i++) {
			wallTemp = new Obstacles(brickWallTopLocations[i][0] * tileSize + tileOffset,
					brickWallTopLocations[i][1] * tileSize + tileOffset, brickWallTopTexture, SCALE);
			obstaclesArr.add(wallTemp);
			wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(wallTemp);
			wallTemp.activatePhysics(world);

			tiles[brickWallTopLocations[i][0]][brickWallTopLocations[i][1]] = true;
			tileGrid[brickWallTopLocations[i][0]][brickWallTopLocations[i][1]] = true;
		}
		for (int i = 0; i < borderSmogLocations.length; i++) {
			wallTemp = new Obstacles(borderSmogLocations[i][0] * tileSize + tileOffset,
					borderSmogLocations[i][1] * tileSize + tileOffset, borderSmogTexture, SCALE);
			obstaclesArr.add(wallTemp);
			wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
			addObject(wallTemp);
			wallTemp.activatePhysics(world);

			tiles[borderSmogLocations[i][0]][borderSmogLocations[i][1]] = true;
			tileGrid[borderSmogLocations[i][0]][borderSmogLocations[i][1]] = true;
		}

		// *************************** CARAVAN, PLAYER, AND WEAPON
		// ***************************
		// Instantiate the caravan:
		caravan = new Caravan(caravanLocation[0] * tileSize + tileOffset, caravanLocation[1] * tileSize + tileOffset,
				5, caravanTexture, survivorITexture, SCALE * 2, displayFontInteract);
		addObject(caravan);
		caravan.activatePhysics(world);

		// Instantiate the player:
		player = new Player(playerLocation[0] * tileSize + tileOffset, playerLocation[1] * tileSize + tileOffset,
				playerTextureUp, playerTextureDown, playerTextureRight, playerTextureLeft, playerTextureIdle, input, SCALE);
		if (isInvincible) {
			player.setHealth(10000);
		}
		addObject(player);
		player.activatePhysics(world);
		player.setAwake(true);

		// Instantiate the weapon:
		weapon = new Weapon(player.getPosition().x, player.getPosition().y);
		player.attachLightToPlayer(weapon.getAbsorbSensor());

		// Instantiate the trees:
		Obstacles treeTemp;
		for (int i = 0; i < treeLocations.length; i++) {
			treeTemp = new Obstacles(treeLocations[i][0] * tileSize + tileOffset, treeLocations[i][1] * tileSize + tileOffset,
					treeTexture, SCALE);
			obstaclesArr.add(treeTemp);
			addObject(treeTemp);
			treeTemp.activatePhysics(world);

			tiles[treeLocations[i][0]][treeLocations[i][1]] = true;
			tileGrid[treeLocations[i][0]][treeLocations[i][1]] = true;
		}
		for (int i = 0; i < treeTallLocations.length; i++) {
			treeTemp = new Obstacles(treeTallLocations[i][0] * tileSize + tileOffset,
					treeTallLocations[i][1] * tileSize + tileOffset, treeTallTexture, SCALE);
			obstaclesArr.add(treeTemp);
			addObject(treeTemp);
			treeTemp.activatePhysics(world);

			tiles[treeTallLocations[i][0]][treeTallLocations[i][1]] = true;
			tileGrid[treeTallLocations[i][0]][treeTallLocations[i][1]] = true;
		}
		for (int i = 0; i < treeBallLocations.length; i++) {
			treeTemp = new Obstacles(treeBallLocations[i][0] * tileSize + tileOffset,
					treeBallLocations[i][1] * tileSize + tileOffset, treeBallTexture, SCALE);
			obstaclesArr.add(treeTemp);
			addObject(treeTemp);
			treeTemp.activatePhysics(world);

			tiles[treeBallLocations[i][0]][treeBallLocations[i][1]] = true;
			tileGrid[treeBallLocations[i][0]][treeBallLocations[i][1]] = true;
		}

		// Gives ammo in debug mode
		if (isDebug()) {
			weapon.setNumAmmo(1000);
		}

		// TO DO: update visuals for purified smog
		purifiedAir = new PurifiedQueue(pureAirTexture, world, SCALE);
		toxicAir = new ToxicQueue(toxicAirTexture, world, SCALE);

		// Instantiate the enemies:
		enemyArr = new Array<Enemy>();
		// shriekerArr = new Array<ShriekerEnemy>();
		// Shrieker enemies
		// int numShriekers = 1;
		// for (int i = 0; i < numShriekers; i++) {
		// ShriekerEnemy shriekTemp = new ShriekerEnemy(player.getX() + 200,
		// player.getY() - 50, enemyTextureUp,
		// enemyTextureDown, enemyTextureRight, enemyTextureLeft, enemyTextureIdle,
		// SCALE);
		// enemyArr.add(shriekTemp);
		// shriekerArr.add(shriekTemp);
		// shriekTemp.activatePhysics(world);
		// addObject(shriekTemp);
		// enemyControllers
		// .add(new ShriekerEnemyController(tileGrid, tileSize, tileOffset, shriekTemp,
		// player, shriekerArr));
		// }

		for (int i = 0; i < enemyLocations.length; i++) {
			if (i % 2 == 0) {
				FloatingEnemy enemyTemp = new FloatingEnemy(enemyLocations[i][0] * tileSize + tileOffset,
						enemyLocations[i][1] * tileSize + tileOffset, enemyTextureUp, enemyTextureDown, enemyTextureRight,
						enemyTextureLeft, enemyTextureIdle, SCALE);
				enemyArr.add(enemyTemp);
				enemyTemp.activatePhysics(world);
				addObject(enemyTemp);
				// enemyControllers.add(new ChaserEnemyController(tileGrid, tileSize,
				// tileOffset, enemyTemp, player, shriekerArr));
				enemyControllers
						.add(new FloatingEnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr, toxicAir));
			}
			if (i % 2 == 1) {
				ScoutEnemy enemyTemp = new ScoutEnemy(enemyLocations[i][0] * tileSize + tileOffset,
						enemyLocations[i][1] * tileSize + tileOffset, enemyTextureUp, enemyTextureDown, enemyTextureRight,
						enemyTextureLeft, enemyTextureIdle, vineTextures, SCALE, world);
				enemyArr.add(enemyTemp);
				enemyTemp.activatePhysics(world);
				addObject(enemyTemp);

				enemyControllers.add(new ScoutEnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr));
			}
		}

		// *************************** SURVIVORS ***************************
		survivorArr = new Array<>();
		Survivor survivorTemp;
		// // Instantiate the survivors:
		for (int i = 0; i < survivorLocations.length; i++) {
			// Survivor survivorTemp = new Survivor(i, survivorLocations[i][0] * tileSize,
			// survivorLocations[i][1] * tileSize, survivorTexture);
			survivorTemp = new Survivor(i, survivorLocations[i][0] * tileSize + tileOffset,
					survivorLocations[i][1] * tileSize + tileOffset, survivorTexture, fHeartTexture, displayFontInteract, SCALE);
			survivorTemp.activatePhysics(world);

			// System.out.println(collisionController.getGameObstacleType(survivorTemp.getUserData()));
			survivorArr.add(survivorTemp);
			addObject(survivorTemp);
			survivorControllers.add(new SurvivorController(survivorTemp, caravan.getPosition(), player.getPosition(),
					tileGrid, tileSize, tileOffset));
		}

		// *************************** SMOG OBSTACLES ***************************

		// Starting Area:
		for (int i = playerLocation[0] - startingBox[0]; i < playerLocation[0] + startingBox[0]; i++) {
			for (int j = playerLocation[1] - startingBox[1]; j < playerLocation[1] + startingBox[1]; j++) {
				tiles[i][j] = true;
			}
		}

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

		// ############## ONLY FOR PLACING SMOG AT ARBITRARY POINTS ACROSS THE GRID
		// ##############
		// We want the smog to be dispersed over a small area to start
		// Small Smog
		// int smogXBeginning = canvas.getWidth() / 3;
		// int smogXEnd = canvas.getWidth() * 2 / 3;
		// int smogYBeginning = canvas.getHeight() / 7;
		// int smogYEnd = canvas.getHeight() * 3 / 7;
		// int step = 20;
		// Full Screen Smog
		// int smogXBeginning = canvas.getWidth() / 10;
		// int smogXEnd = canvas.getWidth() * 9 / 10;
		// int smogYBeginning = canvas.getHeight() / 10;
		// int smogYEnd = canvas.getHeight() * 9 / 10;
		// int step = 15;

		// Smog smogT = new Smog(canvas.getWidth() / 3, canvas.getHeight() / 3,
		// smogTexture);
		// for (int i = smogXBeginning; i < smogXEnd; i += step) {
		// for (int j = smogYBeginning; j < smogYEnd; j += step) {
		// smogT = new Smog(i, j, smogTexture);
		// //smogT.setTextureRegion(smogTexture);
		// addObject(smogT);
		// smogArr.add(smogT);
		// }
		// }

		// *************************** SORTING / DRAWING ORDER
		// ***************************
		// Sort statics and player in order of descending y value for position.
		// Array<Obstacle> tempArr = new Array<>();
		// float maxY = 0;
		// int maxYind = 0;
		// for (int i = 0; i < staticsAndPlayer.size; i++) {
		// for (int j = 0; j < staticsAndPlayer.size; j++) {
		// if (!tempArr.contains(staticsAndPlayer.get(i), false) &&
		// staticsAndPlayer.get(i).getPosition().y > maxY) {
		// maxY = staticsAndPlayer.get(i).getPosition().y;
		// maxYind = i;
		// }
		// }
		// tempArr.add(staticsAndPlayer.get(maxYind));
		// maxY = 0;
		// maxYind = 0;
		// }
		// staticsAndPlayer = tempArr;

		// *************************** EXTRAS / NOTES ***************************
		// Create single smog tile
		// Smog smog = new Smog(canvas.getWidth() / 3, canvas.getHeight() / 3);
		// smog.setTextureRegion(smogTexture);
		// addObject(smog);

	}

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
//		if (!super.preUpdate(dt)) {
//			return false;
//		}
//
//		if (!isFailure() && player.getY() < -1) {
//			setFailure(true);
//			return false;
//		}

		if (player.getHealth() <= 0)
		{
			setFailure(true);
		}

		// Handle resets
		if (input.didReset()) {
			reset();
		}

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
	public void update(float dt) {
		// Read input
		input.readInput();

		if (input.didPause())
		{
			if (!paused && !unpausing)
			{
				paused = true;
				pausing = true;
				for (int i = 0; i < enemyArr.size; i++)
				{
					enemyArr.get(i).setBodyType(BodyDef.BodyType.StaticBody);
				}
				for (int i = 0; i < survivorArr.size; i++)
				{
					survivorArr.get(i).setActive(false);
				}
				for (int i = 0; i < purifiedAir.getQueue().length; i++)
				{
					purifiedAir.getQueue()[i].setActive(false);
				}
				for (int i = 0; i < toxicAir.getQueue().length; i++)
				{
					toxicAir.getQueue()[i].setActive(false);
				}
				return;
			}
			else if (paused && !pausing)
			{
				paused = false;
				unpausing = true;
				for (int i = 0; i < enemyArr.size; i++)
				{
					enemyArr.get(i).setBodyType(BodyDef.BodyType.DynamicBody);
				}
				for (int i = 0; i < survivorArr.size; i++)
				{
					survivorArr.get(i).setActive(true);
				}
				for (int i = 0; i < purifiedAir.getQueue().length; i++)
				{
					purifiedAir.getQueue()[i].setActive(true);
				}
				for (int i = 0; i < toxicAir.getQueue().length; i++)
				{
					toxicAir.getQueue()[i].setActive(true);
				}
			}
		}
		else
		{
			pausing = false;
			unpausing = false;
			if (paused) {
				return;
			}
		}

		if (input.isNextLevel()) {
			if (level == 1)
			{
				level = 2;
				reset();
			}
			else {
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
		if (player.getX() < 10)
		{
			player.setPosition(10, player.getBody().getPosition().y);
		}
		if (player.getX() >= canvas.getWidth()-10)
		{
			player.setPosition(canvas.getWidth()-10, player.getBody().getPosition().y);
		}
		if (player.getY() < 10)
		{
			player.setPosition(player.getBody().getPosition().x, 10);
		}
		if (player.getY() >= canvas.getHeight()-10)
		{
			player.setPosition(player.getBody().getPosition().x, canvas.getHeight()-10);
		}

		// Update UI elements
		airBar.update(weapon.getNumAmmo());

		weapon.update(player.getPosition(), canvas.unproject(input.getMousePos()), input.getShootDir());
		// Check if the weapon is firing

		if(weapon.fire()){
			purifiedAir.attack(weapon.getBullets(), weapon.getPosition(), weapon.getImpulses());
			weapon.incrementAmmo(-weapon.getBullets());
		}
		purifiedAir.update();
		toxicAir.update();

		// Process Collisions
		collisionController.update(world,player,weapon);
		// This detects and resolves all collisions between the weapon sensor and any collided smog tiles
//		if (weapon.isFiring()) {
//			 collisionController.processCollisions(player, weapon, smogArr);
//		}
		//collisionController.processCollisions(player,weapon,smogArr);

		// Update smog animations
		for (Smog obj : smogArr) {
			obj.update();
			if (obj.isFaded())
			{
				obj.markRemoved(true);
			}
		}
		/*
		for (Cliff obj : cliffArr) {
			obj.update();
		}


		 */
		// Update enemy positions and states
		for (int i = 0; i < enemyArr.size; i++)
		{
			enemyArr.get(i).update(enemyControllers.get(i).getAction());
			if (enemyArr.get(i).getX() < 10)
			{
				enemyArr.get(i).setPosition(10, enemyArr.get(i).getBody().getPosition().y);
			}
			if (enemyArr.get(i).getX() >= canvas.getWidth()-10)
			{
				enemyArr.get(i).setPosition(canvas.getWidth()-10, enemyArr.get(i).getBody().getPosition().y);
			}
			if (enemyArr.get(i).getY() < 10)
			{
				enemyArr.get(i).setPosition(enemyArr.get(i).getBody().getPosition().x, 10);
			}
			if (enemyArr.get(i).getY() >= canvas.getHeight()-10)
			{
				enemyArr.get(i).setPosition(enemyArr.get(i).getBody().getPosition().x, canvas.getHeight()-10);
			}
		}

		// This section will be handled by collisionController in the future with the exception of obj.update(..)s and
		// removing objects from arrays once they are deactivated:
		// -------------------------------------------------------------------------------------------------------------
		// Update survivor positions and states
		for (int i = 0; i < survivorArr.size; i++)
		{
			// This will be handled by collisionController in the future
			survivorArr.get(i).update(survivorControllers.get(i).getAction());
			survivorArr.get(i).update();
			if(survivorArr.get(i).isInteractable() && input.didCollectSurvivor()) {
				survivorArr.get(i).setInteractable(false);
				if (!survivorArr.get(i).isFollowing()) {
					player.addToFollowing(survivorArr.get(i));
				}
				survivorArr.get(i).follow();
			}
			if (!survivorArr.get(i).isAlive()){
				player.removeFromFollowing(survivorArr.get(i));
				setFailure(true);
			}
		}
		caravan.update();
		// Update caravan state
		if(caravan.getBody().getFixtureList().get(1).testPoint(player.getPosition())) {
			caravan.setInteractable(true);
		}
		else {
			caravan.setInteractable(false);
		}
		if(caravan.isInteractable() && input.didDropSurvivors()) {
			if(numRescued == numSurvivorsTotal) {
				setComplete(true);
			}
			for(int i = 0; i < survivorArr.size; i++) {
				if(survivorArr.get(i).isFollowing()) {
					survivorArr.get(i).rescue();
					player.removeFromFollowing(survivorArr.get(i));
					survivorArr.get(i).deactivatePhysics(world);
					survivorArr.removeIndex(i);
					numRescued++;
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
//		for (int i = 0; i < staticsAndPlayer.size; i++) {
//			if (staticsAndPlayer.get(i).getType() == GameObstacle.ObstacleType.PLAYER) {
//				staticsAndPlayer.removeIndex(i);
//				break;
//			}
//		}
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
		for (int i = player.getMaxHealth()-1; i >= 0; i--){
			if (player.getHealth() <= i){
				heartArr.get(i).setTexture(sHeartTexture);
			}
		}

	}

	/**
	 * Add a new bullet to the world and send it in the right direction.
	 */
//	private void createBullet() {
//		JsonValue bulletjv = constants.get("bullet");
//		float offset = bulletjv.getFloat("offset",0);
//		offset *= (player.isFacingRight() ? 1 : -1);
//		float radius = bulletTexture.getRegionWidth()/(2.0f*scale.x);
//		WheelObstacle bullet = new WheelObstacle(player.getX()+offset, player.getY(), radius);
//
//	    bullet.setName("bullet");
//		bullet.setDensity(bulletjv.getFloat("density", 0));
//	    bullet.setDrawScale(scale);
//	    bullet.setTexture(bulletTexture);
//	    bullet.setBullet(true);
//	    bullet.setGravityScale(0);
//
//		// Compute position and velocity
//		float speed = bulletjv.getFloat( "speed", 0 );
//		speed  *= (player.isFacingRight() ? 1 : -1);
//		bullet.setVX(speed);
//		addQueuedObject(bullet);
//
//		fireId = playSound( fireSound, fireId );
//	}

	/**
	 * Remove a new bullet from the world.
	 *
	 * @param  bullet   the bullet to remove
	 */
//	public void removeBullet(Obstacle bullet) {
//	    bullet.markRemoved(true);
//	    plopId = playSound( plopSound, plopId );
//	}


	/**
	 * Called when the Screen is paused.
	 *
	 * We need this method to stop all sounds when we pause.
	 * Pausing happens when we switch game modes.
	 */
//	public void pause() {
//		jumpSound.stop(jumpId);
//		plopSound.stop(plopId);
//		fireSound.stop(fireId);
//	}
}