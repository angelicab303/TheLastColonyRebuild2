package com.mygdx.game;

import assets.AssetDirectory;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.*;
import com.mygdx.game.EnemyControllers.*;
import com.mygdx.game.Obstacles.*;
import com.mygdx.game.Obstacles.Enemies.Enemy;
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
    private PooledList<Obstacle> movObjects;
    private float scale;
    private int width;
    private int height;
    private boolean[][] tileGrid;
    private boolean[][] smogTiles;
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
    private int playerID = 1;
    private int numSurvivorIDs = 1;
    private int[] survivorIDs = new int[numSurvivorIDs];
    private int numEnemyIDs = 4;
    private int[] enemyIDs = new int[numEnemyIDs];
    private int numBeforeFloors = 2 + numSurvivorIDs + numEnemyIDs;
    private int numFloorIDs = 25;
    private int[] floorIDs = new int[numFloorIDs];
    private Array<FloorTile> floorArr = new Array<>();
    private int numWallIDs = 14;
    private int[] wallIDs = new int[numWallIDs];
    private Array<Obstacles> wallArr = new Array<Obstacles>();
    private int numObstacleIDs = 15;
    private int numFences = 9;
    private int numTrees = 6;
    private int[] obstacleIDs = new int[numObstacleIDs];
    private Array<Obstacles> obstacleArr = new Array<Obstacles>();
    private int numSmogIDs = 1;
    private int[] smogIDs = new int[numSmogIDs];
    private Array<Obstacles> smogArr = new Array<Obstacles>();
    private int numPlaceableIDs = 3;
    private int[] placeableIDs = new int[numPlaceableIDs];
    private Array<Obstacles> placeableArr = new Array<Obstacles>();
    private int mushroomID;

    private int[] tIDs = new int[numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs + numSmogIDs + numPlaceableIDs];

    // Declare variables for entities that are stored in GameplayController and passed in
    private Caravan caravan;
    private boolean didCreateCaravan = false;
    private Player player;
    private boolean didCreatePlayer = false;
    private Weapon weapon;
    private boolean readyToCreateSurvivorsAndEnemies = false;
    /** Survivor list **/
    private Array<Survivor> survivorArr;
    /** survivor controller list **/
    private Array<SurvivorController> survivorControllers;
    /** survivor paths list **/
    private Array<Vector2[]> survivorPaths;
    /** Enemy list */
    private Array<Enemy> enemyArr;
    /** Shrieker Enemy List */
    private Array<ShriekerEnemy> shriekerArr;
    /** Enemy Controller */
    private Array<EnemyController> enemyControllers;
    /** enemy paths list **/
    private Array<Vector2[]> enemyPaths;

    // Declare variables for each entity that has a lot of instantiations
    private FloorTile floorTemp;
    private Obstacles wallTemp;
    private Obstacles obstacleTemp;
    private Obstacles placeableTemp;
    private FilmStrip[] playerDirectionTextures;
    private FilmStrip[] survivorDirectionTextures;
    private FilmStrip[][] enemyDirectionTextures;
    private FilmStrip[] shriekerTextures;
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

    public JSONLevelReader(AssetDirectory directory, Rectangle bounds, World world, int level,
                           OrthographicCamera camera, InputController input, PooledList<Obstacle> objects, PooledList<Obstacle> movObjects, Array<FloorTile> floorArr,
                           float scale, boolean[][] tileGrid, boolean[][] smogTiles, boolean[][] smogGrid, int tileSize,
                           int tileOffset, int smogTileSize, int smogTileOffset, FilmStrip[] playerDirectionTextures,
                           FilmStrip[] survivorDirectionTextures, FilmStrip[][] enemyDirectionTextures, ToxicQueue toxicAir,
                           Texture survivorITexture, BitmapFont displayFontInteractive, Texture heart, Player player, Weapon weapon) {
        this.directory = directory;
        this.bounds = bounds;
        this.world = world;
        this.level = level;
        this.camera = camera;
        this.input = input;
        this.objects = objects;
        this.movObjects = movObjects;
        this.floorArr = floorArr;
        this.scale = scale;
        this.tileGrid = tileGrid;
        this.smogTiles = smogTiles;
        this.smogGrid = smogGrid;
        this.tileSize = tileSize;
        this.imageTileSize = tileSize * 10;
        this.tileOffset = tileOffset;
        this.smogTileSize = smogTileSize;
        this.smogTileOffset = smogTileOffset;
        this.playerDirectionTextures = playerDirectionTextures;
        this.survivorDirectionTextures = survivorDirectionTextures;
        this.enemyDirectionTextures = enemyDirectionTextures;
        this.shriekerTextures = enemyDirectionTextures[0];
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
            // InputStream inputStream =
            // getClass().getResourceAsStream("assets/tiles/LastColonyTilesetCorrect.json");
            // BufferedReader reader = new BufferedReader(new
            // InputStreamReader(inputStream));
            //  FileReader tilesReader = new
            // FileReader(directory.getAssetFileName(directory.getEntry("tileset",
            // JsonValue.class)));

            JsonValue levelStr = new JsonValue(false);
            if (level == 0) {
                levelStr = directory.getEntry("tutorialLevel1", JsonValue.class);
            } else if (level == 1) {
                levelStr = directory.getEntry("tutorialLevel2", JsonValue.class);
            } else if (level == 2) {
                levelStr = directory.getEntry("tutorialLevel3", JsonValue.class);
            } else if (level == 3) {
                levelStr = directory.getEntry("tutorialLevel4", JsonValue.class);
            } else if (level == 4) {
                levelStr = directory.getEntry("tutorialLevel5", JsonValue.class);
            } else if (level == 5) {
                levelStr = directory.getEntry("mediumLevel", JsonValue.class);
            }

            Array<Vector2> levelBounds = new Array<Vector2>();
            levelBounds.add(new Vector2(15, 10));
            levelBounds.add(new Vector2(20, 15));
            levelBounds.add(new Vector2(30, 13));
            levelBounds.add(new Vector2(20, 20));
            levelBounds.add(new Vector2(25, 25));
            levelBounds.add(new Vector2(30, 30));


            // FileReader mapReader = new FileReader(levelStr);

            // Send the fileReader to a new JsonReader object
            // JsonReader tilesJSONReader = new JsonReader();
            // JsonValue tilesJSON = tilesJSONReader.parse(tilesReader);
            JsonValue tilesJSON = directory.getEntry("tileset", JsonValue.class);

            tileIDs = tilesJSON.get("tiles");

            // Initialize relevant arrays for object creation:
            survivorArr = new Array<>();
            enemyArr = new Array<Enemy>();
            shriekerArr = new Array<ShriekerEnemy>();
            survivorControllers = new Array<SurvivorController>();
            enemyControllers = new Array<EnemyController>();

            /**
             * This is the order of the naming system for the files:
             * Caravan first
             * Player next
             * Survivor next
             * Enemy next
             * Ground floor next
             * Wall tiles next
             * Tree tiles next
             * Smog tiles next
             **/

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
            for (int i = 0; i < numObstacleIDs; i++) {
                obstacleIDs[i] = tileIDs.get(numBeforeFloors + numFloorIDs + numWallIDs + i).getInt("id");
                tIDs[numBeforeFloors + numFloorIDs + numWallIDs + i] = obstacleIDs[i];
            }
            // Get each of the smog IDs
            for (int i = 0; i < numSmogIDs; i++) {
                smogIDs[i] = tileIDs.get(numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs + i).getInt("id");
                tIDs[numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs + i] = smogIDs[i];
            }
            // Get each of the placeable IDs
            for (int i = 0; i < numPlaceableIDs; i++) {
                placeableIDs[i] = tileIDs.get(numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs + numSmogIDs + i).getInt("id");
                tIDs[numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs + numSmogIDs + i] = placeableIDs[i];
            }
            mushroomID = placeableIDs[1];

            // Close the tile reader
//             tilesReader.close();
            // ******************************************* END OF TILEREADER *******************************************

//                Send the fileReader to a new JsonReader object
            JsonReader mapJSONReader = new JsonReader();
            // JsonValue mapJSON = mapJSONReader.parse(mapReader);
            JsonValue mapJSON = levelStr;

            // Get JSON layers of the map
            JsonValue layers = mapJSON.get("layers");
            width = layers.get(0).getInt("width");
            height = layers.get(0).getInt("height");
            System.out.println("Width: " + width + "\t\tHeight: " + height);

//            this.tileGrid = new boolean[canvas.getWidth() / tileSize][canvas.getHeight() / tileSize];
//            this.tileGrid = new boolean[width][height];

            // this.camera.setToOrtho(false, width * tileSize, height * tileSize);
            // this.tileGrid = new boolean[width][height];

            // Loop through each of the layers and first simply instantiate the caravan and
            // player, in that order.
            float caravanX = 0;
            float caravanY = 0;
            float playerX = 0;
            float playerY = 0;
            for (int i = 0; i < layers.size; i++) {
                // Loop through the layer's data and retrieve each data array
                if (!layers.get(i).getString("type").equals("tilelayer")) {
                    continue;
                }
                JsonValue layerData = layers.get(i).get("data");
                for (int j = 0; j < layerData.size; j++) {
                    int dataValue = layerData.getInt(j) - 1;
                    // Do something with the data value...
                    if (dataValue == 0) {
                        caravanX = j % width + 1;
                        caravanY = height - (j / width);
                    } else if (dataValue == 1) {
                        // System.out.println("Found player");
                        playerX = j % width;
                        playerY = height - (j / width);
                        //
                    }
                }
            }
            createObject(caravanX, caravanY, 0);
            createObject(playerX, playerY, 1);
            didCreateCaravan = true;
            didCreatePlayer = true;

            survivorPaths = new Array<Vector2[]>();
            enemyPaths = new Array<Vector2[]>();

            // Loop through each of the object group layers to get the paths for the enemies
            // and survivors
            for (int i = 0; i < layers.size; i++) {
                if (layers.get(i).getString("type").equals("objectgroup")) {
                    JsonValue layerObjects = layers.get(i).get("objects");
                    if (layers.get(i).getString("name").equals("Survivor Paths")) {
                        for (int j = 0; j < layerObjects.size; j++) {
                            JsonValue polygon = layerObjects.get(j);
                            survivorPaths.add(new Vector2[polygon.size]);
                            for (int k = 0; k < polygon.size; k++) {
                                survivorPaths.get(j)[k] = new Vector2(polygon.get(k).get(0).getFloat(0),
                                        polygon.get(k).get(0).getFloat(1));
                            }
                        }
                    } else if (layers.get(i).getString("name").equals("Enemy Paths")) {
                        for (int j = 0; j < layerObjects.size; j++) {
                            JsonValue polygon = layerObjects.get(j);
                            enemyPaths.add(new Vector2[polygon.size]);
                            for (int k = 0; k < polygon.size; k++) {
                                enemyPaths.get(j)[k] = new Vector2(polygon.get(k).get(0).getFloat(0),
                                        polygon.get(k).get(0).getFloat(1));
                            }
                        }
                    } else {
                        System.out.println("Object layer \"" + layers.get(i).getString("name") + "\" not recognized");
                    }
                }
            }

            // Either need new constructor to initialize enemy with path
            // or a set function to set the path of an enemy

            // Loop through each of the layers and instantiate each object from the id
            for (int i = 0; i < layers.size; i++) {
                // Loop through the layer's data and retrieve each data array
                if (!layers.get(i).getString("type").equals("tilelayer")) {
                    continue;
                }
                JsonValue layerData = layers.get(i).get("data");
                for (int j = 0; j < layerData.size; j++) {
                    int dataValue = layerData.getInt(j) - 1;
                    // Do something with the data value...
                    if (dataValue > 0) {
                        createObject(j % width, height - (j / width), dataValue);
                    }
                }
            }

            // Create survivors and enemies after floors and walls
            readyToCreateSurvivorsAndEnemies = true;
            for (int i = 0; i < layers.size; i++) {
                // Loop through the layer's data and retrieve each data array
                if (!layers.get(i).getString("type").equals("tilelayer")) {
                    continue;
                }
                JsonValue layerData = layers.get(i).get("data");
                for (int j = 0; j < layerData.size; j++) {
                    int dataValue = layerData.getInt(j) - 1;
                    // Do something with the data value...
                    if (dataValue == 2) {
                        createObject(j % width, height - (j / width), dataValue);
                    } else if (dataValue == 3 || dataValue == 4 || dataValue == 5 || dataValue == 6) {
                        createObject(j % width, height - (j / width), dataValue);
                    }
                }
            }

            // Create extra smog border
            for (int i = -4; i < levelBounds.get(level).x + 8; i++) {
                for (int j = -4; j < levelBounds.get(level).y + 8; j++) {
                    if (i < 0 || i >= levelBounds.get(level).x || j <= 0 || j >= levelBounds.get(level).y) {
                        createWall(i, j, numBeforeFloors + numFloorIDs + numWallIDs - 1, scale);
                    }
                }
            }

            for (int i = 0; i < survivorArr.size; i++) {
                survivorControllers.add(new SurvivorController(survivorArr.get(i), this.caravan.getPosition(), this.player.getPosition(), this.tileGrid, this.smogGrid, tileSize, tileOffset));
            }

            this.caravan.setMaxCapacity(survivorArr.size);
            if (this.caravan.getX() < 400f) {
                System.out.println("Finished loading JSON Level");
            }
            System.out.println("Finished loading JSON Level");

            // Close the map reader
            // mapRe er.close();
        } catch (Exception e) {
            System.out.println("Failed to load JSON level");
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
        boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x + bounds.width);
        boolean vert = (bounds.y <= obj.getY() && obj.getY() <= bounds.y + bounds.height);
        return horiz && vert;
    }

    /*
     * Immediately adds the object
     * param obj The object to add
     */
    public void addObject(Obstacle obj) {
        assert inBounds(obj) : "Object is not in bounds";
        objects.add(obj);
        obj.activatePhysics(world);
    }

    public void addMovObject(Obstacle obj) {
        assert inBounds(obj) : "Object is not in bounds";
        movObjects.add(obj);
        obj.activatePhysics(world);
    }

    /**
     * Immediately adds the object to the physics world
     *
     * param obj The object to add
     */
    public void addFloor(FloorTile obj) {
        // assert inBounds(obj) : "Object is not in bounds";
        floorArr.add(obj);
        // obj.activatePhysics(world);
    }

    public void createObject(float x, float y, int id) {
        if (id == 0) {
            createCaravan(x, y, scale);
        } else if (id == 1) {
            createPlayer(x, y, scale);
        } else if (id > 1 && id < 2 + numSurvivorIDs) {
            if (readyToCreateSurvivorsAndEnemies) {
                createSurvivor(x, y, id, scale);
            }
        } else if (id > 1 + numSurvivorIDs && id < 2 + numSurvivorIDs + numEnemyIDs) {
            if (readyToCreateSurvivorsAndEnemies) {
                createEnemy(x, y, id, scale);
            }
        } else if (id >= numBeforeFloors && id < numBeforeFloors + numFloorIDs) {
            createFloor(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs && id < numBeforeFloors + numFloorIDs + numWallIDs) {
            createWall(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs + numWallIDs && id < numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs) {
            createObstacle(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs && id < numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs + numSmogIDs) {
            createSmog(x, y, id, scale);
        } else if (id >= numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs + numSmogIDs && id < numBeforeFloors + numFloorIDs + numWallIDs + numObstacleIDs + numSmogIDs + numPlaceableIDs) {
            createFloor(x, y, id, scale);
        } else {
            System.out.println("Error - ID " + id + " out of bounds");
            System.out.println("The bounds are from 0 to " + tIDs.length);
        }
    }

    public TextureRegion getTextureRegionKey(int textReg) {
        String textureName = tileIDs.get(textReg).getString("image");
        return new TextureRegion(directory.getEntry("tiles:" + textureName.substring(0, textureName.length() - 4), Texture.class));
    }


    public Texture getTextureKey(int textReg) {
        String textureName = tileIDs.get(textReg).getString("image");
        return directory.getEntry("tiles:" + textureName.substring(0, textureName.length() - 4), Texture.class);
    }


    public PooledList<Obstacle> getObjects() {
        return objects;
    }
    public PooledList<Obstacle> getMovObjects() {
        return movObjects;
    }
    public boolean[][] getTileGrid() {
        return tileGrid;
    }

    public boolean[][] getSmogGrid() {
        return smogGrid;
    }
    public OrthographicCamera getCamera() {
        return camera;
    }

    public void createCaravan(float x, float y, float scale) {
//        System.out.println("Creating caravan");
        if (didCreateCaravan) {
            return;
        }
        caravan = new Caravan(x * tileSize + tileOffset, y * tileSize + tileOffset, getSurvivors().size, getTextureRegionKey(0), scale, displayFontInteract);
        addObject(caravan);
//        caravan.activatePhysics(world);
    }

    public Caravan getCaravan() {
        return caravan;
    }

    public void createPlayer(float x, float y, float scale) {
//        System.out.println("Creating player");
        // Instantiate the player:
        if (didCreatePlayer) {
            return;
        }
        player = new Player(x * tileSize + tileOffset, y * tileSize + tileOffset, playerDirectionTextures, input, scale, imageTileSize);
        addMovObject(player);
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
//        System.out.println("Creating survivor");
        Survivor survivorTemp;
        survivorTemp = new Survivor(survivorArr.size, x * tileSize + tileOffset, y * tileSize + tileOffset, survivorDirectionTextures, heart, displayFontInteract, scale);
        survivorArr.add(survivorTemp);

//        addObject(survivorTemp);
//        survivorControllers.add(new SurvivorController(survivorTemp, caravan.getPosition(), player.getPosition(), tileGrid, smogGrid, tileSize, tileOffset));

        addMovObject(survivorTemp);
//        survivorControllers.add(new SurvivorController(survivorTemp, caravan.getPosition(), player.getPosition(), tileGrid, smogGrid, tileSize, tileOffset));
    }

    public Array<Survivor> getSurvivors() {
        return survivorArr;
    }
    public Array<SurvivorController> getSurvivorControllers() {
        return survivorControllers;
    }

    public void createEnemy(float x, float y, int id, float scale) {
//        System.out.println("Creating enemy");
        if (id == enemyIDs[0]) {
            // Floater
            FloatingEnemy enemyTemp = new FloatingEnemy(x * tileSize + tileOffset, y * tileSize + tileOffset, enemyDirectionTextures[1], scale, imageTileSize);

            enemyArr.add(enemyTemp);
            addMovObject(enemyTemp);

            enemyControllers.add(new FloatingEnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr, toxicAir));
        } else if (id == enemyIDs[1]) {
            // Scout
            ScoutEnemy enemyTemp = new ScoutEnemy(x * tileSize + tileOffset, y * tileSize + tileOffset, enemyDirectionTextures[1], vineTextures, scale, imageTileSize, world);

            enemyArr.add(enemyTemp);
            addMovObject(enemyTemp);

            enemyControllers.add(new ScoutEnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr));
        } else if (id == enemyIDs[2]) {
            // Chaser
            Enemy enemyTemp = new Enemy(x * tileSize + tileOffset, y * tileSize + tileOffset, enemyDirectionTextures[1], scale, imageTileSize, false);

            enemyArr.add(enemyTemp);
            addMovObject(enemyTemp);

            enemyControllers.add(new ChaserEnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr));
        } else if (id == enemyIDs[3]) {
            // Shrieker
            ShriekerEnemy enemyTemp = new ShriekerEnemy(x * tileSize + tileOffset, y * tileSize + tileOffset, shriekerTextures, scale, imageTileSize);
            shriekerArr.add((ShriekerEnemy) enemyTemp);

            enemyArr.add(enemyTemp);
            addMovObject(enemyTemp);

            enemyControllers.add(new ShriekerEnemyController(tileGrid, tileSize, tileOffset, enemyTemp, player, shriekerArr));
        }
    }

    public Array<Enemy> getEnemies() {
        return enemyArr;
    }

    public Array<EnemyController> getEnemyControllers() {
        return enemyControllers;
    }

    public void createFloor(float x, float y, int id, float scale) {
//        System.out.println("Creating floor");
        floorTemp = new FloorTile(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        floorArr.add(floorTemp);
        // cliffTemp.setAwake(true);
        // floorTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addFloor(floorTemp);
        if (id == mushroomID) { // tile w/ mushroom
            Lights.createMushroomLight(x, y);
        }
    }

    public Array<FloorTile> getFloorArr() {
        return floorArr;
    }

    public void createWall(float x, float y, int id, float scale) {
//        System.out.println("Creating wall");
        wallTemp = new Obstacles(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        wallArr.add(wallTemp);
        tileGrid[(int)(x / tileSize)][(int)(y / tileSize)] = true;
        wallTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addObject(wallTemp);

//        tileGrid[(int)x][(int)y] = true;
//        tiles[wallLocations[i][0]][wallLocations[i][1]] = true;
//        tileGrid[wallLocations[i][0]][wallLocations[i][1]] = true;
    }


    public void createObstacle(float x, float y, int id, float scale) {
//        System.out.println("Creating obstacle (tree / fence)");
        obstacleTemp = new Obstacles(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
        obstacleArr.add(obstacleTemp);
        tileGrid[(int)(x / tileSize)][(int)(y / tileSize)] = true;
        obstacleTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addObject(obstacleTemp);
    }

    public void createSmog(float x, float y, int id, float scale) {
        smogTiles[(int)x][(int)y] = true;
//        System.out.println("Smog id: " + id);
    }

    public boolean[][] getSmogTiles() {
        return smogTiles;
    }

    public void createPlaceable(float x, float y, int id, float scale) {
//        System.out.println("Creating placeable");
        placeableTemp = new Obstacles(x * tileSize + (tileSize / 2), y * tileSize + (tileSize / 2), getTextureRegionKey(id), scale);
//        System.out.println("Created placeable");
        placeableArr.add(placeableTemp);
        placeableTemp.setBodyType(BodyDef.BodyType.StaticBody);
        addObject(placeableTemp);
    }

    public void dispose(){

    }

}


