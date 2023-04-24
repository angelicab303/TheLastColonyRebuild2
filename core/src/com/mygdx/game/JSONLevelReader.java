package com.mygdx.game;

import java.io.FileReader;
import java.util.Objects;

import assets.AssetDirectory;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Obstacles.*;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.EnemyControllers.EnemyController;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;
import obstacle.Obstacle;
import util.PooledList;

public class JSONLevelReader {
    private World world;
    private InputController input;
    private PooledList<Obstacle> objects;
    private float scale;
    private int width;
    private int height;
    private boolean[][] tileGrid;
    private int tileSize;
    private int tileOffset;
    private int smogTileSize;
    private int smogTileOffset;
    private AssetDirectory directory;
    private Rectangle bounds;

    // Declare and instantiate IDs for each element of the level
    private int caravanID = 0;
    private int playerID = 0;
    private int numSurvivorIDs = 1;
    private int[] survivorIDs = new int[numSurvivorIDs];
    private int numEnemyIDs = 2;
    private int[] enemyIDs = new int[numEnemyIDs];
    private int numBeforeFloors = 2 + numSurvivorIDs + numEnemyIDs;
    private int numFloorIDs = 8;
    private int[] floorIDs = new int[numFloorIDs];
    private int numWallIDs = 10;
    private int[] wallIDs = new int[numWallIDs];
    private Array<Obstacles> wallArr = new Array<Obstacles>();
    private int numTreeIDs = 3;
    private int[] treeIDs = new int[numTreeIDs];
    private Array<Obstacles> treeArr = new Array<Obstacles>();
    private int numSmogIDs = 0;
    private int[] smogIDs = new int[numSmogIDs];
    private Array<Obstacles> smogArr = new Array<Obstacles>();

    private int[] tIDs = new int[numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs + numSmogIDs];

    // Declare variables for entities that are stored in GameplayController and passed in
    private Caravan caravan;
    private Player player;
    private Weapon weapon;
    /** Survivor list **/
    private Array<Survivor> survivorArr;
    /** survivor controller list **/
    private Array<SurvivorController> survivorControllers;
    /** Enemy list */
    private Array<Enemy> enemyArr;
    /** Shrieker Enemy List */
    private Array<ShriekerEnemy> shriekerArr;
    /** Enemy Controller */
    private Array<EnemyController> enemyControllers;

    // Declare variables for each entity that has a lot of instantiations
    private Obstacles floorTemp;
    private Obstacles wallTemp;
    private Obstacles treeTemp;
    private Texture[] playerDirectionTextures;
    private Texture[] enemyDirectionTextures;
    private Texture enemyTextureIdle;
    private Texture survivorITexture;
    private BitmapFont displayFontInteract;
    private Texture heart;

    // Texture Stuff
    JsonValue tileIDs;

