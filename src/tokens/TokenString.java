package tokens;

public class TokenString extends Token {
	private String stringContent;

	public TokenString(String stringContent,int position,int line) {
		super(position,line);
		this.stringContent = stringContent;
	}

	public String getStringContent() {
		return stringContent;
	}
}