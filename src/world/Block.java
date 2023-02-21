package world;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;

public class Block {

	private String type;
	private boolean cull;
	
	@SuppressWarnings("serial")
	private static final Map<String, Boolean> doCulling = new HashMap<String, Boolean>() {{
		put("grass_block", true);
		put("gold_block", true);
		put("clay_block", true);
		put("soil_block", true);
		put("oak_log", true);
		put("oak_leaves", false);
		put("stone_block", true);
	}};
	
	public Block(String type) {
		this.type = type;
		this.cull = doCulling.get(type);
	}
	
	// Information about the all.png texture file.
	private static final int TEXTURES_WIDTH = 128;
	private static final int TEXTURES_HEIGHT = 64;
	private static final int TEXTURE_SIZE = 32;
	private static float tsx = (float) TEXTURE_SIZE / TEXTURES_WIDTH;
	private static float tsy = (float) TEXTURE_SIZE / TEXTURES_HEIGHT;
	
	@SuppressWarnings("serial") // Won't be serializing this so don't bother.
	private static final Map<String, Vector2f> textureOffsets = new HashMap<String, Vector2f>() {{
		put("grass_block", new Vector2f(0, 0));
		put("gold_block", new Vector2f(tsx, 0));
		put("clay_block", new Vector2f(0, tsy));
		put("soil_block", new Vector2f(tsx, tsy));
		put("oak_log", new Vector2f(2*tsx, 0));
		put("oak_leaves", new Vector2f(2*tsx, tsy));
		put("stone_block", new Vector2f(3*tsx, 0));
	}};
	
	public Vector2f[] getTextureCoords() {
		Vector2f offset = textureOffsets.get(this.type);
		
		// start texture x, start texture y
		float stx = offset.x;
		float sty = offset.y;
		
		// half and end texture coords
		float htx = stx + (tsx / 2);
		float hty = sty + (tsy / 2);
		float etx = stx + tsx;
		float ety = sty + tsy;
		
		final Vector2f[] textureCoords = {
				// front or back (z sides)
				new Vector2f(stx,hty),
				new Vector2f(stx,ety),
				new Vector2f(htx,ety),
				new Vector2f(htx,hty),
				
				// +x and -x sides
				new Vector2f(etx,sty),
				new Vector2f(etx,hty),
				new Vector2f(htx,hty),
				new Vector2f(htx,sty),
				
				// top
				new Vector2f(stx,sty),
				new Vector2f(stx,hty),
				new Vector2f(htx,hty),
				new Vector2f(htx,sty),
				
				// bottom
				new Vector2f(htx,hty),
				new Vector2f(htx,ety),
				new Vector2f(etx,ety),
				new Vector2f(etx,hty),
		};
		
		return textureCoords;
	}

	public String getType() {
		return type;
	}
	
	public boolean doCulling() {
		return cull;
	}

}
