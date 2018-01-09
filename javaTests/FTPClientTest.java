import java.io.*;

import org.apache.commons.net.ftp.*;

public class FTPClientTest {
	public static void main(String[] args) {
		FTPClient ftpClient = null;
		String serverId = "172.16.1.30";
		String userId = "anonymous";
		String password = "ratwater";
		String workingDirectory = "\\ReaderConfig";
		String remoteFileName = "AdvReaderConfig.xml";
		String backupFileName = "AdvReaderConfig.bak";
		boolean success = false;
		StringBuffer sBuffer = new StringBuffer();
		OutputStreamWriter oStream = null;

		try {
			sBuffer.append("ftp test");
			ftpClient = new FTPClient();
			ftpClient.connect(serverId);
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				System.out.println("main() " + serverId
						+ " refused FTP server connection");
				return;
			}
			success = ftpClient.login(userId, password);
			printReplyStrings("login", ftpClient.getReplyStrings());
			if (!success) {
				System.out.println("main() unable to login");
				return;
			}

			success = ftpClient.changeWorkingDirectory(workingDirectory);
			printReplyStrings("changeWorkingDir", ftpClient.getReplyStrings());
			if (!success) {
				System.out
						.println("main() unable to change working directory to "
								+ workingDirectory);
				return;
			}

			success = ftpClient.rename(remoteFileName, backupFileName);
			printReplyStrings("rename", ftpClient.getReplyStrings());
			if (!success) {
				System.out
						.println("main() unable to change working directory to "
								+ workingDirectory);
			}
			/*
			 * File fileObj = new File("matricsCrap.txt"); OutputStream
			 * outStream = new FileOutputStream(fileObj); success =
			 * ftpClient.retrieveFile(remoteFileName, outStream);
			 * printReplyStrings("retrieveFile", ftpClient.getReplyStrings());
			 * outStream.flush(); outStream.close();
			 * 
			 * success = ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			 * printReplyStrings("setFileType", ftpClient.getReplyStrings());
			 * if(!success) { System.out.println("setFileType replyCode =
			 * "+ftpClient.getReplyString()); return; }
			 * 
			 * ftpClient.enterRemotePassiveMode();
			 * printReplyStrings("enterRemotePassiveMode",
			 * ftpClient.getReplyStrings());
			 * 
			 * InputStream iStream = new ByteArrayInputStream("This is a
			 * Test".getBytes()); success = ftpClient.storeFile(remoteFileName,
			 * iStream); printReplyStrings("storeFile",
			 * ftpClient.getReplyStrings()); iStream.close();
			 */

			OutputStream opStream = ftpClient.storeFileStream(remoteFileName);
			printReplyStrings("storeFileStream", ftpClient.getReplyStrings());
			if (opStream == null) {
				System.out.println("********* opStream is null");
				return;
			}
			oStream = new OutputStreamWriter(opStream);
			oStream.write(sBuffer.toString(), 0, sBuffer.length());
			oStream.flush();
			oStream.close();
			printReplyStrings("ostream closed", ftpClient.getReplyStrings());

		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				if (ftpClient != null && ftpClient.isConnected()) {
					ftpClient.disconnect();
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}

	public static void printReplyStrings(String command, String[] replyStrings) {
		System.out.println("********************   " + command
				+ "     *****************");
		for (int x = 0; x < replyStrings.length; x++) {
			System.out.println(replyStrings[x]);
		}
	}
}
