package entities;

import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import textures.ModelTexture;

public class Block {
	
	private final float[] vertices = {			
			-0.5f,0.5f,-0.5f,	
			-0.5f,-0.5f,-0.5f,	
			0.5f,-0.5f,-0.5f,	
			0.5f,0.5f,-0.5f,		
			
			-0.5f,0.5f,0.5f,	
			-0.5f,-0.5f,0.5f,	
			0.5f,-0.5f,0.5f,	
			0.5f,0.5f,0.5f,
			
			0.5f,0.5f,-0.5f,	
			0.5f,-0.5f,-0.5f,	
			0.5f,-0.5f,0.5f,	
			0.5f,0.5f,0.5f,
			
			-0.5f,0.5f,-0.5f,	
			-0.5f,-0.5f,-0.5f,	
			-0.5f,-0.5f,0.5f,	
			-0.5f,0.5f,0.5f,
			
			-0.5f,0.5f,0.5f,
			-0.5f,0.5f,-0.5f,
			0.5f,0.5f,-0.5f,
			0.5f,0.5f,0.5f,
			
			-0.5f,-0.5f,0.5f,
			-0.5f,-0.5f,-0.5f,
			0.5f,-0.5f,-0.5f,
			0.5f,-0.5f,0.5f	
	};
	private float half = 0.5f;
	private final float[] texture = { // texture coords
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
			0,half,
			0,1,
			half,1,
			half,half,
			//side
			0,half,
			0,1,
			half,1,
			half,half,
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
	private final int[] indices = {
			// back
			0,1,3,
			3,1,2,
			// front
			4,5,7,
			7,5,6,
			// side
			8,9,11,
			11,9,10,
			//side
			12,13,15,
			15,13,14,	
			// top
			16,17,19,
			19,17,18,
			// bottom
			20,21,23,
			23,21,22
	};
	private final float[] normals = {
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
	
	public Block(Loader loader, String type, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		RawModel rModel = loader.loadToVAO(vertices, texture, normals, indices);
		ModelTexture texture =  new ModelTexture(loader.loadTexture(type));
		TexturedModel tModel = new TexturedModel(rModel, texture);
		
		entity = new Entity(tModel, position, rotX, rotY, rotZ, scale);
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
