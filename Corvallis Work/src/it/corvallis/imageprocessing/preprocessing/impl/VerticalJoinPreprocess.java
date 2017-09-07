package it.corvallis.imageprocessing.preprocessing.impl;

import ij.ImagePlus;
import it.corvallis.MainClass;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.preprocessing.AbstractPreprocess;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;

/**
 * This class performs the removing of white rows of an image by means of #{@link Filter} class methods.
 * 
 * @author ChiarellaG
 *
 */
public class VerticalJoinPreprocess extends AbstractPreprocess {
	
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
	public VerticalJoinPreprocess(PreprocessingType p) {
		super(p);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractPreprocess}) constructor.
	 * @param p The object to copy.
	 */
	public VerticalJoinPreprocess(VerticalJoinPreprocess p) {
		super(p);
	}

	@Override
	public ImagePlus performPreprocess(ImagePlus img, double... params) {
		ImagePlus res = new ImagePlus("", img.getProcessor().resize(img.getHeight(), img.getWidth()));
		res.getProcessor().setIntArray(CollectionUtils.transpose(img.getProcessor().getIntArray()));
		res = Filter.join(res);
		ImagePlus dup = new ImagePlus("", res.getProcessor().resize(res.getHeight(), res.getWidth()));
		dup.getProcessor().setIntArray(CollectionUtils.transpose(res.getProcessor().getIntArray()));
		return dup;
	}

	

	
}
