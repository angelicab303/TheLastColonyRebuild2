package com.mygdx.game.Obstacles.Items;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Obstacles.Player;

public class Coffee extends Item{
    public Coffee(float x, float y, TextureRegion cvalue, BitmapFont font, float scale, Player player){
        super(x,y,cvalue, font, scale, player);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.COFFEE;
    }

}