package gphx;

import game.Eraser;
import game.LetterObj;
import game.Platform;
import game.Player;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Timer;

import words.Quiz;
import words.WordScorer;

@SuppressWarnings("serial")
public class GameField extends javax.swing.JPanel {
	//Game constants
	public static final int FIELD_WIDTH = 600;
	public static final int FIELD_HEIGHT = 470;
	
	//How often do we wait for a timer?
	public final int INTERVAL = 30;
	
	public final Color BG_COLOR = new Color(194,232,225);
	
	//The min and max number of milliseconds that should pass between new letters
	public final int NEW_LETTER_MIN = 600;
	public final int NEW_LETTER_MAX = 1200;
	
	//The min and max number of milliseconds that should pass between new erasers
	public final int NEW_ERASER_MIN = 3000;
	public final int NEW_ERASER_MAX = 10000;
	
	//bounds on the letter's position
	public final int GO_POS_MIN = 20;
	public final int GO_POS_MAX = 430;
	
	//velocity for letters and erasers
	public final FloatVector GO_VEL = new FloatVector(100,0);
	
	//time for one game, in seconds
	public final int GAME_TIME = 240;
	
	public final String[] SOUND_PATHS = new String[]{
		"resources/audio/coin.wav",
		"resources/audio/correct.wav",
		"resources/audio/wrong.wav",
		"resources/audio/eraser.wav",
		"resources/audio/gameover.wav"};
	
	//a list of all the objects on the field
	private LinkedHashSet<GameObj> objs;
	
	//Instance variables:
	private StringBuilder wordBuilder = new StringBuilder();
	
	private int score = 0;
	
	//A handler that is fired whenever the score is changed
	public ActionListener scoreListener = null;
	
	//A handler that is fired whenever the word is changed
	public ActionListener wordListener = null;

	private WordScorer scorer;
	
	//Is the game paused?
	private boolean paused = true;
	
	private Player p1;
	
	//time until next letter and eraser, in millis
	private int letterTimer, eraserTimer;
	
	private Clip[] soundClips;
	
	private boolean muted = false;
	
	private JLabel utilLabel = null;
	
	//time remaining in game, millis
	private int gameClock = 0;
	
	private JLabel clockLabel;
	
	public GameField(WordScorer scorer){
		//create set of objects
		objs = new LinkedHashSet<GameObj>();
				
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
		//load sound effects
		AudioInputStream audio;
		soundClips = new Clip[SOUND_PATHS.length];
		try {
			for(int i=0; i < SOUND_PATHS.length; i++){
				audio = AudioSystem.getAudioInputStream(new BufferedInputStream(GameField.class.getClassLoader().getResourceAsStream(SOUND_PATHS[i])));
				soundClips[i] = AudioSystem.getClip();
				soundClips[i].open(audio);
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
			e1.printStackTrace();
		}
		
		//set timer
		Timer timer = new Timer(INTERVAL, new ActionListener(){
			private long lastTick = System.currentTimeMillis();
			
			public void actionPerformed(ActionEvent e){
				long now = System.currentTimeMillis();
				tick((int)(now-lastTick));
				lastTick = now;
			}
		});
			
		timer.start();
		
		setFocusable(true);
		
		this.scorer = scorer;
		
		//will set field later
		if(scorer != null){
			setField();
		}
	}

	/** Clear
	 */
	public void clear() {
		objs.clear();
	}

    /**
     * This method is called every time the timer defined
     * in the constructor triggers.
     * 
     * @param The elapsed in milliseconds
     */
	private void tick(int dt){
		if(inGame()&&!isPaused()){
			gameClock -= dt;
			if(gameClock <= 0){
				gameClock = 0;
				utilLabel.setText("Game Over!");
				playSound(4);
			}
			
			if((gameClock + dt)/1000 != gameClock/1000 && clockLabel != null){
				clockLabel.setText("Time: "+(gameClock/1000));
			}
			
			//should we create a new letter and/or eraser?
			letterTimer -= dt;
			
			eraserTimer -= dt;
			
			if(letterTimer <= 0){
				this.add(new LetterObj(
					new Vector(-30,(int)(Math.random()*(GO_POS_MAX-GO_POS_MIN))),
					GO_VEL,
					scorer.getLetter(getWord())));
				letterTimer = (int)(Math.random()*(NEW_LETTER_MAX-NEW_LETTER_MIN))+NEW_LETTER_MIN;
			}
			
			if(eraserTimer <= 0){
				this.add(new Eraser(
					new Vector(-30,(int)(Math.random()*(GO_POS_MAX-GO_POS_MIN))),
					GO_VEL));
				eraserTimer = (int)(Math.random()*(NEW_ERASER_MAX-NEW_ERASER_MIN))+NEW_ERASER_MIN;
			}
			
			//step each game object
			for(GameObj gameObj: getObjects()){
				gameObj.step(dt);
			}
			
			// update the display
			repaint();
		}
	}
	

	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//draw background
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, FIELD_WIDTH, FIELD_HEIGHT);
		
