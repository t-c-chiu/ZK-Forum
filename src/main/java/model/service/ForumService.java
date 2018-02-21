package model.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.zk.ui.Sessions;

import model.bean.Article;
import model.bean.Tag;
import model.bean.User;
import model.dao.ArticleDAO;
import model.dao.TagDAO;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
public class ForumService {

	@Autowired
	private ArticleDAO articleDAO;
	@Autowired
	private TagDAO tagDAO;

	private static Logger logger = LoggerFactory.getLogger(ForumService.class);

	public void postArticle(String title, String content, Set<Tag> selectedTags) {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		Article article = new Article();
		article.setTitle(title);
		article.setContent(content);
		article.setDate(new Date());
		article.setStatus(0);
		article.setAuthorId(user.getId());
		article.setTags(recordTags(selectedTags));
		articleDAO.insert(article);

		logger.info("PostArticle : user = {}, title = {}, content = {}, tags = {}", user.getName(), title, content,
				selectedTags);
	}

	public void reply(Integer articleId, String replyTitle, String replyContent, Set<Tag> selectedTags) {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		Article article = new Article();
		article.setAuthorId(user.getId());
		article.setReplyId(articleId);
		article.setTitle(replyTitle);
		article.setContent(replyContent);
		article.setDate(new Date());
		article.setStatus(0);
		article.setTags(recordTags(selectedTags));
		articleDAO.insert(article);

		logger.info("Reply : user = {}, title = {}, content = {}", user.getName(), replyTitle, replyContent);
	}

	public Article modify(Integer articleId, String modifyTitle, String modifyContent, Set<Tag> selectedTags) {
		Article article = articleDAO.selectById(articleId);
		article.setTitle(modifyTitle);
		article.setContent(modifyContent);
		article.setTags(recordTags(selectedTags));
		return article;
	}

	private Set<Tag> recordTags(Set<Tag> selectedTags) {
		Set<Tag> myTags = new HashSet<Tag>();
		for (Tag tag : selectedTags) {
			String tagName = tag.getName();
			Tag existTag = tagDAO.selectByName(tagName);
			if (existTag == null) {
				tagDAO.insert(tag);
				existTag = tagDAO.selectByName(tagName);
			}
			myTags.add(existTag);
		}
		return myTags;
	}

	public Article getArticle(Integer articleId) {
		return articleDAO.selectById(articleId);
	}

	public List<Tag> getAllTags() {
		return tagDAO.selectAllTags();
	}

	public List<Article> getAllArticles() {
		return articleDAO.selectAll();
	}

	public void delete(Integer articleId) {
		Article mainArticle = articleDAO.selectById(articleId);
		deleteRecursively(mainArticle);
	}

	private void deleteRecursively(Article mainArticle) {
		mainArticle.setStatus(1);
		for (Article reply : mainArticle.getChildren()) {
			deleteRecursively(reply);
		}
	}

	public boolean hasChild(Integer articleId) {
		return articleDAO.selectChildrenCount(articleId) > 0;
	}

	public List<String> getLatestArticlesTitle(Integer count) {
		return articleDAO.selectLatestArticlesTitle(count);
	}

	public List<String> getLatestArticlesTitle() {
		return articleDAO.selectLatestArticlesTitle(10);
	}

	public List<String> getLatestTenRepliedArticlesTitle(Integer count) {
		return articleDAO.selectLatestRepliedArticlesTitle(count);
	}

	public List<String> getLatestTenRepliedArticlesTitle() {
		return articleDAO.selectLatestRepliedArticlesTitle(10);
	}

	public List<String> getlatestArticlesRelatedToTheCurrentUserTitle(Integer userId, Integer count) {
		return articleDAO.selectLatestArticlesRelatedToTheCurrentUserTitle(userId, count);
	}

	public List<String> getlatestArticlesRelatedToTheCurrentUserTitle(Integer userId) {
		return articleDAO.selectLatestArticlesRelatedToTheCurrentUserTitle(userId, 10);
	}

}
