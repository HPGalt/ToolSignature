package it.corvallis.imageprocessing.preprocessing.impl;

import ij.ImagePlus;
import it.corvallis.MainClass;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.preprocessing.AbstractPreprocess;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;

/**
 * This class performs the resizing of an image by means of #{@link Filter} class methods.
 * 
 * @author ChiarellaG
 *
 */
public class ResizePreprocess extends AbstractPreprocess {
	
	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		width("width", "400.0"),
		height("height", "70.0"),
		keep_aspect("keep_aspect", "true"),
		threshold("threshold", "false");
        
        private String name;
        private String value;
        
        private CONFIGURATION_OPTIONS(String name, String default_value) {
            this.name = name.trim().toLowerCase();
            this.value = default_value.trim().toLowerCase();
        }
        
        @Override
        public String getPropertyName() {
            return name;
        }
        
        @Override
        public String getDefaultValue() {
            return value;
        }
        
    }

	/**
	 * Default constructor. It only calls the super() constructor.
	 * @param p The preprocessing type.
	 */
	public ResizePreprocess(PreprocessingType p) {
		super(p);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractPreprocess}) constructor.
	 * @param p The object to copy.
	 */
	public ResizePreprocess(ResizePreprocess p) {
		super(p);
	}

	@Override
	public ImagePlus performPreprocess(ImagePlus img, double... params) {
		int width = (int) params[0];
		boolean threshold = params[3] > 0.0 ? true : false;
		int height;
		if (params[2] > 0.0) {
			height = (int) ((double) width / ((double) img.getWidth() / (double) img.getHeight()));
		} else {
			height = (int) params[1];
		}
		img = Filter.resizeImage(img, width, height, threshold);
		return img;
	}

}
