package world;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;

public class Block {

	private String type;
	private boolean cull;
	private boolean shiny;
	private int mesh;
	
	public static final int CUBE = 1;
	public static final int CROSSES = 2;
	
	@SuppressWarnings("serial")
	private static final Map<String, Boolean> doCulling = new HashMap<String, Boolean>() {{
		put("grass_block", true);
		put("gold_block", true);
		put("clay_block", true);
		put("soil_block", true);
		put("oak_log", true);
		put("oak_leaves", false);
		put("stone_block", true);
		put("buried_oil", true);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<String, Boolean> isShiny = new HashMap<String, Boolean>() {{
		put("grass_block", false);
		put("gold_block", true);
		put("clay_block", false);
		put("soil_block", false);
		put("oak_log", false);
		put("oak_leaves", false);
		put("stone_block", false);
		put("buried_oil", false);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<String, Integer> meshType = new HashMap<String, Integer>() {{
		put("grass_block", CUBE);
		put("gold_block", CUBE);
		put("clay_block", CUBE);
		put("soil_block", CUBE);
		put("oak_log", CUBE);
		put("oak_leaves", CUBE);
		put("stone_block", CUBE);
		put("buried_oil", CUBE);
	}};
	
	public Block(String type) {
		this.type = type;
		this.cull = doCulling.get(type);
		this.shiny = isShiny.get(type);
		this.mesh = meshType.get(type);
	}
	
	// Information about the all.png texture file.
	private static final int TEXTURES_WIDTH = 128;
	private static final int TEXTURES_HEIGHT = 128;
	private static final int TEXTURE_SIZE = 16;
	private static float tsx = (float) TEXTURE_SIZE / TEXTURES_WIDTH;
	private static float tsy = (float) TEXTURE_SIZE / TEXTURES_HEIGHT;
	
	@SuppressWarnings("serial") // Won't be serializing this so don't bother.
	private static final Map<String, Vector2f> textureOffsets = new HashMap<String, Vector2f>() {{
		put("grass_block", new Vector2f(0, 0));
		put("gold_block", new Vector2f(tsx*2, 0));
		put("clay_block", new Vector2f(0, tsy*2));
		put("soil_block", new Vector2f(tsx, tsy));
		put("oak_log", new Vector2f(4*tsx, 0));
		put("oak_leaves", new Vector2f(4*tsx, tsy));
		put("stone_block", new Vector2f(6*tsx, 0));
		put("buried_oil", new Vector2f(7*tsx, 0));
	}};
	
	enum FaceType {
		allUnique, // Every face has a different texture
		sidesSame, // The top and bottom have different textures
		allSame, // All faces share the same texture
	}
	
	@SuppressWarnings("serial")
	private static final Map<String, FaceType> textureFaceTypes = new HashMap<String, FaceType>() {{
		put("grass_block", FaceType.allUnique);
		put("gold_block", FaceType.allUnique);
		put("clay_block", FaceType.sidesSame);
		put("soil_block", FaceType.allSame);
		put("oak_log", FaceType.sidesSame);
		put("oak_leaves", FaceType.sidesSame);
		put("stone_block", FaceType.allSame);
		put("buried_oil", FaceType.allSame);
	}};
	
	public Vector2f[] getTextureCoords() {
		Vector2f offset = textureOffsets.get(this.type);
		FaceType faceType = textureFaceTypes.get(this.type);
		
		switch (faceType) {
			case allUnique:
				return getTextureCoordsFacesAllUnique(offset);
			case sidesSame:
				return getTextureCoordsSidesSame(offset);
			case allSame:
				return getTextureCoordsFacesAllSame(offset);
		}
		
		return null;
	}
	
	private Vector2f[] getTextureCoordsFacesAllUnique(Vector2f offset) {
		// start texture x, start texture y
		float stx = offset.x;
		float sty = offset.y;
				
		// half and end texture coords
		float htx = stx + tsx;
		float hty = sty + tsy;
		float etx = stx + (tsx * 2);
		float ety = sty + (tsy * 2);
		
		return new Vector2f[] {
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
	}
	
	private Vector2f[] getTextureCoordsSidesSame(Vector2f offset) {
		// start texture x, start texture y
		float stx = offset.x;
		float sty = offset.y;
				
		// half and end texture coords
		float htx = stx + tsx;
		float hty = sty + tsy;
		float etx = stx + (tsx * 2);
		
		return new Vector2f[] {
				// front or back (+z and -z sides)
				new Vector2f(etx,sty),
				new Vector2f(etx,hty),
				new Vector2f(htx,hty),
				new Vector2f(htx,sty),
				
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
				new Vector2f(stx,sty),
				new Vector2f(stx,hty),
				new Vector2f(htx,hty),
				new Vector2f(htx,sty),
		};
	}
	
	private Vector2f[] getTextureCoordsFacesAllSame(Vector2f offset) {
		// start texture x, start texture y
		float stx = offset.x;
		float sty = offset.y;
				
		// end texture coords
		float etx = stx + tsx;
		float ety = sty + tsy;
		
		return new Vector2f[] {
				// front or back (z sides)
				new Vector2f(stx,sty),
				new Vector2f(stx,ety),
				new Vector2f(etx,ety),
				new Vector2f(etx,sty),
				
				// +x and -x sides
				new Vector2f(stx,sty),
				new Vector2f(stx,ety),
				new Vector2f(etx,ety),
				new Vector2f(etx,sty),
				
				// top
				new Vector2f(stx,sty),
				new Vector2f(stx,ety),
				new Vector2f(etx,ety),
				new Vector2f(etx,sty),
				
				// bottom
				new Vector2f(stx,sty),
				new Vector2f(stx,ety),
				new Vector2f(etx,ety),
				new Vector2f(etx,sty),
		};
	}

	public String getType() {
		return type;
	}
	
	public boolean doCulling() {
		return cull;
	}
	
	public boolean isShiny() {
		return shiny;
	}

	public int meshType() {
		return mesh;
	}
}
