package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.GUIElement;
import models.RawModel;
import models.Reticle;
import shaders.GUIShader;
import toolbox.Maths;

public class GUIRenderer {

	private final RawModel quad;
	private GUIShader shader;
	
	public GUIRenderer(Loader loader) {
		float[] screenCorners = { -1, 1, -1, -1, 1, 1, 1, -1 };
		quad = loader.loadFlatToVAO(screenCorners);
		shader = new GUIShader();
	}
	
	public void render(List<GUIElement> gui) {
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for (GUIElement e : gui) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, e.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(e.getPosition(), e.getScale());
			shader.loadTransformMatrix(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);

		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void renderReticle() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		
		RawModel model = Reticle.getModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, model.getVertexCount());
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void clearScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
}
