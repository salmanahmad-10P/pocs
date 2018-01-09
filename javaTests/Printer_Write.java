import java.net.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.net.InetAddress;

public class Printer_Write {
	static final String L3_BLDG_5_RECEIVING_LABEL = "bldg5Receiving";

	static Socket s;

	static String serverId;

	static InputStream in = null;

	static BufferedReader bReader = null;

	static OutputStreamWriter out = null;

	static long startTime = 0;

	static long inBetweenTime = 0L;

	static int qtyToPrint = 1;

	private Printer_Write() {
	}

	public static void main(String args[]) {
		if (args[0] == null) {
			System.out
					.println("Please pass the printer IP address as the first parameter to this command");
			return;
		}
		String printerId = args[0];
		try {
			qtyToPrint = Integer.parseInt(args[1]);
			s = new Socket(printerId, 9100);
			in = s.getInputStream();
			bReader = new BufferedReader(new InputStreamReader(in));
			out = new OutputStreamWriter(s.getOutputStream());

			String pString = simpleWrite();
			out.write(pString, 0, pString.length());
			out.flush();

			String line = null;
			while ((line = bReader.readLine()) != null) {
				System.out.println("bReader line = " + line);
				if (line.startsWith("<end"))
					break;
				else if (line.startsWith("<start"))
					continue;
				else if (line.startsWith("R,")) {
					// System.out.println("tagId =
					// "+line.substring(line.indexOf(44,3) + 1));
					System.out.println("tagId = " + line);
				}
			}

			for (int d = 0; d < qtyToPrint; d++) {
				inBetweenTime = System.currentTimeMillis();
				out.write(pString, 0, pString.length());
				out.flush();

				char[] tagId = new char[24];
				for (int t = 0; t < 24; t++) {
					int x = in.read();
					tagId[t] = (char) x;
				}
				long duration = System.currentTimeMillis() - inBetweenTime;
				System.out.println("tagId = " + new String(tagId)
						+ " : duration = " + (new Date(duration).getTime()));
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

	private static String requestLogFileFromPrinterStatement() throws Exception {
		StringBuffer pBuffer = new StringBuffer();
		pBuffer.append("^XA\n");
		pBuffer.append("^HL\n"); // Send log to the host, then clear log and
		// start new logging
		pBuffer.append("^XZ\n");
		return pBuffer.toString();
	}

	public static String simpleWrite() throws Exception {
		StringBuffer pBuffer = new StringBuffer();
		pBuffer.append("^XA\n");
		pBuffer.append("^RS2\n"); // Read Class 0+
		pBuffer.append("^FO50,50^A0N,65^FN3^FS");
		pBuffer.append("^RFW,H^FD0102030405^FS");
		pBuffer.append("^FN3^RFR,H^FS");
		pBuffer.append("^HV0,96\n");
		pBuffer.append("^XZ");
		return pBuffer.toString();
	}
}
