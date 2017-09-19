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

public class FeaturesChooser extends JFrame {

	/**
	 * 
	 */
	
	public int state;
	
	private static FeaturesChooser Featureframe;
	
	public File fileImage;
	public FileAnalizer analizer;
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton loadButton;
	private String authName;
	
	private JFileChooser fc;
	
	// Array of Selected Features
	private int n = 12;
	public Boolean arrayOfFeatures[] = new Boolean[n]; // n features

	static private final String newline = "\n";
	 

	/**
	 * Create the frame.
	 */
	public FeaturesChooser(int state) {
		
		Featureframe = this;
		 
		setResizable(false);
		
		this.state = state;
		
		for (int i = 0; i < (arrayOfFeatures.length) ;i++){
			arrayOfFeatures[i] = false;
		}
		//arrayOfAlg[0] = true;
		
		setTitle("Image Loader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 329, 606);
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
		
		JLabel lblFeaturesSelector = new JLabel("FEATURES LIST");
		lblFeaturesSelector.setHorizontalAlignment(SwingConstants.CENTER);
		lblFeaturesSelector.setBounds(0, 3, 308, 14);
		panel_1.add(lblFeaturesSelector);
		
		loadButton = new JButton("Load");
		loadButton.setBounds(0, 422, 312, 72);
		panel_1.add(loadButton);
		
    	switch (this.state) {
    		case 1:
    			loadButton.setText("Extraction");
    			loadButton.setIcon(new ImageIcon("C:\\Users\\Francesco\\EclipseWorkspace\\modulo_base\\src\\Open16.gif"));
    			break;
        
    		case 2:
    			loadButton.setText("Training");
    			break;
	    	
			case 3:
				loadButton.setText("Verify");
				break;	
		}	
    	//loadButton.setIcon(new ImageIcon("C:\\Users\\Francesco\\EclipseWorkspace\\modulo_base\\src\\Open16.gif"));
		
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
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(371, 63, 202, 19);
		panel_1.add(lblNewLabel_1);
		
		JButton btnNewButtonBack = new JButton("Back");
		btnNewButtonBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Featureframe.setVisible(false);
				
				RouteSelector ja = new RouteSelector();
				ja.setVisible(true);
				ja.setLocationRelativeTo(null);
				
				
			}
		});
		btnNewButtonBack.setBounds(0, 505, 312, 62);
		panel_1.add(btnNewButtonBack);
		
		// bottone che carica l'immagine
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        //Handler of open_image_file button action.	
				// Array of features
                int k = 0;
                int token = 999;
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
				
				if (token == 0){
			    	switch (state) {
			    		case 1:
			    			//loadButton.setText("Extraction");
			    			Featureframe.setVisible(false);
			    			ExtractionChooser ES = new ExtractionChooser(arrayOfFeatures);
							ES.setVisible(true);
							ES.setLocationRelativeTo(null);
			    			
			    			break;
			        
			    		case 2:
			    			//loadButton.setText("Training");
			    			Featureframe.setVisible(false);
			    			TrainingChooser TS = new TrainingChooser(arrayOfFeatures);
							TS.setVisible(true);
							TS.setLocationRelativeTo(null);
			    			
			    			break;
				    	
						case 3:
							//loadButton.setText("Verify");
							Featureframe.setVisible(false);
							PredictionChooser PC = new PredictionChooser(arrayOfFeatures);
							PC.setVisible(true);
							PC.setLocationRelativeTo(null);
							
							break;	
			    	}
				} else {
					// nulla
					
				}
    
			}
		});		
	}
}
