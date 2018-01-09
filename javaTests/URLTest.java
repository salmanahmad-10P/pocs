import java.net.*;
import java.io.*;

public class URLTest {
	public static final String JMX_CONSOLE = "/jmx-console";

	public static final String INDEX = "/index.html";

	public static int httpPort = 0;

	public static void main(String[] args) {
		BufferedReader readFromDeviceIO = null;
		OutputStream outputStream = null;
		Socket sObj = null;
		String line = null;
		try {

			System.out
					.println("*********** URL METHOD ************************");
			String httpPortString = args[0];
			httpPort = Integer.parseInt(httpPortString);
			URL url = new URL(generateCommandURL("10.10.20.3", INDEX));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
			while ((line = readFromDeviceIO.readLine()) != null) {
				System.out.println(line);
			}

			System.out
					.println("*********** SOCKET METHOD ************************");
			sObj = new Socket("10.10.20.3", httpPort);
			outputStream = sObj.getOutputStream();
			StringBuffer oBuffer = new StringBuffer();
			oBuffer.append("GET /index.html HTTP/1.1");
			oBuffer.append("\nHost: 10.10.20.3:2500");
			oBuffer.append("\nConnection: keep-alive");
			oBuffer.append("\nContent-type: application/x-www-form-urlencoded");
			oBuffer.append("\n\n");
			outputStream.write(oBuffer.toString().getBytes());
			outputStream.flush();
			readFromDeviceIO = new BufferedReader(new InputStreamReader(sObj
					.getInputStream()));
			while ((line = readFromDeviceIO.readLine()) != null) {
				System.out.println(line);
			}

		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				if (readFromDeviceIO != null)
					readFromDeviceIO.close();
				if (outputStream != null)
					outputStream.close();
				if (sObj != null)
					sObj.close();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}

	static String generateCommandURL(String deviceAddress, String command)
			throws Exception {
		StringBuffer commandBuf = new StringBuffer();
		commandBuf.append("HTTP://");
		commandBuf.append(deviceAddress);
		if (httpPort != 0) {
			commandBuf.append(":");
			commandBuf.append(httpPort);
		}
		commandBuf.append(command);
		String completeCommandURL = commandBuf.toString();
		commandBuf.delete(0, commandBuf.length());
		return completeCommandURL;
	}
}
