public class CleanString {
	private static String originalString = "(MOVING TARGET, ONE LOCATION)";

	public static void main(String[] args) {
		System.out.println("cleanString = "+originalString.replaceAll("\u002C", ""));
	}
}
