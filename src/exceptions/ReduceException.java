package exceptions;

public class ReduceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;
		
	public ReduceException(String msg) {
		this.msg = msg;
	}

	public ReduceException() {
	this.msg = "<br>Error while reducing";
	}

	public String getMessage() {
	return this.msg;
	}
}
