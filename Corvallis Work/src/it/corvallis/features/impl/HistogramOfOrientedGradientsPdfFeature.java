package it.corvallis.features.impl;

import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.filtering.Filter;

import java.util.Arrays;

/**
 * The class to compute the Histogram of Oriented Gradients. It extends the class {@link AbstractFeature}.
 * The algorithm is provided by means of the {@link Filter#histogramOfGradient(ImagePlus, int, int, double, int)} method.
 * 
 * @author ChiarellaG
 *
 */
public class HistogramOfOrientedGradientsPdfFeature extends AbstractFeature {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		vertical_block_number("vertical_block_number", "1.0"),
		horizontal_block_number("horizontal_block_number", "1.0"),
		overlap("overlap", "0.5"),
		pdf_bins("pdf_bins", "10.0");
        
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
	 * @param feat The feature type.
	 */
	public HistogramOfOrientedGradientsPdfFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public HistogramOfOrientedGradientsPdfFeature(HistogramOfOrientedGradientsPdfFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		double[][] h = Filter.histogramOfGradient(img, (int) parameters[0], (int) parameters[1], (int) parameters[2], (int) parameters[3]);
		return CollectionUtils.toTrace(h, false);
	}

	@Override
	protected Object checkObject(Object val) {
		double[] new_val = new double[getType().getNumComponents()];
		Arrays.fill(new_val, getType().getDefaultValue());
		if (val != null && val instanceof double[]) {
			for (int i = 0; i < Math.min(new_val.length, ((double[]) val).length); ++i) {
				if (Double.isFinite(((double[]) val)[i]))
					new_val[i] = ((double[]) val)[i];
			}
		}
		return new_val;
	}
}
