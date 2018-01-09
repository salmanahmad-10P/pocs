import java.sql.*;

// javac -cp /usr/share/java/mysql-connector-java.jar StoredProcTest.java
// java -cp .:/usr/share/java/mysql-connector-java.jar StoredProcTest

class StoredProcTest {
    public static void main(String args[]) throws Exception {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver() );

        Connection connObj = DriverManager.getConnection("jdbc:mysql://zareason/jbpm?noAccessToProcedureBodies=true", "bizapp", "bizapp");

        Statement stmt = connObj.createStatement();
        ResultSet rset = stmt.executeQuery("select count(sku) from inventory");
        while (rset.next()){
            System.out.println("main() using sql statement, result = "+rset.getInt(1));
        }
        rset.close();

        CallableStatement cStatement = connObj.prepareCall("{call getSkuCount(?)}");
        cStatement.registerOutParameter(1, Types.INTEGER);
        cStatement.executeQuery();
        int skuCount = cStatement.getInt(1);
        System.out.println("main() using callable statement, result = "+skuCount);

        connObj.close();
    }
}
