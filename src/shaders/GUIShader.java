package shaders;

import org.lwjgl.util.vector.Matrix4f;

public class GUIShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "src/shaders/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/guiFragmentShader.txt";
	
	private int loctransformMatrix;

	public GUIShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformMatrix(Matrix4f matrix){
		super.loadMatrix(loctransformMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		loctransformMatrix = super.getUniformLocation("transformMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	
	

}