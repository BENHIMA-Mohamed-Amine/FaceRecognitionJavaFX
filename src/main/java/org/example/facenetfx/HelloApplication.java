package org.example.facenetfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

public class HelloApplication extends Application {
        static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private VideoCapture capture;
    private CascadeClassifier faceCascade;
    private int imageCount = 0;

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
    public Mat detectFaceAndSave(Mat frame) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY); // Convert to grayscale
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(grayFrame, faces); // Detect faces

        // Create directory to store detected images
        File outputDir = new File("DetectedFaces");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        for (Rect rect : faces.toArray()) {
            // Draw rectangles around detected faces
            Imgproc.rectangle(frame, rect, new Scalar(0, 255, 0), 2);

            // Crop and save detected face
            Mat face = new Mat(frame, rect);
            String filename = "DetectedFaces/face_" + (imageCount++) + ".jpg";
            Imgcodecs.imwrite(filename, face);
            System.out.println("Saved face image: " + filename);
            face.release(); // Release the cropped face memory
        }
        grayFrame.release(); // Release the gray frame memory
        return frame;
    }

    // Capture frame with face detection and saving
    public Image getCaptureWithFaceDetectionAndSave() {
        Mat mat = new Mat();
        if (capture.read(mat)) { // Ensure the frame is successfully read
            Mat processedFrame = detectFaceAndSave(mat);
            Image image = mat2image(processedFrame);
            mat.release(); // Release the original frame memory
            processedFrame.release(); // Release the processed frame memory
            return image;
        }
        return null;
    }

    @Override
    public void start(Stage stage) {
        capture = new VideoCapture(0); // Open the default camera (0)
        if (!capture.isOpened()) {
            System.err.println("Error: Unable to open video capture!");
            return;
        }

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

        // AnimationTimer for continuous frame capture with face detection
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                Image detectedImage = getCaptureWithFaceDetectionAndSave();
                if (detectedImage != null) {
                    imageView.setImage(detectedImage);
                }
            }
        }.start();
    }

    public static void main(String[] args) throws IOException {
//        launch();
//        System.out.println(TensorFlow.version());
        FaceNetModel faceNetModel = new FaceNetModel();
        faceNetModel.loadModel();
//        BufferedImage img = ImageIO.read(new File("/home/benhima/IdeaProjects/FaceNetFX/images/BENHIMA.jpg"));
        BufferedImage img = ImageIO.read(new File("./images/BENHIMA.jpg"));
        Tensor faceTensor = preprocessImage(img); // Ensure this method is defined as in the previous example

        float[][] embedding = faceNetModel.getFaceEmbedding(faceTensor);
        System.out.println("Face Embedding:");
        for (float value : embedding[0]) {
            System.out.printf("%.6f ", value);
        }
    }

    private static Tensor preprocessImage(BufferedImage img) {
        // Resize the image to the required input size for FaceNet (usually 160x160)
        BufferedImage resizedImg = new BufferedImage(160, 160, BufferedImage.TYPE_INT_RGB);
        resizedImg.getGraphics().drawImage(img, 0, 0, 160, 160, null);

        // Convert to float array
        float[] imageData = new float[160 * 160 * 3]; // Assuming RGB
        for (int y = 0; y < 160; y++) {
            for (int x = 0; x < 160; x++) {
                int rgb = resizedImg.getRGB(x, y);
                imageData[(y * 160 + x) * 3] = ((rgb >> 16) & 0xFF) / 255.0f; // Red
                imageData[(y * 160 + x) * 3 + 1] = ((rgb >> 8) & 0xFF) / 255.0f; // Green
                imageData[(y * 160 + x) * 3 + 2] = (rgb & 0xFF) / 255.0f; // Blue
            }
        }

        FloatBuffer buffer = FloatBuffer.wrap(imageData);

        // Create and return a Tensor from the FloatBuffer
        return Tensor.create(new long[]{1, 160, 160, 3}, buffer);
    }
}