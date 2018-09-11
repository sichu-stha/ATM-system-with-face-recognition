package application;

import application.FaceRecognizer;
import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;


import java.util.ArrayList;




import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;

import javafx.scene.control.Label;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import application.Database;


public class FaceDetector implements Runnable {

	Database database = new Database();
	ArrayList<String> user;

	FaceRecognizer faceRecognizer = new FaceRecognizer();
	OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
	Java2DFrameConverter paintConverter = new Java2DFrameConverter();
	ArrayList<String> output = new ArrayList<String>();

	@FXML
	public Label ll;
	private Exception exception = null;
	
	private int count = 0;
	public String classiferName;
	public File classifierFile;
	
	
	public boolean saveFace = false;
	public boolean isRecFace = false;
	public boolean isOutput = false;
	private boolean stop = false;

	private CvHaarClassifierCascade classifier = null;
	private CvHaarClassifierCascade classifierSideFace = null;

	
	
	public CvMemStorage storage = null;
	private FrameGrabber grabber = null;
	private IplImage grabbedImage = null, temp, grayImage = null, smallImage = null;
	public ImageView frames2;
	public ImageView frames;
		
	private CvSeq faces = null;

	int recogniseCode;
	public int acc_num;
	public int phone;
	public String acc_type;
	
	public String fname; //first name
	public String Lname; //last name
	public String bankname; //bank name
	public String name; 
	public boolean knownuser=false;//variable to know if user is recognized or not
	public void init() {
		faceRecognizer.init();

		setClassifier("haar/haarcascade_frontalface_alt.xml");
		setClassifierSideFace("haar/haarcascade_profileface.xml");

	}

	public void start() {
		try {
			new Thread(this).start();
		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}
	}

	public void run() {
		try {
			try {
				grabber = OpenCVFrameGrabber.createDefault(0); //parameter 0 default camera , 1 for secondary

				grabber.setImageWidth(700);
				grabber.setImageHeight(700);
				grabber.start();

				grabbedImage = grabberConverter.convert(grabber.grab());

				storage = CvMemStorage.create();
			} catch (Exception e) {
				if (grabber != null)
					grabber.release();
				grabber = new OpenCVFrameGrabber(0);
				grabber.setImageWidth(700);
				grabber.setImageHeight(700);
				grabber.start();
				grabbedImage = grabberConverter.convert(grabber.grab());

			}
			int count = 15;
			grayImage = cvCreateImage(cvGetSize(grabbedImage), 8, 1); //converting image to grayscale
			
			//reducing the size of the image to speed up the processing
			smallImage = cvCreateImage(cvSize(grabbedImage.width() / 4, grabbedImage.height() / 4), 8, 1); 

			stop = false;

			while (!stop && (grabbedImage = grabberConverter.convert(grabber.grab())) != null) {

				Frame frame = grabberConverter.convert(grabbedImage);
				BufferedImage image = paintConverter.getBufferedImage(frame, 2.2 / grabber.getGamma());
				Graphics2D g2 = image.createGraphics();

				if (faces == null) {
					cvClearMemStorage(storage);
					
					//creating a temporary image
					temp = cvCreateImage(cvGetSize(grabbedImage), grabbedImage.depth(), grabbedImage.nChannels());

					cvCopy(grabbedImage, temp);

					cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
					cvResize(grayImage, smallImage, CV_INTER_AREA);
					
					//cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors, flags, min_size, max_size)
					faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
					//face detection
					
					CvPoint org = null;
					if (grabbedImage != null) {

						if (faces.total() == 0) {
							faces = cvHaarDetectObjects(smallImage, classifierSideFace, storage, 1.1, 3,
									CV_HAAR_DO_CANNY_PRUNING);

						}

						if (faces != null) {
							g2.setColor(Color.green);
							g2.setStroke(new BasicStroke(2));
							int total = faces.total();

							for (int i = 0; i < total; i++) {
								
								//printing rectange box where face detected frame by frame
								CvRect r = new CvRect(cvGetSeqElem(faces, i));
								g2.drawRect((r.x() * 4), (r.y() * 4), (r.width() * 4), (r.height() * 4));

								CvRect re = new CvRect((r.x() * 4), r.y() * 4, (r.width() * 4), r.height() * 4);

								cvSetImageROI(temp, re);

								// File f = new File("captures.png");

								org = new CvPoint(r.x(), r.y());

								if (isRecFace) {
									this.recogniseCode = faceRecognizer.recognize(temp);
									if(this.recogniseCode==99)//changed
									{	
										this.knownuser=false;
										String names="Unknown";
										g2.setColor(Color.WHITE);
										g2.setFont(new Font("Arial Black", Font.BOLD, 20));
										g2.drawString(names, (int) (r.x() * 6.5), r.y() * 4);
									}
									else {
									//getting recognised user from the database
									this.knownuser=true;//changed
									database.init();
									user = new ArrayList<String>();
									user = database.getUser(this.recogniseCode);
									this.output = user;

									//printing recognised person name into the frame
									g2.setColor(Color.WHITE);
									g2.setFont(new Font("Arial Black", Font.BOLD, 20));
									String names = user.get(1) + " " + user.get(2);
									g2.drawString(names, (int) (r.x() * 6.5), r.y() * 4);
									}
								}

								if (saveFace) { //saving captured face to the disk
									String fName = "faces/" + acc_num + "-" + fname + "_" + Lname + "_" + count + ".jpg";
									cvSaveImage(fName, temp);
									count++;

								}

							}
							this.saveFace = false;
							faces = null;
						}

						WritableImage showFrame = SwingFXUtils.toFXImage(image, null);

						frames.setImage(showFrame);

					

					}
					cvReleaseImage(temp);
				}

			}

		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}
	}

