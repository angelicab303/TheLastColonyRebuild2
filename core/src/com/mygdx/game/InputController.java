/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameMode does not have to keep track of the current
 * key mapping.
 *
 * This class is NOT a singleton. Each input device is its own instance,
 * and you may have multiple input devices attached to the game.
 *
 * Author: Walker M. White
 * Based on original GameX Ship Demo by Rama C. Hoetzlein, 2002
 * LibGDX version, 1/16/2015
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import util.*;

/**
 * Device-independent input manager.
 *
 * This class supports both a keyboard and an X-Box controller.  Each player is
 * assigned an ID.  When the class is created, we check to see if there is a 
 * controller for that ID.  If so, we use the controller.  Otherwise, we default
 * the keyboard.
 */
public class InputController {

	/** Player id, to identify which keys map to this player */
	protected int player;

	/** X-Box controller associated with this player (if any) */
	protected XBoxController xbox;

	/** How much forward are we going? */
	private float vertical;

	/** How much are we turning? */
	private float horizontal;

	/** what is the direction are we shooting vertically? */
	private float verticalshoot;

	/** what is the direction are we shooting horizontally? */
	private float horizontalshoot;

	/** the returned shooting direction */
	private Vector2 shootdir;

	/** Did we press the absorb button */
	private static boolean pressedAbsorb;

	/** Did we press the fire button? */
	private static boolean pressedFire;
	/** Did we press E to pick up a survivor? */
	private static boolean pickedUpSurvivor;
	/** Did we press E to drop off survivors? */
	private static boolean droppedOffSurvivors;
	/** Did we press R to restart the level? */
	private static boolean reset;
	/** Did we press ESC to pause the game? */
	private static boolean paused = false;
	/** Whether the debug toggle was pressed. */
	private boolean debugPressed;
	private boolean debugPrevious;

	/**Gets the position of the mouse on screen */
	private Vector2 mousePos;
	/**
	 * Returns the amount of forward movement.
	 *
	 * -1 = backward, 1 = forward, 0 = still
	 *
	 * @return amount of forward movement.
	 */
	public float getVertical() {
		return vertical;
	}

	/**
	 * Returns the amount of horizontal movement.
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return amount of horizontal movement.
	 */
	public float getHorizontal() {
		return horizontal;
	}

	/**
	 * Returns the position of the mouse
	 * @return Vector2 of the mouse position on the screen
	 */
	public Vector2 getMousePos(){return mousePos;}

	/**
	 * Returns the direction of shooting
	 * @return Vector2 of the shooting direction on the screen
	 */
	public Vector2 getShootDir(){return shootdir;}

	/**
	 * Returns whether the absorb button was pressed.
	 *
	 * @return whether the absorb button was pressed.
	 */
	public boolean didPressAbsorb() {
		return pressedAbsorb;
	}

	/**
	 * Returns whether the fire button was pressed.
	 *
	 * @return whether the fire button was pressed.
	 */
	public boolean didPressFire() {
		return pressedFire;
	}

	/**
	 * Returns whether the survivor pick-up button was pressed.
	 *
	 * @return whether the survivor pick-up button was pressed.
	 */
	public boolean didCollectSurvivor() {
		return pickedUpSurvivor;
	}

	/**
	 <<<<<<< HEAD
	 * Returns whether the survivor drop-off button was pressed.
	 *
	 * @return whether the survivor drop-off button was pressed.
	 */
	public boolean didDropSurvivors() {
		return droppedOffSurvivors;
	}

	/** Returns whether the reset button was pressed.
	 * whether the reset button was pressed.
	 * @return
	 */
	public boolean didReset() {
		return reset;
	}

	/** Returns whether the pause button was pressed.
	 *
	 * @return whether the pause button was pressed.
	 */
	public boolean didPause() { return paused; }

