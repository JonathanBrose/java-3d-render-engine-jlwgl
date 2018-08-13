package perPixelMousepick;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import postProcessing.Fbo;
import renderEngine.EntityRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.TerrainRenderer;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import toolbox.Maths;
import entities.AnimatedPlayer;
import entities.Camera;
import entities.Entity;
import entities.Light;

public class MousePickRenderer {
	
	public static float RED = 0f;
	public static float GREEN = 0f;
	public static float BLUE = 0f;
	
	private static List<Vector3f> Colors =  new ArrayList<Vector3f>();

	private Matrix4f projectionMatrix;
	private MousePickShader shader = new MousePickShader();
	private Camera camera;

	private List<Entity> entities = new ArrayList<Entity>();
	private Map< Entity,Vector3f> FinalEntities = new HashMap< Entity,Vector3f>();
	private Map< String,Entity> ColorEntities = new HashMap< String,Entity>();
	private Fbo fbo;
	int i =1;
	
	
	public MousePickRenderer(List<Entity> entities, Matrix4f projectionMatrix,Camera cam) {
		fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		this.camera = cam;
		this.entities = entities;
		enableCulling();
		this.projectionMatrix = projectionMatrix;
	
			for(int y= 0; y< 256; y++){
				for(int j= 0; j< 256; j++){
			Colors.add(new Vector3f(0,y,j));
		
				}
			}
				
		
		

		

	}
	public void cleanUp(){
		shader.cleanUp();
	}
	
	

		

	public Matrix4f getProjectMatrix() {
		return projectionMatrix;
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);

	}

	

	
	public void render(List<Entity> entities) {
		
		fbo.bindFrameBuffer();
		disableCulling();
		shader.start();
		shader.loadViewMatrix(camera);
		
		
		i =1;
		FinalEntities.clear();
		ColorEntities.clear();
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			for (Entity entity : entities) {
				
				if(entity!=null && entity.getClass()!= AnimatedPlayer.class){
				prepareTexturedModel(entity.getModel());
				Vector3f color = Colors.get(i);
				FinalEntities.put(entity, new Vector3f(0,color.y/255f,color.z/255f));
				ColorEntities.put(color.toString(), entity);
				
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getRawModel().getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
				i++;
				}
			}
			unbindTexturedModel();
			
			shader.stop();
			fbo.unbindFrameBuffer();
			
			enableCulling();
		}
	
	public Entity getEntityByColor(Vector3f color ){
		
		
		
		
		
		return ColorEntities.get(color.toString());
	}
	
	
	public Vector3f getColorForMouse(){
		
		fbo.bindToRead();
//		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
//		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
		ByteBuffer fb = BufferUtils.createByteBuffer(3);
		
		fb.rewind();
		GL11.glReadPixels(Mouse.getX(), Mouse.getY(),1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, fb);
		fb.rewind();
		
		Vector3f color =new Vector3f(0,fb.get(1),fb.get(2));
		if(color.z <0.0f){
			color.z += 256;
		}
		if(color.y <0.0f){
			color.y += 256;
		}
		
		
		return color;
		
		
	}
	
	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);

		
		
		
		
		
		
	}
	
	public Fbo getFbo(){
		return fbo;
	}

	private void unbindTexturedModel() {
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadEntityColor(FinalEntities.get(entity));
		
		
	}
}