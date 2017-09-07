package it.corvallis.imageprocessing.measurements;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import it.corvallis.commons.MeasurementUtils;


/** 
 * Calculate a bunch of global features for a signature image.
 *
 * Some features extracted here are suggested in the Schafer-Viriri paper
 * (referred to as [Schafer-Viriri] from here on).
 *
 * The following global features are extracted:
 * ◆ normalised area
 * ◆ aspect ratio
 * ◆ maximum histograms (positions and values)
 * ◆ centre of gravity
 * ◆ number of close parts
 * ◇ perimeter of the convex hull
 * ◇ transition features(number and positions)
 *
 * TODO documentation
 */
public class GlobalFeaturesProcessor {
	private ImagePlus imagePlus;
	
	private double[] histogramPositions = new double[2];
	private double[] histogramValues = new double[2];
	double[] positionsOfTransitions = new double[8];
	double[] numberOfTransitions = new double[2];
	double areaRatio;
	
	/**
	 * The global features processor.
	 * @param imagePlus The image to process.
	 */
	public GlobalFeaturesProcessor(final ImagePlus imagePlus) {
		this.imagePlus = imagePlus;
	}
	
	/**
	 * Calculate the normalised area of the image.
	 *
	 * From [Schafer-Viriri]:
	 *   The normalised area of the signature is the ratio of the area that makes up the signature
	 *   to the area occupied by that signature's bounding box. 
	 *   
	 * ATTENZIONE: normalizzando l'area rispetto al numero totale di pixel del bounding box
	 * si ottiene un valore che dipende dalle dimensioni dell'immagine (se lo spazio per firmare
	 * è grande, farò una firma più grande che però occupa in percentuale una superficie minore 
	 * rispetto a quella occupata da una firma concentrata in uno spazio piccolo). 
	 * 
	 * TODO serve una qualche forma di "normalizzazione" delle immagini prima di chiamare questo
	 * metodo
	 */
	public double getSignatureConvexhullArea() {
		final double A_signatureConvexHull = Hu.moment(imagePlus, 0, 0, 0, 0, imagePlus.getWidth(), imagePlus.getHeight());
		return A_signatureConvexHull;
	}
	
	/**
	 * Calculate the maximum histograms of the image.
	 *
	 * From [Schafer-Viriri]:
	 *   This feature is composed of the maximum horizontal histogram and the maximum 
	 *   vertical histogram. The horizontal histogram is calculated by going through 
	 *   each row in the signature and counting the pixels in the row. The row with the
	 *   highest number of pixels is the maximum horizontal histogram. The vertical 
	 *   histogram is computed analogously.
	 *   
	 * ATTENZIONE: i valori vanno normalizzati, in modo che siano indipendenti dalle dimensioni
	 * dell'immagine
	 */ 
	public void computeHistograms() {
		int width = imagePlus.getWidth();
		int height = imagePlus.getHeight();
			
		int[] columns = new int[width];
		int[] rows = new int[height];

		/* 
		 * To avoid having to traverse the image repeatedly, both the horizontal
		 * and vertical histograms are obtained by making one _single_ pass 
		 * through the image.
		 */
		byte[] pixels = (byte[]) imagePlus.getProcessor().getPixels();
		for (int column = 0; column < width; ++column) {
			for (int row = 0; row < height; ++row) {
				if (pixels[row*width+column] == 0) {
					++columns[column];
					++rows[row];
				}
			}
		}

		final int vIndex = MeasurementUtils.highestElementIndex(columns);
		final int hIndex = MeasurementUtils.highestElementIndex(rows);
		
		final int vMax = columns[vIndex];
		final int hMax = rows[hIndex];
		
		histogramValues[0] = vMax/(double)height;
		histogramValues[1] = hMax/(double)width;
		histogramPositions[0] = vIndex/(double)width;
		histogramPositions[1] = hIndex/(double)height;
	}
	
	public double[] getHistogramValues() {
		return histogramValues;
	}
	
	public double[] getHistogramPositions() {
		return histogramPositions;
	}

	private double convexHullPerimeter() {
		IJ.run("Set Measurements...", "area perimeter redirect=None decimal=3");
		IJ.run(imagePlus, "Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear");
		ResultsTable rt = ResultsTable.getResultsTable();
		double p = 0;
		double[] perimeterCloseParts = rt.getColumnAsDoubles(ResultsTable.PERIMETER);
		for (int i = 0; i < perimeterCloseParts.length; i++) {
			p = p + perimeterCloseParts[i];
		}
		return p / (2.0 * ((double) imagePlus.getWidth() + (double) imagePlus.getHeight()));		
	}
	
	public double getConvexHullPerimeter() {
		return convexHullPerimeter();
	}

