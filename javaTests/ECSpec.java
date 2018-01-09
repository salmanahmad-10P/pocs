/*
 * Copyright 2005 Wavechain Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.wavechain.ale;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.singularityoss.devicemgr.DeviceManager;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.wavechain.ale.exception.ECSpecValidationException;
import com.wavechain.ale.exception.ImplementationException;
import com.wavechain.ale.exception.InvalidURIException;
import com.wavechain.ale.exception.NoSuchSubscriberException;
import com.wavechain.system.ECSpecProfile;
import com.wavechain.util.HibernateUtil;
import com.wavechain.util.JMSUtil;
import com.wavechain.util.ServiceLocator;
import com.wavechain.util.StringUtil;
import com.wavechain.util.XMLUtil;

/**
 * @hibernate.class table="ECSpec"
 */
public class ECSpec implements ContentHandler, MessageListener {
	public final static String SPEC_NAME = "specName";

	public final static String READERS = "logicalReaders";

	public final static String READER = "logicalReader";

	public final static String BOUNDARIES = "boundarySpec";

	public final static String REPORT_SPECS = "reportSpecs";

	public final static String REPORT_SPEC = "reportSpec";

	private String specName;

	private Set interrogators; // list of interrogator names

	private ECBoundarySpec boundaries;

	private HashMap reportSpecs;

	private boolean includeSpecInReports = false;

	private Set subscribers;

	private List rawEPCDataList;

	private Queue dataQueue;

	private Logger log = Logger.getLogger(this.getClass());

	private boolean isPoll = false;

	private StringBuffer charArray = new StringBuffer();

	public ECSpec() {
	}

