package viewmodel;

import java.util.Date;
import java.util.LinkedList;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

import model.bean.Article;
import model.service.ForumService;

public class ArticleTreeNode extends DefaultTreeNode<Article> {
	private static final long serialVersionUID = 1L;

	private ForumService forumService;

	public ArticleTreeNode(Article article) {
		super(article, new LinkedList<TreeNode<Article>>());
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		forumService = context.getBean(ForumService.class);
	}

	public String getTitle() {
		return getData().getTitle();
	}

	public Date getDate() {
		return getData().getDate();
	}

	public Integer getId() {
		return getData().getId();
	}

	@Override
	public boolean isLeaf() {
		Integer articleId = getData().getId();
		return getData() != null && articleId != null && !forumService.hasChild(articleId);
	}

}
