package it.corvallis.imageprocessing.preprocessing.impl;

import ij.ImagePlus;
import it.corvallis.MainClass;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.imageprocessing.preprocessing.AbstractPreprocess;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;


/**
 * This class performs the mean smoothing of an image by means of Filter class.
 * 
 * @author ChiarellaG
 *
 */
public class PadPreprocess extends AbstractPreprocess {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		pad_size("pad_size", "3"),
		padding_value("padding_value", "255");
        
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
	public PadPreprocess(PreprocessingType p) {
		super(p);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractPreprocess}) constructor.
	 * @param p The object to copy.
	 */
	public PadPreprocess(PadPreprocess p) {
		super(p);
	}

	@Override
	public ImagePlus performPreprocess(ImagePlus img, double... params) {
		int pad_size = (int) params[0];
		int padding_value = (int) Math.abs(params[1]) >= 255.0 ? 255 : (int) Math.abs(params[1]);
		int[][] pixels = CollectionUtils.transpose(img.getProcessor().getIntArray());
		int[][] new_pixels = new int[pixels.length + 2 * pad_size][pixels[0].length + 2 * pad_size];
		for (int i = 0; i < new_pixels.length; ++i) {
            for (int j = 0; j < new_pixels[i].length; ++j) {
                if (i >= pad_size && i < new_pixels.length - pad_size && j >= pad_size && j < new_pixels[i].length - pad_size)
                	new_pixels[i][j] = pixels[i - pad_size][j - pad_size];
                else
                	new_pixels[i][j] = padding_value;
            }
        }
		img.setProcessor(img.getProcessor().resize(new_pixels[0].length, new_pixels.length));
		img.getProcessor().setIntArray(CollectionUtils.transpose(new_pixels));
		return img;
	}

}
