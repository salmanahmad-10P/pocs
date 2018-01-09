import java.net.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.net.InetAddress;

public class Zebra_R110Xi_Write_Basic implements Runnable {
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

	private Zebra_R110Xi_Write_Basic() {
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
			Zebra_R110Xi_Write_Basic printerObj = new Zebra_R110Xi_Write_Basic();
			Thread notificationListenerThread = new Thread(printerObj);
			notificationListenerThread.start();

			s = new Socket(printerId, 9100);
			in = s.getInputStream();
			bReader = new BufferedReader(new InputStreamReader(in));
			out = new OutputStreamWriter(s.getOutputStream());
			// s.setSoTimeout(1800);

			String pString = create_READ_WRITE_PrintStatement();
			startTime = System.currentTimeMillis();
			out.write(pString, 0, pString.length());
			out.flush();
			System.out
					.println("Waiting for the printer to complete printing of "
							+ qtyToPrint + " labels");

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
					// System.out.println("tagId = "+line);
				}
			}
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
		pBuffer.append("^FT25,75^A0N,50,30^FDTAG DATA SHOULD BE: ^FS");
		pBuffer.append("^FT101,135^A0N,50,30^FD 0123456789ABCDEF01234567^FS");
		pBuffer.append("^FT25,195^A0N,50,30^FDTAG DATA AS READ FROM TAG:^FS");
		pBuffer.append("^FT101,255^A0N50,30^FN1^FS");
		pBuffer.append("^RS,1764,200^FS"); // Format 9
		// pBuffer.append("^RS,1176,200^FS"); //Format 3
		pBuffer.append("^WT,10,,,1^FD01234567879ABCDEF01234567^FS");
		pBuffer.append("^RT1,,,1,10^FS");
		pBuffer.append("^XZ");
		return pBuffer.toString();
	}
}
