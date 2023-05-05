package com.mygdx.game;

import java.io.*;
import java.util.Objects;

import assets.AssetDirectory;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.mygdx.game.EnemyControllers.FloatingEnemyController;
import com.mygdx.game.EnemyControllers.ScoutEnemyController;
import com.mygdx.game.Obstacles.*;
import com.mygdx.game.Obstacles.Enemies.Enemy;
import com.mygdx.game.EnemyControllers.EnemyController;
import com.mygdx.game.Obstacles.Enemies.FloatingEnemy;
import com.mygdx.game.Obstacles.Enemies.ScoutEnemy;
import com.mygdx.game.Obstacles.Enemies.ShriekerEnemy;
import obstacle.Obstacle;
import util.FilmStrip;
import util.PooledList;

public class JSONLevelReader {
    private World world;
    private int level;
    private OrthographicCamera camera;
    private InputController input;
    private PooledList<Obstacle> objects;
    private float scale;
    private int width;
    private int height;
    private boolean[][] tileGrid;
    private boolean[][] smogGrid;
    private int imageTileSize;
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
    private Array<FloorTile> floorArr = new Array<>();
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
    private boolean didCreateCaravan = false;
    private Player player;
    private boolean didCreatePlayer = false;
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
    private FloorTile floorTemp;
    private Obstacles wallTemp;
    private Obstacles treeTemp;
    private FilmStrip[] playerDirectionTextures;
    private FilmStrip[] enemyDirectionTextures;
    private ToxicQueue toxicAir;
    private Texture survivorITexture;
    private BitmapFont displayFontInteract;
    private Texture heart;
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

    // Texture Stuff
    JsonValue tileIDs;

