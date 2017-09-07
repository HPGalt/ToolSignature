package it.corvallis.imageprocessing.measurements;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;
import it.corvallis.commons.MeasurementUtils;

/**
 * Calculate the slant for a signature image.
 * 
 * TODO documentation
 */
public class SlantProcessor extends SegmentedImageProcessor {

	public SlantProcessor(final ImagePlus imagePlus, final int hSegments, final int vSegments) {
		super(imagePlus, hSegments, vSegments);

		/*
		 * Per fare la thinning dell'immagine usiamo solo la funzione
		 * "Skeletonize", eliminando i precedenti "Open" e "Close-". Queste due
		 * operazioni permetterebbero in alcuni casi di ottenere uno scheletro
		 * più preciso, ma "Open" elimina i tratti che hanno spessore pari a 1
		 * pixel, quindi si rischia di ottenere un immagine nulla nei casi in
		 * cui è già scheletrizzata. Il metodo "Skeletonize" di ImageJ
		 * implementa l'algoritmo di thinning Zhang-Suen.
		 */
		ImageConverter iGrayScale8Bit = new ImageConverter(imagePlus);
		iGrayScale8Bit.convertToGray8();
		
		IJ.run(imagePlus, "Threshold", "");	
		IJ.run(imagePlus, "Skeletonize", "");
	}

	protected double[] processSegment(final ImagePlus segment) {
		int segmentWidth = segment.getWidth();
		int segmentHeight = segment.getHeight();

		int slants[] = new int[Direction.values().length - 2];
		int rowStep = segment.getWidth() - segmentWidth;
		byte[] pixels = (byte[]) segment.getProcessor().getPixels();
		// Partiamo dal secondo pixel (in generale evitaimo il bordo
		// dell'immagine)
		int localCol = 1;
		int localRow = 1;
		int pixelPos = (localRow) * segment.getWidth() + localCol;
		int multiple = 0;
		for (; localRow < segmentHeight - 1; pixelPos++) {

			// Consideriamo solo i casi in cui il pixel corrente è nero
			if (pixels[pixelPos] == 0) {
				Direction currentSlant = Direction.MULTIPLE;
				for (Direction each : Direction.values()) {
					if (each.matches(pixels, pixelPos, segment.getWidth())) {
						currentSlant = each;
						break;
					}
				}
				if (currentSlant == Direction.MULTIPLE)
					multiple++;
				else
					slants[currentSlant.ordinal()]++;
			}

			localCol++;
			if (localCol >= segmentWidth - 1) {
				localCol = 1;
				localRow++;
				pixelPos += rowStep + 2;
			}
		}

		// Cerchiamo il valore di slant ( diverso da Direction.NONE
		// e Direction.MULTIPLE) più frequente. Se la sua prevalenza
		// sugli altri valori è netta, classifichiamo la cella con esso,
		// altrimenti diciamo che il valore di slant della cella è
		// Direction.MULTIPLE (cioè "dubbio")
		double somma = 0;
		for (int i = 0; i < slants.length; i++) {
			somma += slants[i];
		}

		char chosen = Direction.NONE.name().charAt(0);
		if (somma > 0d) {
			int index = MeasurementUtils.highestElementIndex(slants);
			double val = slants[index] / somma;
			if (val < 0.5d)
				chosen = Direction.MULTIPLE.name().charAt(0);
			else
				chosen = Direction.values()[index].name().charAt(0);
		} else if (multiple > 0)
			chosen = Direction.MULTIPLE.name().charAt(0);

		return  new double[] { chosen };
	}
	
	public double[] getSlant() {
		return process();
	}

	private static enum Direction {

		// Maschere di 3x3 pixel corrispondenti alle possibili
		// "classi di inclinazione"

		VERTICAL(new int[] { 0, 1, 0, 0, 1, 0, 0, 1, 0 }),

		HORIZONTAL(new int[] { 0, 0, 0, 1, 1, 1, 0, 0, 0 }),

		ASCENDING(new int[] { 0, 0, 1, 0, 1, 0, 1, 0, 0 }),

		DESCENDING(new int[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 }),

		NONE(null),

		MULTIPLE(null);

		int[] directionMatrix;

		Direction(int[] directionMatrix) {
			this.directionMatrix = directionMatrix;
		}

		/**
		 * Answer whether the current slant direction matches at the specified
		 * position in the pixel array.
		 */
		public boolean matches(final byte[] pixels, int pixelPos, final int globalImageWidth) {
			if (directionMatrix == null)
				return false;

			// Ci posizioniamo in alto a sx e scorriamo gli otto pixels
			// che circondano il pixel nero alla posizione pixelPos
			int matrixPos = 0;
			for (int row = -1; row < 2; row++) {
				for (int col = -1; col < 2; col++) {
					int pos = pixelPos + row * globalImageWidth + col;
					if (pixels[pos] + 1 != directionMatrix[matrixPos])
						return false;
					matrixPos++;
				}
			}
			return true;
		}
	}

}