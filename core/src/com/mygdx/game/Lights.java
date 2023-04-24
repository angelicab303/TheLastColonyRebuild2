package com.mygdx.game;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Obstacles.GameObstacle;

import java.awt.*;

public class Lights {
    static RayHandler rayHandler;
    static int rays;
    public Lights(World world){
        rays = 50;
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.3f);

    }


    public void update(OrthographicCamera camera){
        rayHandler.update();

        rayHandler.setCombinedMatrix(camera.combined);

    }

    public void render(){
        rayHandler.render();
    }

    public void dispose(){
        rayHandler.dispose();

    }

    public static PointLight createPointLight(com.badlogic.gdx.graphics.Color color, float distance, float x, float y){
        return new PointLight(rayHandler, rays, color, distance, x, y);
    }

    public static ConeLight createConeLight(com.badlogic.gdx.graphics.Color color, float distance, float x, float y, float coneDegree){
        ConeLight cone = new ConeLight(rayHandler, rays, color, distance, x, y, 180,coneDegree);
        // Giving up on the filter for now since it's really glitchy
//        Filter filter = new Filter();
//        filter.categoryBits = 0x1000;
//        filter.maskBits = 0xFFF;
//        cone.setContactFilter(filter);
        return cone;
    }

}

