package game;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import words.Freestyle;
import words.Quiz;
import words.WordScorer;

@SuppressWarnings("serial")
public class NewGameSelector extends JFrame {

	private enum PlayMode {
		FREESTYLE("Freestyle","Try to spell out words. Longer words with less common letters get more points."),
		CAPITALS("State Capitals","You're given U.S. states. Spell out the capital, skipping spaces."),
		ELEMENTS("Chemical Elements","You're given atomic symbols. Spell out the element name."),
		SUPERBOWLS("Super Bowl Winners","<html>You're given a Super Bowl number in Roman numerals.<br>Spell out the winning city or region without no spaces.");
				
		final public String name;
		final public String description;
		
		PlayMode(String name, String description){
			this.name = name;
			this.description = description;
		}
		
		public String toString(){
			return name;
		}
		
		public WordScorer getScorer(){
			WordScorer scorer = null;
			switch(this){
				case CAPITALS:
					scorer = new Quiz("capitals.txt");
					break;
				case ELEMENTS:
					scorer = new Quiz("elements.txt");
					break;
				case SUPERBOWLS:
					scorer = new Quiz("superbowls.txt");
					break;
				default:
				case FREESTYLE:
					scorer = new Freestyle();
				
			}
			return scorer;
		}
	}
	
	public NewGameSelector(final SelectorListener run) throws HeadlessException {		
		super("New Scramble Game");
		
		final PlayMode[] modes = { PlayMode.FREESTYLE, PlayMode.CAPITALS, PlayMode.ELEMENTS, PlayMode.SUPERBOWLS};
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		final JLabel description = new JLabel(PlayMode.FREESTYLE.description);
		description.setBorder(new EmptyBorder(2, 2, 2, 2));
		description.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel selectLine = new JPanel();
		selectLine.setLayout(new BoxLayout(selectLine, BoxLayout.LINE_AXIS));
		selectLine.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JComboBox<PlayMode> select = new JComboBox<PlayMode>(modes);
		select.setSelectedIndex(0);
		select.setPreferredSize(new Dimension(200,20));
		select.setMaximumSize( select.getPreferredSize() );
		
		class CheckListener implements ActionListener{			
			private PlayMode curr;
			
			public CheckListener(){
				super();
				curr = modes[0];
			}
			
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent arg0) {
				curr = (PlayMode) ((JComboBox<PlayMode>)arg0.getSource()).getSelectedItem();
				description.setText(curr.description);
			}
			
			public PlayMode getMode(){
				return curr;
			}
		}
		
		final CheckListener listener = new CheckListener();
		
		select.addActionListener(listener);
		
		JButton button = new JButton("Submit");
		
		final JFrame frame = this;
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				run.startGame(listener.getMode().getScorer());
				frame.dispose();
			}
		});
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		frame.getRootPane().setDefaultButton(button);
		
		selectLine.add(new JLabel("Select a play mode: "));
		selectLine.add(select);
		
		panel.add(Box.createVerticalGlue());
		panel.add(selectLine);
		panel.add(Box.createVerticalGlue());
		panel.add(description);
		panel.add(Box.createVerticalGlue());
		panel.add(button);
		panel.add(Box.createVerticalGlue());
		
		this.add(panel);
		this.setLocation(100, 100);
		this.setPreferredSize(new Dimension(500,150));
		this.setResizable(false);
		this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
	}

}
