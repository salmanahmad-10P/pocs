//package com.rfidgs.globalrecord;

import java.net.Socket;

import java.util.*;
import java.io.*;
import java.net.*;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

//import	com.rfidgs.globaledge.devicemgr.interrogatorIO.IRFIDReader;

public class CSL_XMLDriver implements Runnable, org.xml.sax.ContentHandler {
	// private static String connectionAddress = "69.154.123.70";
	private static String connectionAddress = "172.16.1.31";

	private static String sessionId = null;

	private static XMLReader xmlReaderObj = null;

	private static ServerSocket ss = null;

	private static int notificationListenerPort = 1112;

	private static InputSource inSource = null;

	private static Socket notificationSocket = null;

	private static StringBuffer charArray = new StringBuffer();

	private static int length = 0;

	private static StringBuffer commandBuf = new StringBuffer();

	static final String TAG = "Tag";

	private static BufferedReader notificationInputIO = null;

	private static OutputStream notificationOutputIO = null;

	static final String ACK = "Ack";

	static final String HTTP_PREFIX = "http://";

	static final String SESSION_ID = "/API?session_id=";

	static final String LOGIN = "/API?command=login&username=root&password=csl2006";

	static final String LOGOUT = "&command=logout";

	static final String GET_VERSION = "/cgi-bin/coreProxy?oper=getVersion"; // starts

	// polling
	// mode

	static final String START_POLLING = "&command=setEventNotificationLink&event_id=event1&server_id=";

	static final String STOP_POLLING = "/cgi-bin/coreProxy?oper=startPolling&onOff=0"; // stops

	// polling
	// mode

	static final String QUERY_TAGS = "/cgi-bin/dataProxy?oper=queryTags&raw=1"; // calls

	// for
	// only
	// the
	// tags
	// reader
	// currently
	// sees

	static final String QUERY_TAGS_INVISIBLE = "/cgi-bin/dataProxy?oper=queryTags&invis=1&raw=1"; // calls

	// for
	// all
	// tags
	// seen
	// by
	// reader
	// since
	// last
	// purge

	// calls for delta since last command and guarantees inclusion of full
	// ReadPointMap
	static final String QUERY_EVENTS_WITH_RPM = "/cgi-bin/dataProxy?oper=queryEvents&map=1&raw=1";

	// calls for delta since last command
	static final String QUERY_EVENTS = "/cgi-bin/dataProxy?oper=queryEvents&raw=1";

	static final String SET_NEW_TAG_NOTIFY = "/cgi-bin/consoleProxy?oper=setNotifyOption&name=New+Tag&option=1&mod=0";

	static final String SET_TAG_NOT_VISIBLE = "/cgi-bin/consoleProxy?oper=setNotifyOption&name=Tag+Not+Visible&option=1&mod=8";

	static final String SAVE_CONFIG_FILE = "/cgi-bin/consoleProxy?oper=saveConfigFile";

	public static void main(String args[]) {
		if (args.length > 0)
			sessionId = args[0];
		new CSL_XMLDriver();
	}

