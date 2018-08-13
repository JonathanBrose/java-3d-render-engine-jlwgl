package engineTester;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import normalMappingRenderer.NormalMappingShader;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import TextureProjection.Beamer;
import colladaLoader.MyFile;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import perPixelMousepick.MousePickRenderer;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.AnimatedModelLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.TerrainCullRenderer;
import shaders.StaticShader;
import shaders.TerrainCullShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import entities.AnimatedEntity;
import entities.AnimatedPlayer;
import entities.Camera;
import entities.Entity;
import entities.Lamp;
import entities.Light;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import frustumCulling.FrustumG;
import gui.GuiRenderer;
import gui.GuiTexture;

public class MainGameLoop {

	static float x;
	static float z;
	static float y;
	public static HashMap<String, Terrain> terrain = new HashMap<String, Terrain>();
	public static List<Entity> normalEntities = new ArrayList<Entity>();
	public static List<AnimatedEntity> AnimatedEntities = new ArrayList<AnimatedEntity>();
	static float Xmax = 0;
	static float Zmax = 0;
	static float Xmin = 0;
	static float Zmin = 0;
	static AnimatedPlayer player;
	static boolean mouseClicked = false;
	static int FPS = 0;
	public static int Fps = 0;
	static boolean sync = false;
	static List<WaterTile> waters = new ArrayList<WaterTile>();
	protected static Entity selectedEntity;
	protected static Entity MouseOverEntity;
	public static int testTexture;

	public static float WaterHeight = -2.5f;

	public static void main(String[] args) {
		switch (JOptionPane.showConfirmDialog(null, "Fullscreen?")) {
		case 0:
			DisplayManager.setFullscreen(true);
			break;
		case 2:
			System.exit(0);
		}

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		player = AnimatedModelLoader.loadAnimatedPlayer(new MyFile(
				"res/modelsmooth.dae"), "diffuse");
		player.setScale(1.1f);

		Beamer beamer = new Beamer(loader);
		beamer.setFps(24);

		// ---------other----------------------------------------------------

		Camera camera = new Camera(player);
		FrustumG frG = new FrustumG();
		MasterRenderer renderer = new MasterRenderer(loader, camera, frG,
				beamer);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());

		FontType font = new FontType(loader.loadFontTextureAtlas("arial"),
				"arial");

		// ------------terrain----------------------------------------------------

		TerrainTexture backgroundTexture = new TerrainTexture(
				loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(
				loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(
				backgroundTexture, rTexture, gTexture, bTexture);

		TerrainTexture blendMap = new TerrainTexture(
				loader.loadTexture("blendMap"));
		String heightmap = "heightmap";

		Terrain terrain1 = new Terrain(0, 0, loader, texturePack, blendMap,
				heightmap);

		waters.add(new WaterTile(WaterTile.TILE_SIZE, WaterTile.TILE_SIZE,
				WaterHeight));

		System.out.println(waters.size());

		terrain.put("0,0", terrain1);

		for (HashMap.Entry<String, Terrain> entry : terrain.entrySet()) {
			float C = entry.getValue().getX() + Terrain.SIZE;
			if (Xmax < C)
				Xmax = C;
			C -= Terrain.SIZE;
			if (Xmin > C)
				Xmin = C;

			C = entry.getValue().getZ() + Terrain.SIZE;
			if (Zmax < C)
				Zmax = C;
			C -= Terrain.SIZE;
			if (Zmin > C)
				Zmin = C;

		}
		System.out.println("xmax " + Xmax + " xmin " + Xmin + " Zmax " + Zmax
				+ " zmin " + Zmin);

		// -----------------------------------------------------------

		RawModel model = OBJLoader.loadObjModel("lantern", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(
				loader.loadTexture("lantern")));
		staticModel.getTexture().setSpecularMap(loader.loadTexture("lanternS"));

		TexturedModel lowPolyTree = new TexturedModel(OBJLoader.loadObjModel(
				"lowPolyTree", loader), new ModelTexture(
				loader.loadTexture("lowPolyTree")));

		// -----------------------------------------------------------

		TexturedModel tree2 = new TexturedModel(OBJLoader.loadObjModel(
				"cherry", loader), new ModelTexture(
				loader.loadTexture("cherry")));
		tree2.getTexture().setHasTransparency(true);
		tree2.getTexture().setShineDamper(10);
		tree2.getTexture().setReflectivity(0.5f);
		tree2.getTexture().setSpecularMap(loader.loadTexture("cherryS"));

		// -----------------------------------------------------------

		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel(
				"grassModel", loader), new ModelTexture(
				loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);

		// -----------------------------------------------------------

		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel(
				"grassModel", loader), new ModelTexture(
				loader.loadTexture("flower")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);

		// -----------------------------------------------------------

		TexturedModel dragon = new TexturedModel(OBJLoader.loadObjModel(
				"dragon", loader),
				new ModelTexture(loader.loadTexture("white")));
		dragon.getTexture().setReflectivity(0.5f);
		dragon.getTexture().setShineDamper(8);

		// ------------------------------------------------------------------------
		TexturedModel bunny = new TexturedModel(OBJLoader.loadObjModel("bunny",
				loader), new ModelTexture(loader.loadTexture("white")));
		bunny.getTexture().setReflectivity(1);
		bunny.getTexture().setShineDamper(100);

		// ----------------------------------------------------------------
		ModelTexture fernTM = new ModelTexture(loader.loadTexture("fern"));
		fernTM.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",
				loader), fernTM);
		fern.getTexture().setHasTransparency(true);

