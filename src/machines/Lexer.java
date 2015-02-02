package machines;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import tokens.*;

public class Lexer  {
	private InputStreamReader reader;
	private int lookAhead=-1;
	private int actChar=-1;
	private int pos=0;
	private int line=1;

	private Token actToken=null;
	private Token lookAheadToken=null;

	public Lexer(InputStream s) throws IOException {
		reader=new InputStreamReader(s);
	}

	/*
	 * returs next token from stream
	 */
	private Token getNextToken() throws IOException{
		skipWhitespaces();

		switch (actChar) {

		case -1: reader.close();return new TokenEOF(pos,line);
		case 34: return lexString();
		case 40: return new TokenSymbol(TokenSymbol.tokenType.BO,pos,line);
		case 41: return new TokenSymbol(TokenSymbol.tokenType.BC,pos,line);
		case 42: return new TokenSymbol(TokenSymbol.tokenType.MULT,pos,line);
		case 43: return new TokenSymbol(TokenSymbol.tokenType.PLUS,pos,line);
		case 44: return new TokenSymbol(TokenSymbol.tokenType.COMMA,pos,line);
		case 45: return new TokenSymbol(TokenSymbol.tokenType.MINUS,pos,line);
		case 46: return new TokenSymbol(TokenSymbol.tokenType.PERIOD,pos,line);
		case 47: return new TokenSymbol(TokenSymbol.tokenType.DIV,pos,line);
		case 58: return new TokenSymbol(TokenSymbol.tokenType.COLON,pos,line);
		case 59: return new TokenSymbol(TokenSymbol.tokenType.SEMICOLON,pos,line);
		case 60: 
			if (lookAhead != 61) {
				return new TokenSymbol(TokenSymbol.tokenType.LT,pos,line);
			}else{ 
				getNextChar();return new TokenSymbol(TokenSymbol.tokenType.LEQ,pos,line);
			}

		case 61: return new TokenSymbol(TokenSymbol.tokenType.EQ,pos,line);
		case 62: 
			if (lookAhead != 61) {
				return new TokenSymbol(TokenSymbol.tokenType.GT,pos,line); 
			}else{ 
				getNextChar();return new TokenSymbol(TokenSymbol.tokenType.GEQ,pos,line);
			} 
		case 91: return new TokenSymbol(TokenSymbol.tokenType.SBO,pos,line);
		case 93: return new TokenSymbol(TokenSymbol.tokenType.SBC,pos,line);
		case 126: getNextChar();return new TokenSymbol(TokenSymbol.tokenType.NEQ,pos,line); 

		}
		
		if (Character.isDigit(actChar)) return lexNum();
		
		//tokens not fetched until now must be of type name
		return lexName();
	}

	/*
	 * skips whitespaces in stream
	 */
	private void skipWhitespaces() throws IOException {
		while ((Character.isWhitespace(actChar =  getNextChar()))){
			if ((actChar == 10) || (actChar == 13 && !(lookAhead == 10))) {
				line++;
				pos=0;
			}
		}
	}

	/*
	 * returns string. reads until quote is reached or EOF
	 */
	private Token lexString() throws IOException {
		String temp="";
		int actChar;

		while ((actChar= getNextChar()) != 34) {
			if (actChar == -1) throw new EOFException();
			temp+=Character.toString((char)actChar);
		}

		return new TokenString(temp,pos,line);
	}
	
	/*
	 * returns integer, reads until a non-digit character is found
	 */

	private Token lexNum() throws IOException {
		String temp=(Character.toString((char)actChar));

		while (Character.isDigit((lookAhead))) {
			actChar=getNextChar();
			temp+=Character.toString((char)actChar);
		}

		return new TokenNum(Integer.parseInt(temp),pos,line);
	}

	/*
	 * returns name. reads until a character not allowed is reached. 
	 */
	
	private Token lexName() throws IOException {
		String temp= Character.toString((char)actChar);

		while(!Character.isWhitespace(lookAhead) && lookAhead != -1 && lookAhead != 34 && !(lookAhead >= 40 && lookAhead <=47) 
				&& !(lookAhead >= 58 && lookAhead <=62) && lookAhead !=91 && lookAhead != 93 && lookAhead != 126) {
			temp += Character.toString((char)getNextChar());
		}
		
		// check for internal methodes

		if (temp.equals("or")) return new TokenSymbol(TokenSymbol.tokenType.OR,pos,line);
		if (temp.equals("if")) return new TokenSymbol(TokenSymbol.tokenType.IF,pos,line);
		if (temp.equals("hd")) return new TokenSymbol(TokenSymbol.tokenType.HD,pos,line);
		if (temp.equals("tl")) return new TokenSymbol(TokenSymbol.tokenType.TL,pos,line);
		if (temp.equals("def")) return new TokenSymbol(TokenSymbol.tokenType.DEF,pos,line);
		if (temp.equals("not")) return new TokenSymbol(TokenSymbol.tokenType.NOT,pos,line);
		if (temp.equals("and")) return new TokenSymbol(TokenSymbol.tokenType.AND,pos,line);
		if (temp.equals("nil")) return new TokenSymbol(TokenSymbol.tokenType.NIL,pos,line);
		if (temp.equals("then")) return new TokenSymbol(TokenSymbol.tokenType.THEN,pos,line);
		if (temp.equals("else")) return new TokenSymbol(TokenSymbol.tokenType.ELSE,pos,line);
		if (temp.equals("true")) return new TokenBool(true,pos,line);
		if (temp.equals("where")) return new TokenSymbol(TokenSymbol.tokenType.WHERE,pos,line);
		if (temp.equals("false")) return new TokenBool(false,pos,line);

		return new TokenID(temp,pos,line);

	}

	/*
	 * returns the next token
	 */
	public Token next() throws IOException {
		if (lookAheadToken != null) {
			Token temp = lookAheadToken;
			lookAheadToken=null;
			return temp;
		}

		actToken=getNextToken();
		return actToken;
	}

	/*
	 * returns a lookahead token without stepping
	 */
	public Token lookahead() throws IOException {
		if (lookAheadToken == null) actToken=getNextToken();
		lookAheadToken = actToken;	
		return actToken;
	}

	/*
	 * returns the next character of the stream
	 */
	private int getNextChar() throws IOException{
		pos++;

		int oldLookahead=lookAhead;
		int chara=-1;

		if (oldLookahead != -1) {
			chara=oldLookahead;
		}else{
			chara=reader.read();
		}

		lookAhead=reader.read();
		return chara;
	}
}