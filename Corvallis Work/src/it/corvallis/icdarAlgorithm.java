package it.corvallis;

import ij.IJ;
import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.features.AbstractFeature.Grid;
import it.corvallis.features.impl.LongestSegment;
import it.corvallis.features.FeatureFactory;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.preprocessing.PreProcessingFactory;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import static java.lang.Math.toIntExact;

/*
 * algorithm selectors form any algorithms:
 * 0 NULL
 * 1 Daramola
 * 2 Tabessa
 * 3 Griffith
 * 4 Qatar
 * 5 Sabanci
 */

public class icdarAlgorithm {
	private File img;
	private ImagePlus image;
	final int ROWS = 10;
    final int COLS = 10;
    public double result[][] = new double[ROWS][COLS];
	
    
    public JSONObject loadJsonData() throws IOException, ParseException{
    	// load variable from json file configuration
    	JSONParser parser = new JSONParser();
    	Reader reader = new FileReader("config.json");
    	Object jsonObj = parser.parse(reader);
    	JSONObject jsonObject = (JSONObject) jsonObj;
     	//System.out.println("Json = " + jsonObject.toJSONString());
    	
		return jsonObject;
    	 	
    }
    
    
    
    
    // Gearing imaging Funcions
	public icdarAlgorithm(File theImg){
		this.img = theImg;
	}
	public ImagePlus loadImage(File img) {			
		return new ImagePlus(img.getAbsolutePath());
	}
	public void saveImage(ImagePlus img, String path) {
		IJ.save(img, path);
	}
	
	
	/*
	 * Generic System: Calculate the system or the mix 
	 * of Features selected to combine a Classifier
	 *  
	 *  load the relative feartures form an array of values 
	 */
	public void loadSystem(Boolean[] features, String authName){
		
   
		//acquisizione
		image = loadImage(img);
		
		//acquisizione Dati dal JSON
		JSONObject savedData = null;
		try {
			savedData = loadJsonData();
		} 
		catch (IOException | ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//get progressive number
		int numTest = ( (Long) savedData.get("progressiveTest")).intValue();	
		numTest++;
		
		
		//binarizzazione
		this.image.setProcessor(image.getProcessor().convertToByte(true));
		ImagePlus imageNoCrop = PreProcessingFactory.getPreProcess(PreprocessingType.Binarize).execute(image);
		image = PreProcessingFactory.getPreProcess(PreprocessingType.Crop).execute(imageNoCrop);
		//this.saveImage(image, "temp.png");
		ImagePlus SckeFilImage = PreProcessingFactory.getPreProcess(PreprocessingType.Skeletonize).execute(image);
		//this.saveImage(SckeFilImage, "SckeFilImage.png");
		
		//read CSV datas for multple istance detection
		/* 
			Daramola
				0 AreaFeature
				1 PixelsIncidenceAngleFeature
				2 CentroidIncidenceAngleFeature			
			Tabessa
				3 RunLengthPdfFeature			 
			Griffith
				4 ??  GaussianFilteredChainCodePdfFeature		
			Qatar	
				5 DirectionsPdfFeature
				6 CurvaturePdfFeature
				7 TortuosityFeature ( TortuosityPdfFeature )
				8 ChainCodePdfFeature 
				9 EdgeTrackPdfFeature 			
			Sabanci
				10 LocalBinaryPatternPdfFeature 
				11 HistogramOfOrientedGradientsPdfFeature 
		 */
		
			
		for( int i=0; i < features.length; i++ ) {
			// System.out.println("\n Searching Feature " + i + " on " + features[i]+" \n");
			if (features[i]){
				//var of file to write
				FileWriter mFileWriter;
				CSVWriter writer;
				String csv;				
				String dataFeature;
				String lineToWrite;
				String [] analyseddata;
				double angle;
				String newline = "";
				
				// json data reader
				int[] numbers;
				JSONArray criteria;
				
				
				switch (i) {
				case 0:
					// Daramola AreaFeature
					System.out.println("Processing AreaFeature..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("AreaFeature");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test Daramola data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
										
					// Area sull'intera immagine (griglia sulle 64 celle calcolate sul centroide)
					double [] f0 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.AreaFeature)
							.extractFeature(image, new double[]{Grid.CentroidBased.getId(), numbers[0]}));
					
					// Insert nel CSV
					
					csv = "AreaFeature.csv";
					dataFeature = "";
					for( int jk=0; jk <f0.length; jk++ ) {
						dataFeature += String.valueOf(f0[jk]) + ";";
						//System.out.println(f0[jk]); // Test
	            	}
					// create thing to write on CSV
					lineToWrite = authName + ";" + f0.length + ";" + numTest  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						//mCsvWriter = new CSVWriter(mFileWriter);
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				
				case 1:
					// Daramola PixelsIncidenceAngle
					System.out.println("Processing PixelsIncidenceAngle..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("PixelsIncidenceAngleFeature");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test PixelsIncidenceAngleFeature data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					
					
					// media sull'agolo di incidenza (griglia uniforme x64)
					double [] f1 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.PixelsIncidenceAngleFeature )
							.extractFeature(image, new double[]{Grid.CentroidBased.getId(), numbers[0]}));
					
					// Insert nel CSV					
					csv = "PixelsIncidenceAngleFeature.csv";					
					dataFeature = "";
					for( int jk=0; jk <f1.length; jk++ ) {
						dataFeature += String.valueOf(f1[jk]) + ";";						
	            	}
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + f1.length + ";" + numTest  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;	
				
				case 2:
					// Daramola Centroid Incidence Angle
					System.out.println("Processing CentroidIncidenceAngle..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("CentroidIncidenceAngleFeature");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test CentroidIncidenceAngleFeature data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}					
					
					// Area sull'intera immagine (griglia uniforme x64)
					double [] f2 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.CentroidIncidenceAngleFeature )
							.extractFeature(image, new double[]{Grid.CentroidBased.getId(), numbers[0]}));
					
					// Insert nel CSV
					csv = "CentroidIncidenceAngleFeature.csv";					
					dataFeature = "";
					for( int jk=0; jk <f2.length; jk++ ) {
						dataFeature += String.valueOf(f2[jk]) + ";";						
	            	}
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + f2.length + ";" +  numTest  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;	
									
				case 3:
					// Tabessa RunLength Pdf 
					System.out.println("Processing RunLengthPdfFeature..." + newline);
					
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("RunLengthPdfFeature");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test RunLengthPdfFeature data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
										
					// DO NOT rotate
					double [] f3_0 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.RunLengthPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2]})); // 10 lunghezza minima
										
											
					// Insert nel CSV
					csv = "RunLengthPdfFeature.csv";					
					dataFeature = "";
					for( int jk=0; jk <f3_0.length; jk++ ) {
						dataFeature += String.valueOf(f3_0[jk]) + ";";						
	            	}
										
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + f3_0.length + ";" + numTest  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;						
										
				case 4:
					// Griffith GaussianFiltered ChainCode Pdf 
					System.out.println("Processing GaussianFilteredChainCodePdf..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("GaussianFilteredChainCodePdf");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test GaussianFilteredChainCodePdf data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
										
					//  
					ImagePlus GausFilimage = PreProcessingFactory.getPreProcess(PreprocessingType.MeanFilter).execute(image);
					//this.saveImage(GausFilimage, "Gaustemp.png");
					
					// NON CALCOLA!
					double [] f4 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.ChainCodePdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3]}));
					
					// Insert nel CSV
					csv = "GaussianFilteredChainCodePdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f4.length; jk++ ) {
						dataFeature += String.valueOf(f4[jk]) + ";";						
	            	}
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + f4.length + ";" +  numTest  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;	
										
				case 5:
					// Qatar Directions Pdf Feature
					System.out.println("Processing DirectionsPdf..." + newline);
					//  
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("DirectionsPdf");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test DirectionsPdf data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					//senza scheletonizzazione					
					double [] f5 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.DirectionsPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4], numbers[5]}));
					
					/*
					double [] f5 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.DirectionsPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), 1,1,7,3,1,8}));
					*/
					
					
					// Insert nel CSV
					csv = "DirectionsPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f5.length; jk++ ) {
						dataFeature += String.valueOf(f5[jk]) + ";";						
	            	}
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + f5.length + ";" +  numTest  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
					
				case 6:
					// Qatar Curvature Pdf Feature
					System.out.println("Processing CurvaturePdf..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("CurvaturePdf");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test CurvaturePdf data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					
					//  
					double [] f6 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.CurvaturePdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4]}));
					
					// Insert nel CSV
					csv = "CurvaturePdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f6.length; jk++ ) {
						dataFeature += String.valueOf(f6[jk]) + ";";						
	            	}
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + f6.length + ";" + numTest  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;	
					
				case 7:
					// Qatar TortuosityFeature ( TortuosityPdfFeature )
					System.out.println("Processing TortuosityPdf..." + newline);
					//  	
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("TortuosityPdf");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test TortuosityPdf data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					
					// calcolo con la funzione vecchia
					/* double [] f7 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.TortuosityFeature)
						.extractFeature(image, new double[]{Grid.Uniform.getId(), 1, 1}));
					*/ 
					// calcolo con quella nuova di ChiarelaG
					ArrayList<LongestSegment> f7 = LongestSegment.tortuosity(SckeFilImage, numbers[0], numbers[1]);
							
					// Insert nel CSV
					csv = "TortuosityPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk < 10 ; jk++ ) {
						dataFeature += String.valueOf(f7.get(jk).getLength()) + ";";						
	            	}
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + 10 + ";" +  numTest  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;					
					
				case 8:	
					// Qatar ChainCodePdfFeature 
					System.out.println("Processing ChainCodePdf..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("ChainCodePdf");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test ChainCodePdf data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					
					/*  
					double [] f8_0 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.ChainCodePdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), 1, 1, 1, 4})); //7 da cdificare
					*/
					
					double [] f8 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.ChainCodePdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3]})); // da parametrizare
					
					// Insert nel CSV
					csv = "ChainCodePdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f8.length; jk++ ) {
						dataFeature += String.valueOf(f8[jk]) + ";";						
	            	}
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + f8.length + ";" + numTest  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;			
					
				case 9:
					// Qatar EdgeTrackPdfFeature 
					System.out.println("Processing EdgeTrackPdf..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("EdgeTrackPdf");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test EdgeTrackPdf data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					//  
					double [] f9 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.EdgeTrackPdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3]}));
					
					// Insert nel CSV
					csv = "EdgeTrackPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f9.length; jk++ ) {
						dataFeature += String.valueOf(f9[jk]) + ";";						
	            	}
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					lineToWrite = authName + ";" + f9.length + ";" + numTest  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;							

				case 10:
					// Sabanci LocalBinaryPatternPdfFeature
					System.out.println("Processing LocalBinaryPatternPdf..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("LocalBinaryPatternPdf");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test LocalBinaryPatternPdf data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					// 
					// LBP via grid
					double [] f10_G = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.LocalBinaryPatternPdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4]}));
					
					// LBP via polar grid
					double [] f10_P = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.LocalBinaryPatternPdfFeature)
							.extractFeature(image, new double[]{Grid.Polar.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4]}));
					
					// Insert nel CSV
					csv = "LocalBinaryPatternPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f10_G.length; jk++ ) {
						dataFeature += String.valueOf(f10_G[jk]) + ";";						
	            	}
					for( int jk=0; jk <f10_P.length; jk++ ) {
						dataFeature += String.valueOf(f10_P[jk]) + ";";						
	            	}
					
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					int f10p = f10_P.length + f10_G.length;
					lineToWrite = authName + ";" + f10p + ";" +  numTest  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;	
	
				case 11:
					// Sabanci HistogramOfOrientedGradientsPdfFeature
					System.out.println("Processing HistogramOfOrientedGradientsPdf..." + newline);
					
					// Create an int array to accomodate the numbers.
					criteria = (JSONArray) savedData.get("HistogramOfOrientedGradientsPdf");					
					numbers = new int[criteria.size()];
					//System.out.print("*** test HistogramOfOrientedGradientsPdf data ***");
					for (int jk = 0; jk < criteria.size(); jk++) {
						// Extract numbers from JSON array.
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					// 
					// LBP via grid
					double [] f11_G = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.HistogramOfOrientedGradientsPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4], numbers[5]}));
					
					// LBP via polar grid
					double [] f11_P = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.HistogramOfOrientedGradientsPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Polar.getId(),  numbers[6], numbers[7], numbers[8], numbers[9], numbers[10], numbers[11]}));
					
					// Insert nel CSV
					csv = "HistogramOfOrientedGradientsPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f11_G.length; jk++ ) {
						dataFeature += String.valueOf(f11_G[jk]) + ";";						
	            	}
					for( int jk=0; jk <f11_P.length; jk++ ) {
						dataFeature += String.valueOf(f11_P[jk]) + ";";						
	            	}
					
					// create thing to write on CSV
					//lineToWrite = dataFeature + authName;
					int f11p = f11_G.length + f11_P.length;
					lineToWrite = authName + ";" + f11p + ";" +  numTest  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, true);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
					
					
				}		
			}
    	}
					
		savedData.put("progressiveTest", numTest);
		// aggiorno il file di configurazione JSON
		try {

			FileWriter file = new FileWriter("config.json");
			file.write(savedData.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	    /*CSVReader reader;
		try {
			reader = new CSVReader(new FileReader("yourfile.csv"));
		     String [] nextLine;
		     while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		        System.out.println(nextLine[0] + nextLine[1] + "etc...");
		     }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		
	    
		/*
		System.out.println(f1);
		System.out.println(Arrays.toString(f2));
		System.out.println(Arrays.toString(f3));
		*/
	}
	
	
	
	
	/*
	 * Common function to create a set of feature for the Verify image
	 * load the relative feartures form an array of values
	 */
	public void calcFeature(Boolean[] features, String authName){
		FileReader fr;
		
		System.out.println("*** Start Verify of Signature ***");
		//acquisizione
		image = loadImage(img);
		
		//acquisizione Dati dal JSON
		JSONObject savedData = null;
		try {
			savedData = loadJsonData();
		} 
		catch (IOException | ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//get progressive number
		int numVer = ( (Long) savedData.get("progressiveVerify")).intValue();	
		numVer++;
		
		
		//binarizzazione
		this.image.setProcessor(image.getProcessor().convertToByte(true));
		ImagePlus imageNoCrop = PreProcessingFactory.getPreProcess(PreprocessingType.Binarize).execute(image);
		image = PreProcessingFactory.getPreProcess(PreprocessingType.Crop).execute(imageNoCrop);
		//this.saveImage(image, "temp.png");
		ImagePlus SckeFilImage = PreProcessingFactory.getPreProcess(PreprocessingType.Skeletonize).execute(image);
			
		for( int i=0; i < features.length; i++ ) {
			// System.out.println("\n Searching Feature " + i + " on " + features[i]+" \n");
			if (features[i]){
				//var of file to write
				FileWriter mFileWriter;
				CSVWriter writer;
				String csv;				
				String dataFeature;
				String lineToWrite;
				String [] analyseddata;
				double angle;
				String newline = "";
				
				// json data reader
				int[] numbers;
				JSONArray criteria;
				
				
				switch (i) {
				case 0:
					// Daramola Area
					criteria = (JSONArray) savedData.get("AreaFeature");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
					double [] f0 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.AreaFeature)
							.extractFeature(image, new double[]{Grid.CentroidBased.getId(), numbers[0]}));
					csv = "temp/Verify_AreaFeature.csv";
					dataFeature = "";
					for( int jk=0; jk <f0.length; jk++ ) {
						dataFeature += String.valueOf(f0[jk]) + ";";
	            	}
					lineToWrite = authName + ";" + f0.length + ";" + numVer  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				
				case 1:
					// Daramola PixelsIncidenceAngle
					criteria = (JSONArray) savedData.get("PixelsIncidenceAngleFeature");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
					
					double [] f1 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.PixelsIncidenceAngleFeature )
							.extractFeature(image, new double[]{Grid.CentroidBased.getId(), numbers[0]}));				
					csv = "temp/Verify_PixelsIncidenceAngleFeature.csv";					
					dataFeature = "";
					for( int jk=0; jk <f1.length; jk++ ) {
						dataFeature += String.valueOf(f1[jk]) + ";";						
	            	}
					lineToWrite = authName + ";" + f1.length + ";" + numVer  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;	
				
				case 2:
					// Daramola Centroid Incidence Angle
					criteria = (JSONArray) savedData.get("CentroidIncidenceAngleFeature");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}					
					double [] f2 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.CentroidIncidenceAngleFeature )
							.extractFeature(image, new double[]{Grid.CentroidBased.getId(), numbers[0]}));
					csv = "temp/Verify_CentroidIncidenceAngleFeature.csv";					
					dataFeature = "";
					for( int jk=0; jk <f2.length; jk++ ) {
						dataFeature += String.valueOf(f2[jk]) + ";";						
	            	}
					lineToWrite = authName + ";" + f2.length + ";" +  numVer  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;	
									
				case 3:
					// Tabessa RunLength Pdf 
					criteria = (JSONArray) savedData.get("RunLengthPdfFeature");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
					double [] f3_0 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.RunLengthPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2]})); // 10 lunghezza minima
					csv = "temp/Verify_RunLengthPdfFeature.csv";					
					dataFeature = "";
					for( int jk=0; jk <f3_0.length; jk++ ) {
						dataFeature += String.valueOf(f3_0[jk]) + ";";						
	            	}
					lineToWrite = authName + ";" + f3_0.length + ";" + numVer  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;						
										
				case 4:
					// Griffith GaussianFiltered ChainCode Pdf 
					criteria = (JSONArray) savedData.get("GaussianFilteredChainCodePdf");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
										
					//  
					ImagePlus GausFilimage = PreProcessingFactory.getPreProcess(PreprocessingType.MeanFilter).execute(image);
					double [] f4 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.ChainCodePdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3]}));
					csv = "temp/Verify_GaussianFilteredChainCodePdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f4.length; jk++ ) {
						dataFeature += String.valueOf(f4[jk]) + ";";						
	            	}
					lineToWrite = authName + ";" + f4.length + ";" +  numVer  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;	
										
				case 5:
					// Qatar Directions Pdf Feature.
					criteria = (JSONArray) savedData.get("DirectionsPdf");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
									
					double [] f5 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.DirectionsPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4], numbers[5]}));
					
					csv = "temp/Verify_DirectionsPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f5.length; jk++ ) {
						dataFeature += String.valueOf(f5[jk]) + ";";						
	            	}
					lineToWrite = authName + ";" + f5.length + ";" +  numVer  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
					
					
					
				case 6:
					// Qatar Curvature Pdf Feature
					criteria = (JSONArray) savedData.get("CurvaturePdf");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
					
					double [] f6 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.CurvaturePdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4]}));
					csv = "temp/Verify_CurvaturePdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f6.length; jk++ ) {
						dataFeature += String.valueOf(f6[jk]) + ";";						
	            	}
					lineToWrite = authName + ";" + f6.length + ";" + numVer  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;	
					
				case 7:
					// Qatar TortuosityFeature ( TortuosityPdfFeature )
					criteria = (JSONArray) savedData.get("TortuosityPdf");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
					ArrayList<LongestSegment> f7 = LongestSegment.tortuosity(SckeFilImage, numbers[0], numbers[1]);

					csv = "temp/Verify_TortuosityPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk < 10 ; jk++ ) {
						dataFeature += String.valueOf(f7.get(jk).getLength()) + ";";						
	            	}
					lineToWrite = authName + ";" + 10 + ";" +  numVer  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;					
					
				case 8:	
					// Qatar ChainCodePdfFeature 
					criteria = (JSONArray) savedData.get("ChainCodePdf");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
					
					double [] f8 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.ChainCodePdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3]})); // da parametrizare
					
					csv = "temp/Verify_ChainCodePdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f8.length; jk++ ) {
						dataFeature += String.valueOf(f8[jk]) + ";";						
	            	}
					lineToWrite = authName + ";" + f8.length + ";" + numVer  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;			
					
				case 9:
					// Qatar EdgeTrackPdfFeature 
					criteria = (JSONArray) savedData.get("EdgeTrackPdf");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					    //System.out.print(" \n•" +  numbers[jk] + ";");
					}
					
					double [] f9 = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.EdgeTrackPdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3]}));
					
					csv = "temp/Verify_EdgeTrackPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f9.length; jk++ ) {
						dataFeature += String.valueOf(f9[jk]) + ";";						
	            	}
					lineToWrite = authName + ";" + f9.length + ";" + numVer  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;							

				case 10:
					// Sabanci LocalBinaryPatternPdfFeature
					criteria = (JSONArray) savedData.get("LocalBinaryPatternPdf");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
					
					double [] f10_G = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.LocalBinaryPatternPdfFeature)
							.extractFeature(image, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4]}));
					
					double [] f10_P = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.LocalBinaryPatternPdfFeature)
							.extractFeature(image, new double[]{Grid.Polar.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4]}));
					
					csv = "temp/Verify_LocalBinaryPatternPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f10_G.length; jk++ ) {
						dataFeature += String.valueOf(f10_G[jk]) + ";";						
	            	}
					for( int jk=0; jk <f10_P.length; jk++ ) {
						dataFeature += String.valueOf(f10_P[jk]) + ";";						
	            	}
					
					int f10p = f10_P.length + f10_G.length;
					lineToWrite = authName + ";" + f10p + ";" +  numVer  + ";" +  dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;	
	
				case 11:
					// Sabanci HistogramOfOrientedGradientsPdfFeature
					criteria = (JSONArray) savedData.get("HistogramOfOrientedGradientsPdf");					
					numbers = new int[criteria.size()];
					for (int jk = 0; jk < criteria.size(); jk++) {
					    numbers[jk] =  toIntExact((Long) criteria.get(jk));
					}
					
					// 
					// LBP via grid
					double [] f11_G = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.HistogramOfOrientedGradientsPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Uniform.getId(), numbers[0], numbers[1], numbers[2], numbers[3], numbers[4], numbers[5]}));
					
					// LBP via polar grid
					double [] f11_P = CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.HistogramOfOrientedGradientsPdfFeature)
							.extractFeature(SckeFilImage, new double[]{Grid.Polar.getId(),  numbers[6], numbers[7], numbers[8], numbers[9], numbers[10], numbers[11]}));
					
					csv = "temp/Verify_HistogramOfOrientedGradientsPdf.csv";					
					dataFeature = "";
					for( int jk=0; jk <f11_G.length; jk++ ) {
						dataFeature += String.valueOf(f11_G[jk]) + ";";						
	            	}
					for( int jk=0; jk <f11_P.length; jk++ ) {
						dataFeature += String.valueOf(f11_P[jk]) + ";";						
	            	}
					
					int f11p = f11_G.length + f11_P.length;
					lineToWrite = authName + ";" + f11p + ";" +  numVer  + ";" +   dataFeature ;
					analyseddata = lineToWrite.split(";");
					try {
						mFileWriter = new FileWriter(csv, false);
						writer = new CSVWriter(mFileWriter);
						writer.writeNext(analyseddata);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
					
					
				}		
			}
    	}
					
		savedData.put("progressiveVerify", numVer);
		// aggiorno il file di configurazione JSON
		try {

			FileWriter file = new FileWriter("config.json");
			file.write(savedData.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	

	
	
	
	
	
	
}
