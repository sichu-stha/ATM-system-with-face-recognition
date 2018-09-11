package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class UserInterfaceController {
	 @FXML
	 private Button cancel;

    @FXML
    private Button atmoption;
    @FXML
    private Text userName;
    
    void initialize() {}
    @FXML
    void cancelBtn(ActionEvent event) {
    	try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
			Stage stage= new Stage();
			Scene scene = new Scene(loader.load());
			stage.setScene(scene);
			((Node) event.getSource()).getScene().getWindow().hide();
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void optionBtn(ActionEvent event) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Information Dialog");
    	alert.setHeaderText("Transaction button clicked!!");
    	alert.setContentText("");
    	alert.showAndWait();
    }
    
    
    public void setUser(String s) {
    	this.userName.setText(s);
    }
}
