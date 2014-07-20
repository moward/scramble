/**
 * 
 */
package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gphx.FloatVector;
import gphx.GameField;
import gphx.GameObj;
import gphx.Vector;

/**
 * @author Matt Howard
 *
 */
public class LetterObj extends GameObj {
	
	public static final Font f = new Font("SansSerif", Font.BOLD, 20);
	
	public static final Vector dim = new Vector(30,30);
	
	public static final String LETTER_IMAGE = "resources/images/letter-bubble.png";
	
	private char letter;
	
	private static BufferedImage sprite = null;
	
	/**
	 * @param pos The initial position of the letter object
	 * @param vel The initial velocity
	 * @param letter The letter the image contains
	 * @param letterImg
	 */
	public LetterObj(Vector pos, FloatVector vel, char letter) {
		super(pos, dim, vel);
		this.letter = letter;
		
		if(sprite == null){
			try {
				sprite = ImageIO.read(LetterObj.class.getClassLoader().getResourceAsStream(LETTER_IMAGE));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see gphx.GameObj#draw(java.awt.Graphics)
	 */
	/* (non-Javadoc)
	 * @see game.WordGenerator#draw(java.awt.Graphics)
	 */
	@Override
	public void draw(Graphics g) {
		if(sprite!=null){
			g.drawImage(sprite, getPos().x, getPos().y, dim.x, dim.y, null);
		}
		
		g.setFont(f);
		
		//get size and width
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Color.WHITE);
		g.drawString(Character.toString(Character.toUpperCase(letter)),
				getPos().x + (dim.x - fm.charWidth(Character.toUpperCase(letter)))/2 + 1,
				getPos().y + (dim.y + fm.getAscent())/2 - 3);
	}
	
	/* (non-Javadoc)
	 * @see game.WordGenerator#getLetter()
	 */
	public char getLetter(){
		return letter;
	}
	
	/* (non-Javadoc)
	 * @see game.WordGenerator#step(int)
	 */
	@Override
	public void step(int dt){
		super.step(dt);
		
		getField();
		//once letter leaves the stage, remove self to conserve memory and processing
		if(getField() != null && getPos().x > GameField.FIELD_WIDTH){
			getField().remove(this);
		}
	}

}
