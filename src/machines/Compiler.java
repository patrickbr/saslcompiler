package machines;

import java.util.ArrayList;
import java.util.List;


import nodes.*;


public class Compiler {
	private Node tree;
	private ArrayList<Definition> definitions = new ArrayList<Definition>();

	public Compiler(ArrayList<Definition> definitions) {
		this.tree = definitions.get(definitions.size()-1).getNode();
		this.definitions=definitions;
	}

	public Node getCompiledTree() {
		//alle where definitionen anwenden
		proceedAllWheres(definitions);

		//alle noch verbliebenen funktionvariablen abstrahieren
		removeAllVars(definitions);

		//globale definitionen aus allen definitionen selbst entfernen
		removeGlobalDefsFromAll();

		//programm ist die letzte definition
		this.tree = definitions.get(definitions.size()-1).getNode();

		return tree;
	}

	/*
	 * entfernt globale definitionen aus den globalen definitionen selbst
	 */
	private void removeGlobalDefsFromAll() {
		removeSingleVarsFromAll();

		for (int i=0;i<definitions.size();i++) {
			Definition next = definitions.get(i);
			Node n = next.getNode();
			n= removeDefs(n, definitions);
			definitions.get(i).setAbstraction(n);
		}
	}

	/*
	 * einzelne Definition vom Typ def x=y mit Node-I versehen
	 */
	private void removeSingleVarsFromAll() {
		for (int i=0;i<definitions.size();i++) {
			Definition next = definitions.get(i);
			Node n = next.getNode();
			if (n instanceof NodeVar) {
				next.setAbstraction(new NodeApply(new NodeI(),n));
			}
		}
	}

	/*
	 * ersetzt aus einem Node n alle Definitionen defs
	 */
	private Node removeDefs(Node n,List<Definition> defs) {
		if (n== null) return null;
		if (n instanceof NodeVar) {
			NodeVar nodeVar = (NodeVar) n;
			String varName=nodeVar.getName();

			for (int i=defs.size()-1;i>=0;i--) {
				Definition actual = defs.get(i);
				if (actual.getIdentifier().equals(varName)) {
					return actual.getNode();
				}
			}
			return n;
		} else {
			if (n instanceof NodeApply) {
				((NodeApply)n).setLeft(removeDefs(((NodeApply)n).getLeft(), defs));
				((NodeApply)n).setRight(removeDefs(((NodeApply)n).getRight(),defs));
			}
			return n;
		}
	}

	/*
	 * entfernt alle funktionsvariablen aus den definitionen
	 */
	private void removeAllVars(ArrayList<Definition> d) {
		for (int i=0;i<d.size();i++) {
			Definition next = d.get(i);
			Node n = next.getNode();
			ArrayList<String> vars = next.getVars();

			for (int a=vars.size()-1;a>=0;a--) {
				n = abstractVar(n,vars.get(a));
			}
			d.get(i).setAbstraction(n);
		}
	}

	/*
	 * abstrahiert die variable X aus einem node N	 
	 */
	private Node abstractVar(Node n, String x) {
		// Ist n ein where Knoten, wird von allen Definitionen darin x ebenfalls abstrahiert
		if (n instanceof NodeApply && ((NodeApply)n).getLeft() instanceof NodeWhere) {
			NodeWhere whereNode = (NodeWhere) ((NodeApply)n).getLeft();
			ArrayList<Definition> wheres = whereNode.wheres;

			for (int i =0;i<wheres.size();i++) {
				Definition a = wheres.get(i);
				a.setAbstraction(abstractVar(a.getNode(),x));
				wheres.set(i,a);
			}

			whereNode.wheres =wheres;
			((NodeApply)n).setLeft(whereNode);
			((NodeApply)n).setRight(abstractVar(((NodeApply)n).getRight(),x));
			return n;
		}

		// n ist variable

		else if (n instanceof NodeVar) {

			NodeVar varNode = (NodeVar) n;				                 

			if (varNode.getName().equals(x)) {
				return new NodeI();
			}else{
				return new NodeApply(new NodeK(), n);
			}

		}else if (!(n instanceof NodeApply)) {
			return new NodeApply(new NodeK(), n);
		}else if (n instanceof NodeApply) {
			return new NodeApply(new NodeApply(new NodeS(),abstractVar(((NodeApply)n).getLeft(),x)), abstractVar(((NodeApply)n).getRight(),x));
		}

		return null;
	}

