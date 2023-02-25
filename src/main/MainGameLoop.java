package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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

	// Expects display to be created already.
	public static void runGame(Loader loader, GUIRenderer guiRenderer) {
		MasterRenderer renderer = new MasterRenderer();
		
		// ----------- \\
		// WORLD SETUP \\
		
		List<GUIElement> gui = FontEngine.guiFromString("Building World...", new Vector2f(0, 0), 3, true);
		guiRenderer.clearScreen();
		guiRenderer.render(gui);
		DisplayManager.updateDisplay();
		
		World world = new World(loader, 123456789L);
		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Camera camera = new Camera(new Vector3f(0, 20, 0), new Vector3f(0, 0, 0));
		Reticle reticle = new Reticle();
		reticle.loadReticle(loader);
		
		// -------------- \\
		// MAIN GAME LOOP \\
		
		int frames = 0;
		float time = 0;
		List<GUIElement> fps = new ArrayList<>();
		List<GUIElement> debug = new ArrayList<>();
		
		float displayWidth = FontEngine.getDisplayWidth(2);
		float displayHeight = FontEngine.getDisplayHeight(2);
		Vector2f fpsPos = new Vector2f(-1 + 2 * displayWidth, 1 - 2 * displayHeight);
		Vector2f debugPos = new Vector2f(-1 + 2 * displayWidth, 1 - 4 * displayHeight);
		
		Mouse.setGrabbed(true);
		float lastTick = 0;
		boolean closeRequested = false;
		while (!closeRequested && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			// GAME LOGIC
			float delta = DisplayManager.getFrameTimeMS();
			lastTick += delta;
			
			time += delta;
			frames += 1;
			if (frames >= 3) {
				int estimate = (int) (1000 / time) * frames;
				
				fps = FontEngine.guiFromString(estimate+" FPS", fpsPos, 2, false);
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
			debug = FontEngine.guiFromString(FontEngine.formatVectorForDisplay(camPos), debugPos, 2, false);
			
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
			
			DisplayManager.updateDisplay();
			closeRequested = Display.isCloseRequested();
		}
		if (closeRequested) {
			renderer.cleanUp();
			MainAppHandler.cleanUp();
		}
		Mouse.setGrabbed(false);
	}

}
