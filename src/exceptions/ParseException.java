package exceptions;
import tokens.*;


public class ParseException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Token atToken= new TokenEOF(0,0);
	Token expectedToken;
	String expected;
	Token found;


	public ParseException (Token atToken) {
		if (atToken != null) this.atToken=atToken;
	}

	public ParseException (String expected) {
		this.expected=expected;
	}

	public ParseException (Token atToken, Token expectedToken) {

		if (atToken != null) this.atToken=atToken;
		this.expectedToken=expectedToken;

	}

	public ParseException (Token atToken, String expected) {

		if (atToken != null) this.atToken=atToken;
		this.expected=expected;
	}

	public ParseException (Token atToken, Token expectedToken, Token found) {

		if (atToken != null) this.atToken=atToken;
		this.expectedToken=expectedToken;
		this.found=found;

	}

	public ParseException (Token atToken, String expected, Token found) {

		if (atToken != null) this.atToken=atToken;
		this.expected=expected;
		this.found=found;
	}

	private String getTokenString(Token t) {

		if (t instanceof TokenBool) return Boolean.toString(((TokenBool)t).getBool());
		if (t instanceof TokenString) return ((TokenString)t).getStringContent();
		if (t instanceof TokenNum) return Integer.toString(((TokenNum)t).getNum());
		if (t instanceof TokenEOF) return "<EOF>";
		if (t instanceof TokenSymbol) {

			switch(((TokenSymbol) t).getType()) {

			case BC: return "(";
			case BO:return ")";
			case SBC:return "{";
			case SBO:return "}";
			case COLON:return ":";
			case COMMA:return ",";
			case PERIOD:return ".";
			case SEMICOLON:return ";";
			case DEF:return "def";
			case WHERE:return "where";
			case IF:return "if";
			case THEN:return "then";
			case ELSE:return "else";
			case NIL:return "nil";
			case AND:return "and";
			case OR:return "or";
			case DIV:return "div";
			case MULT:return "mult";
			case MINUS:return "minus";
			case PLUS:return "plus";
			case HD:return "hd";
			case TL:return "tl";
			case NOT:return "not";
			case EQ:return "=";
			case NEQ:return "~=";
			case LEQ:return "<=";
			case LT:return "<";
			case GEQ:return ">=";
			case GT:return ">";

			}
		}
		return "<undefinied>";

	}

	public String getMessage() {

		String temp ="<br>Error after parsing '" + getTokenString(found) + "' [" + atToken.getClass().getSimpleName() + "] @ line " + atToken.getLine() + ", pos " + (atToken.getPosition()) + " <br>";

		if (expectedToken !=null) {
			temp += "(Expected: " +getTokenString(expectedToken) + ")";
		}

		else if (expected !=null) {
			temp += "(Expected: " + expected + ")";
		}

		if (found !=null) {
			temp += "<br>(Found: '" + getTokenString(found) + "' [" + found.getClass().getSimpleName() + "])";
		}
		return temp;

	}



}
