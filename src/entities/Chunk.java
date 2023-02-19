package entities;

import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.Loader;

public class Chunk {

	private static final int SIZE = 16;
	
	// String of the form "x,y,z" specified as integers.
	private Map<String, Block> blocks;
	private boolean[][][] occupied;
	private TexturedModel model;
	private Vector3f position;
	
	public Chunk(Loader loader, Map<String, Block> blocks) {
		this.blocks = blocks;
		
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					String pos = String.format("%d%d%d", x, y, z);
					Block block = blocks.get(pos);
					
					occupied[x][y][z] = block != null;
					if (occupied[x][y][z]) {
						//
					} else {
						//
					}
				}
			}
		}
		
		
	}
}
