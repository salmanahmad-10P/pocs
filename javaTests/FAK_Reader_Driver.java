//package com.rfidgs.globalrecord;

import java.net.Socket;

import java.util.*;
import java.io.*;
import java.net.*;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

//import	com.rfidgs.globaledge.devicemgr.interrogatorIO.IRFIDReader;

public class FAK_Reader_Driver extends XR400_Symbol_XMLDriver {
	public static void main(String args[]) {
		new FAK_Reader_Driver();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
		if (qName.equals(XR400_Symbol_XMLDriver.TAG)) {
			for (int a = 0; a < attrs.getLength(); a++) {
				System.out.println("startElement attribute name = "
						+ attrs.getQName(a) + ": value = " + attrs.getValue(a));
			}
		}
	}
}
