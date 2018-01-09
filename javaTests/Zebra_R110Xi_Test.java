import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.ArrayList;
import java.net.InetAddress;

public class Zebra_R110Xi_Test implements Runnable {
	static final String L3_BLDG_5_RECEIVING_LABEL = "bldg5Receiving";

	public static final SimpleDateFormat sdfObj = new SimpleDateFormat("MM/yy");

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

	private Zebra_R110Xi_Test() {
	}

	public void run() {
		try {
			serverId = InetAddress.getLocalHost().getHostAddress();
			String pString = requestLogFileFromPrinterStatement();
			ss = new ServerSocket(notificationListenerPort);
			System.out
					.println("Just started ServerSocket for IP = " + serverId);
			while (true) {
				// notificationSocket = ss.accept();
				// notificationInputIO = new BufferedReader(new
				// InputStreamReader(notificationSocket.getInputStream()));
				// notificationOutputIO = notificationSocket.getOutputStream();
				// String notifyString = notificationInputIO.readLine();
				// System.out.println("\nrun() Notification from printer: "+
				// notifyString);
				// long duration = System.currentTimeMillis() - startTime;
				// System.out.println("\nAverage per tag = "+(new
				// Date(duration).getTime() / qtyToPrint));

				Thread.sleep(3000);
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
			Zebra_R110Xi_Test printerObj = new Zebra_R110Xi_Test();
			Thread notificationListenerThread = new Thread(printerObj);
			notificationListenerThread.start();

			s = new Socket(printerId, 9100);
			in = s.getInputStream();
			bReader = new BufferedReader(new InputStreamReader(in));
			out = new OutputStreamWriter(s.getOutputStream());
			// s.setSoTimeout(1800);

			String pString = null;
			// pString = create_New_L3_BLDG_5_RECEIVING_LABEL_PrintStatement();
			pString = create_SPEAR_RECEIVING_LABEL_PrintStatement();
			// pString = create_Dale_NST_PrintStatement();
			startTime = System.currentTimeMillis();
			// out.write(pString, 0, pString.length());
			// out.flush();
			System.out
					.println("Waiting for the printer to complete printing of "
							+ qtyToPrint + " labels");

			String line = null;
			while ((line = bReader.readLine()) != null) {
				// System.out.println("bReader line = "+line);
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

			/*
			 * for(int d = 0; d < qtyToPrint; d++) { inBetweenTime =
			 * System.currentTimeMillis(); out.write(pString, 0,
			 * pString.length()); out.flush();
			 * 
			 * char[] tagId = new char[24]; for(int t = 0; t < 24; t++) { int x =
			 * in.read(); tagId[t] = (char)x; } long duration =
			 * System.currentTimeMillis() - inBetweenTime;
			 * System.out.println("tagId = "+ new String(tagId) +" : duration = "+
			 * (new Date(duration).getTime())); }
			 */
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

	public static String create_SPEAR_RECEIVING_LABEL_PrintStatement()
			throws Exception {
		StringBuffer pBuffer = new StringBuffer();
		pBuffer.append("^XA^RS,438,,,e^POI\n"); // Print 1.5" and stop, then
		// invert
		pBuffer.append("^LH20,0\n"); // Set Initial Origin

		// Alert the host when the print request is completed
		pBuffer.append("^SXK,D,Y,Y,");
		pBuffer.append(serverId);
		pBuffer.append(",");
		pBuffer.append(notificationListenerPort);
		pBuffer.append("\n");

		// Print <qtyToPrint> # of labels
		pBuffer.append("^PQ");
		pBuffer.append(qtyToPrint);
		pBuffer.append(",0,0,0,N\n");

		pBuffer.append("^LL0812\n");
		pBuffer.append("^PW812");
		pBuffer.append("^FT687,250^A0N,28,27^FD");
		pBuffer.append(sdfObj.format(new Date()));
		pBuffer.append("^FS");
		pBuffer.append("^FT689,207^A0N,28,27^FDDate^FS");
		pBuffer.append("^FT481,338^A0N,28,27^FD");

		pBuffer.append("CAGE123123");

		pBuffer.append("^FS");
		pBuffer.append("^FT479,306^A0N,28,27^FDCAGE^FS");
		pBuffer.append("^FT52,201^A0N,28,27^FDNomenclature^FS");
		pBuffer.append("^FT52,64^A0N,28,27^FDNSN/MSN^FS");
		pBuffer.append("^FT406,64^A0N,28,27^FDPN^FS");
		pBuffer.append("^BY1,3,47^FT59,388^B3N,N,,N,N");
		pBuffer.append("^FD");
		pBuffer.append("PO@#$@#$"); // PO Bar-Code
		pBuffer.append("^FS");
		pBuffer.append("^BY1,3,51^FT51,153^B3N,N,,N,N");
		pBuffer.append("^FD");
		pBuffer.append("nsn214234"); // NSN Bar-Code
		pBuffer.append("^FS");
		// pBuffer.append("^BY2,2.5,51^FT400,152^B3N,N,,N,N");
		pBuffer.append("^BY1,3,51^FT400,152^B3N,N,,N,N");
		pBuffer.append("^FD");
		pBuffer.append("itemId23234"); // ItemId Bar-Code
		pBuffer.append("^FS");
		pBuffer.append("^BY1,3,51^FT68,500^B3N,N,,N,N");
		pBuffer.append("^FD");
		pBuffer.append("234"); // Qty Bar-Code
		pBuffer.append("^FS");
		String nsn = "";
		pBuffer.append("^FT52,94^A0N,28,27^FD");
		pBuffer.append(nsn);
		pBuffer.append("^FS");

		pBuffer.append("^FT404,93^A0N,28,29^FD");
		pBuffer.append("itemId");
		pBuffer.append("^FS");

		pBuffer.append("^FO4,509^GB792,0,8^FS");
		pBuffer.append("^FO8,400^GB793,0,8^FS");
		pBuffer.append("^FO4,266^GB793,0,8^FS");
		pBuffer.append("^FO6,157^GB793,0,8^FS");

		pBuffer.append("^FT270,598^A0N,28,27^FD");
		pBuffer.append("ccode234234");
		pBuffer.append("^FS");

		pBuffer.append("^FT52,598^A0N,28,27^FD");
		pBuffer.append("lwarehouse234234");
		pBuffer.append("^FS");

		pBuffer.append("^FT52,314^A0N,28,27^FD");
		pBuffer.append("po234234");
		pBuffer.append("^FS");

		pBuffer.append("^FT52,243^A0N,34,31^FD");
		pBuffer.append("nomenclature234234");
		pBuffer.append("^FS");

		pBuffer.append("^FO661,410^GB0,98,8^FS");
		pBuffer.append("^FO542,410^GB0,98,8^FS");
		pBuffer.append("^FO139,410^GB0,98,8^FS");
		pBuffer.append("^FO667,166^GB0,98,8^FS");
		pBuffer.append("^FT674,443^A0N,28,29^FDCU.^FS");
		pBuffer.append("^FT556,443^A0N,28,29^FDWT.^FS");
		pBuffer.append("^BY1,3,51^FT58,664^B3N,N,,N,N");
		pBuffer.append("^FD");
		pBuffer.append("lwarehouser234234");
		pBuffer.append("^FS");

		// pBuffer.append("^BY1,3,51^FT100,769^B3N,N,,Y,N^FD^FN1^FS\n"); //Code
		// 39 Bar-Code
		pBuffer.append("^BY2,2,51^FT65,769^B3N,N,,Y,N");
		pBuffer.append("^FN1^FS");
		pBuffer.append("^FN1^RR10^RFR,H,,1^FS\n"); // Read Hexidecimal
		pBuffer.append("^XZ\n");
		System.out.println("createPrintStatement = " + pBuffer.toString());
		return pBuffer.toString();
	}
}
