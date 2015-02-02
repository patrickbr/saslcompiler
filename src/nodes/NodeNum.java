package nodes;

public class NodeNum extends Node {
	int num;
	
	public NodeNum(int num) {		
		super("num");
		this.num=num;		
	}

	public boolean isConstant() {
		return true;
	}
	
	public boolean isPrintable() {
		return true;
	}
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}	

	public String getValue() {
		return Integer.toString(num);
	}	
}