package it.corvallis.features.impl;

import ij.IJ;
import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.measurements.CentroidProcessor;
import it.corvallis.imageprocessing.measurements.Projections;

import java.util.Arrays;


/**
 * The class to compute the curvature feature according to Hassaine - Al-Maadeed - Bouridane - A set of geometrical features for writer identification. It extends the class {@link AbstractFeature}.
 * The algorithm is provided by means of {@link CentroidProcessor} class.
 * 
 * @author ChiarellaG
 *
 */
public class CurvaturePdfFeature extends AbstractFeature {
	
	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		window_width("Window_Width", "3.0"),
		window_height("Window_Height", "3.0"),
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
	public CurvaturePdfFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public CurvaturePdfFeature(CurvaturePdfFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		ImagePlus t = Filter.erode(img, new int[][]{{1,1,1},{1,1,1},{1,1,1}});
		double[][] curvature = Filter.curvature(t, (int) parameters[0], (int) parameters[1]);

		
		
		double[] hist = CollectionUtils.int_to_double(Projections.generateHistogram(CollectionUtils.toTrace(curvature, false), -1.0, 1.0, (int) parameters[2]));
		for (int i = 0; i < hist.length; ++i) {
			hist[i] /= (double) (curvature.length * curvature[0].length);
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
