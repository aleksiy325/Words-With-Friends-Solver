import java.util.Arrays;

public class Word implements Comparable<Word> {
	public String word;
	public int x;
	public int y;
	public String dir;
	public int score;
	
	
	public Word(String word){
		this.word = word;
		score = 0;
	}

	
	public void setPos(int index, int offset, String dir){
		this.dir = dir;
		if(dir.equals("A")){ // across
			y = index;
			x = offset;
		}else{ // down
			y = offset;
			x = index;
		}
	}
	
	public String toString(){
		return(x + " " + y + " " + dir+ " " + word + " " + score);
	}
	
	public boolean canSpell(Expr expr, char[] letters){
		//TODO add blanks
		int[] wordarr = new int[26];
		int[] lettarr = new int[26];
		
		for (int i = 0; i < expr.charlist.size(); i++){ // expr letters
			int c = (int) expr.charlist.get(i) - 97;
			lettarr[c] += 1;
		}
		for ( int i = 0; i < letters.length; i++){ // letters
			int c = (int) letters[i] - 97;
			lettarr[c] += 1;
		}
		for ( int i = 0; i < word.length(); i++){ // word letters
			int c = (int) word.charAt(i) - 97;
			wordarr[c] += 1;
		}
		for (int  i = 0; i < wordarr.length; i++){ //check 
			if(wordarr[i] > lettarr[i]){
				return false;
			}
		}
		
		return true;
	}
	
	public Word clone(){
		return new Word(this.word);
	}
	
	@Override
	public int compareTo(Word other) {
		return other.score - score;
	}

}