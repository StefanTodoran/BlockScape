package world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import renderEngine.Loader;
import toolbox.PerlinNoise;

public class World {

	private Map<Position, Chunk> chunks;
	private long seed;
	private int renderDistance = 4;
	
	public World(Loader loader, long seed) {
		chunks = new HashMap<Position, Chunk>();
		Random random = new Random(seed);
		float[][] perlinNoise = PerlinNoise.generatePerlinNoise(16*Chunk.SIZE, 16*Chunk.SIZE, 6, 0.3f, seed);
		
		for (int cx = 0; cx < 16; cx++) {
			for (int cz = 0; cz < 16; cz++) {														

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
				
				for (int i = 0; i < random.nextFloat() * 3; i++) {
					int x = (int) (random.nextFloat() * Chunk.SIZE);
					int z = (int) (random.nextFloat() * Chunk.SIZE);
					int y = (int) (perlinNoise[cx*Chunk.SIZE + x][cz*Chunk.SIZE + z] * 16);
					
					Position pos = new Position(x, y, z);
					blocks.put(pos, new Block("soil_block"));
					
					for (int dy = y + 1; dy < Math.min(y + 5, Chunk.SIZE); dy++) {						
						pos = new Position(x, dy, z);
						blocks.put(pos, new Block("oak_log"));
					}
				}
				
				Position chunkPos = new Position((cx - 8)*Chunk.SIZE, 0, (cz - 8)*Chunk.SIZE);
				Chunk chunk = new Chunk(new HashMap<Position, Block>(), chunkPos.toVector());
				chunk.setBlocks(blocks);
				chunks.put(chunkPos, chunk);
				chunk.updateMesh(loader);
			}
		}
	}

	public Map<Position, Chunk> getChunksAround(Position center) {
		Map<Position, Chunk> nearby = new HashMap<Position, Chunk>();
		for (Position pos : chunks.keySet()) {
			if (pos.withinDistance(center, renderDistance * Chunk.SIZE)) {
				nearby.put(pos, chunks.get(pos));
			}
		}
		return nearby;
	}

	public int getRenderDistance() {
		return renderDistance;
	}
}
