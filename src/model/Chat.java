package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable{

	private static final long serialVersionUID = 1L;
	private String joinChat;
	private ArrayList<String> member;
	private String message;
	private File file;
	
	public Chat() {
		this.joinChat = "";
		this.member = new ArrayList<>();
		this.message = "";
		this.file = null;
	}
	

	public String getJoinChat() {
		return joinChat;
	}
	public void setJoinChat(String joinChat) {
		this.joinChat = joinChat;
	}
	public ArrayList<String> getMember() {
		return member;
	}
	public void setMember(ArrayList<String> member) {
		this.member = member;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}
