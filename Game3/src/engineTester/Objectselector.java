package engineTester;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import perPixelMousepick.MousePickRenderer;
import terrains.Terrain;
import toolbox.MousePicker;

public class Objectselector {
	static boolean leftMousePressed = false;
	static boolean toggle = false;

	protected static boolean mouseLeftHoldCheck() {
		if (Mouse.isButtonDown(0)) {
			if (!Mouse.getEventButtonState()) {
				
				leftMousePressed = true;
				return true;
			}
		} else if (leftMousePressed) {
			
			leftMousePressed = false;
		}
		return false;
	}

	public static void dealWithSelectedEntities(MousePickRenderer mp,
			MousePicker mpray, Terrain terrain) {
		Vector3f Color = mp.getColorForMouse();
		Vector3f ray = mpray.getCurrentTerrainPoint();

		MainGameLoop.MouseOverEntity = mp.getEntityByColor(Color);
		mouseLeftHoldCheck();
		if (leftMousePressed) {
			if (!toggle) {
				MainGameLoop.selectedEntity = MainGameLoop.MouseOverEntity;
				toggle = true;
				
			}
		} else {
			MainGameLoop.selectedEntity = null;
			toggle = false;
		}

		if (ray != null) {
			if (MainGameLoop.selectedEntity != null) {
				MainGameLoop.selectedEntity.setPosition(new Vector3f(ray.x,
						terrain.getHeightOfTerrain(ray.x, ray.z), ray.z));
			}
		}
	}

}
