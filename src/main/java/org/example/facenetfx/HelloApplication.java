package org.example.facenetfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.control.Label;

public class HelloApplication extends Application {
        static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private VideoCapture capture;
    private CascadeClassifier faceCascade;
    private int imageCount = 0;
    private FaceNetModel faceNetModel = new FaceNetModel();

    // Convert OpenCV Mat to JavaFX Image
    public Image mat2image(Mat mat) {
        MatOfByte bytes = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, bytes);
        return new Image(new ByteArrayInputStream(bytes.toArray()));
    }

    // Load face detection classifier
    public void loadClassifier() {
        faceCascade = new CascadeClassifier();
        String haarCascadePath = "/home/benhima/opencv/data/haarcascades/haarcascade_frontalface_default.xml";
        if (!faceCascade.load(haarCascadePath)) {
            System.err.println("Error: Unable to load face detection classifier!");
        }
    }

    // Detect faces in a frame and save them
    public PersonFrame detectFaceAndSave(Mat frame) throws Exception {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY); // Convert to grayscale
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(grayFrame, faces); // Detect faces

        // Create directory to store detected images
        File outputDir = new File("DetectedFaces");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        boolean samePerson = false;
        for (Rect rect : faces.toArray()) {
            // Draw rectangles around detected faces
            Imgproc.rectangle(frame, rect, new Scalar(0, 255, 0), 2);
            // Crop and save detected face
            Mat face = new Mat(frame, rect);
            String filename = "DetectedFaces/face_" + (imageCount++) + ".jpg";
            Imgcodecs.imwrite(filename, face);
//            System.out.println("Saved face image: " + filename);
            face.release(); // Release the cropped face memory
            if(verifyPerson(filename)){
                samePerson = true;
                break;
            }
        }
        PersonFrame personFrame = new PersonFrame(samePerson, frame);
        return personFrame;
    }

    public boolean verifyPerson(String detectedFace) throws Exception {
        File folder = new File("./DBFaces");
        if (!(folder.exists() && folder.isDirectory())){
            System.out.println("DBFaces not a folder or don't exist");
            return false;
        }
        File[] files = folder.listFiles();
        if(files != null){
            for(File file: files){
                if(file.isFile()){
                    if(faceNetModel.isSamePerson("./DBFaces/"+file.getName(), detectedFace)){
//                        System.out.println("same person");
                        return true;
                    }
//                    else {
////                        System.out.println("not the same person");
//                    }
                }
            }
        }
        return false;
    }

    public void detectFaceAndSave(String imagePath) {
        // Load the image from the given path
        Mat frame = Imgcodecs.imread(imagePath);
        if (frame.empty()) {
            System.err.println("Error: Unable to load image from path: " + imagePath);
            return;
        }

        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY); // Convert to grayscale
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(grayFrame, faces); // Detect faces

        // Create directory to store detected images
        File outputDir = new File("DBFaces");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        for (Rect rect : faces.toArray()) {
            // Crop and save detected face
            Mat face = new Mat(frame, rect);
            String filename = "DBFaces/face_" + (imageCount++) + ".jpg";
            Imgcodecs.imwrite(filename, face);
//            System.out.println("Saved face image: " + filename);
            face.release(); // Release the cropped face memory
        }

        // Release resources
        grayFrame.release();
        frame.release();
    }

    // Capture frame with face detection and saving
    public PersonFrame getCaptureWithFaceDetectionAndSave() throws Exception {
        Mat mat = new Mat();
        if (capture.read(mat)) { // Ensure the frame is successfully read
            PersonFrame personFrame = detectFaceAndSave(mat);
            Mat processedFrame = personFrame.getFrame();
            Image image = mat2image(processedFrame);
            mat.release(); // Release the original frame memory
            processedFrame.release(); // Release the processed frame memory
            personFrame.setImg(image);
            return personFrame;
        }
        return null;
    }

    @Override
    public void start(Stage stage) throws Exception {
        capture = new VideoCapture(0); // Open the default camera (0)
        if (!capture.isOpened()) {
            System.err.println("Error: Unable to open video capture!");
            return;
        }
        faceNetModel.loadModel();
        loadClassifier(); // Load the face detection classifier

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true); // Preserve aspect ratio during resizing
        HBox hbox = new HBox(imageView);
        Scene scene = new Scene(hbox, 800, 600); // Set initial window size
        stage.setScene(scene);
        stage.setTitle("Webcam Stream with Face Detection");
        stage.setResizable(true); // Allow window resizing
        stage.setOnCloseRequest(event -> capture.release()); // Release camera on close
        stage.show();

        // Resize ImageView with the window
        imageView.fitWidthProperty().bind(hbox.widthProperty());
        imageView.fitHeightProperty().bind(hbox.heightProperty());

        File folder = new File("./images");
        if (!(folder.exists() && folder.isDirectory())){
            System.out.println("DBFaces not a folder or don't exist");
            System.exit(0);
            Platform.exit();
        }
        File[] files = folder.listFiles();
        if(files != null){
            for(File file: files){
                if(file.isFile()){
                    detectFaceAndSave("./images/"+file.getName());
                }
            }
        }

        final boolean[] isSamePerson = {false};


        // AnimationTimer for continuous frame capture with face detection
        if(!isSamePerson[0]){
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    Image detectedImage = null;
                    try {
                        PersonFrame personFrame = getCaptureWithFaceDetectionAndSave();
                        detectedImage = personFrame.getImg();
                        isSamePerson[0] = personFrame.isSamePerson();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (detectedImage != null) {
                        imageView.setImage(detectedImage);
                    }
                    if(isSamePerson[0]){
                        this.stop();
                    }
                }
            }.start();
        }

        Label authenticatedLabel = new Label("You are authenticated");
        VBox authenticatedBox = new VBox(authenticatedLabel);
        scene.setRoot(authenticatedBox);

    }

    public static void main(String[] args) throws Exception {
        launch();
    }
}