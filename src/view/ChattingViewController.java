package view;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import application.AppMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.Chat;
import model.Datas;
import protocol.Protocol;

public class ChattingViewController implements Initializable {

    @FXML private TextArea chattf;
    @FXML private ListView<String> chtMember;
    @FXML private Button btnInvite;
    @FXML private Button btnExitChat;
    @FXML private TextField inputtf;
    @FXML private Button sendMsg;
    @FXML private Button sendFile;

    private ObservableList<String> member = FXCollections.observableArrayList();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
		chtMember.setItems(member);
		chtMember.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}	
	
	//채팅하기
	@FXML
	public void chatting() {
		Datas datas = new Datas();
		Chat chat = new Chat();
		chat.setMessage(inputtf.getText());
		chat.setJoinChat(AppMain.client.getJoinChat());
		datas.setMyId(AppMain.client.getMyInfo().getUserId());
		datas.setChat(chat);
		datas.setProtocol(Protocol.CHATTING);
		AppMain.client.send(datas);
		inputtf.setText("");
	}
	
	@FXML
	public void exit() { //나가기
		Stage stage = (Stage) btnExitChat.getScene().getWindow();
		stage.setOnCloseRequest(e -> {
			exit();
		});
		Datas datas = new Datas();
		Chat chat = new Chat();
		datas.setProtocol(Protocol.EXIT_CHAT);
		chat.setJoinChat(AppMain.client.getJoinChat());
		member.remove(AppMain.client.getMyInfo().getUserId());
		chat.getMember().addAll(member);
		datas.setChat(chat);
		AppMain.client.setJoinChat("");
		AppMain.client.send(datas);
		stage.close();
	}
	
	//초대하기
	@FXML
	public void invite() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("친구 초대");
		dialog.setHeaderText(null);
		dialog.setContentText("초대 할 아이디 :");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(name -> {
			Datas datas = new Datas();
			Chat chat = new Chat();
			member.add(name.trim());
			chat.getMember().addAll(member);
			chat.setJoinChat(AppMain.client.getJoinChat());
			datas.setChat(chat);
			datas.setProtocol(Protocol.INVITE_CHAT);
			datas.setFriendId(name.trim());
			AppMain.client.send(datas);
		});
	}

	public TextArea getChattf() {
		return chattf;
	}

	public void setChattf(TextArea chattf) {
		this.chattf = chattf;
	}

	public ListView<String> getChtMember() {
		return chtMember;
	}

	public void setChtMember(ListView<String> chtMember) {
		this.chtMember = chtMember;
	}

	public Button getBtnInvite() {
		return btnInvite;
	}

	public void setBtnInvite(Button btnInvite) {
		this.btnInvite = btnInvite;
	}

	public Button getBtnExitChat() {
		return btnExitChat;
	}

	public void setBtnExitChat(Button btnExitChat) {
		this.btnExitChat = btnExitChat;
	}

	public TextField getInputtf() {
		return inputtf;
	}

	public void setInputtf(TextField inputtf) {
		this.inputtf = inputtf;
	}

	public Button getSendMsg() {
		return sendMsg;
	}

	public void setSendMsg(Button sendMsg) {
		this.sendMsg = sendMsg;
	}

	public Button getSendFile() {
		return sendFile;
	}

	public void setSendFile(Button sendFile) {
		this.sendFile = sendFile;
	}

	public ObservableList<String> getMember() {
		return member;
	}

	public void setMember(ObservableList<String> member) {
		this.member = member;
	}
	
}
