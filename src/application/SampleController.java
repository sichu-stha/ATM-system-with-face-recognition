package application;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

import application.FaceDetector;
import application.Database;


public class SampleController {

	
	public String filePath="./faces";// file location path where the face will be saved & retrieved
	
	@FXML
	private Button startCam;
	@FXML
	private Button stopBtn;
	@FXML
	private Button recogniseBtn;

    @FXML
    private Button stopRecBtn;
	@FXML
	private ImageView frame;
	@FXML
	private ImageView motionView;
	@FXML
	private AnchorPane pdPane;
	@FXML
	private TitledPane dataPane;
	@FXML
	private TextField fname;
	@FXML
	private TextField lname;
	@FXML
	private TextField code;
	@FXML
	private TextField phone;
	@FXML
	private TextField bankname;
	@FXML
	private TextField acc_type;
	@FXML
	public ListView<String> logList;
	@FXML
	public ListView<String> output;
	@FXML
	public ProgressIndicator pb;
	@FXML
	public Label savedLabel;
	@FXML
	public Label warning;
	@FXML
	public Label title;
	@FXML
	public TilePane tile;
	
    @FXML
    private AnchorPane enterPin;//changed
    
    @FXML
    private PasswordField pinlogin;
    @FXML
    private Button submit;
    
    private String accpin;
    private int pintry=0;
    String cuser;
//**********************************************************************************************
	FaceDetector faceDetect = new FaceDetector();	//Creating Face detector object										
	Database database = new Database();		//Creating Database object

	ArrayList<String> user = new ArrayList<String>();
	ImageView imageView1;
	
	public static ObservableList<String> event = FXCollections.observableArrayList();
	public static ObservableList<String> outEvent = FXCollections.observableArrayList();

	public boolean enabled = false;
	public boolean isDBready = false;

	public void putOnLog(String data) {

		Instant now = Instant.now();

		String logs = now.toString() + ":\n" + data;

		event.add(logs);

		logList.setItems(event);

	}

	@FXML
	protected void startCamera() throws SQLException {

		//initializing objects from start camera button event
		faceDetect.init();

		faceDetect.setFrame(frame);

		faceDetect.start();

		if (!database.init()) {

			putOnLog("Error: Database Connection Failed ! ");

		} else {
			isDBready = true;
			putOnLog("Success: Database Connection Succesful ! ");
		}

		//Activating other buttons
		startCam.setVisible(false);
		stopBtn.setVisible(true);

		if (isDBready) {
			recogniseBtn.setDisable(false);
		}

		dataPane.setDisable(false);


				putOnLog(" Real Time WebCam Stream Started !");
		
		//**********************************************************************************************
	}
	int count = 1;

	@FXML
	protected void faceRecognise() {

		
		faceDetect.setIsRecFace(true);
		stopRecBtn.setDisable(false);
		
		boolean userrec=faceDetect.knownuser;
		System.out.println(userrec);
		recogniseBtn.setText("Get Face Data");
		if(userrec)//if user is recognized only then data is fetched----changed
		{	
		
		//Getting detected faces
		user = faceDetect.getOutput();
		
		if (count > 0) {
			enterPin.setVisible(true);//changed
			//Retrieved data will be shown in Fetched Data pane
			String t = "********* Face Data: " + user.get(1) + " " + user.get(2) + " *********";
			cuser=user.get(1)+" "+user.get(2);
			outEvent.add(t);

			String n1 = "First Name\t\t:\t" + user.get(1);

			outEvent.add(n1);

			output.setItems(outEvent);

			String n2 = "Last Name\t\t:\t" + user.get(2);

			outEvent.add(n2);

			output.setItems(outEvent);

			String fc = "Acc_num\t\t\t:\t" + user.get(0);

			outEvent.add(fc);

			output.setItems(outEvent);

			String r = "Phone no\t\t\t:\t" + user.get(3);

			outEvent.add(r);

			output.setItems(outEvent);

			String a = "Acc_type\t\t\t:\t" + user.get(4);

			outEvent.add(a);

			output.setItems(outEvent);
			String s = "Bank\t\t\t\t:\t" + user.get(5);

			outEvent.add(s);

			output.setItems(outEvent);
			
			accpin=user.get(6);
		}

		count++;
		}
		putOnLog("Face Recognition Activated !");



	}


    @FXML
    void stopRecog(ActionEvent event) {
    	faceDetect.setIsRecFace(false);
		faceDetect.clearOutput();
		count=0;
		
		enterPin.setVisible(false);
		pinlogin.clear();
		
		this.user.clear();
		
		
		recogniseBtn.setText("Recognise Face");
		putOnLog("Face Recognition Deactivated !");
    }

	@FXML
	protected void stopCam() throws SQLException {

		
		//changed above
		faceDetect.stop();

		startCam.setVisible(true);
		stopBtn.setVisible(false);
		stopRecBtn.setDisable(true);
		count=0;
		enterPin.setVisible(false);//changed
		pinlogin.clear();
		/* this.saveFace=true; */

		putOnLog("Cam Stream Stopped!");

		recogniseBtn.setDisable(true);
		dataPane.setDisable(true);
		database.db_close();
	
		putOnLog("Database Connection Closed");
		isDBready=false;
	}

	private ImageView createImageView(final File imageFile) {

		try {
			final Image img = new Image(new FileInputStream(imageFile), 120, 0, true, true);
			imageView1 = new ImageView(img);

			imageView1.setStyle("-fx-background-color: BLACK");
			imageView1.setFitHeight(120);

			imageView1.setPreserveRatio(true);
			imageView1.setSmooth(true);
			imageView1.setCache(true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return imageView1;
	}
	
	@FXML
    void loginAtm(ActionEvent event) {
		if(pinlogin.getText().equals(accpin)) {
			try {
				this.stopCam();
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInterface.fxml"));
				
				Stage stage= new Stage();
				Scene scene = new Scene(loader.load());
				stage.setScene(scene);
				
				
				((Node) event.getSource()).getScene().getWindow().hide();
				
				//sending user info to userinterface
				UserInterfaceController controller = loader.<UserInterfaceController>getController();
				controller.setUser(cuser);
				
				stage.show();	
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Wrong pin");
		alert.setHeaderText("Account Pin number incorrect");
		if(pintry>2) {
		alert.setContentText("Pin incorrect try limit exceeded, Please contact bank");	
		alert.showAndWait();
				try {
					this.stopCam();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((Node) event.getSource()).getScene().getWindow().hide();
			}
		else {
			alert.setContentText("Please try again!\nTries left: "+(3-pintry));
			alert.showAndWait();
			pinlogin.clear();		
			pintry++;	
			}				
		}
    }

}
