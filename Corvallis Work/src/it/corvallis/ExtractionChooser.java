package it.corvallis;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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

public class ExtractionChooser extends JFrame {

	/**
	 * 
	 */
	public File fileImage;
	String pathFolder;
	public FileAnalizer analizer;
	public String authName;
	private JFileChooser fc;
	
	private static ExtractionChooser frameExtract;
	
	// Array of Selected Features
	private int n = 12;
	public Boolean arrayOfFeatures[]; // n features
	
	
	static private final String newline = "\n";
	
	 
	/**
	 * Create the frame.
	 * @param arrayOfFeatures2 
	 */
	public ExtractionChooser(Boolean[] arrayOfFeaturesProp) {
		
		frameExtract = this;
		
		this.arrayOfFeatures = arrayOfFeaturesProp;
		setResizable(false);
		
		//Load Button Definition
		String currentDirectoryPath =".";
		fc = new JFileChooser(currentDirectoryPath);
		analizer = new FileAnalizer();
		
				
		setTitle("Extraction");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 392);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		
		JTextField nameField = new JTextField();
		nameField.setBounds(135, 142, 157, 19);
		nameField.setCaretColor(Color.BLACK);
		nameField.setAlignmentY(Component.TOP_ALIGNMENT);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);

		ButtonGroup group = new ButtonGroup();
		
		panel_1.setLayout(null);
		
		
		panel_1.add(nameField);
		nameField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Author");
		lblNewLabel.setBounds(10, 127, 43, 19);
		panel_1.add(lblNewLabel);
		
		JButton loadButton = new JButton("Select Image");
		loadButton.setBounds(159, 16, 130, 64);
		panel_1.add(loadButton);		
		loadButton.setIcon(new ImageIcon("C:\\Users\\Francesco\\EclipseWorkspace\\modulo_base\\src\\Open16.gif"));
		
		JButton btnSelectFolder = new JButton("Select Folder");
		btnSelectFolder.setBounds(159, 16, 130, 64);
		panel_1.add(btnSelectFolder);		
		btnSelectFolder.setIcon(new ImageIcon("C:\\Users\\Francesco\\EclipseWorkspace\\modulo_base\\src\\Open16.gif"));
		
		JButton analizeButton = new JButton("Add Data");
		panel_1.add(analizeButton);
		analizeButton.setBounds(10, 213, 282, 63);
		
		
		
		
		JRadioButton rdbtnIcdarStyle = new JRadioButton("ICDAR Filename");
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Image Data Set");
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Single Image");
		rdbtnNewRadioButton_1.setBounds(20, 52, 119, 28);
		panel_1.add(rdbtnNewRadioButton_1);
		rdbtnNewRadioButton.setBounds(20, 28, 119, 28);
		panel_1.add(rdbtnNewRadioButton);
		rdbtnNewRadioButton.setSelected(true);
		rdbtnNewRadioButton_1.setSelected(false);
		
		
		
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( rdbtnNewRadioButton_1.isSelected() ){
					btnSelectFolder.setVisible(true);
					loadButton.setVisible(false);
				}
				
			}
		});
			
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if ( rdbtnNewRadioButton.isSelected() ){
					btnSelectFolder.setVisible(false);
					loadButton.setVisible(true);
				}
			}
		});
		
		
		
		

		
		// JRadioButton rdbtnIcdarStyle = new JRadioButton("ICDAR Filename");
		rdbtnIcdarStyle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ( rdbtnIcdarStyle.isSelected()){
					lblNewLabel.setVisible(false);
					nameField.setVisible(false);
				} else {
					lblNewLabel.setVisible(true);
					nameField.setVisible(true);
				}
				
			}
		});
		rdbtnIcdarStyle.setBounds(20, 168, 164, 21);
		panel_1.add(rdbtnIcdarStyle);
		
		JRadioButton rdbtnFilename = new JRadioButton("File Name");
		rdbtnFilename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblNewLabel.setVisible(true);
				nameField.setVisible(true);
			}
		});
		rdbtnFilename.setSelected(true);
		rdbtnFilename.setBounds(20, 142, 109, 21);
		
		
		ButtonGroup nameorfilename= new ButtonGroup();
		nameorfilename.add(rdbtnIcdarStyle);
		nameorfilename.add(rdbtnFilename);
		
	    
	    ButtonGroup fleorwhole = new ButtonGroup();
	    fleorwhole.add(rdbtnNewRadioButton);
	    fleorwhole.add(rdbtnNewRadioButton_1);
		
		
		panel_1.add(rdbtnFilename);
		
		JLabel lblImage = new JLabel("Image Selector");
		lblImage.setBounds(10, 11, 99, 19);
		panel_1.add(lblImage);
		
		JLabel labelselction = new JLabel("");
		labelselction.setBounds(10, 80, 282, 36);
		panel_1.add(labelselction);
		
		JButton btnNewButtonBack = new JButton("Back");
		btnNewButtonBack.setBounds(10, 282, 282, 60);
		panel_1.add(btnNewButtonBack);
		
		
		btnNewButtonBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//torno indietro
				frameExtract.setVisible(false);
				FeaturesChooser ic = new FeaturesChooser(1);
				ic.setVisible(true);
				ic.setLocationRelativeTo(null);				
			}
		});
		
		

	
		
		
		
		analizeButton.setVisible(false);
		
		
		
		
		
		analizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// handler of analyzing action process
				if (rdbtnNewRadioButton.isSelected()) {
					// single selection	
					analizeButton.setVisible(false);
					btnNewButtonBack.setText("... Extracting Data");
					
					System.out.println("Loading single image \n");			
					
					analizer.loadImge(fileImage, arrayOfFeatures, authName, rdbtnIcdarStyle.isSelected()); 
					// 1 è per i test 2 è il caricamento normale
					btnNewButtonBack.setText("Back");
					
					
				} else {
					// data set: all images
					System.out.println("Loading whole DataSet \n");
			
					try {
						analizer.loadDataSet(arrayOfFeatures, rdbtnIcdarStyle.isSelected(), pathFolder);
						// Carico l'intero data set da un pathname
					} catch (IOException | ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				

                
				// manage the results by CSV or print them: 
                System.out.println("Data Analyzed" + newline);
				
			}
		});
		
		
		
		// bottone che carica l'immagine
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        //Handler of open_image_file button action.	
				
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				
	            int returnVal = fc.showOpenDialog(ExtractionChooser.this);
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
	                nameField.setText(authName); 	               
	                  
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
		        int returnVal = fc.showOpenDialog(ExtractionChooser.this);
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
