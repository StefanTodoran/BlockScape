package world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.Loader;
import toolbox.PerlinNoiseGenerator;

public class World {

	private Map<Position, Chunk> chunks;
	private int renderDistance = 12;

	private PerlinNoiseGenerator rng;
	private Random random;
	private long seed;
	
	private int groundHeight = 20;
	private int heightVariance = 16;
	private int heightLimit = 32;
	private int worldSize = 32;
	
	public World(Loader loader) {
		chunks = new HashMap<Position, Chunk>();
	}
	
	public World(Loader loader, long seed) {
		chunks = new HashMap<Position, Chunk>();
		this.seed = seed;
		random = new Random(seed);
		rng = new PerlinNoiseGenerator(seed);
		
		for (int cx = 0; cx < worldSize; cx++) {
			for (int cy = 0; cy < 3; cy++) {
				for (int cz = 0; cz < worldSize; cz++) {						
					Position chunkPos = new Position(cx - (worldSize / 2), cy, cz - (worldSize / 2));
					Map<Position, Block> blocks = new HashMap<Position, Block>();
					generateChunkTerrain(blocks, chunkPos);
					
					Chunk chunk = new Chunk(new HashMap<Position, Block>(), chunkPos.toVector());
					chunk.setAllBlocks(blocks);
					chunks.put(chunkPos, chunk);
					chunk.updateMesh(loader);
				}
			}
		}
	}
	
	public void generateChunkTerrain(Map<Position, Block> blocks, Position cp) {
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int height = sampleHeight(cp.x*Chunk.SIZE + x, cp.z*Chunk.SIZE + z);

				int chunkBottom = cp.y * Chunk.SIZE;
				int y = height - chunkBottom;
				
				if (y < Chunk.SIZE && y >= 0) {
					blocks.put(new Position(x, y, z), new Block("grass_block"));
					
					int soilAmnt = (int) (random.nextFloat() * 3);
					for (int dy = y - 1; dy >= 0; dy--) {
						if (dy > y - soilAmnt) {									
							blocks.put(new Position(x, dy, z), new Block("soil_block"));
						} else {									
							blocks.put(new Position(x, dy, z), new Block("stone_block"));
						}
					}
				} else if (y >= Chunk.SIZE) {
					// Fully underground. Do underground generation.
					
					for (int dy = 0; dy < Chunk.SIZE; dy++) {								
						blocks.put(new Position(x, dy, z), new Block("stone_block"));
					}
				}
			}
		}
		
		int trees = (int) (rng.noise(cp.x, cp.y, cp.z) * 5) + 1;
		for (int i = 0; i < trees; i++) {
			int x = (int) (random.nextFloat() * Chunk.SIZE);
			int z = (int) (random.nextFloat() * Chunk.SIZE);
			
			int height = sampleHeight(cp.x*Chunk.SIZE + x, cp.z*Chunk.SIZE + z);
			int chunkBottom = cp.y * Chunk.SIZE;
			int y = height - chunkBottom;
			
			if (y < Chunk.SIZE && y >= 0) {
				blocks.put(new Position(x, y, z), new Block("soil_block"));
				
				int trunkHeight = 4 + (int) (random.nextFloat() * 3);
				for (int dy = 1; dy < trunkHeight; dy++) {				
					blocks.put(new Position(x, y+dy, z), new Block("oak_log"));
					
					int leavesWidth = (dy > trunkHeight - 2 || dy < 2) ? 1 : 2;
					for (int dx = -leavesWidth; dx <= leavesWidth; dx++) {
						for (int dz = -leavesWidth; dz <= leavesWidth; dz++) {
							if (dx != 0 || dz != 0 || dy > trunkHeight - 3)			
								blocks.put(new Position(x+dx, y+dy+2, z+dz), new Block("oak_leaves"));
						}
					}
				}
			}
		}
	}
	
	private int sampleHeight(int x, int z) {
		double y = rng.noise(x, z) * heightVariance + groundHeight;
		return Math.max(0, Math.min(heightLimit, (int) y));
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
			Block block = (blockType != null) ? new Block(blockType) : null;
			chunk.setBlock(blockPos, block);		
			return chunk;
		} else {
			return null;
		}
	}
	
	public Position doRaycast(Vector3f playerPos, float yaw, float pitch, int distLimit) {
	    double ex = playerPos.x + Math.sin(Math.toRadians(yaw)) * distLimit;
	    double ey = playerPos.y - Math.cos(Math.toRadians(pitch)) * distLimit;
	    double ez = playerPos.z - Math.cos(Math.toRadians(yaw)) * distLimit;

	    double dx = ex - playerPos.x;
	    double dy = ey - playerPos.y;
	    double dz = ez - playerPos.z;
	    
	    double stepXZ;
	    if (Math.abs(dx) >= Math.abs(dz)) {
	    	stepXZ = Math.abs(dx);
	    } else {
	    	stepXZ = Math.abs(dz);
	    }
	    
	    // Whichever of these is larger will just become 1, since it is divided by itself.
	    dx = dx / stepXZ;
	    dy = 0;
	    dz = dz / stepXZ;
	    

	    double fx = playerPos.x;
	    double fy = playerPos.y;
	    double fz = playerPos.z;

	    System.out.printf("\nSTART: x:%f, y:%f, z:%f\n", fx, fy, fz);
	    System.out.printf("DIRECTION: x:%f, y:%f, z:%f\n", dx, dy, dz);
	    
	    int x, y, z;
	    int numSteps = 0;
	    while (numSteps < distLimit) {
	    	fx += dx;
	    	fy += dy;
	    	fz += dz;
	    	
	    	x = (int) fx;
			y = (int) fy;
			z = (int) fz;
	    	
	    	Position worldPos = new Position(x, y, z);
			Position chunkPos = Chunk.worldPosToChunkCoords(worldPos);
			Position internalPos = Chunk.worldPosToLocalCoords(worldPos);
			
			System.out.printf("x:%d, y:%d, z:%d\n", x, y, z);
			Chunk chunk = chunks.get(chunkPos);
			if (chunk == null) return null; // Our ray has exited the world!

			assert (chunk.getBlock(chunkPos) != null) == chunk.getOccupied()[internalPos.x][internalPos.y][internalPos.z];
			if (chunk.getOccupied()[internalPos.x][internalPos.y][internalPos.z]) {
				return worldPos;
			}
			
			numSteps += 1;
	    }
	    
	    return null;
	}

	// https://stackoverflow.com/questions/55263298/draw-all-voxels-that-pass-through-a-3d-line-in-3d-voxel-space
