package model;

public class Comment {
	private int id;// ¡Ù—‘±‡∫≈
	private String fromUserName;// ¡Ù—‘»À
	private String content;// ¡Ù—‘ƒ⁄»›
	private String create_time;// ¡Ù—‘ ±º‰
	private boolean valid;//  «∑Ò”––ß

	public Comment() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}


}
