package it.corvallis.features.impl;

import ij.ImagePlus;
import ij.process.AutoThresholder;
import ij.process.AutoThresholder.Method;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.measurements.ConvexHullProcessor;
import it.corvallis.imageprocessing.measurements.Hu;

import java.util.Arrays;

/**
 * The class to compute the Convex Hull Area. It extends the class {@link AbstractFeature}.
 * It computes the convex hull image then computes the area of the convex hull image.
 * 
 * @author ChiarellaG
 *
 */
public class ConvexHullAreaFeature extends AbstractFeature {
	
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
	public ConvexHullAreaFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public ConvexHullAreaFeature(ConvexHullAreaFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		ConvexHullProcessor convHull = new ConvexHullProcessor();
		convHull.setup("", img);
		convHull.run(img.getProcessor());

		// Bisogna ribinarizzare l'immagine
		AutoThresholder th = new AutoThresholder();
		int threshold = th.getThreshold(Method.Otsu, img.getProcessor().getHistogram());
		img.getProcessor().threshold(threshold);
		return new double[]{Hu.area(img, 0, 0, img.getWidth(), img.getHeight()) / (double) (img.getWidth() * img.getHeight())};
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
