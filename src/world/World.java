package world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import renderEngine.Loader;
import toolbox.PerlinNoise;

public class World {

	private Map<Position, Chunk> chunks;
	private long seed;
	private int renderDistance = 16;
	
	public World(Loader loader, long seed) {
		chunks = new HashMap<Position, Chunk>();
		Random random = new Random(seed);
		float[][] perlinNoise = PerlinNoise.generatePerlinNoise(32*Chunk.SIZE, 32*Chunk.SIZE, 6, 0.3f, seed);
		
		for (int cx = 0; cx < 32; cx++) {
			for (int cz = 0; cz < 32; cz++) {														

				Map<Position, Block> blocks = new HashMap<Position, Block>();
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int z = 0; z < Chunk.SIZE; z++) {
						float fy = perlinNoise[cx*Chunk.SIZE + x][cz*Chunk.SIZE + z] * Chunk.SIZE;
						int y = (int) fy;

						blocks.put(new Position(x, y, z), new Block("grass_block"));
//						if (fy - y < 0.4 && fy - y > 0.2) {
//							blocks.put(new Position(x, y + 1, z), new Block("grass"));
//						}
						
						for (y = y - 1; y >= 0; y--) {
							blocks.put(new Position(x, y, z), new Block("stone_block"));
						}
						
					}
				}
				
				for (int i = 0; i < random.nextFloat() * 3; i++) {
					int x = (int) (random.nextFloat() * Chunk.SIZE);
					int z = (int) (random.nextFloat() * Chunk.SIZE);
					int y = (int) (perlinNoise[cx*Chunk.SIZE + x][cz*Chunk.SIZE + z] * Chunk.SIZE);
					
					Position pos = new Position(x, y, z);
					blocks.put(pos, new Block("soil_block"));
					
					for (int dy = y + 1; dy < Math.min(y + 6, Chunk.SIZE); dy++) {
						int c = Math.min(2, dy - y - 2);
						for (int dx = -c + 1; dx < c; dx++) {
							for (int dz = -c + 1; dz < c; dz++) {
								pos = new Position(x+dx, dy, z+dz);
								blocks.put(pos, new Block("oak_leaves"));
							}
						}

						if (dy + 1 < Math.min(y + 6, Chunk.SIZE)) {
							pos = new Position(x, dy, z);
							blocks.put(pos, new Block("oak_log"));
						}
					}
				}
				
				Position chunkPos = new Position((cx - 16), 0, (cz - 16));
				Chunk chunk = new Chunk(new HashMap<Position, Block>(), chunkPos.toVector());
				chunk.setAllBlocks(blocks);
				chunks.put(chunkPos, chunk);
				chunk.updateMesh(loader);
			}
		}
	}

	public Map<Position, Chunk> getChunksAround(Position center) {
		Map<Position, Chunk> nearby = new HashMap<Position, Chunk>();
		
		int dist = renderDistance / 2;
		for (int x = -dist; x < dist; x++) {
			for (int y = -dist; y < dist; y++) {
				for (int z = -dist; z < dist; z++) {
					Position pos = new Position(x , y, z);
					Position adj = Chunk.worldPosToChunkCoords(center);
					pos.add(adj);
					
					Chunk chunk = chunks.get(pos);
					if (chunk != null) {			
						nearby.put(pos, chunk);
					}
				}
			}
		}
		
		return nearby;
	}
	
	public Chunk setBlock(Position blockPos, String blockType) {
		Position chunkPos = Chunk.worldPosToChunkCoords(blockPos);
		
		Chunk chunk = chunks.get(chunkPos);
		if (chunk != null) {
			blockPos = Chunk.worldPosToLocalCoords(blockPos);
			Block block = new Block(blockType);
			chunk.setBlock(blockPos, block);		
			return chunk;
		} else {
			return null;
		}
	}

	public int getRenderDistance() {
		return renderDistance;
	}
}
