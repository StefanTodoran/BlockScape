package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.GUIElement;
import models.Reticle;
import renderEngine.DisplayManager;
import renderEngine.FontEngine;
import renderEngine.GUIRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.ChunkDataHandler;
import userInterface.MainMenu;
import world.Camera;
import world.Chunk;
import world.Light;
import world.Position;
import world.World;

public class Main {
	
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
		World world = null;
		
		while (!closeRequested) {
			action = MainMenu.doUpdate();
			gui = MainMenu.getAssetsForRender();
		
			if (action == MainMenu.RESUME_GAME) {
				runGame(loader, guiRenderer, world);
			}
			if (action == MainMenu.NEW_WORLD) {
				world = createNewWorld();
				MainMenu.canResume(true);
				runGame(loader, guiRenderer, world);
			}
			if (action == MainMenu.LOAD_WORLD) {
				world = loadSavedWorld();
				MainMenu.canResume(true);
				runGame(loader, guiRenderer, world);
			}
			if (action == MainMenu.SETTINGS) {
				// OPEN SETTINGS MENU
			}
			if (action == MainMenu.SAVE_QUIT) {
				ChunkDataHandler.saveWorldData(world);
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
	
	private static World createNewWorld() {		
		List<GUIElement> gui = FontEngine.guiFromString("Building World...", new Vector2f(0, 0), 3, true);
		guiRenderer.clearScreen();
		guiRenderer.render(gui);
		DisplayManager.updateDisplay();
		
		return new World(loader, 123456789L);
	}
	
	private static World loadSavedWorld() {
		List<GUIElement> gui = FontEngine.guiFromString("Loading World...", new Vector2f(0, 0), 3, true);
		guiRenderer.clearScreen();
		guiRenderer.render(gui);
		DisplayManager.updateDisplay();
		
		return new World(loader);
	}
	
	public static void cleanUp() {
		DisplayManager.closeDisplay();
		loader.cleanUp();
		guiRenderer.cleanUp();
		System.exit(0);
	}
	
	private static Vector3f playerPosition = new Vector3f(0, 30, 0);
	private static Vector3f playerRotation = new Vector3f(0, 0, 0);

	// Expects display to be created already.
	public static void runGame(Loader loader, GUIRenderer guiRenderer, World world) {
		MasterRenderer renderer = new MasterRenderer();
		List<GUIElement> gui = new ArrayList<>();
		
		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		Camera camera = new Camera(playerPosition, playerRotation);
		Reticle reticle = new Reticle();
		reticle.loadReticle(loader);
		
		int frames = 0;
		float time = 0;
		List<GUIElement> fps = new ArrayList<>();
		List<GUIElement> debug = new ArrayList<>();
		
		float displayWidth = FontEngine.getDisplayWidth(2);
		float displayHeight = FontEngine.getDisplayHeight(2);
		Vector2f fpsPos = new Vector2f(-1 + 2 * displayWidth, 1 - 2 * displayHeight);
		Vector2f debugPos = new Vector2f(-1 + 2 * displayWidth, 1 - 4 * displayHeight);
		
		Mouse.setGrabbed(true);
		float lastTick = 0;
		boolean closeRequested = false;
		while (!closeRequested && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			// GAME LOGIC
			float delta = DisplayManager.getFrameTimeMS();
			lastTick += delta;
			
			time += delta;
			frames += 1;
			if (frames >= 3) {
				int estimate = (int) (1000 / time) * frames;
				
				fps = FontEngine.guiFromString(estimate+" FPS", fpsPos, 2, false);
				frames = 0;
				time = 0;
			}
					
			// This code ensures that camera position updates only occur every
			// 100 ticks, so faster framerates don't result in faster motion.
			int action = Camera.NO_ACTION;
			while (lastTick > 100) {
				lastTick -= 10;

				int action_ = camera.doUpdateGetActions();
				action = action_ != Camera.NO_ACTION ? action_ : action;
			}
			
			Vector3f camPos = camera.getPosition();
			light.setPosition(camPos);
			debug = FontEngine.guiFromString(FontEngine.formatVectorForDisplay(camPos), debugPos, 2, false);
			
			Chunk updated = null;
			if (action == Camera.LEFT_CLICK) {
				Vector3f camDir = new Vector3f(camera.getPitch(), camera.getRoll(), camera.getYaw());
				Position targetPos = world.doRaycast(camPos, camDir, 10);
				System.out.println(targetPos);
				updated = world.setBlock(new Position(targetPos), "gold_block");
			}
			if (updated != null) {
				updated.updateMesh(loader);
			}
			
			Map<Position, Chunk> chunks = world.getChunksAround(new Position(camPos));
			
			// RENDER STEP
			for (Position position : chunks.keySet()) {
				Chunk chunk = chunks.get(position);
				renderer.processChunk(chunk);
			}
			renderer.render(light, camera);
			
			gui = new ArrayList<>();
			gui.addAll(fps);
			gui.addAll(debug);
			
			guiRenderer.render(gui);
			guiRenderer.renderReticle();
			
			DisplayManager.updateDisplay();
			closeRequested = Display.isCloseRequested();
		}
		if (closeRequested) {
			renderer.cleanUp();
			cleanUp();
		}
		Mouse.setGrabbed(false);
	}

}
