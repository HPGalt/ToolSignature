package it.corvallis;

import java.awt.EventQueue;


public class MainClass {
	/**
     * Loads an image.
     * @param img The image file name to load.
     * @return The loaded image.
     */
	
	//Old version
	/* public static ImagePlus loadImage(File img) {
			return new ImagePlus(img.getAbsolutePath());
		}
	 */


	public MainClass() {		

		RouteSelector ja = new RouteSelector();
		ja.setVisible(true);
		ja.setLocationRelativeTo(null);
		
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainClass();
			}
		});
	}
}
