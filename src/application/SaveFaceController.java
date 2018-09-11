package application;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

import application.FaceDetector;
import application.Database;


public class SaveFaceController {

	
	public String filePath="./faces";
	
	@FXML
	private Button startCam;
	@FXML
	private Button stopBtn;
	@FXML
	private Button saveBtn;
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
    private PasswordField pin;

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
		saveBtn.setDisable(false);

		dataPane.setDisable(false);
	
		
		putOnLog(" Real Time WebCam Stream Started !");
		
		//**********************************************************************************************
	}
	int count = 0;

	@FXML
	protected void saveFace() throws SQLException {

		//Input Validation
		if (fname.getText().trim().isEmpty() || phone.getText().trim().isEmpty() || acc_type.getText().trim().isEmpty()||code.getText().trim().isEmpty()|| bankname.getText().trim().isEmpty()) {

			new Thread(() -> {

				try {
					warning.setVisible(true);

					Thread.sleep(2000);

					warning.setVisible(false);

				} catch (InterruptedException ex) {
				}

			}).start();

		} else {
			//Progressbar
			pb.setVisible(true);

			savedLabel.setVisible(true);

			new Thread(() -> {

				try {

					faceDetect.setFname(fname.getText());

					faceDetect.setFname(fname.getText());
					faceDetect.setLname(lname.getText());
					faceDetect.setAcc_type(acc_type.getText());
					faceDetect.setCode(Integer.parseInt(code.getText()));
					faceDetect.setBank(bankname.getText());
					faceDetect.setPhone(Integer.parseInt(phone.getText()));

					database.setFname(fname.getText());
					database.setLname(lname.getText());
					database.setAcc_type(acc_type.getText());
					database.setCode(Integer.parseInt(code.getText()));
					database.setBank(bankname.getText());
					database.setPhone(Integer.parseInt(phone.getText()));
					database.setPin(pin.getText());
					
					database.insert();

					pb.setProgress(100);

					savedLabel.setVisible(true);
					Thread.sleep(2000);

					pb.setVisible(false);

					savedLabel.setVisible(false);

				} catch (InterruptedException ex) {
				}

			}).start();

			faceDetect.setSaveFace(true);

		}

	}

	@FXML
	protected void stopCam() throws SQLException {

		faceDetect.stop();

		startCam.setVisible(true);
		stopBtn.setVisible(false);

		/* this.saveFace=true; */
		
		code.clear();
		fname.clear();
		lname.clear();
		acc_type.clear();
		bankname.clear();
		pin.clear();
		phone.clear();
		
		putOnLog("Cam Stream Stopped!");


		saveBtn.setDisable(true);
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

}
