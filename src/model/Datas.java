package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Datas implements Serializable {

	private static final long serialVersionUID = 1L;
	private String protocol;
	private User user;
	private Chat chat;
	private Messages messages;
	private String myId;
	private String friendId;
	private ArrayList<String> friendList;
	private ArrayList<User> searchList;
	private ArrayList<Messages> messagesList;
	
		
	public Datas() {}
	
	public Datas(String protocol, User user) {
		this.protocol = protocol;
		this.user = user;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages msg) {
		this.messages = msg;
	}

	public String getMyId() {
		return myId;
	}

	public void setMyId(String myId) {
		this.myId = myId;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public ArrayList<String> getFriendList() {
		return friendList;
	}

	public void setFriendList(ArrayList<String> friendList) {
		this.friendList = friendList;
	}

	public ArrayList<User> getSearchList() {
		return searchList;
	}

	public void setSearchList(ArrayList<User> searchList) {
		this.searchList = searchList;
	}

	public ArrayList<Messages> getMessagesList() {
		return messagesList;
	}

	public void setMessagesList(ArrayList<Messages> messagesList) {
		this.messagesList = messagesList;
	}
		
}
