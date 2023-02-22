package main;

import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import models.Reticle;
import renderEngine.DisplayManager;
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

		World world = new World(loader, 123456789L);
		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Camera camera = new Camera(new Vector3f(0, 20, 0), new Vector3f(0, 0, 0));
		
		float lastTick = 0;
		boolean closeRequested = false;
		while (!closeRequested) {
			
			// GAME LOGIC
			float delta = DisplayManager.getFrameTimeMS();
			lastTick += delta;
			
			int action = Camera.NO_ACTION;
			while (lastTick > 100) {
				lastTick -= 10;

				int action_ = camera.doUpdateGetActions();
				action = action_ != Camera.NO_ACTION ? action_ : action;
			}
			
			Vector3f camPos = camera.getPosition();
			light.setPosition(camPos);
			
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
			
			// isCloseRequested handles the X button
			// updateDisplay checks for escape keypress
			closeRequested = DisplayManager.updateDisplay() || Display.isCloseRequested();
		}
		
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
