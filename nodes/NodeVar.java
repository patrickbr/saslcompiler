package nodes;

public class NodeVar extends Node{
	
	
	
	String name="";
	
	
	public NodeVar(String name) {
		
		super("var");
		this.name=name;
		
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	

	public String makeStyle() {
		return "[shape=triangle label=\"" + this.getSymbol() + ":" + this.name + "\"];";
	}
	
	public String getValue() {
		
		return name;
		
	}




	

}
