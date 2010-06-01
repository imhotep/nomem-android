package org.koalabs.nomem;

public class Note {
	
	private String title;
	private String body;
	private Integer id;
	
	public Note(Integer id, String title, String body) {
		this.title = title;
		this.id = id;
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String toString() {
		return getTitle();
	}

}
