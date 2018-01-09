public class BinaryTest {
	public static void main(String args[]) {
		try {
			long longValue = 12345678901L;
			System.out.println("longValue = " + longValue);
			System.out.println("base2 = " + Long.toString(longValue, 2));
			System.out.println("base16 = " + Long.toString(longValue, 16));

			String sequencerHexString = "2F25A41313844332DFDC1C35".substring(
					15, 24);
			long parsedLong = Long.parseLong("2dfdc1c35", 16);
			System.out.println("sequencerHexString = " + sequencerHexString);
			System.out.println("parsedLong = " + parsedLong);

			byte[] cageCodeArray = "2S194".getBytes();
			printByteArrayToHex(cageCodeArray, cageCodeArray.length);

			System.out.println("bitCount = " + Long.bitCount(longValue));
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private static void printByteArrayToHex(byte[] byteArray, int length) {
		StringBuffer buffer = new StringBuffer();
		for (int z = 0; z < length; z++) {
			buffer.append(Integer.toHexString(byteArray[z]));
			buffer.append(' ');
		}
		System.out.println("byteStream = " + buffer.toString());
	}
}
