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
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.User;
import model.Datas;
import protocol.Protocol;

public class JoinController implements Initializable{

	@FXML private TextField joinIdField;
	@FXML private PasswordField joinPwField;
	@FXML private PasswordField chkPwField;
	@FXML private ToggleGroup gender;
	@FXML private RadioButton M;
	@FXML private RadioButton F;
	@FXML private ComboBox<String> areaCb;
	@FXML private Button chkID;
	@FXML private Button btnJoinOk;
	@FXML private Button btnJoinCancle;	

	private ObservableList<String> areaList = 
			FXCollections.observableArrayList(
					"서울시", "경기도", "인천시", "강원도", 
					"충청남도", "충청북도", "대전시", "세종시",
					"전라남도", "전라북도", "광주시",
					"경상남도", "경상북도", "대구시", "부산시", "울산시",
					"제주도"
					);

	private Datas datas;

	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		areaCb.getItems().setAll(areaList);

		chkID.setOnAction(event -> {
			if(joinIdField.getText().trim().length() == 0) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle(null);
				alert.setHeaderText(null);
				alert.setContentText("아이디를 입력해주세요.");
				alert.showAndWait();
				return;
			}
			handleBtnIDChk();
		});
		btnJoinCancle.setOnAction(event ->close(btnJoinCancle));
		btnJoinOk.setOnAction(event -> handleBtnJoinOk());
	}

	// 아이디 중복체크
	public void handleBtnIDChk() {
		datas = new Datas();
		String chkId = joinIdField.getText().trim();
		datas.setProtocol(Protocol.ID_CHK);
		datas.setMyId(chkId);
		AppMain.client.send(datas);
	}


	// 회원 가입 OK 버튼
	private void handleBtnJoinOk() {
		if(!AppMain.client.isIdChkOk()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(null);
			alert.setHeaderText(null);
			alert.setContentText("아이디 중복확인을 해주세요");
			alert.showAndWait();
			return;
		}

		if(!joinPwField.getText().trim().equals(chkPwField.getText())) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(null);
			alert.setHeaderText(null);
			alert.setContentText("비밀번호가 일치 하지 않습니다.");
			alert.showAndWait();
			return;

		} else {		
			datas = new Datas();
			datas.setProtocol(Protocol.USERJOIN);
			datas.setUser(joinUserInfo());
			AppMain.client.send(datas);

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(null);
			alert.setHeaderText(null);
			alert.setContentText("회원가입 완료");
			alert.showAndWait();
			AppMain.client.setIdChkOk(false);
			close(btnJoinOk);
		}
	}

	//회원가입 정보 전달
	public User joinUserInfo() {
		String userId = joinIdField.getText().trim();
		String userPassword = joinPwField.getText().trim();
		String userGender = null;
		if(M.isSelected())
			userGender = "M";
		else
			userGender = "F";
		String userArea = areaCb.getSelectionModel().getSelectedItem().trim();

		User user = new User(userId, userPassword, userGender, userArea);
		return user;
	}

	// 화면 닫기
	public void close(Button bt) {
		Stage stage = (Stage) bt.getScene().getWindow();
		stage.close();
	}


}
