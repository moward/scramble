package gphx;

/**
 * Identical to Vectors but use float values
 * Can be velocities, accelerations, etc.
 * Note, FloatVectors are immutable once constructed
 * @author Matt Howard
 *
 */
public class FloatVector {
	
	public static final FloatVector ZERO = new FloatVector(0,0);
	
	public final double x, y;
	
	public final double TOLERANCE = 0.0000001;
	
	/** Create a vector from x and y coordinates
	 * 
	 * @param x	The x-dimension of the vector
	 * @param y The y-dimension of the vector
	 */
	public FloatVector (double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public FloatVector add(FloatVector operand){
		return new FloatVector(this.x + operand.x, this.y + operand.y);
	}
	
	public FloatVector multiply(Double scalar){
		return new FloatVector(this.x * scalar, this.y * scalar);
	}
	
	/* Create a string in the format of "FloatVector (x,y)"
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "FloatVector("+x+","+y+")";
	}
	
	public boolean equals(Object o){
		if(o instanceof FloatVector){
			return (this.x - ((FloatVector) o).x) < this.TOLERANCE && (this.y - ((FloatVector) o).y) < this.TOLERANCE;
		}
		else{
			return false;
		}
	}
	
	/** Uses Math.round
	 * @return An integer vector nearest to the float vector
	 */
	public Vector toVector(){
		return new Vector((int)Math.round(x),(int)Math.round(y));
	}
}
