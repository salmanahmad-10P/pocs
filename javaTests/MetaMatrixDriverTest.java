import java.sql.DriverManager;
//import javax.sql.rowset.CachedRowSet;
//import com.sun.rowset.CachedRowSetImpl;
import java.sql.DatabaseMetaData;

public class MetaMatrixDriverTest {
        public static void main(String[] args) {
                try {
                        // CachedRowSet rowSet = new CachedRowSetImpl();
                        // CachedRowSet rowSet =
                        // SearchModule.listUsersInCompany("mountainblasts");;

                        // Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
                        Class.forName("com.metamatrix.jdbc.MMDriver").newInstance();

                        // Class.forName("com.pervasive.jdbc.v2.Driver");
                        java.sql.Connection conn = null;
                        DatabaseMetaData metaData = null;
                        conn = DriverManager.getConnection("jdbc:metamatrix:AMERITRADE_VDB@mm://10.15.36.192:31000", "ameritrade", "poc");
                        metaData = conn.getMetaData();
                        String[] types = { "TABLE" };
                        java.sql.Statement statement = conn.createStatement();
                        java.sql.ResultSet resultSet = null;
                        resultSet = metaData.getTables(null, null, null, types);
                        while (resultSet.next()) {
                                System.out.println(resultSet.getString(2) + " "
                                                + resultSet.getString(3));
                        }
                        /*
                         * StringBuffer statementBuf = new StringBuffer();
                         * //statementBuf.append("select Xf$Name from X$File");
                         * statementBuf.append("select * from tab"); System.out.println("no
                         * problem"); if(statement.execute(statementBuf.toString())) {
                         * resultSet = statement.getResultSet(); while(resultSet.next()) {
                         * System.out.println(resultSet.getString(1)); } }
                         */
                } catch (Exception x) {
                        x.printStackTrace();
                }
        }
}
