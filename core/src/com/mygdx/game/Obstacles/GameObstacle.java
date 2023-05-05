package com.mygdx.game.Obstacles;

import com.badlogic.gdx.physics.box2d.Filter;

/**
 * Interface class for all obstacles in the game.
 */
public interface GameObstacle {

    enum ObstacleType {
        /** The Caravan */
        CARAVAN,
        /** A obstacle */
        OBSTACLE,
        /** A smog */
        SMOG,
        /** A purified air */
        PURIFIED_AIR,
        /** A toxic air */
        TOXIC_AIR,
        /** The Player */
        PLAYER,
        /** The Weapon */
        WEAPON,
        /** An enemy */
        ENEMY,
        /** A Vine */
        VINE,
        /** A survivor */
        SURVIVOR
    }

    /** Categories and Masks */
    final short CATEGORY_SMOG = 0x0001;  // 0000000000000001 in binary
    final short CATEGORY_PLAYER = 0x0002;
    final short CATEGORY_ENEMY = 0x0004;
    final short CATEGORY_SURVIVOR = 0x0008;
    final short CATEGORY_ENV = 0x0010;
    final short CATEGORY_PURIFIED = 0x0020;
    final short CATEGORY_TOXIC = 0x0040;
    final short CATEGORY_CARAVAN = 0x0080;
    final short CATEGORY_VINE = 0x0100;
    final short MASK_SMOG = CATEGORY_ENEMY;
    final short MASK_PLAYER = CATEGORY_ENEMY | CATEGORY_SURVIVOR | CATEGORY_ENV | CATEGORY_TOXIC; //| CATEGORY_CARAVAN;
    final short MASK_ENEMY = CATEGORY_PLAYER | CATEGORY_ENEMY | CATEGORY_ENV |CATEGORY_SMOG | CATEGORY_PURIFIED;// | CATEGORY_CARAVAN ;
    final short MASK_SURVIVOR = CATEGORY_PLAYER| CATEGORY_ENEMY | CATEGORY_SURVIVOR | CATEGORY_ENV | CATEGORY_TOXIC; //| CATEGORY_CARAVAN;
    final short MASK_ENV = -1;

    final short MASK_PURIFIED = CATEGORY_ENV | CATEGORY_ENEMY | CATEGORY_VINE;
    final short MASK_TOXIC = CATEGORY_ENV | CATEGORY_PLAYER | CATEGORY_SURVIVOR;
    final short MASK_VINE = CATEGORY_PURIFIED;
    //final short MASK_CARAVAN = CATEGORY_SURVIVOR | CATEGORY_PLAYER | CATEGORY_ENEMY;


    /** Filmstrip consts */
    final int UP = 0;
    final int DOWN = 1;
    final int RIGHT = 2;
    final int LEFT = 3;
    final int IDLE = 4;
    


    /** Returns the type of an obstacle */
    ObstacleType getType();

    //Additional things that may be helpful

    short getCatagoricalBits();
    short getMaskBits();

    void incBehind(int inc);
    void setBehind(boolean bool);
    boolean getBehind();




}
