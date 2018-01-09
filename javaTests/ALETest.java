import java.util.*;
import com.lansa.jsm.userdefined.ale.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ALETest {
	public static void main(String args[]) {
		Logger log = Logger.getLogger(ALETest.class);
		try {
			PropertyConfigurator.configure("log4j.properties");

			ALE ale = new ALE();
			for (int t = 0; t < 10; t++) {
				Client aleClient = new Client(ale, t);
				aleClient.start();
			}
		} catch (Exception x) {
			x.printStackTrace();
			System.exit(0);
		}
	}
}
