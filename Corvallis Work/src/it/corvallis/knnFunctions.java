package it.corvallis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
 
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.StringToNominal;
 
public class knnFunctions {
	
	public String[] retValue = new String[2];
 
	public knnFunctions() {
		super();
		this.retValue[0] = "";
		this.retValue[1] = "";
	}
	
	public void KNN(String fileName, String uthor) throws Exception {		
		
		BufferedReader br;
        int numFolds = 10;
        br = new BufferedReader(new FileReader(fileName + ".csv"));
        
        CSVLoader loader = new CSVLoader();
        loader.setOptions(new String[] {"-H"});
        loader.setSource(new File(fileName +".csv"));
        loader.setNoHeaderRowPresent(true);
        
        
        //Instances = //...load data with numeric attributes 
        Instances  trainData = loader.getDataSet();
        
        
        /* StringToNominal convert= new StringToNominal();
        String[] options= new String[2];
        options[0]="-R";
        options[1]="1";  //range of variables to make numeric

        convert.setOptions(options);
        convert.setInputFormat(originalTrain);

        Instances trainData =Filter.useFilter(originalTrain, convert);
          */      

        trainData.setClassIndex(0);
 
		//do not use last test
		Instance testItem = trainData.instance(trainData.numInstances()-1);
		trainData.delete(trainData.numInstances()-1);
 
		Classifier ibk = new IBk();		
		ibk.buildClassifier(trainData);
 
		double class1 = ibk.classifyInstance(testItem);
		
		double predictionIndex = ibk.classifyInstance(trainData.instance(trainData.numInstances()-1)); 
		String predictedClassLabel;            
        predictedClassLabel = trainData.classAttribute().value((int) predictionIndex);
        int prediction = (int) class1;
		System.out.println("aspected: " + uthor + "\ncheck: " + prediction + " OR " + predictedClassLabel);
		
        retValue[0]= uthor;
        retValue[1]= Integer.toString(prediction);
	}
}

