package nodes;

public class NodeNil extends Node {	
	public NodeNil() {		
		super("nil");		
	}
	
	public boolean isConstant() {
		return true;
	}
	
	public boolean isPrintable() {
		return true;
	}

	public String getValue(){
		return "nil";
	}
}