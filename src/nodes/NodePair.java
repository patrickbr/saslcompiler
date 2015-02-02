package nodes;

public class NodePair extends Node {
	private Node left=null;
	private Node right=null;

	public NodePair() {
		super("pair");
	}

	public NodePair(Node left, Node right) {
		super("pair");
		this.left=left;
		this.right=right;
	}

	public boolean isConstant() {
		return true;
	}

	public boolean isPrintable() {
		return true;
	}

	public Node getLeft() {
		return this.left;
	}

	public Node getRight() {
		return this.right;
	}

	public void setRight(Node r) {
		this.right=r;
	}
	public void setLeft(Node l) {
		this.left=l;
	}
}