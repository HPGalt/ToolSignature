package it.corvallis.features.impl;

import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.measurements.Projections;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * The class to compute directions feature according to Hassaine - Al-Maadeed - Bouridane - A set of geometrical features for writer identification. It extends the class {@link AbstractFeature}.
 * The algorithm is provided by means of the {@link Filter#crossFinder} method.
 * 
 * @author ChiarellaG
 *
 */
public class DirectionsPdfFeature extends AbstractFeature {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		smoothing_window("smoothing_window", "0.0"),
		neighbor_size("neighbor_size", "1.0"),
		maximum_linear_regression_error("maximum_linear_regression_error", "1.1"),
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
	public DirectionsPdfFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public DirectionsPdfFeature(DirectionsPdfFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		List<Pair<Double, Pair<List<Integer>, List<Integer>>>> segments = Filter.directions(img, (int) parameters[0], (int) parameters[1], (int) parameters[2]);
		double[] angle = new double[segments.size()];
		for (int i = 0; i < angle.length; ++i) {
			angle[i] = segments.get(i).getLeft();
		}
		double[] hist = CollectionUtils.int_to_double(Projections.generateHistogram(angle, 0.0, Math.PI, (int) parameters[3]));
		return hist;
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
