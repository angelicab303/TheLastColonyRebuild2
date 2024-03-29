package com.mygdx.game.Obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameCanvas;
//import com.mygdx.game.SmogRegenerator;
import obstacle.BoxObstacle;
import util.FilmStrip;

import com.badlogic.gdx.utils.Array;


public class Smog extends BoxObstacle implements GameObstacle{

    /** Whether this smog unit is currently being absorbed */
    private boolean absorbed;

    /** Whether this smog unit has fully faded */
    private boolean faded;

    /** Whether this smog can reappear */
    private boolean canReappear;

    /** Maximum time it will take for this smog unit to fade */
    private final float MAX_FADE_TIME = 60;


    private final float REAPPEAR_TIME = 200;
    private float reappearanceTime = 0;

    private boolean willReappear = false;

    /** Time this smog unit has faded */
    private float fadeTime = 0;
    /** Filmstrip for smog */
    protected FilmStrip animator;
    /** How fast we change frames (one frame per 10 calls to update) */
    private static final float ANIMATION_SPEED = 0.02f;
    /** The number of animation frames in our filmstrip */
    private static final int   NUM_ANIM_FRAMES = 5;
    /** Current animation frame for this shell */
    private float aframe;
    /** Scale of the object */
    private float scale;

    private int updateColor;

    private Array<Smog> neighbors;


    /**Filter for filtering */
    private static volatile Filter filter;
    /**
     * Returns the type of this object.
     *
     * We use this instead of runtime-typing for performance reasons.
     *
     * @return the type of this object.
     */
    //public ObstacleType getType() { return ObstacleType.SMOG; }

    /**
     * Returns whether this smog unit is currently being absorbed
     *
     * @return the absorption state of this smog unit
     *
     */
    public boolean isAbsorbed() { return absorbed; }

    /**
     * Sets the absorption state of this smog unit
     *
     * @param absorbed this smog unit's new absorption state
     */
    public void setAbsorbed(boolean absorbed) { this.absorbed = absorbed; }

    /**
     * Returns whether this smog unit is currently being absorbed
     *
     * @return the absorption state of this smog unit
     *
     */
    public boolean isFaded() { return faded; }

    /**
     * Sets the absorption state of this smog unit
     *
     * @param absorbed this smog unit's new absorption state
     */
    public void setFaded(boolean absorbed) { this.faded = faded; }

