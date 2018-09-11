package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WelcomeController {
	

    @FXML
    void loadMainWindow(ActionEvent event) {
    	try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Sample.fxml"));
			Stage stage= new Stage();
			Scene scene = new Scene(loader.load());
			stage.setScene(scene);
			((Node) event.getSource()).getScene().getWindow().hide();
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

}
