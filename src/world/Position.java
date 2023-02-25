package world;

import java.util.Objects;

import org.lwjgl.util.vector.Vector3f;

public final class Position {
    
	public int x , y , z;
    
    public Position(int x , int y , int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Position(Position other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
    
    public Position(Vector3f vector){
        this.x = (int) vector.x;
        this.y = (int) vector.y;
        this.z = (int) vector.z;
    }
    
    public Position add(Position other) {
    	this.x += other.x;
    	this.y += other.y;
    	this.z += other.z;
    	return this;
    }
    
    public Position sub(Position other) {
    	this.x -= other.x;
    	this.y -= other.y;
    	this.z -= other.z;
    	return this;
    }

    public Position scale(float amount) {
    	this.x *= amount;
    	this.y *= amount;
    	this.z *= amount;
    	return this;
    }
    
    // Why tf does LWJGL not have a built in Vector3f scale??
    public static Vector3f scaleVector(Vector3f vector, float amount) {
    	return new Vector3f(vector.x * amount, vector.y * amount, vector.z * amount);
    }
    
    public Vector3f toVector() {
    	return new Vector3f(x, y, z);
    }
    
    public boolean withinDistance(Position other, int distance) {
    	return Math.abs(this.x - other.x) < distance && 
    		   Math.abs(this.y - other.y) < distance &&
    		   Math.abs(this.z - other.z) < distance;
    }

    @Override
	public int hashCode() {
    	// TODO: Determine the best hash code function for performance.
    	return (x << 10 ^ y << 5 ^ z);
//		return Objects.hash(x, y, z);
	}

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return x == other.x && y == other.y && z == other.z;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
    
}