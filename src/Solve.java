import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solve {
	int[][] board;
	char[] letters;
	HashMap<Integer, Word> wordlist;
	final int rows = 15;
	final int DL = 1;
	final int TL = 2;
	final int DW = 3;
	final int TW = 4;
	final int[] points = {1, 4, 4, 2, 1, 4, 3, 3, 1, 10, 5, 2, 4, 2, 1, 4, 10, 1, 1, 1, 2, 5, 4, 8, 3, 10};

	public Solve() {
		board = new int[rows][rows];
	}

	public void solve() {
		ArrayList<Word> validwords = new ArrayList();
		// for all rows
		for (int i = 0; i < rows; i++) {
			validwords.addAll(findValidWords(board[i], i , "A"));
		}
		// for all cols
		for (int i = 0; i < rows; i++) {
			int[] col = new int[rows];
			for(int j = 0; j < rows; j++){
				col[j] = board[j][i];
			}
			validwords.addAll(findValidWords( col , i , "D"));
		}
		//test 
		ArrayList<Word> testedwords = new ArrayList();
		int[][] testboard = new int[rows][rows];
		for (Word word: validwords){
			for (int i = 0; i < rows; i++){ // copy board 
				testboard[i] = board[i].clone();
			}
			
			int p = addWordtoBoard(word.x, word.y, word.dir, word.word, testboard);
			if(validateBoard(testboard)){
				word.score = p;
				testedwords.add(word);
			}
		}
		
		//sort by points and print soln
		Collections.sort(testedwords);
		for( Word cur: testedwords){
			System.out.println(cur);
		}
		
		
		
	}
	
	
	public boolean validateBoard(int[][] board){
		//check every word on board against wordlist
		ArrayList<String> words = new ArrayList();
		// for all rows
		for (int i = 0; i < rows; i++) {
			words.addAll(extractWords(board[i]));
		}
		// for all cols
		for (int i = 0; i < rows; i++) {
			int[] col = new int[rows];
			for(int j = 0; j < rows; j++){
				col[j] = board[j][i];
			}
			words.addAll(extractWords(col));
		}
		
		//check if words are valid
		for (String word: words){
			int key = word.hashCode();
			Word cur = wordlist.get(key);
			if(cur == null){
				return false;
			}
			if(!cur.word.equals(word)){
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<String> extractWords(int[] arr){
		ArrayList<String> words = new ArrayList<String>();
		String str = "";
		boolean series = false;
		
		for( int i = 0; i < arr.length; i++){
			int c = arr[i];
			if (c > 96 && c < 123) { // ischar
				str += (char) c;
				series = true;
			}else{
				if(series && str.length() > 1){
					words.add(str);					
				}
				series = false;
				str = "";
			}
		}
		if(series && str.length() > 1){
			words.add(str);
		}
		return words;		
	}
	
	
	
	public ArrayList<Word> findValidWords(int[] arr , int index, String dir) {
		// TODO Multiple matches of same string?
		ArrayList<Word> words = new ArrayList();
		ArrayList<Expr> exprs = buildReg(arr);
		for (int i = 0; i < exprs.size(); i++) { // compile patterns
			exprs.get(i).compile();
		}
		// search
		for (Word word : wordlist.values()) {
			Matcher m;
			for (int j = 0; j < exprs.size(); j++) { // for all expressions
				Expr expr = exprs.get(j);
				m = expr.p.matcher(word.word);
				if (m.find()  && word.canSpell(expr, letters)) {
					Word cur = word.clone();
					int offset = expr.getOffset() - m.start(1); // calculate offset 
					cur.setPos(index, offset, dir);
					words.add(cur);
				
				}
			}
		}
		return words;
	}

	public ArrayList buildReg(int[] arr) {
		ArrayList<Expr> regs = new ArrayList();
		Expr reg = new Expr("");
		int bcount = 0;
		boolean series = false;
		boolean hitchar = false;
		for (int i = 0; i < arr.length; i++) {
			int c = arr[i];
			if (c > 96 && c < 123) { // ischar
				if (series) { // series of chars
					reg.expr += (char) c;
					reg.addChar((char) c, i);
				} else {
					if (hitchar && bcount > 1) {// need to make new reg
												// expression
						reg.expr += "[a-z]{0," + String.valueOf(bcount - 1) + "}";
						regs.add(reg); // add old to list and start new.
						reg = new Expr("[a-z]{0," + String.valueOf(bcount - 1) + "}" + "(" + (char) c + ")");
						reg.addChar((char) c, i);
						hitchar = false;

					} else {
						String b = "{";
						if(bcount != 1){ // special case of 1 blank seperator
							b+= "0,";
						}
						reg.expr += "[a-z]"+ b + String.valueOf(bcount) + "}" + "(" + (char) c + ")";
						reg.addChar((char) c, i);
						series = true;
					}
				}
				hitchar = true;
				bcount = 0;

			} else { // blanks
				series = false;
				bcount += 1;
			}
		}
		reg.expr += "[a-z]{0," + String.valueOf(bcount) + "}";
		if (!reg.expr.equals("[a-z]{0," + String.valueOf(rows) +  "}")){// ignore full blanks
			regs.add(reg); // add final blanks and add to list 
		}

		return regCombos(regs);
	}


	public ArrayList<Expr> regCombos(ArrayList<Expr> list) {
		ArrayList<Expr> ret = new ArrayList();
		int size = list.size();
		Expr reg;
		for (int i = 0; i < size; i++) {
			reg = list.get(i);
			Expr other = reg.clone();
			other.addBoundary();
			ret.add(other);
			for (int j = i + 1; j < size; j++) {
				reg.combine(list.get(j));
				other = reg.clone();
				other.addBoundary();
				ret.add(other);
			}
		}
		return ret;
	}
	
	

	public void Test() {
		solve();
		//Word test = new Word("near");
		//Expr expr = new Expr("test");
		//boolean b = test.canSpell(expr, letters);
		//System.out.println(b);

	}


	public void initBoard() {
		// TODO initialize Bonus points
		//place double letters
		int x = 0;
		int y = 1;
		for (int i = 0; i < rows/4; i++){
			x += 2;
			y += i;
			placeMir(x,y,DL);
		}
		
		//place triple letters
		placeMir(6,0,TL);
		placeMir(5,5,TL);
		placeMir(3,3,TL);
		
		//place double words
		placeMir(5,1,DW);
		placeMir(7,3,DW);
		
		//place triple words
		placeMir(4,0,TW);
	}
	
	public void placeMir(int y, int x, int val){
		place(x,y,val);
		place(y,x,val);
	}
	
	public void place(int y, int x, int val) {
		board[y][x] = val;
		board[y][rows - 1 - x] = val;
		board[rows - 1 - y][x] = val;
		board[rows - 1 - y][rows - 1 - x] = val;
	}

	public void loadWords(String name) {
		try {
			System.out.println("Loading wordlist from " + name);
			File file = new File(name);
			Scanner in = new Scanner(file);
			wordlist = new HashMap<Integer, Word>();
			String word = "";
			
			while (in.hasNext()) {
				word = in.next();
				Word cur = new Word(word);
				wordlist.put(word.hashCode(), cur);
			}
		} catch (Exception e) {
			System.out.println("Failed to open wordlist:" + e.getMessage());
		}
	}

	public void loadBoardFile(String name) {
		try {
			initBoard();
			System.out.println("Loading board from " + name);
			File file = new File(name);
			Scanner in = new Scanner(file);
			int words = in.nextInt();
			
			for (int i = 0; i < words; i++) {
				int x = in.nextInt();
				int y = in.nextInt();
				String dir = in.next();
				String word = in.next();
				addWordtoBoard(x, y, dir, word, board);
			}
			
			int ltrs = in.nextInt();
			letters = new char[ltrs];
			for (int i = 0; i < ltrs; i++) {
				char c = in.next().charAt(0);
				letters[i] = c;
			}
			
			if(!validateBoard(board)){
				System.out.println("Input board is not valid.");
				System.exit(0);
			}
			
			
		} catch (Exception e) {
			System.out.println("Failed to load board file. " + e.getMessage());
		}
	}

	public int addWordtoBoard(int x, int y, String dir, String word, int[][] board) {
		//also calc point score
		int len = word.length();
		int mod = 1;
		int p = 0;
		int c;
	
		for (int i = 0; i < len; i++) {
			c = (int) word.charAt(i);
			int val;

			val = dir.equals("A") ? board[y][x + i] : board[y+i][x];
			
			switch (val){ // handle bonus points
				case DL: p += (points[c-97] * 2);
					break;
				case TL: p += (points[c-97] * 3);
					break;
				case DW: p += points[c-97];
					mod *= 2;
					break;
				case TW: p += points[c-97];
					mod *= 3;
					break;
				default: p += points[c-97];
					break;
			}
			
			
			if (dir.equals("A")){
				board[y][x + i] =  c;
			}else{
				board[y+i][x] = c;
			}
		}
		
		return p * mod;
	}
	
	

	public void printBoard(int[][] board) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < rows; j++) {
				char str = '_';
				if (board[i][j] != 0) {
					if ( board[i][j] < 10){
						str = (char) (board[i][j] + 48);
					}else{
						str = (char) board[i][j];
					}
					
				}
				System.out.print(str + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void main(String args[]) {
		Solve sol = new Solve();
		sol.loadWords("wwfwordlist.txt");
		sol.loadBoardFile(args[0]);
		sol.printBoard(sol.board);
		sol.solve();
	}
}
