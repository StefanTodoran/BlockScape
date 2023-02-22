package world;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0, 0, 0);
	private Vector3f velocity = new Vector3f(0, 0, 0);
	
	private float moveSpeed = 0.1f;
	private float velFalloff = 0.96f;
	private float sensitivity = 0.1f;

	private float baseSensitivity = 0.1f; // TODO: Make this a setting!
	private static final float ZOOM_SENS_DAMPER = 0.2f;
	
	private float pitch; // up down rotation, or x rotation
	private float yaw; // left right rotation, or y rotation
	private float roll; // tilt of camera, or z rotation
	private float FOV = NORMAL_FOV;

	private static final float NORMAL_FOV = 90;
	private static final float ZOOMED_FOV = 30;
	
	public Camera(Vector3f position, Vector3f velocity) {
		super();
		this.position = position;
		this.velocity = velocity;
	}
	
	public Camera(Vector3f position, Vector3f velocity, float yaw) {
		super();
		this.position = position;
		this.velocity = velocity;
		this.yaw = yaw;
	}
	
	public static final int NO_ACTION = -1;
	public static final int LEFT_CLICK = 0;
	public static final int RIGHT_CLICK = 2;
	
	private boolean mouseWasDown = false;
	
	public int doUpdateGetActions() {
		if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
			FOV = ZOOMED_FOV;
			sensitivity = baseSensitivity * ZOOM_SENS_DAMPER;
		} else {
			FOV = NORMAL_FOV;
			sensitivity = baseSensitivity;			
		}
		
		updateRotation();
		
		float curMoveSpeed = moveSpeed;
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			curMoveSpeed *= 2;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			velocity.z = -1 * curMoveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			velocity.x = -1 * curMoveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			velocity.z = curMoveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			velocity.x = curMoveSpeed;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			velocity.y = curMoveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			velocity.y = -1 * curMoveSpeed;
		}
		
		float dx = (float) -(velocity.z * Math.sin(Math.toRadians(yaw)));
		float dz = (float) (velocity.z * Math.cos(Math.toRadians(yaw)));
		dx += (float) (velocity.x * Math.cos(Math.toRadians(yaw)));
		dz += (float) (velocity.x * Math.sin(Math.toRadians(yaw)));
		
		Vector3f.add(position, new Vector3f(dx, velocity.y, dz), position);
		velocity.scale(velFalloff);
		
		if (!Mouse.isButtonDown(LEFT_CLICK) && mouseWasDown) {
			mouseWasDown = false;
			return LEFT_CLICK;
		} else if (Mouse.isButtonDown(LEFT_CLICK)) {
			mouseWasDown = true;
		}
		
		return NO_ACTION;
	}
	
	private void updateRotation() {
		pitch += -Mouse.getDY() * sensitivity;
		yaw += Mouse.getDX() * sensitivity;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getRoll() {
		return roll;
	}

	public float getFOV() {
		return FOV;
	}

}
