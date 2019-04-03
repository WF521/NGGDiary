package model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 日记类
 *
 */
public class Diary {
	private int id = 0;// 日记ID号
	private String title = "";// 日记标题
	private String address = "";// 日记图片地址
	private Date writeTime = null;// 写日记的时间
	private int userid = 0;// 用户ID
	private String username = "";// 用户名
	private List<Comment> comments;// 日志评论
	private int likes;// 点赞数

	public Diary() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(Date writeTime) {
		this.writeTime = writeTime;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

}
