package it.corvallis.imageprocessing.preprocessing;

import it.corvallis.imageprocessing.preprocessing.impl.BinarizePreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.CropPreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.DilatePreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.HorizontalJoinPreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.MeanFilterPreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.NegativePreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.NonePreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.PadPreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.ParticleFilterPreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.ResizePreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.RotatePreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.SkeletonizePreprocess;
import it.corvallis.imageprocessing.preprocessing.impl.VerticalJoinPreprocess;

import org.apache.log4j.Logger;

/**
 * Factory class to construct preprocessing objects.
 * When a new preprocessing is added to the software You need to add the switch condition in the {@link #getPreProcess(PreprocessingType)} method to build that preprocessing.
 * 
 * @author ChiarellaG
 *
 */
public class PreProcessingFactory {

	Logger log = Logger.getLogger(PreProcessingFactory.class);

	/**
	  * It builds the object of the requested preprocessing.
	  * @param preprocessingType The preprocessing enumerator to build.
	  * @return The preprocessing object if the enum exists, None preprocessing otherwise.
	  */
	public static AbstractPreprocess getPreProcess(PreprocessingType preprocessingType) {
		switch (preprocessingType) {
			case HorizontalJoin:
				return new HorizontalJoinPreprocess(preprocessingType);
			case Resize:
				return new ResizePreprocess(preprocessingType);
			case ParticleFilter:
				return new ParticleFilterPreprocess(preprocessingType);
			case Rotate:
				return new RotatePreprocess(preprocessingType);
			case Crop:
				return new CropPreprocess(preprocessingType);
			case Skeletonize:
				return new SkeletonizePreprocess(preprocessingType);
			case Binarize:
				return new BinarizePreprocess(preprocessingType);
			case Negative:
				return new NegativePreprocess(preprocessingType);
	        case None:
	        	return new NonePreprocess(preprocessingType);
			case VerticalJoin:
				return new VerticalJoinPreprocess(preprocessingType);
			case MeanFilter:
				return new MeanFilterPreprocess(preprocessingType);
			case Dilate:
				return new DilatePreprocess(preprocessingType);
			case Pad:
				return new PadPreprocess(preprocessingType);
			default:
	        	return new NonePreprocess(preprocessingType);
		}
	}
}