	/*
	 * kompiliert alle where-definitionen
	 */
	private void proceedAllWheres(ArrayList<Definition> d) {
		for (int i=0;i<d.size();i++) {
			Definition next = d.get(i);
			Node n = proceedWheres(next.getNode());
			d.get(i).setAbstraction(n);
		}
	}

	/*
	 * kompiliert where definitionen in einem Node n
	 */
	private Node proceedWheres(Node n) {
		if (n==null) return n;

		if ((n instanceof NodeApply) && (((NodeApply)n).getLeft() != null) && (((NodeApply)n).getLeft() instanceof NodeWhere)) {

			NodeWhere nodeWheres = (NodeWhere) ((NodeApply)n).getLeft();
			
			proceedAllWheres(nodeWheres.wheres);
			removeAllVars(nodeWheres.wheres);
			Node bodyNode = proceedWheres(((NodeApply)n).getRight());

			if (nodeWheres.wheres.size() == 1) {
				Definition actualWhere = nodeWheres.wheres.get(0);
				bodyNode = processSingleWhereFromNode(bodyNode, actualWhere);
			} else if (nodeWheres.wheres.size() > 1) {
				bodyNode = processMultWheresFromNode(nodeWheres.wheres, bodyNode);
			}
			return bodyNode;

		}else{

			if (n instanceof NodeApply) {

				((NodeApply)n).setLeft(proceedWheres(((NodeApply)n).getLeft()));
				((NodeApply)n).setRight(proceedWheres(((NodeApply)n).getRight()));
			}
			return n;
		}
	}



	/*
	 * kompiliert einzelne where-definitionen vom type where a=b
	 */
	private Node processMultWheresFromNode(ArrayList<Definition> wheres,
			Node bodyNode) {

		Node nodeA = new NodeApply(new NodeK(),bodyNode);
		Node nodeB = new NodeNil();

		for (int i=wheres.size()-1;i>=0;i--) {
			Definition actWhere = wheres.get(i);
			nodeA = new NodeApply(new NodeU(),abstractVar(nodeA,actWhere.getIdentifier()));
			nodeB = new NodeApply(new NodeApply(new NodeCons(),proceedWheres(actWhere.getNode())), nodeB);
		}

		Node newNodeB = new NodeApply(new NodeK(), nodeB);

		// rekursionen innerhalb des wheres suchen & ggf. Y einsetzen

		for (int a=wheres.size()-1;a>=0;a--) {
			String actVar= wheres.get(a).getIdentifier();
			newNodeB =  new NodeApply(new NodeU(), abstractVar((newNodeB),actVar));
		}

		nodeB  = new NodeApply(new NodeY(),newNodeB);

		bodyNode = new NodeApply(nodeA,nodeB);
		return bodyNode;
	}

	/*
	 * kompiliert mehrere where-definitionen vom type where a=b;c=d
	 */
	private Node processSingleWhereFromNode(Node bodyNode,
			Definition actualWhere) {

		bodyNode = abstractVar(bodyNode, actualWhere.getIdentifier());

		if (identifierInNode(actualWhere.getNode(), actualWhere.getIdentifier())) {
			bodyNode = new NodeApply(bodyNode, new NodeApply(new NodeY(), abstractVar(proceedWheres(actualWhere.getNode()), actualWhere.getIdentifier())));
		}else {			
			bodyNode = new NodeApply(bodyNode, proceedWheres((actualWhere.getNode())));
		}
		return bodyNode;
	}


	/*
	 * sucht einen identifier in einem Baum n
	 */

	private boolean identifierInNode(Node n, String varName) {

		if (n instanceof NodeVar) {

			NodeVar var = (NodeVar) n;
			if (var.getName().equals(varName)) {
				return true;
			}			
		}

		if (n instanceof NodeApply) {

			NodeApply an = (NodeApply) n;
			return (identifierInNode(an.getLeft(),varName) || identifierInNode(an.getRight(),varName));

		}else{
			return false;
		}
	}
}
