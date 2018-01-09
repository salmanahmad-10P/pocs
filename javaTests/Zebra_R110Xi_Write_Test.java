import java.net.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.net.InetAddress;

public class Zebra_R110Xi_Write_Test implements Runnable {
	static final String L3_BLDG_5_RECEIVING_LABEL = "bldg5Receiving";

	// Notification Listener
	private static int notificationListenerPort = 1112;

	private static ServerSocket ss = null;

	private static Socket notificationSocket = null;

	private static BufferedReader notificationInputIO = null;

	private static OutputStream notificationOutputIO = null;

	static Socket s;

	static String serverId;

	static InputStream in = null;

	static BufferedReader bReader = null;

	static OutputStreamWriter out = null;

	static long startTime = 0;

	static long inBetweenTime = 0L;

	static int qtyToPrint = 1;

	private Zebra_R110Xi_Write_Test() {
	}

	public void run() {
		try {
			serverId = InetAddress.getLocalHost().getHostAddress();
			String pString = requestLogFileFromPrinterStatement();
			ss = new ServerSocket(notificationListenerPort);
			System.out
					.println("Just started ServerSocket for IP = " + serverId);
			while (true) {
				notificationSocket = ss.accept();
				notificationInputIO = new BufferedReader(new InputStreamReader(
						notificationSocket.getInputStream()));
				notificationOutputIO = notificationSocket.getOutputStream();
				String notifyString = notificationInputIO.readLine();
				System.out.println("\nrun()  Notification from printer: "
						+ notifyString);
				long duration = System.currentTimeMillis() - startTime;
				System.out.println("\nAverage per tag = "
						+ (new Date(duration).getTime() / qtyToPrint));

				out.write(pString, 0, pString.length());
				out.flush();

				disconnectListenerIO();
			}
		} catch (java.net.SocketException x) {
			System.out.println("Looks like the ServerSocket was closed");
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			disconnectListenerIO();
		}
	}

	private void disconnectListenerIO() {
		try {
			if (notificationOutputIO != null)
				notificationOutputIO.close();
			if (notificationInputIO != null)
				notificationOutputIO.close();
			if (notificationSocket != null)
				notificationSocket.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
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
			Zebra_R110Xi_Write_Test printerObj = new Zebra_R110Xi_Write_Test();
			Thread notificationListenerThread = new Thread(printerObj);
			notificationListenerThread.start();

			s = new Socket(printerId, 9100);
			in = s.getInputStream();
			bReader = new BufferedReader(new InputStreamReader(in));
			out = new OutputStreamWriter(s.getOutputStream());

			String pString = create_READ_WRITE_PrintStatement();
			startTime = System.currentTimeMillis();
			out.write(pString, 0, pString.length());
			out.flush();
			System.out
					.println("Waiting for the printer to complete printing of "
							+ qtyToPrint + " labels");

			String line = null;
			String tagId = null;
			long currentSerialId = 0L;
			while ((line = bReader.readLine()) != null) {
				System.out.println("bReader line = " + line);
				if (line.startsWith("<end"))
					break;
				else if (line.startsWith("<start"))
					continue;
				else if (line.startsWith("R,")) {
					tagId = line.substring(line.indexOf(44, 3) + 1);
					System.out.println("tagId = " + tagId);
				}
			}
			System.out.println("serialId sequencer = "
					+ Long.parseLong(tagId.substring(15, 24), 16));
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				ss.close();

				if (bReader != null)
					bReader.close();
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

	public static String create_READ_WRITE_PrintStatement() throws Exception {
		StringBuffer pBuffer = new StringBuffer();
		pBuffer.append("^XA");

		// Qty To Print
		pBuffer.append("^PQ");
		pBuffer.append(qtyToPrint);
		pBuffer.append("^FS\n");

		// Alert the host when the print request is completed
		pBuffer.append("^SXK,D,Y,Y,");
		pBuffer.append(serverId);
		pBuffer.append(",");
		pBuffer.append(notificationListenerPort);
		pBuffer.append("\n");

		/*
		 * Print 1.5" and stop to read and invert Leaves printer in error mode
		 * with this print job in queue. User can cancel print job manually, if
		 * error condition persists
		 */
		pBuffer.append("^RS,430,,,e^PON\n");

		/* Reference: http://www.acq.osd.mil/log/rfid/purfid_tagspecs_dod_si.htm */
		pBuffer.append("^RFW,H^FD");
		pBuffer.append("2f"); // 8 bit DoD header for 96 bit tag
		pBuffer.append("2"); // 4 bit UID item
		pBuffer.append("5a4131384433"); // 48 bit CAGE CODE
		pBuffer.append("2dfdc1c35"); // 36 bit serial number
		pBuffer.append("^SFHHHHHHHHHHHHHHHHHHHHHHHH,1^FS\n"); // increment by
		// 1
		pBuffer.append("^BY2,2,51^FT65,669^B3N,N,,Y,N\n"); // Bar-Code of new
		// Tag-Id
		pBuffer.append("^FN1^FS\n"); // Print Tag-Id in Text Format
		pBuffer.append("^FN1^RR5^RFR,H,,1^FS\n"); // Read Hexidecimal

		pBuffer.append("^XZ\n");
		System.out.println(pBuffer.toString());
		return pBuffer.toString();
	}
}
