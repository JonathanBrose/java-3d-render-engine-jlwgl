package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import ObjectHilighting.HilightShader;
import TextureProjection.Beamer;
import shaders.AnimatedEntityShader;
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import toolbox.Maths;
import engineTester.MainGameLoop;
import entities.AnimatedEntity;
import entities.Camera;
import entities.Entity;
import entities.Light;
import frustumCulling.FrustumG;

public class MasterRenderer {

	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static  float FAR_PLANE = 1000;
	public static final int MAX_LIGHTS = 4;
	public static final int MIN_FPS = 30;

	public static float RED = 0.5444f;
	public static float GREEN = 0.62f;
	public static float BLUE = 0.69f;
	public static Vector3f underwaterColor = new Vector3f(0.156f,0.52f,0.7f);
	public static float underwaterDensity = 0.02f;
	public static float underwaterGradient = 1; 

	private Matrix4f projectionMatrix;
	private Camera camera;
	
	boolean underwaterFog = true;
	

	private static StaticShader shader = new StaticShader();
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private static TerrainShader terrainShader = new TerrainShader();
	private static AnimatedEntityShader AnimatedShader = new AnimatedEntityShader();
	private AnimatedEntityRenderer AnimatedRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;
	

	private static NormalMappingRenderer normalMapRenderer;
	private SkyboxRenderer skyboxRenderer;
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<AnimatedEntity>> AnimatedEntities = new HashMap<TexturedModel, List<AnimatedEntity>>();
	private List<Entity> renderedEntities = new ArrayList<Entity>();
	private List<Entity> renderedNormalEntities = new ArrayList<Entity>();
	private List<Entity> allRenderedEntities = new ArrayList<Entity>();
	private boolean selectedEntitiyNormal;
	private Entity selectedEntity;
	public static boolean underwater;

	Vector2f left = new Vector2f();
	Vector2f right = new Vector2f();
	Vector2f CamLeft = new Vector2f();
	Vector2f CamRight = new Vector2f();
	Vector2f middle = new Vector2f();
	

	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<Entity> renderedAnimatedEntities = new ArrayList<Entity>();
	
