package it.corvallis.features;

import ij.ImagePlus;
import ij.gui.NewImage;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.commons.MeasurementUtils;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.measurements.Hu;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * AbstractFeature is the base class that a new feature have to extend. It implements all boundary needs to compute a feature delegating the core algorithm
 * to an abstract method to implement in the child class.
 * 
 * @author ChiarellaG
 *
 */
public abstract class AbstractFeature {

	public enum Grid {
		Uniform("uniform", 0),
		CentroidBased("CentroidBased", 1),
		Polar("polar", 2);
		
		private String name = null;
        private int id = 0;
        
        private Grid(String name, int id) {
            this.name = name.trim().toLowerCase();
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public int getId() {
            return id;
        }
        
        /**
         * Convert a string into a Preprocessing, if the string reflect a known classifier.
         * @param type The string to convert.
         * @return The string correspondent enum of BaseClassifierType, null if the string doesn't reflect a known classifier.
         */
        public static Grid getGridFromString(String type) {
        	for (int i = 0; i < Grid.values().length; ++i) {
				if (type.trim().toLowerCase().compareTo(Grid.values()[i].getName()) == 0)
					return Grid.values()[i];
			}
        	return Uniform;
        }
        
        /**
         * Convert an id into a Preprocessing, if the id reflect a known preprocessing type.
         * @param id The id to convert.
         * @return The string correspondent enum of BaseClassifierType, null if the string doesn't reflect a known classifier.
         */
        public static Grid getTypeFromId(int id) {
        	for (int i = 0; i < Grid.values().length; ++i) {
				if (id == Grid.values()[i].getId())
					return Grid.values()[i];
			}
        	return Uniform;
        }
	}

	/**
	 * This enumerator defines all base properties of a feature.
	 * The pattern is the follow:
	 * 			NAME(string_name_to_use_in_the_properties_file, default_value_used_if_the_property_is_not_defined_in_the_properties_file)
	 * 
	 * @author ChiarellaG
	 *
	 */
	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		preprocessings("preprocessings", "{}"),
		dependencies("dependencies", "{}"),
		combining_operations("combining_operations", "{}"),
		grid_type("grid_type", Grid.Uniform.getName()),
		grid_cell_number("grid_cell_number", "64"),
		grid_ellipse_number("grid_ellipse_number", "3"),
		grid_angle_bins("grid_angle_bins", "12"),
		grid_rows("grid_rows", "1"),
		grid_cols("grid_cols", "1");
        
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
	
	protected Logger log = Logger.getLogger(AbstractFeature.class.getName());
	private FeatureType feature = null;

	/**
	 * Default constructor. Do nothing.
	 * @param f The feature type.
	 */
	public AbstractFeature(FeatureType f) {
		feature = f;
	}
	
	/**
	 * Copy constructor. Do nothing.
	 * @param f The feature object to copy.
	 */
	public AbstractFeature(AbstractFeature f) {
		feature = f.feature;
	}
	
	/**
	 * It is the method to call to compute the feature.
	 * It calls {@link #computeFeature(ImagePlus, double...)} before {@link #checkObject(Object)} returning the result of the latter.
	 * @param img The input image onto which compute the feature.
	 * @param parameters The parameters object.
	 * @return The object representing the computed feature.
	 */
	public Object extractFeature(ImagePlus img, double... parameters) {
		int grid_id = (int) parameters[0];
		double[] pars = new double[parameters.length - 1];
		System.arraycopy(parameters, 1, pars, 0, pars.length);
		List<Double> values = null;
		switch (Grid.getTypeFromId(grid_id)) {
			case CentroidBased:
				values = centroidBasedGrid(img.duplicate(), pars);
				break;
			case Polar:
				values = polarBasedGrid(img.duplicate(), pars);
				break;
			case Uniform:
				values = uniformGrid(img.duplicate(), pars);
			default:
		}
		return values.toArray(new Double[0]);
	}
	
	private List<Double> uniformGrid(ImagePlus img, double... parameters) {
		int grid_cols = (int) parameters[0];
		int grid_rows = (int) parameters[1];
		double[] pars = new double[parameters.length - 2];
		System.arraycopy(parameters, 2, pars, 0, pars.length);
		List<Double> values = new ArrayList<Double>();
		int cell_width = (int) ((double) img.getWidth() / (double) grid_cols);
		int cell_height = (int) ((double) img.getHeight() / (double) grid_rows);
		for (int i = 0; i < grid_rows; ++i) {
			int top_left_y;
			int height;
			top_left_y = i * cell_height;
			if (i < grid_rows - 1)
				height = cell_height;
			else
				height = img.getHeight() - top_left_y;
			for (int j = 0; j < grid_cols; ++j) {
				int top_left_x;
				int width;
				top_left_x = j * cell_width;
				if (j < grid_cols - 1)
					width = cell_width;
				else
					width = img.getWidth() - top_left_x;
				ImagePlus roi = img.duplicate();
				roi.setRoi(new Rectangle(top_left_x, top_left_y, width, height));
				roi = new ImagePlus(i + "_" + j, roi.getProcessor().crop());
				Object val = checkObject(computeFeature(roi, CollectionUtils.deep_copy(pars)));
				double[] temp = CollectionUtils.objectToDoubleArray(val);
				values.addAll(Arrays.asList(ArrayUtils.toObject(temp)));
			}
		}
		return values;
	}
	
