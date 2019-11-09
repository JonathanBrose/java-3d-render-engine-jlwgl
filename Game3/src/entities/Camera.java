package entities;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import engineTester.MainGameLoop;
import terrains.Terrain;

public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0,20,0);
	private float pitch = 10;
	private float yaw ;
	private float roll;
	private float yOffset = 3;
	private float terrainHeight;
	private float minPitch = 0;
	boolean animated=false;
	
	
	public float getDistanceFromPlayer() {
		return distanceFromPlayer;
	}
	public void setDistanceFromPlayer(float distanceFromPlayer) {
		this.distanceFromPlayer = distanceFromPlayer;
	}
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	private Player player; 
	private AnimatedPlayer Aplayer;
	
	
	public Camera(Player player){
		this.player = player;
		this.yOffset = player.getHeight()/1.5f;
		System.out.println(yOffset);
	}
	public Camera(AnimatedPlayer player){
		this.Aplayer = player;
		this.yOffset = player.getHeight()/1.5f;
		System.out.println(yOffset);
		this.animated = true;
	}
	
	public void move(){
		calculateAngleAroundPlayer();
		
		calculatePitch();
		calculateZoom();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		if(!animated){
		calculateCameraPosition(horizontalDistance, verticalDistance);
		}else{
			calculateCameraPositionA(horizontalDistance, verticalDistance);
		}
		
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}
	public void updateYaw() {
		this.yaw = 180 -(Aplayer.getRotY() + angleAroundPlayer);
	}

	public float getRoll() {
		return roll;
	}
	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	private void calculateCameraPosition(float horizDistance, float verticDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		float positionx = player.getPosition().x - offsetX;
		float positionz = player.getPosition().z - offsetZ;
		float positiony = player.getPosition().y + verticDistance+ yOffset;
		position.y = positiony;
		this.yaw = 180 -(player.getRotY() + angleAroundPlayer);
		position.x = positionx;
		position.z = positionz;
		
		
		
	}
	private void calculateCameraPositionA(float horizDistance, float verticDistance){
		float theta = Aplayer.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		float positionx = Aplayer.getPosition().x - offsetX;
		float positionz = Aplayer.getPosition().z - offsetZ;
		float positiony = Aplayer.getPosition().y + verticDistance+ yOffset;
		position.y = positiony;
		this.yaw = 180 -(Aplayer.getRotY() + angleAroundPlayer);
		position.x = positionx;
		position.z = positionz;
		
		
		
	}
	public void invertPitch(){
		this.pitch = -pitch;
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
		if(distanceFromPlayer < 10)distanceFromPlayer =10;
	}
	
	private void calculatePitch(){
		if(Mouse.isButtonDown(2) || Keyboard.isKeyDown(Keyboard.KEY_X)){
			terrainHeight = getActiveTerrain(MainGameLoop.terrain, position.x, position.z).getHeightOfTerrain(position.x, position.z);
			
			minPitch = terrainHeight/distanceFromPlayer;
			minPitch = (float) Math.toDegrees(Math.asin(Math.toRadians(minPitch)));
//			System.out.println("mPitch "+minPitch);
			float pitchChange = Mouse.getDY() * 0.1f;
			
			pitch -= pitchChange;
			if(pitch > 90)pitch = 90;
			else if(pitch < minPitch+yOffset)pitch = minPitch+yOffset; 
		}
	}
	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(1)){
			float angleChange = Mouse.getDX()* 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
	
	public void invertAngleAroundPlayer(){
		angleAroundPlayer += 180;
	}
	private static Terrain getActiveTerrain(HashMap<String,Terrain> terrains,float x, float z){
		int xGrid = (int) (x/Terrain.SIZE);
		int zGrid = (int) (z/Terrain.SIZE);
		
		if(x < 0){
			xGrid+=1;
			xGrid*=-1;
		}
		if(z < 0){
			zGrid+=1;
			zGrid*=-1;
			}
		 
			return terrains.getOrDefault(xGrid+","+zGrid, terrains.get("0,0"));	
		
	}

}
