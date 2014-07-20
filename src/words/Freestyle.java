package words;

import java.io.IOException;

public class Freestyle implements WordScorer {
	
	/**
	 * letterFreq is an array of doubles where 'a' corresponds
	 * to the 0th index and 'z' corresponds to the 25th index.
	 * 0.0 = never occurring, very rare
	 * 1.0 = always occurring, the only letter
	 * The sum of the array should be 1.
	 */
	private static final double[] letterFreqDefault = new double[]{
		0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015,
		0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749,
		0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758,
		0.00978, 0.0236, 0.0015, 0.01974, 0.00074};
	
	private Dictionary dict;
	private double[] letterFreq;
	
	/**
	 * Creates a word scorer. 
	 * 
	 * Uses the default letter frequencies
	 */
	public Freestyle() {
		try {
			dict = new Dictionary("resources/words/dictionary.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.letterFreq = letterFreqDefault;
	}
	
	/* (non-Javadoc)
	 * @see words.WordScorer#randLetter()
	 */
	@Override
	public char getLetter(String word){
		double rand = Math.random();
		
		//index in the letterFreq array
		int i = 0;
		//The total frequency of the number of letters cycled through so far
		double cumFreq = 0;
		
		while(cumFreq < rand && i < letterFreq.length-1){
			cumFreq += letterFreq[i];
			i++;
		}
		return (char) ('a' + i - 1);
	}
	
	/**
	 * Scoring criteria: <br/>
	 * <ul> 
	 * <li>Each letter of frequency f has a base value of <br/>
	 *  CEIL[SQRT(1/f/13)] </li>
	 * <li>The score of all the letters in the word are added </li> 
	 * <li>The length multiplier is the ceiling of the word length (1-7) divided by 3 </li> 
	 * <li>A non-word get a score of 0 </li></ol> 
	 * 
	 * @param word The word to score
	 * @return A score according to scoring guidelines
	 */
	public int scoreWord(String word){
		int baseScore = 0;
		if(dict.isWord(word)){
			for(int i=0; i < word.length(); i ++){
				char letter = word.charAt(i);
				if(!Character.isLowerCase(letter) && !Character.isUpperCase(letter)){
					throw new IllegalArgumentException();
				}
				letter = Character.toLowerCase(letter);
				baseScore += (int)Math.ceil(Math.sqrt(1/letterFreq[letter-'a']/13.0));
			}
		}
		return baseScore * (int)Math.ceil(word.length()/3.);
	}
	
	/* (non-Javadoc)
	 * @see words.WordScorer#isWord(java.lang.String)
	 */
	@Override
	public boolean isWord(String word){
		return dict.isWord(word);
	}

	@Override
	public int getPenalty() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public void resetWord() {
	}

}
