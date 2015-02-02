package nodes;

public class NodeCond extends Node {	
	public NodeCond() {
		super("cond");		
	}
	
	public boolean isConstant() {
		return true;
	}
}