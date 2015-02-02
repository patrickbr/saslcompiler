package nodes;
import java.util.ArrayList;

public class Definition {
	private Node node;
	private String identifier;
	private ArrayList<String> var = new ArrayList<String>();

	public Definition(Node node, String identfier) {
		this.node=node;
		this.identifier= identfier;
	}

	public Definition(String identfier, ArrayList<String> var) {
		this.identifier= identfier;
		this.var=var;
	}
	
	public ArrayList<String> getVars() {
		return var;
	}
	
	public Node getNode() {
		return node;
	}
	
	public String getIdentifier() {
		return identifier;
	}


	public void setAbstraction(Node a) {
		this.node=a;
	}
}