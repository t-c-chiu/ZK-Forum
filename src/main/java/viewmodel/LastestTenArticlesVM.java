package viewmodel;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;

import model.service.ForumService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LastestTenArticlesVM {

	@WireVariable
	private ForumService forumService;
	private ListModelList<String> articlesTitle;

	@Init
	public void init() {
		updateArticles();

		EventQueues.lookup("refreshArticle", EventQueues.APPLICATION, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				updateArticles();
			}
		});
	}

	private void updateArticles() {
		articlesTitle = new ListModelList<String>(forumService.getLatestArticlesTitle());
		BindUtils.postNotifyChange(null, null, LastestTenArticlesVM.this, "articlesTitle");
	}

	public ListModelList<String> getArticlesTitle() {
		return articlesTitle;
	}

}