	/*
	 * Constructor Instantiates ECSpec. Instantiating this class fulfills the
	 * "defining" action as per the ALE spec. Afterwards, clients can continue
	 * to "subscribe" to this ECSpec.
	 */
	public ECSpec(String specName, ECBoundarySpec boundaries) {
		log.debug("ECSpec.constructor()");
		this.specName = specName;
		this.boundaries = boundaries;
		try {
			interrogators = new HashSet();
			reportSpecs = new HashMap();
			subscribers = new HashSet();
			rawEPCDataList = new ArrayList();
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}

	/*
	 * poll() As per the ALE spec, when an ECSpec is polled, one event cycle is
	 * executed
	 */
	public ECReports poll() throws ImplementationException {
		log.debug("poll()    " + getSpecName());
		isPoll = true;
		try {
			// if(boundaries.getDuration() != null)
			// initiateEventCycle(boundaries.getDuration());
		} catch (Exception x) {
			x.printStackTrace();
			throw new ImplementationException("poll Exception = " + x);
		} finally {
			try {
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return generateReports();
	}

	/**
	 * onMessage An ECSpec object implements javax.jms.MessageListener and is
	 * listening for incoming raw data files on a queue that was created when
	 * this object is first instantiated. This method is called as each raw
	 * datafile , from each interrogator, is sent to queue
	 */
	public void onMessage(Message message) {
		InputSource inSource = null;
		StringReader stringReader = null;
		try {
			log.debug("ECSpec.onMessage()");
			if (message instanceof TextMessage) {

				String textMessage = ((TextMessage) message).getText();

				log.info("message recieved " + textMessage);

				stringReader = new StringReader(textMessage);

				inSource = new InputSource(stringReader);

				XMLReader xmlReaderObj = XMLReaderFactory
						.createXMLReader("org.apache.xerces.parsers.SAXParser");
				xmlReaderObj.setContentHandler(this);
				xmlReaderObj.setFeature(
						"http://xml.org/sax/features/namespaces", false);
				xmlReaderObj
						.setFeature(
								"http://xml.org/sax/features/namespace-prefixes",
								false);
				// xmlReaderObj.setFeature(
				// "http://apache.org/xml/features/validation/schema", true);
				xmlReaderObj.parse(inSource);

			} else {
				message.clearBody();
			}
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				if (stringReader != null)
					stringReader.close();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}

	public ECReports generateReports() throws ImplementationException {
		ECReports reports;
		try {
			log.debug("generateReports()    " + getSpecName());
			reports = new ECReports(specName);

			/* As per 8.2 */
			if (getIncludeSpecInReports())
				reports.setSpec(this);

			if (boundaries.getDuration() != null)
				reports.setTotalMilliseconds((boundaries.getDuration())
						.getDuration());

			Iterator rsIterator = reportSpecs.keySet().iterator();
			while (rsIterator.hasNext()) {
				String reportName = (String) rsIterator.next();
				ECReportSpec reportSpec = (ECReportSpec) reportSpecs
						.get(reportName);
				ECReport report = reportSpec.generateReport(rawEPCDataList);
				if (report != null) {
					reports.addReport(report);
				}
			}

			if (!isPoll) {
				Document doc = XMLUtil.generateXMLFromECReports(reports);
			}
			return reports;
		} catch (Exception x) {
			x.printStackTrace();
			throw new ImplementationException("Problem attempting to poll "
					+ getSpecName());
		}
	}

	public void registerNotificationURI(String subscriber)
			throws InvalidURIException {
		subscribers.add(subscriber);
	}

	public void unRegisterNotificationURI(String subscriber)
			throws NoSuchSubscriberException {
		subscribers.remove(subscriber);
	}

	/**
	 * @hibernate.id generator-class="assigned"
	 */
	public String getSpecName() {
		return specName;
	}

	public void setSpecName(String x) {
		specName = x;
	}

	/**
	 * @hibernate.set name="interrogators" table="ECSpec_IIds"
	 * @hibernate.collection-key column="specName"
	 * @hibernate.collection-element column="interrogatorId" type="string"
	 */
	public Set getInterrogators() {
		return interrogators;
	}

	public void setInterrogators(Set x) {
		interrogators = x;
	}

	/*
	 * Places string in Set of interrogators Calls method that instantiates IO
	 * threads to reader (if hasn't already been done so by another ECSpec
	 * thread
	 * 
	 */
	public void addInterrogator(String interrogatorId) throws Exception {
		log.debug("addInterogator interrogatorId = " + interrogatorId);
		interrogators.add(interrogatorId);
	}

	public void removeInterrogator(String interrogatorId) {
		getInterrogators().remove(interrogatorId);
	}

	/* NOTE: XDoclet doesn't seem to have a way to indicate --> lazy = "false" */
	/**
	 * @hibernate.many-to-one name="boundaries"
	 *                        class="com.wavechain.ale.ECBoundarySpec"
	 *                        unique="true" cascade="all"
	 */
	public ECBoundarySpec getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(ECBoundarySpec x) {
		boundaries = x;
	}

	/**
	 * @hibernate.set name="subscribers" table="ECSpec_Subscribers"
	 * @hibernate.collection-key column="specName"
	 * @hibernate.collection-element column="subscribers" type="string"
	 */
	public Set getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(Set x) {
		subscribers = x;
	}

	/**
	 * @hibernate.property
	 */
	public boolean getIncludeSpecInReports() {
		return includeSpecInReports;
	}

	public void setIncludeSpecInReports(boolean x) {
		this.includeSpecInReports = x;
	}

	/*
	 * From 8.2 of ALE spec : If the reportSpecs parameter is .......contains
	 * two ECReportSpec instances with the same reportName, then the define and
	 * immediate methods SHALL raise an ECSpecValidation.
	 */
	public void addReportSpec(ECReportSpec x) throws ECSpecValidationException {
		if (reportSpecs.get(x.getReportName()) != null)
			throw new ECSpecValidationException(getSpecName()
					+ "already has a reportSpec with name " + x.getReportName());
		reportSpecs.put(x.getReportName(), x);
	}

	public void deleteReportSpec(ECReportSpec x) {
		reportSpecs.remove(x.getReportName());
	}

	/*
	 * public void close() throws Exception {
	 * JMSUtil.terminateQueue(getSpecName()); }
	 */

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

	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
	}

	public void characters(char buf[], int offset, int len) throws SAXException {
		/*
		 * While parsing a java.io.Reader (as opposed to a java.io.InputStream),
		 * SAX parser calls this method when it encounters the '\n' character
		 * ... no good for me 'cause in doing so, my charArray StringBuffer will
		 * be over-written and populated with a '\n'.
		 */
		if (buf[offset] == '\n')
			return;

		charArray.append(buf, offset, len);
	}

	public void endElement(String nameSpaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("DATA")) {
			log.debug("ECSpec.endElement()  tagName = " + localName
					+ " and value = " + charArray.toString());
			int[] tagId = null;
			try {
				tagId = StringUtil.stringToIntArray(charArray.toString());
			} catch (Exception x) {
				x.printStackTrace();
			}
			rawEPCDataList.add(tagId);
		}
		charArray.delete(0, charArray.length());
	}

	public void endDocument() {
	}

	public boolean registerSpecWithDeviceManagers(Queue dataQueue)
			throws Exception {
		ECTime time = this.getBoundaries().getDuration();

		Iterator iterator = getInterrogators().iterator();
		long endTime = time.getDuration() + System.currentTimeMillis();
		long timeout = 10000;
		while (iterator.hasNext()) {
			String interrogatorId = (String) iterator.next();
			ECSpecProfile specProfile = new ECSpecProfile();
			specProfile.setSpecName(getSpecName());
			specProfile.setEndTime(endTime);
			specProfile.setQueueName(dataQueue.getQueueName());

			DeviceManager dManager = (DeviceManager) ServiceLocator.getService(
					DeviceManager.class, interrogatorId, timeout);
			log.info("Located device manager = " + dManager
					+ " for interrogatorId = " + interrogatorId);
			dManager.registerECSpecProfile(specProfile);
			log.info(getSpecName() + " is registered with DM");
		}
		return true;
	}
}
