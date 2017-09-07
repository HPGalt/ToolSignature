package it.corvallis.imageprocessing.preprocessing;

import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * AbstractPreprocess is the base class that a new preprocessing have to extend. It implements all boundary needs to perform a preprocessing delegating the core algorithm
 * to an abstract method to implement in the child class.
 * 
 * @author ChiarellaG
 *
 */
public abstract class AbstractPreprocess {

	protected Logger log = Logger.getLogger(AbstractPreprocess.class.getName());
	private PreprocessingType preprocessing = null;
	
	/**
	 * Default constructor. Do nothing.
	 * @param p The preprocess type.
	 */
	public AbstractPreprocess(PreprocessingType p) {
		preprocessing = p;
	}
	
	/**
	 * Copy constructor. Do nothing.
	 * @param p The preprocess object to copy.
	 */
	public AbstractPreprocess(AbstractPreprocess p) {
		preprocessing = p.preprocessing;
	}
	
	/**
	 * This method has to implement the preprocessing algorithm.
	 * @param img The input image.
	 * @param params The parameters the preprocessing needs of in array form.
	 * @return The filtered image.
	 */
	public ImagePlus execute(ImagePlus img, double... params) {
		ImagePlus res = performPreprocess(img.duplicate(), CollectionUtils.deep_copy(params));
		res.setTitle(getType().getPrefix() + img.getTitle());
		return res;
	}

	/**
	 * This method has to implement the preprocess algorithm.
	 * @param img The input image onto which perform the preprocess.
	 * @param parameters The parameters.
	 * @return The preprocessed input image.
	 */
	protected abstract ImagePlus performPreprocess(ImagePlus img, double... parameters);
	
	/**
	 * Gets method for the preprocess type.
	 * @return The preprocess type.
	 */
	public PreprocessingType getType() {
		return preprocessing;
	}
}
