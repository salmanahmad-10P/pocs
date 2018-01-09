import java.sql.DriverManager;
import java.sql.Connection;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.sql.rowset.CachedRowSet;
import javax.rmi.PortableRemoteObject;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import com.mountainblasts.system.ERPInterfaces.*;
import com.mountainblasts.integration.remoteERP.westt.*;
import com.mountainblasts.quality.*;
import com.mountainblasts.quote.*;
import com.mountainblasts.user.*;

import com.sun.rowset.CachedRowSetImpl;

public class JBossTest {
	public static void main(String[] args) {
		InitialContext jndiContext = null;
		DataSource dsObj = null;
		Connection conn = null;
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.jnp.interfaces.NamingContextFactory");
			// env.put(Context.PROVIDER_URL, "jnp://localhost:1099");
			// env.put(Context.PROVIDER_URL,
			// "jnp://quality.mountainblasts.com:1099");
			env.put(Context.PROVIDER_URL, "jnp://user:1099");
			env.put(Context.URL_PKG_PREFIXES,
					"org.jboss.naming:org.jnp.interfaces");
			jndiContext = new InitialContext(env);
			UserSLSBHome userSLSBHome = (UserSLSBHome) jndiContext
					.lookup("ejb/user/UserSLSB");
			UserSLSBRemote userSLSB = userSLSBHome.create();
			userSLSB.createCompany(new HashMap());

			// QuotesSLSBHome quotesSLSBHome =
			// (QuotesSLSBHome)jndiContext.lookup("ejb/quotes/QuotesSLSB");
			// QuotesSLSBRemote remote = quotesSLSBHome.create();
			// Integer pk = remote.createQuoteRequest(new HashMap());
			// System.out.println("pk = "+pk);

			// CachedResultSet aRS = remote.getSupplierDefectReport("1");
			// System.out.println("# of rows = "+aRS.getRowCount());

			// HashMap newHash = new HashMap();
			// newHash.put("displayName", "crap");
			// remote.createQualityStatus(newHash);
			// IRemoteERPServer erpServer = erpServerHome.create();
			// AssemblyRemote assembly =
			// erpServer.retrieveAssemblyRemote("TEN0003383-001");
			// System.out.println("assembly = "+assembly.getAssemblyId());
			// Object ref = jndiContext.lookup("TestStatelessSessionEJB");
			// TestHome testHome = (TestHome)PortableRemoteObject.narrow(ref,
			// TestHome.class);
			// TestHome testHome =
			// (TestHome)jndiContext.lookup("TestStatelessSessionEJB");
			// TestRemote test = testHome.create();
			// test.testEJB();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
