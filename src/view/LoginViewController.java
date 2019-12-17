package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.AppMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.User;
import model.Datas;
import protocol.Protocol;

public class LoginViewController implements Initializable {

	@FXML private TextField IDField;
	@FXML private PasswordField pwField;
	@FXML private Button btnLogin;
	@FXML private Button btnJoin;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	//----------------- 이벤트 -------------------

	@FXML
	public void handleLoginBtn() {	//로그인 버튼		
		if (IDField.getText().trim().length() == 0 || pwField.getText().trim().length() == 0) {
			Alert alert = new Alert(AlertType.WARNING);

			alert.setTitle("경고");
			alert.setHeaderText(null);
			alert.setContentText("Id와 PassWord를 입력해주세요");

			alert.showAndWait();
			return;
		}

		Datas datas = new Datas();
		String userId = IDField.getText().trim();
		String userPassword = pwField.getText().trim();
		User user = new User(userId, userPassword);
		datas.setUser(user);
		datas.setProtocol(Protocol.LOGIN);
		AppMain.client.send(datas);
		return;

	}

	public void showAterLogin(Datas datas) { //로그인 후 화면
		try {
			Stage stage = AppMain.primaryStage;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AfterLogin.fxml"));
			Parent root = loader.load();

			AfterLoginController alc = (AfterLoginController) loader.getController();
			AppMain.client.setAlc(alc);
			alc.setMyInfo(datas.getUser());
			alc.getFriend().addAll(datas.getFriendList());
			alc.getFriendList().setItems(alc.getFriend());
			alc.setLblNick(alc.getMyInfo().getUserId());

			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleJoinBtn() { //회원가입버튼
		try {
			Stage joinDialog = new Stage(StageStyle.DECORATED);
			joinDialog.initOwner(btnJoin.getScene().getWindow());
			Parent root = FXMLLoader.load(getClass().getResource("JoinDialog.fxml"));
			Scene scene = new Scene(root);

			joinDialog.setScene(scene);
			joinDialog.setResizable(false);
			joinDialog.show();		

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
