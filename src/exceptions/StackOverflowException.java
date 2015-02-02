package exceptions;

public class StackOverflowException extends ReduceException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getMessage() {
		return "<br>Error while reducing: Stack Overflow (endless recursion?)";
	}
}