package it.corvallis.features.impl;

import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureFactory;
import it.corvallis.features.FeatureType;

import java.util.Arrays;

/**
 * The class to compute the HeightAreaRatioFeature feature. It extends the class {@link AbstractFeature}.
 * It computes the effective height over area ratio.
 * 
 * @author ChiarellaG
 *
 */
public class SizeAreaRatioFeature extends AbstractFeature {
	
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
	public SizeAreaRatioFeature(FeatureType feat) {
		super(feat);
	}
	
	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public SizeAreaRatioFeature(SizeAreaRatioFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		return new double[]{CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.WidthAreaRatioFeature).extractFeature(img, new double[]{Grid.Uniform.getId(), 1, 1}))[0] * CollectionUtils.objectToDoubleArray(FeatureFactory.getFeature(FeatureType.HeightAreaRatioFeature).extractFeature(img, new double[]{Grid.Uniform.getId(), 1, 1}))[0]};
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
