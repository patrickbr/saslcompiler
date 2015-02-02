package exceptions;

public class FunctionNotDefinedException extends ReduceException {
	String function;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FunctionNotDefinedException(String function) {	
		this.function=function;
	}

	public String getMessage() {
		return "<br>Error while reducing: Function not defined ('" + function + "')";
	}
}