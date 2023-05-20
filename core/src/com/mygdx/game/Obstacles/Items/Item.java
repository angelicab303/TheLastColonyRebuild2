package com.mygdx.game.Obstacles.Items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameCanvas;
import com.mygdx.game.Obstacles.GameObstacle;
import com.mygdx.game.Obstacles.Player;
import obstacle.BoxObstacle;
import obstacle.SimpleObstacle;


public class Item extends BoxObstacle implements GameObstacle {

    /**Filter for filtering */
    private static volatile Filter filter;
    private Vector2 position;

    private float scale;
    private boolean isInteractable;
    private boolean displayTorchInstruction;
    private boolean displayTorchExplanation;
    private BitmapFont displayFontInteract;

    private Player player;

    public enum ItemType{
        ITEM,
        KEY,
        TORCH,
        COFFEE
    }
    public Item(float x, float y, TextureRegion cvalue, BitmapFont font, float scale, Player player){
        super(x,y,cvalue.getRegionWidth()*scale, cvalue.getRegionHeight()*scale);
        this.player = player;
        position = new Vector2(x, y);
        setDensity(1);
        setFriction(0);
        setRestitution(0.1f);
        setTexture(cvalue);
        this.scale = scale;

        isInteractable = false;
        displayFontInteract = font;

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
    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, 0.0f, scale,
                scale);

        if (isInteractable) {
            String message = "(E) Pick Up";
            canvas.drawText(message, displayFontInteract, position.x - 16.0f, position.y + 20.0f);
        }
        if (displayTorchInstruction) {
            String message = "(Q) Place Down Torch";
            canvas.drawText(message, displayFontInteract, player.getX() - 32.0f, player.getY() + 40.0f);
        }
        if (displayTorchExplanation) {
            String message = "Torches repel smog creeping back!";
            canvas.drawText(message, displayFontInteract, position.x - 32.0f, position.y + 40.0f);
        }
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

    /**
     * Sets whether the item can be interacted with.
     */
    public void setInteractable(Boolean interact) {
        this.isInteractable = interact;
    }

    /**
     * Returns whether or not the item is interactable.
     *
     * @return whether or not the item is interactable
     */
    public boolean isInteractable() {
        return isInteractable;
    }

    public void collect(){
        markRemoved(true);
    }

    public void setDisplayTorchInstruction(boolean display) {
        displayTorchInstruction = display;
    }

    public void setDisplayTorchExplanation(boolean display) {
        displayTorchExplanation = display;
    }
}
