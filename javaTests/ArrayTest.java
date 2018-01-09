import java.util.Arrays;
import java.io.InputStream;

public class ArrayTest {
	static int[] stopCommand = new int[] { 48, 48 };

	static int[] tagArray1 = new int[] { 0x03, 0x08, 0x05, 0x07, 0xa8, 0x02,
			0x00, 0x10, 0x3e, 0x3a, 0x5e };

	static InputStream in = System.in;

	public static void main(String args[]) throws Exception {
		int[] inboundBuffer = new int[2];
		int x = 0;
		while (true) {
			int z = in.read();
			if (z == '+')
				break;
			else if (z == '\n')
				continue;

			inboundBuffer[x] = z;
			System.out.println("InboundBuffer = "
					+ intArrayToString(inboundBuffer));
			x++;
			if (Arrays.equals(inboundBuffer, stopCommand)) {
				System.out.println("Arrays Match");
				break;
			}
		}
	}

	public static String intArrayToString(int[] array) {
		if (null == array)
			return null;

		StringBuffer s = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			s.append(array[i]);
			s.append(' ');
		}
		s.append('\n');
		return s.toString();
	}
}
