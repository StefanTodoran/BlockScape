package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Block;
import entities.Camera;
import entities.Chunk;
import entities.Light;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();
		
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		Map<String, Block> data = new HashMap<String, Block>();
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					String pos = String.format("%d,%d,%d", x, y, z);
					if (y == 15) {						
						data.put(pos, new Block("grass_block"));
					} else {						
						data.put(pos, new Block("soil_block"));
					}
				}
			}
		}
		chunks.add(new Chunk(loader, data, new Vector3f(0, 0, -16)));
		
		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Camera camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		
		boolean closeRequested = false;
		while (!closeRequested) {
			
			// GAME LOGIC
			camera.update();
			light.setPosition(camera.getPosition());
			
			// RENDER STEP
			for (Chunk chunk : chunks) {				
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
