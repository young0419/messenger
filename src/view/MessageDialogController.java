package view;

import java.net.URL;
import java.util.ResourceBundle;

import application.AppMain;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Datas;
import model.Messages;
import protocol.Protocol;

public class MessageDialogController implements Initializable {

	@FXML private Button btnsend;
	@FXML private TextArea messagetf;
	@FXML private Label lblsend;
	@FXML private Label friendId;
	@FXML private Label lbldate;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnsend.setOnAction(event ->sendMessage());
	}

	//쪽지보내기
	public void sendMessage() {
		Datas data = new Datas();
		Messages msg = new Messages();
		data.setMyId(AppMain.client.getMyInfo().getUserId().trim());
		msg.setSendId(AppMain.client.getMyInfo().getUserId().trim());
		msg.setReceiveId(friendId.getText().trim());
		msg.setMessage(messagetf.getText().trim());
		data.setMessages(msg);
		data.setProtocol(Protocol.SEND_MESSAGE);
		AppMain.client.send(data);
		Stage stage = (Stage) btnsend.getScene().getWindow();
		stage.close();
	}
	

	public Button getBtnsend() {
		return btnsend;
	}

	public void setBtnsend(Button btnsend) {
		this.btnsend = btnsend;
	}

	public TextArea getMessagetf() {
		return messagetf;
	}

	public void setMessagetf(TextArea messagetf) {
		this.messagetf = messagetf;
	}

	public Label getLblsend() {
		return lblsend;
	}

	public void setLblsend(Label lblsend) {
		this.lblsend = lblsend;
	}

	public Label getFriendId() {
		return friendId;
	}

	public void setFriendId(Label friendId) {
		this.friendId = friendId;
	}

	public Label getLbldate() {
		return lbldate;
	}

	public void setLbldate(Label lbldate) {
		this.lbldate = lbldate;
	}
	
}
