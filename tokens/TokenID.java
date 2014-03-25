package tokens;

public class TokenID extends Token {

	private String idName = "<undefinied>";

	public TokenID(String idName,int position,int line) {

		super(position,line);
		this.idName =idName;

	}

	public String getIdName() {
		return idName;
	}

}
