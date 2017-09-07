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

public class ImageChooser extends JFrame {

	/**
	 * 
	 */
	public File fileImage;
	public FileAnalizer analizer;
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton loadButton;
	private JButton analizeButton;
	private JTextField nameField;
	private JTextArea logArea;
	private String authName;
	
	private JFileChooser fc;
	
	// Array of Selected Features
	private int n = 12;
	private Boolean arrayOfFeatures[] = new Boolean[n]; // n features
	private Boolean arrayOfAlg[] = new Boolean[4]; // 4 algorithms
	

	
	static private final String newline = "\n";
	private JRadioButton rdbtnIcdarStyle;
	 
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageChooser frame = new ImageChooser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ImageChooser() {
		setResizable(false);
		
		for (int i = 0; i < (arrayOfFeatures.length) ;i++){
			arrayOfFeatures[i] = false;
		}
		for (int i = 0; i < (arrayOfAlg.length) ;i++){
			arrayOfAlg[i] = false;
		}
		//arrayOfAlg[0] = true;
		
		setTitle("Image Loader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 923, 467);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		ButtonGroup group = new ButtonGroup();
		
		
		nameField = new JTextField();
		nameField.setBounds(371, 84, 202, 19);
		nameField.setCaretColor(Color.BLACK);
		nameField.setAlignmentY(Component.TOP_ALIGNMENT);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);

		//Load Button Definition
		String currentDirectoryPath =".";
		fc = new JFileChooser(currentDirectoryPath);
		analizer = new FileAnalizer();
		panel_1.setLayout(null);
		
		logArea = new JTextArea();
		logArea.setBounds(596, 3, 301, 425);
		logArea.setDisabledTextColor(Color.BLACK);
		//logArea.setEnabled(false);
		//logArea.setEditable(false);
		panel_1.add(logArea);
		
		
	    JScrollPane scroll = new JScrollPane(logArea);
	    scroll.setBounds(596, 3, 301, 425);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

	    //Add Textarea in to middle panel
	    panel_1.add(scroll);
		
		
		panel_1.add(nameField);
		nameField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Author");
		lblNewLabel.setBounds(318, 85, 43, 19);
		panel_1.add(lblNewLabel);
		
		JLabel lblFeaturesSelector = new JLabel("FEATURE LIST");
		lblFeaturesSelector.setHorizontalAlignment(SwingConstants.CENTER);
		lblFeaturesSelector.setBounds(0, 3, 308, 14);
		panel_1.add(lblFeaturesSelector);
		
		loadButton = new JButton("Load");
		loadButton.setBounds(308, 29, 278, 29);
		panel_1.add(loadButton);
		
		loadButton.setIcon(new ImageIcon("C:\\Users\\Francesco\\EclipseWorkspace\\modulo_base\\src\\Open16.gif"));
		
		Panel panelDaramola = new Panel();
		panelDaramola.setForeground(Color.RED);
		panelDaramola.setBounds(6, 20, 296, 84);
		panel_1.add(panelDaramola);
		panelDaramola.setLayout(null);
		
		JCheckBox ck01 = new JCheckBox("Area ");
		ck01.setBounds(10, 20, 89, 20);
		panelDaramola.add(ck01);
		
		JCheckBox ck02 = new JCheckBox("Pixels Incidence Angle ");
		ck02.setBounds(10, 40, 260, 20);
		panelDaramola.add(ck02);
		
		JCheckBox ck03 = new JCheckBox("Centroid Incidence Angle ");
		ck03.setBounds(10, 60, 280, 20);
		panelDaramola.add(ck03);
		
		JLabel lblDaramolaSystem = new JLabel("Daramola System");
		lblDaramolaSystem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (ck01.isSelected() && ck02.isSelected() && ck03.isSelected()){
					ck01.setSelected(false);
					ck02.setSelected(false);
					ck03.setSelected(false);
				} else {
					ck01.setSelected(true);
					ck02.setSelected(true);
					ck03.setSelected(true);
				}
				
			}
		});
		lblDaramolaSystem.setBounds(10, 5, 168, 14);
		panelDaramola.add(lblDaramolaSystem);
		lblDaramolaSystem.setBackground(UIManager.getColor("Button.background"));
		
		Panel panelTabessa = new Panel();
		panelTabessa.setBounds(6, 110, 296, 50);
		panel_1.add(panelTabessa);
		panelTabessa.setLayout(null);
		
		JCheckBox ck04 = new JCheckBox("Run Length");
		ck04.setBounds(10, 20, 264, 20);
		panelTabessa.add(ck04);
		
		JLabel lblTabessaSystem = new JLabel("Tabessa System");
		lblTabessaSystem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (ck04.isSelected()){
					ck04.setSelected(false);
				} else {
					ck04.setSelected(true);
				}
			}
		});
		lblTabessaSystem.setBounds(10, 5, 203, 14);
		panelTabessa.add(lblTabessaSystem);
		
		Panel panelGriffith = new Panel();
		panelGriffith.setBounds(6, 166, 296, 50);
		panel_1.add(panelGriffith);
		panelGriffith.setLayout(null);
		
		JCheckBox ck05 = new JCheckBox("Chain Code (Gaussian Filtered)  ");
		ck05.setBounds(10, 20, 264, 20);
		panelGriffith.add(ck05);
		
		JLabel lblGriffithSystem = new JLabel("Griffith System");
		lblGriffithSystem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (ck05.isSelected()){
					ck05.setSelected(false);
				} else {
					ck05.setSelected(true);
				}
			}
		});
		lblGriffithSystem.setBounds(10, 5, 190, 14);
		panelGriffith.add(lblGriffithSystem);
		
		Panel panelQatar = new Panel();
		panelQatar.setBounds(6, 222, 296, 122);
		panel_1.add(panelQatar);
		panelQatar.setLayout(null);
		
		JCheckBox ck06 = new JCheckBox("Directions");
		ck06.setBounds(10, 20, 264, 20);
		panelQatar.add(ck06);
		
		JCheckBox ck07 = new JCheckBox("Curvature");
		ck07.setBounds(10, 40, 264, 20);
		panelQatar.add(ck07);
		
		JCheckBox ck08 = new JCheckBox("Tortuosity");
		ck08.setBounds(10, 60, 264, 20);
		panelQatar.add(ck08);
		
		JCheckBox ck09 = new JCheckBox("Chain Code");
		ck09.setBounds(10, 80, 264, 20);
		panelQatar.add(ck09);
		
		JCheckBox ck10 = new JCheckBox("Edge Track");
		ck10.setBounds(10, 100, 264, 20);
		panelQatar.add(ck10);
		
		JLabel lblQuataSystem = new JLabel("Quatar System");
		lblQuataSystem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (ck06.isSelected() && ck07.isSelected() && ck08.isSelected() && ck09.isSelected() && ck10.isSelected()){
					ck06.setSelected(false);
					ck07.setSelected(false);
					ck08.setSelected(false);
					ck09.setSelected(false);
					ck10.setSelected(false);
				} else {
					ck06.setSelected(true);
					ck07.setSelected(true);
					ck08.setSelected(true);
					ck09.setSelected(true);
					ck10.setSelected(true);
				}
			}
		});
		lblQuataSystem.setBounds(10, 5, 223, 14);
		panelQatar.add(lblQuataSystem);
		
		Panel panelSabanci = new Panel();
		panelSabanci.setBounds(6, 350, 296, 66);
		panel_1.add(panelSabanci);
		panelSabanci.setLayout(null);
		
		JCheckBox ck11 = new JCheckBox("LBP");
		ck11.setBounds(10, 20, 264, 20);
		panelSabanci.add(ck11);
		
		JCheckBox ck12 = new JCheckBox(" Histogram of Oriented Gradients");
		ck12.setBounds(10, 40, 264, 20);
		panelSabanci.add(ck12);
		
		JLabel labelSabanci = new JLabel("Sabanci System");
		labelSabanci.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (ck11.isSelected() && ck12.isSelected()){
					ck11.setSelected(false);
					ck12.setSelected(false);
				} else {
					ck11.setSelected(true);
					ck12.setSelected(true);
				}
			}
		});
		labelSabanci.setBounds(10, 5, 152, 14);
		panelSabanci.add(labelSabanci);
		
		JLabel lblAlgorithmsList = new JLabel("ALGORITHMS LIST");
		lblAlgorithmsList.setHorizontalAlignment(SwingConstants.CENTER);
		lblAlgorithmsList.setBounds(308, 217, 278, 14);
		panel_1.add(lblAlgorithmsList);
		
		Panel panel_CLASS = new Panel();
		panel_CLASS.setBounds(308, 237, 279, 107);
		panel_1.add(panel_CLASS);
		panel_CLASS.setLayout(null);
		
		JCheckBox algobox1 = new JCheckBox("K-NearestNeighbor");
		algobox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arrayOfAlg[2] = algobox1.isSelected();
			}
		});
		algobox1.setBounds(6, 57, 267, 23);
		panel_CLASS.add(algobox1);
		
		JCheckBox chckbxSupportVectorMachine = new JCheckBox("Support Vector Machine");
		chckbxSupportVectorMachine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arrayOfAlg[1] = chckbxSupportVectorMachine.isSelected();
			}
		});
		chckbxSupportVectorMachine.setBounds(6, 33, 267, 23);
		panel_CLASS.add(chckbxSupportVectorMachine);
		
		JCheckBox chckbxRandomForests = new JCheckBox("Random Forests ");
		chckbxRandomForests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arrayOfAlg[3] = chckbxRandomForests.isSelected();
			}
		});
		chckbxRandomForests.setBounds(6, 83, 267, 23);
		panel_CLASS.add(chckbxRandomForests);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Algebraic Distance");
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				arrayOfAlg[0] = chckbxNewCheckBox.isSelected();
			}
		});
		chckbxNewCheckBox.setBounds(6, 7, 138, 23);
		panel_CLASS.add(chckbxNewCheckBox);
		
		analizeButton = new JButton("Add Data");
		panel_1.add(analizeButton);
		analizeButton.setBounds(308, 140, 278, 34);
		
		JButton btnNewButton = new JButton("Save the Models");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// handler of secuting Model
				int token = 999;
				// Array of features
                int k = 0;
				for( int i=0; i<panelDaramola.getComponentCount(); i++ ) {
					if ((panelDaramola.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelDaramola.getComponent(i)).isSelected();
            	}				
				k = 3;
				for( int i=0; i<panelTabessa.getComponentCount(); i++ ) {
					if ((panelTabessa.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelTabessa.getComponent( i )).isSelected();
            	}
				k = 4;
				for( int i=0; i<panelGriffith.getComponentCount(); i++ ) {
					if ((panelGriffith.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelGriffith.getComponent( i )).isSelected();
            	}				
				k = 5;
				for( int i=0; i<panelQatar.getComponentCount(); i++ ) {
					if ((panelQatar.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelQatar.getComponent( i )).isSelected();            	  
            	}				
				k = 10;
				for( int i=0; i<panelSabanci.getComponentCount(); i++ ) {
					if ((panelSabanci.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelSabanci.getComponent( i )).isSelected();
            	}				
				for (int i = 0; i < (arrayOfFeatures.length) ;i++){
					if (arrayOfFeatures[i]) token = 0;
				}
				if (token == 999){
					System.out.println("no feature list selected" + newline);
					logArea.append("... select (at least) a feature" + newline);
				} else {
					// start verify
					logArea.append("Start Storing data Models..." + newline);					
					
					authName = nameField.getText();
					
					if (authName == ""){
						
					} else {
						// all selected	
						/* arrayOfAlg[0] = 
						arrayOfAlg[1] = 
						arrayOfAlg[2] = 
						arrayOfAlg[3] = 
						*/
						logArea.append("Training Selected Models for author" + authName + newline);
		                
						analizer.saveTheModel(arrayOfFeatures, arrayOfAlg, authName);
						// manage the results by CSV or print them: 
		                System.out.println("... end." + newline);
		                logArea.append("Data of "+authName+" stored" + newline);
		                
					}					
				}
				
			}
		});
		btnNewButton.setBounds(308, 352, 278, 29);
		panel_1.add(btnNewButton);
		
		rdbtnIcdarStyle = new JRadioButton("ICDAR Name");
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Fake Signture");
		chckbxNewCheckBox_1.setBounds(396, 181, 120, 23);
		panel_1.add(chckbxNewCheckBox_1);
		
		JButton btnNewButton_1 = new JButton("Predict");
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Whole Data Set");
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				analizeButton.setVisible(true);
				loadButton.setVisible(false);
				lblNewLabel.setVisible(false);
				nameField.setVisible(false);
				//btnNewButton.setVisible(false);
			}
		});
		
		
		rdbtnNewRadioButton_1.setBounds(477, 6, 120, 21);
		panel_1.add(rdbtnNewRadioButton_1);
		
		
		
		
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Start Verify!
				int token = 999;
				// Array of features
                int k = 0;
				for( int i=0; i<panelDaramola.getComponentCount(); i++ ) {
					if ((panelDaramola.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelDaramola.getComponent(i)).isSelected();
            	}				
				k = 3;
				for( int i=0; i<panelTabessa.getComponentCount(); i++ ) {
					if ((panelTabessa.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelTabessa.getComponent( i )).isSelected();
            	}
				k = 4;
				for( int i=0; i<panelGriffith.getComponentCount(); i++ ) {
					if ((panelGriffith.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelGriffith.getComponent( i )).isSelected();
            	}				
				k = 5;
				for( int i=0; i<panelQatar.getComponentCount(); i++ ) {
					if ((panelQatar.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelQatar.getComponent( i )).isSelected();            	  
            	}				
				k = 10;
				for( int i=0; i<panelSabanci.getComponentCount(); i++ ) {
					if ((panelSabanci.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelSabanci.getComponent( i )).isSelected();
            	}				
				for (int i = 0; i < (arrayOfFeatures.length) ;i++){
					if (arrayOfFeatures[i]) token = 0;
				}
				if (token == 999){
					System.out.println("no feature list selected" + newline);
					logArea.append("... select (at least) a feature" + newline);
				} else {
					// start verify
					logArea.append("Start Storing data Models..." + newline);					
					
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
						authName = nameField.getText();					
						if (authName == ""){
							logArea.append("Please insert Author Name"+ newline);
							
						} else {
							logArea.append("Verifing image for Author: " + authName + newline);
			                Boolean controlloFake = chckbxNewCheckBox_1.isSelected();;
			                
							analizer.evaluateModel(fileImage, arrayOfFeatures, arrayOfAlg, authName, controlloFake);
							// manage the results by CSV or print them: 
			                System.out.println("... end of Verify." + newline);
			                logArea.append("Data of "+authName+" verified" + newline);
			                for (int f=0; f < analizer.returnText.length; f++){
			                	for (int g=0; g < analizer.returnText[f].length; g++){
			                		if (!analizer.returnText[f][g].equals("")){
			                			logArea.append(analizer.returnText[f][g] + newline);
			                		}
			                	}
			                	
			                }
						}
					}
				}
				
			}
				
		});
		btnNewButton_1.setBounds(308, 388, 278, 29);
		panel_1.add(btnNewButton_1);
		
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Single Image");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				analizeButton.setVisible(false);
				loadButton.setVisible(true);
				btnNewButton.setVisible(true);
				if ( rdbtnIcdarStyle.isSelected() ){
					lblNewLabel.setVisible(false);
					nameField.setVisible(false);
				} else {
					lblNewLabel.setVisible(true);
					nameField.setVisible(true);					
				}
			}
		});
		
		rdbtnNewRadioButton.setSelected(true);
		rdbtnNewRadioButton.setBounds(318, 6, 127, 22);
		panel_1.add(rdbtnNewRadioButton);
		
		

		
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
		rdbtnIcdarStyle.setBounds(477, 110, 109, 23);
		panel_1.add(rdbtnIcdarStyle);
		
		JRadioButton rdbtnFilename = new JRadioButton("File Name");
		rdbtnFilename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblNewLabel.setVisible(true);
				nameField.setVisible(true);
			}
		});
		rdbtnFilename.setSelected(true);
		rdbtnFilename.setBounds(318, 110, 109, 23);
		
		
		ButtonGroup nameorfilename= new ButtonGroup();
		nameorfilename.add(rdbtnIcdarStyle);
		nameorfilename.add(rdbtnFilename);
		
	    
	    ButtonGroup fleorwhole = new ButtonGroup();
	    fleorwhole.add(rdbtnNewRadioButton);
	    fleorwhole.add(rdbtnNewRadioButton_1);
		
		
		panel_1.add(rdbtnFilename);
		
		
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(371, 63, 202, 19);
		panel_1.add(lblNewLabel_1);
		

		analizeButton.setVisible(false);
		
		
		
		
		
		analizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// handler of analyzing action process
				//logArea.append("Algorimth Selected: " + Integer.parseInt(group.getSelection().getActionCommand()) + newline);
                logArea.append("Analyzing Data for: " + authName + "." + newline);
               
                // Array of features
                int k = 0;
				for( int i=0; i<panelDaramola.getComponentCount(); i++ ) {
					if ((panelDaramola.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelDaramola.getComponent(i)).isSelected();
            	}
				
				k = 3;
				for( int i=0; i<panelTabessa.getComponentCount(); i++ ) {
					if ((panelTabessa.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelTabessa.getComponent( i )).isSelected();
            	}
				k = 4;
				for( int i=0; i<panelGriffith.getComponentCount(); i++ ) {
					if ((panelGriffith.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelGriffith.getComponent( i )).isSelected();
            	}
				
				k = 5;
				for( int i=0; i<panelQatar.getComponentCount(); i++ ) {
					if ((panelQatar.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelQatar.getComponent( i )).isSelected();            	  
            	}
				
				k = 10;
				for( int i=0; i<panelSabanci.getComponentCount(); i++ ) {
					if ((panelSabanci.getComponent(i)).getClass() != JLabel.class)
					arrayOfFeatures[i + k] = ((JCheckBox) panelSabanci.getComponent( i )).isSelected();
            	}
				
				/*
				for (int i = 0; i < (arrayOfFeatures.length) ;i++){
					logArea.append(i + "° element= " + arrayOfFeatures[i] + "" + newline);
				}
				*/
            	
                // calculating the  Features loading the img selected
                // this bind (fileImg, arrayOfFeature, AuthName)                
                // old control Integer.parseInt(group.getSelection().getActionCommand())
				authName = nameField.getText();
				
				
				// make selection multiple/single analisys.
				/*
				 * 		File dir = new File("dir");
						String[] extensions = new String[] { "txt", "jsp" };
						System.out.println("Getting all .txt and .jsp files in " + dir.getCanonicalPath()
								+ " including those in subdirectories");
						List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
						for (File file : files) {
							System.out.println("file: " + file.getCanonicalPath());
						}
				 */
				if (rdbtnNewRadioButton.isSelected()) {
					// single selection
					//TODO il chck se è dell'ICDAR					
					analizer.loadImge(fileImage, arrayOfFeatures, authName, 2, rdbtnIcdarStyle.isSelected()); 
					// 1 è per i test 2 è il caricamento normale
				} else {
					// Tutti i dati
					System.out.println("Loading whole DataSet \n");
					logArea.append("Loading whole DataSet in DB path" + newline);
					try {
						analizer.loadDataSet(arrayOfFeatures, rdbtnIcdarStyle.isSelected());
						// Carico l'intero data set da un pathname
					} catch (IOException | ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					logArea.append("Data Set succesfull Loaded." + newline);
				}
				
                
                
                
                
                
				// manage the results by CSV or print them: 
                System.out.println("Data Analyzed" + newline);
				
			}
		});
		
		// bottone che carica l'immagine
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        //Handler of open_image_file button action.	
				
	            int returnVal = fc.showOpenDialog(ImageChooser.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                fileImage = fc.getSelectedFile();
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
	                logArea.append("Loaded: " + fileImage.getName() + "." + newline);
	                lblNewLabel_1.setText(fileImage.getName());
	                logArea.append("Author (Suggested): " + authName + newline);	
	                analizeButton.setVisible(true);
	            } else {
	            	// Error to load the file
	            	logArea.append("Open command cancelled by user." + newline);
	            }
	            logArea.setCaretPosition(logArea.getDocument().getLength());		        
			}
		});		
	}
}
