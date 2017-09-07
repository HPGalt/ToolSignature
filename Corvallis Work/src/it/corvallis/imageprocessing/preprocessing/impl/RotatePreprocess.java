package it.corvallis.imageprocessing.preprocessing.impl;

import ij.ImagePlus;
import ij.process.AutoThresholder;
import it.corvallis.MainClass;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.preprocessing.AbstractPreprocess;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;

//import old.verificafirme.Utils;
//import old.verificafirme.preprocess.PreprocessingType;


/**
 * This class performs the rotation of an image by means of #{@link Filter} class methods.
 * 
 * @author ChiarellaG
 *
 */
public class RotatePreprocess extends AbstractPreprocess {
	
	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		angle("angle", "0.0"),
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
	public RotatePreprocess(PreprocessingType p) {
		super(p);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractPreprocess}) constructor.
	 * @param p The object to copy.
	 */
	public RotatePreprocess(RotatePreprocess p) {
		super(p);
	}

	@Override
	public ImagePlus performPreprocess(ImagePlus img, double... params) {
		double angle = params[0];
		boolean threshold = params[1] > 0.0 ? true : false;
		if (threshold)
			img.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, img.getProcessor().getHistogram()));
		img.getProcessor().rotate(angle);
		return img;
	}

}
