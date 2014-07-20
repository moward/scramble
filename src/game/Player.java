/**
 * 
 */
package game;

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
public class Player extends GameObj {
	
	public static final Vector dim = new Vector(22,30);
	
	//maximum horizontal speed in pixels per second
	private final double maxSpeed = 170;
	
	//acceleration due to gravity in pixels per second^2
	//was 160
	private final double gravity = 450;
	
	//the velocity upon jumping
	//calculated to give one second of air time over a stationary surface
	//was -75
	private final double jumpVelocity = -305;
	
	//the x-acceleration the player can gain by walking
	private final double walkAccel = 1040;
	
	//the x-acceleration the player can gain in mid-air
	private final double jumpAccel = 240;
	
	//deceleration due to friction on top of platform, in pixels/s^2
	private final double frictionAccel = 940;
	
	//frame rate of walking animation in millis. Must be divisible by 24 seconds and a multiple of INTERVAL
	private final int spriteRate = 60;
	
	//direction of the player, 1=right, 0 = none, -1= left
	private int dir;
	
	//last nonzero direction of the player on the ground
	private int lastDir;
	
	//All of the sprites, right standing, then left standing, then right jumping, then left jumping
	private BufferedImage[] sprite = new BufferedImage[4];
	
	
	//time elapsed, updated each step, in millis, mod 24 seconds. Used by sprite animation
	private int elapsed = 0;
		
	/**
	 * @param pos
	 * @param dim
	 * @param vel
	 */
	public Player(Vector pos, String[] paths) {
		super(pos, dim, FloatVector.ZERO);
		
		dir = 0;
		
		lastDir = -1;
		
		if(paths.length!=4){
			throw new IllegalArgumentException();
		}
		try {
			for(int i=0; i<4; i++){
				sprite[i] = ImageIO.read(Player.class.getClassLoader().getResourceAsStream(paths[i]));
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see gphx.GameObj#draw(java.awt.Graphics)
	 */
	@Override
	public void draw(Graphics g) {
		int x = 0;
		//is player pointed left?
		if(lastDir == -1){
			x+=2;
		}
		//should player be walking
		if((getVel().x != 0 && (elapsed % (spriteRate *2)) / spriteRate == 1) || getVel().y != 0){
			x+=1;
		}
		g.drawImage(sprite[x], getPos().x, getPos().y, dim.x, dim.y, null);
	}
	
	/* (non-Javadoc)
	 * @see gphx.GameObj#step(int)
	 */
	public void step(int dt){
		double dx, dy, newXVel, newYVel;
		
		//Y-DIMENSION
		
		//The change in y is v_y * dt + 1/2 gravity * dt ^2
		dy = getVel().y*dt/1000.+gravity*dt*dt/2000000.;
		
		boolean hitPlatform = false;
		
		//newdy will only be applied if player is actually on top of a platform
		double newdy = Double.MAX_VALUE;
		
		//check if the player will hit a platform or a letterObj
		//i.e., will the path of its motion in this step pass through a platform's box
		for(GameObj go: getField().getObjects()){
			if(go instanceof Platform){
				//is the player within the width of the platform?
				//MSH: test "edge", literally, cases
				if(getExactPos().x+dim.x > go.getPos().x && getExactPos().x <= go.getPos().x+go.dim.x){
					//player is going to fall through a platform in this step or is already sitting on top of platform
					if(getExactPos().y + dim.y <= go.getPos().y && getExactPos().y + dim.y + dy >= go.getPos().y){
						hitPlatform = true;
						newdy = Math.min(go.getPos().y - dim.y - this.getExactPos().y, newdy);
					}
				}
			}
			//is there a collision with a letter?
			if(go instanceof LetterObj){
				if(this.isCollision(go)){
					getField().playSound(0);
					getField().addLetter(((LetterObj)go).getLetter());
					getField().remove(go);
				}
			}
			//is there a collision with an eraser
			if(go instanceof Eraser){
				if(this.isCollision(go)){
					//MSH, add sound effect
					getField().playSound(3);
					getField().removeLetter();
					getField().remove(go);
				}
			}
		}
		
		//X-DIMENSION
		//cap velocity
		double xAccel = dir * ((hitPlatform?walkAccel:jumpAccel));
		
		if(Math.abs(getVel().x+xAccel*dt/1000.)>maxSpeed){
			newXVel = maxSpeed*dir;
			xAccel = 0;
			dx = newXVel*dt/1000.;
		}
		else{
			newXVel = getVel().x + xAccel*dt/1000.;
			dx = getVel().x*dt/1000.+xAccel*dt*dt/2000000.;
		}
		
		if(hitPlatform){
			//if on top of platform, set position to top of platform and slow x_accel (friction)
			if(dir == 0 && dx != 0){
				newXVel = Math.max(Math.abs(newXVel)-frictionAccel*dt/1000,0)*(newXVel>0?1:-1);
			}
			newYVel = 0;
			dy = newdy;
		}
		else{
			//accelerate velocity according to gravity
			newYVel = getVel().y + gravity*dt/1000.;
		}
		
		getField();
		//keep player inside bounds of field
		if(getExactPos().x+dx < 0 || getExactPos().x+dx > GameField.FIELD_WIDTH-dim.x){
			newXVel = 0;
		}
		
		setVel(new FloatVector(newXVel,newYVel));
		
		getField();
		//limit player to bounds of field
		setPos(new FloatVector(Math.max(0.0,Math.min(getExactPos().x+dx,GameField.FIELD_WIDTH-dim.x)),
			getExactPos().y+dy));
		
		//set time increase
		elapsed = (elapsed + dt) % (1000 * 24);
	}
	
	/**
	 * Make the player jump
	 * To be called by a keyboard listener
	 */
	public void jump(){
		boolean hitPlatform = false;
		
		//is the player on top of a platform?
		for(GameObj go: getField().getObjects()){
			if(go instanceof Platform){
				//is the player within the width of the platform?
				//MSH: test edge cases
				if(getPos().x + dim.x > go.getPos().x && getPos().x <= go.getPos().x + go.dim.x
					&& this.getPos().y + dim.y == go.getPos().y){
					hitPlatform = true;
				}
			}
		}
		
		if(hitPlatform){
			setVel(getVel().add(new FloatVector(0, this.jumpVelocity)));
		}		
		
	}

	/** Set the Player walking in one particular direction
	 * @param 1 if right, -1 is left, 0 is not walking (slowing down)
	 */
	public void walk(int dir){
		if(dir==1||dir==-1||dir==0){
			this.dir = dir;
			//is there an non-zero direction?
			if(dir!=0){
				this.lastDir = dir;
			}
		}
		else{
			throw new IllegalArgumentException();
		}
	}
}
