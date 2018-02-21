package viewmodel;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class ViewTypeVM {

	private String viewType = "treeView.zul";

	@Command
	@NotifyChange("viewType")
	public void showListboxView() {
		viewType = "listboxView.zul";
	}

	@Command
	@NotifyChange("viewType")
	public void showTreeView() {
		viewType = "treeView.zul";
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
}
