package tokens;


public class TokenSymbol extends Token{
	public enum tokenType{BC, BO, SBC, SBO,  COLON, COMMA, PERIOD, SEMICOLON,
		DEF, WHERE, IF, THEN, ELSE, NIL,
		AND, OR, DIV, MULT, MINUS, PLUS, HD, TL,
		NOT, EQ, NEQ, LEQ, LT, GEQ, GT};

		private tokenType type;

		public TokenSymbol(tokenType type, int position,int line) {
			super(position,line);
			this.type=type;
		}

		public tokenType getType() {
			return type;
		}
}