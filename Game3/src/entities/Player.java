package entities;

import java.awt.RenderingHints.Key;

import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity{
	
	private static float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160;
	public static float GRAVITY = -50;
	private static float JUMP_POWER = 30;
	private static float height = 0;
	private static float TERRAIN_HEIGHT = 0;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0 ;
	private float upwardsSpeed = 0;
	
	private boolean inAir = false;

	public Player(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		
		super(model, position, rotX, rotY, rotZ, scale);
	
	}

	

	public void move(Terrain terrain){
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed*DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(),0);
		if(super.getPosition().y<terrainHeight){
			upwardsSpeed = 0;
			inAir=false;
			super.getPosition().y = terrainHeight;
		}
	}
	private void jump(){
		if(!inAir){
			this.upwardsSpeed = JUMP_POWER;
			inAir = true;
			}
	}
	
	private void checkInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}else{
			currentSpeed = 0;
		}
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
		}else{
			currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
			RUN_SPEED = 160.0f;
		}else RUN_SPEED = 40.0f;
		
		
	}
	
}
