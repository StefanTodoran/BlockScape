package userInterface;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import main.MainAppHandler;
import models.GUIElement;
import renderEngine.DisplayManager;
import renderEngine.FontEngine;

public class MainMenu {

	private static List<GUIElement> staticAssets;
	private static List<Button> buttons;
	
	public static void constructMainMenu() {
		staticAssets = new ArrayList<>();
		buttons = new ArrayList<>();
		
		// MENU BACKGROUND
		staticAssets.add(new GUIElement(MainAppHandler.loader.loadTexture("background"), new Vector2f(0, 0), new Vector2f(1, 1)));

		float gap = 2*FontEngine.getDisplayHeight(5);
		staticAssets.addAll(FontEngine.guiFromString("Escape The Down Under", new Vector2f(0, 0.75f), 5, true));
		staticAssets.addAll(FontEngine.guiFromString("Main Menu", new Vector2f(0, 0.75f - gap), 3, true));
	
		buttons.add(new Button("Create New World", new Vector2f(0, 0 + gap), NEW_WORLD));
		buttons.add(new Button("Modify Settings", new Vector2f(0, 0), SETTINGS));
		buttons.add(new Button("Save and Quit", new Vector2f(0, 0 - gap), SAVE_QUIT));
	}
	
	public static final int NO_ACTION = -1;
	public static final int NEW_WORLD = 1;
	public static final int SETTINGS = 2;
	public static final int SAVE_QUIT = 3;
	
	private static boolean mouseWasDown = false;
	private static boolean clicked = false;
	
	public static int doUpdate() {
		clicked = false;
		if (!Mouse.isButtonDown(0) && mouseWasDown) {
			mouseWasDown = false;
			clicked = true;
		} else if (Mouse.isButtonDown(0)) {
			mouseWasDown = true;
		}
		
		// In openGL the middle is (0,0) and the edges of the window are 1 away
		// The mouse coords are from 0 to Display.WIDTH and Display.HEIGHT
		float mouseX = ((float) Mouse.getX() / DisplayManager.WIDTH*2)-1;
		float mouseY = ((float) Mouse.getY() / DisplayManager.HEIGHT*2)-1;

		int action = NO_ACTION;
		for (Button button : buttons) {
			boolean hovered = button.checkBounds(mouseX, mouseY);
			
			if (clicked && hovered) {
				action = button.getAction();
			}
		}
		
		return action;
	}
	
	public static List<GUIElement> getAssetsForRender() {
		List<GUIElement> gui = new ArrayList<>();
		gui.addAll(staticAssets);
		
		for (Button button : buttons) {
			gui.addAll(button.getGUI());
		}
		
		return gui;
	}
}
