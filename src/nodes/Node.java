package nodes;

public abstract class Node {
	private String symbol="";

	public Node(String symbol) {

		this.symbol=symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public boolean isConstant() {
		return false;
	}

	public boolean isPrintable() {
		return false;
	}

	public boolean isPrimitiveNum() {
		return false;
	}

	public boolean isPrimitiveBool() {
		return false;
	}

	public String getValue() {
		return null;
	}
}