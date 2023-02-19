package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Block;
import entities.Chunk;
import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

public class Renderer {
	
	private static final float FOV = 90;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectMatrix;
	private StaticShader shader;
	
	public Renderer(StaticShader shader) {
		this.shader = shader;
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		createProjectMatrix(); 
		// potentially move this so we can change FOV and other camera
		// settings, and allow those settings to be in the camera
		shader.start();
		shader.loadProjectMatrix(projectMatrix);
		shader.stop();
	}

	// Prepares OpenGL to render the game.
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
	}
	
	public void renderChunks(Map<TexturedModel, List<Chunk>> chunks) {
		for (TexturedModel model : chunks.keySet()) {
			prepareTexturedModel(model);
			List<Chunk> batch = chunks.get(model);
			
			for (Chunk chunk : batch) {
				prepareInstance(chunk);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	public void render(Map<TexturedModel, List<Block>> blocks) {
		for (TexturedModel model : blocks.keySet()) {
			prepareTexturedModel(model);
			List<Block> batch = blocks.get(model);
			
			for (Block block : batch) {
				prepareInstance(block.getEntity());
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel tModel) {
		RawModel rModel = tModel.getRawModel();
		GL30.glBindVertexArray(rModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = tModel.getTexture();
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tModel.getTexture().getID());
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {
		Matrix4f transformMatrix = Maths.createTransformMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformMatrix(transformMatrix);
	}

	private void prepareInstance(Chunk chunk) {		
		Matrix4f transformMatrix = Maths.createTranslationMatrix(chunk.getPosition());
		shader.loadTransformMatrix(transformMatrix);
	}
	
	private void createProjectMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float xScale = yScale / aspectRatio;
		float frustumLen = FAR_PLANE - NEAR_PLANE;
		
		projectMatrix = new Matrix4f();
		projectMatrix.m00 = xScale;
		projectMatrix.m11 = yScale;
		projectMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLen);
		projectMatrix.m23 = -1;
		projectMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLen);
		projectMatrix.m33 = 0;
	}
}
