import java.sql.DriverManager;
import java.net.*;

public class JDBCDriverTest {
    public static void main(String[] args) {
        String connectionAddress = null;
        try {
            if (args != null && args.length > 0)
                connectionAddress = args[0];
            else {
                InetAddress local = InetAddress.getLocalHost();
                connectionAddress = InetAddress.getLocalHost().getHostAddress();
            }
            System.out.println("connectionAddress = " + connectionAddress);

            // CachedRowSet rowSet = new CachedRowSetImpl();
            // //CachedRowSet rowSet =
            // SearchModule.listUsersInCompany("mountainblasts");;

            Class.forName("org.postgresql.Driver").newInstance();
            java.sql.Connection conn = null;
            String url = "jdbc:postgresql://"+connectionAddress+"/postgres";
            System.out.println("jdbcCompliant = "+DriverManager.getDriver(url).jdbcCompliant());
            //conn = DriverManager.getConnection("jdbc:postgresql://"+connectionAddress+"/jboss-eap-403", "pgadmin", "pgadmin");
            conn = DriverManager.getConnection(url, "postgres", "postgres");
            System.out.println("connection = " + conn);
            conn.setAutoCommit(false);
            java.sql.Statement statement = conn.createStatement();
            java.sql.ResultSet resultSet = null;
            StringBuffer statementBuf = new StringBuffer();
            // statementBuf.append("select Xf$Name from X$File");
            /*
             * statementBuf.append("select userId from user");
             * if(statement.execute(statementBuf.toString())) { resultSet =
             * statement.getResultSet(); rowSet.populate(resultSet);
             * while(rowSet.next()) { String message = rowSet.getString(1);
             * System.out.println("message = "+ message); } }
            statement.execute("show tables");
             */
            conn.commit();
        } catch (Exception x) {
            x.printStackTrace();
        }

    }
}
