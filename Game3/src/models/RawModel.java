package models;

import org.lwjgl.util.vector.Vector4f;

public class RawModel {
	
	private int vaoID;
	private int vertexCount;
	private float height;
	private float furthestPoint;
	
	public RawModel(int vaoID, int vertexCount){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	public RawModel(int vaoID, int vertexCount,float height){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.height = height;
	}
	public RawModel(int vaoID, int vertexCount,float height,float furthestPoint){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.height = height;
		this.furthestPoint = furthestPoint;
	}

	public int getVaoID() {
		return vaoID;
	}
	public float getHeight() {
		return height;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public float getFurthestPoint(){
		return furthestPoint;
	}

}
