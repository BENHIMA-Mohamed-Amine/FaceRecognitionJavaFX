package org.example.facenetfx;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FaceNetModel {
    private Graph graph;
    private Session session;
    private String modelPath = "/home/benhima/IdeaProjects/FaceNetFX/facenet.pb";


    public void loadModel() {
        graph = new Graph();
        byte[] modelBytes = readAllBytesOrExit(Paths.get(modelPath));
        graph.importGraphDef(modelBytes);
        session = new Session(graph);
        System.out.println("model loaded successufilly.");
    }

    private byte[] readAllBytesOrExit(java.nio.file.Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read model file: " + path, e);
        }
    }

    public float[][] getFaceEmbedding(Tensor faceTensor) {
        Tensor phaseTrainTensor = Tensor.create(false);
        try (Tensor tensor = normalizeImage(faceTensor)) {
            Tensor result = session.runner()
                    .fetch("embeddings:0") // Replace with the actual output tensor name
                    .feed("input:0", tensor) // Replace with the actual input tensor name
                    .feed("phase_train:0", phaseTrainTensor)
                    .run()
                    .getFirst();
            float[][] embedding = new float[(int) result.shape()[0]][(int) result.shape()[1]];
//            System.out.println("result shape: " + result.toString());
//            System.out.println("embeddings shape: " + Arrays.toString(embedding));
            result.copyTo(embedding);

            return embedding;
        }
    }

    private Tensor normalizeImage(Tensor image) {
        // Implement normalization logic here if needed
        return image;
    }
}