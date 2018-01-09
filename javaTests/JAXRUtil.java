package com.wavechain.utilities;

import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;

import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.Connection;
import javax.xml.registry.RegistryService;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.FederatedConnection;
import javax.xml.registry.BulkResponse;

import javax.xml.registry.infomodel.*;

import org.apache.log4j.Logger;

public class JAXRUtil {
	private static Logger log = Logger.getLogger(JAXRUtil.class);
	Properties scoutProperties = new Properties();

	public JAXRUtil() throws Exception {
        scoutProperties.load(getClass().getResourceAsStream("/scout.properties"));
	}

	public void searchRegistry(InitialContext jnpContext) throws Exception {
		log.info("searchRegistry()");
		ConnectionFactory factory = (ConnectionFactory)jnpContext.lookup("JAXR");

		factory.setProperties(scoutProperties);

		Connection cObj = factory.createConnection(); //access a single registry

/*
	the intent of FederatedConnection is to allow queries to be sent to all of the registries w/ which it is associated 
	& for query results to be merged to create a single result set
		Collection listObj = new ArrayList();
		listObj.add(cObj);
		FederatedConnection fConnection = factory.createFederatedConnection(listObj);
*/

		RegistryService registry = cObj.getRegistryService(); //there is one instance of this object for each registry connection
		BusinessQueryManager bqm = registry.getBusinessQueryManager();
		BulkResponse brObj = bqm.getRegistryObjects();
		Collection services = brObj.getCollection();
		Iterator sIterator = services.iterator();
		while(sIterator.hasNext()) {
			RegistryObject service = (RegistryObject)sIterator.next();
			log.info("searchRegistry() service = "+service);
		}

		BusinessLifeCycleManager blcm = registry.getBusinessLifeCycleManager();
	}

	public void stop() {
		log.info("stop()");
	}
}
