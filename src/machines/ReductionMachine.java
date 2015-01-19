package machines;
import java.util.EmptyStackException;
import java.util.Stack;
import exceptions.FunctionNotDefinedException;
import exceptions.ReduceException;
import exceptions.StackOverflowException;
import exceptions.TypeMismatchException;
import nodes.*;

public abstract class ReductionMachine {

	public static Node reduce(Node tree) throws ReduceException {
		Stack<Node> nodeStack = new Stack<Node>();
		
		double oldTimeMillis;
		int maxTime = 1000;

		//am Anfang wird der tree komplett auf den stack geladen
		nodeStack.push(tree);

		//so lange reducen bis auf dem stack ein printable liegt
		
		oldTimeMillis = System.currentTimeMillis();

		while (!nodeStack.peek().isPrintable()) {
			try {
				if (System.currentTimeMillis() - oldTimeMillis > maxTime) {
					System.out.println("Timed out.");
					break;
				}
				
				skipApplies(nodeStack);
				applyTransformations(nodeStack);
				
			}catch(EmptyStackException n) {
				nodeStack.push(new NodeFunction());
			}catch(NullPointerException n) {
				throw new ReduceException();
			}catch(StackOverflowError n) {
				throw new StackOverflowException();
			}

		}
		return nodeStack.pop();
	}

	private static void skipApplies(Stack<Node> nodeStack) {
		while (nodeStack.peek() instanceof NodeApply) {
			nodeStack.push(((NodeApply)nodeStack.peek()).getLeft());
		}
	}

