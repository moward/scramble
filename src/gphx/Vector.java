package gphx;

/**
 * Vector can either be a point on the screen or a dimension
 * Note, Vectors are immutable once constructed
 * 
 * @author Matt Howard
 *
 */
public class Vector {
	public final static Vector ZERO = new Vector(0,0);
	
	public final int x, y;
	
	/** Create a vector from x and y coordinates
	 * 
	 * @param x	The x-dimension of the vector
	 * @param y The y-dimension of the vector
	 */
	public Vector (int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Vector add(Vector operand){
		return new Vector(this.x + operand.x, this.y + operand.y);
	}

	public Vector multiply(int scalar){
		return new Vector(this.x * scalar, this.y * scalar);
	}
	
	/* Create a string in the format of "Vector (x,y)"
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "Vector("+x+","+y+")";
	}
	
	public boolean equals(Object o){
		if(o instanceof Vector){
			return this.x == ((Vector) o).x && this.y == ((Vector) o).y;
		}
		else{
			return false;
		}
	}
	
	public FloatVector toFloatVector(){
		return new FloatVector(x,y);
	}
	
}
