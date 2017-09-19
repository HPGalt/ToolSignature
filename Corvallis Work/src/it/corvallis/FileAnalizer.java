package it.corvallis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.SwingUtilities;
// import javax.swing.filechooser.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import it.corvallis.libsvm.svm;
import it.corvallis.libsvm.svm_model;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.corvallis.svmFunctions;

public class FileAnalizer  {

	// variable files and selector
	private int selector;
	private String authName;
	
	final int ROWS = 10;
    final int COLS = 10;
    public double result[][] = new double[ROWS][COLS];
    public File fileImage;
    
    public String[][] returnText = new String[4][12];
	
    
    
    
	// features
	
    public FileAnalizer() {
    	this.fileImage =null;
    	this.selector = 0;
    }
    
    
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
        	String[] extensions = new String[] { "png", "PNG" };
            for (final String ext : extensions) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
    
    // function to count rows in CSV file
    public int countRows(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
        byte[] c = new byte[1024];
        int count = 0;
        int readChars = 0;
        boolean empty = true;
        while ((readChars = is.read(c)) != -1) {
            empty = false;
            for (int i = 0; i < readChars; ++i) {
                if (c[i] == '\n') {
                    ++count;
                }
            }
        }
        return (count == 0 && !empty) ? 1 : count;
        } finally {
        is.close();
       }
    }
    
    
    
    
    
    /*
     * This function load image and using the selected algoritms, or the list of Features
     * output the arranged list of feature
     */
    public void loadImge(File loadedFile, Boolean[] arrayOfFeatures, String author, Boolean ifICDAR){
    	int sel = 2;
    	
    	
    	this.fileImage = loadedFile;
    	this.selector = sel;
    	
    	if (ifICDAR){
        	//parse the nme on ICDAR way
        	String[] split = author.split("_");
        	author= split[0].substring(split[0].length()-3);
        	
        } else {
        	//author is on filename
        	String[] split = author.split("_");
        	author= split[0];                	
        }
    	author = author.replace("NISDCC-", "");
        author = author.replace("_6g.PNG", "");
        author = author.replace(".png", "");
        author = author.replace(".PNG", "");
        author = author.replace(".jpg", "");
        author = author.replace(".JPG", "");
        
        
    	this.authName = author;
    	icdarAlgorithm prototype = new icdarAlgorithm(fileImage);
    	
    	// running the analysis...
    	switch (this.selector) {
        case 2:
        	// TODO Amonicomprensive System
        	prototype.loadSystem(arrayOfFeatures, this.authName);        	
        	this.result = prototype.result;
            break;
        }
    	
    	
    	
    }
    
    /*
     * Function adapted to lead the whole data sets by features selection 
     */
    public void loadDataSet(Boolean[] arrayofFeatures, Boolean ifICDAR, String pathFolder) throws IOException, ParseException{
    	
    	JSONParser parser = new JSONParser();
    	Reader reader = new FileReader("config.json");
    	Object jsonObj = parser.parse(reader);
    	JSONObject jsonObject = (JSONObject) jsonObj;
    	
    	String pathDB = (String) jsonObject.get("imgeDBPathName");
    	
    	// File representing the folder that you select using a FileChooser
        File dir = new File(pathDB);
        
        if (!pathFolder.equals("")){
        	File dirpath = new File(pathFolder);
        	if (dirpath.isDirectory()) {
        		// check if the pharamether path is correwct
        		dir = new File(pathFolder);
        	}
        }
        
        if (dir.isDirectory()) { // make sure it's a directory
        	for (final File f : dir.listFiles(IMAGE_FILTER)) {
                BufferedImage img = null;
                try {
                    img = ImageIO.read(f);
                    // you probably want something more involved here
                    System.out.println("Analyze Image: " + f.getName());
                    // analizo singola immagine
                    //File outputfile = new File("loadingimg.png");
                    //ImageIO.write(img, "png", outputfile);
                    authName = "";
                    authName = "" + f.getName();                   
                    loadImge(f, arrayofFeatures, authName, ifICDAR);                  
                    authName = "";
                } catch (final IOException e) {
                    // handle errors here
                }
            }            
        }
    }
    
    
    
    /*
     * Function that save each model Distance or SVM via features selection
     */
    public void saveTheModel(Boolean[] arrayOfFeatures, Boolean[] arrayOfAlg, String author, int tgenuine, int tfake, int tforged){
    	
    	// collect CSV data into 2 arrays
    	double[][] xtrain = null;
    	double[][] ytrain = null;
    	// DEVO CALCOLARNE LE DIMENSIONI O NON SE NE FA NULLA
    	// E POI, QUANTI MODELLI SALVO? IO DIREI UNO PER OGNI FEATURES
    	
    	
    	
    	String newline = "\n";
    	
    	
		/* 	0 Area
			1 PixelsIncidenceAngle
			2 CentroidIncidenceAngle		
			3 RunLengthPdf			 
			4 GaussianFilteredChainCodePdf		
			5 DirectionsPdf
			6 CurvaturePdf
			7 Tortuosity
			8 ChainCodePdf
			9 EdgeTrackPdf			
			10 LocalBinaryPatternPdf 
			11 HistogramOfOrientedGradientsPdf 
		*/
    	int manyFeatures = 0;
    	for (int i = 0; i < (arrayOfFeatures.length) ;i++){
    		if (arrayOfFeatures[i]){
    			manyFeatures++;
    		}
    	}
    	int kindex = 0;
    	String[] fileFeatureName = new String[manyFeatures]; // array of structured features 
    	
    	// build array of feature file name
    	for (int i = 0; i < (arrayOfFeatures.length) ;i++){
    		if (arrayOfFeatures[i]){
    			if (i == 0) fileFeatureName[kindex] ="AreaFeature";
        		if (i == 1) fileFeatureName[kindex] ="PixelsIncidenceAngleFeature";
        		if (i == 2) fileFeatureName[kindex] ="CentroidIncidenceAngleFeature";
        		if (i == 3) fileFeatureName[kindex] ="RunLengthPdfFeature";
        		if (i == 4) fileFeatureName[kindex] ="GaussianFilteredChainCodePdf";
        		if (i == 5) fileFeatureName[kindex] ="DirectionsPdf";
        		if (i == 6) fileFeatureName[kindex] ="CurvaturePdf";
        		if (i == 7) fileFeatureName[kindex] ="TortuosityPdf";
        		if (i == 8) fileFeatureName[kindex] ="ChainCodePdf";
        		if (i == 9) fileFeatureName[kindex] ="EdgeTrackPdf";
        		if (i == 10) fileFeatureName[kindex] ="LocalBinaryPatternPdf";
        		if (i == 11) fileFeatureName[kindex] ="HistogramOfOrientedGradientsPdf";
        		kindex++;        		
    		}
    	}
    	Map<String, Integer> mappafeature = null;
    	mappafeature = new HashMap<String, Integer>();
    	
    	// build the leng and kind of array data set features
    	int arrayOfFeaturesLenght = 0;
    	
    	System.out.println("fileFeatureName: " + fileFeatureName.length);
    	
    	for (int i = 0; i < (fileFeatureName.length) ;i++){    		
    		String fileFName ="";
    		fileFName = fileFeatureName[i];
    		/*CSVReader reader; */
    		CSVReader reader;
    		try {
    			 	reader = new CSVReader(new FileReader(fileFName + ".csv"));
    			 	String [] nextLine;
    			 	while ((nextLine = reader.readNext()) != null) {
    			 		// the file exst.
    			 		arrayOfFeaturesLenght += Integer.parseInt(nextLine[1]);
    			 		System.out.println("Nome File: " + fileFName);
    			 		System.out.println("Nome autore: " + author);
    			 		mappafeature.put(fileFName, Integer.parseInt(nextLine[1]));
    			 		
    			 		break; // mi serve slo il primo! 
	    		    }
    			 	
    		} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				// e.printStackTrace();
    			    // do not break the for just continue in any case
    				continue;
    		} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    		}
    		
    	}
    	
    	System.out.println("number of total features for this model: " + arrayOfFeaturesLenght);

    	/*
    	*******************************************
    	********************************************
    	******************************************
    	*roba a finire solo quando ho finito i traduttori
    	*quindi un passo alla volta
    	*- distanza euclidea
    	*- SVM
    	* ***********************************/
    	for (int i = 0; i < (fileFeatureName.length) ;i++){
    		String fileFName ="";
    		fileFName = fileFeatureName[i];
    		CSVReader reader;
    		try {
    				int dimFeatureContent = mappafeature.get(fileFName);
    				double[] distanceMeanVect = new double[dimFeatureContent];
    				
    				int howManyGoodAuth = 0;
    				int howManyBadAuth = 0;
    				int howManyFakeAuth = 0;
    				int totalAauthors =0;
    			 	reader = new CSVReader(new FileReader(fileFName + ".csv"));
    			 	//int nrows = countRows(fileFName + ".csv");
    			 	
    			 	int nrows = tgenuine + tfake + tforged;
    			 	
    			 	
    			 	xtrain = new double[nrows][dimFeatureContent];
    			 	ytrain = new double[nrows][1];
    			 	
    			 	String [] nextLine;
    			 	int k = 0,j =0;
    			 	while ((nextLine = reader.readNext()) != null) {
    			 		// Tip: faccio finta che TUTTE le altre classi siano false
    			 		// checking the author
    			 		//System.out.println("distanceMeanVect: " +distanceMeanVect.length);    			 		
    			 		if (nextLine[0].equals(author)){     			 			
    			 			// a good author line of this feature exo 10 signs
    			 			if (howManyGoodAuth < tgenuine){    			 				    			 		
	    			 			howManyGoodAuth++;
	    			 			//load into distanceMeanVect & xtrain for SVM
	    			 			for (int Kline = 3; Kline < nextLine.length; Kline++){
	    			 				//System.out.println("Kline: " + nextLine[Kline] + ", " + Kline );
	    			 				//System.out.println("inserito: " +distanceMeanVect[Kline-3]);
	    			 				// faccio la somma per il vettore media 
	    			 				distanceMeanVect[Kline-3] += Double.parseDouble(nextLine[Kline]);    
	    			 				xtrain[totalAauthors][Kline-3] = Double.parseDouble(nextLine[Kline]); 
	    			 			}
	    			 			ytrain[totalAauthors][0] = 1;    			 			
	    			 			totalAauthors++;
    			 			}
	    			 	} else { // ONLYH BAD SIGNS HERE	    			 		
	    			 		if (nextLine[0].equals(author+"_F")){ 
	    			 			//different author or considered as FORGED exp 50 signs
	    			 			if (howManyFakeAuth < tforged){ 
	    			 				howManyFakeAuth++;
	    			 				for (int Kline = 3; Kline < nextLine.length; Kline++){   
		    			 				xtrain[totalAauthors][Kline-3] = Double.parseDouble(nextLine[Kline]); 
		    			 			}
		    			 			ytrain[totalAauthors][0] = -1;    			 			
		    			 			totalAauthors++;
	    			 			}
	    			 		} else {
	    			 			//different author or considered as FAKE exo 200 signs
	    			 			if (howManyFakeAuth < tfake){ 
	    			 				howManyBadAuth++;
	    			 				for (int Kline = 3; Kline < nextLine.length; Kline++){   
		    			 				xtrain[totalAauthors][Kline-3] = Double.parseDouble(nextLine[Kline]); 
		    			 			}
		    			 			ytrain[totalAauthors][0] = -1;    			 			
		    			 			totalAauthors++;
	    			 			}
	    			 		}	    			 			    			 	
	    			 	}
	    		     }
    			 	// ho scorso tutto il CSV delle feature "filename"
    			 	
    			 	//threshold distance
    			 	if (arrayOfAlg[0]){    					
	    			 	// setto le cose per la distanza base
	    	 			double summ = 0;
	    	 			// calcolo media e deviazione standard
	    	 			for (int kz = 0; kz < distanceMeanVect.length; kz++){    			 				
	    	 				distanceMeanVect[kz] =  (distanceMeanVect[kz] / howManyGoodAuth); 
	    	 				summ += distanceMeanVect[kz];
	    	 			}    	 		
	    	 			/*System.out.println("howManyGoodAuth: " + howManyGoodAuth);
	    	 			System.out.println("distanceMeanVect: " + distanceMeanVect[2]);
	    	 			System.out.println("summ: " + summ);*/
	    	 			double media = summ / ( dimFeatureContent );
	    	 			double theDistance =0;
	    	 			for (int kz = 0; kz < distanceMeanVect.length; kz++){    			 				    			 				
	    	 				theDistance += Math.pow(distanceMeanVect[kz] - media, 2);
	    	 			}
	    	 			//System.out.println("theDistance: " + theDistance);
	    	 			//System.out.println("dimFeatureContent: " + dimFeatureContent);
	    	 			double Soglia = Math.sqrt(theDistance / ( dimFeatureContent));
	    	 			/*
	    	 			 * scrivo e salvo il valore di soglia per quell'autore e il relativo vettore themplate
	    	 			 * autore, soglia, valori[];
	    	 			 */
	    	 			String csvName = "models/" + fileFName + "_"+ author + "_" + tgenuine + "_"+ tfake + "_"+ tforged +"_Distance.csv";
						String dataFeature = "";
						for( int jk=0; jk <distanceMeanVect.length; jk++ ) {
							dataFeature += String.valueOf(distanceMeanVect[jk]) + ";";
							//System.out.println(f0[jk]); // Test
		            	}
						// create thing to write on CSV
						String lineToWrite = author + ";" + Soglia + ";" +  dataFeature ;
						String [] analyseddata = lineToWrite.split(";");
						FileWriter mFileWriter;
						CSVWriter writer;
						try {
							//mCsvWriter = new CSVWriter(mFileWriter);
							mFileWriter = new FileWriter(csvName, false);
							writer = new CSVWriter(mFileWriter);
							writer.writeNext(analyseddata);
							writer.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    			 	}
    			 	
    	    		if (arrayOfAlg[1]){
    					//SVM
    					// Creating Model
    					svm_model m = svmFunctions.svmTrain(xtrain,ytrain);
    					
    					svm.svm_save_model("models/"+ fileFName + "_"+ author + "_" + tgenuine + "_"+ tfake + "_"+ tforged +"_SVM.dat", m);
    					
    					System.out.println("Training SVM model calculated");
    					// TODO
    					//double[] ypred = svmPredict(xtest, m); 
    				}
    			 	
    		} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    		} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    		}
    		
    	}
    }

    
    /*
     * Funciont for evaluate or predict the signature's owners
     */
    public void evaluateModel(File loadedFile, Boolean[] arrayOfFeatures, Boolean[] arrayOfAlg, String author, Boolean isFake){
    	
    	this.fileImage = loadedFile;
    	this.authName = author;
    	icdarAlgorithm prototype = new icdarAlgorithm(fileImage);
    	
    	 // Fill each row with ""
        for (String[] row: returnText) Arrays.fill(row, "");

    	// calculating features for evaluation
    	prototype.calcFeature(arrayOfFeatures, this.authName); 
    	
    	// build array of feature file name
    	int manyFeatures = 0;
    	for (int i = 0; i < (arrayOfFeatures.length) ;i++){
    		if (arrayOfFeatures[i]){
    			manyFeatures++;
    		}
    	}
    	int kindex = 0;
    	String[] fileFeatureName = new String[manyFeatures]; // array of structured features 
    	
    	String resoultFineName ="";
    	for (int i = 0; i < (arrayOfFeatures.length) ;i++){
    		if (arrayOfFeatures[i]){
    			resoultFineName +="1";
				if (i == 0) fileFeatureName[kindex] ="AreaFeature";
        		if (i == 1) fileFeatureName[kindex] ="PixelsIncidenceAngleFeature";
        		if (i == 2) fileFeatureName[kindex] ="CentroidIncidenceAngleFeature";
        		if (i == 3) fileFeatureName[kindex] ="RunLengthPdfFeature";
        		if (i == 4) fileFeatureName[kindex] ="GaussianFilteredChainCodePdf";
        		if (i == 5) fileFeatureName[kindex] ="DirectionsPdf";
        		if (i == 6) fileFeatureName[kindex] ="CurvaturePdf";
        		if (i == 7) fileFeatureName[kindex] ="TortuosityPdf";
        		if (i == 8) fileFeatureName[kindex] ="ChainCodePdf";
        		if (i == 9) fileFeatureName[kindex] ="EdgeTrackPdf";
        		if (i == 10) fileFeatureName[kindex] ="LocalBinaryPatternPdf";
        		if (i == 11) fileFeatureName[kindex] ="HistogramOfOrientedGradientsPdf";
        		kindex++;        		
    		} else {
    			resoultFineName +="0";
    		}
    	}
    	
    	// ceck sulle dimensioni delle Features
    	Map<String, Integer> mappafeature = null;
    	mappafeature = new HashMap<String, Integer>();
    	int arrayOfFeaturesLenght = 0;
    	for (int i = 0; i < (fileFeatureName.length) ;i++){    		
    		String fileFName ="";
    		fileFName = fileFeatureName[i];
    		/*CSVReader reader; */
    		CSVReader reader;
    		try {
    			 	reader = new CSVReader(new FileReader(fileFName + ".csv"));
    			 	String [] nextLine;
    			 	while ((nextLine = reader.readNext()) != null) {
    			 		arrayOfFeaturesLenght = Integer.parseInt(nextLine[1]);
    			 		mappafeature.put(fileFName, Integer.parseInt(nextLine[1]));
    			 		break; // mi serve slo il primo! 
	    		    }
    		} catch (FileNotFoundException e) { // do not break the for just continue in any case
    				continue;
    		} catch (IOException e) {	// TODO Auto-generated catch block
    				e.printStackTrace();
    		}
    	}
    	
    	
    	
    	
    	
    	/*
    	 * TODO inserire i check e i controlli su quali feature sono state modellizzate!
    	 */

    	System.out.println(Arrays.toString(arrayOfAlg));
    	
    	
    	// calcolo e salvo i modelli per le future verifiche    	
		if (arrayOfAlg[0]){
			//Threshold distance
			
			// loop inside features
			int checkTheOkVote = 0;
			for (int j=0; j <fileFeatureName.length; j++){
				String fileFName ="";
	    		fileFName = fileFeatureName[j];
	    		CSVReader reader, reader2;
	    		try {
    				int dimFeatureContent = mappafeature.get(fileFName);
    				double soglia=0;
    				double[] modelVector = new double[dimFeatureContent];
    				double[] targetVector = new double[dimFeatureContent];
    				
    				// get data from Model
    			 	reader = new CSVReader(new FileReader("models/"+fileFName+"_"+ author +"_Distance" + ".csv"));
    	 			/*il mdello sarà come:
    	 			 * autore, soglia, valori[] */
    			 	String[] nextLine;
					while ((nextLine = reader.readNext()) != null) { // in teoria ho una sola linea
						soglia = Double.parseDouble(nextLine[1]); 
						for(int g = 2; g < nextLine.length; g++){
							modelVector[g-2] = Double.parseDouble(nextLine[g]);
						}
    			 	}
					
    				// get data from Target
    			 	reader2 = new CSVReader(new FileReader("temp/Verify_"+fileFName+ ".csv"));
					while ((nextLine = reader2.readNext()) != null) { // in teoria ho una sola linea
						for(int g = 3; g < nextLine.length; g++){
							targetVector[g-3] = Double.parseDouble(nextLine[g]);
						}
    			 	}
					
					System.out.println("soglia di "+fileFName +": " + soglia);
					//System.out.println("modelVector: " + Arrays.toString(modelVector));
					//System.out.println("targetVector: " + Arrays.toString(targetVector));
					
					//calcolo la distanza
					double distance = 0;
					for(int t=0; t<modelVector.length; t++){
						distance += Math.pow((modelVector[t] - targetVector[t]),2);						
					}
					distance = Math.sqrt(distance /modelVector.length );
					
					System.out.println("distance di "+fileFName +": " + distance);
					
					//clcolo lo scostamento dalla soglia
					double scostamento = (soglia / 100)*5; // dal 5 al 12% di scostamento
					
					if (distance <= soglia){
						System.out.println("OK!");
						checkTheOkVote++;
					} else{
						if (distance <= (soglia+scostamento)){
							System.out.println("OK un po' meno!");
							checkTheOkVote++;
						}
					}
	    			 	
	    		}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
	    		
				
			}
			
			
			
			
			String lineToWrite;
			int previsto = 1;
			if (isFake) {
				previsto = -1;
				this.returnText[0][2] = "The Signature was a FAKE one.";
			}
			
			this.returnText[0][0] = "******************* \nResoult for Distance system:";
					
			System.out.println("votittlali: "+checkTheOkVote+" voti da superare: "+ (int) ((fileFeatureName.length / 2)+1));
			if ( checkTheOkVote >= (int) ((fileFeatureName.length / 2)+1)){
				//Signature ok!				
				this.returnText[0][1] = "Signature "+loadedFile.getName()+" \nhas been predicted Genuine for Author "+authName+".";
				
				lineToWrite = loadedFile.getName() + ";" + authName + ";"+previsto+";1;" ;
			} else {
				//Bad Signature!
				lineToWrite = loadedFile.getName() + ";" + authName + ";"+previsto+";-1;" ;
				this.returnText[0][1] = "WARNING! Signature "+loadedFile.getName()+" \nhas been predicted FAKE for Author "+authName+".";
			}
			
			String csv = "resoults/"+resoultFineName+"_Distance.csv";					
			
			String[] analyseddata = lineToWrite.split(";");
			try {
				FileWriter mFileWriter = new FileWriter(csv, true);
				CSVWriter writer = new CSVWriter(mFileWriter);
				writer.writeNext(analyseddata);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
			
		}
		
		if (arrayOfAlg[1]){
			//SVM
			// loop inside features
			int checkTheOkVote = 0;
			for (int j=0; j <fileFeatureName.length; j++){
				double[] ypred = new double[1];
				
				String fileFName ="";
	    		fileFName = fileFeatureName[j];
	    		CSVReader reader, reader2;
	    		int dimFeatureContent = mappafeature.get(fileFName);
	    		double[][] xtest = new double[1][dimFeatureContent];
	    		try {
    				svm_model m;    				
    				// get data from Model
    			 	//reader = new CSVReader(new FileReader("models/"+fileFName+"_"+ author +"_SVM" + ".csv"));
    			 	m = svm.svm_load_model("models/"+fileFName+"_"+ author +"_SVM" + ".dat");
    	 			/*il mdello sarà un SVM model formattato per  cavoli suoi. */    			 	
    			 	String[] nextLine;					
    				// get data from Target
    			 	reader2 = new CSVReader(new FileReader("temp/Verify_"+fileFName+ ".csv"));
					
					while ((nextLine = reader2.readNext()) != null) { // in teoria ho una sola linea
						for(int g = 3; g < nextLine.length; g++){
							xtest[0][g-3] = Double.parseDouble(nextLine[g]);
						}
    			 	}
					ypred = svmFunctions.svmPredict(xtest, m);
					
					System.out.println("(Actual:" + isFake  + " Prediction:" + ypred[0] + ")"); 
					
					int theVal = 1;
					if (isFake) theVal = -1;
					if (theVal == ypred[0]){
						System.out.println("OK!");
						checkTheOkVote++;
					} 
	    			 	
	    		}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
	    		
				
			}
			
			
			
			
			String lineToWrite;
			int previsto = 1;
			if (isFake) {
				previsto = -1;
				this.returnText[1][2] = "The Signature was a FAKE one.";
			}
			
			this.returnText[1][0] = "******************* \nResoult for SVM system:";
					
			System.out.println("votittlali: "+checkTheOkVote+" voti da superare: "+ (int) ((fileFeatureName.length / 2)+1));
			if ( checkTheOkVote >= (int) ((fileFeatureName.length / 2)+1) ){
				//Signature ok!				
				this.returnText[1][1] = "Signature "+loadedFile.getName()+" \nhas been predicted Genuine for Author "+authName+".";
				
				lineToWrite = loadedFile.getName() + ";" + authName + ";"+previsto+";1;" ;
			} else {
				//Bad Signature!
				lineToWrite = loadedFile.getName() + ";" + authName + ";"+previsto+";-1;" ;
				this.returnText[1][1] = "WARNING! Signature "+loadedFile.getName()+" \nhas been predicted FAKE for Author "+authName+".";
			}
			
			String csv = "resoults/"+resoultFineName+"_SVM.csv";					
			
			String[] analyseddata = lineToWrite.split(";");
			try {
				FileWriter mFileWriter = new FileWriter(csv, true);
				CSVWriter writer = new CSVWriter(mFileWriter);
				writer.writeNext(analyseddata);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		if (arrayOfAlg[2]){
			//KNN
			
			// loop inside features
			int checkTheOkVote = 0;
			for (int j=0; j <fileFeatureName.length; j++){
				String fileFName ="";
	    		fileFName = fileFeatureName[j];
	    		CSVReader reader, reader2;
	    		try {
    				int dimFeatureContent = mappafeature.get(fileFName);
    				double soglia=0;
    				double[] targetVector = new double[dimFeatureContent];
    				
    				// get data from Target
    				//reader = new CSVReader(new FileReader(fileFName+ ".csv"));
    			 	reader2 = new CSVReader(new FileReader("temp/Verify_"+fileFName+ ".csv"));
					String[] nextLine;
					while ((nextLine = reader2.readNext()) != null) { // in teoria ho una sola linea
						for(int g = 3; g < nextLine.length; g++){
							targetVector[g-3] = Double.parseDouble(nextLine[g]);
						}
    			 	}
				
					String csvTest = "models/test_" + fileFName +".csv";
					String dataFeature = "";
					reader = new CSVReader(new FileReader(fileFName+ ".csv"));					
					String lineToWrite;
					String [] analyseddata;
					
					File f = new File(csvTest);			         
					f.createNewFile();
					f.delete();

					
					// copy only the useful file.
					while ((nextLine = reader.readNext()) != null) { // in teoria ho una sola linea
						
						if (author.equals(nextLine[0])){
							lineToWrite = "1;";
						} else {
							lineToWrite = "-1;";
						}
						
						for(int g = 3; g < nextLine.length; g++){
							lineToWrite += Double.parseDouble(nextLine[g]) + ";";
						}
						analyseddata = lineToWrite.split(";");
						try {
							FileWriter mFileWriter = new FileWriter(csvTest, true);
							CSVWriter writer = new CSVWriter(mFileWriter);
							writer.writeNext(analyseddata);
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
    			 	}
					
					for( int jk=0; jk <targetVector.length; jk++ ) {
						dataFeature += String.valueOf(targetVector[jk]) + ";";
	            	}
					 
					lineToWrite = "1;" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						FileWriter mFileWriter = new FileWriter(csvTest, true);
						CSVWriter writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
												
					knnFunctions  knn= new knnFunctions();					
					knn.KNN("models/test_" + fileFName, "1");					
					System.out.println("Classe prevista: "+ knn.retValue[0]+" Classe attesa:"+ knn.retValue[1]);					
					
					if (knn.retValue[0].equals(knn.retValue[1])){
						System.out.println("OK!");
						checkTheOkVote++;
					}  
	    			 	
	    		}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
				
			}
			String lineToWrite;
			int previsto = 1;
			if (isFake) {
				previsto = -1;
				this.returnText[3][2] = "The Signature was a FAKE one.";
			}
			
			this.returnText[3][0] = "******************* \nResoult for Random Forest system:";
					
			System.out.println("votittlali: "+checkTheOkVote+" voti da superare: "+ (int) ((fileFeatureName.length / 2)+1));
			if ( checkTheOkVote >= (int) ((fileFeatureName.length / 2)+1) ){
				//Signature ok!				
				this.returnText[3][1] = "Signature "+loadedFile.getName()+" \nhas been predicted Genuine for Author "+authName+".";
				
				lineToWrite = loadedFile.getName() + ";" + authName + ";"+previsto+";1;" ;
			} else {
				//Bad Signature!
				lineToWrite = loadedFile.getName() + ";" + authName + ";"+previsto+";-1;" ;
				this.returnText[3][1] = "WARNING! Signature "+loadedFile.getName()+" \nhas been predicted FAKE for Author "+authName+".";
			}
			
			String csv = "resoults/"+resoultFineName+"_KNN.csv";					
			
			String[] analyseddata = lineToWrite.split(";");
			try {
				FileWriter mFileWriter = new FileWriter(csv, true);
				CSVWriter writer = new CSVWriter(mFileWriter);
				writer.writeNext(analyseddata);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		if (arrayOfAlg[3]){
			//RANDOM FOREST
			
			// loop inside features
			int checkTheOkVote = 0;
			for (int j=0; j <fileFeatureName.length; j++){
				String fileFName ="";
	    		fileFName = fileFeatureName[j];
	    		CSVReader reader, reader2;
	    		try {
    				int dimFeatureContent = mappafeature.get(fileFName);
    				double soglia=0;
    				double[] targetVector = new double[dimFeatureContent];
    				
    				// get data from Target
    				//reader = new CSVReader(new FileReader(fileFName+ ".csv"));
    			 	reader2 = new CSVReader(new FileReader("temp/Verify_"+fileFName+ ".csv"));
					String[] nextLine;
					while ((nextLine = reader2.readNext()) != null) { // in teoria ho una sola linea
						for(int g = 3; g < nextLine.length; g++){
							targetVector[g-3] = Double.parseDouble(nextLine[g]);
						}
    			 	}
				
					String csvTest = "models/test_" + fileFName +".csv";
					String dataFeature = "";
					reader = new CSVReader(new FileReader(fileFName+ ".csv"));					
					String lineToWrite;
					String [] analyseddata;
					
					File f = new File(csvTest);			         
					f.createNewFile();
					f.delete();

					
					// copy only the useful file.
					while ((nextLine = reader.readNext()) != null) { // in teoria ho una sola linea
						if (author.equals(nextLine[0])){
							lineToWrite = "1;";
						} else {
							lineToWrite = "-1;";
						}
						for(int g = 3; g < nextLine.length; g++){
							lineToWrite += Double.parseDouble(nextLine[g]) + ";";
						}
						analyseddata = lineToWrite.split(";");
						try {
							FileWriter mFileWriter = new FileWriter(csvTest, true);
							CSVWriter writer = new CSVWriter(mFileWriter);
							writer.writeNext(analyseddata);
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
    			 	}
					
					for( int jk=0; jk <targetVector.length; jk++ ) {
						dataFeature += String.valueOf(targetVector[jk]) + ";";
	            	}
					 
					lineToWrite = "1;" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						FileWriter mFileWriter = new FileWriter(csvTest, true);
						CSVWriter writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
												
					randomForestFunctions  rf= new randomForestFunctions();					
					rf.rfnew("models/test_" + fileFName, targetVector);					
					System.out.println("Classe prevista: "+ rf.retValue[0]+" Classe attesa:"+ rf.retValue[1]);					
					
					if (rf.retValue[0].equals(rf.retValue[1])){
						System.out.println("OK!");
						checkTheOkVote++;
					}  
	    			 	
	    		}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
				
			}
			
			String lineToWrite;
			int previsto = 1;
			if (isFake) {
				previsto = -1;
				this.returnText[3][2] = "The Signature was a FAKE one.";
			}
			
			this.returnText[3][0] = "******************* \nResoult for Random Forest system:";
					
			System.out.println("votittlali: "+checkTheOkVote+" voti da superare: "+ (int) ((fileFeatureName.length / 2)+1));
			if ( checkTheOkVote >= (int) ((fileFeatureName.length / 2)+1) ){
				//Signature ok!				
				this.returnText[3][1] = "Signature "+loadedFile.getName()+" \nhas been predicted Genuine for Author "+authName+".";
				
				lineToWrite = loadedFile.getName() + ";" + authName + ";"+previsto+";1;" ;
			} else {
				//Bad Signature!
				lineToWrite = loadedFile.getName() + ";" + authName + ";"+previsto+";-1;" ;
				this.returnText[3][1] = "WARNING! Signature "+loadedFile.getName()+" \nhas been predicted FAKE for Author "+authName+".";
			}
			
			String csv = "resoults/"+resoultFineName+"_RF.csv";					
			
			String[] analyseddata = lineToWrite.split(";");
			try {
				FileWriter mFileWriter = new FileWriter(csv, true);
				CSVWriter writer = new CSVWriter(mFileWriter);
				writer.writeNext(analyseddata);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		


    }


    
    
    public void evaluateModelOnDataSet() throws IOException, ParseException{
    	
    	JSONParser parser = new JSONParser();
    	Reader reader = new FileReader("config.json");
    	Object jsonObj = parser.parse(reader);
    	JSONObject jsonObject = (JSONObject) jsonObj;
    	
    	String pathDB = (String) jsonObject.get("imgeDBPathName");
    	
    	Boolean[] Daramola, Qatar, Griffith, Sabanci, Tabessa;
    	Boolean[] Dist, Knn, SVM, RF;
    	
    	Daramola = new Boolean[] 	{true,true,true,false,false,false,false,false,false,false,false,false};
    	Tabessa = new Boolean[] 	{false,false,false,true,false,false,false,false,false,false,false,false};
    	Griffith = new Boolean[] 	{false,false,false,false,true,false,false,false,false,false,false,false};
    	Qatar = new Boolean[] 		{false,false,false,false,false,true,true,true,true,true,false,false};
    	Sabanci = new Boolean[] 	{false,false,false,false,false,false,false,false,false,false,true,true};
    	
    	Dist = new Boolean[]	{true,false,false,false};
    	SVM = new Boolean[]		{false,true,false,false};
    	Knn = new Boolean[]		{false,false,true,false};    	
    	RF = new Boolean[]		{false,false,false,true};
    	
    	// File representing the folder that you select using a FileChooser
        File dir = new File(pathDB);
        if (dir.isDirectory()) { // make sure it's a directory
        	for (final File f : dir.listFiles(IMAGE_FILTER)) {
                BufferedImage img = null;
                try {
                    img = ImageIO.read(f);
                    System.out.println("Analyze Image: " + f.getName());
                    authName = "";
                    authName = "" + f.getName();                   

                	String[] split = authName.split("_");
                	authName= split[0].substring(split[0].length()-3);
                	authName = authName.replace("NISDCC-", "");
                	authName = authName.replace("_6g.PNG", "");
                	authName = authName.replace(".png", "");
                	authName = authName.replace(".PNG", "");
                    authName = authName.replace(".jpg", "");
                    authName = authName.replace(".JPG", "");
                    
                    evaluateModel(f, Daramola, Dist, authName, false);
                    evaluateModel(f, Tabessa, Knn, authName, false);
                    evaluateModel(f, Griffith, SVM, authName, false);
                    evaluateModel(f, Qatar, RF, authName, false);
                    evaluateModel(f, Sabanci, SVM, authName, false);

                    authName = "";
                } catch (final IOException e) {
                    // handle errors here
                }
            }            
        }
    }
}