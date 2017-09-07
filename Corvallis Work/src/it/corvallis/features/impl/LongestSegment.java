package it.corvallis.features.impl;

//import java.awt.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.imageprocessing.preprocessing.PreProcessingFactory;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;
import it.corvallis.ml.dataprocessing.DataUtils;

public class LongestSegment implements Comparable<LongestSegment>{
	private double length = 0.0;
	private Pair<Point, Point> extrems = null;
	private double angle = 0.0;
	
	public LongestSegment(Pair<Point, Point> _extrems, double _length, double _angle) {
		extrems = _extrems;
		length = _length;
		angle = _angle;
	}

	/**
	 * @return the length
	 */
	public final double getLength() {
		return length;
	}

	/**
	 * @return the extrems
	 */
	public final Pair<Point, Point> getExtrems() {
		Pair<Point, Point> _extrems = Pair.of(new Point(extrems.getLeft()), new Point(extrems.getRight()));
		return _extrems;
	}

	/**
	 * @return the angular_coeff
	 */
	public final double getAngle() {
		return angle;
	}

	@Override
	public int compareTo(LongestSegment o) {
		if (getLength() < o.getLength())
			return 1;
		else if (getLength() == o.getLength())
			return 0;
		else
			return -1;
	}


	/**
	 * Performs the tortuosity according to Hassaine - Al-Maadeed - Bouridane - A set of geometrical features for writer identification.
	 * @param img The input binarized image.
	 * @param angle_bins The number of bins which divide PI angle.
	 * @param run_step The x-axis step.
	 * @return The list of the lines found.
	 */
	public static ArrayList<LongestSegment> tortuosity(ImagePlus img, int angle_bins, int run_step) {
		ArrayList<LongestSegment> lines = new ArrayList<LongestSegment>();
		
		int BACKGROUND_PIXEL_VAL = 0;
		int FOREGROUND_PIXEL_VAL = 1;
		
		if (run_step == 0)
			++run_step;
		ImagePlus image = PreProcessingFactory.getPreProcess(PreprocessingType.Pad).execute(img, new double[]{1.0, BACKGROUND_PIXEL_VAL});
		double angle_delta = Math.PI / (double) angle_bins;
		double half_pi = Math.PI * 0.5;
		int[][] pixels = DataUtils.globalMinmax(CollectionUtils.transpose(image.getProcessor().getIntArray()), FOREGROUND_PIXEL_VAL, BACKGROUND_PIXEL_VAL);
		for (int i = 0; i < pixels.length; ++i) {
			for (int j = 0; j < pixels[i].length; ++j) {
				if (pixels[i][j] == BACKGROUND_PIXEL_VAL)
					continue;
				double longest_length = 0.0;
				Pair<Point, Point> longest_extrems = Pair.of(new Point(j, i), new Point(j, i)); // (lowest point, highest point)
				double longest_angle = 0.0;
				for (int k = 0; k < angle_bins; ++k) {
					Pair<Point, Point> cur_extrems = Pair.of(new Point(j, i), new Point(j, i));
					double cur_length = 0.0;
					double angle = angle_delta * (double) k;
					if (angle >= half_pi - angle_delta / 2.0 && angle < half_pi + angle_delta / 2.0) {
						// x = C
						while ((int) cur_extrems.getRight().getY() > 0 && (int) cur_extrems.getRight().getY() < image.getHeight() && 
								(int) cur_extrems.getRight().getX() > 0 && (int) cur_extrems.getRight().getX() < image.getWidth() && 
								pixels[(int) cur_extrems.getRight().getY()][(int) cur_extrems.getRight().getX()] != BACKGROUND_PIXEL_VAL) {
							cur_extrems.getRight().setLocation(cur_extrems.getRight().getX(), cur_extrems.getRight().getY() - 1.0);
						}
						cur_extrems.getRight().setLocation(cur_extrems.getRight().getX(), cur_extrems.getRight().getY() + 1.0);
						while ((int) cur_extrems.getLeft().getY() > 0 && (int) cur_extrems.getLeft().getY() < image.getHeight() && 
								(int) cur_extrems.getLeft().getX() > 0 && (int) cur_extrems.getLeft().getX() < image.getWidth() && 
								pixels[(int) cur_extrems.getLeft().getY()][(int) cur_extrems.getLeft().getX()] != BACKGROUND_PIXEL_VAL) {
							cur_extrems.getLeft().setLocation(cur_extrems.getLeft().getX(), cur_extrems.getLeft().getY() + 1.0);
						}
						cur_extrems.getLeft().setLocation(cur_extrems.getLeft().getX(), cur_extrems.getLeft().getY() - 1.0);
						double x_diff = cur_extrems.getRight().getX() - cur_extrems.getLeft().getX();
						double y_diff = cur_extrems.getRight().getY() - cur_extrems.getLeft().getY();
						cur_length = Math.sqrt(x_diff * x_diff + y_diff * y_diff);
					} else {
						// y = m * x + q
						double m = - Math.tan(angle); // minus because of the y-axis running sign
						double q = cur_extrems.getRight().getY() - m * cur_extrems.getRight().getX();
						while ((int) cur_extrems.getRight().getY() > 0 && (int) cur_extrems.getRight().getY() < image.getHeight() && 
								(int) cur_extrems.getRight().getX() > 0 && (int) cur_extrems.getRight().getX() < image.getWidth() && 
								pixels[(int) cur_extrems.getRight().getY()][(int) cur_extrems.getRight().getX()] != BACKGROUND_PIXEL_VAL) {
							cur_extrems.getRight().setLocation(cur_extrems.getRight().getX() + run_step, (cur_extrems.getRight().getX() + run_step) * m + q);
						}
						cur_extrems.getRight().setLocation(cur_extrems.getRight().getX() - run_step, (cur_extrems.getRight().getX() - run_step) * m + q);
						while ((int) cur_extrems.getLeft().getY() > 0 && (int) cur_extrems.getLeft().getY() < image.getHeight() && 
								(int) cur_extrems.getLeft().getX() > 0 && (int) cur_extrems.getLeft().getX() < image.getWidth() && 
								pixels[(int) cur_extrems.getLeft().getY()][(int) cur_extrems.getLeft().getX()] != BACKGROUND_PIXEL_VAL) {
							cur_extrems.getLeft().setLocation(cur_extrems.getLeft().getX() - run_step, (cur_extrems.getLeft().getX() - run_step) * m + q);
						}
						cur_extrems.getLeft().setLocation(cur_extrems.getLeft().getX() + run_step, (cur_extrems.getLeft().getX() + run_step) * m + q);
						double x_diff = cur_extrems.getRight().getX() - cur_extrems.getLeft().getX();
						double y_diff = cur_extrems.getRight().getY() - cur_extrems.getLeft().getY();
						cur_length = Math.sqrt(x_diff * x_diff + y_diff * y_diff);
					}
					if (cur_length > longest_length) {
						longest_extrems = cur_extrems;
						longest_length = cur_length;
						longest_angle = angle;
					}
				}
				lines.add(new LongestSegment(longest_extrems, longest_length, longest_angle));
			}
		}
		return lines;
	}
}