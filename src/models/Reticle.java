package models;

import renderEngine.DisplayManager;
import renderEngine.Loader;

public class Reticle {
	
	private static RawModel model;
	
	// Window width and height correction.
	private static final float wc = (float) DisplayManager.HEIGHT / DisplayManager.WIDTH;
	private static final float hc = (float) DisplayManager.WIDTH / DisplayManager.HEIGHT;
	
	// Length and width of reticle.
	private static final float l = 0.02f;
	private static final float w = 0.004f;
	
	// Actual model of the reticle.
	private final float[] vertices = {
			-l*wc, w,
			-l*wc, -w,
			l*wc, -w,
			
			l*wc, -w,
			l*wc, w,
			-l*wc, w,
			
			-w*wc, l,
			-w*wc, -l,
			w*wc, -l,
			
			w*wc, -l,
			w*wc, l,
			-w*wc, l,
	};
	
	public void loadReticle(Loader loader) {
		model = loader.loadToVAO(vertices);
	}

	public static RawModel getModel() {
		return model;
	}
	
}
