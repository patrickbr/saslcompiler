
public class SettingsReader {


	private String filePath="";
	private boolean useFile=false;
	private String outputFilePath="";
	private boolean outputUseFile=false;
	private boolean help=false;
	private String[] includes;
	private boolean useSysIn=true;
	private String args[];
	private int pointer=0;
	private boolean quiet=false;
	private boolean html=false;

	public SettingsReader(String[] args) {
		this.args=args;
		evalArgs();
	}

	/*
	 * reads settings from args()
	 * 
	 */
	private void evalArgs() {

		if (pointer < args.length) {

			// path to sasl file specified
			if (args[pointer].equals("-f")) {
				pointer ++;
				useFile=true;
				useSysIn=false;
				filePath=args[pointer];
			}
			
			// includes specified
			if (args[pointer].equals("-html")) {
				html=true;
			
			}
			
			// includes specified
			if (args[pointer].equals("-I")) {
				pointer ++;
				includes=args[pointer].split(";");
			
			}

			// output file path specified
			if (args[pointer].equals("-o")) {
				pointer ++;
				outputUseFile=true;
				outputFilePath=args[pointer];
			}
			
			// use system in
			if (args[pointer].equals("-i")) {
				useFile=false;;
				useSysIn=true;
				filePath="";
			}
			
			// only print result (quiet mode)
			if (args[pointer].equals("-q")) {
				quiet=true;
			}
			
			// prints help
			if (args[pointer].equals("-h")) {
				help=true;
			}

			pointer++;
			evalArgs();
		}
	}

	public boolean beQuiet() {
		return quiet;
	}
	
	public boolean help() {
		return help;
	}
	
	public boolean html() {
		return html;
	}

	public boolean useFile() {
		return useFile;
	}

	public boolean useSysIn() {
		return useSysIn;
	}

	public String filePath() {
		return filePath;
	}

	public String outputfilePath() {
		return outputFilePath;
	}
	
	public String[] getIncludes() {
		return includes;
	}

	public boolean outputUseFile() {
		return outputUseFile;
	}
	
	public String getHelp() {
		
		return ("Usage:\n\n" +
				" -f [file]  Use [file] to read sasl code \n" +
				" -i         Use System.in to read sasl code (default)\n" +
				" -q         Hide all status messages \n" +
				" -o [file]  Write output to [file]\n" +
				" -I [file1;file2;file3;....;fileN]  Include definitions in files seperated by semicolon\n");
	}
}
