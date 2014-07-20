package gphx;

import java.awt.Graphics;

/**
 * gameObj.java
 * The abstract class for all of the objects that appear in the game world
 * 
 */

/** 
 * @author Matt Howard
 *
 */
public abstract class GameObj {
	
	private FloatVector pos;
	
	public final Vector dim;
	
	private FloatVector vel;
	
	private GameField field;
	
	/**
	 * @param pos The position vector in pixels, referenced from the bottom-left. Can be positive or negative.
	 * @param dim The dimension of GameObj in pixels. Must be positive.
	 * @param vel The velocity of the GameObj, in pixels per second. Can be positive or negative.
	 */
	public GameObj(Vector pos, Vector dim, FloatVector vel){
		//MSH: check for negative dimensions
		this.pos = pos.toFloatVector();
		this.vel = vel;
		this.dim = dim;
		
		field = null;
	}
	
	/** To be used in general cases, i.e. detection collision or drawing
	 * @return
	 */
	public Vector getPos(){
		return pos.toVector();
	}
	
	/** To be used for precise floating-point calculations, i.e. kinematics 
	 * @return
	 */
	public FloatVector getExactPos(){
		return pos;
	}
	
	public void setPos(Vector pos){
		this.pos = pos.toFloatVector();
	}
	
	public void setPos(FloatVector pos){
		this.pos = pos;
	}

	public FloatVector getVel(){
		return vel;
	}

	public void setVel(FloatVector vel){
		this.vel = vel;
	}
	
	public abstract void draw(Graphics g);
	
	/**
	 * @param dt Number of milliseconds
	 */
	public void step(int dt){
		pos = pos.add(new FloatVector(vel.x*dt/1000., vel.y*dt/1000.));
	}
	
	/** Test whether there is a collision between two objects. Uses bounding boxes.
	 * @param go The GameObject to test
	 * @return True if there is a collision
	 */
	public boolean isCollision(GameObj go){
		Vector goPos = go.getPos();
		Vector goDim = go.dim;
		Vector[] goVertices = new Vector[]{goPos,
				goPos.add(new Vector(goDim.x,0)),
				goPos.add(goDim),
				goPos.add(new Vector(0,goDim.y))};
		
		boolean result = false;
		
		// See if any of the four vertices of the parameter GameObject
		// are inside the bounding box of the current GameObject
		for(int i = 0; !result && (i < 4); i++){
			result = pointWithin(goVertices[i]);
		}
		
		return result;
	}
	
	/** Tests if a certain point is within GameObj's bounding box
	 * @param v
	 * @return True if point is inside
	 */
	public boolean pointWithin(Vector v){
		return (v.x>=pos.x && v.x < (pos.x + dim.x)
			&& v.y>=pos.y && v.y < (pos.y + dim.y));
	}
	
	/** Warning: should be called only by GameField's add method
	 * @param field
	 */
	public void setField(GameField field){
		this.field = field;
	}
	
	/**
	 * @return The field the GameObject is on, null if not attached to any field
	 */
	public GameField getField(){
		return field;
	}
	
	/**
	 * Warning: should only be called by the field's remove(GameObject go) method
	 */
	public void remove(){
		field = null;
	}

}