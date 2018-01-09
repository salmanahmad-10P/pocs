/*
 * Copyright 2005 Wavechain Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.com/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.wavechain.utilities;

import java.util.Hashtable;
import java.util.Enumeration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;

import org.apache.log4j.Logger;

public class JNDIUtil {
	private static Logger log = Logger.getLogger(JNDIUtil.class);
	public static final String TYPE_JNP = "jnp";
	public static final String TYPE_LDAP = "ldap";
	public static final String TYPE_FILESYSTEM = "fs";

	public static Context getContext(String providerURL) throws Exception {
		if(providerURL == null || providerURL.equals(""))
			throw new Exception("getInitialContext() please provide a valid providerURL to this method");
		else if(providerURL.startsWith(TYPE_JNP))
			return getJNPInitialContext(providerURL);
		else if(providerURL.startsWith(TYPE_LDAP))
			return getLDAPInitialContext(providerURL);
		else if(providerURL.startsWith(TYPE_FILESYSTEM))
			return getFSInitialContext(providerURL);
		else
			throw new Exception("getInitialContext() please provide a valid providerURL to this method");
	}

	public static InitialContext getFSInitialContext(String providerURL) throws Exception {
		Hashtable env = new Hashtable(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
		env.put(Context.PROVIDER_URL, providerURL);
		InitialContext initCtx = new InitialContext(env);
		return initCtx;
	}

	/*  example providerURL is as follows:
			"ldap://cib-devldap.usa.wachovia.net:4389/ou=1amn,ou=messaging,dc=wachovia,dc=net"
			"ldap://jbride.csb:3891/dc=csb"
	*/
	public static LdapContext getLDAPInitialContext(String providerURL) throws Exception {
		Hashtable env = new Hashtable(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerURL);
// Create LdapContext with no request or response controls
		LdapContext initCtx = new InitialLdapContext(env, null);
		return initCtx;
	}

/*  1099 for default JNDI registry
	1100 for HA-JNDI registry
	Note:  example of providerURL is as follows:
		jnp://jbride.csb:1099
*/
	public static InitialContext getJNPInitialContext(String providerURL) {
		InitialContext jndiContext = null;
		try {
			if(providerURL == null || providerURL.equals("")) {
				StringBuffer urlBuf = new StringBuffer();
				urlBuf.append("jnp://");
				urlBuf.append(System.getProperty("jboss.bind.address"));
				urlBuf.append(":1099");
				providerURL = urlBuf.toString();
			}

/*
	Secured environment use the following :
Hashtable env = new Hashtable();
String factory = "org.jboss.security.jndi.JndiLoginInitialContextFactory";
env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
String url = "jnp://localhost:1099";
env.put(Context.PROVIDER_URL, url);
env.put(Context.SECURITY_CREDENTIALS, "simple");
env.put(Context.SECURITY_PRINCIPAL, "admin");
Context ctx = new InitialContext(env); 
*/
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, providerURL);
			env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:com.jnp.interfaces");
			jndiContext = new InitialContext(env);
		} catch (NamingException ne)  {
			ne.printStackTrace();
		}
		return jndiContext;
	}

/*  client-side invocation to the following server-side HTTP servlet invoker:
		$JBOSS_HOME/server/xxx/deploy/http-invoker.sar/invoker.war/WEB-INF/web.xml
*/
	public static InitialContext getInitialContextViaHttpInvoker(String jndiServerAddress, int port) {
		InitialContext jndiContext = null;
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, "jnp://" + jndiServerAddress + ":"+port+"/invoker/JNDIFactory");
			env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming");
			jndiContext = new InitialContext(env);
		} catch (NamingException ne)  {
			ne.printStackTrace();
		}
		return jndiContext;
	}

/*
	If the property string java.naming.provider.url is empty or if all servers it mentions are not reachable, the JNP
client will try to discover a bootstrap HA-JNDI server through a multicast call on the network (auto-discovery).
Will call the following method for auto-discovery:
	org.jnp.interfaces.NamingContext.discoverServer(NamingContext.java:1317)

	NOTE:  ensure that '<attribute name="DiscoveryDisabled">false</attribute>' flag is set to false in cluster-service.xml
*/
	public static InitialContext getInitialContextViaAutoDiscovery() {
		InitialContext jndiContext = null;
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, "");
			env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:com.jnp.interfaces");
			env.put("jnp.discoveryGroup", "230.0.0.4");
			env.put("jnp.discoveryPort", "1102");
			jndiContext = new InitialContext(env);
		} catch (NamingException ne)  {
			ne.printStackTrace();
		}
		return jndiContext;
	}

	public static void listBindings(Context iContext, String directoryStartPoint) throws Exception {
		if(iContext == null)
			throw new Exception("listBindings() Please pass a valid InitialContext to this method");

		if(directoryStartPoint == null)
			directoryStartPoint = "";

		Enumeration enumObj = iContext.list(directoryStartPoint);
		if(enumObj != null && enumObj.hasMoreElements()) {
			while(enumObj.hasMoreElements()) {
				javax.naming.NameClassPair nameClassPair = (javax.naming.NameClassPair)enumObj.nextElement();
				try {
					Object boundObject = iContext.lookup(nameClassPair.getName());
					if(boundObject instanceof org.jnp.interfaces.NamingContext) {
						loopThroughBindings(nameClassPair.getName(), (Context)boundObject, 1);
					} else {
						log.info("listBindings() name = "+nameClassPair.getName()+" : class = "+nameClassPair.getClassName());
					}
				} catch(Exception x) {
					log.error("listBindings() nameClassPair = "+nameClassPair+" exception  = "+x);
					x.printStackTrace();
				}
			}
		} else {
			log.error("listBindings() nothing listed at "+directoryStartPoint);
		}
	}
	
	public static void loopThroughBindings(String fullPath, Context iContext, int loop) throws Exception {
		
		Enumeration enumObj = iContext.list("");
		if(enumObj != null && enumObj.hasMoreElements()) {
			StringBuffer sBuffer = new StringBuffer();
			while(enumObj.hasMoreElements()) {
				javax.naming.NameClassPair nameClassPair = (javax.naming.NameClassPair)enumObj.nextElement();
				Object boundObject = iContext.lookup(nameClassPair.getName());
				try {
					if(boundObject instanceof org.jnp.interfaces.NamingContext) {
						loopThroughBindings(fullPath+"/"+nameClassPair.getName(), (Context)boundObject, loop+1);
					} else {
						for(int t = 0; t < loop; t++){
							sBuffer.append("\t");
						}
						sBuffer.append("name = "+ nameClassPair.getName()+" : class = "+nameClassPair.getClassName());
						sBuffer.append("\n");
					}
				}
				catch(Exception x) {
					log.error("listBindings() x = "+x);
				}
			log.info(fullPath+" \n"+sBuffer.toString());
			}
		} else {
			log.error("loopThroughBindings() no bindings at: "+fullPath);
		}
	}

	public static void listJNDIProperties() throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		log.info("listJNDIProperties()");
		for (Enumeration e = loader.getResources("jndi.properties"); e.hasMoreElements(); log.info(e.nextElement())) {}
	}
}
