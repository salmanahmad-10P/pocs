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

import com.sun.rowset.CachedRowSetImpl;

import com.eInnovate.user.*;
import java.util.Date;

public class eInnovateTest {
	public static void main(String[] args) {
		InitialContext jndiContext = null;
		DataSource dsObj = null;
		Connection conn = null;
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, "jnp://user:1099");
			env.put(Context.URL_PKG_PREFIXES,
					"org.jboss.naming:org.jnp.interfaces");
			jndiContext = new InitialContext(env);

			UserSLSBHome uHome = (UserSLSBHome) jndiContext
					.lookup("ejb/user/UserSLSB");
			HashMap newHash = new HashMap();
			newHash.put("userId", "moe");
			UserSLSBRemote uSLSB = uHome.create();

			uSLSB.createUser(newHash);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
