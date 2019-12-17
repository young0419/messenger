package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import db.DBSQL;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import model.Chat;
import model.Datas;
import model.Messages;
import model.User;
import protocol.Protocol;

public class Server implements Initializable {

	@FXML private TextArea ta;
	@FXML private Button bt;

	//서버관련
	private ServerSocket server = null;
	private Socket socket = null;
	private ExecutorService executorService;
	private Vector<ServerThread> clients = new Vector<>();
	private ArrayList<String> loginID = new ArrayList<>();

	//DB
	private DBSQL DBConn = new DBSQL();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bt.setOnAction(event-> {
			if (bt.getText().equals("서버 시작"))
				startServer();
			else if (bt.getText().equals("서버 종료"))
				serverStop();
		});
	}

	void startServer() {
		executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()
				);

		try {
			server = new ServerSocket(9400);
		} catch (IOException e) {
			if(!server.isClosed()) { serverStop(); }
			return;
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					dpText("[서버 시작]");
					bt.setText("서버 종료");
				});
				while(true) {
					try {
						socket = server.accept();
						String message = "[접속 : " + socket.getRemoteSocketAddress()  + ": " + Thread.currentThread().getName() + "]";
						Platform.runLater(()->dpText(message));

						ServerThread client = new ServerThread(socket);

						client.start();
						clients.add(client);

						Platform.runLater(()->dpText("[연결 개수 : " + clients.size() + "]"));
					} catch (IOException e) {
						if(!server.isClosed()) { serverStop(); }
						break;
					}
				}	
			}			
		};
		executorService.submit(runnable);
	}

	void serverStop() {
		try {
			Iterator<ServerThread> iterator = clients.iterator();
			while(iterator.hasNext()) {
				ServerThread client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			if(server!=null && !server.isClosed()) { 
				server.close(); 
			}	
			if(executorService!=null && !executorService.isShutdown()) { 
				executorService.shutdown(); 
			}		
			Platform.runLater(() -> {
				dpText("[서버 종료]");
				bt.setText("서버 시작");				
			});
		} catch (IOException e) {}
	}

	class ServerThread extends Thread {

		private String userId = "";
		private Socket socket;
		private ArrayList<String> chatList = new ArrayList<String>();

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		public ServerThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				Datas datas = null;
				while(true) {
					datas = (Datas) ois.readObject();
					String protocol = datas.getProtocol();
					User user = (User) datas.getUser();
					String myId = datas.getMyId();

					dpText("[요청 (" + userId + ") :" + socket.getRemoteSocketAddress()  + ": " + Thread.currentThread().getName() 
							+ " : " + datas.getProtocol() + "]");

					switch (protocol) {
					case Protocol.LOGIN: //로그인 버튼에서요청
						send(login(user, this));
						break;

					case Protocol.ID_CHK: //회원가입시 아이디 중복확인
						send(idChk(myId));

						break;

					case Protocol.USERJOIN: //회원가입 요청
						DBConn.insertUser(user);
						break;


					case Protocol.NICK_EDIT: //닉네임 변경요청
						DBConn.updateNickname(user.getUserId(), user.getUserNickname());
						break;

					case Protocol.MESSAGE_LIST: //메시지함 클릭
						datas.setMessagesList(DBConn.selectMessageList(myId));
						datas.setProtocol(Protocol.MESSAGE_LIST);
						send(datas);
						break;

					case Protocol.SEARCH_FRIEND: //친구찾기
						datas = searchFriendResult(
								DBConn.selectSearchFriendfromId(user.getUserId()));
						send(datas);
						break;

					case Protocol.SEARCH_FRIEND_AREA: //지역친구찾기
						ArrayList<User> list = 
						DBConn.selectSearchFriendfromArea(user.getUserArea(), userId);
						send(searchAreaResult(list));
						break;

					case Protocol.PREFERENCES: //환경설정
						send(setPreferences(user));						
						break;	

					case Protocol.LOGOUT: //로그아웃 요청
						removeUserList(user.getUserId());
						userId = null;
						break;

					case Protocol.EXIT: //종료 요청
						clients.remove(this);
						break;

					case Protocol.SEND_MESSAGE: //메시지 보내기
						saveMessage(datas);
						saveMessageRecieve(datas);
						messageSend(datas.getMessages());
						break;

					case Protocol.ADD_FRIEND: //친구추가요청
						send(addFriend(datas));
						break;

					case Protocol.ADD_FRIEND_RESPONSE: //친구요청 수락
						DBConn.insertFriendRequest(myId, datas.getFriendId());
						DBConn.updataFriendPermit(datas.getFriendId());
						break;	

					case Protocol.START_CHAT: //대화시작
						startChat(datas);
						break;
						
					case Protocol.CHATTING: //대화하기
						Chatting(datas);
						break;	
					
					case Protocol.INVITE_CHAT: //대화초대
						broadcasting(datas);
						break;

					case Protocol.EXIT_CHAT: //대화종료
						exitChat(datas, this);
						break;	

					default:
						break;
					}

				}
			} catch (IOException e) {
				try {
					clients.remove(ServerThread.this);
					String message = "[클라이언트 통신 안됨 : " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
					Platform.runLater(()->dpText(message));
					socket.close();
				} catch (IOException e1) {}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}

		public void send(Datas datas) throws IOException {
			oos.writeObject(datas);
			oos.flush();
		}
	}

	//로그인 요청
	public Datas login(User inputUser, ServerThread st) {
		Datas datas = new Datas();
		String id = inputUser.getUserId();
		boolean flag = DBConn.selectLogin(id, inputUser.getUserPassword());

		if(!flag) {
			datas.setProtocol(Protocol.LOGIN_CHK_NO);
			return datas;
		} else {
			for(ServerThread client : clients) {
				System.out.println(client.userId);
				System.out.println(id);
				if(client.userId.trim().equals(id)) {
					datas.setProtocol(Protocol.LOGIN_CHK_OVERLAP);
					return datas;
				}
			}
		}
		datas.setProtocol(Protocol.LOGIN_CHK_OK);
		datas.setUser(DBConn.selectUserInfoSet(id));
		datas.setFriendList(DBConn.selectFriendList(id));
		st.userId = id;						
		dpText("[요청 : " + socket.getRemoteSocketAddress()  + ": " + Thread.currentThread().getName() 
				+ " : " + datas.getProtocol() + "]");
		return datas;
	}

	//아이디 체크요청
	public Datas idChk(String myId) {
		Datas datas = new Datas();
		boolean status = DBConn.selectIdChk(myId);
		if(status == false)
			datas.setProtocol(Protocol.ID_CHK_NO);
		else
			datas.setProtocol(Protocol.ID_CHK_OK);
		return datas;
	}

	//친구찾기 결과
	public Datas searchFriendResult(User user) {
		Datas datas = new Datas();
		datas.setUser(user);
		datas.setProtocol(Protocol.SEARCH_RESULT);

		return datas;
	}

	//지역친구찾기 결과
	public Datas searchAreaResult(ArrayList<User> list) {
		Datas data = new Datas();
		data.setProtocol(Protocol.SEARCH_RESULT_AREA);
		data.setSearchList(list);
		return data;
	}

	//친구 추가
	public Datas addFriend(Datas datas) throws IOException {
		Datas data = new Datas();
		int result = DBConn.insertFriend(datas.getMyId(), datas.getFriendId());
		if(result == 1) {
			addFriendRequest(datas);
			data.setProtocol(Protocol.ADD_FRIEND_OK);
			data.setFriendId(datas.getFriendId());
			return data;
		} else {
			data.setProtocol(Protocol.ADD_FRIEND_NO);
			return data;
		}
	}

	//친구요청보내기
	public void addFriendRequest(Datas datas) throws IOException {
		Datas data = new Datas();
		for(ServerThread st : clients) {
			if(st.userId.equals(datas.getFriendId())) {
				data.setMyId(datas.getFriendId());
				data.setFriendId(datas.getMyId());
				data.setProtocol(Protocol.ADD_FRIEND_REQUEST);
				st.send(data);
			}
		}
	}

	//쪽지 전달
	public void messageSend(Messages msg) throws IOException {
		Datas data = new Datas();
		for(ServerThread st : clients) {
			if(st.userId.equals(msg.getReceiveId())) {
				data.setProtocol(Protocol.SEND_MESSAGE);
				st.send(data);
			}
		}
	}

	//쪽지 저장하기
	public void saveMessage(Datas datas) {
		DBConn.insertMessage(datas);
	}

	//친구한테 쪽지 저장해놓기
	public void saveMessageRecieve(Datas datas) {
		DBConn.insertMessageReceive(datas);
	}

	//대화시작
	public void startChat(Datas datas) throws IOException {
		Chat chat = datas.getChat();
		for(String mb : chat.getMember()) {
			for(ServerThread st : clients) {
				if(mb.equals(st.userId)) {
					st.chatList.add(chat.getJoinChat());
					st.send(datas);					
				}
			}
		}
	}
	
	//대화하기
	public void Chatting(Datas datas) throws IOException {
		broadcasting(datas);
	}

	//대화 끝
	public void exitChat(Datas datas, ServerThread st) throws IOException {
		Chat chat = datas.getChat();
		for(int i = 0; i < st.chatList.size(); i++) {
			if(st.chatList.get(i).equals(chat.getJoinChat())) {
				st.chatList.remove(i);
				break;
			}
		}
		broadcasting(datas);
	}

	//환경설정
	public Datas setPreferences(User user) {
		DBConn.updateSearchPermit(user.getUserId(), user.getSearchPermit());
		Datas datas = new Datas();
		datas.setProtocol(Protocol.PREFERENCES_OK);
		return datas;
	}

	//접속유저목록에서 제거하기
	public void removeUserList(String myId) {
		for(int i = 0; i < loginID.size(); i++) {
			if(myId.equals(loginID.get(i))) {
				loginID.remove(i);
			}
		}
	}
	
	//전체에 보내기
	public void broadcasting(Datas datas) throws IOException {
		for(ServerThread st : clients) {
			st.send(datas);
		}
	}

	//서버로그
	public void dpText(String text) {
		ta.appendText(text +"\n");
	}

}
