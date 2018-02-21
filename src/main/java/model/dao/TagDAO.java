package model.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import model.bean.Tag;

@Repository
public class TagDAO {

	@PersistenceContext
	private EntityManager em;

	public Tag selectByName(String tagName) {
		Tag tag = null;
		try {
			tag = em.createQuery("select t from Tag t where name = :name", Tag.class).setParameter("name", tagName)
					.getSingleResult();
		} catch (Exception e) {
		}
		return tag;
	}

	public Tag insert(Tag newTag) {
		em.persist(newTag);
		em.flush();
		return newTag;
	}

	public List<Tag> selectAllTags() {
		return em.createQuery("select t from Tag t", Tag.class).getResultList();
	}

	public Collection<? extends String> selectAllTagsName() {
		return em.createQuery("select t.name from Tag t", String.class).getResultList();
	}
}
