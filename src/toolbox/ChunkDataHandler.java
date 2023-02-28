package toolbox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import org.lwjgl.util.vector.Vector3f;

import world.Chunk;
import world.World;

public class ChunkDataHandler {
	
	public static void saveWorldData(World world) {
		try {
			File metaDataFile = new File("world.mdf");
			
			if (metaDataFile.createNewFile()) {
		        System.out.println("Save file created: " + metaDataFile.getName());
			} else {
				System.out.println("Save file already exists.");
			}
			
			FileWriter fileWriter = new FileWriter("world.mdf");
			fileWriter.write("seed:"+world.getWorldSeed());
			fileWriter.write("updated:"+LocalDateTime.now());
			fileWriter.close();
			
		} catch (IOException e) {
			System.out.println("(!) Failed to save world to file");
			e.printStackTrace();
		}
	}

	public static void saveChunkData(Chunk chunk) {
		Vector3f pos = chunk.getChunkPosition();
		String path = String.format("chunk%d,%d,%d", pos.x, pos.y, pos.z);
//		File file = new File(path);
	}

}
