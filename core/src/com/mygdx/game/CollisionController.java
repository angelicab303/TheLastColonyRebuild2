/*
 * CollisionController.java
 *
 * This controller implements basic collision detection as described in
 * the instructions.  All objects in this game are treated as circles,
 * and a collision happens when circles intersect.
 *
 * This controller is EXTREMELY ineffecient.  To improve its performance,
 * you will need to use collision cells, as described in the instructions.
 * You should not need to modify any method other than the constructor
 * and processCollisions.  However, you will need to add your own methods.
 *
 * This is the only file that you need to modify as the first part of
 * the lab.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.math.*;
import com.mygdx.game.Obstacles.*;
import com.mygdx.game.Obstacles.Enemies.Enemy;

import java.util.LinkedList;
import java.util.List;

/**
 * Controller implementing simple game physics.
 *
 * Takes care of raycasting and box2d collisions
 */
public class CollisionController{
    Vector2 temp1;
    Vector2 temp2;
    Vector2 temp3;
    Vector2 zerovector;
    List<Smog> smogList;
    Array<Smog> tempSmogList;

    /** stores a temporary smog*/
    Smog tempSmog;

    /**stores the fraction */
    float curr_fraction;

    /** ray start pos */
    Vector2 ray_start;

    /**ray end pos list */
    Vector2 ray_end;

    /** Width of the collision geometry */
    private float width;
    /** Height of the collision geometry */
    private float height;

    ShapeRenderer shapeRenderer;

    public CollisionController(World world, int width, int height){
        this.width = width;
        this.height = height;
        setContactListener(world);
        temp1 = new Vector2();
        temp2 = new Vector2();
        zerovector = new Vector2(0, 0);
        smogList = new LinkedList<Smog>();
        ray_start = new Vector2();
        ray_end = new Vector2();
        curr_fraction = 0.0f;
    }

    void setContactListener(World world){
        world.setContactListener(new WorldContactListener());
    }

    void update(World world, Player player, Weapon weapon){
        absorbSmog(world, player, weapon);
    }



