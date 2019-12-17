package model;

import java.io.Serializable;

public class User implements Serializable{


	private static final long serialVersionUID = 1L;
	// 회원 아이디 
	private String userId;
	// 회원 비밀번호 
	private String userPassword;
	// 회원 성별 
	private String userGender;
	// 회원 지역
	private String userArea;
	// 회원 닉네임 
	private String userNickname;
	// 검색허용여부
	private String searchPermit;

	public User() {}

	public User(String userId, String userPassword) {
		this.userId = userId;
		this.userPassword = userPassword;
	}

	public User(String userId, String userPassword, String userGender, String userArea) {
		this(userId, userPassword);
		this.userGender = userGender;
		this.userArea = userArea;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	public String getUserArea() {
		return userArea;
	}

	public void setUserArea(String userArea) {
		this.userArea = userArea;
	}

	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}

	public String getSearchPermit() {
		return searchPermit;
	}

	public void setSearchPermit(String searchPermit) {
		this.searchPermit = searchPermit;
	}

	@Override
	public String toString() {
		return userId + "(" + userNickname + ")";
	}

}