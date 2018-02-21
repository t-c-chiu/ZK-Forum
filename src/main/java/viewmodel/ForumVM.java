package viewmodel;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import model.service.ForumService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ForumVM {

	@WireVariable
	private ForumService forumService;

	@Command
	public void post() {
		Sessions.getCurrent().removeAttribute("modifyId");
		Executions.sendRedirect("/postArticle.zul");
	}

	@Command
	public void logout() {
		Sessions.getCurrent().removeAttribute("user");
		Executions.sendRedirect("/login.zul");
	}

}
