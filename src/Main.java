public class Main {
	public static void main(String[] args) {
		SettingsReader sr = new SettingsReader(args);
		SASLCoordinator sc = new SASLCoordinator(sr);
		sc.run();
	}
}