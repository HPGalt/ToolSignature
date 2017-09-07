package it.corvallis.imageprocessing.measurements;
import ij.ImagePlus;

public class TrisurfaceProcessor extends SegmentedImageProcessor {

	/**
	 * The image has to be divided in three parts vertically.
	 */
	public TrisurfaceProcessor(final ImagePlus theImage) {
		super(theImage, 3, 1);
	}
	
	/**
	 * Calculate the normalised area of the image.
	 *
	 * ATTENZIONE: problema nella normalizzazione dell'area se le immagini hanno dimenzioni diverse
	 */
	protected double area(final ImagePlus segment) {
		return Hu.area(segment, 0, 0, segment.getWidth(), segment.getHeight()) / (double) (segment.getWidth() * segment.getHeight());
	}

	/**
	 * Calculate the trisurface feature.
	 *
	 * From [Schafer-Viriri]:
	 *   The trisurface feature is obtained by splitting the signature into three equals parts
	 *   and obtaining the area for each of the three parts.
	 */
	protected double[] processSegment(final ImagePlus segment) {
		return new double[]{ area(segment) };
	}
	
	public double[] getTrisurface(){
		return process();
	}
}