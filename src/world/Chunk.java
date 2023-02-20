package world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import textures.ModelTexture;

public class Chunk {

	// This refers to chunk size in blocks.
	public static final int SIZE = 16;
	
	// Information about the all.png texture file.
	private static final int TEXTURES_WIDTH = 128;
	private static final int TEXTURES_HEIGHT = 64;
	private static final int TEXTURE_SIZE = 32;
	private static float tsx = (float) TEXTURE_SIZE / TEXTURES_WIDTH;
	private static float tsy = (float) TEXTURE_SIZE / TEXTURES_HEIGHT;
	private static ModelTexture TEXTURE;
	
	// String of the form "x,y,z" specified as integers.
	private Map<Position, Block> blocks;
	private boolean[][][] occupied;
	private TexturedModel tModel;
	private Vector3f position;
	
	private static final Vector3f[] vertVectors = {			
			new Vector3f(-0.5f,0.5f,-0.5f),
			new Vector3f(-0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,0.5f,-0.5f),
			
			new Vector3f(-0.5f,0.5f,0.5f),
			new Vector3f(-0.5f,-0.5f,0.5f),
			new Vector3f(0.5f,-0.5f,0.5f),
			new Vector3f(0.5f,0.5f,0.5f),
			
			new Vector3f(0.5f,0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,0.5f),
			new Vector3f(0.5f,0.5f,0.5f),
			
			new Vector3f(-0.5f,0.5f,-0.5f),
			new Vector3f(-0.5f,-0.5f,-0.5f),
			new Vector3f(-0.5f,-0.5f,0.5f),
			new Vector3f(-0.5f,0.5f,0.5f),
			
			new Vector3f(-0.5f,0.5f,0.5f),
			new Vector3f(-0.5f,0.5f,-0.5f),
			new Vector3f(0.5f,0.5f,-0.5f),
			new Vector3f(0.5f,0.5f,0.5f),

			new Vector3f(-0.5f,-0.5f,0.5f),
			new Vector3f(-0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,0.5f),
	};
	private static final Vector3f[] normVectors = {
			new Vector3f(0, 0, -1),
			new Vector3f(0, 0, -1),
			new Vector3f(0, 0, -1),
			new Vector3f(0, 0, -1),
			
			new Vector3f(0, 0, 1),
			new Vector3f(0, 0, 1),
			new Vector3f(0, 0, 1),
			new Vector3f(0, 0, 1),

			new Vector3f(1, 0, 0),
			new Vector3f(1, 0, 0),
			new Vector3f(1, 0, 0),
			new Vector3f(1, 0, 0),

			new Vector3f(-1, 0, 0),
			new Vector3f(-1, 0, 0),
			new Vector3f(-1, 0, 0),
			new Vector3f(-1, 0, 0),

			new Vector3f(0, 1, 0),
			new Vector3f(0, 1, 0),
			new Vector3f(0, 1, 0),
			new Vector3f(0, 1, 0),

			new Vector3f(0, -1, 0),
			new Vector3f(0, -1, 0),
			new Vector3f(0, -1, 0),
			new Vector3f(0, -1, 0),
	};
	@SuppressWarnings("serial") // Won't be serializing this so don't bother.
	private static final Map<String, Vector2f> textureOffsets = new HashMap<String, Vector2f>() {{
		put("grass_block", new Vector2f(0, 0));
		put("gold_block", new Vector2f(tsx, 0));
		put("clay_block", new Vector2f(0, tsy));
		put("soil_block", new Vector2f(tsx, tsy));
		put("oak_log", new Vector2f(2*tsx, 0));
	}};
	
	public Chunk(Loader loader, Map<Position, Block> blocks, Vector3f position) {
		this.blocks = blocks;
		this.position = position;
		this.occupied = new boolean[SIZE][SIZE][SIZE];
		
		// We don't know how many vertices and other mesh data
		// points we will have, so we will build our arrays later.
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] texturesArray = null;
		int[] indicesArray = null;
		
		// First we want to know everything about which blocks are
		// occupied, so when building our vertices list we know which
		// to leave out.
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					Position pos = new Position(x, y, z);
					occupied[x][y][z] = blocks.containsKey(pos);
				}
			}
		}

		int vertIndex = 0;
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {					
					if (occupied[x][y][z]) {						
						Position pos = new Position(x, y, z);
						Block block = blocks.get(pos);

						Vector3f posVect = new Vector3f(x, y, z);
						Vector2f textureCoords[] = getTextureCoords(textureOffsets.get(block.getType()));
						
						// We check if adjacent blocks are occupied or on chunk borders,
						// to know if we need those faces in our mesh or not.
						if (z-1 < 0 || !occupied[x][y][z-1]) {
							addFrontFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							vertIndex += 4;
						}
						if (z+1 >= SIZE || !occupied[x][y][z+1]) {
							addBackFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							vertIndex += 4;
						}
						if (x+1 >= SIZE || !occupied[x+1][y][z]) {
							addLeftFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							vertIndex += 4;
						}
						if (x-1 < 0 || !occupied[x-1][y][z]) {
							addRightFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							vertIndex += 4;
						}
						if (y+1 >= SIZE || !occupied[x][y+1][z]) {
							addTopFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							vertIndex += 4;
						}
						if (y-1 < 0 || !occupied[x][y-1][z]) {
							addBottomFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							vertIndex += 4;
						}
					}
				}
			}
		}
		
		// Now we create our arrays based on the lists we just built up.
		int i;
		
		i = 0;
		verticesArray = new float[vertices.size() * 3];
		for (Vector3f vertex : vertices) {
			verticesArray[i++] = vertex.x;
			verticesArray[i++] = vertex.y;
			verticesArray[i++] = vertex.z;
		}
				
		indicesArray = new int[indices.size()];
		for (i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}
		
		i = 0;
		texturesArray = new float[textures.size() * 2];
		for (Vector2f texture : textures) {
			texturesArray[i++] = texture.x;
			texturesArray[i++] = texture.y;
		}
		
		i = 0;
		normalsArray = new float[normals.size() * 3];
		for (Vector3f normal : normals) {
			normalsArray[i++] = normal.x;
			normalsArray[i++] = normal.y;
			normalsArray[i++] = normal.z;
		}
		
		RawModel rModel = loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
		if (TEXTURE == null) {
			TEXTURE = new ModelTexture(loader.loadTexture("all"));
		}
		this.tModel = new TexturedModel(rModel, TEXTURE);
	}
	
	// FACE HELPER METHODS \\
	// =================== \\
	
	private void addFrontFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices, 
			List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[0], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[1], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[2], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[3], offset, new Vector3f(0, 0, 0)));
		indices.add(vi+3); // 3
		indices.add(vi+2); // 2
		indices.add(vi+1); // 1
		indices.add(vi+1); // 1
		indices.add(vi); // 0
		indices.add(vi+3); // 3
		
		// Will need to do some kind of block based ofset
		textures.add(textureCoords[0]);
		textures.add(textureCoords[1]);
		textures.add(textureCoords[2]);
		textures.add(textureCoords[3]);
		
		normals.add(normVectors[0]);
		normals.add(normVectors[1]);
		normals.add(normVectors[2]);
		normals.add(normVectors[3]);
	}

	private void addBackFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices, 
