package it.corvallis.imageprocessing.measurements;
import ij.ImagePlus;

import java.util.List;

public class CentroidProcessor extends SegmentedImageProcessor {

	/**
	 * The image has to be divided in two parts vertically.
	 * @param theImage The image to process.
	 */
	public CentroidProcessor(final ImagePlus theImage) {
		super(theImage, 2, 1);
	}
	
	/**
	 * Calculate the centroid feature.
	 *
	 * From [Schafer-Viriri]:
	 *   The first step is to divide the image into two equal parts.
	 *   In each of these parts, the centre of gravity is then calculated. 
	 *   The angle between the horizontal axis and the line formed by joining
	 *   the two centres of gravity is the wanted feature.
	 */
	private double centroid(){

		// Calcolo il centro di gravità di ognuna delle due metà in cui è
		// stata suddivisa l'immagine
		double[] centresOfGravity = process();
		List<ImagePlus> segments = getSegments();
		centresOfGravity[2] += segments.get(0).getWidth();
		
		// Calcolo base, altezzza e ipotenusa del triangolo rettangolo formato dal segmento
		// che congiunge i due centri di gravità e le due rette parallele agli assi
		// passanti per tali punti
		double height = centresOfGravity[1] - centresOfGravity[3];
		double base = centresOfGravity[0] - centresOfGravity[2];
		double hypotenuse = Math.sqrt( base*base + height*height );
		
		// Ritorno il centroide della firma
		// (si somma 0.5 per essere sicuri che l'angolo sia rappresentato da un valore tra 0 e 1)
		return Math.asin( height/hypotenuse )/Math.PI + 0.5;
	}
	
	protected double[] processSegment(final ImagePlus segment) {
		// Calcolo il centro di gravità del singolo pezzettino
		double[] localCentreOfGravity = Hu.getCentreOfGravity(segment,
											0, 
											0, 
											segment.getWidth(), 
											segment.getHeight());
			
		// Ritorno il centro di gravità del pezzettino in coordinate globali
		// (rispetto all'intera immagine)
		return new double[]{
				localCentreOfGravity[0], localCentreOfGravity[1]};
	}
	
	public double getCentroid(){
		return centroid();
	}
}