package com.mygdx.game.Obstacles.Items;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameCanvas;
import com.mygdx.game.Obstacles.GameObstacle;
import obstacle.BoxObstacle;
import obstacle.SimpleObstacle;


public class Item extends BoxObstacle implements GameObstacle {

    /**Filter for filtering */
    private static volatile Filter filter;

    public enum ItemType{
        ITEM,
        KEY,
        TORCH,
        COFFEE
    }
    public Item(float x, float y, TextureRegion cvalue, float scale){
        super(x,y,cvalue.getRegionWidth()*scale, cvalue.getRegionHeight()*scale);

        setDensity(1);
        setFriction(0);
        setRestitution(0.1f);
        setTexture(cvalue);

        if (filter == null){
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.ITEM;
    }

    public ItemType getItemType(){
        return ItemType.ITEM;
    }

    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }

        //geometry.setUserData("survivor");
        setFilterData(filter);
        setAwake(true);
        getBody().setUserData(this);
        return true;
    }

    @Override
    public short getCatagoricalBits() {
        return CATEGORY_ITEM;
    }

    @Override
    public short getMaskBits() {
        return MASK_ITEM;
    }

    @Override
    public void incBehind(int inc) {

    }

    public void collect(){
        markRemoved(true);
    }

}
