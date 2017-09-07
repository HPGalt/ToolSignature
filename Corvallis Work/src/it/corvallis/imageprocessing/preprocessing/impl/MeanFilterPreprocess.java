package it.corvallis.imageprocessing.preprocessing.impl;

import ij.ImagePlus;
import it.corvallis.MainClass;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.commons.MeasurementUtils;
import it.corvallis.imageprocessing.preprocessing.AbstractPreprocess;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;


/**
 * This class performs the mean smoothing of an image by means of Filter class.
 * 
 * @author ChiarellaG
 *
 */
public class MeanFilterPreprocess extends AbstractPreprocess {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		kernel_size("kernel_size", "3");
        
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
	public MeanFilterPreprocess(PreprocessingType p) {
		super(p);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractPreprocess}) constructor.
	 * @param p The object to copy.
	 */
	public MeanFilterPreprocess(MeanFilterPreprocess p) {
		super(p);
	}

	@Override
	public ImagePlus performPreprocess(ImagePlus img, double... params) {
		//int kernel_size = (int) Math.abs(params[0]);
		int kernel_size = 3;
		kernel_size = MeasurementUtils.checkWindowDimension(kernel_size);
		double[][] kernel = new double[kernel_size][kernel_size];
		for (int i = 0; i < kernel.length; ++i) {
			for (int j = 0; j < kernel[i].length; ++j) {
				kernel[i][j] = 1.0 / (double) (kernel_size * kernel_size);
			}
		}
		img.getProcessor().setIntArray(CollectionUtils.double_to_int(CollectionUtils.transpose(CollectionUtils.convolve2D(CollectionUtils.transpose(CollectionUtils.int_to_double(img.getProcessor().getIntArray())), kernel, true, 255)), false));
		return img;
	}

}
