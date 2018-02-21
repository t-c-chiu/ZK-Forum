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
public class LatestTenRepliedArticlesVM {

	@WireVariable
	private ForumService forumService;
	private ListModelList<String> repliedArticlesTitle;

	@Init
	public void init() {
		updateArticles();

		EventQueues.lookup("refreshReplyArticle", EventQueues.APPLICATION, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				updateArticles();
				BindUtils.postNotifyChange(null, null, LatestTenRepliedArticlesVM.this, "repliedArticlesTitle");
			}
		});
	}

	private void updateArticles() {
		repliedArticlesTitle = new ListModelList<String>(forumService.getLatestTenRepliedArticlesTitle());
	}

	public ListModelList<String> getRepliedArticlesTitle() {
		return repliedArticlesTitle;
	}
}
