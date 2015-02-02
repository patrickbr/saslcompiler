package machines;


import java.io.PrintStream;

import exceptions.ReduceException;
import exceptions.TypeMismatchException;
import nodes.*;

public class Printer {
	public Printer(Node n) {

	}

	/*
	 * prints a printable reduction-machine result to the output stream
	 */
	public static void print(PrintStream stream, Node n) throws ReduceException {
		if (n instanceof NodeNum) {
			NodeNum numNode = (NodeNum) n;
			stream.print(numNode.getNum());
		} else if (n instanceof NodeBool) {
			NodeBool boolNode = (NodeBool) n;
			stream.print(boolNode.getBoolean());
		} else if (n instanceof NodeString) {
			NodeString stringNode = (NodeString) n;
			stream.print("\"" + stringNode.getStringContent() + "\"" );
		} else if (n instanceof NodeFunction) {
			stream.print("(function)");
		} else if (n instanceof NodeNil) {
			stream.print("nil");
		} else if (n instanceof NodePair) {
			stream.print("[");

			while (!(n instanceof NodeNil)) {

				if (!(n instanceof NodePair)) throw new TypeMismatchException(new NodeCons(), "<list>");
				Printer.print(stream,ReductionMachine.reduce(((NodePair)n).getLeft()));
				n = ReductionMachine.reduce(((NodePair)n).getRight());
				if (!(n instanceof NodeNil)) stream.print(",");
			}

			stream.print("]");
		}
	}
}