	public MasterRenderer(Loader loader, Camera cam, FrustumG frg, Beamer beamer) {
		enableCulling();
		
		createProjectionMatrix();
		this.shadowMapRenderer = new ShadowMapMasterRenderer(cam);
		renderer = new EntityRenderer(shader, projectionMatrix, shadowMapRenderer.getShadowMapSize());
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix,shadowMapRenderer.getShadowMapSize(),beamer);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix, shadowMapRenderer.getShadowMapSize());
		AnimatedRenderer = new AnimatedEntityRenderer(AnimatedShader, projectionMatrix, shadowMapRenderer.getShadowMapSize());
		
		this.camera = cam;

	}

	public void renderScene(Entity selectedEntity,List<AnimatedEntity> AnimatedEntities, List<Entity> entities, List<Entity> normalEntities,
			HashMap<String, Terrain> terrains, List<Light> lights,
			Camera camera, Vector4f clipPlane,Beamer beamer) {
		
		renderedEntities.clear();
		renderedNormalEntities.clear();
		renderedAnimatedEntities.clear();
		allRenderedEntities.clear();
		this.selectedEntity=selectedEntity;

		for (HashMap.Entry<String, Terrain> entry : terrains.entrySet()) {

			processTerrain(entry.getValue());
		}
		for (Entity entity : entities) {
			processEntity(entity);
		}
		for (Entity entity : normalEntities){
			processNormalEntity(entity);
		}
		for (AnimatedEntity entity : AnimatedEntities){
			processAnimatedEntity(entity);
		}
		render(lights, camera,clipPlane,beamer);

	}

	

	public static float getNearPlane() {
		return NEAR_PLANE;
	}

	public static float getFarPlane() {
		return FAR_PLANE;
	}

	public static StaticShader getShader() {
		return shader;
	}

	public static TerrainShader getTerrainShader() {
		return terrainShader;
	}

	public static NormalMappingRenderer getNormalMapRenderer() {
		return normalMapRenderer;
	}

	public SkyboxRenderer getSkyboxRenderer() {
		return skyboxRenderer;
	}

	public Matrix4f getProjectMatrix4f() {
		return projectionMatrix;
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);

	}

	public void render(List<Light> Lights, Camera camera, Vector4f clipPlane,Beamer beamer) {
		prepare();
		
		if(underwaterFog){
			underwater =camera.getPosition().y < MainGameLoop.WaterHeight;
		}else {
			underwater=false;
		}
		shader.start();
		shader.connectTextureUnits();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(RED, GREEN, BLUE);
		shader.loadLights(Lights);
		shader.loadViewMatrix(camera);
		shader.loadUnderwater(underwater);
		shader.loadwaterheight(MainGameLoop.WaterHeight);
		shader.loadUnderwaterColor(underwaterColor);
		shader.loadUnderwaterDensity(underwaterDensity);
		shader.loadUnderwaterGradient(underwaterGradient);
		renderer.render(entities,shadowMapRenderer.getToShadowMapSpaceMatrix());
		shader.stop();
		normalMapRenderer.render(normalMapEntities, clipPlane, Lights, camera);
		AnimatedShader.start();
		AnimatedShader.connectTextureUnits();
		AnimatedShader.loadClipPlane(clipPlane);
		AnimatedShader.loadSkyColor(RED, GREEN, BLUE);
		AnimatedShader.loadLights(Lights);
		AnimatedShader.loadViewMatrix(camera);
		AnimatedShader.loadUnderwater(underwater);
		AnimatedShader.loadwaterheight(MainGameLoop.WaterHeight);
		AnimatedShader.loadUnderwaterColor(underwaterColor);
		AnimatedShader.loadUnderwaterDensity(underwaterDensity);
		AnimatedShader.loadUnderwaterGradient(underwaterGradient);
		AnimatedRenderer.render(AnimatedEntities,shadowMapRenderer.getToShadowMapSpaceMatrix());
		AnimatedShader.stop();
		terrainShader.start();
		terrainShader.connectTextureUnits();
		terrainShader.loadUnderwater(underwater);
		terrainShader.loadwaterheight(MainGameLoop.WaterHeight);
		terrainShader.loadUnderwaterColor(underwaterColor);
		terrainShader.loadUnderwaterDensity(underwaterDensity);
		terrainShader.loadUnderwaterGradient(underwaterGradient);
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColor(RED, GREEN, BLUE);
		terrainShader.loadLights(Lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		skyboxRenderer.render(camera, RED, GREEN, BLUE);
		terrains.clear();
		entities.clear();
		normalMapEntities.clear();
		AnimatedEntities.clear();
		underwaterFog=true;
	}
	
	public void loadRefractRender(boolean b) {
		terrainShader.loadRefractRender(b);
	}
	/**
	 * deactivates the underwatereffects for the next sceneRendering
	 */
	public void deactivateUnderwaterFog(){
		this.underwaterFog = false;
	}

	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	public void processEntity(Entity entity) {
		
			if(renderCheck(entity)){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
		renderedEntities.add(entity);
		allRenderedEntities.add(entity);
		}
		
	}
	public void processAnimatedEntity(AnimatedEntity entity) {
		
		if(renderCheck(entity)){
	TexturedModel entityModel = entity.getModel();
	List<AnimatedEntity> batch = AnimatedEntities.get(entityModel);
	if (batch != null) {
		batch.add(entity);
	} else {
		List<AnimatedEntity> newBatch = new ArrayList<AnimatedEntity>();
		newBatch.add(entity);
		AnimatedEntities.put(entityModel, newBatch);
	}
	renderedAnimatedEntities .add(entity);
	allRenderedEntities.add(entity);
	}
	
}
	
	
	private boolean renderCheck(Entity entity){
		
		return true;
			
	}
	public void processNormalEntity(Entity entity) {
		if(renderCheck(entity)){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
		renderedNormalEntities.add(entity);
		allRenderedEntities.add(entity);
		}
	}
	public List<Entity> getAllRenderedEntities() {
		return allRenderedEntities;
	}

	public void renderShadowMap(List<Entity> entityList, Light sun){
		for(Entity e: entityList){
			processEntity(e);
		}shadowMapRenderer.render(entities, sun);
		entities.clear();
	}

	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp(); 
		AnimatedShader.cleanUp();
		shadowMapRenderer.cleanUp();
	}
	
	public int getShadowMapTexture(){
		return shadowMapRenderer.getShadowMap();
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}

	 private void createProjectionMatrix(){
	    	projectionMatrix = new Matrix4f();
			float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
			float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
			float x_scale = y_scale / aspectRatio;
			float frustum_length = FAR_PLANE - NEAR_PLANE;

			projectionMatrix.m00 = x_scale;
			projectionMatrix.m11 = y_scale;
			projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
			projectionMatrix.m23 = -1;
			projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
			projectionMatrix.m33 = 0;
	    }

	public Matrix4f getProjectionMatrix() {
		// TODO Auto-generated method stub
		return projectionMatrix;
	}

	public List<Entity> getRenderedEntities() {
		return renderedEntities;
	}

	public List<Entity> getRenderedNormalEntities() {
		return renderedNormalEntities;
	}	

}
