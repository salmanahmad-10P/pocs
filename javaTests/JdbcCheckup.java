/*
 * This sample can be used to check the JDBC installation.
 * Just run it and provide the connect information.  It will select
 * "Hello World" from the database.
 *
 * note: jdk1.2 is recommanded. jdk1.1 will also work
 */

// You need to import the java.sql package to use JDBC
import java.sql.*;

// We import java.io to be able to read from the command line
import java.io.*;

class JdbcCheckup {
    public static void main(String args[]) throws SQLException, IOException {
        // Load the Oracle JDBC driver
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

        // Prompt the user for connect information
        System.out.println("Please enter information to test connection to the database");
        String user;
        String password;
        String database;

        user = readEntry("user: ");
        int slash_index = user.indexOf('/');
        if (slash_index != -1) {
            password = user.substring(slash_index + 1);
            user = user.substring(0, slash_index);
        } else
            password = readEntry("password: ");
        database = readEntry("database (a TNSNAME entry, name-value pairs): ");

        System.out.print("Connecting to the database...");
        System.out.flush();

        // Connect to the database
        // You can put a database name after the @ sign in the connection URL.

        System.out.println("Connecting...");
        Connection conn = DriverManager.getConnection("jdbc:oracle:oci8:@"+ database, user, password);
                + database, user, password);

        System.out.println("connected.");

        // Create a statement
        Statement stmt = conn.createStatement();

        // Do the SQL "Hello World" thing
        ResultSet rset = stmt.executeQuery("select 'Hello World' from dual");

        while (rset.next())
            System.out.println(rset.getString(1));

        System.out.println("Your JDBC installation is correct.");

        // close the resultSet
        rset.close();

        // Close the statement
        stmt.close();

        // Close the connection
        conn.close();
    }

    // Utility function to read a line from standard input
    static String readEntry(String prompt) {
        try {
            StringBuffer buffer = new StringBuffer();
            System.out.print(prompt);
            System.out.flush();
            int c = System.in.read();
            while (c != '\n' && c != -1) {
                buffer.append((char) c);
                c = System.in.read();
            }
            return buffer.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }
}