	public void stop() {
		stop = true;

		grabbedImage = grayImage = smallImage = null;
		try {
			grabber.stop();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			
			e.printStackTrace();
		}
		try {
			grabber.release();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
		
			e.printStackTrace();
		}
		grabber = null;
	}

	public void setClassifier(String name) {

		try {

			setClassiferName(name);
			classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");

			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
			}

			// Preload the opencv_objdetect module to work around a known bug.
			Loader.load(opencv_objdetect.class);
			classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
			classifierFile.delete();
			if (classifier.isNull()) {
				throw new IOException("Could not load the classifier file.");
			}

		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}

	}

	public void setClassifierSideFace(String name) {

		try {

			classiferName = name;
			classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");

			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
			}

			// Preload the opencv_objdetect module to work around a known bug.
			Loader.load(opencv_objdetect.class);
			classifierSideFace = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
			classifierFile.delete();
			if (classifier.isNull()) {
				throw new IOException("Could not load the classifier file.");
			}

		} catch (Exception e) {
			if (exception == null) {
				exception = e;

			}
		}

	}

	

	

	public String getClassiferName() {
		return classiferName;
	}

	public void setClassiferName(String classiferName) {
		this.classiferName = classiferName;
	}

	public void setFrames2(ImageView frames2) {
		this.frames2 = frames2;
	}

	

	public void destroy() {
	}

	

	public ArrayList<String> getOutput() {
		return output;
	}

	public void clearOutput() {
		this.output.clear();
	}

	public void setOutput(ArrayList<String> output) {
		this.output = output;
	}

	public int getRecogniseCode() {
		return recogniseCode;
	}

	public void setRecogniseCode(int recogniseCode) {
		this.recogniseCode = recogniseCode;
	}

	public int getCode() {
		return acc_num;
	}

	public void setCode(int code) {
		this.acc_num = code;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return Lname;
	}

	public void setLname(String lname) {
		Lname = lname;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public String getAcc_type() {
		return acc_type;
	}

	public void setAcc_type(String acc_type) {
		this.acc_type = acc_type;
	}

	public String getBank() {
		return bankname;
	}

	public void setBank(String bankname) {
		this.bankname = bankname;
	}

	public void setFrame(ImageView frame) {
		this.frames = frame;
	}

	public void setSaveFace(Boolean f) {
		this.saveFace = f;
	}

	public Boolean getIsRecFace() {
		return isRecFace;
	}

	public void setIsRecFace(Boolean isRecFace) {
		this.isRecFace = isRecFace;
	}


}
