package it.corvallis.features.impl;

import ij.ImagePlus;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureFactory;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.measurements.Hu;

import java.util.Arrays;

/**
 * The class to compute the AreaRatio feature. It extends the class {@link AbstractFeature}.
 * The algorithm is provided by means of {@link Hu} moments, dividing the signature area to the convex hull area.
 * 
 * @author ChiarellaG
 *
 */
public class AreaRatioFeature extends AbstractFeature {

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
	public AreaRatioFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public AreaRatioFeature(AreaRatioFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		double[] val = new double[1];
		val[0] = ((double[]) FeatureFactory.getFeature(FeatureType.AreaFeature).extractFeature(img, new double[]{Grid.Uniform.getId(), 1, 1}))[0];
		val[0] /= ((double[]) FeatureFactory.getFeature(FeatureType.ConvexHullAreaFeature).extractFeature(img, new double[]{Grid.Uniform.getId(), 1, 1}))[0];
		return val;
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
