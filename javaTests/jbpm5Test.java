import java.sql.DriverManager;
import java.sql.Blob;
import java.net.InetAddress;


/*
    Purpose :
        1)  isolate complaints from postgresql regarding use of queries with auto-commit = true on jbpm5 tables

    Expected Result :
        -- upon execution of this class, should invoke an exception from postgresql as follows :
            org.postgresql.util.PSQLException: Large Objects may not be used in auto-commit mode

    Useage  :  
        0)  populate the jbpm5 'content' table in postgresql via normal usage of jbpm5 gwt-console and human task functionality
        1)  javac jbpm5Test.java
        2)  java -cp .:/path/to/postgresql-jdbc.jar jbpm5Test

            
            -- the above executable can also accept the following optional system properties :

                 property                   default value   
                ----------------------------------------------------
                -DAUTO_COMMIT                   true       
                -DJDBC_URL                      jdbc:postgresql://<HOSTNAME>/jbpm 
                -DDB_USER                       jbpm
                -DDB_PASSWORD                   jbpm
 */
public class jbpm5Test {

    public static final String AUTO_COMMIT = "AUTO_COMMIT";
    public static final String JDBC_URL = "JDBC_URL";
    public static final String DB_USER = "DB_USER";
    public static final String DB_PASSWORD = "DB_PASSWORD";

    public static void main(String[] args) {
        String connectionAddress = null;
        boolean autocommit = true;
        String jdbcUrl = null;
        String dbUser = "jbpm";
        String dbPassword = "jbpm";
        try {
            if(System.getProperty(AUTO_COMMIT) != null)
                autocommit = Boolean.parseBoolean(System.getProperty(AUTO_COMMIT));
            if(System.getProperty(JDBC_URL) != null) {
                jdbcUrl = System.getProperty(JDBC_URL);
            } else {
                InetAddress local = InetAddress.getLocalHost();
                connectionAddress = InetAddress.getLocalHost().getHostAddress();
                StringBuilder jdbcBuilder = new StringBuilder("jdbc:postgresql://");
                jdbcBuilder.append(connectionAddress);
                jdbcBuilder.append("/jbpm");
                jdbcUrl = jdbcBuilder.toString();
            }
            if(System.getProperty(DB_USER) != null)
                dbUser = System.getProperty(DB_USER);
            if(System.getProperty(DB_PASSWORD) != null)
                dbPassword = System.getProperty(DB_PASSWORD);

            StringBuilder sBuilder = new StringBuilder("properties as follows");
            sBuilder.append("\n\tautocommit =\t");
            sBuilder.append(autocommit);
            sBuilder.append("\n\tjdbcUrl =\t");
            sBuilder.append(jdbcUrl);
            sBuilder.append("\n\tdbUser =\t");
            sBuilder.append(dbUser);
            sBuilder.append("\n\tdbPassword =\t");
            sBuilder.append(dbPassword);

            System.out.println(sBuilder.toString());

            Class.forName("org.postgresql.Driver").newInstance();
            java.sql.Connection conn = null;
            conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            conn.setAutoCommit(autocommit);
            java.sql.Statement statement = conn.createStatement();
            java.sql.ResultSet resultSet = null;
            StringBuilder statementBuf = new StringBuilder("select * from content");
            if(statement.execute(statementBuf.toString())) { 
                resultSet = statement.getResultSet();
                while(resultSet.next()) { 
                    int id = resultSet.getInt(1);
                    Blob content = resultSet.getBlob(2);
                    System.out.println("main() id = "+id+"/tcontent = "+content);
                }
            } else {
                System.out.println("main() result set is empty");
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
