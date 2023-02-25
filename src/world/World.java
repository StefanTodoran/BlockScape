package world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import renderEngine.Loader;
import toolbox.PerlinNoiseGenerator;

public class World {

	private Map<Position, Chunk> chunks;
	private int renderDistance = 12;

	private PerlinNoiseGenerator rng;
	private Random random;
	private long seed;
	
	private int groundHeight = 24;
	private int worldSize = 32;
	
	public World(Loader loader, long seed) {
		chunks = new HashMap<Position, Chunk>();
		this.seed = seed;
		random = new Random(seed);
		rng = new PerlinNoiseGenerator(seed);
		
		for (int cx = 0; cx < worldSize; cx++) {
			for (int cy = 0; cy < 3; cy++) {
				for (int cz = 0; cz < worldSize; cz++) {						
				Map<Position, Block> blocks = new HashMap<Position, Block>();
				Position chunkPos = new Position(cx - (worldSize / 2), cy, cz - (worldSize / 2));
				
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int z = 0; z < Chunk.SIZE; z++) {
						double fy = rng.noise(cx*Chunk.SIZE + x, cz*Chunk.SIZE + z) * Chunk.SIZE + groundHeight;
						int y = Math.max(0, Math.min(48, (int) fy));

						int chunkBottom = cy * Chunk.SIZE;
						int height = y - chunkBottom;
						
						if (height < Chunk.SIZE && height >= 0) {
							blocks.put(new Position(x, height, z), new Block("grass_block"));
							
							int soilAmnt = (int) (rng.noise(cx*Chunk.SIZE + x, cz*Chunk.SIZE + z) * 5 + 2);
							for (int dy = height - 1; dy >= 0; dy--) {
								if (dy > height - soilAmnt) {									
									blocks.put(new Position(x, dy, z), new Block("soil_block"));
								} else {									
									blocks.put(new Position(x, dy, z), new Block("stone_block"));
								}
							}
						} else if (height >= Chunk.SIZE) {
							
							for (int dy = 0; dy < Chunk.SIZE; dy++) {								
								blocks.put(new Position(x, dy, z), new Block("stone_block"));
							}
						}
					}
				}
				
				int trees = (int) (rng.noise(cx, cy, cz) * 5) + 3;
				for (int i = 0; i < trees; i++) {
					int x = (int) (random.nextFloat() * Chunk.SIZE);
					int z = (int) (random.nextFloat() * Chunk.SIZE);
					
					double fy = rng.noise(cx*Chunk.SIZE + x, cz*Chunk.SIZE + z) * Chunk.SIZE + groundHeight;
					int y = Math.max(0, Math.min(48, (int) fy));

					int chunkBottom = cy * Chunk.SIZE;
					int height = y - chunkBottom;
					
					if (height < Chunk.SIZE && height >= 0) {
						blocks.put(new Position(x, height, z), new Block("soil_block"));
						
						int trunkHeight = 6 + (int) (rng.noise(cx, cy, cz) * 2);
						for (int dy = 1; dy < trunkHeight; dy++) {							
							blocks.put(new Position(x, height+dy, z), new Block("oak_log"));
							
							int leavesWidth = (dy > trunkHeight - 2 || dy < 2) ? 1 : 2;
							for (int dx = -leavesWidth; dx <= leavesWidth; dx++) {
								for (int dz = -leavesWidth; dz <= leavesWidth; dz++) {
									if (dx != 0 || dz != 0 || dy > trunkHeight - 3)			
										blocks.put(new Position(x+dx, height+dy+2, z+dz), new Block("oak_leaves"));
								}
							}
						}
					}
				}

				
				Chunk chunk = new Chunk(new HashMap<Position, Block>(), chunkPos.toVector());
				chunk.setAllBlocks(blocks);
				chunks.put(chunkPos, chunk);
				chunk.updateMesh(loader);
				}
			}
		}
	}

	public Map<Position, Chunk> getChunksAround(Position center) {
		Map<Position, Chunk> nearby = new HashMap<Position, Chunk>();
		
		int dist = renderDistance;
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
