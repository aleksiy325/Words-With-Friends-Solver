import java.util.ArrayList;
import java.util.regex.Pattern;

public class Expr {
	String expr;
	ArrayList<Character> charlist;
	ArrayList<Integer> offsets;
	Pattern p;
	
	public Expr(String expr){
		this.expr = expr;
		charlist = new ArrayList();
		offsets = new ArrayList();
	}
	
	public Expr(String expr, ArrayList<Character> list, ArrayList<Integer> offsets){
		this.expr = expr;
		this.charlist = list;
		this.offsets = offsets;
	}
	
	public int getOffset(){
		return offsets.get(0);
	}
	
	public void addChar(char c, int offset){
		charlist.add(c);
		offsets.add(offset);
	}
	
	public void addBoundary(){
		expr = "\\b" + expr + "\\b";
	}
	
	public void compile(){
		p = Pattern.compile(expr);
	}
	

	
	public long addKey(long key, int[] primes){
		for (int i = 0; i < charlist.size(); i++){
			int c = (int) charlist.get(i) - 97;
			key *= primes[c];
		}
		return key;
	}
	
	
	public String toString(){
		String str = expr + " ";
		for (int i = 0; i < charlist.size(); i++){
			str+= charlist.get(i) + " ";
		}
		return str;
	}
	
	
	public void combine(Expr other){
		// takes 2 expressions and returns combined new one
				
		// fix combos [a-z]{0,x}[a-z]{0,y} assert x = y and change to be
		// [a-z]{x}
		int x = expr.charAt(expr.length() - 2) - 48; // x always at index
													// length - 2
		int y = other.expr.charAt(8) - 48; // y is always at index 8
		assert (x == y);
		expr = expr.substring(0, expr.length() - 4) + Integer.toString(x + 1) + "}";
		expr += other.expr.substring(10);
		charlist.addAll(other.charlist);
	}
	
	public Expr clone(){
		ArrayList<Character> clist = (ArrayList<Character>) charlist.clone();
		ArrayList<Integer>  olist = (ArrayList<Integer>) offsets.clone();
		return new Expr(expr, clist, olist);
	}

}
