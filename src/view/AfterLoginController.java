package view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import application.AppMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Chat;
import model.Datas;
import model.User;
import protocol.Protocol;

public class AfterLoginController implements Initializable {

	@FXML private MenuItem messageList;
	@FXML private MenuItem findFriends;
	@FXML private MenuItem preferences;
	@FXML private MenuItem logout;
	@FXML private MenuItem close;
	@FXML private Label lblNick;
	@FXML private Button btnNick;
	@FXML private MenuItem startChat;

	private ObservableList<String> friend = FXCollections.observableArrayList();
	@FXML private ListView<String> friendList = new ListView<>();

	//유저정보
	private User myInfo;
	private int num = 0;
	private String joinChat = "";


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		friendList.setItems(friend);
		friendList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

	}

	//닉네임 변경
	public void nickChange() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("대화명 입력");
		dialog.setHeaderText(null);
		dialog.setContentText("대화명 입력 :");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(name -> lblNick.setText(name));
		result.ifPresent(name -> myInfo.setUserNickname(name));

		Datas datas = new Datas();
		datas.setProtocol(Protocol.NICK_EDIT);
		datas.setUser(myInfo);
		AppMain.client.send(datas);
	}

	//쪽지보내기
	public void sendMessage(MouseEvent e) {
		if(e.getClickCount() == 2 && !friendList.getItems().isEmpty()) {
			try {
				Stage stage = new Stage(StageStyle.DECORATED);

				FXMLLoader loader = new FXMLLoader();
				loader.setLocation((getClass().getResource("MessageDialog.fxml")));
				Parent root = loader.load();
				Scene scene = new Scene(root);

				MessageDialogController mdc = (MessageDialogController) loader.getController();
				mdc.getLblsend().setText("받는 사람 : ");
				mdc.getFriendId().setText(friendList.getSelectionModel().getSelectedItem());

				stage.setTitle("쪽지보내기");
				stage.setScene(scene);
				stage.setResizable(false);
				stage.show();

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	//대화하기
	@FXML
	public void chatting() {		
		Datas datas = new Datas();
		Chat chat = new Chat();
		ArrayList<String> list = new ArrayList<String>();
		joinChat = myInfo.getUserId() + num;
		list.add(friendList.getSelectionModel().getSelectedItem());
		list.add(myInfo.getUserId());
		
		chat.setJoinChat(joinChat);
		chat.setMember(list);
		datas.setChat(chat);
		datas.setProtocol(Protocol.START_CHAT);
		AppMain.client.send(datas);
	}

	//쪽지함 불러오기
	public void messageListClick() {
		Datas datas = new Datas();
		datas.setMyId(AppMain.client.getMyInfo().getUserId());
		datas.setProtocol(Protocol.MESSAGE_LIST);
		AppMain.client.send(datas);
	}

	//친구찾기
	public void showSearchFriend() {
		Stage stage = new Stage(StageStyle.DECORATED);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("SearchFriend.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);

			SearchFriendController sfc = (SearchFriendController) loader.getController();
			AppMain.client.setSfc(sfc);

			stage.setTitle("친구찾기");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//환경설정	
	public void showPreferences() {
		Stage stage = new Stage(StageStyle.DECORATED);
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Preferences.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);

			PreferencesController pc = loader.getController();
			if(myInfo.getSearchPermit().equals("Y")) {
				pc.getBtnYes().setSelected(true);
			} else if(myInfo.getSearchPermit().equals("N")) {
				pc.getBtnNo().setSelected(true);
			}

			stage.setTitle("환경 설정");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//로그아웃 클릭
	public void handleLogout() {
		Datas datas = new Datas();
		datas.setProtocol(Protocol.LOGOUT);
		datas.setUser(myInfo);
		AppMain.client.setLoginChk(false);
		AppMain.client.send(datas);

		try {
			Parent scene = FXMLLoader.load(AppMain.class.getResource("/view/LoginView.fxml"));
			Stage stage = AppMain.primaryStage;
			stage.setTitle("messenger");
			stage.setScene(new Scene(scene));
			stage.show();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//종료
	public void exit() {
		Datas datas = new Datas();
		datas.setProtocol(Protocol.EXIT);
		datas.setUser(myInfo);
		AppMain.client.send(datas);
		System.exit(0);
	}

	public User getMyInfo() {
		return myInfo;
	}

	public void setMyInfo(User myInfo) {
		this.myInfo = myInfo;
	}

	public Label getLblNick() {
		return lblNick;
	}

	public void setLblNick(String lblNick) {
		this.lblNick.setText(lblNick);
	}

	public ListView<String> getFriendList() {
		return friendList;
	}

	public void setFriendList(ListView<String> friendList) {
		this.friendList = friendList;
	}

	public ObservableList<String> getFriend() {
		return friend;
	}

	public void setFriend(ObservableList<String> friend) {
		this.friend = friend;
	}

}
