package com.mygdx.game.Obstacles.Items;

import box2dLight.Light;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Lights;

public class Torch extends Item{
    private Light torchLight;
    public Torch(float x, float y, TextureRegion cvalue, float scale){
        super(x,y,cvalue,scale);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.TORCH;
    }

    @Override
    protected void createFixtures() {
        super.createFixtures();
        torchLight = Lights.createTorchLight(body);
    }

    public void collect(){
        super.collect();
        torchLight.remove();
    }
}
