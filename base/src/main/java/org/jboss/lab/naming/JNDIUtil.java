package org.jboss.lab.jndi;

import javax.naming.*;

public class JNDIUtil {

    private static void dumpTreeEntry(NamingEnumeration<NameClassPair> list, String s) throws NamingException {
        System.out.println("\ndump " + s);
        while (list.hasMore()) {
            NameClassPair ncp = list.next();
            System.out.println(ncp.toString());
            if (s.length() == 0) {
                dumpJndi(ncp.getName());
            } else {
                dumpJndi(s + "/" + ncp.getName());
            }
        }
    }

   
    // to print from root, pass the following value for jndiRoot:  "" 
    public static void dumpJndi(String jndiRoot) {
        try {
            dumpTreeEntry(iniCtx.list(jndiRoot), jndiRoot);
        } catch (NamingException ignore) {
        }
    }
}
