/**
 * 
 * @author Owner
 */
public class DecToChars {

	/** Creates a new instance of DecToChars */
	public DecToChars() {
		convert();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		DecToChars dtc = new DecToChars();
	}

	public void convert() {
		int i = 0;

		byte[] tagID = { 48, 51, 48, 56, 48, 53, 48, 55, 65, 56, 48, 50, 48,
				48, 49, 48, 48, 48, 49, 66, 69, 48, 50, 66 };

		char[] decToChars = new char[24];

		for (i = 0; i < 24; i++) {

			decToChars[i] = (char) tagID[i];
			// String s = Integer.toHexString(decToChars[i]);
			// System.out.print(decToChars[i]);
		}

		String s = new String(decToChars);
		System.out.println(s);

		// long l = Long.parseLong("A8", 16);

		// int hex =0;
		// for( i=0; i<24; i++) {
		//        
		// hex = tagID[i];
		// hex = hex << 1;
		// i++;
		// hex = hex | tagID[i];
		// String s = Integer.toHexString(hex);
		// System.out.print(s + " ");
		//            
		// }

	}

}