    //Ray casting:
    void absorbSmog(World world, Player pl, Weapon weapon){
        //global Smog smog = null;

        //This one gets the positions of the obstacles
        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (!fixture.isSensor()) {
                    Object obstacle = fixture.getBody().getUserData();
                    if (obstacle instanceof Obstacles) {
                        //System.out.println("Well at least something is working");
                        tempSmog = null;
                        curr_fraction = fraction + 0.06f;
                        //System.out.println(fraction);
                        return curr_fraction;
                    }
                }

                return -1;
            }
        };

        RayCastCallback smogCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                Object obstacle = fixture.getBody().getUserData();
                if (obstacle instanceof Smog){
                    if (((Smog) obstacle).isAbsorbed() == false){
                        //System.out.println("We have dun it");
                        smogList.add((Smog) obstacle);
                        //Gets the closest piece of smog, for now
                    }
                }
                return 1;
            }
        };

        Vector2[] absorptionVertices = weapon.getAbsorptionRange();

        ray_start.set(absorptionVertices[0]);
        //MinAngle
        for(Vector2 r:absorptionVertices){
            if (!r.equals(ray_start)) {
                tempSmog = null;
                curr_fraction = 1.0f;

                world.rayCast(callback, ray_start, r);

                //Corrects distance for drawing
                r.sub(ray_start);
                r.scl(curr_fraction);
                r.add(ray_start);

                world.rayCast(smogCallback, ray_start, r);
            }
        }

        if(weapon.isAbsorbing()) {
            for (Smog s : smogList) {
                if (!s.isAbsorbed()){
                    weapon.incrementAmmo(1);
                }
                s.setAbsorbed(true);
            }
        }
        smogList.clear();
    }


    private class WorldContactListener implements ContactListener {


        //private B2dModel parent;

        public WorldContactListener(){
            super();
        }


        @Override
        public void beginContact(Contact contact) {
            //System.out.println("CollisionController Contact");
            GameObstacle objA = (GameObstacle) contact.getFixtureA().getBody().getUserData();
            GameObstacle objB = (GameObstacle) contact.getFixtureB().getBody().getUserData();

            int collision = objA.getCatagoricalBits() | objB.getCatagoricalBits();

            switch (collision){
                case GameObstacle.CATEGORY_PURIFIED | GameObstacle.CATEGORY_ENEMY:
                    Enemy enemy;
                    Player player;
                    Survivor survivor;
                    if(objA.getType() == GameObstacle.ObstacleType.ENEMY){
                         ((Enemy) objA).setStunned(true);
                        ((Enemy) objA).incToStunTime();
                        ((PurifiedQueue.PurifiedAir) objB).collide();
                    }
                    else {
                        ((Enemy) objB).setStunned(true);
                        ((Enemy) objB).incToStunTime();
                        ((PurifiedQueue.PurifiedAir) objA).collide();
                    }
                    break;
                case GameObstacle.CATEGORY_PURIFIED | GameObstacle.CATEGORY_ENV:
                    if(objA.getType() == GameObstacle.ObstacleType.PURIFIED_AIR){
                        ((PurifiedQueue.PurifiedAir) objA).collide();
                    }
                    else {
                        ((PurifiedQueue.PurifiedAir) objB).collide();
                    }
                    break;
                case GameObstacle.CATEGORY_PLAYER | GameObstacle.CATEGORY_ENEMY:
                    if(objA.getType() == GameObstacle.ObstacleType.ENEMY){
                        enemy = (Enemy) objA;
                        player = (Player) objB;
                    }
                    else {
                        enemy = (Enemy) objB;
                        player = (Player) objA;
                    }
//                    if (player.canLoseLife()) {
//                        enemy.setAttack(false);
//                        player.setHealth(player.getHealth() - 1);
//                        player.coolDown(false);
//                    }
                    break;
                case GameObstacle.CATEGORY_PLAYER | GameObstacle.CATEGORY_SURVIVOR:
                    if(objA.getType() == GameObstacle.ObstacleType.SURVIVOR){
                        survivor = (Survivor) objA;
                    }
                    else {
                        survivor = (Survivor) objB;
                    }
                    survivor.setInteractable(true);
                    break;
                case GameObstacle.CATEGORY_ENEMY | GameObstacle.CATEGORY_SURVIVOR:
                    if(objA.getType() == GameObstacle.ObstacleType.SURVIVOR){
                        survivor = (Survivor) objA;
                        enemy = (Enemy) objB;
                    }
                    else {
                        survivor = (Survivor) objB;
                        enemy = (Enemy) objA;
                    }
                    if(survivor.canLoseLife()){
                        survivor.loseLife();
                        survivor.coolDown(false);
                        enemy.setAttack(false);
                    }
                    break;
                case GameObstacle.CATEGORY_PLAYER | GameObstacle.CATEGORY_TOXIC:
                    if (objA.getType() == GameObstacle.ObstacleType.PLAYER){
                        player = (Player) objA;
                        ((ToxicQueue.ToxicAir) objB).collide();
                    }
                    else {
                        player = (Player) objB;
                        ((ToxicQueue.ToxicAir) objA).collide();
                    }
                    if (player.canLoseLife()){
                        player.setHealth(player.getHealth()-1);
                        player.coolDown(false);
                    }
                    break;
                case GameObstacle.CATEGORY_SURVIVOR | GameObstacle.CATEGORY_TOXIC:
                    if (objA.getType() == GameObstacle.ObstacleType.SURVIVOR){
                        survivor = (Survivor) objA;
                        ((ToxicQueue.ToxicAir) objB).collide();
                    }
                    else {
                        survivor = (Survivor) objB;
                        ((ToxicQueue.ToxicAir) objA).collide();
                    }
                    if (survivor.canLoseLife()){
                        survivor.loseLife();
                        survivor.coolDown(false);
                    }
                    break;
                case GameObstacle.CATEGORY_TOXIC | GameObstacle.CATEGORY_ENV:
                    if(objA.getType() == GameObstacle.ObstacleType.TOXIC_AIR){
                        ((ToxicQueue.ToxicAir) objA).collide();
                    }
                    else {
                        ((ToxicQueue.ToxicAir) objB).collide();
                    }
                    break;
            }
        }

        @Override
        public void endContact(Contact contact) {

            //If enemy is no longer touched by smog, activate
            GameObstacle objA = (GameObstacle) contact.getFixtureA().getBody().getUserData();
            GameObstacle objB = (GameObstacle) contact.getFixtureB().getBody().getUserData();

            int collision = objA.getCatagoricalBits() | objB.getCatagoricalBits();
            switch (collision){
                case GameObstacle.CATEGORY_SMOG | GameObstacle.CATEGORY_ENEMY:
                    if (objA.getType() == GameObstacle.ObstacleType.ENEMY){
                        ((Enemy) objA).setRevealed(true);
                    }
                    else {
                        ((Enemy) objB).setRevealed(true);
                    }
                    break;
                case GameObstacle.CATEGORY_PLAYER | GameObstacle.CATEGORY_SURVIVOR:
                    if (objA.getType() == GameObstacle.ObstacleType.SURVIVOR){
                        ((Survivor) objA).setInteractable(false);
                    }
                    else {
                        ((Survivor) objB).setInteractable(false);
                    }
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

            GameObstacle objA = (GameObstacle) contact.getFixtureA().getBody().getUserData();
            GameObstacle objB = (GameObstacle) contact.getFixtureB().getBody().getUserData();

            int collision = objA.getCatagoricalBits() | objB.getCatagoricalBits();
            switch (collision){
                case GameObstacle.CATEGORY_SMOG | GameObstacle.CATEGORY_ENEMY:
                case GameObstacle.CATEGORY_PURIFIED | GameObstacle.CATEGORY_ENEMY:
                case GameObstacle.CATEGORY_PLAYER | GameObstacle.CATEGORY_SURVIVOR:
                case GameObstacle.CATEGORY_SMOG | GameObstacle.CATEGORY_ENV:
                    contact.setEnabled(false);
            }
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }


}