package shadows;

import org.lwjgl.util.vector.Matrix4f;

import entities.AnimatedEntity;
import shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	
	private static final int MAX_JOINTS = 50;
	private static final String VERTEX_FILE = "/shadows/shadowVertexShader.txt";
	private static final String FRAGMENT_FILE = "/shadows/shadowFragmentShader.txt";
	
	private int location_mvpMatrix;
	private int location_jointTransforms[];
	private int location_animated;
	private int location_in_jointIndices;
	private int location_in_weights;
	
	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		location_jointTransforms = new int[MAX_JOINTS]; 
		for(int i=0; i< MAX_JOINTS;i++){
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms["+i+"]");
		}
		 location_animated= super.getUniformLocation("animated");
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "in_textureCoords");
		super.bindAttribute(3, "in_jointIndices");
		super.bindAttribute(4, "in_weights");
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
	public void loadAnimated(boolean a){
		super.loadBoolean(location_animated, a);
		
	}
}
