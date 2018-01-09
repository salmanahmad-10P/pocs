import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class MPR2010_Test {
	static Socket s;

	static InputStream in;

	static OutputStream out;

	static String serverId = "wavechain";

	public static void main(String args[]) {
		try {
			s = new Socket(serverId, 4000);
			in = s.getInputStream();
			out = s.getOutputStream();
			flushInitialGreeting();
			// s.setSoTimeout(2000);

			int[] bCommand = { 0x07, 0x14, 0x01, 0x00, 0x00 };
			out.write(compute(bCommand));
			out.flush();
			if (in.read() != 0) {
				System.out.println("Command is not Valid!");
				return;
			}

			int byteRead = 0;
			int[] tagId = null;
			int tagByteQuantity = 0;
			while (true)
			// for(int f = 0; f< 20; f++)
			{
				int tagType = in.read();
				if (tagType == 0x0D) // 8 byte tagId
					tagByteQuantity = 8;
				else if (tagType == 0x11) // 12 byte tagId
					tagByteQuantity = 12;
				else
					System.out
							.println("tagByteQuantity is not a valid identifier = "
									+ tagByteQuantity);
				tagId = new int[tagByteQuantity];
				in.read();
				in.read();
				for (int t = 0; t < tagByteQuantity; t++) {
					tagId[t] = in.read();
				}
				in.read();
				in.read();
				printIntArray(tagId);
			}
			// out.write(0x00);
			// out.flush();

		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (s != null)
					s.close();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}

	}

	public static void printIntArray(int[] intArray) {
		StringBuffer buffer = new StringBuffer();
		for (int z = 0; z < intArray.length; z++) {
			buffer.append(Integer.toHexString(intArray[z]));
			buffer.append(' ');
		}
		System.out.println("tagId = " + buffer.toString());
	}

	public static void printByteArray(byte[] byteArray) {
		StringBuffer buffer = new StringBuffer();
		for (int z = 0; z < byteArray.length; z++) {
			buffer.append(Integer.toHexString(byteArray[z]));
			buffer.append(' ');
		}
		System.out.println("tagId = " + buffer.toString());
	}

	private static void flushInitialGreeting() throws Exception {
		StringBuffer greeting = new StringBuffer();
		while (true) {
			greeting.append((char) in.read());
			if (greeting.toString().lastIndexOf("MODULE") != -1)
				break;
		}
		System.out.println("flishInitialGreeting = " + greeting.toString());
	}

	public static byte[] compute(int[] buffer) {
		int count = buffer.length;
		int register = 0xffff;
		byte[] bArray = new byte[buffer.length + 2];
		for (int t = 0; t < buffer.length; t++) {
			bArray[t] = (byte) buffer[t];
		}

		while (count > 0) {
			int element = buffer[buffer.length - count];
			int t = ((int) ((register >>> 8) ^ element) & 0xff);
			register <<= 8;
			register ^= table[t];
			count--;
		}

		register ^= 0xFFFF;
		int upperByte = register >> 8;
		int lowerByte = register & 0x000000FF;
		bArray[buffer.length] = (byte) upperByte;
		bArray[buffer.length + 1] = (byte) lowerByte;
		return bArray;
	}

	public static final int[] table = new int[] { (int) 0x0000, (int) 0x1021,
			(int) 0x2042, (int) 0x3063, (int) 0x4084, (int) 0x50a5,
			(int) 0x60c6, (int) 0x70e7, (int) 0x8108, (int) 0x9129,
			(int) 0xa14a, (int) 0xb16b, (int) 0xc18c, (int) 0xd1ad,
			(int) 0xe1ce, (int) 0xf1ef, (int) 0x1231, (int) 0x0210,
			(int) 0x3273, (int) 0x2252, (int) 0x52b5, (int) 0x4294,
			(int) 0x72f7, (int) 0x62d6, (int) 0x9339, (int) 0x8318,
			(int) 0xb37b, (int) 0xa35a, (int) 0xd3bd, (int) 0xc39c,
			(int) 0xf3ff, (int) 0xe3de, (int) 0x2462, (int) 0x3443,
			(int) 0x0420, (int) 0x1401, (int) 0x64e6, (int) 0x74c7,
			(int) 0x44a4, (int) 0x5485, (int) 0xa56a, (int) 0xb54b,
			(int) 0x8528, (int) 0x9509, (int) 0xe5ee, (int) 0xf5cf,
			(int) 0xc5ac, (int) 0xd58d, (int) 0x3653, (int) 0x2672,
			(int) 0x1611, (int) 0x0630, (int) 0x76d7, (int) 0x66f6,
			(int) 0x5695, (int) 0x46b4, (int) 0xb75b, (int) 0xa77a,
			(int) 0x9719, (int) 0x8738, (int) 0xf7df, (int) 0xe7fe,
			(int) 0xd79d, (int) 0xc7bc, (int) 0x48c4, (int) 0x58e5,
			(int) 0x6886, (int) 0x78a7, (int) 0x0840, (int) 0x1861,
			(int) 0x2802, (int) 0x3823, (int) 0xc9cc, (int) 0xd9ed,
			(int) 0xe98e, (int) 0xf9af, (int) 0x8948, (int) 0x9969,
			(int) 0xa90a, (int) 0xb92b, (int) 0x5af5, (int) 0x4ad4,
			(int) 0x7ab7, (int) 0x6a96, (int) 0x1a71, (int) 0x0a50,
			(int) 0x3a33, (int) 0x2a12, (int) 0xdbfd, (int) 0xcbdc,
			(int) 0xfbbf, (int) 0xeb9e, (int) 0x9b79, (int) 0x8b58,
			(int) 0xbb3b, (int) 0xab1a, (int) 0x6ca6, (int) 0x7c87,
			(int) 0x4ce4, (int) 0x5cc5, (int) 0x2c22, (int) 0x3c03,
			(int) 0x0c60, (int) 0x1c41, (int) 0xedae, (int) 0xfd8f,
			(int) 0xcdec, (int) 0xddcd, (int) 0xad2a, (int) 0xbd0b,
			(int) 0x8d68, (int) 0x9d49, (int) 0x7e97, (int) 0x6eb6,
			(int) 0x5ed5, (int) 0x4ef4, (int) 0x3e13, (int) 0x2e32,
			(int) 0x1e51, (int) 0x0e70, (int) 0xff9f, (int) 0xefbe,
			(int) 0xdfdd, (int) 0xcffc, (int) 0xbf1b, (int) 0xaf3a,
			(int) 0x9f59, (int) 0x8f78, (int) 0x9188, (int) 0x81a9,
			(int) 0xb1ca, (int) 0xa1eb, (int) 0xd10c, (int) 0xc12d,
			(int) 0xf14e, (int) 0xe16f, (int) 0x1080, (int) 0x00a1,
			(int) 0x30c2, (int) 0x20e3, (int) 0x5004, (int) 0x4025,
			(int) 0x7046, (int) 0x6067, (int) 0x83b9, (int) 0x9398,
			(int) 0xa3fb, (int) 0xb3da, (int) 0xc33d, (int) 0xd31c,
			(int) 0xe37f, (int) 0xf35e, (int) 0x02b1, (int) 0x1290,
			(int) 0x22f3, (int) 0x32d2, (int) 0x4235, (int) 0x5214,
			(int) 0x6277, (int) 0x7256, (int) 0xb5ea, (int) 0xa5cb,
			(int) 0x95a8, (int) 0x8589, (int) 0xf56e, (int) 0xe54f,
			(int) 0xd52c, (int) 0xc50d, (int) 0x34e2, (int) 0x24c3,
			(int) 0x14a0, (int) 0x0481, (int) 0x7466, (int) 0x6447,
			(int) 0x5424, (int) 0x4405, (int) 0xa7db, (int) 0xb7fa,
			(int) 0x8799, (int) 0x97b8, (int) 0xe75f, (int) 0xf77e,
			(int) 0xc71d, (int) 0xd73c, (int) 0x26d3, (int) 0x36f2,
			(int) 0x0691, (int) 0x16b0, (int) 0x6657, (int) 0x7676,
			(int) 0x4615, (int) 0x5634, (int) 0xd94c, (int) 0xc96d,
			(int) 0xf90e, (int) 0xe92f, (int) 0x99c8, (int) 0x89e9,
			(int) 0xb98a, (int) 0xa9ab, (int) 0x5844, (int) 0x4865,
			(int) 0x7806, (int) 0x6827, (int) 0x18c0, (int) 0x08e1,
			(int) 0x3882, (int) 0x28a3, (int) 0xcb7d, (int) 0xdb5c,
			(int) 0xeb3f, (int) 0xfb1e, (int) 0x8bf9, (int) 0x9bd8,
			(int) 0xabbb, (int) 0xbb9a, (int) 0x4a75, (int) 0x5a54,
			(int) 0x6a37, (int) 0x7a16, (int) 0x0af1, (int) 0x1ad0,
			(int) 0x2ab3, (int) 0x3a92, (int) 0xfd2e, (int) 0xed0f,
			(int) 0xdd6c, (int) 0xcd4d, (int) 0xbdaa, (int) 0xad8b,
			(int) 0x9de8, (int) 0x8dc9, (int) 0x7c26, (int) 0x6c07,
			(int) 0x5c64, (int) 0x4c45, (int) 0x3ca2, (int) 0x2c83,
			(int) 0x1ce0, (int) 0x0cc1, (int) 0xef1f, (int) 0xff3e,
			(int) 0xcf5d, (int) 0xdf7c, (int) 0xaf9b, (int) 0xbfba,
			(int) 0x8fd9, (int) 0x9ff8, (int) 0x6e17, (int) 0x7e36,
			(int) 0x4e55, (int) 0x5e74, (int) 0x2e93, (int) 0x3eb2,
			(int) 0x0ed1, (int) 0x1ef0 };

}
