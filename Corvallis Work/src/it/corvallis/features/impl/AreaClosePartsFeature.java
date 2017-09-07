package it.corvallis.features.impl;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;

import java.util.Arrays;

/**
 * The class to compute the AreaCloseParts. It extends the class {@link AbstractFeature}.
 * 
 * @author ChiarellaG
 *
 */
public class AreaClosePartsFeature extends AbstractFeature {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		minimum_hole_size("minimum_hole_size", "0.0");
        
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
	public AreaClosePartsFeature(FeatureType feat) {
		super(feat);
	}
	
	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public AreaClosePartsFeature(AreaClosePartsFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		IJ.run(img, "Fill Holes", "");
		IJ.run(img, "Open", "");
		
		IJ.run("Set Measurements...", "area perimeter redirect=None decimal=3");
		IJ.run(img, "Analyze Particles...", "size=" + (int) parameters[0] + "-Infinity circularity=0.00-1.00 show=Nothing clear");
//		ResultsTable rt = new ResultsTable();
//      ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_MASKS | ParticleAnalyzer.CLEAR_WORKSHEET, Measurements.PERIMETER, rt, 0, Double.POSITIVE_INFINITY, 0, 1);
//      pa.setHideOutputImage(true);
//      pa.analyze(img);
//      img = pa.getOutputImage();
//		return Hu.area(img, 0, 0, img.getWidth(), img.getHeight()) / (double) (img.getWidth() * img.getHeight());
		if (ResultsTable.getResultsTable().columnExists(ResultsTable.AREA)) {
			double[] areasCloseParts = ResultsTable.getResultsTable().getColumnAsDoubles(ResultsTable.AREA);
	
			// Calcolo l'area totale e la salvo nella corrisponendente variabile
			// di classe
			double areaTot = 0.0;
			if (areasCloseParts != null) {
				for (int i = 0; i < areasCloseParts.length; i++) {
					areaTot = areaTot + areasCloseParts[i];
				}
			}
			return new double[]{areaTot / (double) (img.getWidth() * img.getHeight())};
		} else {
			return new double[]{getType().getDefaultValue()};
		}
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
