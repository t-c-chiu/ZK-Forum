package viewmodel;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import model.bean.Article;
import model.bean.User;
import model.service.ForumService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MainArticleVM {
	
	@WireVariable
	private ForumService forumService;
	private Article currentArticle;
	private boolean canBeDeleted;
	private boolean canBeModified;

	@GlobalCommand
	@NotifyChange({ "currentArticle", "canBeDeleted", "canBeModified" })
	public void showContent(@BindingParam("target") Article article) {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		int userId = user.getId();
		currentArticle = forumService.getArticle(article.getId());
		canBeModified = userId == article.getAuthorId() && currentArticle.getChildren().isEmpty();
		canBeDeleted = userId == article.getAuthorId();
	}

	@Command
	public void requestModify() {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		if (user.getId() == currentArticle.getAuthorId() && currentArticle.getChildren().isEmpty()) {
			Sessions.getCurrent().setAttribute("modifyId", currentArticle.getId());
			Executions.sendRedirect("postArticle.zul");
		}
	}

	@Command
	@NotifyChange("currentArticle")
	public void delete() {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		if (currentArticle.getAuthorId() == user.getId()) {
			forumService.delete(currentArticle.getId());
			EventQueues.lookup("refreshMyArticle", EventQueues.SESSION, true).publish(new Event("onRefresh"));
			if (currentArticle.getParent() == null)
				EventQueues.lookup("refreshArticle", EventQueues.APPLICATION, true).publish(new Event("onRefresh"));
			else
				EventQueues.lookup("refreshReplyArticle", EventQueues.APPLICATION, true)
						.publish(new Event("onRefresh"));

			currentArticle = null;
		}
	}

	@Command
	public void reply() {
		Sessions.getCurrent().removeAttribute("modifyId");
		Executions.sendRedirect("postArticle.zul?articleId=" + currentArticle.getId());
	}

	public Article getCurrentArticle() {
		return currentArticle;
	}

	public void setCurrentArticle(Article currentArticle) {
		this.currentArticle = currentArticle;
	}

	public boolean isCanBeDeleted() {
		return canBeDeleted;
	}

	public void setCanBeDeleted(boolean canBeDeleted) {
		this.canBeDeleted = canBeDeleted;
	}

	public boolean isCanBeModified() {
		return canBeModified;
	}

	public void setCanBeModified(boolean canBeModified) {
		this.canBeModified = canBeModified;
	}
}
