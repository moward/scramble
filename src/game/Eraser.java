package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gphx.FloatVector;
import gphx.GameObj;
import gphx.Vector;

public class Eraser extends GameObj {

	public static final Vector dim = new Vector(30,30);
	
	public static final String ERASER_IMAGE = "resources/images/eraser.png";
	
	private static BufferedImage sprite = null;
	
	public Eraser(Vector pos, FloatVector vel) {
		super(pos, dim, vel);
		
		if(sprite == null)
			try {
				sprite = ImageIO.read(Eraser.class.getClassLoader().getResourceAsStream(ERASER_IMAGE));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void draw(Graphics g) {
		if(sprite!=null){
			g.drawImage(sprite, getPos().x, getPos().y, dim.x, dim.y, null);
		}
	}

}
