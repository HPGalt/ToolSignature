package it.corvallis.features.impl;

import ij.IJ;
import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.filtering.Filter.DirectionType;
import it.corvallis.imageprocessing.measurements.Projections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import org.apache.commons.lang3.ArrayUtils;

/**
 * The class to compute the chain code according to Hassaine - Al-Maadeed - Bouridane - A set of geometrical features for writer identification. It extends the class {@link AbstractFeature}.
 * 
 * @author ChiarellaG
 *
 */
public class ChainCodePdfFeature extends AbstractFeature {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {
		connection8("connection8", "true"),
		pdf_pattern_dimension("pdf_pattern_dimension", "2.0");
        
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
	public ChainCodePdfFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public ChainCodePdfFeature(ChainCodePdfFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		List<Filter.DirectionType> direction_order = new ArrayList<Filter.DirectionType>();
		if (parameters[0] == 1.0) {
			direction_order.add(DirectionType.UPPER_RIGHT);
			direction_order.add(DirectionType.RIGHT);
			direction_order.add(DirectionType.LOWER_RIGHT);
			direction_order.add(DirectionType.LOWER);
			direction_order.add(DirectionType.LOWER_LEFT);
			direction_order.add(DirectionType.LEFT);
			direction_order.add(DirectionType.UPPER_LEFT);
			direction_order.add(DirectionType.UPPER);
		} else {
			direction_order.add(DirectionType.RIGHT);
			direction_order.add(DirectionType.LOWER);
			direction_order.add(DirectionType.LEFT);
			direction_order.add(DirectionType.UPPER);
		}
		List<Integer> chain = Filter.chainCode(img, direction_order).getRight();
		int pdfDim = (int)parameters[1];
		double[] hist = new double[(int) Math.pow(direction_order.size(), pdfDim)];
		Arrays.fill(hist, 0.0);
		
		StringBuilder chainSB = new StringBuilder();
		for (int i = 0; i < chain.size(); i++)
			chainSB = chainSB.append(chain.get(i).toString());
		String chainString = chainSB.toString();
		switch (pdfDim) {
		case 1:
			for (int i = 0; i < direction_order.size(); i++){
				StringBuilder builder = new StringBuilder();
				hist[i] = chainString.split(builder.append(i+1).toString()).length-1;
			}
			break;
		case 2:
			for (int i = 0; i < direction_order.size(); i++)
				for (int j = 0; j < direction_order.size(); j++){
					StringBuilder builder = new StringBuilder();
					hist[direction_order.size()*i+j] = chainString.split(builder.append(i+1).append(j+1).toString()).length-1;
				}
			break;
		case 3:
			for (int i = 0; i < direction_order.size(); i++)
				for (int j = 0; j < direction_order.size(); j++)
					for (int k = 0; k < direction_order.size(); k++){
						StringBuilder builder = new StringBuilder();
						hist[direction_order.size()*direction_order.size()*i+direction_order.size()*j+k] = chainString.split(builder.append(i+1).append(j+1).append(k+1).toString()).length-1;
					}
			break;
		case 4:
			for (int i = 0; i < direction_order.size(); i++)
				for (int j = 0; j < direction_order.size(); j++)
					for (int k = 0; k < direction_order.size(); k++)
						for (int l = 0; l < direction_order.size(); l++){
							StringBuilder builder = new StringBuilder();
							hist[direction_order.size()*direction_order.size()*direction_order.size()*i+direction_order.size()*direction_order.size()*j+direction_order.size()*k+l] = chainString.split(builder.append(i+1).append(j+1).append(k+1).append(l+1).toString()).length-1;
						}
			break;
		default:
			break;
		}
		
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
