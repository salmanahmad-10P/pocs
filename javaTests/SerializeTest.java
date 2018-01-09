import java.io.*;
import java.util.*;

public class SerializeTest {
	public static void main(String args[]) {
		HashMap newHash = new HashMap();
		newHash.put("deviceId", "device1");
		newHash.put("connectionAddress", "172.16.1.131");
		newHash.put("portId", new Integer(9100));
		newHash.put("poNumber", "OU1569");
		newHash.put("lineItemId", "001");
		newHash.put("shipmentNumber", "0001");
		newHash
				.put(
						"itemNomenclature",
						"I tell you the truth, if you have faith as small as a mustard seed, you can say to this mountain, \"Move from here to there and it will move.");
		newHash.put("itemNumber", "5970-01-024-5038");
		newHash.put("qty", "4");
		newHash.put("uom", "EA");
		newHash.put("jobNumber", "2768");
		newHash.put("workNumber", "workNumber");
		newHash.put("releaseNumber", "releaseNumber");
		newHash.put("testObject", new TestObject());

		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(newHash);
			oos.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}

class TestObject {
	public TestObject() {
	}
}
