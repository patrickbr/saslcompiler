import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import machines.Compiler;
import machines.Lexer;
import machines.Parser;
import machines.Printer;
import machines.ReductionMachine;
import nodes.Definition;
import nodes.Node;

import java.io.ByteArrayOutputStream;
import org.junit.Test;



public class Tests {


	/**
	 * tests all basic operations
	 */
	@Test
	public void baseTests() throws Exception {

		assertEquals(testFile("test/deftest.sasl"),"23");
		assertEquals(testFile("test/deftest2.sasl"),"23");
		assertEquals(testFile("test/recursionTest.sasl"),"23");
		assertEquals(testFile("test/listTest.sasl"),"[23]");
		assertEquals(testFile("test/functest.sasl"),"23");
		assertEquals(testFile("test/whereTest1.sasl"),"23");
		assertEquals(testFile("test/whereTest2.sasl"),"23");
		assertEquals(testFile("test/whereTest3.sasl"),"23");
		assertEquals(testFile("test/whereTest4.sasl"),"23");
		assertEquals(testFile("test/whereTest5.sasl"),"23");
		assertEquals(testFile("test/whereTest6.sasl"),"23");

		assertEquals(testFile("test/prelude/prelude1.sasl"),"[1,2,3,4]");
		assertEquals(testFile("test/prelude/prelude2.sasl"),"[3,2,1]");
		assertEquals(testFile("test/prelude/sort.sasl"),"[-44,2,4,5,6,8,9,1000]");
		assertEquals(testFile("test/prelude/dropTake.sasl"),"[1,2,3,4,5,6,7,8,9,10]");

		assertEquals(testFile("test/prelude/bigtest.sasl"),"[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229]");
		assertEquals(testFile("test/prelude/funcInListTest"),"5");


	}


	public String testFile(String file) throws Exception {


		FileInputStream fis = new FileInputStream(file);

		Lexer lex = new Lexer(fis);
		Parser p = new Parser();
		ArrayList<Definition> defs = p.parse(lex);
		Compiler c = new Compiler(defs);

		Node reduced=ReductionMachine.reduce(c.getCompiledTree());


		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(byteOut);
		Printer.print(ps, reduced);
		String result = byteOut.toString();

		return result;

	}


}
