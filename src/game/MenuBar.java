package game;

import gphx.GameField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import words.WordScorer;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	public MenuBar(final JFrame frame, final GameField field, final Game run) {
		super();
		
		JMenuItem item;
		JMenu menu = new JMenu("Game");
		
		item = new JMenuItem("New Game");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				field.setPaused(true);
				frame.setVisible(false);
				new NewGameSelector(new SelectorListener(){
					@Override
					public void startGame(WordScorer scorer) {
						field.resetField(scorer);
						frame.setVisible(true);
					}
				});
				
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
		menu.add(item);
		
		item = new JMenuItem("Pause");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				field.setPaused(!field.isPaused());
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
		menu.add(item);
		
		item = new JCheckBoxMenuItem("Muted");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				field.setMuted(((JCheckBoxMenuItem)arg0.getSource()).isSelected());
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Exit");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				System.exit(0);
			}
		});
		item.setMnemonic(KeyEvent.VK_F4);
		menu.add(item);
		
		this.add(menu);
		
		menu = new JMenu("Help");
		
		item = new JMenuItem("Help Contents");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				field.setPaused(true);
				run.launchHelp();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
		menu.add(item);
		
		this.add(menu);
	}

}
