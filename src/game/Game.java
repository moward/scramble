/**
 * 
 */
package game;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import gphx.GameField;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import words.WordScorer;

/**
 * @author Matt Howard
 *
 */
public class Game implements Runnable, SelectorListener {
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		new NewGameSelector(this);
	}
	
	public void startGame(WordScorer scorer){
		//MSH change game name
		final JFrame frame = new JFrame("Scramble");
		
		final JPanel layout = new JPanel();
		
		final GameField field = new GameField(scorer);
		
		frame.setMaximumSize(new Dimension(GameField.FIELD_WIDTH,GameField.FIELD_HEIGHT+50));
		
		frame.setMinimumSize(new Dimension(GameField.FIELD_WIDTH,GameField.FIELD_HEIGHT+50));
		
		frame.setResizable(false);
		
		frame.setJMenuBar(new MenuBar(frame, field, this));
		
		//start adding to GUI
		layout.setLayout(new BoxLayout(layout, BoxLayout.PAGE_AXIS));
		
		final JPanel statusBar = new JPanel();
		
		statusBar.setLayout(new GridLayout(0,3));
		
		statusBar.setPreferredSize(new Dimension(GameField.FIELD_WIDTH,20));
		
		final JLabel scoreLabel = new JLabel("Score: "+0);
		
		scoreLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		field.setScoreListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				scoreLabel.setText("Score: "+field.getScore());
			}
		});
		
		final JLabel wordLabel = new JLabel("",JLabel.CENTER);
		
		wordLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		//when a word changes, change the label text
		field.setWordListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				wordLabel.setText(field.getWord());
			}
		});
		
		//a label that is used to show time left in game
		JLabel timeLabel = new JLabel("0",JLabel.RIGHT);
		timeLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
		field.setClockLabel(timeLabel);
		
		//a label that is used for questions in the quiz game
		JLabel utilLabel = new JLabel("");
		utilLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
		utilLabel.setPreferredSize(new Dimension(GameField.FIELD_WIDTH,20));
		utilLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		statusBar.add(scoreLabel);
		
		statusBar.add(wordLabel);
		
		statusBar.add(timeLabel);
		
		//add the status bar
		layout.add(statusBar);
		
		layout.add(utilLabel);
		
		//create the game field
		layout.add(field);
		
		frame.add(layout);
		
        frame.setLocation(100,100);
        
        field.setUtilLabel(utilLabel);
        
        field.addKeyListener(
        	new KeyAdapter(){
        		//make sure the player only jumps once per key press
        		private boolean jumped = false;
        		
	        	public void keyPressed(KeyEvent e){
					if (e.getKeyCode() == KeyEvent.VK_W && !jumped && field.inGame() && !field.isPaused()){
						jumped = true;
						field.getPlayer().jump();
					}
					else if (e.getKeyCode() == KeyEvent.VK_A && field.inGame() && !field.isPaused())
						field.getPlayer().walk(-1);
					else if (e.getKeyCode() == KeyEvent.VK_D && field.inGame() && !field.isPaused())
						field.getPlayer().walk(1);
					else if (e.getKeyCode() == KeyEvent.VK_ENTER && field.inGame() && !field.isPaused())
						field.submitWord();
				}
				public void keyReleased(KeyEvent e){
					if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D){
						field.getPlayer().walk(0);
					}
					if (e.getKeyCode() == KeyEvent.VK_W){
					jumped = false;
					}
				}
		});
        
     // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
	}
	
	public static void main(String[] args){
        SwingUtilities.invokeLater(new Game());
    }

	public void launchHelp() {
		try {
			java.awt.Desktop.getDesktop().browse(new URI("http://moward.github.io/scramble/help/"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
