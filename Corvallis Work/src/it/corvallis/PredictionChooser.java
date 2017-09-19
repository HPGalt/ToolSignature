package it.corvallis;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

//import javax.swing.SwingConstants;
import javax.swing.ButtonGroup;
//import java.awt.FlowLayout;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import java.awt.Component;
import java.awt.Color;


import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Panel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;

import org.json.simple.parser.ParseException;

import javax.swing.event.ChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PredictionChooser extends JFrame {

	/**
	 * 
	 */
	public File fileImage;
	public FileAnalizer analizer;
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String authName;
	
	private JFileChooser fc;
	


	String pathFolder;
		
	// Array of Selected Features
	private int n = 12;
	private Boolean arrayOfFeatures[] = new Boolean[n]; // n features
	public Boolean arrayOfAlg[] = new Boolean[4]; // 4 algorithms
	
	private static PredictionChooser framePred;
	
	static private final String newline = "\n";
	private JTextField namefield;
	 

	/**
	 * Create the frame.
	 * @param arrayOfFeatures2 
	 */
	public PredictionChooser(Boolean[] arrayOfFeaturesProp) {
		setResizable(false);

		framePred = this;
		
		this.arrayOfFeatures = arrayOfFeaturesProp;
		
		for (int i = 0; i < (arrayOfAlg.length) ;i++){
			arrayOfAlg[i] = false;
		}
		
		setTitle("Image Loader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 317, 569);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		ButtonGroup group = new ButtonGroup();
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);

		//Load Button Definition
		String currentDirectoryPath =".";
		fc = new JFileChooser(currentDirectoryPath);
		analizer = new FileAnalizer();
		panel_1.setLayout(null);
		
	    JScrollPane scroll = new JScrollPane();
	    scroll.setBounds(10, 393, 282, 64);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

	    //Add Textarea in to middle panel
	    panel_1.add(scroll);
	    
	    JTextArea logarea = new JTextArea();
	    logarea.setDisabledTextColor(Color.BLACK);
	    scroll.setViewportView(logarea);
		
		JLabel lblAlgorithmsList = new JLabel("ALGORITHMS LIST");
		lblAlgorithmsList.setHorizontalAlignment(SwingConstants.CENTER);
		lblAlgorithmsList.setBounds(10, 152, 282, 32);
		panel_1.add(lblAlgorithmsList);
		
		Panel panel_CLASS = new Panel();
		panel_CLASS.setBounds(10, 190, 279, 107);
		panel_1.add(panel_CLASS);
		panel_CLASS.setLayout(null);
		
		JCheckBox algobox1 = new JCheckBox("K-Nearest Neighbor");
		algobox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arrayOfAlg[2] = algobox1.isSelected();
			}
		});
		algobox1.setBounds(6, 57, 155, 23);
		panel_CLASS.add(algobox1);
		
		JCheckBox chckbxSupportVectorMachine = new JCheckBox("Support Vector Machine");
		chckbxSupportVectorMachine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arrayOfAlg[1] = chckbxSupportVectorMachine.isSelected();
			}
		});
		chckbxSupportVectorMachine.setBounds(6, 33, 155, 23);
		panel_CLASS.add(chckbxSupportVectorMachine);
		
		JCheckBox chckbxRandomForests = new JCheckBox("Random Forests ");
		chckbxRandomForests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arrayOfAlg[3] = chckbxRandomForests.isSelected();
			}
		});
		chckbxRandomForests.setBounds(6, 83, 155, 23);
		panel_CLASS.add(chckbxRandomForests);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Algebraic Distance");
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				arrayOfAlg[0] = chckbxNewCheckBox.isSelected();
			}
		});
		chckbxNewCheckBox.setBounds(6, 7, 155, 23);
		panel_CLASS.add(chckbxNewCheckBox);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Fake Signture");
		chckbxNewCheckBox_1.setBounds(10, 363, 120, 23);
		panel_1.add(chckbxNewCheckBox_1);
		
		JButton analizeButton = new JButton("Predict");
		
		
		JButton loadButton = new JButton("Select Image");
		loadButton.setBounds(159, 16, 130, 64);
		panel_1.add(loadButton);		
		loadButton.setIcon(new ImageIcon("C:\\Users\\Francesco\\EclipseWorkspace\\modulo_base\\src\\Open16.gif"));
		
		JButton btnSelectFolder = new JButton("Select Folder");
		btnSelectFolder.setBounds(159, 16, 130, 64);
		panel_1.add(btnSelectFolder);		
		btnSelectFolder.setIcon(new ImageIcon("C:\\Users\\Francesco\\EclipseWorkspace\\modulo_base\\src\\Open16.gif"));
		
		
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Single Image");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( rdbtnNewRadioButton.isSelected() ){
					btnSelectFolder.setVisible(false);
					loadButton.setVisible(true);
				}
			}
		});
		rdbtnNewRadioButton.setSelected(true);
		rdbtnNewRadioButton.setBounds(20, 28, 119, 28);
		panel_1.add(rdbtnNewRadioButton);
		
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Image Data Set");
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( rdbtnNewRadioButton_1.isSelected() ){
					btnSelectFolder.setVisible(true);
					loadButton.setVisible(false);
				}
				
			}
		});
		rdbtnNewRadioButton_1.setSelected(false);
		rdbtnNewRadioButton_1.setBounds(20, 52, 119, 28);
		panel_1.add(rdbtnNewRadioButton_1);
		
		
		rdbtnNewRadioButton.setSelected(true);
		rdbtnNewRadioButton_1.setSelected(false);
		
		ButtonGroup fleorwhole = new ButtonGroup();
	    fleorwhole.add(rdbtnNewRadioButton);
	    fleorwhole.add(rdbtnNewRadioButton_1);
	
		analizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Start Verify!
				int token = 999;
				// Array of features
                
				if (token == 0){
					System.out.println("no feature list selected" + newline);
					logarea.append("... select (at least) a feature" + newline);
				} else {
					// start verify
					logarea.append("Start Storing data Models..." + newline);					
					
					if(rdbtnNewRadioButton_1.isSelected()){
						//allora faccio il run su TUTTO di TUTTO
						try {
							analizer.evaluateModelOnDataSet();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else {
						authName = namefield.getText();					
						if (authName == ""){
							logarea.append("Please insert Author Name"+ newline);
							
						} else {
							logarea.append("Verifing image for Author: " + authName + newline);
			                Boolean controlloFake = chckbxNewCheckBox_1.isSelected();;
			                
							analizer.evaluateModel(fileImage, arrayOfFeatures, arrayOfAlg, authName, controlloFake);
							// manage the results by CSV or print them: 
			                System.out.println("... end of Verify." + newline);
			                logarea.append("Data of "+authName+" verified" + newline);
			                for (int f=0; f < analizer.returnText.length; f++){
			                	for (int g=0; g < analizer.returnText[f].length; g++){
			                		if (!analizer.returnText[f][g].equals("")){
			                			logarea.append(analizer.returnText[f][g] + newline);
			                		}
			                	}
			                	
			                }
						}
					}
				}
				
			}
				
		});
		analizeButton.setBounds(10, 303, 282, 53);
		panel_1.add(analizeButton);
		
		
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(371, 63, 202, 19);
		panel_1.add(lblNewLabel_1);
		
	
		
		
		

		

		
		JLabel labelselction = new JLabel("");
		labelselction.setBounds(10, 80, 282, 36);
		panel_1.add(labelselction);
		
		JLabel label_1 = new JLabel("Image Selector");
		label_1.setBounds(10, 11, 99, 19);
		panel_1.add(label_1);
		
		JButton button_2 = new JButton("Back");
		button_2.setBounds(10, 468, 282, 60);
		panel_1.add(button_2);
		
		namefield = new JTextField();
		namefield.setColumns(10);
		namefield.setCaretColor(Color.BLACK);
		namefield.setAlignmentY(0.0f);
		namefield.setBounds(95, 127, 192, 19);
		panel_1.add(namefield);
		
		JLabel label_2 = new JLabel("Author");
		label_2.setBounds(10, 127, 56, 19);
		panel_1.add(label_2);
		
		
		
		
		
		// bottone che carica l'immagine
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        //Handler of open_image_file button action.	
				
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				
	            int returnVal = fc.showOpenDialog(PredictionChooser.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                fileImage = fc.getSelectedFile();
	                pathFolder = null;
	                //This is where a real application would open the file.
	               
	                // File loaded	               
	                //Add the name of author on string
	                
                	//parse the name on ICDAR way
	                authName = fileImage.getName().replace("NISDCC-", "");
	                
                	String[] split = authName.split("_");
                	authName= split[0].substring(split[0].length()-3);
	                		                
                	authName = authName.replace("NISDCC-", "");
                	authName = authName.replace("_6g.PNG", "");
                	authName = authName.replace(".png", "");
                	authName = authName.replace(".PNG", "");
                	authName = authName.replace(".jpg", "");
                	authName = authName.replace(".JPG", "");
	                
	                authName = authName.replace("_6g.PNG", "");
	                authName = authName.replace(".png", "");
	                authName = authName.replace(".jpg", "");
	                authName = authName.replace(".PNG", "");
	                authName = authName.replace(".JPG", "");
	                namefield.setText(authName); 	               
	                  
	                labelselction.setText("File Selected:" + fileImage.getName());
	                analizeButton.setVisible(true);
	            } else {
	            	// Error to load the file
	            }
		        
			}
		});		
	
		
		btnSelectFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        // disable the "All files" option.
		        fc.setAcceptAllFileFilterUsed(false);
		        int returnVal = fc.showOpenDialog(PredictionChooser.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		        	pathFolder = fc.getSelectedFile().getPath();		        			        	
	            	fileImage = null;
	                //pathFolder = fc.getCurrentDirectory(); 	                
	                labelselction.setText("Folder: " + pathFolder);
	                analizeButton.setVisible(true);
	            }			    
			}
		});
	
	
		
		
	}
}
