package it.corvallis.features.impl;

import ij.ImagePlus;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.measurements.Hu;
import it.corvallis.imageprocessing.preprocessing.PreProcessingFactory;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;

import java.util.Arrays;

/**
 * The class to compute the HeightAreaRatioFeature feature. It extends the class {@link AbstractFeature}.
 * It computes the effective height over area ratio.
 * 
 * @author ChiarellaG
 *
 */
public class HeightAreaRatioFeature extends AbstractFeature {
	
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
	 * @param feat The feature type.
	 */
	public HeightAreaRatioFeature(FeatureType feat) {
		super(feat);
	}
	
	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public HeightAreaRatioFeature(HeightAreaRatioFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		double area = Hu.area(img, 0, 0, img.getWidth(), img.getHeight());
		if (area == 0.0)
			return new double[]{getType().getDefaultValue()};
		img = PreProcessingFactory.getPreProcess(PreprocessingType.VerticalJoin).execute(img, new double[0]);
		return new double[] {(double) (img.getHeight()) / area};
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
