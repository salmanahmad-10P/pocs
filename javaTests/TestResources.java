package mil.vmfxml.services.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Note that this doesn't seem to work
@Path("/test/{parentUri: .*}")
public class TestResources extends VmfXml_Base_ServiceImpl {

	private static Logger log = null;

	/**
	 * sample usage :	curl -X PUT -HContent-Type:text/plain -HAccept:text/plain http://localhost:8888/test/mil/usmc/1stMarDiv/1stReconBn/D_Co/3rdPlt/test
	 */
	@PUT @Path("/test")
	@Produces("text/plain")	
	public String test(@PathParam("parentUri") final String parentUri) throws Exception {
		System.out.println("parentUri = "+parentUri);
		return "successful";
	}

}
