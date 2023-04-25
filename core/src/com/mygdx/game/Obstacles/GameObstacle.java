package com.mygdx.game.Obstacles;

import com.badlogic.gdx.physics.box2d.Filter;

/**
 * Interface class for all obstacles in the game.
 */
public interface GameObstacle {

    enum ObstacleType {
        /** The Caravan **/
        CARAVAN,
        /** A obstacle **/
        OBSTACLE,
        /** A smog **/
        SMOG,
        /** A purified air **/
        PURIFIED_AIR,
        /** The Player **/
        PLAYER,
        /** The Weapon **/
        WEAPON,
        /** An enemy */
        ENEMY,
        /** A survivor */
        SURVIVOR
    }

    /** Categories and Masks **/
    final short CATEGORY_SMOG = 0x0001;  // 0000000000000001 in binary
    final short CATEGORY_PLAYER = 0x0002;
    final short CATEGORY_ENEMY = 0x0004;
    final short CATEGORY_SURVIVOR = 0x0008;
    final short CATEGORY_ENV = 0x0010;
    final short CATEGORY_PURIFIED = 0x0020;
    final short CATEGORY_CARAVAN = 0x0040;
    final short MASK_SMOG = CATEGORY_ENEMY;
    final short MASK_PLAYER = CATEGORY_ENEMY | CATEGORY_SURVIVOR | CATEGORY_ENV;// | CATEGORY_CARAVAN;
    final short MASK_ENEMY = CATEGORY_PLAYER | CATEGORY_ENEMY | CATEGORY_ENV |CATEGORY_SMOG | CATEGORY_PURIFIED;// | CATEGORY_CARAVAN ;
    final short MASK_SURVIVOR = CATEGORY_PLAYER| CATEGORY_ENEMY | CATEGORY_SURVIVOR | CATEGORY_ENV; //| CATEGORY_CARAVAN;
    final short MASK_ENV = -1;

    final short MASK_PURIFIED = CATEGORY_ENV | CATEGORY_ENEMY;
    //final short MASK_CARAVAN = CATEGORY_SURVIVOR | CATEGORY_PLAYER | CATEGORY_ENEMY;


    /** Returns the type of an obstacle */
    ObstacleType getType();

    //Additional things that may be helpful

    short getCatagoricalBits();
    short getMaskBits();




}
