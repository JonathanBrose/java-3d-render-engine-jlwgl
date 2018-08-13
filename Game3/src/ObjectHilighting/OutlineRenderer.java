package ObjectHilighting;

import java.util.ArrayList;
import java.util.List;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import postProcessing.Fbo;
import toolbox.Maths;
import entities.AnimatedPlayer;
import entities.Camera;
import entities.Entity;

public class OutlineRenderer {

	public static float RED = 0f;
	public static float GREEN = 0f;
	public static float BLUE = 0f;

	private Matrix4f projectionMatrix;
	private HilightShader shader = new HilightShader();
	private Camera camera;
	private Vector3f pos;

	private Vector3f OutlineColor = new Vector3f(1, 1, 1);

	public OutlineRenderer(Matrix4f projectionMatrix, Camera cam) {

		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		this.camera = cam;
		this.projectionMatrix = projectionMatrix;

	}

	public void cleanUp() {
		shader.cleanUp();
	}

	public Matrix4f getProjectMatrix() {
		return projectionMatrix;
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_FRONT);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);

	}

	public void render(List<Entity> entities) {
		
		
		
		shader.start();
		shader.loadViewMatrix(camera);
		
		
		
		
		
	
			for (Entity entity : entities) {
				
				if(entity!=null && entity.getClass()!= AnimatedPlayer.class){
				prepareTexturedModel(entity.getModel());
				
				
				
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getRawModel().getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
				
				}
			}
			unbindTexturedModel();
			
			shader.stop();
			
			
			
		}

	public void render(Entity entity) {
		enableCulling();

		shader.start();
		shader.loadViewMatrix(camera);

		if (entity != null && entity.getClass() != AnimatedPlayer.class) {
			prepareTexturedModel(entity.getModel());

			prepareInstance(entity);
			GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel()
					.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

		}

		unbindTexturedModel();

		shader.stop();

		disableCulling();
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);

	}

	

	private void unbindTexturedModel() {

		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		float scale = 1.02f;
		pos = entity.getPosition();
		pos.z-= (1-scale)/2;
		pos.x-= (1-scale)/2;
		pos.y-= (1-scale)/2;
		
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				pos, entity.getRotX(), entity.getRotY(),
				entity.getRotZ(), entity.getScale() * scale);
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOutlineColor(OutlineColor);

	}

}
