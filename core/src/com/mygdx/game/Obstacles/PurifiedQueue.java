/**
 * PurifiedQueue.cs (An alteration of photonqueue)
 *
 * This class implements a "particle system" that manages the photons fired
 * by either ship in the game.  When a ship fires a photon, it adds it to this
 * particle system.  The particle system is responsible for moving (and drawing)
 * the photon particle.  It also keeps track of the age of the photon.  Photons
 * that are too old are deleted, so that they are not bouncing about the game
 * forever.
 *
 * The PhotonQueue is exactly what it sounds like: a queue. In this implementation
 * we use the circular array implementation of a queue (which you may have learned
 * in CS 2110).  Why do we do this when C# has perfectly good Collection classes?
 * Because in game programming it is considered bad form to have "new" statements
 * in an update or a graphics loop if you can easily avoid it.  Each "new" is
 * a potentially expensive memory allocation.  It is (often) much better to
 * allocate all the memory that you need at start-up, so that all you do is
 * assign variables during game time. If you notice, all the Photon objects
 * are declared and initialized in the constructor; we just reassign the fields
 *
 * Author: Walker M. White (edited by vd95)
 * Based on original GameX Ship Demo by Rama C. Hoetzlein, 2002
 * MonoGame version, 12/30/2013
 */

package com.mygdx.game.Obstacles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameCanvas;
import obstacle.BoxObstacle;
import org.w3c.dom.Text;
import util.FilmStrip;


/**
 * Model class representing an "particle system" of photons.
 *
 * Note that the graphics resources in this class are static.  That
 * is because all photons share the same image file, and it would waste
 * memory to load the same image file for each photon.
 */
public class PurifiedQueue {
    // Private constants to avoid use of "magic numbers"
    /** Fixed velocity for a photon */
    private static final float PHOTON_VELOCITY = 10.0f;
    /** Number of animation frames a photon lives before deleted */
    private static final int MAX_AGE = 480;
    /** Maximum number of photons allowed on screen at a time. */
    private static final int MAX_PHOTONS = 512;

    /** Graphic asset representing a single photon. */
    private static Texture texture;
    private static Texture collisionTexture;

    // QUEUE DATA STRUCTURES
    /** Array implementation of a circular queue. */
    protected PurifiedAir[] queue;
    /** Index of head element in the queue */
    protected int head;
    /** Index of tail element in the queue */
    protected int tail;
    /** Number of elements currently in the queue */
    protected int size;

    /**Filter for filtering */
    private static volatile Filter filter;

    /** Position for moving box2d objects offscreen */
    Vector2 offscreen;

    private Player player;

    protected int ticks;

    /**
     * An inner class that represents a single Purified air pellet
     *
     * To count down on memory references, the photon is "flattened" so that
     * it contains no other objects.
     */
    public class PurifiedAir extends BoxObstacle implements GameObstacle {

        /**
         * Whether this purified air unit is currently fading
         */
        private boolean fading;

        /**
         * Whether this purified air unit has fully faded
         */
        private boolean faded;

        /**
         * Age of this purified air tile
         */
        private int age;

        /**
         * Maximum time it will take for this purified air unit to fade
         */
        private final float MAX_FADE_TIME = 30;

        /**
         * Time this purified air unit has faded
         */
        private float fadeTime = 0;
        /**
         * Time this purified air unit starts fading
         */
        private float timeToFade = 450;
        /** Filmstrip for smog */
        protected FilmStrip animator;
        protected FilmStrip collisionAnimator;
        /** How fast we change frames (one frame per 10 calls to update) */
        private static final float ANIMATION_SPEED = 0.02f;
        /** The number of animation frames in our filmstrip */
        private static final int   NUM_ANIM_FRAMES = 1;

        private static final int   NUM_ANIM_COLLISION_FRAMES = 13;
        /** Current animation frame for this shell */
        private float aframe;
        private float aframeCollision;
        /** Scale of the object */
        private float scale;
        private int ticks;

