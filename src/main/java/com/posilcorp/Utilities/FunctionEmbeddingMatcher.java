package com.posilcorp.Utilities;

import java.io.FileReader;
import java.util.ArrayList;


import org.ejml.simple.SimpleMatrix;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;

public class FunctionEmbeddingMatcher {
    private static ArrayList<FunctionData> functionsData;


    public static class FunctionData {
        public String functionName;
        public String functionArgs;
        public double[] embedding;
        public String functionDesc;

        public FunctionData(String functionName, String functionArgs, String functionDesc, double[] embedding) {
            this.functionName = functionName;
            this.functionArgs = functionArgs;
            this.functionDesc = functionDesc;
            this.embedding = embedding;
        }
    }

    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        SimpleMatrix vecA = new SimpleMatrix(1, vectorA.length, true, vectorA);
        SimpleMatrix vecB = new SimpleMatrix(1, vectorB.length, true, vectorB);

        double dotProduct = vecA.dot(vecB);
        double magnitudeA = vecA.normF();
        double magnitudeB = vecB.normF();

        return dotProduct / (magnitudeA * magnitudeB);

    }

    public static void loadCSV() throws IOException, CsvValidationException, NumberFormatException {
        ArrayList<FunctionData> functionList = new ArrayList<>();
        //CSVOPENER
        try (CSVReader reader = new CSVReader(new FileReader("resources/functionsEmbeddings.csv"))) {
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                // Estrarre nome, argomenti e embedding
                String functionName = line[0];
                String functionArgs = line[1];
                String functionDesc = line[2];
                String[] embeddingString = line[3].replace("[", "").replace("]", "").split(",");
                double[] embedding = new double[embeddingString.length];
                for (int i = 0; i < embeddingString.length; i++) {
                    embedding[i] = Double.parseDouble(embeddingString[i]);
                }

                // Aggiungere la funzione con embedding alla lista
                functionList.add(new FunctionData(functionName, functionArgs, functionDesc, embedding));
            }
        }
        functionsData = functionList;



    }

    public static FunctionData fetchFunction(double[] inputEmbedding) throws Exception {
        FunctionEmbeddingMatcher.FunctionData bestMatch = null;
        double bestSimilarity = -1;

        for (FunctionData function : functionsData) {
            double similarity = cosineSimilarity(inputEmbedding, function.embedding);
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestMatch = function;
            }
        }
        if (bestMatch == null)
            throw new Exception("funzione non trovata");
        return bestMatch;
    }



}
