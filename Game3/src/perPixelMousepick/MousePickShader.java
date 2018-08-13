package perPixelMousepick;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;
import toolbox.Maths;
import entities.Camera;

public class MousePickShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/perPixelMousepick/MousePickVertexShader.txt";
	private static final String FRAGMENT_FILE = "/perPixelMousepick/MousePickFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_EntityColor;
	
	public MousePickShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_EntityColor=super.getUniformLocation("EntityColor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}
	public void loadEntityColor(Vector3f color){
		super.loadVector3f(location_EntityColor, color);
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