        /**
         * Initialize a standard purified air unit
         */
        public PurifiedAir (float scale)
        {
            super(offscreen.x, offscreen.y, PurifiedQueue.texture.getWidth()*scale/NUM_ANIM_FRAMES, PurifiedQueue.texture.getHeight()*scale);
            setBodyType(BodyDef.BodyType.DynamicBody);
            // setTexture(PurifiedQueue.texture);
            // setLinearDamping(1); //arbitrary damping coeff.
            this.age = -1;
            ticks = 0;

            this.fading = false;
            this.faded = false;

            setActive(false);
            animator = new FilmStrip(PurifiedQueue.texture,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
            collisionAnimator = new FilmStrip(PurifiedQueue.collisionTexture, 1, NUM_ANIM_COLLISION_FRAMES, NUM_ANIM_COLLISION_FRAMES);
//            float maxFrame = 4;
//            float minFrame = 0;
//            float frameNum = (float)(Math.random()*(maxFrame-minFrame+1)+minFrame);
            aframe = 0;
            aframeCollision = 0;
            this.scale = scale;
        }

        @Override
        public void drawDebug(GameCanvas canvas) {
            super.drawDebug(canvas);
        }

        /**
         * Returns the type of this object.
         *
         * We use this instead of runtime-typing for performance reasons.
         *
         * @return the type of this object.
         */
        public ObstacleType getType() { return ObstacleType.PURIFIED_AIR; }


        /**
         * Returns whether this purified air unit is currently fading
         *
         * @return the fading state of this purified air unit
         *
         */
        public boolean isFading() { return fading; }

        /**
         * Sets the fading state of this purified air unit
         *
         * @param fading this purified air unit's new fading state
         */
        public void setFading(boolean fading) { this.fading = fading; }

        /**
         * Returns whether this purified air unit is currently faded
         *
         * @return the faded state of this purified air unit
         *
         */
        public boolean isFaded() { return faded; }

        /**
         * Sets the faded state of this purified air unit
         *
         * @param faded this purified air unit's new faded state
         */
        public void setFaded(boolean faded) { this.faded = faded; }


        public void allocate(float x, float y, Vector2 velocity){
            this.setPosition(x,y);
//            this.setVX(velocity.x);
//            this.setVY(velocity.y);
            body.setActive(true);
            this.age = 0;
        }

        public void applyImpulse(Vector2 impulse){
            body.applyLinearImpulse(impulse, getPosition(), true);
        }

        public void collide() {
            age = MAX_AGE;
        }

        public void reset(){
            this.setPosition(offscreen);
            body.setLinearVelocity(0, 0);
            body.setActive(false);
            //markRemoved(true);
        }

        /** Updates the fade time of fading purified air */
        public void update()
        {
            if (age != MAX_AGE) {
                age++;
            }
            if (age == MAX_AGE)
            {
                aframeCollision += ANIMATION_SPEED;
                if (ticks % 50 == 0) {
                    aframeCollision += 1;
                }
//                faded = true;
//                reset();
            }
            else {
                aframeCollision = 0;
            }
            if (aframeCollision > 12) {
                aframeCollision = 0;
                faded = true;
                reset();
            }

            setX(body.getWorldCenter().x);
            setY(body.getWorldCenter().y);

            // Increase animation frame
            aframe += ANIMATION_SPEED;

            if (aframe >= NUM_ANIM_FRAMES) {
                aframe -= NUM_ANIM_FRAMES;
            }

        }

        public Vector2 getOrigin(){
            return origin;
        }

        public boolean activatePhysics(World world) {
            // create the box from our superclass
            if (!super.activatePhysics(world)) {
                return false;
            }
            geometry.setUserData("purified air");
            body.setUserData(this);
            body.setActive(false);

            setFilterData(filter);
            return true;
        }

        @Override
        public short getCatagoricalBits() {
            return CATEGORY_PURIFIED;
        }

        @Override
        public short getMaskBits() {
            return MASK_PURIFIED;
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     *  Constructs a new (empty) PhotonQueue
     */
    public PurifiedQueue(Texture texture, World world, float scale, Player player, Texture collision) {
        //Constants
        offscreen = new Vector2(-50, -50);

        //Filter
        filter = new Filter();
        filter.categoryBits = GameObstacle.CATEGORY_PURIFIED;
        filter.maskBits = GameObstacle.MASK_PURIFIED;

        // Construct the queue.
        // this.texture = texture;
        this.texture = texture;
        this.collisionTexture = collision;
        queue = new PurifiedAir[MAX_PHOTONS];

        head = 0;
        tail = -1;
        size = 0;

        // "Predeclare" all the photons for efficiency
        for (int ii = 0; ii < MAX_PHOTONS; ii++) {
            queue[ii] = new PurifiedAir(scale);
            queue[ii].activatePhysics(world);
        }
    }

    /**
     * Returns the image for a single photon; reused by all photons.
     *
     * This value should be loaded by the GameMode and set there. However, we
     * have to be prepared for this to be null at all times
     *
     * @return the image for a single photon; reused by all photons.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the image for a single photon; reused by all photons.
     *
     * This value should be loaded by the GameMode and set there. However, we
     * have to be prepared for this to be null at all times
     *
     * @param value the image for a single photon; reused by all photons.
     */
    public void setTexture(Texture value) {
        texture = value;
    }

    public PurifiedAir[] getQueue() { return queue; }

    /**
     * Adds a photon to the active queue.
     *
     * When adding a photon, we assume that it is fired from a ship with the
     * given position, velocity, and facing (angle).  We could have a general
     * photon adding function, but this will make refactoring easier in
     * Exercise 9.
     *
     * As all Photons are predeclared, this involves moving the head and the tail,
     * and reseting the values of the object in place.  This is a simple implementation
     * of a memory pool. It works because we delete objects in the same order that
     * we allocate them.
     *
     * @param position  The position of the ship firing the photon
     * @param impulse   The impulse of the photon
     */
    public void addPhoton(Vector2 position, Vector2 impulse) {
        // Determine direction and velocity of the photon.

        // Check if any room in queue.
        // If maximum is reached, remove the oldest photon.
        if (size == MAX_PHOTONS) {
            head = ((head + 1) % MAX_PHOTONS);
            size--;
        }

        // Add a new photon at the end.
        // Already declared, so just initialize.
        tail = ((tail + 1) % MAX_PHOTONS);
        queue[tail].allocate(position.x,position.y, player.getVelocity());
        queue[tail].applyImpulse(impulse);
        size++;
    }

    public void attack(int bullets, Vector2 pos, Vector2[] impulses){
        for(int ii = 0; ii < bullets; ii++){
            addPhoton(pos, impulses[ii]);
        }
    }

    /**
     * Moves all the photons in the active queue.
     *
     * Each photon is advanced according to its velocity. Photons out of bounds are
     * rebounded into view. Photons which are too old are deleted.
     */
    public void update() {
        // First, delete all old photons.
        // INVARIANT: Photons are in queue in decending age order.
        // That means we just remove the head until the photons are young enough.
        while (size > 0 && queue[head].age > MAX_AGE) {
            // As photons are predeclared, all we have to do is move head forward.
            head = ((head + 1) % MAX_PHOTONS);
            size--;
        }

        // Now, step through each active photon in the queue.
        for (int ii = 0; ii < size; ii++) {
            // Find the position of this photon.
            int idx = ((head+ii) % MAX_PHOTONS);

            // Move the photon according to velocity.
            queue[idx].update();
        }
    }

    //To do: add deletion method

    /**
     * Draws the photons to the drawing canvas.
     *
     * This method uses additive blending, which is set before this method is
     * called (in GameMode).
     *
     * @param canvas The drawing canvas.
     */
    public void draw(GameCanvas canvas) {
        if (texture == null) {
            return;
        }

        Color tint = Color.ROYAL;
        tint.a = 1.0f;
        // Step through each active photon in the queue.
        for (int ii = 0; ii < size; ii++) {
            // Find the position of this photon.
            int idx = ((head + ii) % MAX_PHOTONS);

            PurifiedAir air = queue[idx];

            // How big to make the photon.  Decreases with age.
            //float scale = (1.25f - (float)queue[idx].age * 0.5f / (float)MAX_AGE)*queue[idx].scale;
            //float ratio = (float)queue[idx].age/(float)MAX_AGE;
            //tint.set((float)100*ratio,(float)250*ratio,(float)250*ratio,(float)1*(1-ratio));

            // Use this information to draw.
            air.animator.setFrame((int)air.aframe);
            air.collisionAnimator.setFrame((int)air.aframeCollision);
            if(air.age >= MAX_AGE) {
                canvas.draw(air.collisionAnimator,Color.WHITE,air.getOrigin().x,air.getOrigin().y,air.getPosition().x,air.getPosition().y,0,air.scale,air.scale);
            }
            else {
                canvas.draw(air.animator,Color.WHITE,air.getOrigin().x,air.getOrigin().y,air.getPosition().x,air.getPosition().y,0,air.scale,air.scale);
            }


        }
    }

    public void drawDebug(GameCanvas canvas) {
        for (int ii = 0; ii < size; ii++) {
            // Find the position of this photon.
            int idx = ((head + ii) % MAX_PHOTONS);

            PurifiedAir air = queue[idx];

            // How big to make the photon.  Decreases with age.
            //float scale = (1.25f - (float)queue[idx].age * 0.5f / (float)MAX_AGE)*queue[idx].scale;
            //float ratio = (float)queue[idx].age/(float)MAX_AGE;
            //tint.set((float)100*ratio,(float)250*ratio,(float)250*ratio,(float)1*(1-ratio));

            // Use this information to draw.
            (air).drawDebug(canvas);
        }
    }
}




