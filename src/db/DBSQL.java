package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

import model.Datas;
import model.Messages;
import model.User;

public class DBSQL implements DBSQLInterface{

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	@Override
	public boolean selectIdChk(String id) {	//아이디 중복검색	
		try {
			String sql = "select user_id from users where user_id = ?";
			conn  = DButil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				return false;
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(rs, pstmt, conn);
		return true;
	}

	@Override
	public void insertUser(User user) { //회원가입 신청
		StringBuffer sql = new StringBuffer();
		sql.append("insert into users (user_id, user_password, user_gender, user_area) ");
		sql.append("values (?, ?, ?, ?) ");

		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, user.getUserId());
			pstmt.setString(2, user.getUserPassword());
			pstmt.setString(3, user.getUserGender());
			pstmt.setString(4, user.getUserArea());
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);
	}

	@Override
	public boolean selectLogin(String id, String pw) { //로그인 요청
		StringBuffer sql = new StringBuffer();
		sql.append("select user_id, user_password from users ");
		sql.append("where user_id = ? ");
		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				String user_id = rs.getString("user_id");
				String user_pw = rs.getString("user_password");
				if(user_id.equals(id)) {
					if(user_pw.equals(pw)) {
						return true;
					}
				}
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(rs, pstmt, conn);
		return false;

	}

	@Override
	public  ArrayList<String> selectFriendList(String id) { //친구 목록 불러오기
		ArrayList<String> friendList = new ArrayList<String>();

		StringBuffer sql = new StringBuffer();
		sql.append("select friend_id ");
		sql.append("from friendlist ");
		sql.append("where user_id = ? ");
		sql.append("order by friend_id asc ");
		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				friendList.add(rs.getString("friend_id"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(rs, pstmt, conn);

		return friendList;
	}

	@Override
	public ArrayList<Messages> selectMessageList(String id) { //쪽지함 불러오기		
		ArrayList<Messages> list = new ArrayList<>();
		Messages msg = null;

		StringBuffer sql = new StringBuffer();
		sql.append("select send_id, receive_id, message_content, send_date ");
		sql.append("from messages ");
		sql.append("where user_id = ? ");
		sql.append("order by send_date desc ");

		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				String sendId = rs.getString("send_id");
				String receiveId = rs.getString("receive_id");
				String message = rs.getString("message_content");
				LocalDateTime ldc = rs.getTimestamp("send_date").toLocalDateTime();
				ZonedDateTime zdt = ZonedDateTime.of(ldc, ZoneId.of("Asia/Seoul"));
				DateTimeFormatter dtf 
				= DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
				String date = dtf.format(zdt);
				msg = new Messages(sendId, receiveId, message, date);
				list.add(msg);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public void insertMessage(Datas datas) { //보낸 쪽지 저장하기
		Messages msg = datas.getMessages();
		StringBuffer sql = new StringBuffer();
		sql.append("insert into messages (user_id, send_id, receive_id, message_content, send_date) ");
		sql.append("values (?, ?, ?, ?, sysdate) ");

		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, datas.getMyId());
			pstmt.setString(2, msg.getSendId());
			pstmt.setString(3, msg.getReceiveId());
			pstmt.setString(4, msg.getMessage());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);
	}

	@Override
	public void insertMessageReceive(Datas datas) { //받은 쪽지 저장하기
		Messages msg = datas.getMessages();
		StringBuffer sql = new StringBuffer();
		sql.append("insert into messages (user_id, send_id, receive_id, message_content, send_date) ");
		sql.append("values (?, ?, ?, ?, sysdate) ");

		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, msg.getReceiveId());
			pstmt.setString(2, datas.getMyId());
			pstmt.setString(3, msg.getReceiveId());
			pstmt.setString(4, msg.getMessage());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);
	}


	@Override
	public User selectUserInfoSet(String id) {	//로그인시 유저정보 불러오기
		User user = new User();
		user.setUserId(id);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select user_id, user_nickname, search_permit ");
			sql.append("from users ");
			sql.append("where user_id = ?");

			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				String userId = rs.getString("user_id");
				String nick = rs.getString("user_nickname");
				String permit = rs.getString("search_permit");

				user.setUserId(userId);
				user.setUserNickname(nick);
				user.setSearchPermit(permit);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);

		return user;
	}

	@Override
	public void updateNickname(String id, String nick) { //닉네임 바꾸기
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE users ");
			sql.append("set user_nickname = ? ");
			sql.append("where user_id = ? ");

			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, nick);
			pstmt.setString(2, id);
			pstmt.executeUpdate();			
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);
	}

	@Override
	public void updateSearchPermit(String id, String searchPermint) { //검색허용 설정
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE users ");
			sql.append("set search_permit = ? ");
			sql.append("where user_id = ? ");

			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, searchPermint);
			pstmt.setString(2, id);
			pstmt.executeUpdate();			
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);
	}

	@Override
	public User selectSearchFriendfromId(String id) { //친구찾기
		User user = new User();

		StringBuffer sql = new StringBuffer();
		sql.append("select user_id, user_nickname ");
		sql.append("from users ");
		sql.append("where user_id = ? and search_permit = ? ");

		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, id);
			pstmt.setString(2, "Y");
			rs = pstmt.executeQuery();

			if(rs.next()) {
				String user_id = rs.getString("user_id");
				String user_nickname = rs.getString("user_nickname");
				user.setUserId(user_id);
				user.setUserNickname(user_nickname);
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(rs, pstmt, conn);

		return user = null;	
	}

	@Override
	public ArrayList<User> selectSearchFriendfromArea(String area, String id) { //지역친구찾기
		ArrayList<User> list = new ArrayList<>();


		StringBuffer sql = new StringBuffer();
		sql.append("select user_id, user_nickname, user_area ");
		sql.append("from users ");
		sql.append("where user_area = ? and search_permit = ? and user_id != ? ");

		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, area);
			pstmt.setString(2, "Y");
			pstmt.setString(3, id);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				User user = new User();
				String user_id = rs.getString("user_id");
				String user_nickname = rs.getString("user_nickname");
				String user_area = rs.getString("user_area");
				user.setUserId(user_id);
				user.setUserNickname(user_nickname);
				user.setUserArea(user_area);
				list.add(user);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(rs, pstmt, conn);

		return list;
	}

	@Override
	public int insertFriend(String myId, String friendId) { //친구추가
		int result = -1;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into friendlist (user_id, friend_id) ");
		sql.append("select ?, ? ");
		sql.append("from dual ");
		sql.append("where not exists ");
		sql.append("(select user_id from friendlist"
				+ " where user_id = ? and friend_id = ?) ");

		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, myId);
			pstmt.setString(2, friendId);
			pstmt.setString(3, myId);
			pstmt.setString(4, friendId);
			result = pstmt.executeUpdate();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);		

		return result;
	}

	@Override
	public void insertFriendRequest(String myId, String friendId) { //친구요청 수락
		StringBuffer sql = new StringBuffer();
		sql.append("insert into friendlist (user_id, friend_id, friend_permit) ");
		sql.append("values (?, ?, ?) ");
		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, myId);
			pstmt.setString(2, friendId);
			pstmt.setString(3, "O");
			pstmt.executeUpdate();				
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);
	}

	@Override
	public void updataFriendPermit(String friendId) { //친구요청 수락시 서로친구로 변경
		StringBuffer sql = new StringBuffer();
		sql.append("update friendlist ");
		sql.append("set friend_permit = ? ");
		sql.append("where user_id = ?");
		try {
			conn = DButil.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, "O");
			pstmt.setString(2, friendId);
			pstmt.executeUpdate();				
		} catch (SQLException e) {
			e.printStackTrace();
		} DButil.close(pstmt, conn);
	}

}
