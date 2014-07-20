/**
 * 
 */
package events;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Matt Howard
 *
 */
public class KeyHoldListener extends KeyAdapter {

	private HashMap<Integer,KeyControlled> events;
	
	private HashSet<Integer> keysPressed;
	
	/**
	 * Creates a KeyListener just for held-down keys
	 */
	public KeyHoldListener() {
		events = new HashMap<Integer,KeyControlled>();
		keysPressed = new HashSet<Integer>();
	}
	
	public void KeyPressed(KeyEvent e){
		keysPressed.add(e.getKeyCode());
	}
	
	public void KeyReleased(KeyEvent e){
		keysPressed.remove(e.getKeyCode());
	}
	
	public void addHandlerObj(KeyControlled obj){
		for(int keyCode: obj.getKeys()){
			events.put(keyCode, obj);
		}
	}
	
	/**
	 * @param interval Number of milliseconds passed since last step
	 */
	public void step(int interval){
		for(int keyCode: keysPressed){
			KeyControlled obj = events.get(keyCode);
			if(obj!=null){
				obj.handleKey(keyCode, interval);
			}
		}
	}

}
