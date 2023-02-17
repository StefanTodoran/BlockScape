package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0, 0, 0);
	private Vector3f velocity = new Vector3f(0, 0, 0);
	
	private float moveSpeed = 0.05f;
	private float velFalloff = 0.96f;
	private float turnSpeed = 0.1f; // TODO: make a setting for this
	
	private float pitch; // up down rotation, or x rotation
	private float yaw; // left right rotation, or y rotation
	private float roll; // tilt of camera, or z rotation
	
	public Camera(Vector3f position, Vector3f velocity) {
		super();
		this.position = position;
		this.velocity = velocity;
	}
	
	public void update() {
		pitch += -Mouse.getDY() * turnSpeed;
		yaw += Mouse.getDX() * turnSpeed;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			// position.z -= moveSpeed;
			velocity.z = -1 * moveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			// position.x -= moveSpeed;
			velocity.x = -1 * moveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			// position.z += moveSpeed;
			velocity.z = moveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			// position.x += moveSpeed;
			velocity.x = moveSpeed;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			// position.y += moveSpeed;
			velocity.y = moveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			// position.y -= moveSpeed;
			velocity.y = -1 * moveSpeed;
		}
		
		float dx = (float) -(velocity.z * Math.sin(Math.toRadians(yaw)));
		float dz = (float) (velocity.z * Math.cos(Math.toRadians(yaw)));
		dx += (float) (velocity.x * Math.cos(Math.toRadians(yaw)));
		dz += (float) (velocity.x * Math.sin(Math.toRadians(yaw)));
		
		Vector3f.add(position, new Vector3f(dx, 0, dz), position);
		Vector3f.add(position, new Vector3f(0, velocity.y, 0), position);
		
//		Vector3f.add(position, velocity, position);
		velocity.scale(velFalloff);
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

}
