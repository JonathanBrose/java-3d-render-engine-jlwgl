package renderEngine;

import java.util.List;

import models.RawModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import shaders.TerrainCullShader;
import shaders.TerrainShader;
import terrains.Terrain;
import toolbox.Maths;

public class TerrainCullRenderer {
	private TerrainCullShader shader;
	private Camera cam;

	public TerrainCullRenderer(TerrainCullShader shader,
			Matrix4f projectionMatrix, Camera cam) {
		this.cam = cam;
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(List<Terrain> terrains, Vector4f clipPlane) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		shader.loadClipPlane(clipPlane);
		shader.loadViewMatrix(cam);
		for (Terrain terrain : terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel()
					.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}

	public void render(Terrain terrain, Vector4f clipPlane) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0,0,0,1.0f);
		shader.loadClipPlane(clipPlane);
		shader.loadViewMatrix(cam);
		prepareTerrain(terrain);
		loadModelMatrix(terrain);
		GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel()
				.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		unbindTexturedModel();

	}

	private void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);

	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}

}
