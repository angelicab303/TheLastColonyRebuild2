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
	private long fireId = -1;

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
	/** Used for playtesting, player is invincible */
	private boolean isInvincible = false;



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
		treeTexture = new TextureRegion(directory.getEntry("images:tree1",Texture.class));
		smogTexture = directory.getEntry("images:testSmog", Texture.class);
		airBarTexture = directory.getEntry("images:airBar", Texture.class);
		// pureAirTexture = new TextureRegion(directory.getEntry("images:smog1", Texture.class));
		pureAirTexture = directory.getEntry("images:weaponProjectile", Texture.class);
		smogTexture2 = new TextureRegion(directory.getEntry("images:smog2", Texture.class));
		caravanTexture = new TextureRegion(directory.getEntry("images:caravan1", Texture.class));
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

		vineTextures = new Texture[14];
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
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
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
		Shadow.setSize(32f);


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
		airBar = new AirBar(airBarTexture, weapon.getMaxNumAmmo(), weapon.getNumAmmo());

		// Hearts
		int numLives = player.getHealth();
		heartArr = new Array<Heart>(numLives);
		float heartX = canvas.camera.position.x + (canvas.getWidth()*cameraZoom)/2.0f - (30.0f * cameraZoom);
		float heartY = canvas.camera.position.y + (canvas.getHeight()*cameraZoom)/2.0f - (30.0f * cameraZoom);
		float spacing = 0.0f;

		for (int i = 0; i < numLives; i++){
			if (i > 0){
				spacing += 30.0f;
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

		// Update UI elements
		airBar.update(weapon.getNumAmmo());

		weapon.update(player.getPosition(), canvas.unproject(input.getMousePos()), input.getShootDir());
		// Check if the weapon is firing

		if(weapon.fire()){
			purifiedAir.attack(weapon.getBullets(), weapon.getPosition(), weapon.getImpulses());
			weapon.incrementAmmo(-weapon.getBullets());
		}
		purifiedAir.update();

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
				survivorArr.get(i).follow();
			}
			if (!survivorArr.get(i).isAlive()){
				setFailure(true);
			}
		}
		caravan.update();
		// Update caravan state
		if(caravan.getBody().getFixtureList().first().testPoint(player.getPosition())) {
			caravan.setInteractable(true);
		}
		else {
			caravan.setInteractable(false);
		}
		if(caravan.isInteractable() && input.didDropSurvivors()) {
			if(numRescued == survivorArr.size) {
				setComplete(true);
			}
			for(int i = 0; i < survivorArr.size; i++) {
				if(survivorArr.get(i).isFollowing()) {
					survivorArr.get(i).rescue();
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