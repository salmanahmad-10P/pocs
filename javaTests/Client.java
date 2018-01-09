import java.util.*;

import org.apache.log4j.*;
import com.lansa.jsm.userdefined.ale.*;

public class Client extends java.lang.Thread {
	int t = 0;

	ALE ale = null;

	Logger log = null;

	public Client(ALE ale, int t) {
		log = Logger.getLogger(this.getClass());
		this.ale = ale;
		this.t = t;
	}

	public void run() {
		try {
			String specName = "spec" + t;
			ECBoundarySpec boundaries;
			ECFilterSpec filter = new ECFilterSpec(new ArrayList(),
					new ArrayList());
			ECGroupSpec group = new ECGroupSpec(new ArrayList());
			ECReportSpec reportSpec = new ECReportSpec("report1", filter, group);
			ECSpec spec;

			/* Create ECBoundarySpec */
			ECTime time = new ECTime();
			time.setDuration(10000L);
			boundaries = new ECBoundarySpec();
			boundaries.setDuration(time);

			/* Create ECSpec */
			spec = new ECSpec(specName, boundaries);
			spec.addReader("COM1");
			spec.addReportSpec(reportSpec);

			ECReports reports = ale.immediate(spec);
			log.debug("finished");
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
