import java.sql.DriverManager;
import java.util.*;

public class csv_mysql_conversion {
	public static void main(String[] args) {
		BufferedReader in = null;
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			java.sql.Connection conn = null;
			conn = DriverManager
					.getConnection("jdbc:mysql://rfidgs:3306/wavechain?user=wavechain&password=wavechain");
			System.out.println("connection = " + conn);
			conn.setAutoCommit(false);
			java.sql.Statement statement = conn.createStatement();
			java.sql.ResultSet resultSet = null;

			in = new BufferedReader(new FileReader(new File(args[0])));
			StringBuffer statementBuf = new StringBuffer();
			statement.execute("show tables");
			conn.commit();
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}

	}
}
