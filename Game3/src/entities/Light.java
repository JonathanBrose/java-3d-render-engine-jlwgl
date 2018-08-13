package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {
	
	private Vector3f position;
	private Vector3f colour;
	private Vector3f attenuation = new Vector3f(1,0,0);
	private float distanceToCamera;

	private float lightValueChange = 0.0019f;

public void increaseColor(Vector3f colorIncrease){
  colorIncrease = new Vector3f(getColour().x+lightValueChange,getColour().y+lightValueChange,getColour().z+lightValueChange);
  setColour(new Vector3f(colorIncrease.x,colorIncrease.y,colorIncrease.z));
 }
 public void decreaseColor(Vector3f colorDecrease){
  colorDecrease = new Vector3f(getColour().x-lightValueChange,getColour().y-lightValueChange,getColour().z-lightValueChange);
  setColour(new Vector3f(colorDecrease.x,colorDecrease.y,colorDecrease.z));
 }

	
	public Light(Vector3f position, Vector3f colour) {
		this.position = position;
		this.colour = colour;
	}
	public Light(Vector3f position, Vector3f colour,Vector3f attenuation) {
		this.position = position;
		this.colour = colour;
		this.attenuation = attenuation;
	}
	public float updateDistanceToCamera(Camera cam){
		this.distanceToCamera=distance(cam.getPosition(),position);
		this.distanceToCamera *= distanceToCamera < 0? -1 : 1;
		return distanceToCamera;
	}
	public float getDistanceToCamera(){
		return distanceToCamera;
	}
	private static float distance(Vector3f v, Vector3f u){
		return (float) Math.sqrt((v.x-u.x)*(v.x-u.x)+(v.y-u.y)*(v.y-u.y)+(v.z-u.z)*(v.z-u.z));
	}

	public Vector3f getPosition() {
		return position;
	}
	public void getPosition(Vector3f position) {
		this.position= position;
	}
	public Vector3f getAttenuation(){
		return attenuation;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColour() {
		return colour;
	}

	public void setColour(Vector3f colour) {
		this.colour = colour;
	}
	

}
