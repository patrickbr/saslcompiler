package nodes;

public class NodeMinus extends Node {	
	
	public NodeMinus() {
		super("minus");		
	}
	
	public boolean isConstant() {
		return true;
	}
	
	public boolean isPrimitiveNum() {
		return true;
	}
}