	// Will run XR400 in hybrid mode (from XR400 Interface Control Guide)
	public CSL_XMLDriver() {
		try {
			xmlReaderObj = XMLReaderFactory
					.createXMLReader("org.apache.xerces.parsers.SAXParser");
			xmlReaderObj.setContentHandler(this);
			xmlReaderObj.setFeature("http://xml.org/sax/features/namespaces",
					false);

			// Step #1: login
			if (sessionId == null)
				login();

			// Step #2: put reader in polling mode
			startPolling();

			// Step #3: send reader an initial queryTags command
			// queryTags();

			// Step #4: start listener for queryEvent notifications
			// ss = new ServerSocket(notificationListenerPort);
			// Thread notificationListenerThread = new Thread(this);
			// notificationListenerThread.start();

			// Thread.sleep(2000);

			// Step #5: place reader in notify/subscribe mode
			// setHostLink();

			// Thread.sleep(15000);
			// ss.close();
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				logout();
				// stopPolling();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}

	private static void login() throws Exception {
		BufferedReader readFromDeviceIO = null;
		try {
			System.out.println("login() ");
			StringBuffer loginBuf = new StringBuffer();
			loginBuf.append(HTTP_PREFIX);
			loginBuf.append(connectionAddress);
			loginBuf.append(LOGIN);
			URL url = new URL(loginBuf.toString());
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
			// printBufferedReader(readFromDeviceIO);
			inSource = new InputSource(readFromDeviceIO);
			xmlReaderObj.parse(inSource);
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
	}

	private static void logout() throws Exception {
		BufferedReader readFromDeviceIO = null;
		try {
			System.out.println("logout() sessionId = " + sessionId);
			URL url = new URL(CSL_XMLDriver.generateCommandURL(LOGOUT));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
			printBufferedReader(readFromDeviceIO);
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
	}

	private static void startPolling() throws Exception {
		BufferedReader readFromDeviceIO = null;
		try {
			System.out.println("startPolling()  ");
			URL url = new URL(CSL_XMLDriver.generateCommandURL(START_POLLING));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
	}

	public static void stopPolling() throws Exception {
		BufferedReader readFromDeviceIO = null;
		URL url = null;
		try {
			System.out.println("stopPolling() ");
			url = new URL(CSL_XMLDriver.generateCommandURL(STOP_POLLING));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
		try {
			url = new URL(CSL_XMLDriver.generateCommandURL(SAVE_CONFIG_FILE));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
	}

	public static void queryTags() throws Exception {
		BufferedReader readFromDeviceIO = null;
		try {
			System.out.println("queryTags()");
			URL url = new URL(CSL_XMLDriver.generateCommandURL(QUERY_TAGS));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
			inSource = new InputSource(readFromDeviceIO);
			xmlReaderObj.parse(inSource);
		} finally {
			if (readFromDeviceIO != null) {
				readFromDeviceIO.close();
				readFromDeviceIO = null;
			}
		}
	}

	public void queryEvents() throws Exception {
		BufferedReader readFromDeviceIO = null;
		try {
			System.out.println("queryEvents()");
			URL url = new URL(CSL_XMLDriver.generateCommandURL(QUERY_EVENTS));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
			inSource = new InputSource(readFromDeviceIO);
			xmlReaderObj.parse(inSource);
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
	}

	private static void setHostLink() throws Exception {
		System.out.println("setHostLink()");
		StringBuffer hostLinkBuf = new StringBuffer();
		hostLinkBuf
				.append("/cgi-bin/consoleProxy?oper=setHostLink&link=http\u003A\u002F\u002F");
		hostLinkBuf.append(System.getProperty("java.rmi.server.hostname"));
		hostLinkBuf.append("\u003A1111");
		BufferedReader readFromDeviceIO = null;
		URL url = null;
		try {
			url = new URL(CSL_XMLDriver.generateCommandURL(hostLinkBuf
					.toString()));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
		try {
			url = new URL(CSL_XMLDriver.generateCommandURL(SET_NEW_TAG_NOTIFY));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
		try {
			url = new URL(CSL_XMLDriver.generateCommandURL(SET_TAG_NOT_VISIBLE));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
		try {
			url = new URL(CSL_XMLDriver.generateCommandURL(SAVE_CONFIG_FILE));
			readFromDeviceIO = new BufferedReader(new InputStreamReader(url
					.openStream()));
		} finally {
			if (readFromDeviceIO != null)
				readFromDeviceIO.close();
		}
	}

	private static String generateCommandURL(String command) throws Exception {
		commandBuf.append(HTTP_PREFIX);
		commandBuf.append(connectionAddress);
		commandBuf.append(SESSION_ID);
		commandBuf.append(sessionId);
		commandBuf.append(command);
		String completeCommandURL = commandBuf.toString();
		commandBuf.delete(0, commandBuf.length());
		return completeCommandURL;
	}

	public void startDocument() throws SAXException {
	}

	public void startPrefixMapping(java.lang.String prefix, String uri) {
	}

	public void skippedEntity(java.lang.String name) {
	}

	public void setDocumentLocator(org.xml.sax.Locator locator) {
	}

	public void endPrefixMapping(java.lang.String prefix) {
	}

	public void processingInstruction(java.lang.String target,
			java.lang.String data) {
	}

	public void ignorableWhitespace(char buf[], int offset, int len) {
	}

	public void characters(char buf[], int offset, int len) throws SAXException {
		/*
		 * While parsing a java.io.Reader (as opposed to a java.io.InputStream),
		 * SAX parser calls this method when it encounters the '\n' character
		 * .... no good for me 'cause in doing so, my charArray StringBuffer
		 * will be over-written and populated with a '\n'.
		 */
		if (buf[offset] == '\n')
			return;

		charArray.append(buf, offset, len);
		length = len;
	}

	public void endElement(String nameSpaceURI, String localName, String qName)
			throws SAXException {
		System.out.println("endElement()  qName = " + qName + " : value = "
				+ charArray.toString());
		if (qName.equals(ACK)) {
			sessionId = charArray.substring(15, charArray.length());
		}
		int l = charArray.length();
		charArray.delete(0, l);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
		System.out.println("startElement()  uri = " + uri + " : localName = "
				+ localName + " : qName = " + qName);
	}

	public void endDocument() {
	}

	/**
	 * NotificationListener The purpose of this thread is to listen on a
	 * pre-designated port for notification events sent by the XR400. This
	 * listener will run as an independant thread in the background. When the
	 * XR400 sends a notification to this listener, this listener will in-turn
	 * make a 'queryEvents' request of the reader.
	 */
	public void run() {
		try {
			while (true) {
				notificationSocket = ss.accept();
				notificationInputIO = new BufferedReader(new InputStreamReader(
						notificationSocket.getInputStream()));
				notificationOutputIO = notificationSocket.getOutputStream();
				String notifyString = notificationInputIO.readLine();
				System.out.println("run()  Notification from Reader: "
						+ notifyString);
				if (notifyString.contains("oper=notify")) {
					queryEvents();
				} else if (notifyString.contains("oper=test")) {
				}
				String testReply = "HTTP/1.0 200 OK\nContent-type: text/html\nConnection: close\n\n<?xml version='1.0'?><Matrics><HostAck/></Matrics>";
				notificationOutputIO.write(testReply.getBytes());
				notificationOutputIO.flush();
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

	private static void printBufferedReader(BufferedReader bReader) {
		try {
			String line = null;
			while ((line = bReader.readLine()) != null) {
				System.out.println("bReader line = " + line);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
