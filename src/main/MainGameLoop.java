package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.GUIElement;
import models.Reticle;
import renderEngine.DisplayManager;
import renderEngine.FontEngine;
import renderEngine.GUIRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import world.Camera;
import world.Chunk;
import world.Light;
import world.Position;
import world.World;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();
		
		Reticle reticle = new Reticle();
		reticle.loadReticle(loader);
		
		FontEngine.loadFont(loader);
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		List<GUIElement> gui = FontEngine.buildElementsFromString("Building World...", new Vector2f(0, 0), 3, true);

		guiRenderer.render(gui);
		DisplayManager.updateDisplay();
		
		World world = new World(loader, 123456789L);
		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Camera camera = new Camera(new Vector3f(0, 20, 0), new Vector3f(0, 0, 0));
		
		int frames = 0;
		float time = 0;
		List<GUIElement> fps = new ArrayList<>();
		List<GUIElement> debug = new ArrayList<>();
		
		float displayWidth = FontEngine.getDisplayWidth(2);
		float displayHeight = FontEngine.getDisplayHeight(2);
		Vector2f fpsPos = new Vector2f(-1 + 2 * displayWidth, 1 - 2 * displayHeight);
		Vector2f debugPos = new Vector2f(-1 + 2 * displayWidth, 1 - 4 * displayHeight);
		
		float lastTick = 0;
		boolean closeRequested = false;
		while (!closeRequested) {
			
			// GAME LOGIC
			float delta = DisplayManager.getFrameTimeMS();
			lastTick += delta;
			
			time += delta;
			frames += 1;
			if (time >= 1000) {
				fps = FontEngine.buildElementsFromString(frames+" FPS", fpsPos, 2, false);
				frames = 0;
				time = 0;
			}
						
			int action = Camera.NO_ACTION;
			while (lastTick > 100) {
				lastTick -= 10;

				int action_ = camera.doUpdateGetActions();
				action = action_ != Camera.NO_ACTION ? action_ : action;
			}
			
			Vector3f camPos = camera.getPosition();
			light.setPosition(camPos);
			debug = FontEngine.buildElementsFromString(FontEngine.formatVectorForDisplay(camPos), debugPos, 2, false);
			
			Chunk updated = null;
			if (action == Camera.LEFT_CLICK) {
				updated = world.setBlock(new Position(camPos), "gold_block");
			}
			if (updated != null) {
				updated.updateMesh(loader);
			}
			
			Map<Position, Chunk> chunks = world.getChunksAround(new Position(camPos));
			
			// RENDER STEP
			for (Position position : chunks.keySet()) {
				Chunk chunk = chunks.get(position);
				renderer.processChunk(chunk);
			}
			renderer.render(light, camera);
			
			gui = new ArrayList<>();
			gui.addAll(fps);
			gui.addAll(debug);
			
			guiRenderer.render(gui);
			guiRenderer.renderReticle();
			
			// isCloseRequested handles the X button
			// updateDisplay checks for escape keypress
			closeRequested = DisplayManager.updateDisplay() || Display.isCloseRequested();
		}
		
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
