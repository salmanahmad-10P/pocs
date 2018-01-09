import javax.naming.InitialContext;
import javax.naming.Context;
import java.util.*;

import com.rfidgs.globaledge.devicemgr.*;
import com.rfidgs.globaledge.util.JNDIUtil;

public class GlobalEdgeSLSBTest {
	public static void main(String[] args) {
		InitialContext jndiContext = null;
		try {
			jndiContext = JNDIUtil.getInitialContext();
			DeviceMgrSLSBHome dSLSBHome = (DeviceMgrSLSBHome) jndiContext
					.lookup("ejb/devicemgr/DeviceMgrSLSB");
			DeviceMgrSLSBRemote dSLSB = dSLSBHome.create();
			System.out.println(dSLSB.test());
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