List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[4], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[5], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[6], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[7], offset, new Vector3f(0, 0, 0)));
		indices.add(vi); // 4
		indices.add(vi+1); // 5
		indices.add(vi+3); // 7
		indices.add(vi+3); // 7
		indices.add(vi+1); // 5
		indices.add(vi+2); // 6
		
		textures.add(textureCoords[0]);
		textures.add(textureCoords[1]);
		textures.add(textureCoords[2]);
		textures.add(textureCoords[3]);
		
		normals.add(normVectors[4]);
		normals.add(normVectors[5]);
		normals.add(normVectors[6]);
		normals.add(normVectors[7]);
	}

	private void addLeftFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices, 
List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[8], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[9], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[10], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[11], offset, new Vector3f(0, 0, 0)));
		indices.add(vi); // 8
		indices.add(vi+3); // 11
		indices.add(vi+1); // 9
		indices.add(vi+3); // 11
		indices.add(vi+2); // 10
		indices.add(vi+1); // 9
		
		textures.add(textureCoords[4]);
		textures.add(textureCoords[5]);
		textures.add(textureCoords[6]);
		textures.add(textureCoords[7]);
		
		normals.add(normVectors[8]);
		normals.add(normVectors[9]);
		normals.add(normVectors[10]);
		normals.add(normVectors[11]);
	}

	private void addRightFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices, 
List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[12], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[13], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[14], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[15], offset, new Vector3f(0, 0, 0)));
		indices.add(vi); // 12
		indices.add(vi+1); // 13
		indices.add(vi+3); // 15
		indices.add(vi+3); // 15
		indices.add(vi+1); // 13
		indices.add(vi+2); // 14
		
		textures.add(textureCoords[4]);
		textures.add(textureCoords[5]);
		textures.add(textureCoords[6]);
		textures.add(textureCoords[7]);
		
		normals.add(normVectors[12]);
		normals.add(normVectors[13]);
		normals.add(normVectors[14]);
		normals.add(normVectors[15]);
	}

	private void addTopFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices, 
List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[16], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[17], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[18], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[19], offset, new Vector3f(0, 0, 0)));
		indices.add(vi); // 16
		indices.add(vi+3); // 19
		indices.add(vi+1); // 17
		indices.add(vi+3); // 19
		indices.add(vi+2); // 18
		indices.add(vi+1); // 17
		
		textures.add(textureCoords[8]);
		textures.add(textureCoords[9]);
		textures.add(textureCoords[10]);
		textures.add(textureCoords[11]);
		
		normals.add(normVectors[16]);
		normals.add(normVectors[17]);
		normals.add(normVectors[18]);
		normals.add(normVectors[19]);
	}

	private void addBottomFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices, 
List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[20], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[21], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[22], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[23], offset, new Vector3f(0, 0, 0)));
		indices.add(vi); // 20
		indices.add(vi+1); // 21
		indices.add(vi+3); // 23
		indices.add(vi+3); // 23
		indices.add(vi+1); // 21
		indices.add(vi+2); // 22
		
		textures.add(textureCoords[12]);
		textures.add(textureCoords[13]);
		textures.add(textureCoords[14]);
		textures.add(textureCoords[15]);
		
		normals.add(normVectors[20]);
		normals.add(normVectors[21]);
		normals.add(normVectors[22]);
		normals.add(normVectors[23]);
	}
	
	// TEXTURE HELPER METHODS \\
	// ====================== \\
	
	private static Vector2f[] getTextureCoords(Vector2f offset) {
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
	
	// OTHER CHUNK FUNCTIONS \\
	// ===================== \\
	
	public static Position worldPosToChunkCoords(int x, int y, int z) {
		return new Position(x / SIZE, y / SIZE, z / SIZE);
	}

	public static Position worldPosToChunkPos(int x, int y, int z) {
		return new Position(x % SIZE, y % SIZE, z % SIZE);
	}
	
	// GETTERS AND SETTERS \\
	// =================== \\
	
	public TexturedModel getModel() {
		return tModel;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public Map<Position, Block> getBlocks() {
		return blocks;
	}

	public boolean[][][] getOccupied() {
		return occupied;
	}
	
}