		for(GameObj go: objs){
			go.draw(g);
		}
	}

	@Override
	public Dimension getPreferredSize(){
		return new Dimension(FIELD_WIDTH,FIELD_HEIGHT);
	}
	
	public void add(GameObj go){
		go.remove();
		objs.add(go);
		go.setField(this);
	}

	/** 
	 * @param go The game object to be removed
	 */
	public void remove(GameObj go) {
		if(objs.remove(go)){	
			go.remove();
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	public LinkedHashSet<GameObj> getObjects(){
		return new LinkedHashSet<GameObj>(objs);
	}
	
	public void addLetter(char letter){
		wordBuilder.append(letter);
		
		if(wordListener != null){
			wordListener.actionPerformed(new ActionEvent(this, 0, "letter added"));
		}
	}
	
	/**
	 * Attempts to score the current word.
	 * Will add points and set off the score listener.
	 * Resets word at end
	 */
	public void submitWord(){
		if(scorer.isWord(getWord())){
			playSound(1);
			setScore(getScore()+scorer.scoreWord(getWord()));
		}
		else{
			playSound(2);
			setScore(getScore()-scorer.getPenalty());
		}
		
		newWord();
	}
	
	private void newWord(){
		wordBuilder = new StringBuilder();
		
		scorer.resetWord();
		
		if(wordListener != null){
			wordListener.actionPerformed(new ActionEvent(this, 0, "word changed"));
		}
	}
	
	/**
	 * @return The current word that is being formed
	 */
	public String getWord(){
		return wordBuilder.toString();
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		boolean same = (score == this.score);
		this.score = score;
		if(scoreListener != null && !same){
			scoreListener.actionPerformed(new ActionEvent(this, 1, "score changed"));
		}
	}
	
	public ActionListener getScoreListener() {
		return scoreListener;
	}

	public void setScoreListener(ActionListener scoreListener) {
		this.scoreListener = scoreListener;
	}
	
	public ActionListener getWordListener() {
		return wordListener;
	}

	public void setWordListener(ActionListener wordListener) {
		this.wordListener = wordListener;
	}

	/**
	 * @return the paused
	 */
	public boolean isPaused() {
		return paused & inGame();
	}
	
	/**
	 * @return Is the field in the middle of a game right now, i.e. not game over
	 */
	public boolean inGame() {
		return gameClock > 0;
	}
	

	/**
	 * @param paused the paused to set
	 */
	public void setPaused(boolean paused) {
		if(inGame()){
			this.paused = paused;
			if(paused){
				if(scorer instanceof Quiz){
					((Quiz) scorer).setQuestionBox(null);
				}
				utilLabel.setText("Paused");
			}
			else{
				utilLabel.setText("");
				if(scorer instanceof Quiz && utilLabel != null){
					((Quiz) scorer).setQuestionBox(utilLabel);
				}
			}
		}
	}
	
	/**
	 * Create the player and the bounds
	 * Called upon new game (resetField()) and upon construction
	 */
	private void setField(){
		//add the platforms
		add(new Platform(new Vector(0,466),600));
        
		add(new Platform(new Vector(0,366),150));
        
		add(new Platform(new Vector(450,366),150));
        
		add(new Platform(new Vector(250,266),100));
        
		add(new Platform(new Vector(0,166),150));
        
		add(new Platform(new Vector(450,166),150));
		
		//add the player and its four sprites
        String[] sprites = new String[]{
    		"resources/images/player0.png",
    		"resources/images/player1.png",
    		"resources/images/player2.png",
    		"resources/images/player3.png"
        };
        
        p1 = new Player(new Vector(300,80), sprites);
        
        add(p1);
        
        newWord();
        
        letterTimer = (int)(Math.random()*(NEW_LETTER_MAX-NEW_LETTER_MIN))+NEW_LETTER_MIN;
        
        eraserTimer = (int)(Math.random()*(NEW_ERASER_MAX-NEW_ERASER_MIN))+NEW_ERASER_MIN;
        
        paused = false;
        
        gameClock = GAME_TIME * 1000;
        
        if(utilLabel != null){
			if(scorer instanceof Quiz){
				((Quiz) scorer).setQuestionBox(utilLabel);
			}
			else{
				utilLabel.setText("");
			}
        }
        
        if(clockLabel != null){
        	clockLabel.setText("Time: "+(gameClock/1000));
        }
	}

	/**
	 * Creates a new game
	 */
	public void resetField(WordScorer scorer){
		objs.clear();
		
		this.scorer = scorer;
		
		setScore(0);
		
		if(scorer != null){
			setField();
		}
	}
	
	/**
	 * @return the p1
	 */
	public Player getPlayer() {
		return p1;
	}
	
	public void playSound(int i){
		if(!isMuted()){
			if (soundClips[i].isRunning()){
				soundClips[i].stop();
			}
			soundClips[i].setFramePosition(0);
			soundClips[i].start();
		}
	}

	public void removeLetter() {
		if(wordBuilder.length()>0){
			wordBuilder.deleteCharAt(wordBuilder.length()-1);
		}
		if(wordListener != null){
			wordListener.actionPerformed(new ActionEvent(this, 0, "letter removed"));
		}
	}

	/**
	 * @return the muted
	 */
	public boolean isMuted() {
		return muted;
	}

	/**
	 * @param muted the muted to set
	 */
	public void setMuted(boolean muted) {
		this.muted = muted;
		//stop all current sound clips
		if(muted){
			for(Clip c: soundClips){
				c.stop();
			}
		}
	}

	/**
	 * @param utilLabel the utilLabel to set
	 */
	public void setUtilLabel(JLabel utilLabel) {
		this.utilLabel = utilLabel;
		if(scorer instanceof Quiz && utilLabel != null){
			((Quiz) scorer).setQuestionBox(utilLabel);
		}
	}

	/**
	 * @return the clockLabel
	 */
	public JLabel getClockLabel() {
		return clockLabel;
	}

	/**
	 * @param clockLabel the clockLabel to set
	 */
	public void setClockLabel(JLabel clockLabel) {
		this.clockLabel = clockLabel;
		
		if((gameClock + INTERVAL)/1000 != gameClock/1000 && clockLabel != null){
			clockLabel.setText("Time: "+(gameClock/1000));
		}
	}
}
