package nodes;

public class NodeString extends Node  {
	
	

	String content;
	
	
	public NodeString(String content) {
		
		super("string");
		this.content=content;
		
	}


	public String getStringContent() {
		return content;
	}


	public void setStringContent(String content) {
		this.content = content;
	}
	
	public boolean isConstant() {
		return true;
	}
	
	public boolean isPrintable() {
		return true;
	}
	public String getValue() {
		return content;
	}


	
	
	public String makeStyle() {
		return "[shape=triangle label=\"" + this.getSymbol() + this.content + "\"];";
	}
	
}
