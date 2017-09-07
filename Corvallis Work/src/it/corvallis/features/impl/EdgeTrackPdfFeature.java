package it.corvallis.features.impl;

import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.commons.MeasurementUtils;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.filtering.Filter.DirectionType;
import it.corvallis.imageprocessing.measurements.Projections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * The class to compute the chain code according to Hassaine - Al-Maadeed - Bouridane - A set of geometrical features for writer identification. It extends the class {@link AbstractFeature}.
 * 
 * @author ChiarellaG
 *
 */
public class EdgeTrackPdfFeature extends AbstractFeature {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		mask_size("mask_size", "3.0"),
		filled("filled", "false");
        
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
	public EdgeTrackPdfFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public EdgeTrackPdfFeature(EdgeTrackPdfFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		List<Filter.DirectionType> direction_order = new ArrayList<Filter.DirectionType>();
		direction_order.add(DirectionType.UPPER_RIGHT);
		direction_order.add(DirectionType.RIGHT);
		direction_order.add(DirectionType.LOWER_RIGHT);
		direction_order.add(DirectionType.LOWER);
		direction_order.add(DirectionType.LOWER_LEFT);
		direction_order.add(DirectionType.LEFT);
		direction_order.add(DirectionType.UPPER_LEFT);
		direction_order.add(DirectionType.UPPER);
		int[][] edge_track = Filter.edgeTrack(img, direction_order, (int) parameters[0], parameters[1] == 1.0 ? true : false);
		int windows_size = MeasurementUtils.checkWindowDimension((int) parameters[0]);
		int bins = parameters[1] == 1.0 ? ((int) ((windows_size * windows_size - 1.0) / 2.0)) : ((int) ((windows_size - 1.0) * 2.0));
		double[] hist = CollectionUtils.int_to_double(Projections.generateHistogram(CollectionUtils.int_to_double(CollectionUtils.toTrace(edge_track, false)), 1, bins, bins));
		double sum = DoubleStream.of(hist).sum();
		for (int i = 0; i < hist.length; ++i) {
			hist[i] /= sum;
		}
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
