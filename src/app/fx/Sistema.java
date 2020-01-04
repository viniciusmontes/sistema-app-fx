package app.fx;

import java.net.URL;

import app.fx.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Sistema extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Localização do arquivo fxml.
		URL location = getClass().getResource("view/MainView.fxml");
		FXMLLoader fxml = new FXMLLoader(location);
		Parent view = fxml.load();
		MainController controller = fxml.getController();
		Scene scene = new Scene(view);

		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(controller::fechar);
		primaryStage.setTitle("Sistema");
		primaryStage.show();
		
		

	}

}
