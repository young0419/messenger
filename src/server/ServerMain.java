package server;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Server.fxml"));
			Scene scene = new Scene(root);
			
			primaryStage.setTitle("server");
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(event -> {
				Platform.exit();
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
}
