package main;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.PerlinNoise;
import world.Block;
import world.Camera;
import world.Chunk;
import world.Light;
import world.Position;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();
		
		Map<Position, Chunk> chunks = new HashMap<Position, Chunk>();
		int genX = 8; int genY = 4; int genZ = 8;
		float[][] perlinNoise = PerlinNoise.generatePerlinNoise(genX * Chunk.SIZE, genZ * Chunk.SIZE, 6, 0.3f, 123456789L);
		
		for (int cx = 0; cx < genX; cx++) {
			for (int cz = 0; cz < genZ; cz++) {
														
				Map<Position, Block> blocks = new HashMap<Position, Block>();
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int z = 0; z < Chunk.SIZE; z++) {
						int y = (int) (perlinNoise[cx*Chunk.SIZE + x][cz*Chunk.SIZE + z] * 16);
						Position pos = new Position(x, y, z);

						blocks.put(pos, new Block("grass_block"));
						for (y = y - 1; y >= 0; y--) {
							pos = new Position(x, y, z);
							
							blocks.put(pos, new Block("soil_block"));
						}
						
					}
				}
				
				
				Position chunkPos = new Position(cx*Chunk.SIZE, 0, cz*Chunk.SIZE);
				chunks.put(chunkPos, new Chunk(loader, blocks, chunkPos.toVector()));
			}
		}

		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Camera camera = new Camera(new Vector3f(0, 20, 0), new Vector3f(0, 0, 0), 135);
		
		boolean closeRequested = false;
		while (!closeRequested) {
			
			// GAME LOGIC
			camera.update();
			light.setPosition(camera.getPosition());
			
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
