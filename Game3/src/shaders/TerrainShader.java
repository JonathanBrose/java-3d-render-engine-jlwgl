package shaders;

import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import TextureProjection.Beamer;
import renderEngine.MasterRenderer;
import toolbox.Maths;
import entities.Camera;
import entities.Light;

public class TerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "/shaders/terrainFragmentShader.txt";
	private static final int MAX_LIGHTS = MasterRenderer.MAX_LIGHTS;

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColour;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColor;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_attenuation[];
	private int location_blendMap;
	private int location_plane;
	private int location_cel;
	private int location_toShadowSpaceMatrix;
	private int location_shadowMap;
	private int location_mapSize;
	private int location_underwater;
	private int location_underwaterColor;
	private int location_underwaterDensity;
	private int location_underwaterGradient;
	private int location_waterheight;
	private int location_projectionTexture;
	private int location_beamerTexture;
	private int location_beamerViewMatrix;
	private int location_depthMap;
	private int location_refractRender;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void loadSkyColor(float r, float g, float b) {
		super.loadVector3f(location_skyColor, new Vector3f(r, b, g));
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_plane = super.getUniformLocation("plane");
		location_beamerViewMatrix = super.getUniformLocation("beamerViewMatrix");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColor = super.getUniformLocation("skyColor");
		location_backgroundTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("gTexture");
		location_blendMap = super.getUniformLocation("blendMap");
		location_cel = super.getUniformLocation("celShading");
		location_toShadowSpaceMatrix = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_mapSize = super.getUniformLocation("mapSize");
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		location_waterheight = super.getUniformLocation("waterheight");
		location_underwater = super.getUniformLocation("underwater");
		location_underwaterColor = super.getUniformLocation("underwaterColor");
		location_underwaterDensity = super.getUniformLocation("underwaterDensity");
		location_underwaterGradient = super.getUniformLocation("underwaterGradient");
		location_projectionTexture = super.getUniformLocation("projectionTexture");
		location_beamerTexture = super.getUniformLocation("beamerTexture");
		location_depthMap = super.getUniformLocation("depthMap");
		location_refractRender = super.getUniformLocation("refractRender");

		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}

	}

	public void loadBeamerViewMatrix(Beamer beamer) {
		super.loadMatrix(location_beamerViewMatrix, Maths.createViewMatrix(beamer));
	}

	public void loadwaterheight(float f) {
		super.loadFloat(location_waterheight, f);

	}

	public void loadUnderwaterGradient(float f) {
		super.loadFloat(location_underwaterGradient, f);
	}

	public void loadUnderwaterDensity(float f) {
		super.loadFloat(location_underwaterDensity, f);
	}

	public void loadUnderwaterColor(Vector3f color) {
		super.loadVector3f(location_underwaterColor, color);
	}

	public void loadUnderwater(boolean underwater) {
		super.loadBoolean(location_underwater, underwater);
	}

	public void loadToShadowSpaceMatrix(Matrix4f mat) {
		super.loadMatrix(location_toShadowSpaceMatrix, mat);
	}

	public void loadShadowMapSize(float f) {
		super.loadFloat(location_mapSize, f);
	}

	public void setCelShading(boolean b) {
		super.loadBoolean(location_cel, b);
	}

	public void loadClipPlane(Vector4f plane) {
		super.loadVector(location_plane, plane);
	}

	public void connectTextureUnits() {
		super.loadInt(location_backgroundTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
		super.loadInt(location_shadowMap, 5);
		super.loadInt(location_projectionTexture, 6);
		super.loadInt(location_beamerTexture, 7);
		super.loadInt(location_depthMap, 8);

	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadRefractRender(boolean b) {
		float f;
		if (b)
			f = 1;
		else
			f = 0;
		super.loadFloat(location_refractRender, f);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector3f(location_lightColour[i], lights.get(i).getColour());
				super.loadVector3f(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector3f(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector3f(location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector3f(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

}
