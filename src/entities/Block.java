package entities;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import textures.ModelTexture;

public class Block {

	private static final float[] vertices = {			
			-0.5f,0.5f,-0.5f, // 1
			-0.5f,-0.5f,-0.5f, // 2
			0.5f,-0.5f,-0.5f, // 3
			0.5f,0.5f,-0.5f, // 4
			
			-0.5f,0.5f,0.5f, // 5
			-0.5f,-0.5f,0.5f, // 6
			0.5f,-0.5f,0.5f, // 7
			0.5f,0.5f,0.5f, // 8
			
			0.5f,0.5f,-0.5f, // 9
			0.5f,-0.5f,-0.5f, // 10
			0.5f,-0.5f,0.5f, // 11
			0.5f,0.5f,0.5f, // 12
			
			-0.5f,0.5f,-0.5f, // 13
			-0.5f,-0.5f,-0.5f, // 14
			-0.5f,-0.5f,0.5f, // 15
			-0.5f,0.5f,0.5f, // 16
			
			-0.5f,0.5f,0.5f, // 17
			-0.5f,0.5f,-0.5f, // 18
			0.5f,0.5f,-0.5f, // 19
			0.5f,0.5f,0.5f, // 20
			
			-0.5f,-0.5f,0.5f, // 21
			-0.5f,-0.5f,-0.5f, // 22
			0.5f,-0.5f,-0.5f, // 23
			0.5f,-0.5f,0.5f	 // 24
	};
	private static final int[] indices = {
			// -z side (front)
			3,2,1,
			1,0,3,
			// +z side (back)
			4,5,7,
			7,5,6,
			// +x side (left)
			8,11,9,
			11,10,9,
			// -x side (right)
			12,13,15,
			15,13,14,
			// top
			16,19,17,
			19,18,17,
			// bottom
			20,21,23,
			23,21,22
	};
	private static float half = 0.5f;
	private static final float[] texture = { // texture coords
			// back
			0,half,
			0,1,
			half,1,
			half,half,
			// front
			0,half,
			0,1,
			half,1,
			half,half,
			// side
			1,0,
			1,half,
			half,half,
			half,0,
			//side
			1,0,
			1,half,
			half,half,
			half,0,
			// top
			0,0,
			0,half,
			half,half,
			half,0,
			// bottom
			half,half,
			half,1,
			1,1,
			1,half
	};
	private static final float[] normals = {
		// back
		0, 0, -1,
		0, 0, -1,
		0, 0, -1,
		0, 0, -1,
		// front
		0, 0, 1,
		0, 0, 1,
		0, 0, 1,
		0, 0, 1,
		// right
		1, 0, 0,
		1, 0, 0,
		1, 0, 0,
		1, 0, 0,
		// left
		-1, 0, 0,
		-1, 0, 0,
		-1, 0, 0,
		-1, 0, 0,
		// top
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		// bottom 
		0, -1, 0,
		0, -1, 0,
		0, -1, 0,
		0, -1, 0,
	};
	
	private Entity entity;
	private static RawModel rModel = null; // block model, shared across all blocks
	private static Map<String, TexturedModel> map = new HashMap<String, TexturedModel>();
	
	public Block(Loader loader, String type, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		TexturedModel tModel = getTexturedModel(loader, type);
		entity = new Entity(tModel, position, rotX, rotY, rotZ, scale);
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public static TexturedModel getTexturedModel(Loader loader, String type) {
		if (rModel == null) {
			rModel = loader.loadToVAO(vertices, texture, normals, indices);
		}
		
		if (!map.containsKey(type)) {
			ModelTexture texture =  new ModelTexture(loader.loadTexture(type));
			TexturedModel tModel = new TexturedModel(rModel, texture);
			map.put(type, tModel);
		}
		return map.get(type);
	}

}
