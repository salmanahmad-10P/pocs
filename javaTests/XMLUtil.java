package com.wavechain.utilities;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.xml.validation.Validator;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class XMLUtil  {
	public static final SimpleDateFormat sdfObj = new SimpleDateFormat("yyyy-MM-dd_k:mm");
	public static final String XML_DOCUMENT_IMPL = "org.apache.xerces.dom.DocumentImpl";
	static Class foDocClass = null;
	static Class docClass = null;
	static Logger log = null;

	static 
	{
		try 
		{
			foDocClass = Class.forName(XML_DOCUMENT_IMPL);
			docClass = Class.forName(XML_DOCUMENT_IMPL);
			log = Logger.getLogger("XMLUtil");
		} 
		catch (java.lang.ClassNotFoundException x) 
		{
			x.printStackTrace();
		}
	}

	public static void dumpNodeDetails(Node node) throws Exception {
		NamedNodeMap attributes = node.getAttributes();
		for(int t=0; t < attributes.getLength(); t++) {
			Node aNode = attributes.item(t);
			log.info("aName = "+aNode.getNodeName()+" : aValue = "+aNode.getNodeValue());
		}
	}

	public static InputSource createInputSourceFromDocument(Document doc) throws Exception
	{
		StringWriter sWriter = null;
		try 
		{
			OutputFormat format = new OutputFormat(doc, "UTF-8", true);
			sWriter = new StringWriter();
			XMLSerializer xmlSerializer = new XMLSerializer(sWriter, format);
			xmlSerializer.serialize(doc);

			InputSource iSource = new InputSource(new StringReader(sWriter
					.toString()));
			return iSource;
		}
		finally 
		{
			if (sWriter != null)
				sWriter.close();
		}
	}

	public static void createElementAndAppend(String name, int value,
			Document doc, Element appendeeElement, String attributeName,
			String attributeValue) 
	{
		Element newElement = doc.createElement(name);
		Text text = doc.createTextNode(String.valueOf(value));
		newElement.appendChild(text);
		if (attributeName != null && !attributeName.equals("")) 
		{
			newElement.setAttribute(attributeName, attributeValue);
		}
		appendeeElement.appendChild(newElement);
	}

	public static void createElementAndAppend(String name, double value,
			Document doc, Element appendeeElement, String attributeName,
			String attributeValue) 
	{
		Element newElement = doc.createElement(name);
		Text text = doc.createTextNode(String.valueOf(value));
		newElement.appendChild(text);
		if (attributeName != null && !attributeName.equals("")) 
		{
			newElement.setAttribute(attributeName, attributeValue);
		}
		appendeeElement.appendChild(newElement);
	}

	public static void createElementAndAppend(String name, Date value,
			Document doc, Element appendeeElement, String attributeName,
			String attributeValue) 
	{
		Element newElement = null;
		if (value == null) 
		{

			log.info("XMLUtil.createElementAndAppend()  value == null for name = "
							+ name);
			newElement = doc.createElement(name);
			Text text = doc.createTextNode("");
			newElement.appendChild(text);
		} else {
			newElement = doc.createElement(name);
			Text text = doc.createTextNode(sdfObj.format(value));
			newElement.appendChild(text);
		}
		if (attributeName != null && !attributeName.equals("")) {
			newElement.setAttribute(attributeName, attributeValue);
		}
		appendeeElement.appendChild(newElement);
	}

	public static void createElementAndAppend(String name, String value,
			Document doc, Element appendeeElement, String attributeName,
			String attributeValue) {
		if (value == null || value.equals("")) {

			log.info("XMLUtil.createElementAndAppend()  value == null for name = "
							+ name + ".");
			value = "";
		}
		Element newElement = doc.createElement(name);
		Text text = doc.createTextNode(value);
		newElement.appendChild(text);
		if (attributeName != null && !attributeName.equals("")) {
			newElement.setAttribute(attributeName, attributeValue);
		}

		appendeeElement.appendChild(newElement);
	}

	/**
	 * createXMLDoc(String) Given a name for the root element will create and
	 * return a org.w3c.dom.Document with the root node specified. The Document
	 * will be based on the Document implementation as specified in the system
	 * properties file XML_DOCUMENT_CLASS
	 * 
	 * @param String
	 *            rootName
	 * @return Document
	 * @author Peter Manta (after blatant copying of Jeff)
	 */
	public static org.w3c.dom.Document createXMLDoc(String rootName) {
		Class docClass = null;
		Document doc = null;
		Element root = null;
		try {
			docClass = Class.forName(XML_DOCUMENT_IMPL);
			doc = (Document) docClass.newInstance();
			root = doc.createElement(rootName);
			doc.appendChild(root);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return doc;
	}// createXMLDoc

	public static void printDocumentToFile(Document doc, String fileName) 
	{
		//StringWriter xmlWriter = new StringWriter();
		PrintWriter pWriter = null;
		try 
		{
			String dirPath = IOUtil.createDirectoryPath(IOUtil.FILETYPE_TAGIDMAP_PROCESSING, null, null);
			pWriter = IOUtil.createPrintWriter(dirPath, fileName);
			OutputFormat format = new OutputFormat(doc, "UTF-8", true);
			
			XMLSerializer xmlSerializer = new XMLSerializer(pWriter, format);
			xmlSerializer.serialize(doc);
			//log.info(xmlWriter.getBuffer());
		} 
		catch (Exception x) 
		{
		   	log.error("unable to log document",x);
		}
		finally
		{
			if(pWriter != null)
				pWriter.close();
		}
	}

	public static Document generateXMLFromListOfIntArrays(String interrogatorId, List aList)
	{
		Class docClass = null;
		Document doc = null;
		Element root = null;
		PrintWriter xmlWriter = null;
		Iterator iterator = null;
		String reportName = null;

		try {
			log.debug("generateXMLFromListOfIntArrays() ");
			docClass = Class.forName(XML_DOCUMENT_IMPL);
			doc = (Document) docClass.newInstance();
			root = doc.createElement("DATA");

			iterator = aList.iterator();
			while (iterator.hasNext()) {
				int[] iArray = (int[]) iterator.next();
				Element reportElem = doc.createElement("data");
				XMLUtil.createElementAndAppend("data", StringUtil
						.intArrayToString(iArray, true), doc, root, null, null);

			}
			doc.appendChild(root);
			XMLUtil.printDocumentToFile(doc, interrogatorId + ".xml");
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			if (xmlWriter != null)
				xmlWriter.close();
		}
		return doc;
	}

	public static void  validateXML(InputStream schemaStream, InputStream xmlStream) throws Exception {
      	SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
     	Source schemaSource = new StreamSource(schemaStream);
		Schema schema = schemaFactory.newSchema(schemaSource);
		
		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(xmlStream));
		log.info("validateXML() validation successful");
	}
}