    public JSONLevelReader(AssetDirectory directory, Rectangle bounds, World world, int level, OrthographicCamera camera, InputController input, PooledList<Obstacle> objects, Array<FloorTile> floorArr, float scale, boolean[][] tileGrid, boolean[][] smogGrid, int tileSize, int tileOffset, int smogTileSize, int smogTileOffset, FilmStrip[] playerDirectionTextures, FilmStrip[] enemyDirectionTextures, ToxicQueue toxicAir, Texture survivorITexture, BitmapFont displayFontInteractive, Texture heart, Player player, Weapon weapon) {
        this.directory = directory;
        this.bounds = bounds;
        this.world = world;
        this.level = level;
        this.camera = camera;
        this.input = input;
        this.objects = objects;
        this.floorArr = floorArr;
        this.scale = scale;
        this.tileGrid = tileGrid;
        this.smogGrid = smogGrid;
        this.tileSize = tileSize;
        this.imageTileSize = tileSize * 10;
        this.tileOffset = tileOffset;
        this.smogTileSize = smogTileSize;
        this.smogTileOffset = smogTileOffset;
        this.playerDirectionTextures = playerDirectionTextures;
        this.enemyDirectionTextures = enemyDirectionTextures;
        this.toxicAir = toxicAir;
        this.survivorITexture = survivorITexture;
        this.displayFontInteract = displayFontInteractive;
        this.heart = heart;
        this.player = player;
        this.weapon = weapon;
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

        try {
            // Read the JSON file into a FileReader object
            //InputStream inputStream = getClass().getResourceAsStream("assets/tiles/LastColonyTilesetCorrect.json");
            //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            //FileReader tilesReader = new FileReader(directory.getAssetFileName(directory.getEntry("tileset", JsonValue.class)));

            JsonValue levelStr = new JsonValue(false);
            if (level == 0) {
                levelStr = directory.getEntry("easyLevel", JsonValue.class);
            } else if (level == 1) {
                levelStr = directory.getEntry("mediumLevel", JsonValue.class);
            } else if (level == 2) {
                levelStr = directory.getEntry("alphaLevel", JsonValue.class);
            } else if (level == 3) {
                levelStr = directory.getEntry("symmetricalLevel", JsonValue.class);
            } else if (level == 4) {
                levelStr = directory.getEntry("apartmentLevel", JsonValue.class);
            } else if (level == 5) {
                levelStr = directory.getEntry("mazeLevel", JsonValue.class);
            }

            //FileReader mapReader = new FileReader(levelStr);

            // Send the fileReader to a new JsonReader object
            //JsonReader tilesJSONReader = new JsonReader();
            //JsonValue tilesJSON = tilesJSONReader.parse(tilesReader);
            JsonValue tilesJSON = directory.getEntry("tileset", JsonValue.class);

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
            //tilesReader.close();
            // ******************************************* END OF TILEREADER *******************************************

            // Send the fileReader to a new JsonReader object
            //JsonReader mapJSONReader = new JsonReader();
            //JsonValue mapJSON = mapJSONReader.parse(mapReader);
            JsonValue mapJSON = levelStr;

            // Get JSON layers of the map
            JsonValue layers = mapJSON.get("layers");
            width = layers.get(0).getInt("width");
            height = layers.get(0).getInt("height");
            System.out.println("Width: " + width + "\t\tHeight: " + height);

//            this.camera.setToOrtho(false, width * tileSize, height * tileSize);
            this.tileGrid = new boolean[width][height];

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
                        caravanY = height - (j / width);
                    } else if (dataValue == 1) {
//                        System.out.println("Found player");
                        playerX = j % width;
                        playerY = height - (j / width);
                    }
                }
            }
            createObject(caravanX, caravanY, 0);
            createObject(playerX, playerY, 1);
            didCreateCaravan = true;
            didCreatePlayer = true;

            // Loop through each of the layers and instantiate each object from the id
            for (int i = 0; i < layers.size; i++) {
                // Loop through the layer's data and retrieve each data array
                JsonValue layerData = layers.get(i).get("data");
                for (int j = 0; j < layerData.size; j++) {
                    int dataValue = layerData.getInt(j) - 1;
                    // Do something with the data value...
                    if (dataValue > 0) {
                        createObject(j % width, height - (j / width), dataValue);
                    }
                }
            }
            for (int i = 0; i < survivorArr.size; i++)
            {
                survivorControllers.add(new SurvivorController(survivorArr.get(i), caravan.getPosition(), this.player.getPosition(), this.tileGrid, this.smogGrid, tileSize, tileOffset));
            }
            for (int i = 0; i < enemyArr.size; i++)
            {
                enemyControllers.add(new FloatingEnemyController(this.tileGrid, tileSize, tileOffset, (FloatingEnemy)enemyArr.get(i), this.player, shriekerArr, toxicAir));
            }
            caravan.setMaxCapacity(survivorArr.size);
            if (caravan.getX() < 400f) {
                System.out.println("Finished loading JSON Level");
            }
            System.out.println("Finished loading JSON Level");

            // Close the map reader
            //mapReader.close();
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
        obj.activatePhysics(world);
    }

    /**
     * Immediately adds the object to the physics world
     *
     * param obj The object to add
     */
    public void addFloor(FloorTile obj) {
//        assert inBounds(obj) : "Object is not in bounds";
        floorArr.add(obj);
//        obj.activatePhysics(world);
    }

    public void createObject(int x, int y, int id) {
        //System.out.println(id + " " + x + " " + y);
        if (id == 0) {
            createCaravan(x, y, scale);
        } else if (id == 1) {
            createPlayer(x, y, scale);
        } else if (id > 1 && id < 2 + numSurvivorIDs) {
            createSurvivor(x, y, id, scale);
        } else if (id > 1 + numSurvivorIDs && id < 2 + numSurvivorIDs + numEnemyIDs) {
            createEnemy(x, y, id, scale);
        } else if (id >= numBeforeFloors && id < numBeforeFloors + numFloorIDs) {
            createFloor(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs && id < 1 + numBeforeFloors + numFloorIDs + numWallIDs) {
            createWall(x, y, id, scale);
        } else if (id >= 1 + numBeforeFloors + numFloorIDs + numWallIDs && id < 1 + numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs) {
            createTree(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs && id < 1 + numBeforeFloors + numFloorIDs + numWallIDs + numTreeIDs + numSmogIDs) {
            createSmog(x, y, id, scale);
        } else {
            System.out.println("Error - ID " + id + " out of bounds");
            System.out.println("The bounds are from 0 to " + tIDs.length);
        }
    }

    public TextureRegion getTextureRegionKey(int textReg) {
        String textureName = tileIDs.get(textReg).getString("image");
//        System.out.println(textureName);
        return new TextureRegion(directory.getEntry("tiles:" + textureName.substring(0, textureName.length() - 4), Texture.class));
    }

    public Texture getTextureKey(int textReg) {
        String textureName = tileIDs.get(textReg).getString("image");
        return directory.getEntry("tiles:" + textureName.substring(0, textureName.length() - 4), Texture.class);
    }

    public PooledList<Obstacle> getObjects() {
        return objects;
    }
    public boolean[][] getTileGrid() {
        return tileGrid;
    }
    public boolean[][] getSmogGrid() {return smogGrid;}
    public OrthographicCamera getCamera() {
        return camera;
    }

    public void createCaravan(float x, float y, float scale) {
        if (didCreateCaravan) {
            return;
        }
        caravan = new Caravan(x * tileSize + tileOffset, y * tileSize + tileOffset, getSurvivors().size, getTextureRegionKey(0), survivorITexture, scale, displayFontInteract);
        addObject(caravan);
//        caravan.activatePhysics(world);
    }

    public Caravan getCaravan() {
        return caravan;
    }

    public void createPlayer(float x, float y, float scale) {
        // Instantiate the player:
        if (didCreatePlayer) {
            return;
        }
        player = new Player(x * tileSize + tileOffset, y * tileSize + tileOffset, playerDirectionTextures, input, scale, imageTileSize);
        addObject(player);
//        player.activatePhysics(world);
        player.setAwake(true);

        // Instantiate the weapon:
        weapon = new Weapon(player.getPosition().x, player.getPosition().y);
        //player.attachLightToPlayer(weapon.getAbsorbSensor());
    }

    public Player getPlayer() {
        return player;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void createSurvivor(float x, float y, int id, float scale) {
        Survivor survivorTemp;
        // Instantiate a survivor:
        //Survivor survivorTemp = new Survivor(i, survivorLocations[i][0] * tileSize, survivorLocations[i][1] * tileSize, survivorTexture);
        survivorTemp = new Survivor(survivorArr.size, x * tileSize + tileOffset, y * tileSize + tileOffset, getTextureRegionKey(id), heart, displayFontInteract, scale);
//        survivorTemp.activatePhysics(world);

        survivorArr.add(survivorTemp);
        addObject(survivorTemp);
        //survivorControllers.add(new SurvivorController(survivorTemp, caravan.getPosition(), player.getPosition(), tileGrid, tileSize, tileOffset));
    }

    public Array<Survivor> getSurvivors() {
        return survivorArr;
    }
    public Array<SurvivorController> getSurvivorControllers() {
        return survivorControllers;
    }

    public void createEnemy(float x, float y, int id, float scale) {
        FloatingEnemy enemyTemp;
        enemyTemp = new FloatingEnemy(x * tileSize + tileOffset, y * tileSize + tileOffset, enemyDirectionTextures, scale, imageTileSize);
//        enemyTemp.activatePhysics(world);

        enemyArr.add(enemyTemp);
        addObject(enemyTemp);
//        System.out.println("Width: " + tileGrid.length + "\t\tHeight: " + tileGrid[0].length);
        //enemyControllers.add(new FloatingEnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr, toxicAir));
    }

    public Array<Enemy> getEnemies() {
        return enemyArr;
    }
    public Array<EnemyController> getEnemyControllers() {
        return enemyControllers;
    }

    public void createFloor(float x, float y, int id, float scale) {
        floorTemp = new FloorTile(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        floorArr.add(floorTemp);
        //cliffTemp.setAwake(true);
//        floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addFloor(floorTemp);
        if (id == 8){ //tile w/ mushroom
            Lights.createMushroomLight(x,y);
        }
//        wallTemp.activatePhysics(world);
    }

    public void createWall(int x, int y, int id, float scale) {
        wallTemp = new Obstacles(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        wallArr.add(wallTemp);
        //cliffTemp.setAwake(true);
        wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addObject(wallTemp);
        //wallTemp.activatePhysics(world);
//        tileGrid[(int)x][(int)y] = true;
//        tiles[wallLocations[i][0]][wallLocations[i][1]] = true;
//        tileGrid[wallLocations[i][0]][wallLocations[i][1]] = true;
        //System.out.println(id + " " + x + " " + y);
        tileGrid[x][y - 1] = true;
        if (id >= 18 && id <= 21) {
            if (x > 0) {
                tileGrid[x - 1][y - 1] = true;
            }
            if (x < width) {
                tileGrid[x + 1][y - 1] = true;
            }
//            if (y > 1) {
//                tileGrid[x][y - 2] = true;
//            }
        }
        if (caravan.getX() < 400f && (id < 22 && id > 12)) {
            System.out.println("WALL: " + x + ", " + (y-1) + "    |" + id);
            System.out.println("-----");
            int i = 0;
        }
    }

    public void createTree(int x, int y, int id, float scale) {
        treeTemp = new Obstacles(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        treeArr.add(treeTemp);
        //cliffTemp.setAwake(true);
        treeTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addObject(treeTemp);
//        treeTemp.activatePhysics(world);
        tileGrid[x][y-1] = true;
        if (caravan.getX() < 400f) {
            System.out.println("TREE: " + x + ", " + y);
            System.out.println("-----");
            int i = 0;
        }
    }

    public void createSmog(float x, float y, int id, float scale) {
        smogGrid[(int)y][(int)x] = true;
    }

    public void dispose(){

    }

}