	private List<Double> centroidBasedGrid(ImagePlus img, double... parameters) {
		int number_of_cells = (int) Math.pow(4, MeasurementUtils.nearestForthPower((int) parameters[0]));
		double[] pars = new double[parameters.length - 1];
		System.arraycopy(parameters, 1, pars, 0, pars.length);
		List<Double> values = new ArrayList<Double>();
		List<ImagePlus> cur_cells = new ArrayList<ImagePlus>();
		cur_cells.add(img.duplicate());
		do {
			List<ImagePlus> new_cells = new ArrayList<ImagePlus>();
			for (int j = 0; j < cur_cells.size(); ++j) {
				int segmentWidth = cur_cells.get(j).getWidth();
				int segmentHeight = cur_cells.get(j).getHeight();
				double[] segmentCentreOfGravity = Hu.getCentreOfGravity(cur_cells.get(j), 0, 0, segmentWidth, segmentHeight);
				int separatorX = (int) Math.round(segmentCentreOfGravity[0]);
				int separatorY = (int) Math.round(segmentCentreOfGravity[1]);
				Rectangle bounds = new Rectangle(0, 0, separatorX, separatorY);
				ImagePlus roi = cur_cells.get(j).duplicate();
				roi.setRoi(bounds);
				roi = new ImagePlus("", roi.getProcessor().crop());
				new_cells.add(roi);
				bounds = new Rectangle(separatorX, 0, segmentWidth - separatorX, separatorY);
				roi = cur_cells.get(j).duplicate();
				roi.setRoi(bounds);
				roi = new ImagePlus("", roi.getProcessor().crop());
				new_cells.add(roi);
				bounds = new Rectangle(0, separatorY, separatorX, segmentHeight - separatorY);
				roi = cur_cells.get(j).duplicate();
				roi.setRoi(bounds);
				roi = new ImagePlus("", roi.getProcessor().crop());
				new_cells.add(roi);
				bounds = new Rectangle(separatorX, separatorY, segmentWidth - separatorX, segmentHeight - separatorY);
				roi = cur_cells.get(j).duplicate();
				roi.setRoi(bounds);
				roi = new ImagePlus("", roi.getProcessor().crop());
				new_cells.add(roi);
			}
			cur_cells = new_cells;
		} while (cur_cells.size() < number_of_cells);
		for (int i = 0; i < cur_cells.size(); ++i) {
			Object val = checkObject(computeFeature(cur_cells.get(i), CollectionUtils.deep_copy(pars)));
			double[] temp = CollectionUtils.objectToDoubleArray(val);
			values.addAll(Arrays.asList(ArrayUtils.toObject(temp)));
		}
		return values;
	}
	
