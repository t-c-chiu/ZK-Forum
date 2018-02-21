package model.bean;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Where;

@Entity
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Integer authorId;
	private Integer replyId;
	private Integer status;
	private Date date;
	private String title;
	private String content;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "TagDetail", joinColumns = { @JoinColumn(name = "articleId") }, inverseJoinColumns = {
			@JoinColumn(name = "tagId") })
	private Set<Tag> tags;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	@Where(clause = "status=0")
	private List<Article> children;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "replyId", insertable = false, updatable = false)
	private Article parent;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "authorId", insertable = false, updatable = false)
	private User author;

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public List<Article> getChildren() {
		return children;
	}

	public void setChildren(List<Article> children) {
		this.children = children;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public Article getParent() {
		return parent;
	}

	public void setParent(Article parent) {
		this.parent = parent;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public Integer getReplyId() {
		return replyId;
	}

	public void setReplyId(Integer replyId) {
		this.replyId = replyId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
