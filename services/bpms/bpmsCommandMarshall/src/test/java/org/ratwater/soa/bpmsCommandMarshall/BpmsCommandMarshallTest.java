package org.ratwater.soa.bpmsCommandMarshall;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.drools.core.command.runtime.process.StartProcessCommand;
import org.kie.api.command.Command;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.acme.insurance.Policy;
import org.acme.insurance.Driver;

public class BpmsCommandMarshallTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String pInstanceId = "0";
		String deploymentId = "testDeployment";
		Policy pObj = new Policy();
		Driver dObj = new Driver();
		dObj.setAge(20);
		dObj.setDriverName("Azra and Alex");
		dObj.setNumberOfAccidents(0);
		dObj.setNumberOfTickets(1);
		pObj.setPolicyType("AUTO");
		pObj.setDriver(dObj);
		pObj = null;
		Map<String, Object> payloadMap = new HashMap<String, Object>();
		payloadMap.put("policy", pObj);
		Command<?> sProcessCommand = new StartProcessCommand(pInstanceId, payloadMap);
		JaxbCommandsRequest jaxbCRequest = new JaxbCommandsRequest(deploymentId, sProcessCommand);
		
		Class<?> [] types = new Class<?>[2];
		types[0] = JaxbCommandsRequest.class;
		types[1] = Policy.class;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(types);
			
			Marshaller jcMarshaller = jaxbContext.createMarshaller();
			
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			jcMarshaller.marshal(jaxbCRequest, oStream);
			byte[] marshalledBytes = oStream.toByteArray();
			oStream.close();
			System.out.println("test() # of marshalled bytes = "+marshalledBytes.length);
			
			ByteArrayInputStream iStream = new ByteArrayInputStream(marshalledBytes);
			Unmarshaller jcUMarshaller = jaxbContext.createUnmarshaller();
			Object uMarshalledObj = jcUMarshaller.unmarshal(iStream);
			System.out.println("test() uMarshalledObj = "+uMarshalledObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
