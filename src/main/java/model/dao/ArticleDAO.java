package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import model.bean.Article;

@Repository
public class ArticleDAO {
	@PersistenceContext
	private EntityManager em;

	public Integer insert(Article article) {
		em.persist(article);
		em.flush();
		return article.getId();
	}

	public List<String> selectLatestArticlesTitle(Integer count) {
		TypedQuery<String> query = em.createQuery(
				"select a.title from Article a where replyId is null and status = 0 order by date desc", String.class);
		query.setMaxResults(count);
		return query.getResultList();
	}

	public List<String> selectLatestRepliedArticlesTitle(Integer count) {
		TypedQuery<String> query = em.createQuery(
				"select a.title from Article a where replyId is not null and status = 0 order by date desc",
				String.class);
		query.setMaxResults(count);
		return query.getResultList();
	}

	public List<String> selectLatestArticlesRelatedToTheCurrentUserTitle(Integer userId, Integer count) {
		TypedQuery<String> query = em.createQuery(
				"select a.title from Article a where authorId = :authorId and status = 0 order by date desc",
				String.class);
		query.setParameter("authorId", userId);
		query.setMaxResults(count);
		return query.getResultList();
	}

	public List<Article> selectAll() {
		return em.createQuery("select a from Article a where a.replyId is null and status = 0 order by date desc",
				Article.class).getResultList();
	}

	public Article selectById(Integer articleId) {
		Article article = null;
		try {
			article = em.createQuery("select a from Article a where a.id = :id and a.status = 0", Article.class)
					.setParameter("id", articleId).getSingleResult();
		} catch (Exception e) {
		}
		return article;
	}

	public int selectChildrenCount(Integer articleId) {
		Number num = (Number) em
				.createNativeQuery("select count(*) from Article where replyId = :replyId and status = 0")
				.setParameter("replyId", articleId).getSingleResult();
		return num.intValue();
	}

}
