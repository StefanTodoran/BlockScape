package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	private static final int FPS_CAP = 120;
	
	private static boolean fullScreen = false;
	private static long lastFrameTime;
	private static long delta;

	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
//			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));

			DisplayMode displayMode = null;
	        DisplayMode[] modes = Display.getAvailableDisplayModes();

	         for (int i = 0; i < modes.length; i++)
	         {
	             if (modes[i].getWidth() == WIDTH
	             && modes[i].getHeight() == HEIGHT
	             && modes[i].isFullscreenCapable())
	               {
	                    displayMode = modes[i];
	               }
	         }
	         Display.setDisplayMode(displayMode);
			
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("Escape the Down Under");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = Sys.getTime();
		Mouse.setGrabbed(true);
	}
	
	public static boolean updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();
		
		long currentTime = Sys.getTime();
		delta = currentTime - lastFrameTime;
		lastFrameTime = currentTime;
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					return true; // True, close is requested.
				}
			}
		}
		return false;
	}
	
	public static float getFrameTimeMS() {
		return delta;
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}
	
	public static void toggleFullscreen() {
		fullScreen = !fullScreen;
		try {
			Display.setFullscreen(fullScreen);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
}
