package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.TexturedModel;
import shaders.StaticShader;
import world.Camera;
import world.Chunk;
import world.Entity;
import world.Light;

public class MasterRenderer {

	private StaticShader shader = new StaticShader();
	private Renderer renderer = new Renderer(shader);
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Chunk>> chunks = new HashMap<TexturedModel, List<Chunk>>();
	
	public void render(Light sun, Camera camera) {
		renderer.prepare();
		shader.start();
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
//		renderer.render(entities);
		renderer.renderChunks(chunks);
		shader.stop();
//		entities.clear();
		chunks.clear();
	}
	
	public void processEntity(Entity entity) {
		TexturedModel tModel = entity.getModel();
		List<Entity> batch = entities.get(tModel);
		
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(tModel, newBatch);
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
