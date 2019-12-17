package application;
import java.io.IOException;

import client.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AppMain extends Application {

	public static Stage primaryStage;

	public static Client client = new Client();

	@Override
	public void start(Stage primaryStage) {
		AppMain.primaryStage = primaryStage;
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("../view/LoginView.fxml"));

			AnchorPane login = (AnchorPane) loader.load();

			Scene scene = new Scene(login);

			primaryStage.getIcons().add(new Image("file:src/image/login.png"));
			primaryStage.setTitle("messenger");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setOnCloseRequest(e -> {
				System.exit(0);
			});
			primaryStage.show();		

		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public static void main(String[] args) {
		launch(args);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

}
