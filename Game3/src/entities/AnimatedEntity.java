package entities;

import models.TexturedModel;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import dataStructures.MeshData;
import Animation.Animation;
import Animation.Animator;
import Animation.EntityAnimator;
import Animation.Joint;


public class AnimatedEntity extends Entity {
	
	private final Joint rootJoint;
	private final int jointCount;
	private MeshData meshData;

	private final Animator animator;

	public AnimatedEntity(TexturedModel model,  Vector3f position,
			float rotX, float rotY, float rotZ, float scale,Joint rootJoint, int jointCount, MeshData meshData) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.animator = new Animator(this);
		rootJoint.calcInverseBindTransform(new Matrix4f());
		this.meshData = meshData;
	
		
	}
	
	public Joint getRootJoint(){
		return rootJoint;
	}
	public void doAnimation(Animation animation,boolean loop) {
		animator.doAnimation(animation,loop);
	}
public MeshData getMeshData(){
	return meshData;
}
	/**
	 * Updates the animator for this entity, basically updating the animated
	 * pose of the entity. Must be called every frame.
	 */
	public void update() {
		animator.update();
	}

	/**
	 * Gets an array of the all important model-space transforms of all the
	 * joints (with the current animation pose applied) in the entity. The
	 * joints are ordered in the array based on their joint index. The position
	 * of each joint's transform in the array is equal to the joint's index.
	 * 
	 * @return The array of model-space transforms of the joints in the current
	 *         animation pose.
	 */
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}

	/**
	 * This adds the current model-space transform of a joint (and all of its
	 * descendants) into an array of transforms. The joint's transform is added
	 * into the array at the position equal to the joint's index.
	 * 
	 * @param headJoint
	 *            - the current joint being added to the array. This method also
	 *            adds the transforms of all the descendents of this joint too.
	 * @param jointMatrices
	 *            - the array of joint transforms that is being filled.
	 */
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

}
