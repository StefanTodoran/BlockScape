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
    
    public Vector3f toVector() {
    	return new Vector3f(x, y, z);
    }

    @Override
	public int hashCode() {
    	// TODO: Determine the best hash code function for performance.
    	// return (x << 10 ^ y << 5 ^ z);
		return Objects.hash(x, y, z);
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
}