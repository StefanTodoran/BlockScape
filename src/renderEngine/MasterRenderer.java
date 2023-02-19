package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Block;
import entities.Camera;
import entities.Chunk;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;

public class MasterRenderer {

	private StaticShader shader = new StaticShader();
	private Renderer renderer = new Renderer(shader);
	
	private Map<TexturedModel, List<Block>> blocks = new HashMap<TexturedModel, List<Block>>();
	private Map<TexturedModel, List<Chunk>> chunks = new HashMap<TexturedModel, List<Chunk>>();
	
	public void render(Light sun, Camera camera) {
		renderer.prepare();
		shader.start();
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
		renderer.render(blocks);
		renderer.renderChunks(chunks);
		shader.stop();
		blocks.clear();
	}
	
	public void processEntity(Block block) {
		TexturedModel tModel = block.getEntity().getModel();
		List<Block> batch = blocks.get(tModel);
		
		if (batch != null) {
			batch.add(block);
		} else {
			List<Block> newBatch = new ArrayList<Block>();
			newBatch.add(block);
			blocks.put(tModel, newBatch);
		}
	}
	
	public void processChunk(Chunk chunk) {
		TexturedModel tModel = chunk.getModel();
		List<Chunk> batch = chunks.get(tModel);
		
		if (batch != null) {
			batch.add(chunk);
		} else {
			List<Chunk> newBatch = new ArrayList<Chunk>();
			newBatch.add(chunk);
			chunks.put(tModel, newBatch);
		}
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
}
