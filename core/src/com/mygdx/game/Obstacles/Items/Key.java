package com.mygdx.game.Obstacles.Items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Key extends Item{
    public Key(float x, float y, TextureRegion cvalue, float scale){
        super(x,y,cvalue,scale);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.KEY;
    }

}
