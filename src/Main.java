public class Main {
	
	
	
	/**
	 * 
	 * @author Benjamin Böhm, Patrick Brosi
	 *
	 */


	public static void main(String[] args) {

		SettingsReader sr = new SettingsReader(args);
		SASLCoordinator sc = new SASLCoordinator(sr);

		sc.run();
	}
}