	private static void applyTransformations(Stack<Node> nodeStack)
			throws FunctionNotDefinedException, ReduceException {
		
		//Node B
		if (nodeStack.peek() instanceof NodeB) {
			nodeStack.pop();
			Node f =  ((NodeApply)nodeStack.pop()).getRight();
			Node g =  ((NodeApply)nodeStack.pop()).getRight();
			Node x =  ((NodeApply)nodeStack.peek()).getRight();

			NodeApply nodeApply = (NodeApply) nodeStack.pop();

			nodeApply.setLeft(f);
			nodeApply.setRight(new NodeApply(g,x));

			nodeStack.push(nodeApply);
		}

		//Node C
		else if (nodeStack.peek() instanceof NodeC) {
			nodeStack.pop();
			Node f =  ((NodeApply)nodeStack.pop()).getRight();
			Node g =  ((NodeApply)nodeStack.pop()).getRight();
			Node x =  ((NodeApply)nodeStack.peek()).getRight();

			NodeApply nodeApply = (NodeApply) nodeStack.pop();

			nodeApply.setLeft(new NodeApply(f,x));
			nodeApply.setRight(g);

			nodeStack.push(nodeApply);
		}

		//Node S'
		else if (nodeStack.peek() instanceof NodeSstrich) {
			nodeStack.pop();
			Node c = ((NodeApply)nodeStack.pop()).getRight();
			Node f = ((NodeApply)nodeStack.pop()).getRight();
			Node g = ((NodeApply)nodeStack.pop()).getRight();
			Node x = ((NodeApply)nodeStack.peek()).getRight();

			NodeApply nodeApply = (NodeApply) nodeStack.pop();

			nodeApply.setLeft(new NodeApply(c,new NodeApply(f,x)));
			nodeApply.setRight(new NodeApply(g,x));

			nodeStack.push(nodeApply);
		}

		//Node B*
		else if (nodeStack.peek() instanceof NodeBstern) {
			nodeStack.pop();
			Node c =  ((NodeApply)nodeStack.pop()).getRight();
			Node f =  ((NodeApply)nodeStack.pop()).getRight();
			Node g =  ((NodeApply)nodeStack.pop()).getRight();
			Node x =  ((NodeApply)nodeStack.peek()).getRight();

			NodeApply nodeApply = (NodeApply) nodeStack.pop();

			nodeApply.setLeft(c);
			nodeApply.setRight(new NodeApply(f,new NodeApply(g,x)));

			nodeStack.push(nodeApply);
		}

		//Node C'
		else if (nodeStack.peek() instanceof NodeCstrich) {
			nodeStack.pop();
			Node c = ((NodeApply)nodeStack.pop()).getRight();
			Node f = ((NodeApply)nodeStack.pop()).getRight();
			Node g = ((NodeApply)nodeStack.pop()).getRight();
			Node x = ((NodeApply)nodeStack.peek()).getRight();

			NodeApply nodeApply = (NodeApply) nodeStack.pop();

			nodeApply.setLeft(new NodeApply(c, new NodeApply(f,x)));
			nodeApply.setRight(g);

			nodeStack.push(nodeApply);
		}

		//Node I
		else if (nodeStack.peek() instanceof NodeI) {
			nodeStack.pop();

			if (nodeStack.isEmpty()) nodeStack.push(new NodeFunction()); else {
				nodeStack.push(((NodeApply)nodeStack.pop()).getRight());
			}
		}

		//Node var -> funktion nicht definiert
		else if (nodeStack.peek() instanceof NodeVar) {
			NodeVar varNode = (NodeVar) nodeStack.peek();
			throw new FunctionNotDefinedException(varNode.getName());
		}

		//Node U
		else if (nodeStack.peek() instanceof NodeU) {
			nodeStack.pop();

			Node f = ((NodeApply)nodeStack.pop()).getRight();
			NodeApply nodeApply = (NodeApply) nodeStack.pop();
			Node z = ((NodeApply)nodeApply).getRight();

			nodeApply.setRight(new NodeApply(new NodeTl(),z));
			nodeApply.setLeft(new NodeApply(f,new NodeApply(new NodeHd(),z)));

			nodeStack.push(nodeApply);
		}

		//Node S
		else if (nodeStack.peek() instanceof NodeS) {

			nodeStack.pop();

			Node A = ((NodeApply)nodeStack.pop()).getRight();
			Node B = ((NodeApply)nodeStack.peek()).getRight();			
			Node returnNode =  nodeStack.pop();

			// Optimierungen

			// S@(K@f)@(K@g)

			if ((A instanceof NodeApply) &&(((NodeApply)A).getLeft() instanceof NodeK) && (B instanceof NodeApply) && (((NodeApply)B).getLeft() instanceof NodeK)) {

				Node f =((NodeApply)A).getRight();
				Node g =((NodeApply)B).getRight();

				((NodeApply)returnNode).setLeft(new NodeK());
				((NodeApply)returnNode).setRight(new NodeApply(f,g));

				//S@(K@f)@I

			}else if ((A instanceof NodeApply) &&(((NodeApply)A).getLeft() instanceof NodeK) &&  B instanceof NodeI) {

				Node f = ((NodeApply)A).getRight();

				returnNode = f;

				//S@(K@f)@(B@g@h)					

			}else if ((A instanceof NodeApply) && (((NodeApply)A).getLeft() instanceof NodeK) && ((NodeApply)B).getLeft() instanceof NodeApply && ((NodeApply)((NodeApply)B).getLeft()).getLeft() instanceof NodeB){


				Node g= ((NodeApply)((NodeApply)B).getLeft()).getRight();
				Node h= ((NodeApply)B).getRight(); 
				Node f= ((NodeApply)A).getRight();

				((NodeApply)returnNode).setLeft(new NodeApply(new NodeApply(new NodeBstern(),f),g));
				((NodeApply)returnNode).setRight( h);

				//S@(K@f)@g

			}else if ((A instanceof NodeApply) &&((NodeApply)A).getLeft() instanceof NodeK)  {
				Node g=B;
				Node f = ((NodeApply)A).getRight();

				((NodeApply)returnNode).setLeft(new NodeApply(new NodeB(),f));
				((NodeApply)returnNode).setRight(g);

				//S@(B@f@g)@(K@h)

			}else if ((A instanceof NodeApply) &&(((NodeApply)A).getLeft() instanceof NodeApply && ((NodeApply)((NodeApply)A).getLeft()).getLeft() instanceof NodeB) && ((NodeApply)B).getLeft() instanceof NodeK) {

				Node g = ((NodeApply)A).getRight();
				Node f = ((NodeApply)((NodeApply)A).getLeft()).getRight();
				Node h = ((NodeApply)B).getRight();

				((NodeApply)returnNode).setRight(h);
				((NodeApply)returnNode).setLeft(new NodeApply(new NodeApply(new NodeCstrich(),f),g));

				//S@(B@f@g)@h

			}else if ((A instanceof NodeApply) &&((NodeApply)A).getLeft() instanceof NodeApply && ((NodeApply)((NodeApply)A).getLeft()).getLeft() instanceof NodeB){

				Node h = B;

				Node g = ((NodeApply)A).getRight();
				Node f = ((NodeApply)((NodeApply)A).getLeft()).getRight();

				((NodeApply)returnNode).setRight(h);
				((NodeApply)returnNode).setLeft(new NodeApply(new NodeApply(new NodeSstrich(),f),g));

				//S@f@(K@g)

			} else if ((B instanceof NodeApply) &&((NodeApply)B).getLeft() instanceof NodeK){


				Node f = A;
				Node g = ((NodeApply)B).getRight();

				((NodeApply)returnNode).setRight(g);
				((NodeApply)returnNode).setLeft(new NodeApply(new NodeC(),f));

				//sonst standardverfahren ohne optimierung

			} else {
				Node f = A;
				Node g = B;			

				returnNode =(NodeApply) nodeStack.pop();

				Node x = ((NodeApply)returnNode).getRight();

				((NodeApply)returnNode).setLeft(new NodeApply(f,x));
				((NodeApply)returnNode).setRight(new NodeApply(g,x));
			}

			nodeStack.push(returnNode);

		}

		//Node Y

		else if (nodeStack.peek() instanceof NodeY) {
			nodeStack.pop();

			Node f = ((NodeApply)nodeStack.pop()).getRight();
			Node temp = new NodeApply(f,null);

			((NodeApply)temp).setRight(temp);

			nodeStack.push(temp);
		}

		//Node K

		else if (nodeStack.peek() instanceof NodeK) {
			nodeStack.pop();

			Node x  = ((NodeApply)nodeStack.pop()).getRight();
			Node nodeApply = nodeStack.pop();

			((NodeApply)nodeApply).setLeft(new NodeI());
			((NodeApply)nodeApply).setRight(x);

			nodeStack.push(nodeApply);
		}

		//Node Cons

		else if (nodeStack.peek() instanceof NodeCons) {
			nodeStack.pop();
			Node x =  (((NodeApply)nodeStack.pop()).getRight());
			NodeApply nodeApply=  ((NodeApply)nodeStack.pop());
			Node y =  (nodeApply.getRight());

			nodeApply.setLeft(new NodeI());
			nodeApply.setRight(new NodePair(x,y));

			nodeStack.push(nodeApply);
		} 

		//Node Hd

		else if (nodeStack.peek() instanceof NodeHd) {
			nodeStack.pop();

			NodeApply nodeApply =(NodeApply)nodeStack.pop();

			NodePair x=  (NodePair)reduce(nodeApply.getRight());

			expectPair(new NodeHd(),x);

			nodeApply.setLeft(new NodeI());
			nodeApply.setRight(x.getLeft());
			nodeStack.push(nodeApply);
		}

		//Node Tl

		else if (nodeStack.peek() instanceof NodeTl) {
			nodeStack.pop();
			NodeApply nodeApply = (NodeApply)nodeStack.pop();

			NodePair x= (NodePair) reduce(nodeApply.getRight());

			expectPair(new NodeTl(),x);

			nodeApply.setLeft(new NodeI());
			nodeApply.setRight(x.getRight());

			nodeStack.push(nodeApply);
		} 

		//Primitive num-Funktionen (+,-,*,/,<,>,=,=>,=<)

		else if ((!(nodeStack.peek() == null)) && nodeStack.peek().isPrimitiveNum()) {
			Node function = nodeStack.peek();

			NodeApply nodeApply=null;
			nodeStack.pop();

			Node xx = reduce(((NodeApply)nodeStack.pop()).getRight());

			if (function instanceof NodePlus && xx instanceof NodeString){

				nodeApply = (NodeApply) nodeStack.pop();
				Node temp = reduce(nodeApply.getRight());
				expectString(function,temp);

				NodeString yVal =(NodeString) temp;
				NodeString xVal= (NodeString) xx;

				nodeStack.push(new NodeApply(new NodeI(),new NodeString(xVal.getStringContent() + yVal.getStringContent())));
			}else{
				expectNum(function,xx);

				NodeNum x = (NodeNum) xx;
				int xVal = x.getNum();
				int yVal=0;

				if (function instanceof NodePlus && nodeStack.isEmpty()){

					nodeStack.push(new NodeApply(new NodeI(),new NodeNum(xVal)));
				}
				else if (function instanceof NodeMinus && nodeStack.isEmpty()){

					nodeStack.push(new NodeApply(new NodeI(),new NodeNum(-xVal)));
				}
				else if (!(nodeStack.isEmpty())) {
					nodeApply = (NodeApply) nodeStack.pop();

					Node temp = reduce(nodeApply.getRight());

					expectNum(function, temp);

					NodeNum y = (NodeNum) temp;
					yVal = y.getNum();

					if (function instanceof NodePlus) {
						nodeApply.setLeft(new NodeI());
						nodeApply.setRight(new NodeNum(xVal + yVal));
						nodeStack.push(nodeApply);
					}

					else if (function instanceof NodeMinus) {
						nodeApply.setLeft(new NodeI());
						nodeApply.setRight(new NodeNum(xVal - yVal));
						nodeStack.push(nodeApply);
					}

					else if (function instanceof NodeDiv) {

						nodeApply.setLeft(new NodeI());
						nodeApply.setRight(new NodeNum(xVal / yVal));
						nodeStack.push(nodeApply);
					}
					else if (function instanceof NodeMult) {

						nodeApply.setLeft(new NodeI());
						nodeApply.setRight(new NodeNum(xVal * yVal));
						nodeStack.push(nodeApply);
					}
					else if (function instanceof NodeLess) {

						nodeApply.setLeft(new NodeI());
						nodeApply.setRight(new NodeBool(xVal < yVal));
						nodeStack.push(nodeApply);

					}
					else if (function instanceof NodeGreater) {
						nodeApply.setLeft(new NodeI());
						nodeApply.setRight(new NodeBool(xVal > yVal));
						nodeStack.push(nodeApply);
					}
					else if (function instanceof NodeLte) {
						nodeApply.setLeft(new NodeI());
						nodeApply.setRight(new NodeBool(xVal <= yVal));
						nodeStack.push(nodeApply);
					}
					else if (function instanceof NodeGte) {
						nodeApply.setLeft(new NodeI());
						nodeApply.setRight(new NodeBool(xVal >= yVal));
						nodeStack.push(nodeApply);
					}
				} 
			}
		}

		//Node not

		else if (nodeStack.peek() instanceof NodeNot) {
			nodeStack.pop();
			NodeApply nodeApply = (NodeApply) nodeStack.pop();
			Node right=reduce(nodeApply.getRight());

			expectBool(new NodeNot(),right);
			NodeBool x = (NodeBool) right;
			boolean xVal = x.getBoolean();

			nodeApply.setLeft(new NodeI());
			nodeApply.setRight(new NodeBool(!xVal));
			nodeStack.push(nodeApply);
		}

		//Node equal & Node notequal

		else if ((nodeStack.peek() instanceof NodeEqual) || (nodeStack.peek() instanceof NodeNotEqual)){
			boolean not=nodeStack.peek() instanceof NodeNotEqual;

			nodeStack.pop();

			Node x =reduce(((NodeApply)nodeStack.pop()).getRight());
			NodeApply nodeApply = (NodeApply) nodeStack.pop();
			Node y =  reduce(nodeApply.getRight());

			if (x instanceof NodeNil || y instanceof NodeNil) {

				boolean val = x instanceof NodeNil &&  x instanceof NodeNil;

				nodeApply.setLeft(new NodeI());
				nodeApply.setRight(new NodeBool(val));

				nodeStack.push(nodeApply);

			} else if (x instanceof NodeNum) {

				NodeNum xNum = (NodeNum) x;
				expectNum(new NodeEqual(),y);
				NodeNum yNum = (NodeNum) y;

				nodeApply.setLeft(new NodeI());
				if (not) nodeApply.setRight(new NodeBool(xNum.getNum() != yNum.getNum())); else nodeApply.setRight(new NodeBool(xNum.getNum() == yNum.getNum()));

				nodeStack.push(nodeApply);

			} else if (x instanceof NodeBool) {

				NodeBool xNum = (NodeBool) x;
				expectBool(new NodeEqual(),y);
				NodeBool yNum = (NodeBool) y;

				nodeApply.setLeft(new NodeI());

				if (not) nodeApply.setRight(new NodeApply(new NodeI(),new NodeBool(xNum.getBoolean()!= yNum.getBoolean())));
				else nodeApply.setRight(new NodeApply(new NodeI(),new NodeBool(xNum.getBoolean() == yNum.getBoolean())));
				nodeStack.push(nodeApply);
			} else if (x instanceof NodeString) {
				NodeString xNum = (NodeString) x;
				expectString(new NodeEqual(),y);
				NodeString yNum = (NodeString) y;

				nodeApply.setLeft(new NodeI());

				if (not) nodeApply.setRight(new NodeApply(new NodeI(),new NodeBool(!xNum.getStringContent().equals(yNum.getStringContent()) )));
				else nodeApply.setRight(new NodeApply(new NodeI(),new NodeBool(xNum.getStringContent().equals(yNum.getStringContent()) )));


				nodeStack.push(nodeApply);
			}
		} 

		//Primitive bool-funktionen

		else if (!nodeStack.isEmpty() && !(nodeStack.peek() == null)  && nodeStack.peek().isPrimitiveBool()) {
			Node function = nodeStack.peek();

			nodeStack.pop();

			Node tempX =reduce(((NodeApply)nodeStack.pop()).getRight());
			Node tempY= reduce(((NodeApply)nodeStack.pop()).getRight());

			expectBool(function,tempX);
			expectBool(function,tempY);

			NodeBool x = (NodeBool) tempX;
			NodeBool y = (NodeBool) tempY;

			boolean xVal = x.getBoolean();
			boolean yVal = y.getBoolean();

			if (function instanceof NodeAnd) nodeStack.push(new NodeApply(new NodeI(),new NodeBool(xVal && yVal)));
			if (function instanceof NodeOr) nodeStack.push(new NodeApply(new NodeI(),new NodeBool(xVal || yVal)));

		}

		//Node cond

		else if (nodeStack.peek()instanceof NodeCond) {

			nodeStack.pop();

			Node temp = reduce(((NodeApply)nodeStack.pop()).getRight());
			expectBool(new NodeCond(), temp);
			NodeBool condition = (NodeBool) temp;
			Node x =  ((NodeApply)nodeStack.pop()).getRight();			NodeApply nodeApply = (NodeApply) nodeStack.pop();
			Node y = nodeApply.getRight();

			if (condition.getBoolean()) {

				nodeApply.setLeft(new NodeI());
				nodeApply.setRight(x);
			}else{
				nodeApply.setLeft(new NodeI());
				nodeApply.setRight(y);;
			}

			nodeStack.push(nodeApply);
		}
	}


	// Typsicherheiten bei primitiven Funktionsanwendungen

	// überprüft ob t vom typ NodeNum ist, sonst exception
	private static void expectNum(Node n,Node t) throws ReduceException {
		if (!(t instanceof NodeNum)) {
			throw new TypeMismatchException(n,"num");
		}
	}

	// überprüft ob t vom typ NodePair ist, sonst exception
	private static void expectPair(Node n,Node t) throws ReduceException {
		if (!(t instanceof NodePair)) {
			throw new TypeMismatchException(n,"list");
		}
	}

	// überprüft ob t vom typ NodeBool ist, sonst exception
	private static void expectBool(Node n,Node t) throws ReduceException {
		if (!(t instanceof NodeBool)) {
			throw new TypeMismatchException(n,"boolean");
		}
	}

	// überprüft ob t vom typ NodeString ist, sonst exception
	private static void expectString(Node n,Node t) throws ReduceException {
		if (!(t instanceof NodeString)) {
			throw new TypeMismatchException(n,"string");
		}
	}
}