	/**
	 * Compute the transition feature, i.e.
	 * 1) an array containing the number of transition from black to white pixels
	 * 	  and vice versa, in the horizontal and vertical direction;
	 * 2) an array containing the positions of the horizontal and vertical
	 *    transitions, distinguishing between transitions from black to white pixels
	 *    and transitions from white to black pixels.
	 *    
	 * All elements of the arrays are suitably normalized with respect to the height
	 * or the width of the image.
	 */
	public void transitions(){
		
		double horizontalTransitionsBW = 0;
		double verticalTransitionsBW = 0;
		double horizontalTransitionsWB = 0;
		double verticalTransitionsWB = 0;
		
		double xSumHorBW = 0;
		double xSumHorWB = 0;
		double ySumHorBW = 0;
		double ySumHorWB = 0;
		
		double xSumVerBW = 0;
		double xSumVerWB = 0;
		double ySumVerBW = 0;
		double ySumVerWB = 0;
		
		int width = imagePlus.getWidth();
		int height = imagePlus.getHeight();
		
		byte[] pixels = (byte[]) imagePlus.getProcessor().getPixels();
		
		int k = 0;
		
		for(int row = 0; row<height; row++){
			
			for(int col = 0; col<width; col++){
				
				if(col<width-1) {
					if( pixels[k+col] == 0 && pixels[k+col+1] == -1 ){
						xSumHorBW += (col+1);
						ySumHorBW += row;
						horizontalTransitionsBW++;
					} else if ( pixels[k+col] == -1 && pixels[k+col+1] == 0 ){
						xSumHorWB += (col+1);
						ySumHorWB += row;
						horizontalTransitionsWB++;
					}
				}
				
				if( row < height-1){
					if( pixels[k+col] == 0 && pixels[k+col+width] == -1 ){
						xSumVerBW += col;
						ySumVerBW += (row+1);
						verticalTransitionsBW++;
					} else if ( pixels[k+col] == -1 && pixels[k+col+width] == 0 ){
						xSumVerWB += col;
						ySumVerWB += (row+1);
						verticalTransitionsWB++;
					}
				}
			}
			k += width;
		}
	
		double xAverageHorBW = 0;
		double yAverageHorBW = 0;
		if( horizontalTransitionsBW > 0 ){
			xAverageHorBW = xSumHorBW/horizontalTransitionsBW; 
			yAverageHorBW = ySumHorBW/horizontalTransitionsBW; 
		}		
		double xAverageHorWB = 0;
		double yAverageHorWB = 0;
		if( horizontalTransitionsWB > 0 ){
			xAverageHorWB = xSumHorWB/horizontalTransitionsWB;
			yAverageHorWB = ySumHorWB/horizontalTransitionsWB;
		}		
		double xAverageVerBW = 0;
		double yAverageVerBW = 0;
		if( verticalTransitionsBW > 0 ){
			xAverageVerBW = xSumVerBW/verticalTransitionsBW; 
			yAverageVerBW = ySumVerBW/verticalTransitionsBW; 			
		}		
		double xAverageVerWB = 0;
		double yAverageVerWB = 0;
		if( verticalTransitionsWB > 0 ){
			xAverageVerWB = xSumVerWB/verticalTransitionsWB;
			yAverageVerWB = ySumVerWB/verticalTransitionsWB;
		}
		
		// Le posizioni "medie" delle transizioni da bianco a nero e 
		// da nero a bianco vengono normalizzate tra 0 e 1 ,
		// dividendo per altezza/larghezza dell'immagine -1
		// a seconda dei casi
		positionsOfTransitions[0] = xAverageHorBW /(width-1); 
		positionsOfTransitions[1] = yAverageHorBW/(height-1);
		positionsOfTransitions[2] = xAverageVerBW/(width-1);
		positionsOfTransitions[3] = yAverageVerBW/(height-1);	
		
		positionsOfTransitions[4] = xAverageHorWB/(width-1);
		positionsOfTransitions[5] = yAverageHorWB/(height-1);
		positionsOfTransitions[6] = xAverageVerWB/(width-1);
		positionsOfTransitions[7] = yAverageVerWB/(height-1);	

		// Normalizziamo rispetto al numero totale di transizioni possibili:
		//  - per ogni riga le transizioni orizzontali possono essere al più
		// 	  in numero pari alla larghezza dell'immagine meno -1
		//  - per ogni colonna le transizioni verticali possono essere al più
		// 	  in numero pari all'altezza dell'immagine meno -1
		numberOfTransitions[0] = ( horizontalTransitionsBW + horizontalTransitionsWB )/((width-1)*height);
		numberOfTransitions[1] = ( verticalTransitionsBW + verticalTransitionsWB )/((height-1)*width);

	}	
	
	public double[] getPositionsOfTransitions(){
		return positionsOfTransitions;
	}
	
	public double[] getNumberOfTransitions(){
		return numberOfTransitions;
	}

}
