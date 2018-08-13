package entities;

import java.util.HashMap;
import java.util.Map;

import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import colladaLoader.MyFile;
import dataStructures.MeshData;
import renderEngine.AnimationLoader;
import renderEngine.DisplayManager;
import terrains.Terrain;
import Animation.Animation;
import Animation.Joint;

public class AnimatedPlayer extends AnimatedEntity {
	private static float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160;
	public static float GRAVITY = -40;
	private static float JUMP_POWER = 38;
	private static float height = 0;
	private static float TERRAIN_HEIGHT = 0;
	int walking = 0;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private Map<String, Animation> Animations = new HashMap<String, Animation>();

	private boolean inAir = false;

	public AnimatedPlayer(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale, Joint rootJoint,
			int jointCount, MeshData mesh) {
		super(model, position, rotX, rotY, rotZ, scale, rootJoint, jointCount,
				mesh);
		Animations.put("walking",
				AnimationLoader.loadAnimation(new MyFile("res/walking.dae")));
		Animations.put("stand",
				AnimationLoader.loadAnimation(new MyFile("res/stand.dae")));
			doAnimation(Animations.get("stand"), true);
		Animations.put("jump",
				AnimationLoader.loadAnimation(new MyFile("res/jump.dae")));

	}

	public void move(Terrain terrain) {
		checkInputs();
		super.increaseRotation(0,
				currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math
				.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math
				.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,
				super.getPosition().z);
		if (upwardsSpeed > 0) {
			upwardsSpeed += 1.3f * GRAVITY
					* DisplayManager.getFrameTimeSeconds();
		} else {
			upwardsSpeed += 2.2f * GRAVITY
					* DisplayManager.getFrameTimeSeconds();
		}
		super.increasePosition(0,
				upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			inAir = false;
			super.getPosition().y = terrainHeight;
		}
	}

	private void jump() {
		if (!inAir) {
			this.upwardsSpeed = JUMP_POWER;
			inAir = true;
			doAnimation(Animations.get("jump"), false);
		}
	}

	private void checkInputs() {

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
			if (walking == 0)
				doAnimation(Animations.get("walking"), true);
			walking++;

		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
			if (walking == 0)
				doAnimation(Animations.get("walking"), true);
			walking++;
		} else {
			if (walking != 0) {
				doAnimation(Animations.get("stand"), true);
			}
			walking = 0;
			currentSpeed = 0;

		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		} else {
			currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			RUN_SPEED = 160.0f;
		} else
			RUN_SPEED = 40.0f;

	}

}
