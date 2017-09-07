package it.corvallis.features.impl;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.AutoThresholder;
import ij.process.AutoThresholder.Method;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.measurements.ConvexHullProcessor;

import java.util.Arrays;

/**
 * The class to compute the Convex Hull Perimeter. It extends the class {@link AbstractFeature}.
 * It computes the convex hull image then computes the perimeter of the convex hull image using ImageJ library.
 * 
 * @author ChiarellaG
 *
 */
public class ConvexHullPerimeterFeature extends AbstractFeature {
	
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
	public ConvexHullPerimeterFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public ConvexHullPerimeterFeature(ConvexHullPerimeterFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		// Bisogna ribinarizzare l'immagine
		ConvexHullProcessor convHull = new ConvexHullProcessor();
		convHull.setup("", img);
		convHull.run(img.getProcessor());
		AutoThresholder th = new AutoThresholder();
		int threshold = th.getThreshold(Method.Otsu, img.getProcessor().getHistogram());
		img.getProcessor().threshold(threshold);
		
		
		IJ.run("Set Measurements...", "area perimeter redirect=None decimal=3");
		IJ.run(img, "Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear");
		ResultsTable rt = ResultsTable.getResultsTable();
		/*ResultsTable rt = new ResultsTable();
        ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_MASKS | ParticleAnalyzer.CLEAR_WORKSHEET, Measurements.PERIMETER, rt, 0, Double.POSITIVE_INFINITY, 0, 1);
        pa.setHideOutputImage(true);
        pa.analyze(img);*/
        
        
		double p = 0;
		double[] perimeterCloseParts = rt.getColumnAsDoubles(ResultsTable.PERIMETER);
		for (int i = 0; i < perimeterCloseParts.length; i++) {
			p = p + perimeterCloseParts[i];
		}

		return new double[]{p / (2.0 * ((double) img.getWidth() + (double) img.getHeight()))};
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
