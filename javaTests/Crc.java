public class Crc {
	static int crc = 0xFFFF;

	public static void main(String args[]) {
		int[] iArray = new int[3];
		iArray[0] = 0x05;
		iArray[1] = 0x00;
		iArray[2] = 0x00;
		System.out.println("crc = " + compute(iArray, iArray.length));
	}

	private static int compute(int[] iArray, int len) {
		for (int i = 0; i < len; i++) {
			crc = (iArray[i] << 8) ^ crc;
			for (int j = 0; j < 8; j++) {
				if (crc == 0x8000)
					crc = (crc << 1) ^ 0x1021;
				else
					crc <<= 1;
			}
		}
		return (crc ^ 0xFFFF);
	}
}
