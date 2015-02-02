package tokens;

public class TokenBool extends Token{		
	private boolean bool;
		
	public TokenBool(boolean bool, int position,int line) {
		super(position,line);
		this.bool=bool;
	}
	
	public boolean getBool() {
		return bool;
	}
}