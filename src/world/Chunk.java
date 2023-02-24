package world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.ModelTexture;
import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;

public class Chunk {

	// This refers to chunk size in blocks.
	public static final int SIZE = 16;
	private static ModelTexture TEXTURE;
	
	// String of the form "x,y,z" specified as integers.
	private Map<Position, Block> blocks;
	private boolean[][][] occupied;
	
	private TexturedModel tModel;

	private Vector3f worldCoords;
	private Vector3f chunkCoords;
	
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
	
	public Chunk(Map<Position, Block> blocks, Vector3f chunkPosition) {
		this.blocks = blocks;
		this.chunkCoords = chunkPosition;
		this.worldCoords = Position.scaleVector(chunkPosition, Chunk.SIZE);
	}
	
	public void updateMesh(Loader loader) {
		this.occupied = new boolean[SIZE][SIZE][SIZE];
		
		// We don't know how many vertices and other mesh data
		// points we will have, so we will build our arrays later.
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> shines = new ArrayList<Integer>();

		List<Integer> indices = new ArrayList<Integer>();
		
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] texturesArray = null;
		float[] shinesArray = null;

		int[] indicesArray = null;
		
		boolean[][][] culls = new boolean[SIZE][SIZE][SIZE];
		
		// First we want to know everything about which blocks are
		// occupied, so when building our vertices list we know which
		// to leave out.
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					Position pos = new Position(x, y, z);
					Block block = blocks.get(pos);
					occupied[x][y][z] = block != null;
					culls[x][y][z] = block != null && block.doCulling();
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
						Vector2f textureCoords[] = block.getTextureCoords();
						
						// We check if adjacent blocks are occupied or on chunk borders,
						// to know if we need those faces in our mesh or not.
						if (z-1 < 0 || !cullsFaces(x, y, z-1, culls) || !block.doCulling()) {
							addFrontFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							addFaceShine(block, shines);
							vertIndex += 4;
						}
						if (z+1 >= SIZE || !cullsFaces(x, y, z+1, culls) || !block.doCulling()) {
							addBackFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							addFaceShine(block, shines);
							vertIndex += 4;
						}
						if (x+1 >= SIZE || !cullsFaces(x+1, y, z, culls) || !block.doCulling()) {
							addLeftFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							addFaceShine(block, shines);
							vertIndex += 4;
						}
						if (x-1 < 0 || !cullsFaces(x-1, y, z, culls) || !block.doCulling()) {
							addRightFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							addFaceShine(block, shines);
							vertIndex += 4;
						}
						if (y+1 >= SIZE || !cullsFaces(x, y+1, z, culls) || !block.doCulling()) {
							addTopFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							addFaceShine(block, shines);
							vertIndex += 4;
						}
						if (y-1 < 0 || !cullsFaces(x, y-1, z, culls) || !block.doCulling()) {
							addBottomFace(vertIndex, posVect, vertices, indices, textures, normals, textureCoords);
							addFaceShine(block, shines);
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
		
		shinesArray = new float[shines.size()];
		for (i = 0; i < shines.size(); i++) {
			shinesArray[i] = shines.get(i);
		}
		
		indicesArray = new int[indices.size()];
		for (i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}
		
		RawModel rModel;
		if (this.tModel == null) {
			rModel = loader.loadModelToVAO(verticesArray, texturesArray, normalsArray, shinesArray, indicesArray);			
		} else {
			int vaoID = tModel.getRawModel().getVaoID();
			rModel = loader.updateModelVAO(vaoID, verticesArray, texturesArray, normalsArray, shinesArray, indicesArray);
		}
		
		if (TEXTURE == null) {
			TEXTURE = new ModelTexture(loader.loadTexture("all"));
		}
		this.tModel = new TexturedModel(rModel, TEXTURE);
	}
	
	// FACE HELPER METHODS \\
	// =================== \\
	
	private boolean cullsFaces(int x, int y, int z, boolean[][][] culls) {
		return occupied[x][y][z] && culls[x][y][z];
	}
	
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
	
