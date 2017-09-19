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

public class RouteSelector extends JFrame {

	/**
	 * 
	 */
	public File fileImage;
	public FileAnalizer analizer;
	
	private JButton loadButton;
	private JButton analizeButton;
	private String authName;
	
	private static RouteSelector frame;
	
	static private final String newline = "\n";
	 


	/**
	 * Create the frame.
	 */
	public RouteSelector() {
		setResizable(false);
		
		frame = this;
		
		setTitle("Work Pannel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 455, 162);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		ButtonGroup group = new ButtonGroup();
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);

		//Load Button Definition
		String currentDirectoryPath =".";
		analizer = new FileAnalizer();
		panel_1.setLayout(null);
		
		loadButton = new JButton("Extraction");
		loadButton.setBounds(10, 11, 133, 97);
		panel_1.add(loadButton);
		
		loadButton.setIcon(null);
		
		analizeButton = new JButton("Training");
		panel_1.add(analizeButton);
		analizeButton.setBounds(153, 11, 133, 97);
		
		JButton btnNewButton = new JButton("Verify");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// handler of verify model
				frame.setVisible(false);
				FeaturesChooser ic = new FeaturesChooser(3);
				ic.setVisible(true);
				ic.setLocationRelativeTo(null);
			}
		});
		btnNewButton.setBounds(296, 11, 133, 97);
		panel_1.add(btnNewButton);
		
		
		ButtonGroup nameorfilename= new ButtonGroup();
		
	    
	    ButtonGroup fleorwhole = new ButtonGroup();
		
		
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(371, 63, 202, 19);
		panel_1.add(lblNewLabel_1);
		
		
		
		
		
		
		analizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// handler of analyzing action process
				frame.setVisible(false);
				FeaturesChooser ic = new FeaturesChooser(2);
				ic.setVisible(true);
				ic.setLocationRelativeTo(null);
				
				
				
			}
		});
		
		
		
		// bottone che carica l'immagine
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        //Handler of open_image_file button action.
				frame.setVisible(false);
				FeaturesChooser ic = new FeaturesChooser(1);
				ic.setVisible(true);
				ic.setLocationRelativeTo(null);
				

				
			}
		});		
	}
}
