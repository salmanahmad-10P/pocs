import java.net.*;
import java.io.*;

public class AR400_Test {
	static Socket s;

	static String serverId = "192.168.1.10";

	static InputStream in = null;

	static OutputStream out = null;

	public static void main(String args[]) {
		try {
			s = new Socket(serverId, 3000);
			in = s.getInputStream();
			out = s.getOutputStream();
			// s.setSoTimeout(2000);

			byte[] buffer = new byte[256];
			byte[] command = new byte[7];
			command[0] = 0x01;
			command[1] = 0x04;
			command[2] = 0x06;
			command[3] = 0x22;
			command[4] = (byte) 0xb0;
			command[5] = 0x00;
			command[6] = 0x00;

			int csum = matricsCRC((int) 0xBEEF, (int) 5, command);
			command[5] = (byte) (csum & (short) 0xff);
			command[6] = (byte) (csum >> 8);

			while (true) {
				out.write(command, 0, 7);
				// printByteArray(command);

				boolean morePackets = true;
				int tagQuantity = 0;

				while (morePackets) {
					int bytesRead = in.read(buffer);

					/*
					 * byte #4 indicates whether more tag data exists or not
					 * if(buffer[4] == 0) break;
					 */

					/* byte #6 indicates # of tags in data packet */
					tagQuantity = buffer[6] & 0x000000FF;
					System.out.println("# of tags read  = " + tagQuantity);
					if (tagQuantity == 0)
						break;

					int tagIndicatorPosition = 7;
					for (int z = 0; z < tagQuantity; z++) {
						/* get the byte indicator (indicates type of tag) */
						int tagLength = 0;
						if (buffer[tagIndicatorPosition] == 0x000)
							tagLength = 8; // 8 byte EPC tag
						else if (buffer[tagIndicatorPosition] == 0x10)
							tagLength = 12; // 12 byte EPC tag

						int[] tagId = new int[tagLength];
						int tagPosition = 0;
						for (int a = tagIndicatorPosition + tagLength; a > tagIndicatorPosition; a--) {
							tagId[tagPosition] = buffer[a] & 0x000000FF;
							tagPosition++;
						}
						printIntArray(tagId);
						tagIndicatorPosition = tagIndicatorPosition + tagLength
								+ 1;
					}

					if (bytesRead == -1)
						break;
				}
			}
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

	private static int matricsCRC(int crc, int len, byte nbuf[]) {
		int buf[] = new int[256];
		for (int i = 0; i < len + 2; i++) {
			buf[i] = nbuf[i];
			if (buf[i] < 0)
				buf[i] = (buf[i] | 0x80) & 0xff;
		}

		int tempcrc;
		int tseed = crc;
		int nseed = 0;
		for (int dx = 1; dx < len; dx++) {
			tempcrc = tseed;
			tseed = (tseed ^ buf[dx]);
			tseed &= 0xff; // (int) (tseed & (byte) 0xff);
			tseed = matrics_table[tseed];
			nseed = tempcrc;
			nseed = ((nseed >> 8) & 0xff);
			tseed = (tseed ^ nseed);
			nseed = tseed;
		}
		nseed = (nseed ^ 0xffff);
		return nseed;
	}

	static int matrics_table[] = { (int) 0x0000, (int) 0x1189, (int) 0x2312,
			(int) 0x329B, (int) 0x4624, (int) 0x57AD, (int) 0x6536,
			(int) 0x74BF, (int) 0x8C48, (int) 0x9DC1, (int) 0xAF5A,
			(int) 0xBED3, (int) 0xCA6C, (int) 0xDBE5, (int) 0xE97E,
			(int) 0xF8F7, (int) 0x1081, (int) 0x0108, (int) 0x3393,
			(int) 0x221A, (int) 0x56A5, (int) 0x472C, (int) 0x75B7,
			(int) 0x643E, (int) 0x9CC9, (int) 0x8D40, (int) 0xBFDB,
			(int) 0xAE52, (int) 0xDAED, (int) 0xCB64, (int) 0xF9FF,
			(int) 0xE876, (int) 0x2102, (int) 0x308B, (int) 0x0210,
			(int) 0x1399, (int) 0x6726, (int) 0x76AF, (int) 0x4434,
			(int) 0x55BD, (int) 0xAD4A, (int) 0xBCC3, (int) 0x8E58,
			(int) 0x9FD1, (int) 0xEB6E, (int) 0xFAE7, (int) 0xC87C,
			(int) 0xD9F5, (int) 0x3183, (int) 0x200A, (int) 0x1291,
			(int) 0x0318, (int) 0x77A7, (int) 0x662E, (int) 0x54B5,
			(int) 0x453C, (int) 0xBDCB, (int) 0xAC42, (int) 0x9ED9,
			(int) 0x8F50, (int) 0xFBEF, (int) 0xEA66, (int) 0xD8FD,
			(int) 0xC974, (int) 0x4204, (int) 0x538D, (int) 0x6116,
			(int) 0x709F, (int) 0x0420, (int) 0x15A9, (int) 0x2732,
			(int) 0x36BB, (int) 0xCE4C, (int) 0xDFC5, (int) 0xED5E,
			(int) 0xFCD7, (int) 0x8868, (int) 0x99E1, (int) 0xAB7A,
			(int) 0xBAF3, (int) 0x5285, (int) 0x430C, (int) 0x7197,
			(int) 0x601E, (int) 0x14A1, (int) 0x0528, (int) 0x37B3,
			(int) 0x263A, (int) 0xDECD, (int) 0xCF44, (int) 0xFDDF,
			(int) 0xEC56, (int) 0x98E9, (int) 0x8960, (int) 0xBBFB,
			(int) 0xAA72, (int) 0x6306, (int) 0x728F, (int) 0x4014,
			(int) 0x519D, (int) 0x2522, (int) 0x34AB, (int) 0x0630,
			(int) 0x17B9, (int) 0xEF4E, (int) 0xFEC7, (int) 0xCC5C,
			(int) 0xDDD5, (int) 0xA96A, (int) 0xB8E3, (int) 0x8A78,
			(int) 0x9BF1, (int) 0x7387, (int) 0x620E, (int) 0x5095,
			(int) 0x411C, (int) 0x35A3, (int) 0x242A, (int) 0x16B1,
			(int) 0x0738, (int) 0xFFCF, (int) 0xEE46, (int) 0xDCDD,
			(int) 0xCD54, (int) 0xB9EB, (int) 0xA862, (int) 0x9AF9,
			(int) 0x8B70, (int) 0x8408, (int) 0x9581, (int) 0xA71A,
			(int) 0xB693, (int) 0xC22C, (int) 0xD3A5, (int) 0xE13E,
			(int) 0xF0B7, (int) 0x0840, (int) 0x19C9, (int) 0x2B52,
			(int) 0x3ADB, (int) 0x4E64, (int) 0x5FED, (int) 0x6D76,
			(int) 0x7CFF, (int) 0x9489, (int) 0x8500, (int) 0xB79B,
			(int) 0xA612, (int) 0xD2AD, (int) 0xC324, (int) 0xF1BF,
			(int) 0xE036, (int) 0x18C1, (int) 0x0948, (int) 0x3BD3,
			(int) 0x2A5A, (int) 0x5EE5, (int) 0x4F6C, (int) 0x7DF7,
			(int) 0x6C7E, (int) 0xA50A, (int) 0xB483, (int) 0x8618,
			(int) 0x9791, (int) 0xE32E, (int) 0xF2A7, (int) 0xC03C,
			(int) 0xD1B5, (int) 0x2942, (int) 0x38CB, (int) 0x0A50,
			(int) 0x1BD9, (int) 0x6F66, (int) 0x7EEF, (int) 0x4C74,
			(int) 0x5DFD, (int) 0xB58B, (int) 0xA402, (int) 0x9699,
			(int) 0x8710, (int) 0xF3AF, (int) 0xE226, (int) 0xD0BD,
			(int) 0xC134, (int) 0x39C3, (int) 0x284A, (int) 0x1AD1,
			(int) 0x0B58, (int) 0x7FE7, (int) 0x6E6E, (int) 0x5CF5,
			(int) 0x4D7C, (int) 0xC60C, (int) 0xD785, (int) 0xE51E,
			(int) 0xF497, (int) 0x8028, (int) 0x91A1, (int) 0xA33A,
			(int) 0xB2B3, (int) 0x4A44, (int) 0x5BCD, (int) 0x6956,
			(int) 0x78DF, (int) 0x0C60, (int) 0x1DE9, (int) 0x2F72,
			(int) 0x3EFB, (int) 0xD68D, (int) 0xC704, (int) 0xF59F,
			(int) 0xE416, (int) 0x90A9, (int) 0x8120, (int) 0xB3BB,
			(int) 0xA232, (int) 0x5AC5, (int) 0x4B4C, (int) 0x79D7,
			(int) 0x685E, (int) 0x1CE1, (int) 0x0D68, (int) 0x3FF3,
			(int) 0x2E7A, (int) 0xE70E, (int) 0xF687, (int) 0xC41C,
			(int) 0xD595, (int) 0xA12A, (int) 0xB0A3, (int) 0x8238,
			(int) 0x93B1, (int) 0x6B46, (int) 0x7ACF, (int) 0x4854,
			(int) 0x59DD, (int) 0x2D62, (int) 0x3CEB, (int) 0x0E70,
			(int) 0x1FF9, (int) 0xF78F, (int) 0xE606, (int) 0xD49D,
			(int) 0xC514, (int) 0xB1AB, (int) 0xA022, (int) 0x92B9,
			(int) 0x8330, (int) 0x7BC7, (int) 0x6A4E, (int) 0x58D5,
			(int) 0x495C, (int) 0x3DE3, (int) 0x2C6A, (int) 0x1EF1,
			(int) 0x0F78, (int) 0x5818, (int) 0x0088, (int) 0x5788,
			(int) 0x0088, (int) 0x56C8, (int) 0x0088, (int) 0x5868,
			(int) 0x0088 };

}
