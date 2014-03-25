package nodes;

public class NodeApply extends Node{

	private Node left=null;
	private Node right=null;

	public NodeApply(Node left, Node right) {
		super("@");
		this.left=left;
		this.right=right;
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