	/**
	 * Creates a new input controller for the specified player.
	 *
	 * The game supports a single player using a keyboard, this is
	 * WASD vs. Arrow keys.  We also support multiple X-Box game controllers.
	 *
	 */
	public InputController() {
		player = 0;
		mousePos = new Vector2();
		shootdir = new Vector2();

		// Currently game-pads don't work.
		// If we have a game-pad for id, then use it.
//		Array<XBoxController> controllers = Controllers.get().getXBoxControllers();
//		xbox = controllers.get(0);
	}
	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didDebug() {
		return debugPressed && !debugPrevious;
	}
	/**
	 * Reads the input for this player and converts the result into game logic.
	 *
	 * This is an example of polling input.  Instead of registering a listener,
	 * we ask the controller about its current state.  When the game is running,
	 * it is typically best to poll input instead of using listeners.  Listeners
	 * are more appropriate for menus and buttons (like the loading screen).
	 */
	public void readInput() {
		// If there is a game-pad, then use it.
		if (xbox != null) {
			vertical = -xbox.getLeftY();
			vertical = (vertical < 0.1 && vertical > -0.1 ? 0.0f : vertical);

			horizontal = -xbox.getRightX();
			horizontal = (horizontal < 0.1 && horizontal > -0.1 ? 0.0f : horizontal);

			pressedFire = xbox.getRightTrigger() > 0.6f;
			debugPrevious  = debugPressed;
		} else {
			// Figure out, based on which player we are, which keys
			// control our actions (depends on player).
			int up, left, right, down, absorb, shoot, upshoot, leftshoot, rightshoot, downshoot,
					pickUpSurvivor, dropOffSurvivors, restart, pause;
			up    = Input.Keys.W;
			down  = Input.Keys.S;
			left  = Input.Keys.A;
			right = Input.Keys.D;
			absorb = Input.Buttons.LEFT;
			shoot = Input.Buttons.RIGHT;
			upshoot = Input.Keys.UP;
			downshoot  = Input.Keys.DOWN;
			leftshoot  = Input.Keys.LEFT;
			rightshoot = Input.Keys.RIGHT;
			pickUpSurvivor = Input.Keys.E;
			dropOffSurvivors = Input.Keys.E;
			restart = Input.Keys.R;
			pause = Input.Keys.ESCAPE;
			// Convert keyboard state into game commands
			vertical = horizontal =verticalshoot = horizontalshoot = 0;
			pressedAbsorb = false;
			pressedFire = false;
			pickedUpSurvivor = false;
			droppedOffSurvivors = false;
			reset = false;
			paused = false;
			debugPressed = (debugPressed) || (Gdx.input.isKeyPressed(Input.Keys.X));
			// Movement forward/backward
			if (Gdx.input.isKeyPressed(up) && !Gdx.input.isKeyPressed(down) && !Gdx.input.isKeyPressed(pause)) {
				vertical = 1;
			} else if (Gdx.input.isKeyPressed(down) && !Gdx.input.isKeyPressed(up) && !Gdx.input.isKeyPressed(pause)) {
				vertical = -1;
			}

			// Movement left/right
			if (Gdx.input.isKeyPressed(left) && !Gdx.input.isKeyPressed(right) && !Gdx.input.isKeyPressed(pause)) {
				horizontal = -1;
			} else if (Gdx.input.isKeyPressed(right) && !Gdx.input.isKeyPressed(left) && !Gdx.input.isKeyPressed(pause)) {
				horizontal = 1;
			}

			//shooting buttons pressed
			// Movement forward/backward
			if (Gdx.input.isKeyPressed(upshoot) && !Gdx.input.isKeyPressed(downshoot) && !Gdx.input.isKeyPressed(pause)) {
				verticalshoot = 1;
			} else if (Gdx.input.isKeyPressed(downshoot) && !Gdx.input.isKeyPressed(upshoot) && !Gdx.input.isKeyPressed(pause)) {
				verticalshoot = -1;
			}

			// Movement left/right
			if (Gdx.input.isKeyPressed(leftshoot) && !Gdx.input.isKeyPressed(rightshoot) && !Gdx.input.isKeyPressed(pause)) {
				horizontalshoot = -1;
			} else if (Gdx.input.isKeyPressed(rightshoot) && !Gdx.input.isKeyPressed(leftshoot) && !Gdx.input.isKeyPressed(pause)) {
				horizontalshoot = 1;
			}

			if(horizontalshoot != 0 && shootdir.x != horizontalshoot){
				shootdir.set(horizontalshoot, 0);
			}
			else if(verticalshoot != 0 && shootdir.y != verticalshoot){
				shootdir.set(0, verticalshoot);
			}
			else if(horizontalshoot == 0 && verticalshoot == 0){
				shootdir.set(0,0);
			}

			// Mouse Movement
			mousePos.set(Gdx.input.getX(),Gdx.input.getY());
//			if (Gdx.input.getDeltaY() < 0 && player == 0) {
//				vertical = 1;
//			} else if (Gdx.input.getDeltaY() > 0 && player == 0) {
//				vertical = -1;
//			}
//
//			if (Gdx.input.getDeltaX() < 0 && player == 0) {
//				horizontal = 0.1f;
//			} else if (Gdx.input.getDeltaX() > 0 && player == 0) {
//				horizontal = -0.1f;
//			}

			// Shooting
			//if (Gdx.input.isKeyPressed(upshoot)||Gdx.input.isKeyPressed(downshoot)||Gdx.input.isKeyPressed(leftshoot)||Gdx.input.isKeyPressed(rightshoot)){
			if(Gdx.input.isButtonPressed(shoot) && !Gdx.input.isKeyPressed(pause)){
				pressedFire = true;
			}

			//Absorbing
			if (Gdx.input.isButtonPressed(absorb) && !Gdx.input.isKeyPressed(pause)) {
				pressedAbsorb = true;
			}
			// Picking up Survivor
			if (Gdx.input.isKeyPressed(pickUpSurvivor) && !Gdx.input.isKeyPressed(pause)) {
				pickedUpSurvivor = true;
				droppedOffSurvivors = true;
			}
			// Resetting the level
			if (Gdx.input.isKeyPressed(restart)) {
				reset = true;
			}
			// Pausing the game
			if (Gdx.input.isKeyPressed(pause))
			{
				paused = true;
			}
		}
	}
}