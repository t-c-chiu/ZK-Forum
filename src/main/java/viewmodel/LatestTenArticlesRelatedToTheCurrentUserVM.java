package viewmodel;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;

import model.bean.User;
import model.service.ForumService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LatestTenArticlesRelatedToTheCurrentUserVM {

	@WireVariable
	private ForumService forumService;
	private ListModelList<String> articlesRelatedToCurrentUserTitle;

	@Init
	public void init() {
		updateArticles();
		EventQueues.lookup("refreshMyArticle", EventQueues.SESSION, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				updateArticles();
			}
		});
	}

	private void updateArticles() {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		articlesRelatedToCurrentUserTitle = new ListModelList<String>(
				forumService.getlatestArticlesRelatedToTheCurrentUserTitle(user.getId()));
		BindUtils.postNotifyChange(null, null, this, "articlesRelatedToCurrentUserTitle");
	}

	public ListModelList<String> getArticlesRelatedToCurrentUserTitle() {
		return articlesRelatedToCurrentUserTitle;
	}

}
