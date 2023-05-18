package com.mygdx.game.Obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameCanvas;
import obstacle.BoxObstacle;
import util.FilmStrip;

/**
 * Model class representing an "particle system" of photons.
 *
 * Note that the graphics resources in this class are static.  That
 * is because all photons share the same image file, and it would waste
 * memory to load the same image file for each photon.
 */
public class ToxicQueue {
    // Private constants to avoid use of "magic numbers"
    /** Fixed velocity for a photon */
    private static final float PHOTON_VELOCITY = 500.0f;
    /** Number of animation frames a photon lives before deleted */
    private static final int MAX_AGE = 200;
    /** Maximum number of photons allowed on screen at a time. */
    private static final int MAX_PHOTONS = 5;

    /** Graphic asset representing a single photon. */
    private static Texture texture;
    private static Texture collisionTexture;

    // QUEUE DATA STRUCTURES
    /** Array implementation of a circular queue. */
    protected ToxicAir[] queue;
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

    /**
     * An inner class that represents a single toxic air pellet
     *
     * To count down on memory references, the photon is "flattened" so that
     * it contains no other objects.
     */
    public class ToxicAir extends BoxObstacle implements GameObstacle {

        /**
         * Whether this toxic air unit is currently fading
         */
        private boolean fading;

        /**
         * Whether this toxic air unit has fully faded
         */
        private boolean faded;

        /**
         * Age of this toxic air tile
         */
        private int age;

        /**
         * Maximum time it will take for this toxic air unit to fade
         */
        private final float MAX_FADE_TIME = 30;

        /**
         * Time this toxic air unit has faded
         */
        private float fadeTime = 0;
        /**
         * Time this toxic air unit starts fading
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
         * Initialize a standard toxic air unit
         */
        public ToxicAir (float scale)
        {
            super(offscreen.x, offscreen.y, ToxicQueue.texture.getWidth()*scale, ToxicQueue.texture.getHeight()*scale);
            setBodyType(BodyDef.BodyType.DynamicBody);
            setDimension(ToxicQueue.texture.getWidth()*scale/5, ToxicQueue.texture.getHeight()*scale);
            // setTexture(PollutedQueue.texture);
            setLinearDamping(0); //arbitrary damping coeff.
            this.age = -1;
            ticks = 0;

            this.fading = false;
            this.faded = false;

            setFilterData(filter);
            setActive(false);
            animator = new FilmStrip(ToxicQueue.texture,1,NUM_ANIM_FRAMES,NUM_ANIM_FRAMES);
            collisionAnimator = new FilmStrip(ToxicQueue.collisionTexture, 1, NUM_ANIM_COLLISION_FRAMES, NUM_ANIM_COLLISION_FRAMES);
//            float maxFrame = 4;
//            float minFrame = 0;
//            float frameNum = (float)(Math.random()*(maxFrame-minFrame+1)+minFrame);
            aframe = 0;
            aframeCollision = 0;
            this.scale = scale;
        }

        /**
         * Returns the type of this object.
         *
         * We use this instead of runtime-typing for performance reasons.
         *
         * @return the type of this object.
         */
        public ObstacleType getType() { return ObstacleType.TOXIC_AIR; }


        /**
         * Returns whether this polluted air unit is currently fading
         *
         * @return the fading state of this polluted air unit
         *
         */
        public boolean isFading() { return fading; }

        /**
         * Sets the fading state of this polluted air unit
         *
         * @param fading this polluted air unit's new fading state
         */
        public void setFading(boolean fading) { this.fading = fading; }

        /**
         * Returns whether this toxic air unit is currently faded
         *
         * @return the faded state of this toxic air unit
         *
         */
        public boolean isFaded() { return faded; }

        /**
         * Sets the faded state of this toxic air unit
         *
         * @param faded this toxic air unit's new faded state
         */
        public void setFaded(boolean faded) { this.faded = faded; }


        public void allocate(float x, float y){
            this.setPosition(x,y);
            body.setActive(true);
            this.age = 0;
        }

        public void applyImpulse(Vector2 impulse){
            body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
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

        /** Updates the fade time of fading toxic air */
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
            geometry.setUserData("toxic air");
            body.setUserData(this);
            body.setActive(false);

            setFilterData(filter);
            return true;
        }

        @Override
        public short getCatagoricalBits() {
            return CATEGORY_TOXIC;
        }

        @Override
        public short getMaskBits() {
            return MASK_TOXIC;
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

        public void drawDebug(GameCanvas canvas) {
            //canvas.beginDebug();
            canvas.drawPhysics(shape, Color.RED, getX()*drawScale.x, getY()*drawScale.y, getAngle(), drawScale.x, drawScale.y);
            //canvas.endDebug();
        }
    }


    /**
     *  Constructs a new (empty) PhotonQueue
     */
    public ToxicQueue(Texture texture, World world, float scale, Texture collision) {
        //Constants
        offscreen = new Vector2(-50, -50);

        //Filter
        filter = new Filter();
        filter.categoryBits = GameObstacle.CATEGORY_TOXIC;
        filter.maskBits = GameObstacle.MASK_TOXIC;

        // Construct the queue.
        // this.texture = texture;
        this.texture = texture;
        this.collisionTexture = collision;
        queue = new ToxicAir[MAX_PHOTONS];

        head = 0;
        tail = -1;
        size = 0;

        // "Predeclare" all the photons for efficiency
        for (int ii = 0; ii < MAX_PHOTONS; ii++) {
            queue[ii] = new ToxicAir(scale);
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

    public ToxicAir[] getQueue()
    {
        return queue;
    }

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
        queue[tail].allocate(position.x,position.y);
        queue[tail].applyImpulse(impulse);
        size++;
    }

    public void attack(int bullets, Vector2 pos, Vector2 impulse){
        for(int ii = 0; ii < bullets; ii++){
            addPhoton(pos, impulse.scl(PHOTON_VELOCITY));
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

        Color tint = Color.CYAN;
        tint.a = 1.0f;
        // Step through each active photon in the queue.
        for (int ii = 0; ii < size; ii++) {
            // Find the position of this photon.
            int idx = ((head + ii) % MAX_PHOTONS);

            ToxicAir air = queue[idx];

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
        if (texture == null) {
            return;
        }

        Color tint = Color.CYAN;
        tint.a = 0.5f;
        // Step through each active photon in the queue.
        for (int ii = 0; ii < size; ii++) {
            // Find the position of this photon.
            int idx = ((head + ii) % MAX_PHOTONS);

            ToxicAir air = queue[idx];

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
}

