package renderEngine;

import java.util.List;
import java.util.Map;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.AnimatedEntity;
import entities.Entity;
import shaders.AnimatedEntityShader;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

public class AnimatedEntityRenderer {

	private AnimatedEntityShader shader;
	



	public AnimatedEntityRenderer(AnimatedEntityShader shader,Matrix4f projectionMatrix, float mapSize) {
		this.shader = shader;
		shader.start();
		shader.loadShadowMapSize(mapSize);
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadShadowMap();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<AnimatedEntity>> entities, Matrix4f toShadowSpace) {
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<AnimatedEntity> batch = entities.get(model);
			for (AnimatedEntity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		ModelTexture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());
		if(texture.isHasTransparency()){
			MasterRenderer.disableCulling();
		}
		shader.LoadFakeLightingVariable(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		shader.loadUseSpecularMap(texture.hasSpecularMap());
		if(texture.hasSpecularMap()){
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getSpecularMap());
		}
	}

	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(AnimatedEntity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadJointTransformMatrix(entity.getJointTransforms());
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}

}
