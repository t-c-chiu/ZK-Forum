package viewmodel;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;

import model.bean.Article;
import model.bean.User;
import model.service.ForumService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ListboxArticlesVM {

	@WireVariable
	private ForumService forumService;
	private ListModelList<Article> articles;
	private Article currentArticle;

	@Init
	public void init() {
		buildArticleList();

		EventQueues.lookup("refreshArticle", EventQueues.APPLICATION, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				buildArticleList();
			}
		});

		EventQueues.lookup("refreshReplyArticle", EventQueues.APPLICATION, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				currentArticle = forumService.getArticle(currentArticle.getId());
				BindUtils.postNotifyChange(null, null, ListboxArticlesVM.this, "currentArticle");
			}
		});
	}

	private void buildArticleList() {
		articles = new ListModelList<Article>(forumService.getAllArticles());
		BindUtils.postNotifyChange(null, null, ListboxArticlesVM.this, "articles");
	}

	@GlobalCommand
	@NotifyChange("currentArticle")
	public void showContent(@BindingParam("target") Article article) {
		currentArticle = forumService.getArticle(article.getId());
	}

	@Command
	public void reply(@BindingParam("articleId") Integer articleId) {
		Sessions.getCurrent().removeAttribute("modifyId");
		Executions.sendRedirect("postArticle.zul?articleId=" + articleId);
	}

	@Command
	public void requestModify(@BindingParam("target") Article article) {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		if (user.getId() == article.getAuthorId() && article.getChildren().isEmpty()) {
			Sessions.getCurrent().setAttribute("modifyId", article.getId());
			Executions.sendRedirect("postArticle.zul");
		}
	}

	@Command
	@NotifyChange("currentArticle")
	public void delete(@BindingParam("target") Article article) {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		if (user.getId() == article.getAuthorId()) {
			forumService.delete(article.getId());
			EventQueues.lookup("refreshMyArticle", EventQueues.SESSION, true).publish(new Event("onRefresh"));
			if (!article.getChildren().isEmpty())
				EventQueues.lookup("refreshReplyArticle", EventQueues.APPLICATION, true)
						.publish(new Event("onRefresh"));
			currentArticle = forumService.getArticle(currentArticle.getId());
		}
	}

	public Article getCurrentArticle() {
		return currentArticle;
	}

	public ListModelList<Article> getArticles() {
		return articles;
	}

	public void setArticles(ListModelList<Article> articles) {
		this.articles = articles;
	}

	public void setCurrentArticle(Article currentArticle) {
		this.currentArticle = currentArticle;
	}

}
