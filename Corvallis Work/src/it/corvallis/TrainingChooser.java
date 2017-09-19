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

public class TrainingChooser extends JFrame {

	/**
	 * 
	 */
	public File fileImage;
	public FileAnalizer analizer;
	
	private static TrainingChooser frameTraining;
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String authName;
	
	private JFileChooser fc;
	
	// Array of Selected Features
	private int n = 12;
	private Boolean arrayOfFeatures[] = new Boolean[n]; // n features
	private Boolean arrayOfAlg[] = new Boolean[4]; // 4 algorithms
	

	
	static private final String newline = "\n";
	private JTextField nameField;
	private JTextField fieldgenuine;
	private JTextField fieldforged;
	private JTextField fieldfake;
	 

	/**
	 * Create the frame.
	 * @param arrayOfFeatures2 
	 */
	public TrainingChooser(Boolean[] arrayOfFeaturesProp) {
		setResizable(false);
		
		frameTraining = this;
		
		this.arrayOfFeatures = arrayOfFeaturesProp;
		
		setTitle("Training");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 311, 429);
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
		
		JLabel lblAlgorithmsList = new JLabel("ALGORITHMS LIST");
		lblAlgorithmsList.setHorizontalAlignment(SwingConstants.CENTER);
		lblAlgorithmsList.setBounds(10, 11, 282, 32);
		panel_1.add(lblAlgorithmsList);
		
		Panel panel_CLASS = new Panel();
		panel_CLASS.setBounds(10, 49, 277, 62);
		panel_1.add(panel_CLASS);
		panel_CLASS.setLayout(null);
		
		JCheckBox chckbxSupportVectorMachine = new JCheckBox("Support Vector Machine");
		chckbxSupportVectorMachine.setSelected(true);
		chckbxSupportVectorMachine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arrayOfAlg[1] = chckbxSupportVectorMachine.isSelected();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Author");
		lblNewLabel.setBounds(10, 117, 56, 19);
		panel_1.add(lblNewLabel);
		
		
		chckbxSupportVectorMachine.setBounds(6, 33, 231, 23);
		panel_CLASS.add(chckbxSupportVectorMachine);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Algebraic Distance");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				arrayOfAlg[0] = chckbxNewCheckBox.isSelected();
			}
		});
		chckbxNewCheckBox.setBounds(6, 7, 231, 23);
		panel_CLASS.add(chckbxNewCheckBox);
		
		JButton btnNewButton = new JButton("Save Models");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// handler of secuting Model
				// start verify
				if (authName == ""){
					
				} else {

					int gernuine =Integer.parseInt(fieldgenuine.getText());
					int fake =Integer.parseInt(fieldfake.getText());
					int forgered =Integer.parseInt(fieldforged.getText());
					analizer.saveTheModel(arrayOfFeatures, arrayOfAlg, authName, gernuine, fake, forgered);
					// 
					
					//manage the results by CSV or print them: 
	                System.out.println("... model saved." + newline);
	                
	                
				}
				
			}
		});
		btnNewButton.setBounds(10, 254, 282, 60);
		panel_1.add(btnNewButton);
		

		
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setCaretColor(Color.BLACK);
		nameField.setAlignmentY(0.0f);
		nameField.setBounds(95, 117, 192, 19);
		panel_1.add(nameField);
		
		JButton button = new JButton("Back");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//torno indietro
				frameTraining.setVisible(false);
				FeaturesChooser ic = new FeaturesChooser(2);
				ic.setVisible(true);
				ic.setLocationRelativeTo(null);
			}
		});
		button.setBounds(10, 325, 282, 60);
		panel_1.add(button);
		

	    
		ButtonGroup nameorfilename= new ButtonGroup();
		
		JPanel dataformodelPanel = new JPanel();
		dataformodelPanel.setBounds(10, 147, 277, 96);
		panel_1.add(dataformodelPanel);
		dataformodelPanel.setLayout(null);
		
		fieldgenuine = new JTextField();
		fieldgenuine.setText("10");
		fieldgenuine.setBounds(177, 10, 76, 20);
		fieldgenuine.setHorizontalAlignment(SwingConstants.LEFT);
		dataformodelPanel.add(fieldgenuine);
		fieldgenuine.setColumns(10);
		
		fieldforged = new JTextField();
		fieldforged.setText("50");
		fieldforged.setHorizontalAlignment(SwingConstants.LEFT);
		fieldforged.setColumns(10);
		fieldforged.setBounds(177, 40, 76, 20);
		dataformodelPanel.add(fieldforged);
		
		fieldfake = new JTextField();
		fieldfake.setText("200");
		fieldfake.setHorizontalAlignment(SwingConstants.LEFT);
		fieldfake.setColumns(10);
		fieldfake.setBounds(177, 70, 76, 20);
		dataformodelPanel.add(fieldfake);
		
		JLabel lblNumberFoGenuine = new JLabel("Number of Genuine");
		lblNumberFoGenuine.setBounds(10, 10, 157, 20);
		dataformodelPanel.add(lblNumberFoGenuine);
		
		JLabel lblNumberOfForged = new JLabel("Number of Forged");
		lblNumberOfForged.setBounds(10, 40, 157, 20);
		dataformodelPanel.add(lblNumberOfForged);
		
		JLabel lblNumberOfFake = new JLabel("Number of Fake");
		lblNumberOfFake.setBounds(10, 70, 157, 20);
		dataformodelPanel.add(lblNumberOfFake);
	}
}
