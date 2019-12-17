package view;

import java.net.URL;
import java.util.ResourceBundle;

import application.AppMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.User;
import model.Datas;
import protocol.Protocol;

public class SearchFriendController implements Initializable {

	@FXML private TextField searchField;
	@FXML private Button btnSearch;
	@FXML private ToggleGroup chkSearch;
	@FXML private RadioButton searchId;
	@FXML private RadioButton searchArea;
	
	private ObservableList<String> friends = FXCollections.observableArrayList();
	@FXML private ListView<String> searchList = new ListView<>(friends);
	
	@FXML private MenuItem addFriends;
		
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		searchId.setOnAction(e -> {
			searchField.setText("");
			searchField.setPromptText("ID를 입력해주세요");
			friends.clear();
			searchList.setItems(friends);
			});
		searchArea.setOnAction(e ->	{
			searchField.setText("");
			searchField.setPromptText("지역명 입력 ex-서울시, 충청남도, 제주도 등등");
			friends.clear();
			searchList.setItems(friends);
		});
	}

	@FXML
	public void searchFriends() { //친구 찾기
		if(searchField.getText().trim().length() == 0) {
			emptyField();
			return;
		}
		
		Datas datas = new Datas();
		User user = new User();
		if(searchId.isSelected()) {
			user.setUserId(searchField.getText());
			datas.setProtocol(Protocol.SEARCH_FRIEND);
		} else if(searchArea.isSelected()) {
			user.setUserArea(searchField.getText());
			datas.setProtocol(Protocol.SEARCH_FRIEND_AREA);
		}
		datas.setUser(user);
		AppMain.client.send(datas);
	}
	
	@FXML
	public void addFriend() {
		Datas datas = new Datas();
		datas.setMyId(AppMain.client.getMyInfo().getUserId());
		datas.setFriendId(searchList.getSelectionModel().getSelectedItem());
		datas.setProtocol(Protocol.ADD_FRIEND);
		AppMain.client.send(datas);
		Stage stage = (Stage) btnSearch.getScene().getWindow();
		stage.close();
	}
	
	public void emptyField() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(null);
		alert.setHeaderText(null);
		alert.setContentText("검색어를 입력하세요");
		alert.showAndWait();
	}


	public ListView<String> getSearchList() {
		return searchList;
	}

	public void setSearchList(ListView<String> searchList) {
		this.searchList = searchList;
	}

	public Button getBtnSearch() {
		return btnSearch;
	}

	public void setBtnSearch(Button btnSearch) {
		this.btnSearch = btnSearch;
	}

	public ObservableList<String> getFriends() {
		return friends;
	}

	public void setFriends(ObservableList<String> friends) {
		this.friends = friends;
	}

	public MenuItem getAddFriends() {
		return addFriends;
	}

	public void setAddFriends(MenuItem addFriends) {
		this.addFriends = addFriends;
	}

}
