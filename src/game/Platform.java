package game;

import java.awt.Color;
import java.awt.Graphics;

import gphx.FloatVector;
import gphx.GameObj;
import gphx.Vector;

/**
 * @author Matt Howard
 * 
 * Draws a platform that is (int width) wide and 4 tall.
 * Dimensions include a 1 pixel border
 */
public class Platform extends GameObj {
	public static final Color fillColor = new Color(129,200,99);
	
	public static final Color strokeColor = new Color(92,64,80);

	public Platform(Vector pos, int width) {
		super(pos, new Vector(width, 4), FloatVector.ZERO);
	}

	@Override
	public void draw(Graphics g) {
		Vector pos = getPos();
		
		g.setColor(fillColor);
		
		//draw border
		g.fillRect(pos.x, pos.y, dim.x, dim.y-1);
		
		g.setColor(strokeColor);
		
		//fill in
		g.fillRect(pos.x, pos.y + dim.y - 1, dim.x, 1);
	}

}
