package words;

public interface WordScorer {

	/**
	 * @return A random letter rated by its frequency in the given letterFreq
	 */
	public abstract char getLetter(String word);

	public abstract int scoreWord(String word);

	public abstract boolean isWord(String word);

	public abstract int getPenalty();

	public abstract void resetWord();

}