	/*
	private void addFrontFace(Vector3f offset, List<Vector3f> vertices, 
			List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[3], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[2], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[1], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[1], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[0], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[3], offset, new Vector3f(0, 0, 0)));
		
		textures.add(textureCoords[3]);
		textures.add(textureCoords[2]);
		textures.add(textureCoords[1]);
		textures.add(textureCoords[1]);
		textures.add(textureCoords[0]);
		textures.add(textureCoords[3]);
		
		normals.add(normVectors[3]);
		normals.add(normVectors[2]);
		normals.add(normVectors[1]);
		normals.add(normVectors[1]);
		normals.add(normVectors[0]);
		normals.add(normVectors[3]);
	}

	private void addBackFace(Vector3f offset, List<Vector3f> vertices, 
			List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[4], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[5], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[7], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[7], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[5], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[6], offset, new Vector3f(0, 0, 0)));

		textures.add(textureCoords[0]); // 4
		textures.add(textureCoords[1]); // 5
		textures.add(textureCoords[3]); // 7
		textures.add(textureCoords[3]); // 7
		textures.add(textureCoords[1]); // 5
		textures.add(textureCoords[2]); // 6

		normals.add(normVectors[4]);
		normals.add(normVectors[5]);
		normals.add(normVectors[7]);
		normals.add(normVectors[7]);
		normals.add(normVectors[5]);
		normals.add(normVectors[6]);
	}

	private void addLeftFace(Vector3f offset, List<Vector3f> vertices, 
			List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[8], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[11], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[9], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[11], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[10], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[9], offset, new Vector3f(0, 0, 0)));
		
		textures.add(textureCoords[4]); // 8
		textures.add(textureCoords[7]); // 11
		textures.add(textureCoords[5]); // 9
		textures.add(textureCoords[7]); // 11
		textures.add(textureCoords[6]); // 10
		textures.add(textureCoords[5]); // 9

		normals.add(normVectors[8]);
		normals.add(normVectors[11]);
		normals.add(normVectors[9]);
		normals.add(normVectors[11]);
		normals.add(normVectors[10]);
		normals.add(normVectors[9]);
	}

	private void addRightFace(Vector3f offset, List<Vector3f> vertices, 
			List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[12], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[13], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[15], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[15], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[13], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[14], offset, new Vector3f(0, 0, 0)));
		
		textures.add(textureCoords[4]); // 12
		textures.add(textureCoords[5]); // 13
		textures.add(textureCoords[7]); // 15
		textures.add(textureCoords[7]); // 15
		textures.add(textureCoords[5]); // 13
		textures.add(textureCoords[6]); // 14

		normals.add(normVectors[12]);
		normals.add(normVectors[13]);
		normals.add(normVectors[15]);
		normals.add(normVectors[15]);
		normals.add(normVectors[13]);
		normals.add(normVectors[14]);
	}

	private void addTopFace(Vector3f offset, List<Vector3f> vertices, 
			List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[16], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[19], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[17], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[19], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[18], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[17], offset, new Vector3f(0, 0, 0)));
		
		textures.add(textureCoords[8]); // 16
		textures.add(textureCoords[11]); // 19
		textures.add(textureCoords[9]); // 17
		textures.add(textureCoords[11]); // 19
		textures.add(textureCoords[10]); // 18
		textures.add(textureCoords[9]); // 17
		
		normals.add(normVectors[16]);
		normals.add(normVectors[19]);
		normals.add(normVectors[17]);
		normals.add(normVectors[19]);
		normals.add(normVectors[18]);
		normals.add(normVectors[17]);
	}

	private void addBottomFace(Vector3f offset, List<Vector3f> vertices, 
			List<Vector2f> textures, List<Vector3f> normals, Vector2f[] textureCoords) {
		vertices.add(Vector3f.add(vertVectors[20], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[21], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[23], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[23], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[21], offset, new Vector3f(0, 0, 0)));
		vertices.add(Vector3f.add(vertVectors[22], offset, new Vector3f(0, 0, 0)));
		
		textures.add(textureCoords[12]); // 20
		textures.add(textureCoords[13]); // 21
		textures.add(textureCoords[15]); // 23
		textures.add(textureCoords[15]); // 23
		textures.add(textureCoords[13]); // 21
		textures.add(textureCoords[14]); // 22
		
		normals.add(normVectors[20]);
		normals.add(normVectors[21]);
		normals.add(normVectors[23]);
		normals.add(normVectors[23]);
		normals.add(normVectors[21]);
		normals.add(normVectors[22]);
	}
*/
	
	private void addFaceShine(Block block, List<Integer> shines) {
		// Shine is set for each vertex to a boolean 0 or 1 value, indicating whether 
		// that vertex is part of a shiny/reflective block. All vertices in a block will
		// either have reflectivity 0 or 1.
		
		for (int i = 0; i < 4; i++) {
			shines.add(block.isShiny() ? 1 : 0);
		}
	}
	
	// OTHER CHUNK FUNCTIONS \\
	// ===================== \\
	
	public static Position worldPosToChunkCoords(int x, int y, int z) {
		int cx = x / SIZE;
		if (x < 0) { cx -= 1; }
		int cy = y / SIZE;
		int cz = z / SIZE;
		if (z < 0) { cz -= 1; }
		return new Position(cx, cy, cz);
	}

	public static Position worldPosToChunkCoords(Position wp) {
		return worldPosToChunkCoords(wp.x, wp.y, wp.z);
	}

	public static Position worldPosToLocalCoords(int x, int y, int z) {
		int lx = (x >= 0) ? x % SIZE: SIZE + (x % SIZE);
		int ly = (y >= 0) ? y % SIZE: SIZE + (y % SIZE);
		int lz = (z >= 0) ? z % SIZE: SIZE + (z % SIZE);
		return new Position(lx, ly, lz);
	}

	public static Position worldPosToLocalCoords(Position wp) {
		return worldPosToLocalCoords(wp.x, wp.y, wp.z);
	}
	
	// GETTERS AND SETTERS \\
	// =================== \\
	
	public TexturedModel getModel() {
		return tModel;
	}
	
	public Vector3f getChunkPosition() {
		return chunkCoords;
	}
	
	public Vector3f getWorldPosition() {
		return worldCoords;
	}

	public Map<Position, Block> getBlocks() {
		return blocks;
	}
	
	public void setBlock(Position pos, Block block) {
		blocks.put(pos, block);
	}
	
	public void setAllBlocks(Map<Position, Block> newBlocks) {
		this.blocks = newBlocks;
	}

	public boolean[][][] getOccupied() {
		return occupied;
	}
	
}
