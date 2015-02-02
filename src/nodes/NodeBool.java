package nodes;

public class NodeBool extends Node {	
	private boolean bool;
		
	public NodeBool(boolean bool) {
		
		super("bool");
		this.bool=bool;
			
	}	
	
	public boolean isConstant() {
		return true;
	}
	
	public boolean isPrintable() {
		return true;
	}
	
	public boolean getBoolean() {
		return bool;
	}


	public void setBoolean(boolean bool) {
		this.bool = bool;
	}

	public String getValue() {
		return Boolean.toString(bool);
	}	
}