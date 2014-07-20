/**
 * 
 */
package game;

import events.KeyControlled;

/**
 * @author Matt Howard
 *
 */
public class KeyboardControl implements KeyControlled {

	Player p;
	int jump, walkLeft, walkRight;
	
	/**
	 * 
	 */
	public KeyboardControl(Player p, int jump, int walkLeft, int walkRight) {
		this.p = p;
		this.jump = jump;
		this.walkLeft = walkLeft;
		this.walkRight = walkRight;
	}

	/* (non-Javadoc)
	 * @see events.KeyControlled#getKeys()
	 */
	@Override
	public int[] getKeys() {
		return new int[]{jump,walkLeft,walkRight};
	}

	/* (non-Javadoc)
	 * @see events.KeyControlled#handleKey(int)
	 */
	@Override
	public void handleKey(int keyCode, int interval) {
		
	}

}
