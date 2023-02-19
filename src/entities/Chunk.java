package entities;

import java.util.ArrayList;
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
	private static final int SIZE = 16;
	
	// Information about the all.png texture file.
	private static final int TEXTURES_WIDTH = 64;
	private static final int TEXTURES_HEIGHT = 64;
	private static final int TEXTURE_SIZE = 16;
	
	// String of the form "x,y,z" specified as integers.
	private Map<String, Boolean> blocks;
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
	
	public Chunk(Loader loader, Map<String, Boolean> blocks, Vector3f position) {
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
					String pos = String.format("%d,%d,%d", x, y, z);
					occupied[x][y][z] = blocks.containsKey(pos);
//					System.out.println(pos + blocks.containsKey(pos));
				}
			}
		}

		int vertIndex = 0;
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {					
					if (occupied[x][y][z]) {						
						String pos = String.format("%d,%d,%d", x, y, z);
						//Block block = blocks.get(pos); TODO: will be used for texture

						Vector3f posVect = new Vector3f(x, y, z);
						if (!occupied[x][y][z-1]) {
							addFrontFace(vertIndex, posVect, vertices, indices);
							vertIndex += 4;
						}
						if (!occupied[x][y][z+1]) {
							addBackFace(vertIndex, posVect, vertices, indices);
							vertIndex += 4;
						}
						if (!occupied[x+1][y][z]) {
							addLeftFace(vertIndex, posVect, vertices, indices);
							vertIndex += 4;
						}
						if (!occupied[x-1][y][z]) {
							addRightFace(vertIndex, posVect, vertices, indices);
							vertIndex += 4;
						}
						if (!occupied[x][y+1][z]) {
							addTopFace(vertIndex, posVect, vertices, indices);
							vertIndex += 4;
						}
						if (!occupied[x][y-1][z]) {
							addBottomFace(vertIndex, posVect, vertices, indices);
							vertIndex += 4;
						}
					}
				}
			}
		}
		
		verticesArray = new float[vertices.size() * 3];
		
		int i = 0;
		for (Vector3f vertex : vertices) {
			verticesArray[i++] = vertex.x;
			verticesArray[i++] = vertex.y;
			verticesArray[i++] = vertex.z;
		}
		
		indicesArray = new int[indices.size()];
		
		for (i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}
		
		normalsArray = new float[vertices.size()*3];
		texturesArray = new float[vertices.size()*2];
		
		RawModel rModel = loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
		// TODO: Make this model texture static!
		ModelTexture texture = new ModelTexture(loader.loadTexture("all"));
		this.tModel = new TexturedModel(rModel, texture);
		
		System.out.println(vertices.toString());
	}
	
	// FACE HELPER METHODS \\
	// =================== \\
	
	private void addFrontFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices) {
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
	}

	private void addBackFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices) {
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
	}

	private void addLeftFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices) {
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
	}

	private void addRightFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices) {
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
	}

	private void addTopFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices) {
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
	}

	private void addBottomFace(Integer vi, Vector3f offset, List<Vector3f> vertices, List<Integer> indices) {
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
	}
	
	// GETTERS AND SETTERS \\
	// =================== \\
	
	public TexturedModel getModel() {
		return tModel;
	}
	
	public Vector3f getPosition() {
		return position;
	}
}
