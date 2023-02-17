package shaders;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class StaticShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

	private int locTransformMatrix;
	private int locProjectMatrix;
	private int locViewMatrix;
	private int locLightPos;
	private int locLightColor;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texture");
	}
	
	@Override
	protected void getAllUniformLocations() {
		locTransformMatrix = super.getUniformLocation("transformMatrix");
		locProjectMatrix = super.getUniformLocation("projectMatrix");
		locViewMatrix = super.getUniformLocation("viewMatrix");
		locLightPos = super.getUniformLocation("locLightPos");
		locLightColor = super.getUniformLocation("locLightColor");
	}
	
	public void loadTransformMatrix(Matrix4f matrix) {
		super.loadMatrix(locTransformMatrix, matrix);
	}

	public void loadProjectMatrix(Matrix4f matrix) {
		super.loadMatrix(locProjectMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix(locViewMatrix, matrix);
	}
	
	public void loadLight(Light light) {
		super.loadVector(locLightPos, light.getPosition());
		super.loadVector(locLightColor, light.getColor());
	}

}
