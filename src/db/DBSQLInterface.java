package db;

import java.util.ArrayList;

import model.Datas;
import model.Messages;
import model.User;

public interface DBSQLInterface {

	// 아이디 중복 확인 select
	public boolean selectIdChk(String id);
	
	// 회원가입 insert
	public void insertUser(User user);
	
	// 로그인요청 아이디 비번 확인 select
	public boolean selectLogin(String id, String pw);
	
	// 친구목록불러오기 select
	public ArrayList<String> selectFriendList(String id);
	
	// 쪽지함 불러오기 select
	public ArrayList<Messages> selectMessageList(String id);
	
	// 쪽지 저장하기 insert
	public void insertMessage(Datas datas);
	
	// 받은 쪽지 저장하기
	public void insertMessageReceive(Datas datas);
	
	// 닉네임 불러오기
	public User selectUserInfoSet(String id);
	
	// 닉네임 수정하기 update
	public void updateNickname(String id, String nick);
	
	// 검색허용 설정 update
	public void updateSearchPermit(String id, String searchPermint);
	
	// 아이디로 친구 찾기 select
	public User selectSearchFriendfromId(String id);
	
	// 지역으로 친구 찾기 select
	public ArrayList<User> selectSearchFriendfromArea(String area, String id);
	
	// 친구추가 insert
	public int insertFriend(String myId, String friendId);
	
	// 친구 요청 수락시 친구 추가
	public void insertFriendRequest(String myId, String friendId);
	
	// 서로 친구 표시 변경
	public void updataFriendPermit(String friendId);
}
