package tokens;

public abstract class Token {
	private int position=0;
	private int line=1;

	/*
	 * token saves position in stream for tracing purposes
	 */
	
	protected Token(int position,int line) {
		this.position=position;
		this.line=line;
	}

	public int getPosition() {
		return position;
	}

	public int getLine() {
		return line;
	}
}