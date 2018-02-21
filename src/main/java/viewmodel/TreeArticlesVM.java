package viewmodel;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

import model.bean.Article;
import model.service.ForumService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TreeArticlesVM {

	@WireVariable
	private ForumService forumService;
	private TreeModel<TreeNode<Article>> articlesModel;

	@Init
	public void init() {
		buildArticleTree();

		EventQueues.lookup("refreshArticle", EventQueues.APPLICATION, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				buildArticleTree();
			}
		});

		EventQueues.lookup("refreshReplyArticle", EventQueues.APPLICATION, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				buildArticleTree();
			}
		});
	}

	private void buildArticleTree() {
		Article articleRoot = new Article();
		articleRoot.setTitle("All articles");
		articleRoot.setChildren(forumService.getAllArticles());

		ArticleTreeNode rootNode = constructArticleTreeNode(articleRoot);

		articlesModel = new DefaultTreeModel<Article>(rootNode);
		BindUtils.postNotifyChange(null, null, TreeArticlesVM.this, "articlesModel");
	}

	private ArticleTreeNode constructArticleTreeNode(Article articleRoot) {
		ArticleTreeNode articleNode = new ArticleTreeNode(articleRoot);

		for (Article child : articleRoot.getChildren()) {
			ArticleTreeNode childNode = new ArticleTreeNode(child);
			articleNode.add(childNode);
		}

		return articleNode;
	}

	@Command
	public void loadChildren(@BindingParam("target") ArticleTreeNode node) {
		if (node.getChildren().isEmpty()) {
			Article article = forumService.getArticle(node.getData().getId());
			for (Article child : article.getChildren()) {
				ArticleTreeNode childNode = new ArticleTreeNode(child);
				node.add(childNode);
			}
		}
	}

	public TreeModel<TreeNode<Article>> getArticlesModel() {
		return articlesModel;
	}

	public void setArticlesModel(TreeModel<TreeNode<Article>> articlesModel) {
		this.articlesModel = articlesModel;
	}

}
