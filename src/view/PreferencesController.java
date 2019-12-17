package view;

import java.net.URL;
import java.util.ResourceBundle;

import application.AppMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import model.User;
import model.Datas;
import protocol.Protocol;

public class PreferencesController implements Initializable {
	
	@FXML private ToggleGroup searchOk;
    @FXML private RadioButton btnYes;
    @FXML private RadioButton btnNo;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	@FXML
	public void setSearchPermit(ActionEvent e) {
		Datas datas = new Datas();
		User user = new User();
		if(e.getSource() == btnYes) {
			user.setUserId(AppMain.client.getMyInfo().getUserId());
			user.setSearchPermit("Y");
		}
		if(e.getSource() == btnNo) {
			user.setUserId(AppMain.client.getMyInfo().getUserId());
			user.setSearchPermit("N");
		}
		datas.setProtocol(Protocol.PREFERENCES);
		datas.setUser(user);
		AppMain.client.send(datas);
	}

	public RadioButton getBtnYes() {
		return btnYes;
	}

	public void setBtnYes(RadioButton btnYes) {
		this.btnYes = btnYes;
	}

	public RadioButton getBtnNo() {
		return btnNo;
	}

	public void setBtnNo(RadioButton btnNo) {
		this.btnNo = btnNo;
	}

}
