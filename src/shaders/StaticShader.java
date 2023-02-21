package shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;
import world.Camera;
import world.Light;

public class StaticShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

	private int locTransformMatrix;
	private int locProjectMatrix;
	private int locViewMatrix;
	private int locLightPos;
	private int locLightColor;
	private int locShineDamper;
	private int locReflectivity;
	private int locSkyColor;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texture");
		super.bindAttribute(2, "normal");
	}
	
	@Override
	protected void getAllUniformLocations() {
		// The string argument is the name in the GLSL code.
		locTransformMatrix = super.getUniformLocation("transformMatrix");
		locProjectMatrix = super.getUniformLocation("projectMatrix");
		locViewMatrix = super.getUniformLocation("viewMatrix");

		locLightPos = super.getUniformLocation("lightPosition");
		locLightColor = super.getUniformLocation("lightColor");
		locShineDamper = super.getUniformLocation("shineDamper");
		locReflectivity = super.getUniformLocation("reflectivity");
		locSkyColor = super.getUniformLocation("skyColor");
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
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(locShineDamper, damper);
		super.loadFloat(locReflectivity, reflectivity);
	}
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(locSkyColor, new Vector3f(r, g, b));
	}

}
