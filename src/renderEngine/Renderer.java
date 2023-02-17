package renderEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Block;
import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import toolbox.Maths;

public class Renderer {
	
	private static final float FOV = 90;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectMatrix;
	
	public Renderer(StaticShader shader) {
		createProjectMatrix();
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
	
	public void render(Block block, StaticShader shader) {
		Entity entity = block.getEntity();
		TexturedModel tModel = entity.getModel();
		RawModel rModel = tModel.getRawModel();
		GL30.glBindVertexArray(rModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Matrix4f transformMatrix = Maths.createTransformMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		
		shader.loadTransformMatrix(transformMatrix);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tModel.getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, rModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
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