		// -----------------------------------------------------------------

		ModelTexture pineTM = new ModelTexture(loader.loadTexture("pine"));

		TexturedModel pine = new TexturedModel(OBJLoader.loadObjModel("pine",
				loader), pineTM);
		pine.getTexture().setHasTransparency(true);

		ModelTexture lampTM = new ModelTexture(loader.loadTexture("lamp"));

		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp",
				loader), lampTM);
		lamp.getTexture().setUseFakeLighting(true);
		lamp.getTexture().setSpecularMap(loader.loadTexture("lampS"));

		List<Entity> entities = new ArrayList<Entity>();
		List<Lamp> Lamps = new ArrayList<Lamp>();
		Random random = new Random();
		// -----------------normal map Models---------------------------------

		TexturedModel barrelModel = new TexturedModel(
				NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(
				loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(1f);
		barrelModel.getTexture().setSpecularMap(loader.loadTexture("barrelS"));
		Entity Barrel = new Entity(barrelModel, new Vector3f(90, 10, 95), 0, 0,
				0, 1f);

		normalEntities.add(Barrel);

		x = 0;
		z = 0;
		y = 0;

		// ----------------------------entity generate
		// loop-------------------------------
		Light Tlight = new Light(new Vector3f(160, 0, -293), new Vector3f(1.2f,
				1.2f, 1.2f), new Vector3f(1, 0.01f, 0.002f));

		for (int i = 0; i < 130; i++) {

			if (i % 5 == 0) {
				genEntityPos(terrain1);
				Lamp lamp1=new Lamp(staticModel, Tlight, 75, new Vector3f(x, y,
						z), 0, 0, 0, 0.8f + (random.nextFloat() * 0.5f) - 0.25f);
			
				Lamps.add(lamp1);

			}
			genEntityPos(terrain1);
			entities.add(new Entity(lowPolyTree, new Vector3f(x, y, z), 0, 0,
					0, random.nextFloat() * 2));
			genEntityPos(terrain1);
			entities.add(new Entity(tree2, new Vector3f(x, y, z), 0, 0, 0,
					5 + (random.nextFloat() * 2) - 1f));
			genEntityPos(terrain1);
			entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1));
			genEntityPos(terrain1);
			entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y,
					z), 0, 0, 0, 0.6f));
			genEntityPos(terrain1);
			entities.add(new Entity(flower, new Vector3f(x, y, z), 0, 0, 0, 1));
			genEntityPos(terrain1);
			entities.add(new Entity(pine, new Vector3f(x, y, z), 0, 0, 0,
					random.nextFloat() * 2));

			genEntityPos(terrain1);

			normalEntities.add(new Entity(barrelModel, new Vector3f(x, y
					+ barrelModel.getRawModel().getHeight() / 2, z), 0f, 0f,
					0f, 1f));
		}

		// for (int i = 0; i < 500; i++) {
		// x = 0;
		// z = 0;
		// y = 20;
		//
		// entities.add(new Entity(staticModel, new Vector3f(x, y, z), 0, 0,
		// 0, random.nextFloat() * 2));
		//
		// entities.add(new Entity(tree2, new Vector3f(x, y, z), 0, 0, 0,
		// random.nextFloat()));
		//
		// entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1));
		//
		// entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y,
		// z), 0, 0, 0, 0.6f));
		//
		// entities.add(new Entity(flower, new Vector3f(x, y, z), 0, 0, 0, 1));
		//
		// entities.add(new Entity(pine, new Vector3f(x, y, z), 0, 0, 0,
		// random.nextFloat() * 2));
		//
		//
		//
		// }

		// -----------------------------------------------------------
		// genEntityPos(terrain);
		// entities.add(new Entity(dragon, new Vector3f(x, y, z),
		// 0, 0, 0, 10f));
		// genEntityPos(terrain);
		// entities.add(new Entity(bunny, new Vector3f(x, y, z),
		// 0, 0, 0, 1.5f));
		//

		// ----------light------------------------------------------------

		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(100, 150000, -100), new Vector3f(1f,
				1f, 1f));
		lights.add(sun);

		Lamp Tlamp = new Lamp(lamp, Tlight, 95, new Vector3f(160, 0, 293), 0,
				0, 0, 1);
		Lamp lamp2 = new Lamp(lamp, Tlight, 95, new Vector3f(200, 0, 293), 0,
				0, 0, 1);

		
		Lamps.add(lamp2);
		Lamps.add(Tlamp);

		for (Lamp l : Lamps) {
			lights.add(l.getLight());
			entities.add(l);

		}

		// ----------------------water-------------------------------------
		WaterFrameBuffers fbos = new WaterFrameBuffers();

		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader,
				renderer.getProjectionMatrix(), fbos);

		List<GuiTexture> guis = new ArrayList<GuiTexture>();

		AnimatedEntities.add(player);
		// ----------gui-----------------------------------------

		
		// ------------celshading----------------------------------------

		setCelShading(false);

		// ------------------------------------------------------------------
		SkyboxRenderer.time = 10000;

		GUIText postext = new GUIText(
				"Position: x" + player.getPosition().x + " z"
						+ player.getPosition().z + " y"
						+ player.getPosition().y, 1, font, new Vector2f(0f,
						0.2f), 1f, false);
		GUIText fpstext = new GUIText(
				"Frames: " + Fps + "   " + selectedEntity, 1, font,
				new Vector2f(0f, 0.1f), 1f, false);

		// -----------------------------------------------------------------

		ParticleTexture particleTexture = new ParticleTexture(
				loader.loadTexture("cosmic"), 4, true);
		ParticleSystem system = new ParticleSystem(particleTexture, 60, 10,
				0.1f, 1f, 20f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.25f);
		system.setScaleError(0.5f);
		system.randomizeRotation();
		ParticleTexture particleTexture2 = new ParticleTexture(
				loader.loadTexture("fire"), 8, true);
		new ParticleSystem(particleTexture2, 60, 10, 0.1f, 1f, 60f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.25f);
		system.setScaleError(0.5f);
		system.randomizeRotation();
		ParticleTexture particleTexture3 = new ParticleTexture(
				loader.loadTexture("smoke"), 8, false);
		new ParticleSystem(particleTexture3, 60, 10, 0.1f, 1f, 6f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.25f);
		system.setScaleError(0.5f);
		system.randomizeRotation();

		// -------------------------------------------------------------

		List<Entity> allENTITIES = new ArrayList<Entity>();
		for (Entity entity : normalEntities) {
			allENTITIES.add(entity);
		}
		for (Entity entity : AnimatedEntities) {
			allENTITIES.add(entity);
		}
		for (Entity entity : entities) {
			allENTITIES.add(entity);
		}

		// -----------------------------------------------------------------

		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight(),
				true);
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(),
				Fbo.DEPTH_TEXTURE);
		Fbo outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(),
				Fbo.DEPTH_TEXTURE);
		

		PostProcessing.init(loader);

		MousePickRenderer mpRenderer = new MousePickRenderer(allENTITIES,
				renderer.getProjectionMatrix(), camera);
		MousePicker mpRay = new MousePicker(camera,
				renderer.getProjectionMatrix(), terrain);

		/*
		 * 
		 * looop
		 */

		while (!Display.isCloseRequested()) {
			sortLightsByDistanceToCamera(lights, camera);

			new Vector3f(player.getPosition().x, player.getPosition().y
					+ player.getHeight() / 1.5f, player.getPosition().z);

			boundsCheck(player, Xmax, Xmin, Zmax, Zmin);
			player.move(terrain1);
			for (AnimatedEntity e : AnimatedEntities) {
				e.update();
			}
			beamer.update();
			camera.move();
			dayCycle(sun);
			ParticleMaster.update(camera);
			renderer.renderShadowMap(allENTITIES, sun);

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			// render reflection texture
			renderer.loadRefractRender(false);
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - WaterHeight);
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.deactivateUnderwaterFog();
			renderer.renderScene(selectedEntity, AnimatedEntities, entities,
					normalEntities, terrain, lights, camera, new Vector4f(0, 1,
							0, -WaterHeight), beamer);
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			fbos.unbindCurrentFrameBuffer();
			
			
			// render refraction texture
			renderer.loadRefractRender(true);
			GL13.glActiveTexture(GL13.GL_TEXTURE8);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,
					fbos.getRefractionDepthTexture());
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(
					selectedEntity,
					AnimatedEntities,
					entities,
					normalEntities,
					terrain,
					lights,
					camera,
					camera.getPosition().y < MainGameLoop.WaterHeight ? new Vector4f(
							0, 1, 0, -WaterHeight) : new Vector4f(0, -1, 0,
							WaterHeight), beamer);
			ParticleMaster.renderParticles(camera);
			fbos.unbindCurrentFrameBuffer();
			
			// render to screen
			renderer.loadRefractRender(false);
			GL13.glActiveTexture(GL13.GL_TEXTURE8);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,
					fbos.getRefractionDepthTexture());
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(selectedEntity, AnimatedEntities, entities,
					normalEntities, terrain, lights, camera, new Vector4f(0,
							-1, 0, 10000000), beamer);
			MasterRenderer.disableCulling();
			waterRenderer.render(waters, camera, sun);
			MasterRenderer.enableCulling();
			ParticleMaster.renderParticles(camera);
			multisampleFbo.bindFrameBuffer();
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(),
					outputFbo2.getColourTexture());

			// ------------------------------------

			mpRay.update();
			mpRenderer.render(renderer.getAllRenderedEntities());
			Objectselector
					.dealWithSelectedEntities(mpRenderer, mpRay, terrain1);

			guiRenderer.render(guis);

			postext.remove();
			fpstext.remove();

			fpstext = new GUIText("Frames: "
					+ (int) (1 / DisplayManager.getFrameTimeSeconds()) + "   "
					+ selectedEntity + "     hover:" + MouseOverEntity, 1,
					font, new Vector2f(0f, 0.03f), 1f, false);
			postext = new GUIText("Position:   x " + player.getPosition().x
					+ "   z " + player.getPosition().z + "   y "
					+ player.getPosition().y + "   r " + player.getRotY(), 1,
					font, new Vector2f(0f, 0f), 1f, false);
			postext.setColour(Color.RED.getRed(), Color.RED.getGreen(),
					Color.RED.getBlue());
			fpstext.setColour(Color.RED.getRed(), Color.RED.getGreen(),
					Color.RED.getBlue());
			TextMaster.render();
			DisplayManager.updateDisplay();

		}

		// -------------------------end of loop-----------------------------
		multisampleFbo.cleanUp();
		outputFbo.cleanUp();
		outputFbo.cleanUp();
		PostProcessing.cleanUp();
		ParticleMaster.cleanUp();
		guiRenderer.cleanUp();
		fbos.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		TextMaster.cleanUp();
		DisplayManager.closeDisplay();

	}

	// ----------------------methods----------------------------------------

	private static void setCelShading(boolean celShading) {
		TerrainShader ts = MasterRenderer.getTerrainShader();
		ts.start();
		ts.setCelShading(celShading);
		ts.stop();
		StaticShader ss = MasterRenderer.getShader();
		ss.start();
		ss.setCelShading(celShading);
		ss.stop();
		NormalMappingShader nms = MasterRenderer.getNormalMapRenderer()
				.getShader();
		nms.start();
		nms.setCelShading(celShading);
		nms.stop();
	}

	public static void genEntityPos(Terrain terrain) {
		Random random = new Random();
		float xDif = Xmax - Xmin;
		float zDif = Zmax - Zmin;
		boolean gh = true;
		do {
			x = (random.nextFloat() * xDif) - (Xmin * -1);
			z = (random.nextFloat() * zDif) - (Zmin * -1);
			y = terrain.getHeightOfTerrain(x, z);
			if (x > Xmin + 10 && x < Xmax - 10 && z > Zmin + 10
					&& z < Zmax - 10 && y > WaterHeight + 0.3) {
				gh = false;
			}
		} while (gh);

	}

	public static void genEntityPos(Terrain terrain, float x, float z) {
		y = terrain.getHeightOfTerrain(x, z);

	}

	// private static Terrain getActiveTerrain(HashMap<String, Terrain>
	// terrains,
	// float x, float z) {
	// int xGrid = (int) (x / Terrain.SIZE);
	// int zGrid = (int) (z / Terrain.SIZE);
	//
	// if (x < 0) {
	// if (x > -1) {
	// xGrid += 1;
	// xGrid *= -1;
	// } else {
	// xGrid -= 1;
	// }
	// }
	// if (z < 0) {
	// if (z > -1) {
	// zGrid += 1;
	// zGrid *= -1;
	// } else {
	// zGrid -= 1;
	// }
	// }
	//
	// return terrains.getOrDefault(xGrid + "," + zGrid, terrains.get("0,0"));
	//
	// }

	private static void boundsCheck(Entity player, float xmax, float xmin,
			float zmax, float zmin) {
		Vector3f pos = player.getPosition();
		if (pos.x > xmax) {
			pos.x = xmax;

		} else if (pos.x < xmin) {
			pos.x = xmin;
		} else if (pos.z > zmax) {
			pos.z = zmax;
		} else if (pos.z < zmin) {
			pos.z = zmin;
		}

		player.setPosition(pos);
	}

	private static void dayCycle(Light sun) {
		SkyboxRenderer.setSpeed(10 * 10);
		if (SkyboxRenderer.time >= 0 && SkyboxRenderer.time < 5000) {
			sun.setColour(new Vector3f(0.3f, 0.3f, 0.3f));
			MasterRenderer.RED = 0.01f;
			MasterRenderer.GREEN = 0.01f;
			MasterRenderer.BLUE = 0.01f;
		} else if (SkyboxRenderer.time >= 5000 && SkyboxRenderer.time < 8000) {
			sun.increaseColor(new Vector3f(0.0001f, 0.0001f, 0.0001f));
			MasterRenderer.RED += 0.00157f;
			MasterRenderer.GREEN += 0.00157f;
			MasterRenderer.BLUE += 0.0018f;
		} else if (SkyboxRenderer.time >= 8000 && SkyboxRenderer.time < 21000) {
			sun.setColour(new Vector3f(1f, 1f, 1f));
			MasterRenderer.RED = 0.5444f;
			MasterRenderer.GREEN = 0.62f;
			MasterRenderer.BLUE = 0.69f;
		} else {
			sun.decreaseColor(new Vector3f(0.0001f, 0.0001f, 0.0001f));
			MasterRenderer.RED -= 0.002f;
			MasterRenderer.GREEN -= 0.002f;
			MasterRenderer.BLUE -= 0.002f;
		}
	}

	private static void sortLightsByDistanceToCamera(List<Light> lights,
			Camera cam) {
		for (Light light : lights) {
			light.updateDistanceToCamera(cam);
		}

		for (int i = 1; i < lights.size(); i++) {
			for (int j = 0; j < lights.size() - i; j++) {
				if (lights.get(j).getDistanceToCamera() > lights.get(j + 1)
						.getDistanceToCamera())
					swap(lights, j, j + 1);
			}
		}

	}

	private static void swap(List<Light> lights, int pos1, int pos2) {
		Light c = lights.get(pos1);
		lights.set(pos1, lights.get(pos2));
		lights.set(pos2, c);
	}

}
