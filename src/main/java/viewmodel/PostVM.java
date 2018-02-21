package viewmodel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

import model.bean.Article;
import model.bean.Tag;
import model.service.ForumService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PostVM {

	@WireVariable
	private ForumService forumService;
	private String title;
	private String content;
	private Integer targetId;
	private ListModelList<Tag> existTags;
	private Article modifyArticle;
	private Set<Tag> selectedTags = new HashSet<Tag>();
	private ScheduledFuture<?> future;
	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	@Init
	public void init() {
		existTags = new ListModelList<Tag>(forumService.getAllTags());

		Integer modifyId = (Integer) Sessions.getCurrent().getAttribute("modifyId");
		if (modifyId != null) {
			modifyArticle = forumService.getArticle(modifyId);
			title = modifyArticle.getTitle();
			content = modifyArticle.getContent();
			existTags.setMultiple(true);
			existTags.setSelection(modifyArticle.getTags());
			selectedTags = modifyArticle.getTags();
		}

		String articleId = Executions.getCurrent().getParameter("articleId");
		if (articleId != null)
			targetId = Integer.valueOf(articleId);

		EventQueues.lookup("postQueue", EventQueues.SESSION, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				if (targetId != null)
					doReply();
				else if (modifyArticle != null)
					doModify();
				else
					doPost();
				Executions.sendRedirect("/forum.zul");
			}

			private void doPost() {
				forumService.postArticle(title, content, selectedTags);
				EventQueues.lookup("refreshArticle", EventQueues.APPLICATION, true).publish(new Event("onRefresh"));
			}

			private void doReply() {
				forumService.reply(targetId, title, content, selectedTags);
				EventQueues.lookup("refreshReplyArticle", EventQueues.APPLICATION, true)
						.publish(new Event("onRefresh"));
			}

			private void doModify() {
				forumService.modify(modifyArticle.getId(), title, content, selectedTags);
				if (modifyArticle.getParent() == null)
					EventQueues.lookup("refreshArticle", EventQueues.APPLICATION, true).publish(new Event("onRefresh"));
				else
					EventQueues.lookup("refreshReplyArticle", EventQueues.APPLICATION, true)
							.publish(new Event("onRefresh"));
			}
		});
	}

	@Command
	public void selectTag(@BindingParam("tags") Set<Tag> tags) {
		selectedTags = tags;
	}

	@Command
	public void newTag(@BindingParam("tag") String tagName) {
		Tag tag = new Tag();
		tag.setName(tagName);
		existTags.add(tag);
		existTags.addToSelection(tag);
		selectedTags.add(tag);
	}

	@Command
	@NotifyChange({ "canBeCanceled", "shallShowCountdown", "future" })
	public void post() {
		final EventQueue<Event> postQueue = EventQueues.lookup("postQueue", EventQueues.SESSION, true);
		future = scheduler.schedule(new Runnable() {
			public void run() {
				postQueue.publish(new Event("onPost"));
			}
		}, 10L, TimeUnit.SECONDS);
	}

	@Command
	public void cancel() {
		String cancelDoubleCheck = Labels.getLabel("cancelDoubleCheck");
		String tenSecondPrompt = Labels.getLabel("tenSecondPrompt");
		Messagebox.show(cancelDoubleCheck, tenSecondPrompt,
				new Messagebox.Button[] { Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION,
				new EventListener<Messagebox.ClickEvent>() {
					public void onEvent(ClickEvent event) throws Exception {
						if (Messagebox.Button.YES.equals(event.getButton())) {
							future.cancel(true);
							future = null;
							BindUtils.postNotifyChange(null, null, PostVM.this, "future");
						}
					}
				});
	}

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

	public ListModelList<Tag> getTagNames() {
		return existTags;
	}

	public void setTagNames(ListModelList<Tag> tags) {
		this.existTags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ListModelList<Tag> getExistTags() {
		return existTags;
	}

	public void setExistTags(ListModelList<Tag> existTags) {
		this.existTags = existTags;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getTargetId() {
		return targetId;
	}

	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}

	public Article getModifyArticle() {
		return modifyArticle;
	}

	public void setModifyArticle(Article modifyArticle) {
		this.modifyArticle = modifyArticle;
	}

}
