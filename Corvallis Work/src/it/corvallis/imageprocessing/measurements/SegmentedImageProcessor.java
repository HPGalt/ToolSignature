package it.corvallis.imageprocessing.measurements;
import static java.lang.Math.floor;
import ij.ImagePlus;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculate graphometric features of an image by dividing it into rectangular segments.
 *
 * This class performs the grunt work of determining how exactly to divide the image 
 * into segments and then defers the actual extracting of the graphometric features to subclasses.
 *
 * The size of the segments is calculated automatically by the number of horizontal
 * and vertical segments specified. If the image cannot be divided evenly into the
 * requested number of segments, the (generally small number of) odd pixels which do
 * not fit will analysed in the bottom and right cells. Therefore, it happens that
 * not all segments have the exact same size. In general, the overflow borders will 
 * be quite small and thus only a small amount (if any) of information will be add
 * in some cells.
 */
public abstract class SegmentedImageProcessor /*extends BaseImageProcessor*/ {
	/** 
	 * The wrapped image as passed to the constructor.
	 */
	protected ImagePlus imagePlus;
	/**
	 * The number of horizontal segments into which the image is segmented.
	 */
	protected int hSegments;
	/**
	 * The number of vertical segments into which the image is segmented.
	 */
	protected int vSegments;

	/**
	 * Prepare analysing a new image.
	 *
	 * @param theImage The image that has to be analysed.
	 * @param hSegments The number of horizontal segments into which the image hass to be divided. Different types of graphometric features may have different 'good' segment sizes.
	 * @param vSegments The number of vertical segments into which the image hass to be divided. Different types of graphometric features may have different 'good' segment sizes.
	 */
	public SegmentedImageProcessor(final ImagePlus theImage, final int hSegments, final int vSegments) {
		this.imagePlus = theImage;
		this.hSegments = hSegments;
		this.vSegments = vSegments;
	}

	/**
	 * Divides the signature image in cells.
	 */
	protected List<ImagePlus> getSegments(){
		List <ImagePlus> imageSegments = new ArrayList<ImagePlus>();
		
		final int hSize = (int) floor((float) imagePlus.getWidth() / (float) hSegments);
		final int vSize = (int) floor((float) imagePlus.getHeight() / (float) vSegments);

		for(int row = 0; row < vSegments; ++row) {
			final int y = vSize * row;
			int h = vSize;
			if(row == vSegments-1)
				h = imagePlus.getHeight() - (vSize * row);
			
			for(int column = 0; column < hSegments; ++column) {
				final int x = hSize * column;
				int w = hSize;

				if(column == hSegments-1)
					w = imagePlus.getWidth() - (hSize * column);
				
				final Rectangle bounds = new Rectangle(x, y, w, h);
				ImagePlus roi = imagePlus.duplicate();
				roi.setRoi(bounds);
				roi = new ImagePlus("", roi.getProcessor().crop());
				imageSegments.add(roi);
			}
		}
		
		return imageSegments;

	}

	/**
	 * Analyze a signature image cell by cell and extract
	 * the features locally.
	 * 
	 * @return an array containing the feature values extracted
	 * 		   from each cell of the grid
	 */
	protected double[] process(){
		
		List <ImagePlus> imageSegments = getSegments();
		
		double[][] imageSegmentsValues = new double [imageSegments.size()][];
		
		int i = 0;
		
		for (ImagePlus imageSegment : imageSegments){
			imageSegmentsValues[i] = processSegment(imageSegment);
			i++;
		}
		
		double[] processedValues = new double [imageSegmentsValues[0].length*i];
		int z = 0;
		
		for (int j = 0; j<i; j++){
			for (int k = 0; k<imageSegmentsValues[j].length; k++){
				processedValues[z]=imageSegmentsValues[j][k];
				z++;
			}
		}
		
		return processedValues;
	}
	
	/**
	 * Analyse a single cell of a signature image
	 */
	protected abstract double[] processSegment(ImagePlus imageSegment);

}
