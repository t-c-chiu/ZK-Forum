package viewmodel;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import model.service.ForumService;
import model.service.LoginService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LoginVM {

	@WireVariable
	private LoginService loginService;
	@WireVariable
	private ForumService forumService;
	private String account;
	private String password;
	private boolean failLogin = false;

	@Command
	@NotifyChange("failLogin")
	public void login() {
		boolean isLogin = false;
		if (account != null && password != null)
			isLogin = loginService.login(account, password);
		if (isLogin)
			Executions.sendRedirect("/forum.zul");
		else
			failLogin = true;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isFailLogin() {
		return failLogin;
	}

	public void setFailLogin(boolean failLogin) {
		this.failLogin = failLogin;
	}

}
