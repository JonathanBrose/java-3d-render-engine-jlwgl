package TextureProjection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import engineTester.MainGameLoop;
import entities.Camera;
import entities.Entity;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class Beamer {
	private int[] textures;
	private int fps;
	private float progression = 0;
	private Vector3f position = new Vector3f(0,100,0);
	private float pitch = 90;
	private float yaw ;
	private float roll;
	private float height=100000;
	
	
	
	public Beamer(Loader loader){
		loadTextureArray(loadCausticsNames(), loader);
		
	}

	
	
	public void update(){
		position.y = height;
		
		progression+=  DisplayManager.getFrameTimeSeconds();

		if(progression*fps >= textures.length)progression = 0;
//		
//		GL13.glActiveTexture(GL13.GL_TEXTURE7);
//		
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[(int)(progression * fps)]);

		
		
		
	}
	public float getProgression() {
		return progression;
	}
	public void setProgression(float progression) {
		this.progression = progression;
	}
	public void loadTextureArray(String[] fileNames,Loader loader){
		textures= new int[fileNames.length];
		for(int i=0; i < fileNames.length; i++){
			
			textures[i]= loader.loadTexture(fileNames[i]);
			
			
			
		}
	
	}
	public static String[] loadCausticsNames(){
		String[] names = new String[60];
		for(int i = 1; i<= 60; i++){
			names[i-1]="Caustics/CausticsRender_0"+ (i > 9 ? "":"0")+i;
		}
		return names;
	}
	
	
	
	
	public int[] getTextures() {
		return textures;
	}
	public void setTextures(int[] textures) {
		this.textures = textures;
	}
	public int getFps() {
		return fps;
	}
	public void setFps(int fps) {
		this.fps = fps;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public float getPitch() {
		return pitch;
	}
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	public float getYaw() {
		return yaw;
	}
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	public float getRoll() {
		return roll;
	}
	public void setRoll(float roll) {
		this.roll = roll;
	}

	
}
