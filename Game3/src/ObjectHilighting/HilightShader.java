package ObjectHilighting;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import shaders.ShaderProgram;
import toolbox.Maths;

public class HilightShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/ObjectHilighting/HilightVertex.glsl";
	private static final String FRAGMENT_FILE = "/ObjectHilighting/HilightFragment.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_OutlineColor;
	
	public HilightShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_OutlineColor=super.getUniformLocation("OutlineColor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}
	public void loadOutlineColor(Vector3f color){
		super.loadVector3f(location_OutlineColor, color);
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
