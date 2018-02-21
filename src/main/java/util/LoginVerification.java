package util;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;

public class LoginVerification implements Initiator {

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		Object user = Sessions.getCurrent().getAttribute("user");
		if (user != null) {
			Executions.sendRedirect("/forum.zul");
		}
	}

}
