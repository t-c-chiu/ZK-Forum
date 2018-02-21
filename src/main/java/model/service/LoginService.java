package model.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.zk.ui.Sessions;

import model.bean.User;
import model.dao.UserDAO;
import util.Encryption;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
public class LoginService {

	@Autowired
	private UserDAO userDAO;

	private static Logger logger = LoggerFactory.getLogger(LoginService.class);

	public boolean login(String account, String password) {
		User user = userDAO.selectByAccount(account);
		if (user != null) {
			if (user.getPassword().equals(Encryption.SHA1(password))) {
				Sessions.getCurrent().setAttribute("user", user);
				logger.info("User login, userId = {}, userName = {}", user.getId(), user.getName());
				return true;
			}
		}
		return false;
	}
}
