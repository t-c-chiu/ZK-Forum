package model.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import model.bean.User;

@Repository
public class UserDAO {
	@PersistenceContext
	private EntityManager em;

	public User selectByAccount(String account) {
		User user = null;
		try {
			user = em.createQuery("select u from User u where account = :account", User.class)
					.setParameter("account", account).getSingleResult();
		} catch (Exception e) {
		}
		return user;
	}

}
