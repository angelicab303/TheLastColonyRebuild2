package com.mygdx.game;

import box2dLight.Light;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * A model class representing the player.
 *
 */
public class Weapon {
    public static int MAX_AMMO_CAPACITY = 500;
    private float DEFAULT_SHOOT_RADIUS = 200f;
    private Vector2 DEFAULT_ANGLE_RANGE = new Vector2(-35f, 35f);
    private float DEFAULT_ABSORB_RADIUS = 100f;
    private float ABSORB_FREQUENCY = 4;

    // constants for the weapon
    /** True if this weapon is absorbing */
    private boolean absorbing;
    // constants for the weapon
    /** True if this weapon is firing */
    private boolean firing;
    /** The position of the weapon on the game screen */
    private Vector2 position;
    private int absorbCounter;
    /** The amount of ammunition units this weapon currently has */
    private int numAmmo;
    /** The amount of purified air pellets fired with each press*/
    private final int bullets = 1;// must be odd

    /** How far this weapon can shoot purified air from the player's location */
    private float shootingRadius;
    /** How far this weapon can absorb smog from the player's location */
    private float absorbRadius;
    /** The angle range of shooting purified air from the player's position in degrees. i.e (0, 90) would equate to a
     * shot range of a top right quarter circle with the origin as the player's position */
    private Vector2 shootingRange;
    /** The angle range of absorbing smog from the player's position in degrees. i.e (0, 90) would equate to an
     * absorption range of a top right quarter circle with the origin as the player's position */
    private Vector2 absorbRange;
    /** The area on the screen where dispersal of purified area from the weapon is possible as represented by the start
     * and endpoints of raycasts. Index 0 is the start position and the rest are the endpoints of the raycasts */
    private Vector2[] shootingVertices;

    /** The number of frames until we can fire again */
    private static final int   RELOAD_RATE = 60;

    /** Countdown to limit refire rate */
    protected int refire;

    /** number of raycasts used to determine range*/
    private final int raycasts = 20; //must be >1
    /** The area on the screen where it is possible for the weapon to absorb smog as represented by the start
     * and endpoints of raycasts. Index 0 is the start position and the rest are the endpoints of the raycasts */
    private Vector2[] absorptionVertices;

    /** Light cones */
    Light absorbSensor;
    Light attackSensor;

    /** The impulses given to a shooting thing */
    private Vector2[] impulses;

    /** impulse magnitude */
    private final float power = 300;
    private Vector2 temp1;

    private Vector2 relMousePos;
    private Vector2 mousePos;

    private Vector2 shootingDir;

    /**
     * Returns true if this weapon is absorbing and false otherwise.
     *
     * @return true if this weapon is absorbing and false otherwise.
     */
    public boolean isAbsorbing() {
        return absorbing;
    }

    /**
     * Sets absorbing to value.
     * @param value the new value of absorbing.
     */
    public void setAbsorbing(boolean value) {
        absorbing = value;
    }

    /**
     * Returns true if this weapon is firing and false otherwise.
     *
     * @return true if this weapon is firing and false otherwise.
     */
    public boolean isFiring() {
        return firing;
    }

    /**
     * Sets firing to value.
     * @param value the new value of firing.
     */
    public void setFiring(boolean value) {
        firing = value;
    }

    /**
     * Fires a bullet.
     * @return whether bullet was fired successfully for chaining
     */
    public boolean fire(){
        if(isFiring() && numAmmo > 0 && (refire > RELOAD_RATE)){
            refire = 0;
            return true;
        }
        else {
            return false;
        }
    }

    public int getBullets(){
        if(numAmmo < bullets){
            return numAmmo;
        }
        return bullets;
    }

    /**
     * Returns the current position of this weapon.
     *
     * @return the current position of this weapon.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Increments this weapon's ammunition units accordingly based on added ammunition units. Number of ammunition
     * units in this weapon will never surpass MAX_AMMO_CAPACITY.
     *
     * @param addedAmmo amount of ammunition units to be added to this weapon's current ammunition units
     */
    public void incrementAmmo(int addedAmmo) {
        if (addedAmmo < 0) {
            if (numAmmo < 4) {
                numAmmo = 0;
            } else {
                numAmmo += addedAmmo * 4;
            }
        } else {
            if (absorbCounter >= ABSORB_FREQUENCY) {
                if (numAmmo + addedAmmo >= MAX_AMMO_CAPACITY) {
                    numAmmo = MAX_AMMO_CAPACITY;
                } else {
                    numAmmo += addedAmmo;
                }
                absorbCounter = 0;
            } else {
                absorbCounter++;
            }
        }
    }

