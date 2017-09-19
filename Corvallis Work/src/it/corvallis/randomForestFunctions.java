package it.corvallis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Random;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;


public class randomForestFunctions {
	//numFolds in number of crossvalidations usually between 1-10
	public String[] retValue = new String[2];
	/**
     * @throws java.lang.Exception
     */
	
	
    public void rfnew(String Filename, double[] predictArray) throws Exception {
        BufferedReader br;
        int numFolds = 10;
        br = new BufferedReader(new FileReader(Filename + ".csv"));
        
        CSVLoader loader = new CSVLoader();
        loader.setOptions(new String[] {"-H"});
        loader.setSource(new File(Filename +".csv"));
        loader.setNoHeaderRowPresent(true);
        
        Instances trainData = loader.getDataSet();
 
        //Instances trainData = new Instances(br);
        trainData.setClassIndex(0);
        br.close();
        
        //trainData.add(new DenseInstance(1.0, predictArray));
        RandomForest rf = new RandomForest();
        rf.setNumIterations(100);         
     
        Evaluation evaluation = new Evaluation(trainData);
        evaluation.crossValidateModel(rf, trainData, numFolds, new Random(1));
        rf.buildClassifier(trainData);
        // PrintWriter out = new PrintWriter("orf_out");
        // out.println("No.\tTrue\tPredicted");

        String oldClassLabel = trainData.instance(trainData.numInstances()-1).toString(trainData.classIndex());
         // Discreet prediction
        double predictionIndex = rf.classifyInstance(trainData.instance(trainData.numInstances()-1)); 

        // Get the predicted class label from the predictionIndex.
        String predictedClassLabel;            
        predictedClassLabel = trainData.classAttribute().value((int) predictionIndex);
        //out.println((i+1)+"\t"+trueClassLabel+"\t"+predictedClassLabel);
        //double class1 = ibk.classifyInstance(testItem);
		
		//double predictionIndex = ibk.classifyInstance(trainData.instance(trainData.numInstances()-1)); 
				
        int prediction = (int) predictionIndex;
        
        
        System.out.println("aspected: " + oldClassLabel + "\ncheck: "  + prediction);
        
       retValue[0]= oldClassLabel;
       retValue[1]= Integer.toString(prediction);
        
    }
	public randomForestFunctions() {
		super();
		this.retValue[0] = "";
		this.retValue[1] = "";
	}
 
    
}