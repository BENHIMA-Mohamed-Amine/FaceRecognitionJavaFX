package org.example.facenetfx;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FaceNetModel {
    private Graph graph;
    private Session session;
    private String modelPath = "/home/benhima/IdeaProjects/FaceNetFX/facenet.pb";


    public void loadModel() {
        graph = new Graph();
        byte[] modelBytes = readAllBytesOrExit(Paths.get(modelPath));
        graph.importGraphDef(modelBytes);
        session = new Session(graph);
//        System.out.println("model loaded successufilly.");
    }

    private byte[] readAllBytesOrExit(java.nio.file.Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read model file: " + path, e);
        }
    }

    public float[][] getFaceEmbedding(String imagePath) throws Exception {
        BufferedImage img = ImageIO.read(new File(imagePath));
        try (Tensor faceTensor = preprocessImage(img)) {
            Tensor phaseTrainTensor = Tensor.create(false);
            try (Tensor tensor = normalizeImage(faceTensor)) {
                Tensor result = session.runner()
                        .fetch("embeddings:0")
                        .feed("input:0", tensor)
                        .feed("phase_train:0", phaseTrainTensor)
                        .run()
                        .getFirst();
                float[][] embedding = new float[(int) result.shape()[0]][(int) result.shape()[1]];
                result.copyTo(embedding);
                return embedding;
            }
        } catch (Exception e) {
            System.out.println("execption in get face embedding: " + e.getMessage());
            throw new Exception("error");
        }
    }

    private Tensor normalizeImage(Tensor image) {
        return image;
    }

    private Tensor preprocessImage(BufferedImage img) {
        BufferedImage resizedImg = new BufferedImage(160, 160, BufferedImage.TYPE_INT_RGB);
        resizedImg.getGraphics().drawImage(img, 0, 0, 160, 160, null);
        float[] imageData = new float[160 * 160 * 3];
        for (int y = 0; y < 160; y++) {
            for (int x = 0; x < 160; x++) {
                int rgb = resizedImg.getRGB(x, y);
                imageData[(y * 160 + x) * 3] = ((rgb >> 16) & 0xFF) / 255.0f;
                imageData[(y * 160 + x) * 3 + 1] = ((rgb >> 8) & 0xFF) / 255.0f;
                imageData[(y * 160 + x) * 3 + 2] = (rgb & 0xFF) / 255.0f;
            }
        }
        FloatBuffer buffer = FloatBuffer.wrap(imageData);
        return Tensor.create(new long[]{1, 160, 160, 3}, buffer);
    }

    /**
     * Checks if two face embeddings are the same person based on Euclidean distance.
     * @param embedding1 First face embedding (1D array)
     * @param embedding2 Second face embedding (1D array)
     * @return true if the distance is below the threshold, false otherwise
     */
    public boolean isSamePerson(float[] embedding1, float[] embedding2) {
        if (embedding1.length != embedding2.length) {
            throw new IllegalArgumentException("Embeddings must have the same length.");
        }

        float distance = calculateEuclideanDistance(embedding1, embedding2);
        float threshold = 0.9f; // You can adjust this threshold as needed
        return distance < threshold;
    }

    public boolean isSamePerson(String imagePath1, String imagePath2) throws Exception {
        float[][] imageEmbedding1 = getFaceEmbedding(imagePath1);
        float[][] imageEmbedding2 = getFaceEmbedding(imagePath2);
        return isSamePerson(imageEmbedding1[0], imageEmbedding2[0]);
    }

    /**
     * Calculates the Euclidean distance between two embeddings.
     * @param embedding1 First face embedding (1D array)
     * @param embedding2 Second face embedding (1D array)
     * @return Euclidean distance
     */
    private float calculateEuclideanDistance(float[] embedding1, float[] embedding2) {
        float sum = 0.0f;
        for (int i = 0; i < embedding1.length; i++) {
            float diff = embedding1[i] - embedding2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }

}