package entities;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class Lamp extends Entity {
	private Light light;
	private float lightHeight = 0; // in percent to entity height
	private float lightOffset=0;
	private float alpha=0;


	public Lamp(TexturedModel model,Light light,float lightHeightpercent,  Vector3f position, float rotX,
			float rotY, float rotZ, float scale,int index) {
		
		super(model, index, position, rotX, rotY, rotZ, scale);
		super.getModel().getTexture().setUseFakeLighting(true);
		
		this.lightHeight = lightHeightpercent;
		this.light = new Light(light.getPosition(),light.getColour(),light.getAttenuation());
		
		positionLight();
	}
	public Lamp(TexturedModel model,Light light,float lightHeightpercent, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.lightHeight = lightHeightpercent;
		this.light = new Light(light.getPosition(),light.getColour(),light.getAttenuation());
		
		positionLight();
		
	}
	public void increasePosition(float dx, float dy, float dz) {
		super.getPosition().x += dx;
		super.getPosition().y += dy;
		super.getPosition().z += dz;
		positionLight();
	}
	public void setPosition(Vector3f position) {
		super.setPosition(position);
		positionLight();
	}
	public void setLightOffset(float f,float alpha){
		this.alpha =alpha;
		this.lightOffset=f;
	}
	
	private void positionLight(){
		Vector3f pos = super.getPosition();
		float winkel=super.getRotY()+alpha;
		float dx = (float) (lightOffset*super.getScale() * Math.sin(Math.toRadians(winkel)));
		float dz = (float) (lightOffset * super.getScale()*Math.cos(Math.toRadians(winkel)));
	
		light.setPosition(new Vector3f(pos.x+dx,((pos.y+super.getHeight()) *lightHeight)/100,pos.z+dz));
	}
	
	public Light getLight(){
		return light;
	}
	

}
