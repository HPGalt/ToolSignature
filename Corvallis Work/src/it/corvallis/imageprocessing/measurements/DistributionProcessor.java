package it.corvallis.imageprocessing.measurements;
import ij.ImagePlus;

/**
 * Calculate the pixel distribution for a signature image.
 *
 * The pixel distribution of an image is calculated by cutting the image
 * into rectangular segments. Each segment is analysed individually and 
 * is segmented further into four quadrants of equal size. The pixel distribution
 * is measured by projecting the pixels of each half of the image to the
 * nearest border and counting the number of 'rays' that hit those borders. The
 * process will yield four numbers per segment, one for each of the four borders
 * of that segment.
 *
 */
public class DistributionProcessor extends SegmentedImageProcessor {
	
	public DistributionProcessor(final ImagePlus theImage, final int hSegments, final int vSegments) {
		super(theImage, hSegments, vSegments);
	}
	
	protected double[] processSegment(final ImagePlus segment) {
		final int width = segment.getWidth();
		final int height = segment.getHeight();

		final int halfX = segment.getWidth() / 2;
		final int halfY = segment.getHeight() / 2;

		boolean[] leftHalfProjection = new boolean[height];
		boolean[] topHalfProjection = new boolean[width];
		boolean[] rightHalfProjection = new boolean[height];
		boolean[] bottomHalfProjection = new boolean[width];

		for (int column = 0; column < width; ++column) {
			for (int row = 0; row < height; ++row) {
				final int sample = segment.getProcessor().getIntArray()[column][row];//getSample(x, y, 0);

				if (sample != 0) {
					continue;
				}

				if (column <= halfX)
					leftHalfProjection[row] = true;
				else
					rightHalfProjection[row] = true;

				if (row <= halfY)
					topHalfProjection[column] = true;
				else
					bottomHalfProjection[column] = true;
			}
		}
		
		int leftHalfProjCount, topHalfProjCount, rightHalfProjCount, bottomHalfProjCount;
		leftHalfProjCount = topHalfProjCount = rightHalfProjCount = bottomHalfProjCount = 0;
		for (int row = 0; row < height; ++row) {
			if (leftHalfProjection[row])
				++leftHalfProjCount;
			if (rightHalfProjection[row])
				++rightHalfProjCount;
		}

		for (int column = 0; column < width; ++column) {
			if (topHalfProjection[column])
				++topHalfProjCount;
			if (bottomHalfProjection[column])
				++bottomHalfProjCount;
		}
	
		// ATTENZIONE: bisogna normalizzare i valori rispetto ad altezza e larghezza della cella	
		return new double[] {leftHalfProjCount / (double) height, topHalfProjCount / (double) width, 
				 rightHalfProjCount / (double) height, bottomHalfProjCount / (double) width};
	}
	
	public double[] getDistribution(){
		return process();
	}

}