    public JSONLevelReader(AssetDirectory directory, Rectangle bounds, World world, InputController input, PooledList<Obstacle> objects, float scale, boolean[][] tileGrid, int tileSize, int tileOffset, int smogTileSize, int smogTileOffset, Texture[] playerDirectionTextures, Texture[] enemyDirectionTextures, Texture enemyTextureIdle, Texture survivorITexture, BitmapFont displayFontInteractive, Texture heart, Player player, Weapon weapon) {
        this.directory = directory;
        this.bounds = bounds;
        this.world = world;
        this.input = input;
        this.objects = objects;
        this.scale = scale;
        this.tileGrid = tileGrid;
        this.tileSize = tileSize;
        this.tileOffset = tileOffset;
        this.smogTileSize = smogTileSize;
        this.smogTileOffset = smogTileOffset;
        this.playerDirectionTextures = playerDirectionTextures;
        this.enemyDirectionTextures = enemyDirectionTextures;
        this.enemyTextureIdle = enemyTextureIdle;
        this.survivorITexture = survivorITexture;
        this.displayFontInteract = displayFontInteractive;
        this.heart = heart;
        this.player = player;
        this.weapon = weapon;
        try {
            // Read the JSON file into a FileReader object
            FileReader tilesReader = new FileReader("assets/tiles/LastColonyTilesetCorrect.json");
            FileReader mapReader = new FileReader("assets/levels/LastColonyAlphaCorrect.json");

            // Send the fileReader to a new JsonReader object
            JsonReader tilesJSONReader = new JsonReader();
            JsonValue tilesJSON = tilesJSONReader.parse(tilesReader);

            tileIDs = tilesJSON.get("tiles");

            // Initialize relevant arrays for object creation:
            survivorArr = new Array<>();
            enemyArr = new Array<Enemy>();
            shriekerArr = new Array<ShriekerEnemy>();
            survivorControllers = new Array<SurvivorController>();
            enemyControllers = new Array<EnemyController>();

            /** This is the order of the naming system for the files:
             * Caravan first
             * Player next
             * Survivor next
             * Enemy next
             * Ground floor next
             * Wall tiles next
             * Tree tiles next
             * Smog tiles next
             * **/

            // Get each of the IDs from the tile specification JSON
            caravanID = tileIDs.get(0).getInt("id");
            playerID = tileIDs.get(1).getInt("id");

            tIDs[0] = caravanID;
            tIDs[1] = playerID;

            // Get each of the survivor IDs
            for (int i = 0; i < numSurvivorIDs; i++) {
                survivorIDs[i] = tileIDs.get(2 + i).getInt("id");
                tIDs[2 + i] = survivorIDs[i];
            }
            // Get each of the enemy IDs
            for (int i = 0; i < numEnemyIDs; i++) {
                enemyIDs[i] = tileIDs.get(2 + numSurvivorIDs + i).getInt("id");
                tIDs[2 + numSurvivorIDs + i] = enemyIDs[i];
            }
            // Get each of the floor IDs
            for (int i = 0; i < numFloorIDs; i++) {
                floorIDs[i] = tileIDs.get(numBeforeFloors + i).getInt("id");
                tIDs[numBeforeFloors + i] = floorIDs[i];
            }
            // Get each of the wall IDs
            for (int i = 0; i < numWallIDs; i++) {
                wallIDs[i] = tileIDs.get(numBeforeFloors + numFloorIDs + i).getInt("id");
                tIDs[numBeforeFloors + numFloorIDs + i] = wallIDs[i];
            }
            // Get each of the tree IDs
            for (int i = 0; i < numTreeIDs; i++) {
                treeIDs[i] = tileIDs.get(numBeforeFloors + numFloorIDs + numWallIDs + i).getInt("id");
                tIDs[numBeforeFloors + numFloorIDs + numWallIDs + i] = treeIDs[i];
            }
            // Get each of the smog IDs
            for (int i = 0; i < numSmogIDs; i++) {
                smogIDs[i] = tileIDs.get(numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs + i).getInt("id");
                tIDs[numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs + i] = smogIDs[i];
            }

            // Close the tile reader
            tilesReader.close();
            // ******************************************* END OF TILEREADER *******************************************

            // Send the fileReader to a new JsonReader object
            JsonReader mapJSONReader = new JsonReader();
            JsonValue mapJSON = mapJSONReader.parse(mapReader);

            // Get JSON layers of the map
            JsonValue layers = mapJSON.get("layers");
            width = layers.get(0).getInt("width");
            height = layers.get(0).getInt("height");

            // Loop through each of the layers and first simply instantiate the caravan and player, in that order.
            int caravanX = 0;
            int caravanY = 0;
            int playerX = 0;
            int playerY = 0;
            for (int i = 0; i < layers.size; i++) {
                // Loop through the layer's data and retrieve each data array
                JsonValue layerData = layers.get(i).get("data");
                for (int j = 0; j < layerData.size; j++) {
                    int dataValue = layerData.getInt(j) - 1;
                    // Do something with the data value...
                    if (dataValue == 0) {
                        caravanX = j % width;
                        caravanY = j / (width - 1);
                    } else if (dataValue == 1) {
                        playerX = j % width;
                        playerY = j / (width - 1);
                    }
                }
            }
            createObject(caravanX, caravanY, 0);
            createObject(playerX, playerY, 1);

            // Loop through each of the layers and instantiate each object from the id
            for (int i = 0; i < layers.size; i++) {
                // Loop through the layer's data and retrieve each data array
                JsonValue layerData = layers.get(i).get("data");
                for (int j = 0; j < layerData.size; j++) {
                    int dataValue = layerData.getInt(j) - 1;
                    // Do something with the data value...
                    if (dataValue > 0) {
                        createObject(j % width, j / (width - 1), dataValue);
                    }
                }
            }

            // Close the map reader
            mapReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * Immediately adds the object to the physics world
     *
     * param obj The object to add
     */
    public void addObject(Obstacle obj) {
        assert inBounds(obj) : "Object is not in bounds";
        objects.add(obj);
        //obj.activatePhysics(world);
    }

    public void createObject(int x, int y, int id) {
        if (id == 0) {
            createCaravan(x, y, scale);
        } else if (id == 1) {
            createPlayer(x, y, scale);
        } else if (id > 1 && id < 2 + numSurvivorIDs) {
            createSurvivor(x, y, id, scale);
        } else if (id > 1 + numSurvivorIDs && id < 2 + numSurvivorIDs + numEnemyIDs) {
            createEnemy(x, y, id, scale);
        } else if (id >= numBeforeFloors && id < 1 + numBeforeFloors + numFloorIDs) {
            createFloor(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs && id < 1 + numBeforeFloors + numFloorIDs + numWallIDs) {
            createWall(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs + numWallIDs && id < 1 + numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs) {
            createTree(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs && id < 1 + numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs + numSmogIDs) {
            createSmog(x, y, id, scale);
        } else {
            System.out.println("Error - ID " + id + " out of bounds");
            System.out.println("The bounds are from 0 to " + tIDs.length);
        }
    }

    public TextureRegion getTextureRegionKey(int textReg) {
        return new TextureRegion(directory.getEntry("tiles:" + tileIDs.get(textReg).getString("image"), Texture.class));
    }

    public Texture getTextureKey(int textReg) {
        return directory.getEntry("tiles:" + tileIDs.get(textReg).getString("image"), Texture.class);
    }

    public void createCaravan(int x, int y, float scale) {
        caravan = new Caravan(x * tileSize + tileOffset, y * tileSize + tileOffset, 5, getTextureRegionKey(0), survivorITexture, scale, displayFontInteract);
        addObject(caravan);
        caravan.activatePhysics(world);
    }

    public Caravan getCaravan() {
        return caravan;
    }

    public void createPlayer(int x, int y, float scale) {
        // Instantiate the player:
        player = new Player(x * tileSize + tileOffset, y * tileSize + tileOffset, playerDirectionTextures[0], playerDirectionTextures[1], playerDirectionTextures[2], playerDirectionTextures[3], input, scale);
        addObject(player);
        player.activatePhysics(world);
        player.setAwake(true);

        // Instantiate the weapon:
        weapon = new Weapon(player.getPosition().x, player.getPosition().y);
    }

    public Player getPlayer() {
        return player;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void createSurvivor(int x, int y, int id, float scale) {
        Survivor survivorTemp;
        // Instantiate a survivor:
        //Survivor survivorTemp = new Survivor(i, survivorLocations[i][0] * tileSize, survivorLocations[i][1] * tileSize, survivorTexture);
        survivorTemp = new Survivor(survivorArr.size, x * tileSize + tileOffset, y * tileSize + tileOffset, getTextureRegionKey(id), heart, displayFontInteract, scale);
        survivorTemp.activatePhysics(world);

        survivorArr.add(survivorTemp);
        addObject(survivorTemp);
        survivorControllers.add(new SurvivorController(survivorTemp, caravan.getPosition(), player.getPosition(), tileGrid, tileSize, tileOffset));
    }

    public Array<Survivor> getSurvivors() {
        return survivorArr;
    }
    public Array<SurvivorController> getSurvivorControllers() {
        return survivorControllers;
    }

    public void createEnemy(int x, int y, int id, float scale) {
        Enemy enemyTemp;
        enemyTemp = new Enemy(x * tileSize + tileOffset, y * tileSize + tileOffset, enemyDirectionTextures[0], enemyDirectionTextures[1], enemyDirectionTextures[2], enemyDirectionTextures[3], enemyTextureIdle, scale);
        enemyTemp.activatePhysics(world);

        enemyArr.add(enemyTemp);
        addObject(enemyTemp);
        enemyControllers.add(new EnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr));
    }

    public Array<Enemy> getEnemies() {
        return enemyArr;
    }
    public Array<EnemyController> getEnemyControllers() {
        return enemyControllers;
    }

    public void createFloor(int x, int y, int id, float scale) {
        wallTemp = new Obstacles(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        wallArr.add(wallTemp);
        //cliffTemp.setAwake(true);
        wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addObject(wallTemp);
//        wallTemp.activatePhysics(world);
    }

    public void createWall(int x, int y, int id, float scale) {
        wallTemp = new Obstacles(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        wallArr.add(wallTemp);
        //cliffTemp.setAwake(true);
        wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addObject(wallTemp);
        wallTemp.activatePhysics(world);
//        tiles[wallLocations[i][0]][wallLocations[i][1]] = true;
//        tileGrid[wallLocations[i][0]][wallLocations[i][1]] = true;
    }

    public void createTree(int x, int y, int id, float scale) {
        treeTemp = new Obstacles(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        treeArr.add(treeTemp);
        //cliffTemp.setAwake(true);
        treeTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addObject(treeTemp);
        treeTemp.activatePhysics(world);
    }

    public void createSmog(int x, int y, int id, float scale) {

    }

}
