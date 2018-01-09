import javax.naming.InitialContext;
import javax.naming.Context;
import java.util.*;

public class JNDIContextTest {
	public static void main(String[] args) {
		InitialContext jndiContext = null;
		String name = args[0];
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, "jnp://10.10.30.2:1099");
			env.put(Context.URL_PKG_PREFIXES,
					"org.jboss.naming:org.jnp.interfaces");
			jndiContext = new InitialContext(env);

			Enumeration namingEnum = jndiContext.list(name);
			while (namingEnum.hasMoreElements()) {
				System.out.println("name = " + namingEnum.nextElement());
			}

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