    /**
     * Initialize a standard smog unit
     *
     * @param x the x-coordinate of this smog unit
     * @param y the y-coordinate of this smog unit
     */
    public Smog (float x, float y, Texture value, float frame, float scale)
    {
        super(x, y, value.getWidth()*scale/NUM_ANIM_FRAMES, value.getHeight()*scale);
        setBodyType(BodyDef.BodyType.StaticBody);
        //setTexture(value);
        absorbed = false;
        faded = false;
        canReappear = true;

        if (filter == null){
            filter = new Filter();
            filter.categoryBits = getCatagoricalBits();
            filter.maskBits = getMaskBits();
        }

        updateColor = 0;


        animator = new FilmStrip(value,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
        aframe = frame;
        this.scale = scale;
        neighbors = new Array<>();
    }

    /** Updates the fade time of absorbed smog */
    public void update()
    {
        //Deletes the box2d object if destroyed completely, currently crashes due to queue problems
        /*
        if (fadeTime > MAX_FADE_TIME){
            world.destroyBody(body);
            body.setUserData(null);
            body = null;
        }

         */
        if(absorbed){
            setActive(false);
        }
        if(!canReappear && (updateColor != 0)){
            fadeTime = MAX_FADE_TIME;
            faded = true;
            setActive(false);
            reappearanceTime = 0;
            updateColor = 0;
        }
        if(updateColor > 0){
            updateColor++;
        }
        if(updateColor >= 10){
            fadeTime = 0;
            updateColor = 0;
        }

        if (reappearanceTime >= REAPPEAR_TIME){
            reappear();
            fadeTime = MAX_FADE_TIME;
            updateColor = 1;
            reappearanceTime = 0;
        }



        //Adds absorption time if absorbed
        if (isAbsorbed())
        {
            if (body != null){
                body.setAwake(true);
            }

            fadeTime++;
            if (fadeTime >= MAX_FADE_TIME)
            {
                fadeTime = MAX_FADE_TIME;
                faded = true;
            }
        }

        // Increase animation frame
        aframe += ANIMATION_SPEED;

        if (aframe >= NUM_ANIM_FRAMES) {
            aframe -= NUM_ANIM_FRAMES;
        }

        if(!isActive()){
            int sum = 0;
            int total = 0;
            for(Smog s: neighbors){
                if (s.isActive()){
                    sum++;
                }
                total++;
            }
            if (((double)sum)/((double)total)> 0.25){
                reappearanceTime++;
            }
            else {
                reappearanceTime = 0;
            }
        }
        if (isFaded() &&!(reappearanceTime >= REAPPEAR_TIME)){
            setActive(false);
        }
    }

    public void setCanReappear(boolean bool){
        canReappear = bool;
    }

    public static void addNeighboringSmog(Smog s1, Smog s2){
        if (s1 != null && s2 != null){
            s1.addNeighbor(s2);
            s2.addNeighbor(s1);
        }

    }

    private void addNeighbor(Smog smog){
        neighbors.add(smog);
        return;
    }


    public void setTransparent(){
        fadeTime = MAX_FADE_TIME;

    }
    /**
     * Draws this obstacle to the canvas with varying alpha values for smog fading
     * <p>
     * There is only one drawing pass in this application, so you can draw the obstacles
     * in any order.
     *
     * @param canvas The drawing context
     */
    public void draw (GameCanvas canvas) {

        // canvas.draw(texture, new Color(255, 255, 255,( MAX_FADE_TIME - fadeTime )/ MAX_FADE_TIME ), origin.x, origin.y, body.getWorldCenter().x*drawScale.x, body.getWorldCenter().y*drawScale.y, getAngle(), 1, 1);
        //canvas.draw(texture, new Color(255, 255, 255,( MAX_FADE_TIME - fadeTime )/ MAX_FADE_TIME ), origin.x, origin.y, getX()* drawScale.x, getY()* drawScale.y,  getAngle(), 1, 1);
        animator.setFrame((int)aframe);
        Color color = Color.PURPLE;
        color.a = ( MAX_FADE_TIME - fadeTime )/ MAX_FADE_TIME;

        canvas.draw(animator, color, origin.x, origin.y, body.getWorldCenter().x*drawScale.x - animator.getRegionWidth()*scale/2, body.getWorldCenter().y*drawScale.y- animator.getRegionHeight()*scale/2, 0.0f, scale, scale) ;
        // canvas.draw(animator, new Color(255, 255, 255,( MAX_FADE_TIME - fadeTime )/ MAX_FADE_TIME ), origin.x, origin.y, body.getWorldCenter().x*drawScale.x, body.getWorldCenter().y*drawScale.y, animator.getRegionWidth(), animator.getRegionHeight()) ;
    }

    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }
        geometry.setUserData("smog");

        fixture.filter.categoryBits = 0x0004;
        fixture.filter.maskBits = 0x0001;


        setX(body.getWorldCenter().x);
        setY(body.getWorldCenter().y);

        setFilterData(filter);

        body.setUserData(this);
        return true;
    }

    public void reappear(){
        body.setActive(true);
        setAbsorbed(false);
        faded = false;
        //fadeTime = 0;
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.SMOG;
    }

    @Override
    public short getCatagoricalBits() {
        return CATEGORY_SMOG;
    }

    @Override
    public short getMaskBits() {
        return MASK_SMOG;
    }

    @Override
    public void incBehind(int inc) {


    }

    @Override
    public void setBehind(boolean bool) {

    }

    @Override
    public boolean getBehind() {
        return false;
    }
}