//	public Position doRaycast(Vector3f playerPos, float yaw, float pitch, int distLimit) {
//	    int startX = (int) playerPos.x;
//	    int startY = (int) playerPos.y;
//	    int startZ = (int) playerPos.z;
//		
//		int endX = (int) (playerPos.x + Math.sin(Math.toRadians(yaw)) * distLimit);
//	    int endY = (int) (playerPos.y + Math.cos(Math.toRadians(pitch)) * distLimit);
//	    int endZ = (int) (playerPos.z - Math.cos(Math.toRadians(yaw)) * distLimit);
//		
//		int dx = Math.abs(endX - startX);
//		int dy = Math.abs(endY - startY);
//		int dz = Math.abs(endZ - startZ);
//		
//		int stepX = startX < endX ? 1 : -1;
//		int stepY = startY < endY ? 1 : -1;
//		int stepZ = startZ < endZ ? 1 : -1;
//		
//		System.out.printf("dx:%d, dy:%d, dz:%d\n", dx, dy, dz);
//		System.out.printf("sx:%d, sy:%d, sz:%d\n", stepX, stepY, stepZ);
//		
//		double hypotenuse = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
//		
//		double tMaxX = hypotenuse * 0.5 / dx;
//		double tMaxY = hypotenuse * 0.5 / dy;
//		double tMaxZ = hypotenuse * 0.5 / dz;
//		
//		double tDeltaX = hypotenuse / dx;
//		double tDeltaY = hypotenuse / dy;
//		double tDeltaZ = hypotenuse / dz;
//		
//		int numSteps = 0;
//		while (numSteps < distLimit){
//		    if (tMaxX < tMaxY) {
//		        if (tMaxX < tMaxZ) {
//		            startX = startX + stepX;
//		            tMaxX = tMaxX + tDeltaX;
//		        }
//		        else if (tMaxX > tMaxZ){
//		            startZ = startZ + stepZ;
//		            tMaxZ = tMaxZ + tDeltaZ;
//		        }
//		        else {
//		            startX = startX + stepX;
//		            tMaxX = tMaxX + tDeltaX;
//		            startZ = startZ + stepZ;
//		            tMaxZ = tMaxZ + tDeltaZ;
//		        }
//		    }
//		    else if (tMaxX > tMaxY){
//		        if (tMaxY < tMaxZ) {
//		            startY = startY + stepY;
//		            tMaxY = tMaxY + tDeltaY;
//		        }
//		        else if (tMaxY > tMaxZ){
//		            startZ = startZ + stepZ;
//		            tMaxZ = tMaxZ + tDeltaZ;
//		        }
//		        else {
//		            startY = startY + stepY;
//		            tMaxY = tMaxY + tDeltaY;
//		            startZ = startZ + stepZ;
//		            tMaxZ = tMaxZ + tDeltaZ;
//
//		        }
//		    } 
//		    else {
//		        if (tMaxY < tMaxZ) {
//		            startY = startY + stepY;
//		            tMaxY = tMaxY + tDeltaY;
//		            startX = startX + stepX;
//		            tMaxX = tMaxX + tDeltaX;
//		        }
//		        else if (tMaxY > tMaxZ){
//		            startZ = startZ + stepZ;
//		            tMaxZ = tMaxZ + tDeltaZ;
//		        }
//		        else {
//		            startX = startX + stepX;
//		            tMaxX = tMaxX + tDeltaX;
//		            startY = startY + stepY;
//		            tMaxY = tMaxY + tDeltaY;
//		            startZ = startZ + stepZ;
//		            tMaxZ = tMaxZ + tDeltaZ;
//
//		        }
//		    }
//		    
//		    System.out.printf("x:%d, y:%d, z:%d\n", startX, startY, startZ);
//		    
//		    Position worldPos = new Position(startX, startY, startZ);
//			Position chunkPos = Chunk.worldPosToChunkCoords(worldPos);
//			Position internalPos = Chunk.worldPosToLocalCoords(worldPos);
//			
//			Chunk chunk = chunks.get(chunkPos);
//			if (chunk == null) return null; // Our ray has exited the world!
//			if (chunk.getOccupied()[internalPos.x][internalPos.y][internalPos.z]) {
//				return worldPos;
//			}
//			
//			numSteps++;
//		}
//
//		return null;
//	}

	public int getRenderDistance() {
		return renderDistance;
	}
	
	public long getWorldSeed() {
		return seed;
	}
}
