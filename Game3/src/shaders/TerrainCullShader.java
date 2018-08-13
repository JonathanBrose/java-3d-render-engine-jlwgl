package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.MasterRenderer;
import toolbox.Maths;
import TextureProjection.Beamer;
import entities.Camera;
import entities.Light;

public class TerrainCullShader extends ShaderProgram {

	
	
	private static final String VERTEX_FILE = "/shaders/terrainCullVertexShader.txt";
	private static final String FRAGMENT_FILE = "/shaders/terrainCullFragmentShader.txt";
	
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_plane;
	
		

	
	public TerrainCullShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_plane = super.getUniformLocation("plane");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		
		
		
	}
	
	public void loadClipPlane(Vector4f plane){
		super.loadVector(location_plane, plane);
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
