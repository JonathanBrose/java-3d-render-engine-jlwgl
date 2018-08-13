package frustumCulling;

public class Plane {
	 
	public float a;
	public void setA(float a) {
		this.a = a;
	}

	public void setB(float b) {
		this.b = b;
	}

	public void setC(float c) {
		this.c = c;
	}

	public void setD(float d) {
		this.d = d;
	}

	public float b;
	public float c;
	public float d;
	
	public Plane(float a, float b, float c, float d) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public float getA() {
		return a;
	}

	public float getB() {
		return b;
	}

	public float getC() {
		return c;
	}

	public float getD() {
		return d;
	}
	
	
	
}
