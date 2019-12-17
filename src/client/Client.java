package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Chat;
import model.Datas;
import model.Messages;
import model.User;
import protocol.Protocol;
import view.AfterLoginController;
import view.ChattingViewController;
import view.LoginViewController;
import view.MessageListController;
import view.SearchFriendController;

public class Client {

	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	//내정보
	private User myInfo = null;
	private User friendInfo = null;
	private ArrayList<String> friendList = null;
	private String joinChat = "";


	//확인값들
	private boolean idChkOk = false; //아이디 중복확인
	private boolean loginChk = false; //로그인 아이디비번확인

	private AfterLoginController alc;
	private SearchFriendController sfc;
	private ChattingViewController cvc;
	public Client() {
		startClient();
	}

	public void startClient() { //서버 시작
		System.out.println("클라시작");
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					socket = new Socket("localhost", 9400);

				} catch(IOException ioe) {
					Platform.runLater(()->{
						Alert alert = new Alert(AlertType.ERROR);
						alert.setContentText("서버 접속이 안됩니다");
						alert.showAndWait();
					});
					if(!socket.isClosed()) { stopClient(); }
					return;
				}
				receive();
			}
		};
		thread.start();
	}

	public void stopClient() { //서버 종료
		try {
			if(socket!=null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {}
	}

	public void receive() {
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			Datas datas = null;
			while(true) {

				datas = (Datas) ois.readObject();

				String protocol = datas.getProtocol();
				System.out.println(protocol);

				switch (protocol) {
				case Protocol.LOGIN_CHK_OK:
					myInfo = (User) datas.getUser();
					friendList = datas.getFriendList();
					LoginOk(datas);
					break;

				case Protocol.LOGIN_CHK_NO:
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.WARNING);

						alert.setTitle("로그인 오류");
						alert.setHeaderText(null);
						alert.setContentText("아이디나 비밀번호를 확인해주세요");
						alert.showAndWait();
					});
					break;

				case Protocol.LOGIN_CHK_OVERLAP:
					loginOverlap();
					break;

				case Protocol.ID_CHK_OK:
					resultIdChk(protocol);
					idChkOk = true;
					break;

				case Protocol.ID_CHK_NO:
					resultIdChk(protocol);
					break;	

				case Protocol.SEARCH_RESULT:
					resultSearchFriend(datas);
					break;

				case Protocol.SEARCH_RESULT_AREA:
					resultSearchArea(datas);
					break;	

				case Protocol.SEND_MESSAGE:
					receiveMessage(datas);
					break;

				case Protocol.MESSAGE_LIST:
					showMessageList(datas.getMessagesList());
					break;

				case Protocol.ADD_FRIEND_OK:
					addFriendOk(datas.getFriendId());
					break;	

				case Protocol.ADD_FRIEND_NO:
					addFriendNo();
					break;	

				case Protocol.ADD_FRIEND_REQUEST:
					addFriendResponse(datas);
					break;		

				case Protocol.PREFERENCES_OK:
					setOkPreference();
					break;

				case Protocol.START_CHAT:
					showChat(datas);
					break;

				case Protocol.CHATTING:
					chatting(datas);
					break;	

				case Protocol.INVITE_CHAT:
					inviteChat(datas);
					break;

				case Protocol.EXIT_CHAT:
					exitChat(datas);
					break;

				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {

		}
	}

	public void send(Datas datas) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					oos.writeObject(datas);
					oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		};
		thread.start();
	}

	//아이디 중복확인 결과
	public void resultIdChk(String text) { 
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("ID 중복확인");
			alert.setHeaderText(null);	
			if(text.equals(Protocol.ID_CHK_OK)) {
				alert.setContentText("사용 하실 수 있는 아이디 입니다.");
			} else {
				alert.setContentText("사용 하실 수 없는 아이디 입니다.");
			}		
			alert.showAndWait();
		});
	}	

	//로그인 수락
	public void LoginOk(Datas datas) {
		Platform.runLater(() -> {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/view/LoginView.fxml"));
				loader.load();
				LoginViewController lvc = (LoginViewController) loader.getController();
				lvc.showAterLogin(datas);

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	//중복로그인
	public void loginOverlap() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("로그인 오류");
			alert.setHeaderText(null);
			alert.setContentText("중복 로그인입니다");
			alert.showAndWait();
		});
	}

	//쪽지 수신
	public void receiveMessage(Datas datas) {
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("알림");
			alert.setHeaderText(null);
			alert.setContentText("쪽지가 도착했습니다.");
			alert.showAndWait();			
		});
	}

	//쪽지함 불러오기
	public void showMessageList(ArrayList<Messages> messageList) {
		Platform.runLater(() -> {	
			try {
				Stage stage = new Stage(StageStyle.DECORATED);
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/view/MessageList.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				MessageListController mlc = (MessageListController) loader.getController();

				if(!messageList.isEmpty()) {
					mlc.getList().addAll(messageList);
					mlc.getMessageList().setItems(mlc.getList());
				}

				stage.setTitle("쪽지함");
				stage.setScene(scene);
				stage.setResizable(false);
				stage.show();			

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}


	//친구찾기결과-id
	public void resultSearchFriend(Datas datas) {
		Platform.runLater(()->{	
			if(datas.getUser() == null) {
				searchFriendAlert();
			} else {
				friendInfo = ((User) datas.getUser());
				sfc.getFriends().add(friendInfo.getUserId());
				sfc.getSearchList().setItems(sfc.getFriends());
			}
		});
	}

	//친구찾기 결과-area
	public void resultSearchArea(Datas datas) {
		Platform.runLater(()->{	
			if(datas.getSearchList().isEmpty()) {
				searchFriendAlert();
			} else {
				sfc.getFriends().clear();
				for(User user : datas.getSearchList()) {
					sfc.getFriends().add(user.getUserId());
				}
				sfc.getSearchList().setItems(sfc.getFriends());
			}
		});
	}

	//친구추가완료
	public void addFriendOk(String friendId) throws IOException {
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.INFORMATION);

			alert.setTitle("친구 추가");
			alert.setHeaderText(null);
			alert.setContentText("친구추가 완료");
			alert.showAndWait();

			alc.getFriend().add(friendId);
			alc.getFriendList().setItems(alc.getFriend());
		});
	}

	//기존 친구 추가시도시
	public void addFriendNo() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);

			alert.setTitle("친구 추가");
			alert.setHeaderText(null);
			alert.setContentText("이미 등록된 친구입니다.");
			alert.showAndWait();
		});
	}

	//친구찾기 결과없음
	public void searchFriendAlert() {
		Alert alert = new Alert(AlertType.WARNING);

		alert.setTitle("친구 찾기");
		alert.setHeaderText(null);
		alert.setContentText("검색된 친구가 없습니다");
		alert.showAndWait();
	}

	//친구추가 요청 받았을때
	public void addFriendResponse(Datas datas) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("친구 요청");
			alert.setContentText(datas.getFriendId() + "님의 친구 신청을 수락하시겠습니까?");			

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){

				Datas data = new Datas();
				data.setMyId(datas.getMyId());
				data.setFriendId(datas.getFriendId());
				data.setProtocol(Protocol.ADD_FRIEND_RESPONSE);
				send(data);

				alc.getFriend().add(datas.getFriendId());
				alc.getFriendList().setItems(alc.getFriend());
			}
		});
	}

	//대화 시작	
	public void showChat(Datas datas) {
		Chat chat = datas.getChat();
		for(String st : chat.getMember()) {
			if(st.equals(myInfo.getUserId())) {
				joinChat = chat.getJoinChat();
				Platform.runLater(() -> {	
					try {
						Stage stage = new Stage(StageStyle.DECORATED);
						FXMLLoader loader = new FXMLLoader();
						loader.setLocation(getClass().getResource("/view/ChattingView.fxml"));
						Parent root = loader.load();
						Scene scene = new Scene(root);

						cvc = loader.getController();
						cvc.getMember().addAll(chat.getMember());
						cvc.getChtMember().setItems(cvc.getMember());
						cvc.getChattf().appendText("#####    대화방이 열렸습니다.    #####\n");
						
						stage.setTitle("대화하기");
						stage.setScene(scene);
						stage.setResizable(false);
						stage.show();			

					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}
	}

	//대화주고받기
	public void chatting(Datas datas) {
		Chat chat = datas.getChat();
		if(chat.getJoinChat().equals(joinChat)) {
			Platform.runLater(() ->{
				if(chat.getJoinChat().equals(joinChat)) {
					cvc.getChattf().appendText(datas.getMyId() + ": "+chat.getMessage() + "\n");
				}
			});
		}
	}

	//대화친구 초대
	public void inviteChat(Datas datas) {
		Chat chat = datas.getChat();
		Platform.runLater(() -> {	
			if(chat.getJoinChat().equals(joinChat)) {
				cvc.getMember().clear();
				cvc.getMember().addAll(chat.getMember());
				cvc.getChtMember().setItems(cvc.getMember());
				cvc.getChattf().appendText("#####    초대한 사람이 들어왔습니다.    #####\n");
				return;
			} else if(datas.getFriendId().equals(myInfo.getUserId())) {
				joinChat = chat.getJoinChat();
				try {
					Stage stage = new Stage(StageStyle.DECORATED);
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/view/ChattingView.fxml"));
					Parent root = loader.load();
					Scene scene = new Scene(root);

					cvc = loader.getController();
					cvc.getMember().addAll(chat.getMember());
					cvc.getChtMember().setItems(cvc.getMember());
					cvc.getChattf().appendText("#####    대화방이 열렸습니다.    #####\n");
					
					stage.setTitle("대화하기");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.show();			

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	//채팅나가기
	public void exitChat(Datas datas) {
		Chat chat = datas.getChat();
		Platform.runLater(() ->{
			if(chat.getJoinChat().equals(joinChat)) {
				cvc.getMember().clear();
				cvc.getMember().addAll(chat.getMember());
				cvc.getChtMember().setItems(cvc.getMember());
				cvc.getChattf().appendText("#####    대화상대가 나갔습니다.    #####\n");
			}
		});
	}

	//환경설정 완료
	public void setOkPreference() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("환경설정");
			alert.setHeaderText(null);	
			alert.setContentText("저장 완료");
			alert.showAndWait();
		});
	}	


	public boolean isIdChkOk() {//아이디중복확인
		return idChkOk;
	}

	public void setIdChkOk(boolean idChkOk) {
		this.idChkOk = idChkOk;
	}

	public boolean isLoginChk() {//로그인확인
		return loginChk;
	}

	public void setLoginChk(boolean loginChk) {
		this.loginChk = loginChk;
	}

	public User getMyInfo() {
		return myInfo;
	}

	public void setMyInfo(User myInfo) {
		this.myInfo = myInfo;
	}

	public User getFriendInfo() {
		return friendInfo;
	}

	public void setFriendInfo(User friendInfo) {
		this.friendInfo = friendInfo;
	}

	public String getJoinChat() {
		return joinChat;
	}

	public void setJoinChat(String joinChat) {
		this.joinChat = joinChat;
	}

	public ArrayList<String> getFriendList() {
		return friendList;
	}

	public void setFriendList(ArrayList<String> friendList) {
		this.friendList = friendList;
	}

	public void setSfc(SearchFriendController sfc) {
		this.sfc = sfc;
	}

	public void setAlc(AfterLoginController alc) {
		this.alc = alc;
	}
}
