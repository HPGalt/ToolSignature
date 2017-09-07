package it.corvallis.imageprocessing.measurements;

import ij.ImagePlus;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class SixfoldSurfaceProcessor extends SegmentedImageProcessor {

	/**
	 * The image has to be divided in three parts vertically.
	 */
	public SixfoldSurfaceProcessor(final ImagePlus theImage) {
		super(theImage, 3, 1);
	}

	/**
	 * Calculate the sixfold-surface feature.
	 * 
	 * From [Schafer-Viriri]: The first step is to divide the image into three
	 * equal parts. In each of the three areas, the centre of gravity is then
	 * calculated. For each bounding box, the area of pixels above and below the
	 * centre of gravity is calculated. Each area is represented as a percentage
	 * of the entire image area.
	 */
	public double[] getSixfoldSurface() {
		return process();
	}

	@Override
	protected double[] processSegment(final ImagePlus segment) {
		
		if(segment == null)
			return new double[]{0.0, 0.0};

		int segmentWidth = segment.getWidth();
		int segmentHeight = segment.getHeight();

		double[] segmentCentreOfGravity = Hu.getCentreOfGravity(segment, 0, 0, segmentWidth, segmentHeight );

		int separatorY = (int) Math.round(segmentCentreOfGravity[1]);
		int firstArea = (int) Hu.area(segment, 0, 0, segmentWidth, separatorY );
		int secondArea = (int) Hu.area(segment, 0, 0 + separatorY, segmentWidth, segmentHeight - separatorY);

		// Bisogna normalizzare l'area della firma in ogni cella
		// rispetto all'area della cella
		return new double[]{
				firstArea / (double) (segment.getWidth() * separatorY),
				secondArea / (double) (segment.getWidth() * (segment.getHeight()-separatorY))};
	}

	@Override
	protected List<ImagePlus> getSegments() {

		// Prendo le tre parti in cui divido l'immagine
		List<ImagePlus> imageSegments = super.getSegments();
		List<ImagePlus> croppedImageSegments = new ArrayList<ImagePlus>();

		// Per ogni parte devo considerare solo il bounding box minimo
		for (ImagePlus segment : imageSegments) {
			Rectangle bounds = autocrop(segment);
			// Se nel rettangolo considerato non ci sono pixel neri,
			// restituiamo segment==null, in modo da potervi poi assegnare
			// direttamente area==0, senza effettuare ulteriori calcoli
			if( bounds.x < 0 || bounds.y < 0 || bounds.width < 0 || bounds.height < 0 )
				croppedImageSegments.add(null);
			else {
				ImagePlus t = segment.duplicate();
				t.setRoi(bounds);
				croppedImageSegments.add(new ImagePlus("", t.getProcessor().crop()));
			}
		}
		
		return croppedImageSegments;

	}

	// Metodo per fare il crop di una parte dell'immagine
	private Rectangle autocrop(ImagePlus segment) {

		Rectangle bounds = new Rectangle(0, 0, segment.getWidth(), segment.getHeight());
		Point topLeft = new Point(bounds.x + bounds.width, bounds.y + bounds.height);
		Point bottomRight = new Point(0, 0);
		int rowStep = segment.getWidth() - bounds.width;
		byte[] pixels = (byte[]) segment.getProcessor().getPixels();
		int pixelPos = bounds.y * bounds.width + bounds.x;
		int localCol = 0;
		int localRow = 0;
		for (; localRow < bounds.height; pixelPos++) {
			if (pixels[pixelPos] == 0) {
				topLeft.x = Math.min(topLeft.x, localCol);
				topLeft.y = Math.min(topLeft.y, localRow);
				bottomRight.x = Math.max(bottomRight.x, localCol);
				bottomRight.y = Math.max(bottomRight.y, localRow);
			}

			localCol++;
			if (localCol >= bounds.width) {
				localCol = 0;
				localRow++;
				pixelPos += rowStep;
			}
		}

		return new Rectangle(
				bounds.x + topLeft.x, bounds.y + topLeft.y,
				bottomRight.x-topLeft.x+1, bottomRight.y-topLeft.y+1 );
	}
}