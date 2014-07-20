package events;

/**
 * Defines an interface for an object that may be controlled by KeyListener
 * First, KeyListener requests the keys (via getKeys) that an object requests calls for
 * Then, each time the key is held down, the event's handleKey method is called
 * 
 * @author Matt Howard
 *
 */
public interface KeyControlled {
	/**
	 * @return a list of KeyCodes that a particular event will handle
	 */
	public int[] getKeys();
	
	/**
	 * @param keyCode The key code of the key that just fired
	 * @param interval 
	 */
	public void handleKey(int keyCode, int interval);
}
