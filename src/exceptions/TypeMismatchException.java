package exceptions;

import nodes.Node;

public class TypeMismatchException extends ReduceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Node expecter;
	String expected;

	public TypeMismatchException (Node expecter,String expected) {
		this.expected=expected;

		this.expecter=expecter;
	}



	public String getMessage() {
		return "<br>Error while reducing: Primitive function '" + expecter.getSymbol() + "' expected '" + expected + "' as parameter!";
	}

}
