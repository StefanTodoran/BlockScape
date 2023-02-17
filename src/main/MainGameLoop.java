package main;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Block;
import entities.Camera;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
		List<Block> blocks = new ArrayList<Block>();
		blocks.add(new Block(loader, "grass_block", new Vector3f(-1, 0, -5), 0, 0, 0, 1));
		blocks.add(new Block(loader, "clay_block", new Vector3f(1, 0, -5), 0, 0, 0, 1));
		
		Camera camera = new Camera();
		
		boolean closeRequested = false;
		while (!closeRequested) {
			
			// TODO: GAME LOGIC HERE
			camera.update();
			
			renderer.prepare();
			shader.start();
			shader.loadViewMatrix(camera);
			for (Block block:blocks) {				
				renderer.render(block, shader);
			}
			shader.stop();
			
			// isCloseRequested handles the X button
			// updateDisplay checks for escape keypress
			closeRequested = DisplayManager.updateDisplay() || Display.isCloseRequested();
		}
		
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
