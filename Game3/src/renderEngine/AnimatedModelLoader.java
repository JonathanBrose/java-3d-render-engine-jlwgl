package renderEngine;

import org.lwjgl.util.vector.Vector3f;





import Animation.GeneralSettings;
import Animation.Joint;
import colladaLoader.ColladaLoader;
import dataStructures.AnimatedModelData;
import dataStructures.JointData;
import dataStructures.MeshData;
import dataStructures.SkeletonData;
import entities.AnimatedEntity;
import entities.AnimatedPlayer;
import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import textures.ModelTexture;
import colladaLoader.MyFile;

public class AnimatedModelLoader {

	/**
	 * Creates an AnimatedEntity from the data in an entity file. It loads up
	 * the collada model data, stores the extracted data in a VAO, sets up the
	 * joint heirarchy, and loads up the entity's texture.
	 * 
	 * @param entityFile
	 *            - the file containing the data for the entity.
	 * @return The animated entity (no animation applied though)
	 */
	
	public static AnimatedEntity loadAnimatedEntity(MyFile modelFile, String textureFile) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, GeneralSettings.MAX_WEIGHTS);
		RawModel model = createRawModel(entityData.getMeshData());
		ModelTexture texture = loadModelTexture(textureFile);
		TexturedModel tm = new TexturedModel(model, texture);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedEntity(tm, new Vector3f(), 0, 0, 0, 1, headJoint,skeletonData.jointCount,entityData.getMeshData() );
	}
	public static AnimatedPlayer loadAnimatedPlayer(MyFile modelFile, String textureFile) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, GeneralSettings.MAX_WEIGHTS);
		RawModel model = createRawModel(entityData.getMeshData());
		ModelTexture texture = loadModelTexture(textureFile);
		TexturedModel tm = new TexturedModel(model, texture);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedPlayer(tm, new Vector3f(), 0, 0, 0, 1, headJoint,skeletonData.jointCount, entityData.getMeshData());
	}

	/**
	 * Loads up the diffuse texture for the model.
	 * 
	 * @param textureFile
	 *            - the texture file.
	 * @return The diffuse texture.
	 */
	
	private static ModelTexture loadModelTexture(String textureFile) {
		Loader loader = new Loader();
		ModelTexture diffuseTexture = new ModelTexture(loader.loadTexture(textureFile));
		return diffuseTexture;
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data
	 *            - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}

	/**
	 * Stores the mesh data in a VAO.
	 * 
	 * @param data
	 *            - all the data about the mesh that needs to be stored in the
	 *            VAO.
	 * @return The VAO containing all the mesh data for the model.
	 */
	
	private static RawModel createRawModel(MeshData data){
		Loader loader = new Loader();
		return loader.loadToVAO(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices(),data.getJointIds(),data.getVertexWeights(),data.getModelheight(),data.getFurthestPoint());
		
	}

}
