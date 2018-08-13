package renderEngine;

import java.util.List;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import TextureProjection.Beamer;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class TerrainRenderer {

	private TerrainShader shader;
	private Matrix4f beamProjMatrix = new Matrix4f();
	private Beamer beamer;

	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix,float  MapSize, Beamer beamer) {
		this.beamer = beamer;
		this.shader = shader;
		shader.start();
		shader.loadShadowMapSize(MapSize);
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(List<Terrain> terrains, Matrix4f toShadowSpace) {
		shader.loadBeamerViewMatrix(beamer);
	
		shader.loadToShadowSpaceMatrix(toShadowSpace);
	
		for (Terrain terrain : terrains) {
			prepareTerrain(terrain, beamer);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
					GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}

	private void prepareTerrain(Terrain terrain,Beamer beamer) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrain, beamer);
		shader.loadShineVariables(1,0);
		
	}
	private void bindTextures(Terrain terrain, Beamer beamer){
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE7);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, beamer.getTextures()[(int)(beamer.getFps()*beamer.getProgression())]);
		
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
	private void updateOrthoProjectionMatrix(float width, float height, float length) {
		beamProjMatrix.setIdentity();
		beamProjMatrix.m00 = 2f / width;
		beamProjMatrix.m11 = 2f / height;
		beamProjMatrix.m22 = -2f / length;
		beamProjMatrix.m33 = 1;
	}
	
}