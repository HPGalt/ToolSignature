package it.corvallis.imageprocessing.measurements;

import ij.ImagePlus;


/*
 * Un'altra interpretazione della sixfold surface feature prevede il calcolo
 * del numero di pixel totali dei sei bounding box, come percentuale del
 * numero di pixel totali dell'immagine.
 */
public class AlternativeSixfoldSurfaceProcessor extends SixfoldSurfaceProcessor {

	public AlternativeSixfoldSurfaceProcessor(final ImagePlus theImage) {
		super(theImage);
	}
	
	@Override
	protected double[] processSegment(final ImagePlus segment) {
		
		if( segment == null )
			return new double[]{0,0};

		int segmentWidth = segment.getWidth();
		int segmentHeight = segment.getHeight();

		double[] segmentCentreOfGravity = Hu.getCentreOfGravity(segment, 0, 0, segmentWidth, segmentHeight);

		int separatorY = (int) Math.round(segmentCentreOfGravity[1]);
		int firstArea = segment.getWidth() * separatorY;
		int secondArea = segment.getWidth() * (segment.getHeight() - separatorY);
		double areaTot = imagePlus.getWidth() * imagePlus.getHeight();

		return new double[]{ firstArea / areaTot, secondArea / areaTot };
		
	}

}