    /**
     * Sets the current position of this weapon.
     *
     * @param pos the Vector2 osition of the weapon.
     */
    public void setPosition(Vector2 pos) {
        position = pos;
    }

    /**
     * Returns the min angle of this weapon's shooting range.
     *
     * @return the current min angle of this weapon's shooting range.
     */
    public float getMinAngleShoot() {
        return shootingRange.x;
    }

    /**
     * Returns the min angle of this weapon's shooting range.
     *
     * @return the current min angle of this weapon's shooting range.
     */
    public float getMaxAngleShoot() {
        return shootingRange.y;
    }
    /**
     * Returns the min angle of this weapon's absorb range.
     *
     * @return the current min angle of this weapon's absorb range.
     */
    public float getMinAngleAbsorb() {
        return absorbRange.x;
    }

    /**
     * Returns the min angle of this weapon's absorb range.
     *
     * @return the current min angle of this weapon's absorb range.
     */
    public float getMaxAngleAbsorb() {
        return absorbRange.y;
    }

    /**
     * Returns the number of ammunition units currently in this weapon.
     *
     * @return number of ammunition units currently in this weapon.
     */
    public int getNumAmmo() {
        return numAmmo;
    }
    /**
     * Returns the number of maximum ammunition units currently in this weapon.
     *
     * @return maximum number of ammunition units currently in this weapon.
     */
    public int getMaxNumAmmo() {
        return MAX_AMMO_CAPACITY;
    }

    /**
     * Sets the number of ammunition units currently in this weapon.
     *
     * @param ammo the number of ammunition units this weapon must have.
     */
    public void setNumAmmo(int ammo) {
        numAmmo = ammo;
    }

    /**
     * Returns the shooting radius of this weapon.
     *
     * @return the shooting radius of this weapon.
     */
    public float getShootingRadius() {
        return shootingRadius;
    }

    /**
     * Sets the shooting radius of this weapon and updates the weapon's shooting sensor based on new radius.
     *
     * @param radius the new shooting radius of this weapon.
     */
    public void setShootingRadius(float radius) {
        shootingRadius = radius;
    }

    /**
     * Returns the absorption radius of this weapon.
     *
     * @return the absorption radius of this weapon.
     */
    public float getAbsorbRadius() {
        return absorbRadius;
    }

    /**
     * Sets the absorption radius of this weapon and updates the weapon's absorption sensor based on new radius.
     *
     * @param radius the new absorption radius of this weapon.
     */
    public void setAbsorbRadius(float radius) {
        absorbRadius = radius;
    }

    /**
     * Returns the shooting range of this weapon.
     *
     * @return the shooting range of this weapon represented by angle range in degrees.
     */
    public Vector2 getShootingRange() {
        return shootingRange;
    }

    /**
     * Sets the shooting range of this weapon and updates the weapon's shooting sensor based on new radius.
     *
     * @param angleMin the new shooting range's start represented in degrees.
     * @param angleMax the new shooting range's end represented in degrees.
     */
    public void setShootingRange(float angleMin, float angleMax) {
        shootingRange.x = angleMin;
        shootingRange.y = angleMax;
    }

    /**
     * Returns the absorption range of this weapon.
     *
     * @return the absorption range of this weapon represented by angle range in degrees.
     */
    public Vector2 getAbsorbRange() {
        return absorbRange;
    }

    /**
     * Sets the absorption range of this weapon and updates the weapon's absorption sensor based on new radius.
     *
     * @param angleMin the new absorption range's start represented in degrees.
     * @param angleMax the new absorption range's end represented in degrees.
     */
    public void setAbsorptionRange( float angleMin, float angleMax) {
        absorbRange.x = angleMin;
        absorbRange.y = angleMax;
    }

