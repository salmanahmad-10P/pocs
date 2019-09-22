import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

public class OracleDriverTest {

    private static String connectionURL="jdbc:oracle:thin:apimgmt/apimgmt@127.0.0.1:1521/ORCLPDB1.localdomain";

    public static void main(String[] args) {
        try {

            System.out.println("main() connectionURL = "+connectionURL);

            Class.forName("oracle.jdbc.OracleDriver").newInstance();

            java.sql.Connection conn = DriverManager.getConnection(connectionURL);
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = { "TABLE" };
            java.sql.Statement statement = conn.createStatement();
            java.sql.ResultSet resultSet = null;
            resultSet = metaData.getTables(null, null, null, types);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
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
