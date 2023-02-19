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
		
		List<Block> blocks = new ArrayList<Block>();
		
		blocks.add(new Block(loader, "grass_block", new Vector3f(2, 0, 5), 0, 0, 0, 1));
		blocks.add(new Block(loader, "clay_block", new Vector3f(0, 0, 5), 0, 0, 0, 1));
		
		Block gold = new Block(loader, "gold_block", new Vector3f(-2, 0, 5), 0, 0, 0, 1);
		gold.getEntity().getModel().getTexture().setShineDamper(5);
		gold.getEntity().getModel().getTexture().setReflectivity(0.5f);
		blocks.add(gold);
		
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		data.put("1,1,1", true);
		data.put("2,3,4", true);
		Chunk chunk = new Chunk(loader, data, new Vector3f(0, 0, 10));
		
		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Camera camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), -225);
		
		boolean closeRequested = false;
		while (!closeRequested) {
			
			// GAME LOGIC
			camera.update();
			light.setPosition(camera.getPosition());
			
			// RENDER STEP
			for (Block block : blocks) {				
				renderer.processEntity(block);
			}
			renderer.processChunk(chunk);
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
