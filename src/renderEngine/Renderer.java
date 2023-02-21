package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.RawModel;
import models.Reticle;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;
import world.Chunk;
import world.Entity;

public class Renderer {
	
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectMatrix;
	private StaticShader shader;
	
	public Renderer(StaticShader shader) {
		this.shader = shader;
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	// Prepares OpenGL to render the game.
	public void prepare(float FOV) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
		createProjectMatrix(FOV); 
		shader.start();
		shader.loadProjectMatrix(projectMatrix);
		shader.stop();
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

	public void renderEntities(Map<TexturedModel, List<Entity>> entities) {
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			
			for (Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	public void renderReticle() {
		RawModel model = Reticle.getModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
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
	
	private void createProjectMatrix(float FOV) {
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
