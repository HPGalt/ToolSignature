package it.corvallis.imageprocessing.measurements;

import ij.ImagePlus;

/**
 * Calculate the number of line ends and junctions of a signature image.
 * 
 * TODO documentation
 */
public class LineEndsAndJunctionsProcessor extends SegmentedImageProcessor {
	private double[] lineEnds;
	private double[] lineJunctions;

	public LineEndsAndJunctionsProcessor(final ImagePlus imagePlus, final int hSegments, final int vSegments) {
		super(imagePlus, hSegments, vSegments);
	}

	public void countLineEndsAndJunctions() {
		double[] completeVector = process();
		lineEnds = new double[ completeVector.length / 2 ];
		lineJunctions = new double[ completeVector.length / 2 ];
		
		int k=0;
		for(int i=0; i<completeVector.length; i+=2 ){
			lineEnds[k] = completeVector[i];
			lineJunctions[k] = completeVector[i+1];
			k++;
		}
	}

	public double[] getLineEnds() {
		return lineEnds;
	}
	
	public double[] getLineJunctions() {
		return lineJunctions;
	}

	protected double[] processSegment(final ImagePlus segment) {
//		int segmentOffsetX = segment.getSampleModelTranslateX();
//		int segmentOffsetY = segment.getSampleModelTranslateY();
		int segmentWidth = segment.getWidth();
		int segmentHeight = segment.getHeight();
		int rowStep = /*imagePlus*/segment.getWidth() - segmentWidth;
		byte[] pixels = (byte[]) /*imagePlus*/segment.getProcessor().getPixels();
		
		// Contatori
		double lineEndCount = 0;
		double lineJunctionCount = 0;
				
		// Partiamo dal secondo pixel (in generale evitiamo il bordo
		// dell'immagine)
		int localCol = 1;
		int localRow = 1;
		int pixelPos = (/*segmentOffsetY*/ + localRow) * /*imagePlus*/segment.getWidth() + /*segmentOffsetX*/ + localCol;
		for( ; localRow < segmentHeight - 1; pixelPos++ ) {

			// Consideriamo solo i casi in cui il pixel corrente è nero
			if (pixels[pixelPos] == 0) {

				// Analizziamo i suoi 8 vicini
				int[] count = countAdjacentPixels(pixels, pixelPos, /*imagePlus*/segment.getWidth());
				
				// Verifichiamo se si tratta di un punto finale o di un incrocio
				if( isLineEnd(count) )
					lineEndCount++;
				else if(isLineJunction(count) )
					lineJunctionCount++;
				
			}

			localCol++;
			if (localCol >= segmentWidth - 1) {
				localCol = 1;
				localRow++;
				pixelPos += rowStep + 2;
			}
			
		}

		double[] ret = { lineEndCount, lineJunctionCount };
		return ret;

	}
	
	private boolean isLineJunction(int[] count) {
		return count[0] - count[1] >= 3;
	}
	
	private boolean isLineEnd(int[] count) {
		return count[0] == 1;
	}

	private int[] countAdjacentPixels(
			final byte[] pixels, int pixelPos, final int globalImageWidth ) {
		int oneCount = 0;
		int consecutiveCount = 0;
		
		byte previousPixel = 5;
		int[][] giro = new int[][] {{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0}};
		
		// Ci posizioniamo in alto a sx e scorriamo in senso orario gli otto pixels
		// che circondano il pixel nero alla posizione pixelPos
		for (int[] passo : giro) {
			
			int pos = pixelPos + passo[1] * globalImageWidth + passo[0];			
			byte pixelCorrente = pixels[pos];
			
			// Controlliamo se il pixel corrente è nero
			if( pixelCorrente == 0 ){
				oneCount++;
				
				// ...e se è vicino a un altro pixel nero
				if( pixelCorrente == previousPixel )
					consecutiveCount++;
			}
			
			// Salviamo il valore del pixel precedente a quello
			// da considerare
			previousPixel = pixelCorrente;
		}
		
		// Confrontiamo l'ultimo pixel col primo
		if( previousPixel == 0 && 
				previousPixel == pixels[pixelPos + giro[0][1] * globalImageWidth + giro[0][0]] )
			consecutiveCount++;
		
		return new int[] {oneCount, consecutiveCount};
	}

}