    private float getAbsorbAngleChange(){
        return absorbRange.y - absorbRange.x;
    }

    public void calculateAbsorptionRange(Vector2 mouseRelPos){
        // Start position for raycasts
        absorptionVertices[0].set(position);
        // End positions for raycasts

        float angle_change = (absorbRange.y - absorbRange.x)/ ((float) raycasts - 1);
        float angle = mouseRelPos.angleDeg();
        temp1.set(absorbRadius, 0);
        temp1.rotateDeg(absorbRange.x);
        temp1.rotateDeg(angle);
        for(int i = 0; i < raycasts; i ++){
            absorptionVertices[i+1].set(temp1.cpy().add(getPosition()));
            temp1.rotateDeg(angle_change);
        }
    }

    public void calculateAttackRange(Vector2 mouseRelPos){


    }


    public void calculateImpulses(Vector2 mouseRelPos){
        float angle = mouseRelPos.angleDeg();
        impulses[0].setAngleDeg(angle);
        if (bullets - 1 > 0){
            float angle_change = (absorbRange.y - absorbRange.x)/ ((float) getBullets() - 1);
            for(int ii = 1; ii < getBullets(); ii++){
                impulses[ii].setAngleDeg(angle + shootingRange.x + angle_change*((float) ii));
            }
        }

    }

    public Vector2[] getImpulses(){
        return impulses;
    }

    public Vector2[] getAttackRange(){
        return shootingVertices;
    }

    public Vector2[] getAbsorptionRange(){
        return absorptionVertices;
    }

    /**
     * Create weapon with starting location (posX, posY), a shooting radius of shootingRadius, an absorption radius of
     * absorbRadius, a shooting range of (shootingRangeMin, shootingRangeMax), an absorption radius of
     * (absorbRangeMin, absorbRangeMax), and a starting ammunition count of 0.
     *
     * @param posX             The initial x-coordinate of the weapon
     * @param posY             The initial y-coordinate of the weapon
     */
    public Weapon(float posX, float posY) {
        numAmmo = 0;
        firing = false;
        position = new Vector2();
        position.x = posX;
        position.y = posY;
        shootingRadius = DEFAULT_SHOOT_RADIUS;
        shootingRange = new Vector2();
        shootingRange.x = DEFAULT_ANGLE_RANGE.x;
        shootingRange.y = DEFAULT_ANGLE_RANGE.y;
        absorbRadius = DEFAULT_ABSORB_RADIUS;
        absorbRange = new Vector2();
        absorbRange.x = DEFAULT_ANGLE_RANGE.x;
        absorbRange.y =   DEFAULT_ANGLE_RANGE.y;
        absorptionVertices = new Vector2[raycasts + 1];
        for(int i = 0; i <  raycasts + 1; i++){
            absorptionVertices[i] = new Vector2();
        }
        impulses = new Vector2[bullets];
        for(int i = 0; i <  bullets; i++){
            impulses[i] = new Vector2(power, 0);
        }
        temp1 = new Vector2();
        mousePos = new Vector2();
        refire = RELOAD_RATE;
        absorbSensor = Lights.createConeLight(Color.BLUE, absorbRadius*2, position.x, position.y, getAbsorbAngleChange()/2);
    }

    /**
     * gets the absorb sensor
     */
    public Light getAbsorbSensor() {
        return absorbSensor;
    }

    void update(Vector2 playerPos, Vector2 mousePos, Vector2 shootingDir){
        position = playerPos;
        this.shootingDir = shootingDir;
        this.mousePos = mousePos;
        this.relMousePos = mousePos.cpy().sub(playerPos);

        calculateAbsorptionRange(relMousePos);
        calculateImpulses(relMousePos);
        absorbSensor.setPosition(position);
        absorbSensor.setDirection(relMousePos.angleDeg());

        if (refire <= RELOAD_RATE) {
            refire++;
        }

    }

    void draw(GameCanvas canvas){
        canvas.drawLine(Color.BLUE, position, mousePos);
        Vector2 ray_start = absorptionVertices[0];
        for(Vector2 r : absorptionVertices){
            if (!r.equals(ray_start)){
                canvas.drawLine(Color.RED, ray_start, r);
            }


        }
    }
}
