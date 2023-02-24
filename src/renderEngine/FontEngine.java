package renderEngine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.GUIElement;

public class FontEngine {
	
	private static final float width = DisplayManager.WIDTH;
	
	private static Map<Character, Integer> textures;

	@SuppressWarnings("serial")
	public static void loadFont(Loader loader) {
		textures = new HashMap<>() {{
			put('A', loader.loadTexture("font/A"));
			put('B', loader.loadTexture("font/B"));
			put('C', loader.loadTexture("font/C"));
			put('D', loader.loadTexture("font/D"));
			put('E', loader.loadTexture("font/E"));
			put('F', loader.loadTexture("font/F"));
			put('G', loader.loadTexture("font/G"));
			put('H', loader.loadTexture("font/H"));
			put('I', loader.loadTexture("font/I"));
			put('J', loader.loadTexture("font/J"));
			put('K', loader.loadTexture("font/K"));
			put('L', loader.loadTexture("font/L"));
			put('M', loader.loadTexture("font/M"));
			put('N', loader.loadTexture("font/N"));
			put('O', loader.loadTexture("font/O"));
			put('P', loader.loadTexture("font/P"));
			put('Q', loader.loadTexture("font/Q"));
			put('R', loader.loadTexture("font/R"));
			put('S', loader.loadTexture("font/S"));
			put('T', loader.loadTexture("font/T"));
			put('U', loader.loadTexture("font/U"));
			put('V', loader.loadTexture("font/V"));
			put('W', loader.loadTexture("font/W"));
			put('X', loader.loadTexture("font/X"));
			put('Y', loader.loadTexture("font/Y"));
			put('Z', loader.loadTexture("font/Z"));
			put('a', loader.loadTexture("font/_a"));
			put('b', loader.loadTexture("font/_b"));
			put('c', loader.loadTexture("font/_c"));
			put('d', loader.loadTexture("font/_d"));
			put('e', loader.loadTexture("font/_e"));
			put('f', loader.loadTexture("font/_f"));
			put('g', loader.loadTexture("font/_g"));
			put('h', loader.loadTexture("font/_h"));
			put('i', loader.loadTexture("font/_i"));
			put('j', loader.loadTexture("font/_j"));
			put('k', loader.loadTexture("font/_k"));
			put('l', loader.loadTexture("font/_l"));
			put('m', loader.loadTexture("font/_m"));
			put('n', loader.loadTexture("font/_n"));
			put('o', loader.loadTexture("font/_o"));
			put('p', loader.loadTexture("font/_p"));
			put('q', loader.loadTexture("font/_q"));
			put('r', loader.loadTexture("font/_r"));
			put('s', loader.loadTexture("font/_s"));
			put('t', loader.loadTexture("font/_t"));
			put('u', loader.loadTexture("font/_u"));
			put('v', loader.loadTexture("font/_v"));
			put('w', loader.loadTexture("font/_w"));
			put('x', loader.loadTexture("font/_x"));
			put('y', loader.loadTexture("font/_y"));
			put('z', loader.loadTexture("font/_z"));
			put('0', loader.loadTexture("font/0"));
			put('1', loader.loadTexture("font/1"));
			put('2', loader.loadTexture("font/2"));
			put('3', loader.loadTexture("font/3"));
			put('4', loader.loadTexture("font/4"));
			put('5', loader.loadTexture("font/5"));
			put('6', loader.loadTexture("font/6"));
			put('7', loader.loadTexture("font/7"));
			put('8', loader.loadTexture("font/8"));
			put('9', loader.loadTexture("font/9"));
			put('.', loader.loadTexture("font/period"));
			put(',', loader.loadTexture("font/comma"));
			put('!', loader.loadTexture("font/exclamation"));
			put('?', loader.loadTexture("font/question"));
			put('/', loader.loadTexture("font/slash"));
			put('-', loader.loadTexture("font/dash"));
		}};
	}
	
	public static List<GUIElement> buildElementsFromString(String text, Vector2f position, float scale) {
		List<GUIElement> letters = new ArrayList<>();
		Vector2f fontSize = getFontSize(scale);
		
		float i = 0;
		for (char c : text.toCharArray()) {
			Vector2f offset = new Vector2f(i, 0);	
			
			System.out.println(c);
			if (c != ' ') {				
				GUIElement character = new GUIElement(
						textures.get(c),
						Vector2f.add(position, offset, offset),
						fontSize
						);
				letters.add(character);
			}
			
			i += fontSize.x;
		}
		
		return letters;
	}
	
	public static Vector2f getFontSize(float scale) {
		float charWidth = scale * 9f / width; // 9x15
		float charHeight = scale * 15f / width; // 9x15
		return new Vector2f(charWidth, charHeight);
	}
	
	public static float getDisplayWidth(int length, float scale) {
		float charWidth = scale * 9f / width;
		return charWidth * length;
	}
	
	private static final DecimalFormat positionFormatter = new DecimalFormat("#.##");
	public static String formatVectorForDisplay(Vector3f position) {
		String x = positionFormatter.format(position.x);
		String y = positionFormatter.format(position.y);
		String z = positionFormatter.format(position.z);
		return x + "," + y + "," + z;
	}
}
