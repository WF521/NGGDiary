package model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * �ռ���
 *
 */
public class Diary {
	private int id = 0;// �ռ�ID��
	private String title = "";// �ռǱ���
	private String address = "";// �ռ�ͼƬ��ַ
	private Date writeTime = null;// д�ռǵ�ʱ��
	private int userid = 0;// �û�ID
	private String username = "";// �û���
	private List<Comment> comments;// ��־����
	private int likes;// ������

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
