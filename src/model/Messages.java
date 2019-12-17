package model;

import java.io.Serializable;

public class Messages implements Serializable {

	private static final long serialVersionUID = 1L;
	private String sendId;
	private String receiveId;
	private String date;
	private String message;

	public Messages() {	}

	public Messages(String sendId, String receiveId, String message) {
		this.sendId = sendId;
		this.receiveId = receiveId;
		this.message = message;
	}
	
	public Messages(String sendId, String receiveId, String message, String date) {
		this(sendId, receiveId, message);
		this.date = date;
	}

	public String getSendId() {
		return sendId;
	}

	public void setSendId(String seneId) {
		this.sendId = seneId;
	}

	public String getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(String receiveId) {
		this.receiveId = receiveId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}	
	
}
