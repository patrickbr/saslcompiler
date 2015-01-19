package tokens;

public class TokenNum extends Token{

	private int num;


	public TokenNum(int num, int position,int line) {

		super(position,line);
		this.num=num;

	}

	public int getNum() {
		return num;
	}

}
