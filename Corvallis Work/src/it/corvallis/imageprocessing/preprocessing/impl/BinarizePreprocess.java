package it.corvallis.imageprocessing.preprocessing.impl;

import ij.ImagePlus;
import ij.process.AutoThresholder;
import it.corvallis.MainClass;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.imageprocessing.preprocessing.AbstractPreprocess;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;

import java.util.Arrays;


/**
 * This class performs the binarization of an image by means of thresholding using ImageJ library.
 * 
 * @author ChiarellaG
 *
 */
public class BinarizePreprocess extends AbstractPreprocess {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		;
        
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
	public BinarizePreprocess(PreprocessingType p) {
		super(p);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractPreprocess}) constructor.
	 * @param p The object to copy.
	 */
	public BinarizePreprocess(BinarizePreprocess p) {
		super(p);
	}

	@Override
	public ImagePlus performPreprocess(ImagePlus img, double... params) {
		img.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, img.getProcessor().getHistogram()));
		return img;
	}

}
