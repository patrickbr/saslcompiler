package machines;

import java.io.IOException;
import java.util.ArrayList;

import exceptions.ParseException;

import nodes.*;

import tokens.TokenBool;
import tokens.TokenEOF;
import tokens.TokenID;
import tokens.TokenSymbol;
import tokens.TokenNum;
import tokens.TokenString;
import tokens.Token;
import tokens.TokenSymbol.tokenType;


public class Parser {
	private Lexer lex;
	private Token previousToken=null;
	private Token actToken=null;
	private Token lookahead=null;

	//list of all definitions. last entry is main
	private ArrayList<Definition> definitions = new ArrayList<Definition>();


	public ArrayList<Definition> parse(Lexer lex) throws ParseException,IOException  {
		this.lex=lex;

		Node main = evSystem();
		Definition haupt = new Definition("",new ArrayList<String>());

		haupt.setAbstraction(main);
		definitions.add(haupt);
		return definitions;
	}
	
	public ArrayList<Definition> parseInclude(Lexer lex) throws ParseException,IOException  {
		this.lex=lex;
		check(new TokenSymbol(tokenType.DEF,0,0));
		evIncludeFuncDef();
		return definitions;
	}

	/*
	 * 
	 * apply the grammar rules to the tokenstream
	 * 
	 * 
	 */	
	private Node evSystem() throws ParseException,IOException  {
		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.DEF)) {
			fetchNextToken();
			evFuncDef();	
			check(new TokenSymbol(tokenType.PERIOD,0,0));
		}

		Node expression = evExpr();

		if (expression == null) throw new ParseException("<expression>");
		if (moreTokens()) throw new ParseException(fetchPreviousToken(), "<EOF>", getLookAhead());

		return expression;
	}

	private void evFuncDef() throws ParseException,IOException  {
		definitions.add(evDef());

		if (!nextTokenIsSymbolTokenType(TokenSymbol.tokenType.PERIOD)) {
			check(new TokenSymbol(tokenType.DEF,0,0));
			evFuncDef();	
		}
	}
	
	private void evIncludeFuncDef() throws ParseException,IOException  {
		definitions.add(evDef());
		
		if (!(getLookAhead() instanceof TokenEOF)) {
			check(new TokenSymbol(tokenType.DEF,0,0));
			evIncludeFuncDef();	
		}
	}
	
	private Definition evDef() throws ParseException,IOException {
		String name = evName();
		checkNull(name,"<identifier>");
		ArrayList<String> vars = new ArrayList<String>();
		String var;

		while ((var = evName()) != null) {
			vars.add(var);
		}

		Definition tempDef = new Definition(name,vars);
		Node abstraction = evAbstraction();
		checkNull(abstraction,"<definition body>");
		tempDef.setAbstraction(abstraction);
		return tempDef;
	}

	private String evName() throws ParseException,IOException  {
		if (nextTokenIsIdToken()) {
			TokenID temp = (TokenID)fetchNextToken();
			return temp.getIdName();
		}

		return null;
	}
	
	private Node evAbstraction() throws ParseException,IOException  {
		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.EQ)) {
			fetchNextToken();
			return evExpr();
		}else{
			return null;
		}
	}

	private Node evExpr() throws ParseException,IOException  {
		Node condExpr = evCondExpr();
		NodeWhere where =null;

		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.WHERE)) {

			where = new NodeWhere();
			fetchNextToken();
			Definition a = evDef();
			where.wheres.add(a);

			while (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.SEMICOLON)){
				fetchNextToken();
				where.wheres.add(evDef());
			}
		}

		if (where == null) {
			return condExpr;
		}else{
			return new NodeApply(where,condExpr);
		}
	}

	private Node evCondExpr() throws ParseException,IOException {
		checkNull(getLookAhead(), "<expression>");

		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.IF)) {

			//IF
			fetchNextToken();
			Node condition = evExpr();
			checkNull(condition,"<expression>");

			//THEN
			check(new TokenSymbol(tokenType.THEN,0,0));
			Node then = evCondExpr();
			checkNull(then,"<expression>");

			//ELSE
			check(new TokenSymbol(tokenType.ELSE,0,0));
			Node sonst = evCondExpr();
			checkNull(sonst,"<expression>");
			return new NodeApply(new NodeApply(new NodeApply(new NodeCond(), condition),then),sonst);

		}else{
			return evListExpr();
		}
	}


	private Node evListExpr() throws ParseException,IOException {
		Node opExpr = evOpExpr();
		Node listExprStrich = evListExprStrich();

		checkNull(opExpr,"<expression>");

		if (listExprStrich != null) {
			return new NodeApply(new NodeApply(new NodeCons(),opExpr),listExprStrich);
		}else{
			return opExpr;
		}
	}


	private Node evListExprStrich() throws ParseException,IOException {
		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.COLON)) {

			fetchNextToken();
			Node a = evOpExpr();
			Node b = evListExprStrich();

			if (b!= null) {
				return new NodeApply(new NodeApply(new NodeCons(),a),b);
			}
			checkNull(a,"<expression>");
			return a;
		}
		return null;
	}


	private Node evOpExpr() throws ParseException,IOException {
		Node conjunct = evConjunct();
		Node opExprStrich = evOpExprStrich();

		if (opExprStrich != null) {
			return new NodeApply(new NodeApply(new NodeOr(),conjunct),opExprStrich);
		}else{
			return conjunct;
		}
	}

	private Node evOpExprStrich() throws ParseException,IOException {
		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.OR)) {

			fetchNextToken();
			Node conjunct = evConjunct();
			Node opExprStrich = evOpExprStrich();

			if (opExprStrich != null) {
				return new NodeApply(new NodeApply(new NodeOr(),conjunct),opExprStrich);
			}else{
				return conjunct;
			}
		}
		return null;

	}

	private Node evConjunct() throws ParseException,IOException  {
		Node compar = evCompar();
		Node conjunctStrich = evConjunctStrich();

		if (conjunctStrich != null) {
			return new NodeApply(new NodeApply(new NodeAnd(),compar),conjunctStrich);
		}
		return compar;

	}


	private Node evConjunctStrich()throws ParseException,IOException  {
		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.AND)) {

			fetchNextToken();
			Node compar = evCompar();
			Node conjunctStrich = evConjunctStrich();

			if (conjunctStrich != null) {
				return new NodeApply(new NodeApply(new NodeAnd(),compar),conjunctStrich);
			}else{
				return compar;
			}

		}
		return null;
	}


	private Node evCompar() throws ParseException,IOException {
		Node add = evAdd();
		Node relop= null;

		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.LT) ||
				nextTokenIsSymbolTokenType(TokenSymbol.tokenType.GT) || 
				nextTokenIsSymbolTokenType(TokenSymbol.tokenType.EQ) ||
				nextTokenIsSymbolTokenType(TokenSymbol.tokenType.NEQ) ||
				nextTokenIsSymbolTokenType(TokenSymbol.tokenType.LEQ) ||
				nextTokenIsSymbolTokenType(TokenSymbol.tokenType.GEQ)){

			relop = evRelop();
		}

		if (relop != null) {
			Node comparStrich = evCompar();
			return new NodeApply(new NodeApply(relop,add),comparStrich);
		}else{
			return add;
		}

	}


	private Node evAdd() throws ParseException,IOException {
		Node mul = evMul();
		Node addop= null;

		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.MINUS) ||
				nextTokenIsSymbolTokenType(TokenSymbol.tokenType.PLUS) ){
			addop = evAddop();
		}

		if (addop != null) {

			Node addStrich = evAdd();
			return new NodeApply(new NodeApply(addop,mul),addStrich);
		}else{
			return mul;
		}
	}

	private Node evMul() throws ParseException,IOException {
		Node factor = evFactor();
		checkNull(factor,"<expression>");
		Node mulop= null;

		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.MULT) ||
				nextTokenIsSymbolTokenType(TokenSymbol.tokenType.DIV)) {
			mulop = evMulop();
		}

		if (mulop != null) {
			Node mulStrich = evMul();
			return new NodeApply(new NodeApply(mulop,factor),mulStrich);
		}else{
			return factor;
		}

	}

	private Node evFactor() throws ParseException,IOException {
		if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.NOT)
				|| nextTokenIsSymbolTokenType(TokenSymbol.tokenType.HD)
				|| nextTokenIsSymbolTokenType(TokenSymbol.tokenType.TL)
				|| nextTokenIsSymbolTokenType(TokenSymbol.tokenType.PLUS)
				|| nextTokenIsSymbolTokenType(TokenSymbol.tokenType.MINUS)
		) {

			Node prefix = evPrefix();
			Node comb = evComb();
			checkNull(comb,"<expression>");
			return new NodeApply(prefix,comb);
		}else{
			Node comb = evComb();
			return comb;
		}
	}

	private Node evComb() throws ParseException,IOException {
		Node simple = evSimple();
		Node nextSimple;
		Node tempBaum = simple;

		while (moreTokens() && (nextSimple = evSimple())!= null) {
			tempBaum = new NodeApply(tempBaum,nextSimple);
		}

		return tempBaum;
	}

	private Node evSimple() throws ParseException {
		try {
			if (nextTokenIsIdToken()) {
				TokenID id = (TokenID)getLookAhead();
				fetchNextToken();

				return new NodeVar(id.getIdName());
			}

			if (nextTokenIsNumToken()) {
				TokenNum num = (TokenNum)getLookAhead();
				fetchNextToken();
				return new NodeNum(num.getNum());
			}

			if (nextTokenIsBoolToken()) {
				TokenBool bool = (TokenBool)getLookAhead();
				fetchNextToken();
				return new NodeBool(bool.getBool());
			}

			if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.NIL)) {
				fetchNextToken();
				return new NodeNil();
			}

			if (nextTokenIsStringToken()) {
				TokenString string = (TokenString)getLookAhead();
				fetchNextToken();
				return new NodeString(string.getStringContent());
			}

			if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.SBO)) {
				return evList();
			}

			if (nextTokenIsSymbolTokenType(TokenSymbol.tokenType.BO)) {
				fetchNextToken();
				Node expression = evExpr();
				check(new TokenSymbol(tokenType.BC,0,0));
				return expression;
			}

		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw new ParseException(fetchPreviousToken(), "<simple>");
		}

		return null;
	}

	private Node evPrefix() throws IOException {
		if (getLookAhead() instanceof TokenSymbol) {
			switch(((TokenSymbol) getLookAhead()).getType()) {

			case MINUS:
				fetchNextToken();
				return new NodeMinus();
			case PLUS:
				fetchNextToken();
				return new NodePlus();
			case NOT:
				fetchNextToken();
				return new NodeNot();
			case HD:
				fetchNextToken();
				return new NodeHd();
			case TL:
				fetchNextToken();
				return new NodeTl();
			}
		}
		return null;
	}

	private Node evAddop() throws IOException {
		if (getLookAhead() instanceof TokenSymbol) {
			switch(((TokenSymbol) getLookAhead()).getType()) {
			case MINUS: 
				fetchNextToken();
				return new NodeMinus();
			case PLUS: 
				fetchNextToken();
				return new NodePlus();
			}
		}
		return null;
	}

	private Node evMulop() throws IOException {
		if (getLookAhead() instanceof TokenSymbol) {
			switch(((TokenSymbol) getLookAhead()).getType()) {

			case DIV: 
				fetchNextToken();
				return new NodeDiv();
			case MULT: 
				fetchNextToken();
				return new NodeMult();
			}
		}
		return null;
	}

	private Node evRelop() throws IOException {
		if (getLookAhead() instanceof TokenSymbol) {
			switch(((TokenSymbol) getLookAhead()).getType()) {

			case EQ:
				fetchNextToken();
				return new NodeEqual();
			case NEQ:
				fetchNextToken();
				return new NodeNotEqual();
			case LT:
				fetchNextToken();
				return new NodeLess();
			case GT:
				fetchNextToken();
				return new NodeGreater();
			case GEQ:
				fetchNextToken();
				return new NodeGte();
			case LEQ:
				fetchNextToken();
				return new NodeLte();
			}
		}
		return null;
	}

	private Node evList() throws ParseException,IOException {
		if (nextTokenIsSymbolTokenType(tokenType.SBO)) {

			fetchNextToken();

			if (nextTokenIsSymbolTokenType(tokenType.SBC)) {
				fetchNextToken();
				return new NodeNil();
			}
			return evListElems();
		} else {
			return null;
		}
	}

	private Node evListElems() throws ParseException,IOException {
		Node expression = evExpr();
		checkNull(expression, "<expression>");

		if (nextTokenIsSymbolTokenType(tokenType.COMMA)) {
			fetchNextToken();
			Node listElemsStrich = evListElems();
			checkNull(listElemsStrich, "<expression>");
			return new NodeApply(new NodeApply(new NodeCons(),expression),listElemsStrich);
		} else {
			check(new TokenSymbol(tokenType.SBC,0,0));
			return new NodeApply(new NodeApply(new NodeCons(),expression),new NodeNil());
		}
	}


	private void check(Token tokenExpected) throws ParseException,IOException {
		Token after = fetchPreviousToken();
		Token currentToken;

		if (!(moreTokens()))  {
			throw new ParseException(after, tokenExpected,new TokenEOF(fetchPreviousToken().getPosition(),fetchPreviousToken().getLine()));
		}

		if (tokenExpected instanceof TokenSymbol) {

			if (!((currentToken = fetchNextToken()) instanceof TokenSymbol) || (((TokenSymbol)currentToken).getType() != ((TokenSymbol)tokenExpected).getType())) {
				throw new ParseException(currentToken, tokenExpected,fetchPreviousToken());
			}

		} else if ((!(fetchNextToken().getClass().equals(tokenExpected.getClass())))) {
			throw new ParseException(after, tokenExpected,fetchPreviousToken());
		}
	}

	private void checkNull(Node nullNode, String expected) throws ParseException,IOException {
		if (getLookAhead() != null)	{
			if (nullNode ==null) throw new ParseException(fetchPreviousToken(),expected,getLookAhead());
		} else {
			if (nullNode ==null) throw new ParseException(fetchPreviousToken(),expected);
		}
	}

	private void checkNull(Token nullToken, String expected) throws ParseException,IOException {
		if (getLookAhead() != null)	{
			if (nullToken ==null) throw new ParseException(fetchPreviousToken(),expected,getLookAhead());
		} else {
			if (nullToken ==null) throw new ParseException(fetchPreviousToken(),expected);
		}
	}

	private void checkNull(String nullString, String expected) throws ParseException, IOException {
		if (getLookAhead() != null)	{
			if (nullString ==null) throw new ParseException(fetchPreviousToken(),expected,getLookAhead());
		} else {
			if (nullString ==null) throw new ParseException(fetchPreviousToken(),expected);
		}
	}
	
	/*
	 * returns next token from lexer
	 */
	private Token fetchNextToken() throws IOException {
		previousToken = actToken;
		if (lookahead != null) {
			actToken=lookahead;
			lookahead=null;
		} else {
			actToken=lex.next();
		}
		return actToken;
	}

	/*
	 * returns the previous token for debugging purposes
	 */	
	private Token fetchPreviousToken() {
		return previousToken;
	}

	/*
	 * returns the lexer's lookahead
	 */	
	private Token getLookAhead() throws IOException{
		return lex.lookahead();
	}
	
	/*
	 * returns true if more tokens are availabe, false if not
	 */
	private boolean moreTokens() throws IOException {
		return (((lookahead != null) && !(lookahead instanceof TokenEOF)) || !(getLookAhead() instanceof TokenEOF));
	}
	
	/*
	 * returns true if next token is of type symbol
	 */
	private boolean nextTokenIsSymbolTokenType(TokenSymbol.tokenType type) throws IOException {
		if (moreTokens()){
			Token test=this.getLookAhead();
			return (test instanceof TokenSymbol && type==((TokenSymbol)test).getType());
		}
		return false;
	}
	
	/*
	 * returns true if next token is of type num
	 */
	private boolean nextTokenIsNumToken() throws IOException {
		if (moreTokens()){
			Token test=this.getLookAhead();
			return (test instanceof TokenNum);
		}
		return false;
	}
	
	/*
	 * returns true if next token is of type boolean
	 */	
	private boolean nextTokenIsBoolToken() throws IOException {
		if (moreTokens()){
			Token test=this.getLookAhead();
			return (test instanceof TokenBool);
		}
		return false;
	}
	
	/*
	 * returns true if next token is of type string
	 */	
	private boolean nextTokenIsStringToken() throws IOException {
		if (moreTokens()){
			Token test=this.getLookAhead();
			return (test instanceof TokenString);
		}
		return false;
	}
	
	/*
	 * returns true if next token is of type id
	 */
	private boolean nextTokenIsIdToken() throws IOException {
		if (moreTokens()){
			Token test=this.getLookAhead();
			return (test instanceof TokenID);
		}
		return false;
	}
}