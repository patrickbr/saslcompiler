import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import machines.Compiler;
import machines.Lexer;
import machines.Parser;
import machines.Printer;
import machines.ReductionMachine;
import nodes.Definition;
import nodes.Node;
import exceptions.ParseException;
import exceptions.ReduceException;


public class SASLCoordinator {
	private SettingsReader s;
	private PrintStream statusStream = System.out;
	private PrintStream outputStream = System.out;
	private ArrayList<Definition> includes=new ArrayList<Definition>();
	private PrintStream errorStream = System.out;
	private InputStream lexStream=System.in;
	private long zeit;

	public SASLCoordinator(SettingsReader s) {
		this.s=s;
	}

	/*
	 * runs a sasl job using the given settings
	 */
	public void run() {
		if (s.help()) {
			msg(s.getHelp());
			return;
		}

		msg(" - SASL Compiler 0.3 - \n\n");

		try {
			if (s.useSysIn()) {
				lexStream=System.in;
			}
			if (s.useFile()) {
				msg("Using " + s.filePath() + "\n");
				lexStream=new FileInputStream(s.filePath());
			}
			if (s.outputUseFile()) {
				outputStream=new PrintStream(new FileOutputStream(s.outputfilePath()));
			}

			if (s.useFile()) {
				msg("Lexing & Parsing " + s.filePath() + "... ");
			}
			if (s.useSysIn()) {
				msg("Code must be terminated by EOF (Unix:CTRL+D, Windows:CTRL+Z) \n");
				msg("\n[sasl]: ");
			}

			addIncludes();

			// lex
			Lexer lex = new Lexer(lexStream);
			// parse
			Node cTree = compile(parse(lex));

                        // can be used to print the Tree to a dot file
			// DotPrinter dp = new DotPrinter(cTree);
			// System.out.println(dp.print());

			// reduce
			Node reduced = reduce(cTree);

			// print
			msg("\nResult:\n\n");
			Printer.print(outputStream,reduced);
			msg("\n\n");
			outputStream.close();
			stopTimer();
		}catch(ParseException e) {
			errorStream.println(e.getMessage());
		}catch(ReduceException e) {
			errorStream.println(e.getMessage());
		}catch(EOFException e) {
			errorStream.print("Error: Unexpected end of stream.");
		}catch(IOException e) {
			if (s.html()) {
				errorStream.print("Error IO: " + e.getMessage().replace("\n", "<br>"));
			}
		}
	}

	/*
	 * include all files specified by settings
	 */

	private void addIncludes() throws IOException, ParseException {
		String[] includes = s.getIncludes();

		if (includes != null) {

			for (int i=0;i<includes.length;i++) {
				include(includes[0]);
			}
		}
	}

	/*
	 * adds definitions from a include-file to the compiled code
	 */
	private void include(String file) throws IOException, ParseException{

		// lex
		Lexer lexInc = new Lexer(new FileInputStream(file));
		// parse

		Parser p = new Parser();
		includes.addAll(p.parseInclude(lexInc));
	}

	private ArrayList<Definition> parse(Lexer lex) throws ParseException,
	IOException {
		if (s.useFile()) startTimer();

		Parser p = new Parser();

		ArrayList<Definition> defs = p.parse(lex);
		if (s.useFile()) stopTimer();

		//falls includes vorhanden, füge hinzu

		if (includes.size()!=0) {
			includes.addAll(defs);
			return includes;
		}
		return defs;
	}


	private Node reduce(Node cTree) throws ReduceException {
		msg("\nRunning...\n");
		startTimer();
		Node reduced=ReductionMachine.reduce(cTree);

		return reduced;
	}

	private Node compile(ArrayList<Definition> defs) {

		Compiler c = new Compiler(defs);
		msg("Compiling... ");
		startTimer();
		Node cTree=c.getCompiledTree();
		stopTimer();
		return cTree;
	}

	private void startTimer() {
		zeit = System.currentTimeMillis();
	}

	private void stopTimer() {
		msg("(" +(System.currentTimeMillis() - zeit) + "ms)\n");
	}

	/*
	 * print msg to outputstream if quiet-mode is disabled
	 */
	public void msg(String msg) {
		if (!s.beQuiet()) {
			statusStream.print(msg);
		}
	}
}
