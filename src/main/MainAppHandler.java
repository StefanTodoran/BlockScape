package main;

import java.util.List;

import org.lwjgl.opengl.Display;

import models.GUIElement;
import renderEngine.DisplayManager;
import renderEngine.FontEngine;
import renderEngine.GUIRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import userInterface.MainMenu;

public class MainAppHandler {

	public static Loader loader;
	public static GUIRenderer guiRenderer;
	
	public static void main(String[] args) {
		DisplayManager.createDisplay();
		loader = new Loader();
		FontEngine.loadFont(loader);
		guiRenderer = new GUIRenderer(loader);
		
		MainMenu.constructMainMenu();
		List<GUIElement> gui;

		int action = MainMenu.NO_ACTION;
		boolean closeRequested = false;
		while (!closeRequested) {
			action = MainMenu.doUpdate();
			gui = MainMenu.getAssetsForRender();
		
			if (action == MainMenu.NEW_WORLD) {
				MainGameLoop.runGame(loader, guiRenderer);
			}
			if (action == MainMenu.SETTINGS) {
				// OPEN SETTINGS MENU
			}
			if (action == MainMenu.SAVE_QUIT) {
				// SAVE CHUNK DATA
				cleanUp();
			}
			
			guiRenderer.clearScreen();
			guiRenderer.render(gui);
			DisplayManager.updateDisplay();
			closeRequested = Display.isCloseRequested();
		}
		if (closeRequested) {
			cleanUp();
		}
	}
	
	public static void cleanUp() {
		DisplayManager.closeDisplay();
		loader.cleanUp();
		guiRenderer.cleanUp();
		System.exit(0);
	}
}
