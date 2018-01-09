import java.io.*;
import java.util.*;
import javax.comm.*;

public class SerialChat {
	static CommPortIdentifier portIdentifier;

	static SerialPort serialPort;

	public static void main(String[] args) {
		boolean portFound = false;
		String readerOnString = null;
		String defaultPort = "COM1";
		String threadSleep = null;

		if (args.length > 0) {
			readerOnString = args[0];
			defaultPort = args[1];
			threadSleep = args[2];
		} else {
			System.out
					.println("Please provide the length of time (seconds) that you want the reader on for and the port #.");
			System.exit(0);
		}

		try {
			Enumeration ports = CommPortIdentifier.getPortIdentifiers();
			while (ports.hasMoreElements()) {
				System.out.println("port = "
						+ ((CommPortIdentifier) ports.nextElement()).getName());
			}
			/*
			 * portIdentifier =
			 * CommPortIdentifier.getPortIdentifier(defaultPort); serialPort =
			 * (SerialPort) portIdentifier.open("SimpleReadApp", 20000);
			 * serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8,
			 * SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			 * serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			 * System.out.println(" serialPort = "+serialPort);
			 * 
			 * int readerOnInt = Integer.parseInt(readerOnString); long
			 * threadSleepLong = (Long.decode(threadSleep)).longValue();
			 * WriteToReaderThread writer = new
			 * WriteToReaderThread(serialPort.getOutputStream(), readerOnInt,
			 * threadSleepLong); ReadFromReaderThread reader = new
			 * ReadFromReaderThread(serialPort.getInputStream());
			 * writer.start(); reader.start();
			 */

		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}
}

class WriteToReaderThread extends Thread {
	OutputStream writeToReaderStream;

	SerialPort serialPort;

	int readerOnInt;

	long threadSleepLong;

	WriteToReaderThread(OutputStream oStream, int readerOnInt,
			long threadSleepLong) {
		this.writeToReaderStream = oStream;
		this.readerOnInt = readerOnInt;
		this.threadSleepLong = threadSleepLong;
	}

	public void run() {
		int y = 0;
		try {
			String command = "\nread\n";
			byte[] commandBuf = command.getBytes();

			long endTime = (readerOnInt * 1000) + System.currentTimeMillis();
			while (System.currentTimeMillis() < endTime) {
				writeToReaderStream.write(commandBuf);
				writeToReaderStream.flush();
				Thread.sleep(threadSleepLong);
				y++;

			}
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				writeToReaderStream.close();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		System.out
				.println("*********************************  wrote to reader = "
						+ y);
	}
}

class ReadFromReaderThread extends Thread {
	InputStream readFromReaderStream;

	StreamTokenizer sTokenizer;

	ReadFromReaderThread(InputStream in) {
		readFromReaderStream = in;
	}

	public void run() {
		try {
			sTokenizer = new StreamTokenizer(readFromReaderStream);
			String token;
			int y = 0;
			while (sTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
				switch (sTokenizer.ttype) {
				case StreamTokenizer.TT_WORD:
					if (!sTokenizer.sval.startsWith("OK")
							&& !sTokenizer.sval.endsWith("ead")) {
						token = sTokenizer.sval;
						System.out.println("y = " + y + "      Token = "
								+ token);
						y++;
						break;
					}
				case StreamTokenizer.TT_EOL:
					token = new String("End of Line");
					break;
				default: // single character in ttype
					token = String.valueOf((char) sTokenizer.ttype);
					break;
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
