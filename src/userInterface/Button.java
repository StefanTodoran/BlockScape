package userInterface;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import models.GUIElement;
import renderEngine.FontEngine;

public class Button {

	private float minX;
	private float minY;
	private float maxX;
	private float maxY;
	
	private boolean hovered = false;
	private int action;
	private List<GUIElement> guiNormal;
	private List<GUIElement> guiHover;
	
	public Button(String text, Vector2f position, int action) {
		float width = FontEngine.getDisplayWidth(text.length(), 2);
		float height = FontEngine.getDisplayHeight(2);
		
		minX = position.x - (width / 2);
		maxX = position.x + (width / 2);
		minY = position.y - height;
		maxY = position.y + height;
		
		guiNormal = FontEngine.guiFromString(text, position, 2, true);
		guiHover = FontEngine.guiFromString("> "+text+" <", position, 2, true);
		
		this.action = action;
	}
	
	public boolean checkBounds(float mouseX, float mouseY) {
		if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY) {
			hovered = true;
		} else {
			hovered = false;
		}
		return hovered;
	}

	// Make sure you call checkBounds() before this if 
	// the mouse has moved, otherwise hovered won't be up to date.
	public List<GUIElement> getGUI() {
		if (hovered) {
			return guiHover;
		} else {
			return guiNormal;
		}
	}

	public int getAction() {
		return action;
	}
}
