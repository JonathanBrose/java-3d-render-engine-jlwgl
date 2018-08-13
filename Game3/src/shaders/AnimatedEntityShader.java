package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.MasterRenderer;
import toolbox.Maths;
import entities.Camera;
import entities.Light;

public class AnimatedEntityShader extends ShaderProgram{
	
	private static final int MAX_LIGHTS = MasterRenderer.MAX_LIGHTS;
	private static final int MAX_JOINTS = 50;
	
	private static final String VERTEX_FILE = "/shaders/animatedEntityVertex.glsl";
	private static final String FRAGMENT_FILE = "/shaders/animatedEntityFragment.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	private int location_cel;
	private int location_jointTransforms[];
	private int location_toShadowSpaceMatrix;
	private int location_mapSize;
	private int location_shadowMap;
	private int location_underwater;
	private int location_underwaterColor;
	private int location_underwaterDensity;
	private int location_underwaterGradient;
	private int location_waterheight;
	private int location_specularMap;
	private int location_usesSpecularMap;
	private int location_modelTexture;
		
	
	
	public AnimatedEntityShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "in_jointIndices");
		super.bindAttribute(4, "in_weights");
	}
	public void setCelShading(boolean b){
		super.loadBoolean(location_cel, b);
	}
	public void loadToShadowSpaceMatrix(Matrix4f mat){
		super.loadMatrix(location_toShadowSpaceMatrix, mat);
	}
		
	@Override
	protected void getAllUniformLocations() {
			location_toShadowSpaceMatrix = super.getUniformLocation("toShadowSpace");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_cel = super.getUniformLocation("celShading");
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		location_plane = super.getUniformLocation("plane");
		location_jointTransforms = new int[MAX_JOINTS]; 
		location_mapSize = super.getUniformLocation("mapSize");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_underwater = super.getUniformLocation("underwater");
		location_underwaterColor= super.getUniformLocation("underwaterColor");
		location_underwaterDensity= super.getUniformLocation("underwaterDensity");
		location_underwaterGradient= super.getUniformLocation("underwaterGradient");
		location_waterheight=super.getUniformLocation("waterheight");
		location_specularMap = super.getUniformLocation("specularMap");
		location_usesSpecularMap = super.getUniformLocation("usesSpecularMap");
		location_modelTexture = super.getUniformLocation("modelTexture");
		
		
		for(int i=0; i<MAX_LIGHTS;i++){
			location_lightColour[i] = super.getUniformLocation("lightColour["+i+"]");
			location_lightPosition[i] = super.getUniformLocation("lightPosition["+i+"]");
			location_attenuation[i] = super.getUniformLocation("attenuation["+i+"]");
		}
		for(int i=0; i< MAX_JOINTS;i++){
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms["+i+"]");
		}
		
		
	}
	public void connectTextureUnits(){
		super.loadInt(location_shadowMap, 5);
		super.loadInt(location_modelTexture, 0);
		super.loadInt(location_specularMap, 1);
	}
	public void loadUseSpecularMap(boolean b){
		super.loadBoolean(location_usesSpecularMap, b);
	}
	
	public void loadwaterheight(float f){
		super.loadFloat(location_waterheight, f);
	}
	public void loadUnderwaterGradient(float f){
		super.loadFloat(location_underwaterGradient, f);
	}
	public void loadUnderwaterDensity(float f){
		super.loadFloat(location_underwaterDensity, f);
	}
	
	public void loadUnderwaterColor(Vector3f color){
		super.loadVector3f(location_underwaterColor, color);
	}
	
	public void loadUnderwater(boolean underwater){
		super.loadBoolean(location_underwater, underwater);
	}
	public void loadJointTransformMatrix(Matrix4f[] transforms){
		for(int i=0; i< MAX_JOINTS;i++){
			if(i<transforms.length){
				super.loadMatrix(location_jointTransforms[i], transforms[i]);
			}else{
				super.loadMatrix(location_jointTransforms[i], new Matrix4f());
			}
		}
	}
	
	public void loadNumberOfRows(int num){
	super.loadFloat(location_numberOfRows, num);	
	}
	public void loadShadowMap(){
		super.loadInt(location_shadowMap, 5);
	}
	
	public void loadOffset(float x, float y){
		super.loadVector2D(location_offset, new Vector2f(x,y));
		}
	
	public void loadSkyColor(float r, float g,float b){
		super.loadVector3f(location_skyColor, new Vector3f(r,b,g));
	}
	
	public void LoadFakeLightingVariable(boolean useFake){
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	public void loadClipPlane(Vector4f plane){
		super.loadVector(location_plane, plane);
	}
	
	public void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	
	public void loadShadowMapSize(float f){
		super.loadFloat(location_mapSize, f);
	}
	
	public void loadLights(List<Light> lights){
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if(i<lights.size()){
				super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector3f(location_lightColour[i], lights.get(i).getColour());
				super.loadVector3f(location_attenuation[i], lights.get(i).getAttenuation());
			}else{
				super.loadVector3f(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector3f(location_lightColour[i],  new Vector3f(0,0,0));
				super.loadVector3f(location_attenuation[i],new Vector3f(0,0,0));
			}
		}
	}
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
public void loadProjectionMatrix(Matrix4f projection){
	super.loadMatrix(location_projectionMatrix, projection);
}

	

}
