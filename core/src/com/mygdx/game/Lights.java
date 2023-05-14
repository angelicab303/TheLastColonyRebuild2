package com.mygdx.game;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Obstacles.GameObstacle;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import static com.mygdx.game.Obstacles.GameObstacle.CATEGORY_ENV;

public class Lights {
    static RayHandler rayHandler;
    static int rays;

    private static final float  MUSH_DIST = 10f;

    private static final float TORCH_DIST = 30f;


    protected static List<Light> lightsList;
    public Lights(World world){
        rays = 50;
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.5f);
        //lightsList = new LinkedList<Light>();

    }

    public void activateLights(){
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
        /*
        for(Light light : lightsList){
            //light.dispose();
        }
        lightsList = null;

         */
        rayHandler.dispose();
    }

    public static PointLight createPointLight(com.badlogic.gdx.graphics.Color color, float distance, float x, float y){
        PointLight light = new PointLight(rayHandler, rays, color, distance, x, y);
        //lightsList.add(light);

        Filter filter = new Filter();
        filter.categoryBits = 0x1000;
        filter.maskBits = 0;
        light.setContactFilter(filter);
        return light;

    }


    public static PointLight createMushroomLight(float x, float y){
        PointLight light = new PointLight(rayHandler, rays, Color.PINK, MUSH_DIST, x*32+24, y*32+16);
        //lightsList.add(light);

        Filter filter = new Filter();
        filter.categoryBits = 0x1000;
        filter.maskBits = 0;
        light.setContactFilter(filter);
        return light;

    }

    public static PointLight createTorchLight(Body body){
        PointLight light = new PointLight(rayHandler, rays, Color.ORANGE, TORCH_DIST, 0, 0);
        light.attachToBody(body);
        //lightsList.add(light);
        light.setSoft(true);

        Filter filter = new Filter();
        filter.categoryBits = 0x1000;
        filter.maskBits = CATEGORY_ENV;
        light.setContactFilter(filter);
        return light;
    }

    public static ConeLight createConeLight(com.badlogic.gdx.graphics.Color color, float distance, float x, float y, float coneDegree){
        ConeLight light = new ConeLight(rayHandler, rays, color, distance, x, y, 180,coneDegree);
        light.setSoft(false);
        // Giving up on the filter for now since it's really glitchy
        Filter filter = new Filter();
        filter.categoryBits = 0x1000;
        filter.maskBits = CATEGORY_ENV;
        light.setContactFilter(filter);
        //lightsList.add(light);
        return light;
    }

}

