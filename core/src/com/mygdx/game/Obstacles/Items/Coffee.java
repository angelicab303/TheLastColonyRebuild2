package com.mygdx.game.Obstacles.Items;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Coffee extends Item{
    public Coffee(float x, float y, TextureRegion cvalue, BitmapFont font, float scale){
        super(x,y,cvalue, font, scale);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.COFFEE;
    }

}