import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.SearchResult;
import javax.naming.ReferralException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.login.LoginException;

/**
 * Example code for retrieving a Users Primary Group
 * from Microsoft Active Directory via. its LDAP API
 * 
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class LDAPTest {

    public static final String ldapAdServer = "ldaps://ipa2.opentlc.com:636";
     
    public static final String ldapUsername = "uid=jenkinsOnForge,cn=sysaccounts,cn=etc,dc=opentlc,dc=com";
    public static final String ldapPassword = "CU5FxHhoPvdTV9FYPO0K4fTIKKrJLevvU1ieosvI1thVwlDdiM5OWEia3YY80SP9xutUZ9LRbcRhlN60DZWJIWy5pqaMMDkg78scma5EPbViL2wxfYCljzjJXY0MnvWJ";
      
    public static final String ldapSearchBase = "cn=users,cn=accounts,dc=opentlc,dc=com";
    public static final String ldapAccountToLookup = "jbride-redhat.com";

    public static final String rolesCtxDN = "cn=groups,cn=accounts,dc=opentlc,dc=com";
    public static final String roleFilter = "";
    public static final String roleAttributeId = "memberOf";

    public static SearchControls searchControls = null;

    public static void main(String[] args) throws Exception {
        
        
        searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapAdServer);

        //ensures that objectSID attribute values
        //will be returned as a byte[] instead of a String
        //env.put("java.naming.ldap.attributes.binary", "objectSID");
        
        // the following is helpful in debugging errors
        //env.put("com.sun.jndi.ldap.trace.ber", System.err);

        traceLdapEnv(env);
        
        LdapContext ctx = new InitialLdapContext(env, null);
        
        LDAPTest ldap = new LDAPTest();
        
        //1) lookup the ldap account
        SearchResult srLdapUser = ldap.findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
        if(srLdapUser == null) {
            System.out.println("no account found : "+ldapSearchBase+" : "+ldapAccountToLookup);
            return;
        }else{
            System.out.println("srLdapUser = "+srLdapUser);
        }

        ldap.rolesSearch(ctx);
        
    }
    
    public SearchResult findAccountByAccountName(DirContext ctx, String ldapSearchBase, String accountName) throws NamingException {

        String searchFilter = "uid="+accountName;


        NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

        SearchResult searchResult = null;
        if(results.hasMoreElements()) {
             searchResult = (SearchResult) results.nextElement();

            //make sure there is not another item available, there should be only 1 match
            if(results.hasMoreElements()) {
                System.err.println("Matched multiple users for the accountName: " + accountName);
                return null;
            }
        }
        
        return searchResult;
    }

    public void rolesSearch(LdapContext searchContext) throws LoginException {
      Object[] filterArgs = {ldapAccountToLookup, rolesCtxDN};

      NamingEnumeration results = null;
      try {
            System.out.println("rolesCtxDN=" + rolesCtxDN + " roleFilter=" + roleFilter + " filterArgs[0]=" + filterArgs[0] + " filterArgs[1]=" + filterArgs[1]);

         if (roleFilter != null && roleFilter.length() > 0) {
            boolean referralsExist = true;
            while (referralsExist) {
               try {
                  // http://docs.oracle.com/javase/7/docs/api/javax/naming/directory/InitialDirContext.html#search%28javax.naming.Name,%20java.lang.String,%20java.lang.Object[],%20javax.naming.directory.SearchControls%29
                  results = searchContext.search(rolesCtxDN, roleFilter, filterArgs, searchControls);
                  while (results.hasMore()) {
                     SearchResult sr = (SearchResult) results.next();
                     System.out.println("role = "+sr);
                  }
                  referralsExist = false;
               }
               catch (ReferralException e) {
                  searchContext = (LdapContext) e.getReferralContext();
               }
            }
         } else {
            System.out.println("no roleFilter specified");
         }
      } catch (NamingException e) {
         LoginException le = new LoginException("Error finding roles");
         le.initCause(e);
         throw le;
        }
      finally {
         if (results != null) {
            try{
               results.close();
            } catch (NamingException e) {
               System.out.println("Problem closing results");
            }
         }
      }

   }

    public static void traceLdapEnv(Hashtable env) {
         Properties tmp = new Properties();
         tmp.putAll(env);
         System.out.println("Logging into LDAP server, env=" + tmp.toString());
   }

}
