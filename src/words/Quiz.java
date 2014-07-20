/**
 * 
 */
package words;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JLabel;

/**
 * @author Matt Howard
 *
 */
public class Quiz implements WordScorer {

	//the fraction of letters created that are purposely meant to fit in the answer
	private final double targetedRate = .5;
	
	private ArrayList<String[]> questions;
	
	private JLabel questionBox = null;

	private String[] currQuestion;
	
	/**
	 * @param filename The filename of a question/answer file.
	 * @throws IOException 
	 */
	public Quiz(String filename){
		if(filename == null){
			throw new IllegalArgumentException();
		}
		  
		questions = new ArrayList<String[]>();
		  
		String sCurrentLine;
		  
		BufferedReader br = null;
		
		br = new BufferedReader(
			new InputStreamReader(
				Quiz.class.getClassLoader().getResourceAsStream("resources/words/"+filename)));

		try {
			while ((sCurrentLine = br.readLine()) != null) {
				  String trimmed = sCurrentLine.trim();
				  
				  int delimitPos = trimmed.indexOf(',');
				  
				  int hashPos = trimmed.indexOf('#');
				  
				  //is there a comma in the middle of the string and is this not a comment?
				  if(delimitPos > 0 && hashPos == -1){
					  String question = trimmed.substring(0, delimitPos).trim();
					  String answer = trimmed.substring(delimitPos+1,trimmed.length()).trim();
					  if(!question.isEmpty() && !answer.isEmpty()){
						  questions.add(new String[]{question,answer});
					  }
				  }
			  }
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see words.WordScorer#getLetter()
	 */
	@Override
	public char getLetter(String word) {
		//output either a letter in the word or a random letter
		if(Math.random()<targetedRate){
			//the "random" letter is most likely to be the next in the sequence
			//the probability distribution is quadratic
			int[] letterProbs = new int[currQuestion[1].length()];
			int probTotal = 0;
			//max probability numerator so that the min is 1
			int sqrtMax = Math.max(currQuestion[1].length()-word.length(), word.length()+1);
			//create distribution
			for(int i=0; i<currQuestion[1].length();i++){
				probTotal += letterProbs[i] = (sqrtMax-Math.abs(word.length()-i))*(sqrtMax-Math.abs(word.length()-i));
			}
			
			double rand = Math.random();
			int i = 0;
			double cumProb = 0;
			//Find the letter that is (rand) of the way into the distribution
			//I.e. picked a random letter from the weighted distribution
			while(cumProb < rand && i < currQuestion[1].length()){
				cumProb += (double)letterProbs[i]/probTotal;
				i++;
			}
			return Character.toLowerCase(currQuestion[1].charAt(i-1));
		}
		else{
			return (char)('a'+(int)(26*Math.random()));
		}
	}

	/* (non-Javadoc)
	 * @see words.WordScorer#scoreWord(java.lang.String)
	 */
	@Override
	public int scoreWord(String word) {
		if(isWord(word)){
			return 10*word.length();
		}
		else{
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see words.WordScorer#isWord(java.lang.String)
	 */
	@Override
	public boolean isWord(String word) {
		return word.equalsIgnoreCase(currQuestion[1]);
	}

	/* (non-Javadoc)
	 * @see words.WordScorer#getPenalty()
	 */
	@Override
	public int getPenalty() {
		return 10;
	}

	/* (non-Javadoc)
	 * @see words.WordScorer#resetWord()
	 */
	@Override
	public void resetWord() {
		currQuestion = questions.get((int)(Math.random()*questions.size()));
		if(questionBox != null){
			questionBox.setText(currQuestion[0]);
		}
	}
	
	public JLabel getQuestionBox() {
		return questionBox;
	}

	public void setQuestionBox(JLabel questionBox) {
		this.questionBox = questionBox;
		
		if(questionBox != null){
			questionBox.setText(currQuestion[0]);
		}
	}
}
