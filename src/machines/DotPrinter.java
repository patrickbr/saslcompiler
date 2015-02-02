package machines;

import java.util.ArrayList;

import nodes.Node;
import nodes.NodeApply;

public class DotPrinter {
	/*
	 * DotPrinter, kann für Debugging verwendet werden.
	 */

	private Node n;
	private ArrayList<Node> down = new ArrayList<Node>();

	public DotPrinter(Node n) {
		this.n=n;
	}

	/*
	 * gibt einen Baum als Dot-Graph aus
	 */

	public String print() {
		String temp =  "digraph G {graph [size=\"5\"];";
		temp+=printer(n);
		temp += "}";
		return temp;
	}
	
	/*
	 * gibt die knotenverbindungen zurück
	 */
	private String printer(Node n) {
		String temp="";

		if (!down.contains(null)) {

			down.add(n);

			if (n instanceof NodeApply){
				temp += n.hashCode() + "[shape=circle label=\"@\"];";
			}else {

				String label=n.getValue();
			
				if (label == null) {
					label=n.getSymbol();
					temp += n.hashCode() + "[shape=rectangle label=\"" + label + "\"];";
				}else{
					temp += n.hashCode() + "[shape=triangle label=\"" + label + "\"];";
				}
			}
		}

		if (n instanceof NodeApply && ((NodeApply)n).getLeft() != null) {
			temp += n.hashCode() + " -> " + ((NodeApply)n).getLeft().hashCode() +";";
			if (!down.contains(((NodeApply)n).getLeft())) temp += printer(((NodeApply)n).getLeft());
		}

		if (n instanceof NodeApply && ((NodeApply)n).getRight() != null) {

			temp += n.hashCode() + " -> " + ((NodeApply)n).getRight().hashCode() +";";
			if (!down.contains(((NodeApply)n).getRight())) temp += printer(((NodeApply)n).getRight());
		}
		return temp;
	}
}