	private List<Double> polarBasedGrid(ImagePlus img, double... parameters) {
		int grid_ellipse_number = (int) parameters[0];
		int grid_angle_bins = (int) parameters[1];
		double[] pars = new double[parameters.length - 2];
		System.arraycopy(parameters, 2, pars, 0, pars.length);
		List<Double> values = new ArrayList<Double>();
		int x_centre = (int) ((double) img.getWidth() / 2.0);
		int y_centre = (int) ((double) img.getHeight() / 2.0);
		double y_ellipse_step = ((double) img.getHeight() / 2.0) / (double) grid_ellipse_number;
		double x_ellipse_step = ((double) img.getWidth() / 2.0) / (double) grid_ellipse_number;
		double angle_step = (Math.PI * 2.0) / (double) grid_angle_bins;
		for (int i = 0; i < grid_ellipse_number; ++i) {
			double a_min = x_ellipse_step * i;
			double a_max = x_ellipse_step * (i + 1);
			double b_min = y_ellipse_step * i;
			double b_max = y_ellipse_step * (i + 1);
			for (int j = 0; j < grid_angle_bins; ++j) {
				double r = (a_min * b_min) / Math.sqrt(Math.pow(a_min * Math.sin(angle_step * (double) j), 2.0) + Math.pow(b_min * Math.cos(angle_step * (double) j), 2.0));
				int x1 = x_centre + (int) (r * Math.cos(angle_step * (double) j));
				int y1 = y_centre - (int) (r * Math.sin(angle_step * (double) j));
				r = (a_min * b_min) / Math.sqrt(Math.pow(a_min * Math.sin(angle_step * (double) (j + 1)), 2.0) + Math.pow(b_min * Math.cos(angle_step * (double) (j + 1)), 2.0));
				int x3 = x_centre + (int) (r * Math.cos(angle_step * (double) (j + 1)));
				int y3 = y_centre - (int) (r * Math.sin(angle_step * (double) (j + 1)));
				r = (a_max * b_max) / Math.sqrt(Math.pow(a_max * Math.sin(angle_step * (double) j), 2.0) + Math.pow(b_max * Math.cos(angle_step * (double) j), 2.0));
				int x6 = x_centre + (int) (r * Math.cos(angle_step * (double) j));
				int y6 = y_centre - (int) (r * Math.sin(angle_step * (double) j));
				r = (a_max * b_max) / Math.sqrt(Math.pow(a_max * Math.sin(angle_step * (double) (j + 1)), 2.0) + Math.pow(b_max * Math.cos(angle_step * (double) (j + 1)), 2.0));
				int x4 = x_centre + (int) (r * Math.cos(angle_step * (double) (j + 1)));
				int y4 = y_centre - (int) (r * Math.sin(angle_step * (double) (j + 1)));
				r = (a_max * b_max) / Math.sqrt(Math.pow(a_max * Math.sin(angle_step * (double) j + (angle_step * (double) (j + 1) - angle_step * (double) j) / 2.0), 2.0) + Math.pow(b_max * Math.cos(angle_step * (double) j + (angle_step * (double) (j + 1) - angle_step * (double) j) / 2.0), 2.0));
				int x5 = x_centre + (int) (r * Math.cos(angle_step * (double) j + (angle_step * (double) (j + 1) - angle_step * (double) j) / 2.0));
				int y5 = y_centre - (int) (r * Math.sin(angle_step * (double) j + (angle_step * (double) (j + 1) - angle_step * (double) j) / 2.0));
				r = (a_min * b_min) / Math.sqrt(Math.pow(a_min * Math.sin(angle_step * (double) j + (angle_step * (double) (j + 1) - angle_step * (double) j) / 2.0), 2.0) + Math.pow(b_min * Math.cos(angle_step * (double) j + (angle_step * (double) (j + 1) - angle_step * (double) j) / 2.0), 2.0));
				int x2 = x_centre + (int) (r * Math.cos(angle_step * (double) j + (angle_step * (double) (j + 1) - angle_step * (double) j) / 2.0));
				int y2 = y_centre - (int) (r * Math.sin(angle_step * (double) j + (angle_step * (double) (j + 1) - angle_step * (double) j) / 2.0));
				List<Integer> x = new ArrayList<Integer>();
				List<Integer> y = new ArrayList<Integer>();
				x.add(x1); y.add(y1);
				x.add(x2); y.add(y2);
				x.add(x3); y.add(y3);
				x.add(x4); y.add(y4);
				x.add(x5); y.add(y5);
				x.add(x6); y.add(y6);
				Polygon p = new Polygon(ArrayUtils.toPrimitive(x.toArray(new Integer[0])), ArrayUtils.toPrimitive(y.toArray(new Integer[0])), x.size());
				ImagePlus roi = NewImage.createByteImage(i + "_" + j, img.getWidth(), img.getHeight(), 1, NewImage.FILL_WHITE);
				roi.getProcessor().setColor(0);
				roi.getProcessor().fillPolygon(p);
				roi = Filter.and(img, roi, 255, 0);
				Object val = checkObject(computeFeature(roi, CollectionUtils.deep_copy(pars)));
				double[] temp = CollectionUtils.objectToDoubleArray(val);
				values.addAll(Arrays.asList(ArrayUtils.toObject(temp)));
			}
		}
		return values;
	}
	
	/**
	 * Gets method for the feature type.
	 * @return The feature type.
	 */
	public FeatureType getType() {
		return feature;
	}
	
	/**
	 * This method has to implement the feature algorithm.
	 * @param img The input image onto which compute the feature.
	 * @param parameters The parameters object.
	 * @return The object representing the value of the computed feature.
	 */
	protected abstract Object computeFeature(ImagePlus img, double... parameters);
	
	/**
	 * This method checks the value of the feature. If you wanted to check the feature value you need to override this method.
	 * @param val The value Object to check.
	 * @return The checked value Object
	 */
	protected Object checkObject(Object val) {
		return val;
	}

}
