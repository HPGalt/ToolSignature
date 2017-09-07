package it.corvallis.features.impl;

import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.features.AbstractFeature;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.filtering.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

/**
 * The class to compute the run length feature.
 * 
 * @author ChiarellaG
 *
 */
public class RunLengthPdfFeature extends AbstractFeature {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		histogram_length("histogram_length", "10.0"),
		levels("levels", "2.0");
        
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
	public RunLengthPdfFeature(FeatureType feat) {
		super(feat);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractFeature}) constructor.
	 * 
	 * @param f The object to copy.
	 */
	public RunLengthPdfFeature(RunLengthPdfFeature f) {
		super(f);
	}
	
	@Override
	protected Object computeFeature(ImagePlus img, double... parameters) {
		boolean rotated = false;
		if (img.getWidth() > img.getHeight()) {
			int[][] pixels = img.getProcessor().getIntArray();
			img.setProcessor(img.getProcessor().resize(img.getHeight(), img.getWidth()));
			img.getProcessor().setIntArray(CollectionUtils.transpose(pixels));
			rotated = true;
		}
		HashMap<Integer, int[][][]> run_length = Filter.runLength(img);
		List<Integer> cur_levs = CollectionUtils.getDistinctValues(CollectionUtils.toTrace(img.getProcessor().getIntArray(), true));
		//int defaultLeng = 
		
		int w = img.getWidth();
		int h = img.getHeight();
		int max_length = Math.min((int) (Math.sqrt(w * w + h * h) + 1.0), (int) parameters[0]); // cambio del minimo
		
		List<Integer> levels = new ArrayList<Integer>();
		levels.add(0);
		levels.add(255);
		int[][][] hist = new int[4][cur_levs.get(cur_levs.size() - 1) + 1][max_length];
		for (int i = 0; i < hist.length; ++i) {
			for (int j = 0; j < hist[i].length; ++j) {
				for (int k = 0; k < hist[i][j].length; ++k) {
					hist[i][j][k] = 0;
				}
			}
		}
		for (Integer level : run_length.keySet()) {
			int dir_index = rotated ? 2 : 0;
			// PI/4
			for (int i = 0; i < w; ++i) {
				if (run_length.get(level)[dir_index][0][i] != 0 && run_length.get(level)[dir_index][0][i] < max_length)
					++hist[dir_index][level][run_length.get(level)[dir_index][0][i]];
				for (int j = 1; j < i + 1; ++j) {
					if (run_length.get(level)[dir_index][j][i - j] >= max_length)
						continue;
					if (run_length.get(level)[dir_index][j][i - j] != 0 && run_length.get(level)[dir_index][j][i - j] != run_length.get(level)[dir_index][j - 1][i - j + 1])
						++hist[dir_index][level][run_length.get(level)[dir_index][j][i - j]];
				}
			}
			for (int i = 1; i < h; ++i) {
				if (run_length.get(level)[dir_index][i][w - 1] != 0 && run_length.get(level)[dir_index][i][w - 1] < max_length)
					++hist[dir_index][level][run_length.get(level)[dir_index][i][w - 1]];
				for (int j = w - 2, t = i + 1; j >= 0 && t < h; --j, ++t) {
					if (run_length.get(level)[dir_index][t][j] >= max_length)
						continue;
					if (run_length.get(level)[dir_index][t][j] != 0 && run_length.get(level)[dir_index][t][j] != run_length.get(level)[dir_index][t - 1][j + 1])
						++hist[dir_index][level][run_length.get(level)[dir_index][t][j]];
				}
			}
			// PI/2
			dir_index = (dir_index + 1) % 4;
			for (int i = 0; i < w; ++i) {
				if (run_length.get(level)[dir_index][0][i] != 0 && run_length.get(level)[dir_index][0][i] < max_length)
					++hist[dir_index][level][run_length.get(level)[dir_index][0][i]];
				for (int j = 1; j < h; ++j) {
					if (run_length.get(level)[dir_index][j][i] >= max_length)
						continue;
					if (run_length.get(level)[dir_index][j][i] != 0 && run_length.get(level)[dir_index][j][i] != run_length.get(level)[dir_index][j - 1][i])
						++hist[dir_index][level][run_length.get(level)[dir_index][j][i]];
				}
			}
			// 3PI/4
			dir_index = (dir_index + 1) % 4;
			for (int i = 0; i < w; ++i) {
				if (run_length.get(level)[dir_index][0][i] != 0 && run_length.get(level)[dir_index][0][i] < max_length)
					++hist[dir_index][level][run_length.get(level)[dir_index][0][i]];
				for (int j = i + 1, t = 1; j < w && t < h - i; ++j, ++t) {
					if (run_length.get(level)[dir_index][t][j] >= max_length)
						continue;
					if (run_length.get(level)[dir_index][t][j] != 0 && run_length.get(level)[dir_index][t][j] != run_length.get(level)[dir_index][t - 1][j - 1])
						++hist[dir_index][level][run_length.get(level)[dir_index][t][j]];
				}
			}
			for (int i = 1; i < h; ++i) {
				if (run_length.get(level)[dir_index][i][0] != 0 && run_length.get(level)[dir_index][i][0] < max_length)
					++hist[dir_index][level][run_length.get(level)[dir_index][i][0]];
				for (int j = i + 1, t = 1; j < h && t < w; ++j, ++t) {
					if (run_length.get(level)[dir_index][j][t] >= max_length)
						continue;
					if (run_length.get(level)[dir_index][j][t] != 0 && run_length.get(level)[dir_index][j][t] != run_length.get(level)[dir_index][j - 1][t - 1])
						++hist[dir_index][level][run_length.get(level)[dir_index][j][t]];
				}
			}
			// PI
			dir_index = (dir_index + 1) % 4;
			for (int i = 0; i < h; ++i) {
				if (run_length.get(level)[dir_index][i][0] != 0 && run_length.get(level)[dir_index][i][0] < max_length)
					++hist[dir_index][level][run_length.get(level)[dir_index][i][0]];
				for (int j = 1; j < w; ++j) {
					if (run_length.get(level)[dir_index][i][j] >= max_length)
						continue;
					if (run_length.get(level)[dir_index][i][j] != 0 && run_length.get(level)[dir_index][i][j] != run_length.get(level)[dir_index][i][j - 1])
						++hist[dir_index][level][run_length.get(level)[dir_index][i][j]];
				}
			}
		}
		
		/* List<Double> histogram = new ArrayList<Double>();
		for (int i = 0; i < hist.length; ++i) {
		    for (int j = 0; j < levels.size(); ++j) {
		        if (!CollectionUtils.isContained(levels, j) && !CollectionUtils.isContained(cur_levs, j))
		            continue;
		        histogram.addAll(Arrays.asList(ArrayUtils.toObject(CollectionUtils.int_to_double(hist[i][j]))));
		    }
		}
		return  histogram.toArray();*/
		
		/* SAREBBE IL METODO NOVO MA NON FUNZIONA! (forse) */
		List<Double> histogram = new ArrayList<Double>();
		 
		for (int i = 0; i < hist.length; ++i) {
		    for (int j = 0; j < cur_levs.size(); ++j) {
		        if (!CollectionUtils.isContained(levels, cur_levs.get(j)))
		            continue;
		        histogram.addAll(Arrays.asList(ArrayUtils.toObject(CollectionUtils.int_to_double(hist[i][cur_levs.get(j)]))));
		    }
		}
		return ArrayUtils.toPrimitive(histogram.toArray(new Double[0]));
	}
	
}
