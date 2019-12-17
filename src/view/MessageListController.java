package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Messages;

public class MessageListController implements Initializable {

	@FXML private TableView<Messages> messageList = new TableView<>();
	@FXML private TableColumn<Messages, String> sendId;
	@FXML private TableColumn<Messages, String> receiveId;
	@FXML private TableColumn<Messages, String> sendDate;
	@FXML private TableColumn<Messages, String> content;
	@FXML private Button btnreplyMessage;
    
	private ObservableList<Messages> list = FXCollections.observableArrayList();
	
	private Messages msg;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sendId.setCellValueFactory(new PropertyValueFactory<Messages, String>("sendId"));
		receiveId.setCellValueFactory(new PropertyValueFactory<Messages, String>("receiveId"));
		sendDate.setCellValueFactory(new PropertyValueFactory<Messages, String>("date"));
		content.setCellValueFactory(new PropertyValueFactory<Messages, String>("message"));
		messageList.setItems(list);
		
		messageList.setRowFactory(tv -> showMessage());
//		btnreplyMessage.setOnAction(event -> replyMessage());
	}
	
	@FXML
	public TableRow<Messages> showMessage() { //쪽지확인
		TableRow<Messages> row = new TableRow<>();
		row.setOnMouseClicked(event -> {
			if(event.getClickCount() == 2 && !row.isEmpty()) {
				msg = row.getItem();
				try {
					Stage stage = new Stage(StageStyle.DECORATED);

					FXMLLoader loader = new FXMLLoader();
					loader.setLocation((getClass().getResource("MessageDialog.fxml")));
					Parent root = loader.load();
					Scene scene = new Scene(root);

					MessageDialogController mdc = (MessageDialogController) loader.getController();
					mdc.getLblsend().setText("보낸 사람 : ");
					mdc.getFriendId().setText(msg.getSendId());
					mdc.getMessagetf().setText(msg.getMessage());
					mdc.getMessagetf().setEditable(false);
					mdc.getBtnsend().setVisible(false);						
			
					stage.setTitle("쪽지");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.show();

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		return row;
	}

	
	public void replyMessage() {
		try {
			Stage stage = new Stage(StageStyle.DECORATED);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation((getClass().getResource("MessageDialog.fxml")));
			Parent root = loader.load();
			Scene scene = new Scene(root);

			MessageDialogController mdc = (MessageDialogController) loader.getController();
			mdc.getLblsend().setText("받는 사람 : ");
			mdc.getFriendId().setText(msg.getSendId());
	
			stage.setTitle("쪽지");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	
	public TableView<Messages> getMessageList() {
		return messageList;
	}

	public void setMessageList(TableView<Messages> messageList) {
		this.messageList = messageList;
	}

	public TableColumn<Messages, String> getSendId() {
		return sendId;
	}

	public void setSendId(TableColumn<Messages, String> sendId) {
		this.sendId = sendId;
	}

	public TableColumn<Messages, String> getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(TableColumn<Messages, String> receiveId) {
		this.receiveId = receiveId;
	}

	public TableColumn<Messages, String> getSendDate() {
		return sendDate;
	}

	public void setSendDate(TableColumn<Messages, String> sendDate) {
		this.sendDate = sendDate;
	}

	public TableColumn<Messages, String> getContent() {
		return content;
	}

	public void setContent(TableColumn<Messages, String> content) {
		this.content = content;
	}

	public ObservableList<Messages> getList() {
		return list;
	}

	public void setList(ObservableList<Messages> list) {
		this.list = list;
	}

		
}
