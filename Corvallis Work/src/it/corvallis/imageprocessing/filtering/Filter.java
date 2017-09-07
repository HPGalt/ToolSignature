/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package it.corvallis.imageprocessing.filtering;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.RankFilters;
import ij.process.AutoThresholder;
import ij.process.ImageProcessor;
import it.corvallis.commons.CollectionUtils;
import it.corvallis.commons.MeasurementUtils;
import it.corvallis.features.FeatureType;
import it.corvallis.imageprocessing.measurements.ConvexHullProcessor;
import it.corvallis.imageprocessing.measurements.DCT;
import it.corvallis.imageprocessing.measurements.Hu;
import it.corvallis.imageprocessing.preprocessing.PreProcessingFactory;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;
import it.corvallis.ml.dataprocessing.DataUtils;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.stat.StatUtils;

/**
 *
 * @author ChiarellaG
 */
public class Filter {
    
    private static final int BACKGROUND_PIXEL_VAL = 255;
    private static final int FOREGROUND_PIXEL_VAL = 0;

    
    /**
     * This method checks whether the central element is equal to its neighbor.
     * @param neighbor a 3x3 neighbor.
     * @return A boolean indicating whether the central element is equal (true) or not (false) to its neighbor.
     */
    public static boolean isBorderPixel(double[][] neighbor) {
    	
    	if (neighbor[neighbor.length / 2 - 1][neighbor.length / 2 - 1] == neighbor[neighbor.length / 2][neighbor.length / 2] && neighbor[neighbor.length / 2 - 1][neighbor.length / 2] == neighbor[neighbor.length / 2][neighbor.length / 2] &&
    			neighbor[neighbor.length / 2 - 1][neighbor.length / 2 + 1] == neighbor[neighbor.length / 2][neighbor.length / 2] && neighbor[neighbor.length / 2][neighbor.length / 2 - 1] == neighbor[neighbor.length / 2][neighbor.length / 2] &&
				neighbor[neighbor.length / 2][neighbor.length / 2 + 1] == neighbor[neighbor.length / 2][neighbor.length / 2] && neighbor[neighbor.length / 2 + 1][neighbor.length / 2 - 1] == neighbor[neighbor.length / 2][neighbor.length / 2] &&
				neighbor[neighbor.length / 2 + 1][neighbor.length / 2] == neighbor[neighbor.length / 2][neighbor.length / 2] && neighbor[neighbor.length / 2 + 1][neighbor.length / 2 + 1] == neighbor[neighbor.length / 2][neighbor.length / 2])
    		return false;
    	return true;
    }
    
    /**
     * Gets a roi from an image.
     * @param img The input image from which to get the roi.
     * @param top_left_x The top left x-axis of the rectangle to consider.
     * @param top_left_y The top left y-axis of the rectangle to consider.
     * @param width The width of the rectangle to consider.
     * @param height The height of the rectangle to consider.
     * @return The roi.
     */
    public static ImagePlus getRoi(ImagePlus img, int top_left_x, int top_left_y, int width, int height) {
    	int[][] pixels = img.getProcessor().getIntArray();
    	int[][] roi_pixels = new int[width][height];
    	top_left_x = top_left_x <= 0 ? 0 : top_left_x;
    	top_left_y = top_left_y <= 0 ? 0 : top_left_y;
        for (int i = top_left_y; i < (img.getHeight() >  top_left_y + height ?  top_left_y + height : img.getHeight()); ++i) {
            for (int j = top_left_x; j < (img.getWidth() > top_left_x + width ? top_left_x + width : img.getWidth()); ++j) {
            	roi_pixels[j - top_left_x][i - top_left_y] = pixels[j][i];
            }
        }
        ImagePlus roi = new ImagePlus("", img.getProcessor().resize(roi_pixels.length, roi_pixels[0].length));
        roi.getProcessor().setIntArray(roi_pixels);
        return roi;
    }
    
    /**
     * Filter a binary image from the non uniform dotted noise. 
     * @param img The binary input image.
     * @param minimum_particle_size The minimum particle size to identify [0.0 to maximum_particle_size].
     * @param maximum_particle_size The maximum particle size to identify [minimum_particle_size to Infinitive].
     * @param maximum_particle_circularity The minimum particle circularity to identify [0.0 to minimum_particle_circularity].
     * @param minimum_particle_circularity The maximum particle circularity to identify [maximum_particle_circularity to 1.0].
     * @return The filtered image.
     */
    public static ImagePlus ParticleFilter(ImagePlus img, double minimum_particle_size, double maximum_particle_size, double minimum_particle_circularity, double maximum_particle_circularity) {
        ImagePlus binary = img.duplicate();
        binary = normalizePixelValues(binary, FOREGROUND_PIXEL_VAL, BACKGROUND_PIXEL_VAL, true);
        binary.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, binary.getProcessor().getHistogram()));
        ResultsTable rt = new ResultsTable();
        ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_MASKS | ParticleAnalyzer.CLEAR_WORKSHEET, Measurements.AREA | Measurements.MEAN | Measurements.MIN_MAX, rt, minimum_particle_size, maximum_particle_size, minimum_particle_circularity, maximum_particle_circularity);
        pa.setHideOutputImage(true);
        if (pa.analyze(binary)) {
            ImagePlus mask = pa.getOutputImage().duplicate();
            mask.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, mask.getProcessor().getHistogram()));
            mask = negative(mask);
            binary = xor(binary, mask, BACKGROUND_PIXEL_VAL, FOREGROUND_PIXEL_VAL);
        }
        /*ImagePlus roi = img.duplicate();
        roi.setRoi(convexHull(binary).getProcessor().getRoi());
        roi = new ImagePlus("", roi.getProcessor().crop());
        return roi;*/
        return binary;
    }
    
    /**
     * Counts the holes over a minimum size.
     * @param img The image into which find the holes.
     * @param min_size The minimum size of the holes which find according to ParticleAnalyzer class of IJ library.
     * @return The number of the holes.
     */
    public static double holesNumber(ImagePlus img, double min_size) {
    	ImagePlus dup = img.duplicate();
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_MASKS | ParticleAnalyzer.INCLUDE_HOLES | ParticleAnalyzer.CLEAR_WORKSHEET, Measurements.AREA | Measurements.PERIMETER, rt, 0.0, Double.POSITIVE_INFINITY, 0.0, 1.0);
		pa.setHideOutputImage(true);

		pa.analyze(dup.duplicate());
		
		ImagePlus holes = xor(dup.duplicate(), pa.getOutputImage().duplicate(), BACKGROUND_PIXEL_VAL, FOREGROUND_PIXEL_VAL);
		rt = new ResultsTable();
		pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_MASKS | ParticleAnalyzer.CLEAR_WORKSHEET, Measurements.AREA | Measurements.PERIMETER, rt, (min_size <= 0.0 || min_size >= Double.POSITIVE_INFINITY) ? 0.0 : min_size, Double.POSITIVE_INFINITY, 0.0, 1.0);
		pa.setHideOutputImage(true);

		pa.analyze(negative(holes.duplicate()));

		return (double) rt.getCounter();
    }
    
    /**
     * Computes the area of the holes in an image.
     * @param img The input image.
     * @param min_size The minimum size of the holes.
     * @return The total are of the holes in the input image.
     */
    public static double holesArea(ImagePlus img, double min_size) {
    	ImagePlus dup = img.duplicate();
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_MASKS | ParticleAnalyzer.INCLUDE_HOLES | ParticleAnalyzer.CLEAR_WORKSHEET, Measurements.AREA | Measurements.PERIMETER, rt, 0.0, Double.POSITIVE_INFINITY, 0.0, 1.0);
		pa.setHideOutputImage(true);

		pa.analyze(dup.duplicate());
		
		ImagePlus holes = xor(dup.duplicate(), pa.getOutputImage().duplicate(), BACKGROUND_PIXEL_VAL, FOREGROUND_PIXEL_VAL);
		rt = new ResultsTable();
		pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_MASKS | ParticleAnalyzer.CLEAR_WORKSHEET, Measurements.AREA | Measurements.PERIMETER, rt, (min_size <= 0.0 || min_size >= Double.POSITIVE_INFINITY) ? 0.0 : min_size, Double.POSITIVE_INFINITY, 0.0, 1.0);
		pa.setHideOutputImage(true);

		pa.analyze(negative(holes.duplicate()));

		return Hu.area(pa.getOutputImage().duplicate(), 0, 0, pa.getOutputImage().getWidth(), pa.getOutputImage().getHeight());
    }
    
    /**
     * Find the convex hull of an image.
     * @param img The input image.
     * @return The convex hull of the input image.
     */
    public static ImagePlus convexHull(ImagePlus img) {
    	ImagePlus ch = img.duplicate();
    	ch = normalizePixelValues(ch, FOREGROUND_PIXEL_VAL, BACKGROUND_PIXEL_VAL, true);
    	ch.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, ch.getProcessor().getHistogram()));
        ConvexHullProcessor convHull = new ConvexHullProcessor();
        if (convHull.setup("", ch) != ij.plugin.filter.PlugInFilter.DONE) {
            convHull.run(ch.getProcessor());
            ch.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, ch.getProcessor().getHistogram()));
        } else {
        	ch = img.duplicate();
        	int[][] pixels = ch.getProcessor().getIntArray();
        	for (int i = 0; i < pixels.length; ++i) {
				for (int j = 0; j < pixels[i].length; ++j) {
					pixels[i][j] = BACKGROUND_PIXEL_VAL;
				}
			}
        	ch.getProcessor().setIntArray(pixels);
        }
        ch.setTitle("");
        return ch;
    }
    
    /**
     * Compute the negative form of an image.
     * @param img The image to negate.
     * @return The negated image.
     */
    public static ImagePlus negative(ImagePlus img) {
        ImagePlus n = img.duplicate();
        double max_val = Math.pow(2.0, 8.0) - 1.0;
        int[][] pixels = n.getProcessor().getIntArray();
        for (int i = 0; i < pixels.length; ++i) {
            for (int j = 0; j < pixels[i].length; ++j) {
                pixels[i][j] = (int) (max_val - (double) pixels[i][j]);
            }
        }
        n.getProcessor().setIntArray(pixels);
        //n.getProcessor().invert();
        n.setTitle("");
        return n;
    }
    
    /**
     * Perform the XOR [ !((a && !b) || (!a && b)) ] between two binary images.
     * @param a The first operand of the operation.
     * @param b The second operand of the operation.
     * @param zero_equivalent_value The image value that corresponds to the 0-logic value.
     * @param one_equivalent_value The image value that corresponds to the 1-logic value.
     * @return A binary image result of the XOR between the two parameters.
     */
    public static ImagePlus xor(ImagePlus a, ImagePlus b, int zero_equivalent_value, int one_equivalent_value) {
    	ImagePlus a_ = a.duplicate();
    	ImagePlus b_ = b.duplicate();
    	a_ = normalizePixelValues(a_, 0, 255, true);
    	a_.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, a_.getProcessor().getHistogram()));
    	b_ = normalizePixelValues(b_, 0, 255, true);
    	b_.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, b_.getProcessor().getHistogram()));
        ImagePlus c = a_.duplicate();
        int[][] apixels = a_.getProcessor().getIntArray();
        int[][] bpixels = b_.getProcessor().getIntArray();
        int rows = apixels.length <= bpixels.length ? apixels.length : bpixels.length;
        int cols = apixels[0].length <= bpixels[0].length ? apixels[0].length : bpixels[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                //!((a && !b) || (!a && b))
                apixels[i][j] = ((apixels[i][j] == zero_equivalent_value && bpixels[i][j] == one_equivalent_value) || (apixels[i][j] == one_equivalent_value && bpixels[i][j] == zero_equivalent_value)) ? one_equivalent_value : zero_equivalent_value;
            }
        }
        c.getProcessor().setIntArray(apixels);
        c.setTitle("");
        return c;
    }
    
    /**
     * Perform the AND [ a && b ] between two binary images.
     * @param a The first operand of the operation.
     * @param b The second operand of the operation.
     * @param zero_equivalent_value The image value that corresponds to the 0-logic value.
     * @param one_equivalent_value The image value that corresponds to the 1-logic value.
     * @return A binary image result of the AND between the two parameters.
     */
    public static ImagePlus and(ImagePlus a, ImagePlus b, int zero_equivalent_value, int one_equivalent_value) {
    	ImagePlus a_ = a.duplicate();
    	ImagePlus b_ = b.duplicate();
    	a_ = normalizePixelValues(a_, 0, 255, true);
    	b_ = normalizePixelValues(b_, 0, 255, true);
        ImagePlus c = a_.duplicate();
        int[][] apixels = a_.getProcessor().getIntArray();
        int[][] bpixels = b_.getProcessor().getIntArray();
        int rows = apixels.length <= bpixels.length ? apixels.length : bpixels.length;
        int cols = apixels[0].length <= bpixels[0].length ? apixels[0].length : bpixels[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                //a && b
                apixels[i][j] = (apixels[i][j] == one_equivalent_value && bpixels[i][j] == one_equivalent_value) ? one_equivalent_value : zero_equivalent_value;
            }
        }
        c.getProcessor().setIntArray(apixels);
        c.setTitle("");
        return c;
    }
    
    /**
     * Perform the OR [ a || b ] between two binary images.
     * @param a The first operand of the operation.
     * @param b The second operand of the operation.
     * @param zero_equivalent_value The image value that corresponds to the 0-logic value.
     * @param one_equivalent_value The image value that corresponds to the 1-logic value.
     * @return A binary image result of the AND between the two parameters.
     */
    public static ImagePlus or(ImagePlus a, ImagePlus b, int zero_equivalent_value, int one_equivalent_value) {
    	ImagePlus a_ = a.duplicate();
    	ImagePlus b_ = b.duplicate();
    	a_ = normalizePixelValues(a_, 0, 255, true);
    	b_ = normalizePixelValues(b_, 0, 255, true);
        ImagePlus c = a_.duplicate();
        int[][] apixels = a_.getProcessor().getIntArray();
        int[][] bpixels = b_.getProcessor().getIntArray();
        int rows = apixels.length <= bpixels.length ? apixels.length : bpixels.length;
        int cols = apixels[0].length <= bpixels[0].length ? apixels[0].length : bpixels[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                //a || b
                apixels[i][j] = (apixels[i][j] == zero_equivalent_value && bpixels[i][j] == zero_equivalent_value) ? zero_equivalent_value : one_equivalent_value;
            }
        }
        c.getProcessor().setIntArray(apixels);
        c.setTitle("");
        return c;
    }
    
    /**
     * Perform the NOT [ !a ] of an image.
     * @param a The operand of the operation.
     * @param zero_equivalent_value The image value that corresponds to the 0-logic value.
     * @param one_equivalent_value The image value that corresponds to the 1-logic value.
     * @return A binary image result of the NOT of the first parameters.
     */
    public static ImagePlus not(ImagePlus a, int zero_equivalent_value, int one_equivalent_value) {
    	ImagePlus a_ = a.duplicate();
    	a_ = normalizePixelValues(a_, 0, 255, true);
    	a_.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, a_.getProcessor().getHistogram()));
        ImagePlus c = a_.duplicate();
        int[][] apixels = a_.getProcessor().getIntArray();
        int max = (int) CollectionUtils.max(CollectionUtils.int_to_double(apixels));
        int rows = apixels.length;
        int cols = apixels[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                //!a
                apixels[i][j] = max - apixels[i][j];
            }
        }
        c.getProcessor().setIntArray(apixels);
        c.setTitle("");
        return c;
    }
    
    /**
     * Perform the XOR [ !((a && !b) || (!a && b)) ] between two binary images.
     * @param a The first operand of the operation.
     * @param b The second operand of the operation.
     * @return A binary image result of the XOR between the two parameters.
     */
    public static ImagePlus booleanXOR(ImagePlus a, ImagePlus b) {
    	ImagePlus a_ = a.duplicate();
    	ImagePlus b_ = b.duplicate();
        ImagePlus c = a_.duplicate();
        int[][] apixels = a_.getProcessor().getIntArray();
        int[][] bpixels = b_.getProcessor().getIntArray();
        int rows = apixels.length <= bpixels.length ? apixels.length : bpixels.length;
        int cols = apixels[0].length <= bpixels[0].length ? apixels[0].length : bpixels[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                //a ^ b
                apixels[i][j] = apixels[i][j] ^ bpixels[i][j];
            }
        }
        c.getProcessor().setIntArray(apixels);
        c.setTitle("");
        return c;
    }
    
    /**
     * Perform the AND [ a && b ] between two binary images.
     * @param a The first operand of the operation.
     * @param b The second operand of the operation.
     * @return A binary image result of the AND between the two parameters.
     */
    public static ImagePlus booleanAND(ImagePlus a, ImagePlus b) {
    	ImagePlus a_ = a.duplicate();
    	ImagePlus b_ = b.duplicate();
    	a_ = normalizePixelValues(a_, 0, 255, true);
    	b_ = normalizePixelValues(b_, 0, 255, true);
        ImagePlus c = a_.duplicate();
        int[][] apixels = a_.getProcessor().getIntArray();
        int[][] bpixels = b_.getProcessor().getIntArray();
        int rows = apixels.length <= bpixels.length ? apixels.length : bpixels.length;
        int cols = apixels[0].length <= bpixels[0].length ? apixels[0].length : bpixels[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                //a & b
                apixels[i][j] = apixels[i][j] & bpixels[i][j];
            }
        }
        c.getProcessor().setIntArray(apixels);
        c.setTitle("");
        return c;
    }
    
    /**
     * Perform the OR [ a || b ] between two binary images.
     * @param a The first operand of the operation.
     * @param b The second operand of the operation.
     * @return A binary image result of the AND between the two parameters.
     */
    public static ImagePlus booleanOR(ImagePlus a, ImagePlus b) {
    	ImagePlus a_ = a.duplicate();
    	ImagePlus b_ = b.duplicate();
    	a_ = normalizePixelValues(a_, 0, 255, true);
    	b_ = normalizePixelValues(b_, 0, 255, true);
        ImagePlus c = a_.duplicate();
        int[][] apixels = a_.getProcessor().getIntArray();
        int[][] bpixels = b_.getProcessor().getIntArray();
        int rows = apixels.length <= bpixels.length ? apixels.length : bpixels.length;
        int cols = apixels[0].length <= bpixels[0].length ? apixels[0].length : bpixels[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                //a | b
                apixels[i][j] = apixels[i][j] | bpixels[i][j];
            }
        }
        c.getProcessor().setIntArray(apixels);
        c.setTitle("");
        return c;
    }
    
    /**
     * Perform the NOT [ !a ] of an image.
     * @param a The operand of the operation.
     * @return A binary image result of the NOT of the first parameters.
     */
    public static ImagePlus booleanNOT(ImagePlus a) {
    	ImagePlus a_ = a.duplicate();
    	a_ = normalizePixelValues(a_, 0, 255, true);
    	a_.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, a_.getProcessor().getHistogram()));
        ImagePlus c = a_.duplicate();
        int[][] apixels = a_.getProcessor().getIntArray();
        int rows = apixels.length;
        int cols = apixels[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                //~ a
                apixels[i][j] = ~ apixels[i][j];
            }
        }
        c.getProcessor().setIntArray(apixels);
        c.setTitle("");
        return c;
    }
    
    /**
     * Stretches the image in order to have pixel values in a range.
     * @param img A gray scale input image.
     * @param min The lower bound of the pixel values.
     * @param max The upper bound of the pixel values.
     * @param threshold If true the resulting image is thresholded before returning.
     * @return The gray scale scaled image.
     */
    public static ImagePlus normalizePixelValues(ImagePlus img, int min, int max, boolean threshold) {
        ImagePlus c = new ImagePlus(img.getTitle(), img.getProcessor().convertToByte(true));
        c.getProcessor().setIntArray(CollectionUtils.double_to_int(DataUtils.globalMinmax(CollectionUtils.int_to_double(c.getProcessor().getIntArray()), min, max), false));
        if (threshold) {
            c.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, c.getProcessor().getHistogram()));
        }
        c.setTitle("");
        return c;
    }
    
    /**
     * Resize an image keeping the aspect.
     * @param img The input image.
     * @param width The new width of the image.
     * @param threshold If true the resulting image is thresholded before returning.
     * @return The resized image.
     */
    public static ImagePlus resizeImage(ImagePlus img, int width, boolean threshold) {
    	ImagePlus resized = img.duplicate();
    	resized = new ImagePlus("", resized.getProcessor().resize(width));
    	if (threshold) {
    		resized.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, resized.getProcessor().getHistogram()));
    	}
    	resized.setTitle("");
    	return resized;
    }
    
    /**
     * Resize an image.
     * @param img The input image.
     * @param width The new width of the image.
     * @param height  The new height of the image.
     * @param threshold If true the resulting image is thresholded before returning.
     * @return The resized image.
     */
    public static ImagePlus resizeImage(ImagePlus img, int width, int height, boolean threshold) {
    	ImagePlus resized = img.duplicate();
    	resized = new ImagePlus("", resized.getProcessor().resize(width, height, true));
        if (threshold) {
        	resized.getProcessor().threshold(new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, resized.getProcessor().getHistogram()));
        }
        resized.setTitle("");
        return resized;
    }
    
    /**
     * Performs the median filter.
     * @param img The input image.
     * @param kernel_width The gaussian kernel width.
     * @param kernel_height The gaussian kernel height.
     * @param padding Whether to pad the input matrix.
     * @param padding_value The value to use for pad the matrix.
     * @return The median filtered input image.
     */
    public static ImagePlus median(ImagePlus img, int kernel_width, int kernel_height, boolean padding, int padding_value) {
    	double[][] pixels = CollectionUtils.transpose(CollectionUtils.int_to_double(img.getProcessor().getIntArray()));
    	double[][] new_pixels = CollectionUtils.filter2D(pixels, kernel_height, kernel_width, new Function<double[][], Double>() {
			
			@Override
			public Double apply(double[][] t) {
				return DataUtils.median(CollectionUtils.toTrace(t, false));
			}
		}, padding, padding_value);
    	ImagePlus res = img.duplicate();
    	res.getProcessor().setIntArray(CollectionUtils.transpose(CollectionUtils.double_to_int(new_pixels, false)));
    	return res;
    }
    
    /**
     * It removes the white columns of an image.
     * @param img The input image.
     * @return The input image without white columns.
     */
    public static ImagePlus join(ImagePlus img) {
    	int[][] new_image = img.getProcessor().getIntArray();
        int[][] pixels = img.getProcessor().convertToByte(true).getIntArray();
        int cur_col = 0;
        while (pixels.length > 0 && cur_col < pixels.length) {
            int j = 0;
            for (; j < pixels[cur_col].length && pixels[cur_col][j] == BACKGROUND_PIXEL_VAL; ++j)
                ;
            if (j == pixels[cur_col].length) {
            	pixels = CollectionUtils.double_to_int(CollectionUtils.removeRows(CollectionUtils.int_to_double(pixels), new int[]{cur_col}), false);
            	new_image = CollectionUtils.double_to_int(CollectionUtils.removeRows(CollectionUtils.int_to_double(new_image), new int[]{cur_col}), false);
            } else {
                ++cur_col;
            }
        }
        ImageProcessor ip = img.duplicate().getProcessor().createProcessor(new_image.length, new_image[0].length);
        ip.setIntArray(new_image);
        return new ImagePlus("", ip);
    }

    /**
     * Finds the number of crosses in an image.
     * @param img The skeletonized image.
     * @param smoothing_window The kernel window dimension to smooth the nearest crosses. If 0 then no smoothing is applied.
     * @return The number of cross in the image.
     */
    public static Pair<ImagePlus, Double> crossFinder(ImagePlus img, int smoothing_window) {
    	ImagePlus res = img.duplicate();
    	int[][] pixels = CollectionUtils.transpose(res.getProcessor().getIntArray());

		int[][] previous_cross = new int[pixels.length][];
		previous_cross[0] = new int[pixels[0].length];
		previous_cross[pixels.length - 1] = new int[pixels[pixels.length - 1].length];
		Arrays.fill(previous_cross[0], -1);
		Arrays.fill(previous_cross[pixels.length - 1], -1);
		for (int i = 1; i < pixels.length - 1; ++i) {
			int[] cur_cross = new int[pixels[0].length];
			Arrays.fill(cur_cross, -1);
			for (int j = 1; j < pixels[i].length - 1; ++j) {
				if (pixels[i][j] == BACKGROUND_PIXEL_VAL)
					continue;
				int[][] kernel = new int[3][3];
				for(int k = 0; k < 3; ++k) {
					for(int l = 0; l < 3; ++l) {
						kernel[k][l] = pixels[i - 1 + k][j - 1 + l];
					}
				}
				
				int is_cross = isCross(kernel);
				if (is_cross < 0) {
					cur_cross[j] = -1;
				} else {
					if (previous_cross[i - 1][j] > 0) {
						if (is_cross >= previous_cross[i - 1][j]) {
							cur_cross[j] = -1;
						} else {
							previous_cross[i - 1][j] = -1;
							cur_cross[j] = is_cross;
						}
					} else {
						cur_cross[j] = is_cross;
					}
				}
			}
			previous_cross[i] = new int[cur_cross.length];
			System.arraycopy(cur_cross, 0, previous_cross[i], 0, cur_cross.length);
		}
		if (smoothing_window > 0) {
			smoothing_window = MeasurementUtils.checkWindowDimension(smoothing_window);
			for (int i = smoothing_window / 2; i < previous_cross.length - smoothing_window / 2; ++i) {
				for (int j = smoothing_window / 2; j < previous_cross[i].length - smoothing_window / 2; ++j) {
					int crosses = 0;
					int adjacency_type = 0;
					int x = 0;
					int y = 0;
					for(int k = 0; k < smoothing_window; ++k) {
						for(int l = 0; l < smoothing_window; ++l) {
							if (previous_cross[i - smoothing_window / 2 + k][j - smoothing_window / 2 + l] > -1)  {
								x += i - smoothing_window / 2 + k;
								y += j - smoothing_window / 2 + l;
								++crosses;
								adjacency_type += previous_cross[i - smoothing_window / 2 + k][j - smoothing_window / 2 + l];
							}
						}
					}
					if (crosses == 0)
						continue;
					x = (int) ((double) x / (double) crosses);
					y = (int) ((double) y / (double) crosses);
					for(int k = 0; k < smoothing_window; ++k) {
						for(int l = 0; l < smoothing_window; ++l) {
							if (previous_cross[i - smoothing_window / 2 + k][j - smoothing_window / 2 + l] >= 0)  {
								previous_cross[i - smoothing_window / 2 + k][j - smoothing_window / 2 + l] = -1;
							}
						}
					}
					previous_cross[x][y] = (int) ((double) adjacency_type / (double) crosses);
				}
			}
		}
		int crosses = 0;
		for (int i = 0; i < previous_cross.length; ++i) {
			for (int j = 0; j < previous_cross[i].length; ++j) {
				if (previous_cross[i][j] >= 0)  {
					previous_cross[i][j] = FOREGROUND_PIXEL_VAL;
					++crosses;
				} else {
					previous_cross[i][j] = BACKGROUND_PIXEL_VAL;
				}
			}
		}
		res.getProcessor().setIntArray(CollectionUtils.transpose(previous_cross));
		return Pair.of(res, (double) crosses);
    }
    
    private static int isCross(int[][] kernel) {
    	int adjacency = Filter.findWhiteAdjacentPixels(kernel);
    	int sum = 0;
    	for(int i = 0; i < kernel.length; ++i) {
			for(int j = 0; j < kernel[i].length; ++j) {
				if (kernel[i][j] == FOREGROUND_PIXEL_VAL)
					++sum;
			}
		}
		if ((sum == 4 && adjacency == 3) ||	((sum == 5 || sum == 6) && adjacency > 0)) {
			return sum;
		} else {
			return -1;
		}
    }
    
	/**
	 * Retrieves the number of pixels which have at most two white adjacent pixels.
	 * @param pixels The 3x3 piece of image into which searching.
	 * @return The number of pixel which have at most two white adjacent pixels.
	 */
	private static int findWhiteAdjacentPixels(final int[][] pixels) {
		int[] p = new int[pixels.length * pixels[0].length - (pixels.length - 2) * (pixels[0].length - 2)];
		int index = -1;
		for(int i = 0; i < pixels[0].length; ++i) {
			++index;
			p[index] = pixels[0][index];
		}
		for(int i = 1; i < pixels.length; ++i) {
			++index;
			p[index] = pixels[i][pixels[0].length - 1];
		}
		for(int i = pixels[pixels.length - 1].length - 2; i >= 0; --i) {
			++index;
			p[index] = pixels[pixels.length - 1][i];
		}
		for(int i = pixels.length - 2; i > 0; --i) {
			++index;
			p[index] = pixels[i][0];
		}
		int at_most_two_white_adiacent_pixels = 0;
		for(int i = 1; i < p.length - 1; ++i) {
			if (p[i] == FOREGROUND_PIXEL_VAL && p[i - 1] == BACKGROUND_PIXEL_VAL && p[i + 1] == BACKGROUND_PIXEL_VAL) {
				++at_most_two_white_adiacent_pixels;
			}
		}
		if (p[0] == FOREGROUND_PIXEL_VAL && p[1] == BACKGROUND_PIXEL_VAL && p[p.length - 1] == BACKGROUND_PIXEL_VAL)
			++at_most_two_white_adiacent_pixels;
		if (p[p.length - 1] == FOREGROUND_PIXEL_VAL && p[0] == BACKGROUND_PIXEL_VAL && p[p.length - 2] == BACKGROUND_PIXEL_VAL)
			++at_most_two_white_adiacent_pixels;
		return at_most_two_white_adiacent_pixels;
	}
	
	/**
	 * Computes the distribution of the slants in an image by means of four 2D convolutions using the following kernels:
	 * - VERITCAL SLANT
	 *	{
	 *			{0.0, 0.0, 0.0},
	 *			{1.0, 1.0, 1.0},
	 *			{0.0, 0.0, 0.0}
	 *	};
	 *
	 * - HORIZONTAL_SLANT
	 *	{
	 *			{0.0, 1.0, 0.0},
	 *			{0.0, 1.0, 0.0},
	 *			{0.0, 1.0, 0.0}
	 *	};
	 *
	 * - ASCENDANT_SLANT
	 *	double[][] ascendant_slant = new double[][] {
	 *			{0.0, 0.0, 1.0},
	 *			{0.0, 1.0, 0.0},
	 *			{1.0, 0.0, 0.0}
	 *	};
	 *
	 * - DESCENDANT_SLANT
	 *	{
	 *			{1.0, 0.0, 0.0},
	 *			{0.0, 1.0, 0.0},
	 *			{0.0, 0.0, 1.0}
	 *	};
	 *
	 * @param img The input image.
	 * @return A four-elements array containing the distribution of the slants.
	 */
	public static double[] slantDistribution(ImagePlus img) {
		ImagePlus res = img.duplicate();
		double[][] pixels = DataUtils.globalMinmax(CollectionUtils.int_to_double(res.getProcessor().getIntArray()), 0.0, 1.0);
		double[] slant_distribution = new double[4];
		Arrays.fill(slant_distribution, 0.0);
		double[][] vertical_slant = new double[][] {
				{0.0, 0.0, 0.0},
				{1.0, 1.0, 1.0},
				{0.0, 0.0, 0.0}
		};
		double[][] horizontal_slant = new double[][] {
				{0.0, 1.0, 0.0},
				{0.0, 1.0, 0.0},
				{0.0, 1.0, 0.0}
		};
		double[][] ascendant_slant = new double[][] {
				{0.0, 0.0, 1.0},
				{0.0, 1.0, 0.0},
				{1.0, 0.0, 0.0}
		};
		double[][] descendant_slant = new double[][] {
				{1.0, 0.0, 0.0},
				{0.0, 1.0, 0.0},
				{0.0, 0.0, 1.0}
		};

		double[][] vertical = CollectionUtils.convolve2D(pixels, vertical_slant, true, 0.0);
		double[][] horizontal = CollectionUtils.convolve2D(pixels, horizontal_slant, true, 0.0);
		double[][] ascendant = CollectionUtils.convolve2D(pixels, ascendant_slant, true, 0.0);
		double[][] descendant = CollectionUtils.convolve2D(pixels, descendant_slant, true, 0.0);
		
		res.getProcessor().setIntArray(CollectionUtils.double_to_int(DataUtils.globalMinmax(vertical, 0, 255), false));
        res.getProcessor().threshold(/*new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, res.getProcessor().getHistogram())*/254);
        vertical = CollectionUtils.int_to_double(CollectionUtils.deep_copy(res.getProcessor().getIntArray()));

		res.getProcessor().setIntArray(CollectionUtils.double_to_int(DataUtils.globalMinmax(horizontal, 0, 255), false));
        res.getProcessor().threshold(/*new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, res.getProcessor().getHistogram())*/254);
        horizontal = CollectionUtils.int_to_double(CollectionUtils.deep_copy(res.getProcessor().getIntArray()));
		
		res.getProcessor().setIntArray(CollectionUtils.double_to_int(DataUtils.globalMinmax(ascendant, 0, 255), false));
        res.getProcessor().threshold(/*new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, res.getProcessor().getHistogram())*/254);
        ascendant = CollectionUtils.int_to_double(CollectionUtils.deep_copy(res.getProcessor().getIntArray()));
		
		res.getProcessor().setIntArray(CollectionUtils.double_to_int(DataUtils.globalMinmax(descendant, 0, 255), false));
        res.getProcessor().threshold(/*new AutoThresholder().getThreshold(AutoThresholder.Method.Otsu, res.getProcessor().getHistogram())*/254);
        descendant = CollectionUtils.int_to_double(CollectionUtils.deep_copy(res.getProcessor().getIntArray()));

        for (int i = 0; i < pixels.length; ++i) {
			for (int j = 0; j < pixels[i].length; ++j) {
				if (vertical[i][j] > horizontal[i][j] && vertical[i][j] > ascendant[i][j] && vertical[i][j] > descendant[i][j])
					++slant_distribution[0];
				else if (horizontal[i][j] > vertical[i][j] && horizontal[i][j] > ascendant[i][j] && horizontal[i][j] > descendant[i][j])
					++slant_distribution[1];
				else if (ascendant[i][j] > vertical[i][j] && ascendant[i][j] > horizontal[i][j] && ascendant[i][j] > descendant[i][j])
					++slant_distribution[2];
				else if (descendant[i][j] > vertical[i][j] && descendant[i][j] > horizontal[i][j] && descendant[i][j] > ascendant[i][j])
					++slant_distribution[3];
			}
		}
        double sum = 0.0;
        sum = Arrays.asList(ArrayUtils.toObject(slant_distribution)).parallelStream().map((element) -> element).reduce(sum, (accumulator, _item) -> accumulator + _item);
        for (int i = 0; i < slant_distribution.length; ++i) {
			slant_distribution[i] /= sum;
		}
		return slant_distribution;
	}
	
	/**
	 * Count the number of line ends inside an image.
	 * @param img Skeletonized image.
	 * @return The number of the line ends.
	 */
	public static double lineEndsCounter(ImagePlus img) {
		double counter = 0.0;
		ImagePlus res = img.duplicate();
		int[][] pixels = res.getProcessor().getIntArray();
		for (int i = 0; i < pixels.length; ++i) {
			for (int j = 0; j < pixels[i].length; ++j) {
				if (pixels[i][j] != FOREGROUND_PIXEL_VAL)
					continue;
				if (i == 0 || j == 0 || i == pixels.length - 1 || j == pixels[i].length - 1) {
					if ((i == 0 && j == 0) || (i == 0 && j == pixels[i].length - 1) || (i == pixels.length - 1 && j == 0) || (i == pixels.length - 1 && j == pixels[i].length - 1) ||
						(i == 0 && (pixels[i][j - 1] == BACKGROUND_PIXEL_VAL || pixels[i][j + 1] == BACKGROUND_PIXEL_VAL)) ||
						(i == pixels.length - 1 && (pixels[i][j - 1] == BACKGROUND_PIXEL_VAL || pixels[i][j + 1] == BACKGROUND_PIXEL_VAL)) ||
						(j == 0 && (pixels[i - 1][j] == BACKGROUND_PIXEL_VAL || pixels[i + 1][j] == BACKGROUND_PIXEL_VAL)) ||
						(j == pixels[i].length - 1 && (pixels[i - 1][j] == BACKGROUND_PIXEL_VAL || pixels[i + 1][j] == BACKGROUND_PIXEL_VAL)))
						++counter;
					continue;
				}
				int foreground_pixels = 0;
				for (int k = 0; k < 3; ++k) {
					for (int l = 0; l < 3; ++l) {
						if (pixels[i - 1 + k][j - 1 + l] == FOREGROUND_PIXEL_VAL)
							++foreground_pixels;
					}
				}
				if (foreground_pixels <= 2)
					++counter;
			}
		}
		return counter;
	}
	
	/**
	 * Generates a matrix into which each pixel is labeled by the label of the component to which it belongs.
	 * @param img A binary image into which to search the connected components.
	 * @return The labeled pixel matrix.
	 */
	/*public static Pair<int[][], HashMap<Integer, Integer>> connectedComponents(ImagePlus img) {
		List<Integer> labels_list = new ArrayList<Integer>();
		Set<Pair<Integer, Integer>> equivalences = new HashSet<Pair<Integer, Integer>>();
		Set<Pair<Integer, Integer>> removeCandidates = new HashSet<Pair<Integer, Integer>>();
		int[][] pixels = img.getProcessor().getIntArray();
		int[][] labels = new int[pixels.length][pixels[0].length];
		for (int i = 0; i < labels.length; ++i) {
			pixels[i][0] = 0;
			pixels[i][pixels[i].length - 1] = 0;
			labels[i][0] = 0;
			labels[i][labels[i].length - 1] = 0;
		}
		for (int i = 0; i < labels[0].length; ++i) {
			pixels[0][i] = 0;
			pixels[pixels.length - 1][i] = 0;
			labels[0][i] = 0;
			labels[labels.length - 1][i] = 0;
		}
		for (int i = 1; i < pixels.length - 1; ++i) {
			for (int j = 1; j < pixels[i].length - 1; ++j) {
				pixels[i][j] = pixels[i][j] == 0 ? 1 : 0;
				labels[i][j] = 0;
			}
		}
		pixels = CollectionUtils.transpose(pixels);
		labels = CollectionUtils.transpose(labels);
		for (int i = 1; i < pixels.length - 1; ++i) {
			for (int j = 1; j < pixels[i].length - 1; ++j) {
				if (pixels[i][j] == 0)
					continue;
				if (pixels[i][j - 1] == 0 && pixels[i - 1][j - 1] == 0 && pixels[i - 1][j] == 0 && pixels[i - 1][j + 1] == 0) {
					int label = labels_list.size() + 1;
					labels[i][j] = label;
					labels_list.add(label);
					equivalences.add(Pair.of(label, 0));
				} else {
					if (pixels[i][j - 1] == 1) {
						labels[i][j] = labels[i][j - 1];
					}
					if (pixels[i - 1][j - 1] == 1) {
						if (labels[i][j] == 0) {
							labels[i][j] = labels[i - 1][j - 1];
						} else if (labels[i][j] != labels[i - 1][j - 1]) {
							equivalences.add(Pair.of(labels[i][j], labels[i - 1][j - 1]));
							removeCandidates.add(Pair.of(labels[i - 1][j - 1], 0));
						}
					}
					if (pixels[i - 1][j] == 1) {
						if (labels[i][j] == 0) {
							labels[i][j] = labels[i - 1][j];
						} else if (labels[i][j] != labels[i - 1][j]) {
							equivalences.add(Pair.of(labels[i][j], labels[i - 1][j]));
							removeCandidates.add(Pair.of(labels[i - 1][j], 0));
						}
					}
					if (pixels[i - 1][j + 1] == 1) {
						if (labels[i][j] == 0) {
							labels[i][j] = labels[i - 1][j + 1];
						} else if (labels[i][j] != labels[i - 1][j + 1]) {
							equivalences.add(Pair.of(labels[i][j], labels[i - 1][j + 1]));
							removeCandidates.add(Pair.of(labels[i - 1][j + 1], 0));
						}
					}
				}
			}
		}
		for (Pair<Integer,Integer> pair : removeCandidates) {
			for (Pair<Integer,Integer> pa : equivalences) {
				if (pair.getLeft() == pa.getLeft() && pair.getRight() == pa.getRight()) {
					equivalences.remove(pa);
					break;
				}
			}
		}
		List<Pair<Integer, Integer>> temporary = new ArrayList<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> p : equivalences) {
			temporary.add(Pair.of(p.getLeft(), p.getRight()));
		}
		Set<Integer> parents = new HashSet<Integer>();
		while (!temporary.isEmpty()) {
			Pair<Integer, Integer> p = temporary.remove(0);
			if (p.getLeft() != p.getRight() && p.getRight() != 0) {
				for (int i = 0; i < labels.length; ++i) {
					for (int j = 0; j < labels[i].length; ++j) {
						if (labels[i][j] == 0)
							continue;
						if (labels[i][j] == p.getRight())
							labels[i][j] = p.getLeft();
					}
				}
				for (int i = 0; i < temporary.size(); ++i) {
					if (p.getRight() == temporary.get(i).getLeft()) {
						temporary.set(i, Pair.of(p.getLeft(), temporary.get(i).getRight()));
					}
					if (p.getRight() == temporary.get(i).getRight()) {
						temporary.set(i, Pair.of(temporary.get(i).getLeft(), p.getLeft()));
					}
				}
			} else {
				parents.add(p.getLeft());
			}
		}
		int max = (int) DataUtils.findMinMax(CollectionUtils.int_to_double(ArrayUtils.toPrimitive(parents.toArray(new Integer[0]))), true) + 1;
		int index = 1;
		for (Integer p : parents) {
			for (int i = 0; i < labels.length; ++i) {
				for (int j = 0; j < labels[i].length; ++j) {
					if (labels[i][j] == p)
						labels[i][j] = index + max;
				}
			}
			++index;
		}
		HashMap<Integer, Integer> cc_area = new HashMap<Integer, Integer>();
		for (int i = 0; i < labels.length; ++i) {
			for (int j = 0; j < labels[i].length; ++j) {
				if (labels[i][j] != 0) {
					if (!cc_area.containsKey(labels[i][j]))
						cc_area.put(labels[i][j], 0);
					cc_area.put(labels[i][j], cc_area.get(labels[i][j]) + 1);
				}
			}
		}
		
		//final int max_area = cc_area.get(cc_area.keySet().stream().reduce(new ArrayList<Integer>(cc_area.keySet()).get(0), (a, b) -> cc_area.get(a) >= cc_area.get(b) ? a : b));
		//cc_area.keySet().parallelStream().forEach((a) -> cc_area.put(a, cc_area.get(a) / max_area));
		return Pair.of(labels, cc_area);
	}*/
	
	/**
	 * Removes connected components which are composed by a number of pixels less or equal than a threshold.
	 * @param img A binary image to filter.
	 * @param threshold The minimum number of pixels by which a connected component is composed.
	 * @return The label matrix of the filtered connected component.
	 */
	/*public static Pair<int[][], HashMap<Integer, Integer>> removeConnectedComponent(ImagePlus img, int threshold) {
		Pair<int[][], HashMap<Integer, Integer>> cc = connectedComponents(img);
		for (Integer label : cc.getRight().keySet()) {
			if (cc.getRight().get(label) > threshold)
				continue;
			for (int i = 0; i < cc.getLeft().length; ++i) {
				for (int j = 0; j < cc.getLeft()[i].length; ++j) {
					if (cc.getLeft()[i][j] == label)
						cc.getLeft()[i][j] = 0;
				}
			}
		}
		int[][] p = CollectionUtils.deep_copy(cc.getLeft());
		int index = 0;
		int max = 190;
		int min = 128;
		for (Integer l : cc.getRight().keySet()) {
			for (int i = 0; i < p.length; ++i) {
				for (int j = 0; j < p[i].length; ++j) {
					if (p[i][j] == l)
						p[i][j] = min + index * (max - min) / cc.getRight().keySet().size();
				}
			}
			++index;
		}
		ImagePlus r = img.duplicate();
		r.getProcessor().setIntArray(CollectionUtils.transpose(p));
		Dashboard.saveImage(r, "r.png");
		return cc;
	}*/
	
	/**
	 * Computes the histogram of gradient (HoG).
	 * @param img The gray scale input image.
	 * @param histogram_bins the number of bins to quantize the direction of the gradient.
	 * @param vertical_block_number Height of a single cell.
	 * @param horizontal_block_number Width of a single cell.
	 * @param overlap The overlap percentage of the blocks.
	 * @return The histogram of the directions of the image.
	 */
	public static double[][] histogramOfGradient(ImagePlus img, int vertical_block_number, int horizontal_block_number, double overlap, int histogram_bins) {
		ImagePlus image = img.duplicate();
		double starting_angle = Math.PI;
		double[][] histogram = new double[vertical_block_number * horizontal_block_number][histogram_bins + 1];
		double[] histogram_magnitude_sum = new double[vertical_block_number * horizontal_block_number];
		double delta = 2.0 * Math.PI / (double) histogram_bins;
		//int vertical_block_number = (int) ((img.getHeight() + block_height - overlap * block_height) / ((1.0 - overlap) * block_height) - 1.0);
		//int horizontal_block_number = (int) ((img.getWidth() + block_width - overlap * block_width) / ((1.0 - overlap) * block_width) - 1.0);
		int block_height = (int) (image.getHeight() / ((1.0 - overlap) * vertical_block_number));
		int block_width = (int) (image.getWidth() / ((1.0 - overlap) * horizontal_block_number));
		for (int i = 0; i < vertical_block_number; ++i) {
			for (int j = 0; j < horizontal_block_number; ++j) {
				Arrays.fill(histogram[i * horizontal_block_number + j], 0.0);
				histogram_magnitude_sum[i * horizontal_block_number + j] = 0.0;
				image.setRoi((int) ((1.0 - overlap) * block_width * j), (int) ((1.0 - overlap) * block_height * i), block_width, block_height);
				ImagePlus block = new ImagePlus("", image.getProcessor().crop());
				int[][] pixels = CollectionUtils.transpose(block.getProcessor().getIntArray());
				double[][] kernelx = new double[][] {
						{1.0 / 4.0, 0, -1.0 / 4.0},
						{2.0 / 4.0, 0, -2.0 / 4.0},
						{1.0 / 4.0, 0, -1.0 / 4.0}
				};
				double[][] gx = CollectionUtils.convolve2D(CollectionUtils.int_to_double(pixels), kernelx, false, 0);
				double[][] kernely = new double[][] {
						{-1.0 / 4.0, -2.0 / 4.0, -1.0 / 4.0},
						{0, 0, 0},
						{1.0 / 4.0, 2.0 / 4.0, 1.0 / 4.0}
				};
				double[][] gy = CollectionUtils.convolve2D(CollectionUtils.int_to_double(pixels), kernely, false, 0);
				for (int k = 0; k < gy.length; ++k) {
					for (int l = 0; l < gy[k].length; ++l) {
						if (gx[k][l] == 0.0) {
							if (gy[k][l] < 0.0)
								++histogram[i * horizontal_block_number + j][histogram_bins - (int) (histogram_bins / 4.0)];
							else if (gy[k][l] > 0.0)
								++histogram[i * horizontal_block_number + j][(int) (histogram_bins / 4.0)];
							else
								++histogram[i * horizontal_block_number + j][0];
						} else {
							double mag = Math.sqrt(gx[k][l] * gx[k][l] + gy[k][l] * gy[k][l]);
							double angle = Math.atan2(gy[k][l], gx[k][l]) + starting_angle;
							int index = (int) (angle / delta);
							histogram[i * horizontal_block_number + j][index] += mag;
							histogram_magnitude_sum[i * horizontal_block_number + j] += mag;
						}
					}
				}
			}
		}
		histogram = CollectionUtils.transpose(DataUtils.scale(CollectionUtils.transpose(histogram), histogram_magnitude_sum));
		return histogram;
	}
	
	/**
	 * Dilates a binary image.
	 * @param img Binary input image.
	 * @param se Structuring element to dilate with.
	 * @return The dilated image.
	 */
	public static ImagePlus dilate(ImagePlus img, int[][] se) {
		ImagePlus res = img.duplicate();
		double[][] data = CollectionUtils.transpose(CollectionUtils.int_to_double(res.getProcessor().getIntArray()));
		data = CollectionUtils.filter2D(data, se.length, se[0].length, new Function<double[][], Double>() {
			
			@Override
			public Double apply(double[][] arg0) {
				double max = 0;
				for (int i = 0; i < arg0.length; ++i) {
					for (int j = 0; j < arg0[i].length; ++j) {
						if (se[i][j] <= 0)
							continue;
						if (arg0[i][j] >= max)
							max = arg0[i][j];
					}
				}
				return max;
			}

		}, true, 0);
		res.getProcessor().setIntArray(CollectionUtils.transpose(CollectionUtils.double_to_int(data, true)));
		return res;
	}
	
	/**
	 * Erodes a binary image.
	 * @param img Binary input image.
	 * @param se Structuring element according to erode.
	 * @return The eroded image.
	 */
	public static ImagePlus erode(ImagePlus img, int[][] se) {
		ImagePlus res = img.duplicate();
		double[][] data = CollectionUtils.transpose(CollectionUtils.int_to_double(res.getProcessor().getIntArray()));
		data = CollectionUtils.filter2D(data, se.length, se[0].length, new Function<double[][], Double>() {
			
			@Override
			public Double apply(double[][] arg0) {
				double min = 255;
				for (int i = 0; i < arg0.length; ++i) {
					for (int j = 0; j < arg0[i].length; ++j) {
						if (se[i][j] <= 0)
							continue;
						if (arg0[i][j] <= min)
							min = arg0[i][j];
					}
				}
				return min;
			}
		}, true, 255);
		res.getProcessor().setIntArray(CollectionUtils.transpose(CollectionUtils.double_to_int(data, true)));
		return res;
	}
	
	/**
	 * Given two images it horizontal merges in a single image.
	 * @param a Left image.
	 * @param b Right image.
	 * @param padding_value Padding value in case input images haven't the same sizes.
	 * @return The horizontal merge of the two images.
	 */
	public static ImagePlus horizontalMerge(ImagePlus a, ImagePlus b, int padding_value) {
		ImagePlus c;
		int width = a.getWidth() + b.getWidth();
		int height = a.getHeight() >= b.getHeight() ? a.getHeight() : b.getHeight();
		int[][] apixels = CollectionUtils.transpose(a.getProcessor().getIntArray());
		int[][] bpixels = CollectionUtils.transpose(b.getProcessor().getIntArray());
		int[][] pixels = new int[height][width];
		for (int i = 0; i < apixels.length; ++i) {
			for (int j = 0; j < apixels[i].length; ++j) {
				pixels[i][j] = apixels[i][j];
			}
		}
		for (int i = 0; i < bpixels.length; ++i) {
			for (int j = 0; j < bpixels[i].length; ++j) {
				pixels[i][j + apixels[0].length] = bpixels[i][j];
			}
		}
		for (int i = apixels.length; i < pixels.length; ++i) {
			for (int j = 0; j < apixels[i].length; ++j) {
				pixels[i][j] = padding_value;
			}
		}
		for (int i = bpixels.length; i < pixels.length; ++i) {
			for (int j = apixels[0].length; j < apixels[i].length; ++j) {
				pixels[i][j] = padding_value;
			}
		}
		c = new ImagePlus("", a.getProcessor().resize(width, height));
		c.getProcessor().setIntArray(CollectionUtils.transpose(pixels));
		return c;
	}
	
	/**
	 * Computes the mean incidence angle according to Daramola - Ibiyemi: Novel Feature Extraction Technique for Off-line Signature Verification System.
	 * @param img The skeletonized image.
	 * @return The mean incidence angle.
	 */
	public static double meanIncidenceAngle(ImagePlus img) {
		double angle_sum = 0.0;
		int pixels[][] = CollectionUtils.transpose(img.getProcessor().getIntArray());
		for (int i = 0; i < pixels.length; ++i) {
			for (int j = 0; j < pixels[i].length; ++j) {
				if (pixels[i][j] == FOREGROUND_PIXEL_VAL) {
					angle_sum += Math.atan2((img.getHeight() - i), (img.getWidth() - j));
				}
			}
		}
		return angle_sum / Hu.area(img, 0, 0, img.getWidth(), img.getHeight());
	}
	
	/**
	 * Computes the curvature pdf according to Hassaine - Al-Maadeed - Bouridane: A set of geometrical features for writer identification.
	 * @param img The binary image.
     * @param window_height The rows of the kernel.
     * @param window_width The columns of the kernel.
	 * @return The pdf of the curvature.
	 */
	public static double[][] curvature(ImagePlus img, int window_width, int window_height) {
		window_width = MeasurementUtils.checkWindowDimension(window_width);
		window_height = MeasurementUtils.checkWindowDimension(window_height);
		double[][] curvature = CollectionUtils.filter2D(CollectionUtils.int_to_double(CollectionUtils.transpose(img.getProcessor().getIntArray())), window_height, window_width, new Function<double[][], Double>() {
			
			@Override
			public Double apply(double[][] arg0) {
				if (arg0 == null || arg0.length == 0 || arg0[0].length == 0)
					return FeatureType.CurvaturePdfFeature.getDefaultValue();
				if (arg0[arg0.length / 2][arg0[0].length / 2] != FOREGROUND_PIXEL_VAL)
					return FeatureType.CurvaturePdfFeature.getDefaultValue();
				if (isBorderPixel(arg0))
					return FeatureType.CurvaturePdfFeature.getDefaultValue();
				double n2 = 0.0;
				for (int i = 0; i < arg0.length; ++i) {
					for (int j = 0; j < arg0[i].length; ++j) {
						if (arg0[i][j] == FOREGROUND_PIXEL_VAL) {
							++n2;
						}
					}
				}
				return 1.0 - 2.0 * n2 / (double) (arg0.length * arg0[0].length);
			}
			
		}, true, BACKGROUND_PIXEL_VAL);
		return curvature;
	}
	
	/**
	 * Computes the local binary pattern according to Hassaine - Al-Maadeed - Bouridane: A set of geometrical features for writer identification.
	 * @param img The gray scale image.
     * @param window_height The rows of the kernel.
     * @param window_width The columns of the kernel.
	 * @param power_base The base of the power.
	 * @return The pdf of the curvature.
	 */
	public static double[][] localBinaryPattern(ImagePlus img, int window_width, int window_height, double power_base) {
		window_height = MeasurementUtils.checkWindowDimension(window_height);
		window_width = MeasurementUtils.checkWindowDimension(window_width);
		double[][] lbp = CollectionUtils.filter2D(CollectionUtils.int_to_double(CollectionUtils.transpose(img.getProcessor().getIntArray())), window_height, window_width, new Function<double[][], Double>() {
			
			@Override
			public Double apply(double[][] arg0) {
				if (arg0 == null || arg0.length == 0 || arg0[0].length == 0)
					return FeatureType.LocalBinaryPatternPdfFeature.getDefaultValue();
				arg0 = DataUtils.threshold(arg0, arg0[arg0.length / 2][arg0[0].length / 2], 0, 1);
				double lbp = 0.0;
				int exponent = 0;
				for (int i = 0; i < arg0.length; ++i) {
					for (int j = 0; j < arg0[i].length; ++j) {
						if (i == arg0.length / 2 && j == arg0[0].length / 2)
							continue;
						if (arg0[arg0.length- 1 - i][arg0[i].length - 1 - j] == 1) {
							lbp += Math.pow(power_base, exponent);
						}
						++exponent;
					}
				}
				return lbp;
			}
			
		}, true, 255);
		return lbp;
	}
	
	/**
	 * Performs the DCT and returns the first coeff_number coefficients.
	 * @param img The input image.
	 * @param n the number of points onto which compute the DCT.
	 * @param coeff_number The number of coefficients to return.
	 * @return An array containing the DCT coeffients.
	 */
	public static double[] dct(ImagePlus img, int n, int coeff_number) {
		double[] coeffs = new double[coeff_number];
		n = MeasurementUtils.nearestSquarePower(n);
		img.setProcessor(img.getProcessor().resize(n, n));
		DCT dct = new DCT(n);
		coeffs = ArrayUtils.toPrimitive(Arrays.asList(ArrayUtils.toObject(dct.zigZag(dct.forwardDCT(CollectionUtils.int_to_double(CollectionUtils.transpose(img.getProcessor().getIntArray())))))).subList(0, coeff_number).toArray(new Double[0]));
		return coeffs;
	}
	
	public static enum DirectionType {
		UPPER_RIGHT(1, -1, 1),
		RIGHT(2, 0, 1),
		LOWER_RIGHT(3, 1, 1),
		LOWER(4, 1, 0),
		LOWER_LEFT(5, 1, -1),
		LEFT(6, 0, -1),
		UPPER_LEFT(7, -1, -1),
		UPPER(8, -1, 0);
		
		private int code;
		private int relative_row;
		private int relative_col;
		
		private DirectionType(int _code, int _relative_row, int _relative_col) {
			code = _code;
			relative_row = _relative_row;
			relative_col = _relative_col;
		}
		
		public int getCode() {
			return code;
		}
		
		public int getRelativeRow() {
			return relative_row;
		}
		
		public int getRelativeCol() {
			return relative_col;
		}
	}
	
	/**
	 * Finds the chain code of each connected components.
	 * @param img The image into searching.
	 * @param direction_order The direction ordered list.
	 * @return The chain code.
	 */
	public static Pair<List<int[][]>, List<Integer>> chainCode(ImagePlus img, List<DirectionType> direction_order) {
		ImagePlus image = PreProcessingFactory.getPreProcess(PreprocessingType.Pad).execute(img, new double[]{1.0, BACKGROUND_PIXEL_VAL});
		int[][] pixels = new int[0][0];
		image = removeSinglePixelLines(image, 4);
		if (Hu.area(image, 0, 0, image.getWidth(), image.getHeight()) > 0.0) {
			pixels = CollectionUtils.transpose(image.getProcessor().getIntArray());
		}
		List<int[][]> directional_matrices = new ArrayList<int[][]>();
		for (int i = 0; i < direction_order.size(); ++i) {
			int[][] dir = CollectionUtils.deep_copy(pixels);
			for (int[] row: dir)
			    Arrays.fill(row, 0);
			directional_matrices.add(dir);
		}
		List<Integer> chain = new ArrayList<Integer>();
		int last_column = 0;
		while (pixels.length > 0 && pixels[0].length > 0 && last_column < pixels[0].length) {
			int col = last_column;
			int row = 0;
			boolean found = false;
			while (!found && col < pixels[0].length) {
				for (row = 0; row < pixels.length; ++row) {
					if (pixels[row][col] == FOREGROUND_PIXEL_VAL) {
						found = true;
						break;
					}
				}
				if (!found)
					++col;
			}
			if (row < pixels.length && col < pixels[0].length) {
				int cur_row = row;
				int cur_col = col;
				List<Integer> chain_code = new ArrayList<Integer>();
				List<Integer> x = new ArrayList<Integer>();
				List<Integer> y = new ArrayList<Integer>();
				x.add(col);
				y.add(row);
				int loop = 0;
				int code = 0;
				boolean finish = false;
				for (; code < direction_order.size() && loop < direction_order.size(); code = (code + 1) % direction_order.size(), ++loop) {
					if (pixels[cur_row + direction_order.get(code).getRelativeRow()][cur_col + direction_order.get(code).getRelativeCol()] == FOREGROUND_PIXEL_VAL) {
						chain_code.add(direction_order.get(code).getCode());
						x.add(cur_col);
						y.add(cur_row);
						directional_matrices.get(code)[cur_row][cur_col] = 255;
						cur_row += direction_order.get(code).getRelativeRow();
						cur_col += direction_order.get(code).getRelativeCol();
						loop = -1;
						code = (code + direction_order.size() / 2) % direction_order.size();
						finish = true;
					}
					if (cur_row == row && cur_col == col && finish)
						break;
				}
				if (!chain_code.isEmpty()) {
					chain.addAll(chain_code);
				} else {
					chain.add(-1);
				}
				Polygon p = new Polygon(ArrayUtils.toPrimitive(x.toArray(new Integer[0])), ArrayUtils.toPrimitive(y.toArray(new Integer[0])), x.size());
				image.getProcessor().setColor(BACKGROUND_PIXEL_VAL);
				image.getProcessor().fillPolygon(p);
				image = removeSinglePixelLines(image, 4);
				if (Hu.area(image, 0, 0, image.getWidth(), image.getHeight()) > 0.0) {
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.HORIZONTAL_JOIN).execute(image, new double[]{});
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.VERTICAL_JOIN).execute(image, new double[]{});
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.PAD).execute(image, new double[]{1.0, BACKGROUND_PIXEL_VAL});
					pixels = CollectionUtils.transpose(image.getProcessor().getIntArray());
				} else {
					pixels = new int[0][0];
				}
			}
			last_column = 0;
		}
		return Pair.of(directional_matrices, chain);
	}
	
	/**
	 * Removes the single pixel lines.
	 * @param img The image from which remove the lines.
	 * @param minimum_black_pixel_number The minimum number of pixels which determine a pixel belong to a single pixel line.
	 * @return The input image without the single pixel lines.
	 */
	public static ImagePlus removeSinglePixelLines(ImagePlus img, int minimum_black_pixel_number) {
		double[][] pixels = CollectionUtils.filter2D(CollectionUtils.transpose(CollectionUtils.int_to_double(img.getProcessor().getIntArray())), 3, 3, new Function<double[][], Double>() {
			
			@Override
			public Double apply(double[][] arg0) {
				int black_pixel_number = 0;
				for(int i = 0; i < arg0.length; ++i) {
				    for(int j = 0; j < arg0[i].length; ++j) {
				        if (arg0[i][j] == FOREGROUND_PIXEL_VAL)
				        	++black_pixel_number;
				    }
				}
				if (black_pixel_number <= minimum_black_pixel_number)
					return (double) BACKGROUND_PIXEL_VAL;
				return arg0[arg0.length / 2][arg0[0].length / 2];
			}
			
		}, true, BACKGROUND_PIXEL_VAL);
		ImagePlus clean = new ImagePlus(img.getTitle(), img.getProcessor());
		clean.getProcessor().setIntArray(CollectionUtils.transpose(CollectionUtils.double_to_int(pixels, false)));
		return clean;
	}
	
	/**
	 * Performs the directions according to Hassaine - Al-Maadeed - Bouridane - A set of geometrical features for writer identification.
	 * @param img The input skeletonized image.
	 * @param smoothing_window The size of the smoothing window inside which all crosses are merged in one.
	 * @param neighbor_size The number of pixel to consider before and after the current pixel.
	 * @param maximum_linear_regression_error The maxum linear regression error to accept.
	 * @return The list of the lines found.
	 */
	public static List<Pair<Double, Pair<List<Integer>, List<Integer>>>> directions(ImagePlus img, int smoothing_window, int neighbor_size, double maximum_linear_regression_error) {
		ImagePlus image = img.duplicate();
		Pair<ImagePlus, Double> cross_info = crossFinder(image, 0);
		int[][] pixels = image.getProcessor().getIntArray();
		int[][] crosses = cross_info.getLeft().getProcessor().getIntArray();
		for (int i = smoothing_window / 2; i < crosses.length - smoothing_window / 2; ++i) {
			for (int j = smoothing_window / 2; j < crosses[i].length - smoothing_window / 2; ++j) {
				if (crosses[i][j] == FOREGROUND_PIXEL_VAL) {
					for(int k = 0; k < smoothing_window; ++k) {
						for(int l = 0; l < smoothing_window; ++l) {
							pixels[i - smoothing_window / 2 + k][j - smoothing_window / 2 + l] = BACKGROUND_PIXEL_VAL;
						}
					}
				}
			}
		}
		image.getProcessor().setIntArray(pixels);
		image = PreProcessingFactory.getPreProcess(PreprocessingType.Pad).execute(image, new double[]{1.0, BACKGROUND_PIXEL_VAL});
		List<DirectionType> codebook = new ArrayList<DirectionType>();
		codebook.add(DirectionType.UPPER_RIGHT);
		codebook.add(DirectionType.RIGHT);
		codebook.add(DirectionType.LOWER_RIGHT);
		codebook.add(DirectionType.LOWER);
		codebook.add(DirectionType.LOWER_LEFT);
		codebook.add(DirectionType.LEFT);
		codebook.add(DirectionType.UPPER_LEFT);
		codebook.add(DirectionType.UPPER);
		pixels = new int[0][0];
		//int[][] new_pixels = new int[0][0];
		if (Hu.area(image, 0, 0, image.getWidth(), image.getHeight()) > 0.0) {
			pixels = CollectionUtils.transpose(image.getProcessor().getIntArray());
			/*new_pixels = new int[pixels.length][];
			for (int i = 0; i < new_pixels.length; ++i) {
				new_pixels[i] = new int[pixels[i].length];
				Arrays.fill(new_pixels[i], BACKGROUND_PIXEL_VAL);
			}*/
		}
		List<Pair<Double, Pair<List<Integer>, List<Integer>>>> segments = new ArrayList<Pair<Double, Pair<List<Integer>, List<Integer>>>>();
		List<Double> angle = new ArrayList<Double>();
		List<Double> b0 = new ArrayList<Double>();
		List<Double> b1 = new ArrayList<Double>();
		List<Double> rmse = new ArrayList<Double>();
		int last_column = 0;
		while (pixels.length > 0 && pixels[0].length > 0 && last_column < pixels[0].length) {
			int col = last_column;
			int row = 0;
			boolean found = false;
			while (!found && col < pixels[0].length) {
				for (row = 0; row < pixels.length; ++row) {
					if (pixels[row][col] == FOREGROUND_PIXEL_VAL) {
						found = true;
						break;
					}
				}
				if (!found)
					++col;
			}
			if (row < pixels.length && col < pixels[0].length) {
				int clockwise_cur_row = row;
				int clockwise_cur_col = col;
				int counterclockwise_cur_row = clockwise_cur_row;
				int counterclockwise_cur_col = clockwise_cur_col;
				List<Integer> x = new ArrayList<Integer>();
				List<Integer> y = new ArrayList<Integer>();
				int loop = 0;
				int code = 0;
				for (; code < codebook.size() && loop < codebook.size() - 1; ++code, ++loop) {
					if (pixels[clockwise_cur_row + codebook.get(code).getRelativeRow()][clockwise_cur_col + codebook.get(code).getRelativeCol()] == FOREGROUND_PIXEL_VAL) {
						x.add(0, clockwise_cur_col);
						y.add(0, clockwise_cur_row);
						clockwise_cur_row += codebook.get(code).getRelativeRow();
						clockwise_cur_col += codebook.get(code).getRelativeCol();
						loop = -1;
						code = (code + codebook.size() / 2) % codebook.size();
					}
				}
				x.add(0, clockwise_cur_col);
				y.add(0, clockwise_cur_row);
				loop = 0;
				code = 4;
				for (; codebook.size() - 1 - code >= 0 && loop < codebook.size() - 1; ++code, ++loop) {
					if (pixels[counterclockwise_cur_row + codebook.get(codebook.size() - 1 - code).getRelativeRow()][counterclockwise_cur_col + codebook.get(codebook.size() - 1 - code).getRelativeCol()] == FOREGROUND_PIXEL_VAL) {
						x.add(counterclockwise_cur_col);
						y.add(counterclockwise_cur_row);
						counterclockwise_cur_row += codebook.get(codebook.size() - 1 - code).getRelativeRow();
						counterclockwise_cur_col += codebook.get(codebook.size() - 1 - code).getRelativeCol();
						loop = -1;
						code = (code + codebook.size() / 2) % codebook.size();
					}
				}
				x.add(counterclockwise_cur_col);
				y.add(counterclockwise_cur_row);
				for (int i = 0; i < x.size(); ++i) {
					pixels[y.get(i)][x.get(i)] = BACKGROUND_PIXEL_VAL;
					y.set(i, - y.get(i));
				}
				if (x.size() >= 2 * neighbor_size + 1) {
					for (int i = neighbor_size + 1; i < x.size() - neighbor_size - 1; ++i) {
						double[] x_ = CollectionUtils.int_to_double(ArrayUtils.toPrimitive(x.subList(i - neighbor_size - 1, i + neighbor_size).toArray(new Integer[0])));
						double[] y_ = CollectionUtils.int_to_double(ArrayUtils.toPrimitive(y.subList(i - neighbor_size - 1, i + neighbor_size).toArray(new Integer[0])));
						double x_mean = StatUtils.mean(x_);
						double y_mean = StatUtils.mean(y_);
						double b1_numerator = 0.0;
						double b1_denominator = 0.0;
						for (int j = 0; j < x_.length; ++j) {
							b1_numerator += (x_[j] - x_mean) * (y_[j] - y_mean);
							b1_denominator += (x_[j] - x_mean) * (x_[j] - x_mean);
						}
						b1.add(b1_numerator / b1_denominator);
						angle.add(Math.atan2(b1_numerator, b1_denominator));
						if (Double.isNaN(b1.get(b1.size() - 1))) {
							if (b1_numerator >= 0.0)
								b1.set(b1.size() - 1, Double.MAX_VALUE);
							else
								b1.set(b1.size() - 1, - Double.MAX_VALUE);
							b0.add(x_mean);
						} else {
							b0.add((y_mean - b1.get(b1.size() - 1) * x_mean));
						}
						double e = 0.0;
						for (int j = 0; j < x_.length; ++j) {
							double t = 0.0;
							if (b1.get(b1.size() - 1) < Double.MAX_VALUE && b1.get(b1.size() - 1) > - Double.MAX_VALUE) {
								t = (y_[j] - (b0.get(b0.size() - 1) + b1.get(b1.size() - 1) * x_[j]));
							}
							e += t * t;
						}
						int[] a = new int[4];
						Arrays.sort(a);
						rmse.add(Math.sqrt(e / (double) (x_.length - 2)));
						if (rmse.get(rmse.size() - 1) >= maximum_linear_regression_error) {
							angle.remove(angle.size() - 1);
							b0.remove(b0.size() - 1);
							b1.remove(b1.size() - 1);
							rmse.remove(rmse.size() - 1);
							continue;
						}
						segments.add(Pair.of(angle.get(angle.size() - 1), Pair.of(x, y)));
						/*for (int j = 0; j < x_.length; ++j) {
							if (b1.get(b1.size() - 1) < Double.MAX_VALUE) {
								new_pixels[(int) (- (b0.get(b0.size() - 1) + b1.get(b1.size() - 1) * x_[j]))][(int) x_[j]] = FOREGROUND_PIXEL_VAL;
							} else {
								new_pixels[(int) (- y_[j])][(int) (double) b0.get(b0.size() - 1)] = FOREGROUND_PIXEL_VAL;
							}
						}*/
					}
				}
				/*ImagePlus new_image = img.duplicate();
				new_image.getProcessor().setIntArray(CollectionUtils.transpose(new_pixels));
				Dashboard.saveImage(new_image, "minus_line.png");*/
				image.getProcessor().setIntArray(CollectionUtils.transpose(pixels));
				if (Hu.area(image, 0, 0, image.getWidth(), image.getHeight()) > 0.0) {
					//image.getProcessor().setIntArray(CollectionUtils.transpose(pixels));
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.HORIZONTAL_JOIN).execute(image, new double[]{});
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.VERTICAL_JOIN).execute(image, new double[]{});
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.PAD).execute(image, new double[]{1.0, BACKGROUND_PIXEL_VAL});
					//pixels = CollectionUtils.transpose(image.getProcessor().getIntArray());
				} else {
					pixels = new int[0][0];
				}
			}
			last_column = 0;
		}
		return segments;
	}
	
	/**
	 * Performs the tortuosity according to Hassaine - Al-Maadeed - Bouridane - A set of geometrical features for writer identification.
	 * @param img The input skeletonized image.
	 * @param smoothing_window The size of the smoothing window inside which all crosses are merged in one.
	 * @param line_number The number of lines to retrieve.
	 * @return The list of the lines found.
	 */
	public static List<List<Pair<Integer, Integer>>> tortuosity(ImagePlus img, int smoothing_window, int line_number) {
		List<List<Pair<Integer, Integer>>> lines = new ArrayList<List<Pair<Integer, Integer>>>();
		ImagePlus image = img.duplicate();
		Pair<ImagePlus, Double> cross_info = crossFinder(image, 0);
		int[][] pixels = image.getProcessor().getIntArray();
		int[][] crosses = cross_info.getLeft().getProcessor().getIntArray();
		for (int i = smoothing_window / 2; i < crosses.length - smoothing_window / 2; ++i) {
			for (int j = smoothing_window / 2; j < crosses[i].length - smoothing_window / 2; ++j) {
				if (crosses[i][j] == FOREGROUND_PIXEL_VAL) {
					for(int k = 0; k < smoothing_window; ++k) {
						for(int l = 0; l < smoothing_window; ++l) {
							pixels[i - smoothing_window / 2 + k][j - smoothing_window / 2 + l] = BACKGROUND_PIXEL_VAL;
						}
					}
				}
			}
		}
		image.getProcessor().setIntArray(pixels);
		image = PreProcessingFactory.getPreProcess(PreprocessingType.Pad).execute(image, new double[]{1.0, BACKGROUND_PIXEL_VAL});

		return lines;
	}
	
	/**
	 * Given a gray scale image it computes the run-length of each color level.
	 * @param img A binarized image.
	 * @return An HashMap which contains for each level the correspondent 45-run-length, 90-run-length, 135-run-length, 180-run-length.
	 */
	public static HashMap<Integer, int[][][]> runLength(ImagePlus img) {
		int[][] pixels = CollectionUtils.transpose(img.getProcessor().getIntArray());
		List<Integer> levels = CollectionUtils.getDistinctValues(CollectionUtils.toTrace(pixels, false));
		HashMap<Integer, int[][][]> run_length = new HashMap<Integer, int[][][]>();
		for (int i = 0; i < levels.size(); ++i) {
			int[][][] t = new int[4][pixels.length][pixels[0].length];
			for (int j = 0; j < t.length; ++j) {
				for (int k = 0; k < t[j].length; ++k) {
					for (int l = 0; l < t[j][k].length; ++l) {
						t[j][k][l] = 0;
					}
				}
			}
			run_length.put(levels.get(i), t);
		}
		for (int i = 0; i < pixels.length; ++i) {
			for (int j = 0; j < pixels[i].length; ++j) {
				for (int k = 0; k < run_length.get(pixels[i][j]).length; ++k) {
					run_length.get(pixels[i][j])[k][i][j] = 1;
				}
				int row = i - 1;
				int col = j + 1;
				// PI/4
				while (row >= 0 && col < pixels[row].length && pixels[row][col] == pixels[i][j]) {
					++run_length.get(pixels[i][j])[0][i][j];
					--row;
					++col;
				}
				row = i + 1;
				col = j - 1;
				while (row < pixels.length && col >= 0 && pixels[row][col] == pixels[i][j]) {
					++run_length.get(pixels[i][j])[0][i][j];
					++row;
					--col;
				}
				row = i - 1;
				col = j;
				// PI/2
				while (row >= 0 && pixels[row][col] == pixels[i][j]) {
					++run_length.get(pixels[i][j])[1][i][j];
					--row;
				}
				row = i + 1;
				col = j;
				while (row < pixels.length && pixels[row][col] == pixels[i][j]) {
					++run_length.get(pixels[i][j])[1][i][j];
					++row;
				}
				row = i - 1;
				col = j - 1;
				// 3PI/4
				while (row >= 0 && col >= 0 && pixels[row][col] == pixels[i][j]) {
					++run_length.get(pixels[i][j])[2][i][j];
					--row;
					--col;
				}
				row = i + 1;
				col = j + 1;
				while (row < pixels.length && col < pixels[row].length && pixels[row][col] == pixels[i][j]) {
					++run_length.get(pixels[i][j])[2][i][j];
					++row;
					++col;
				}
				row = i;
				col = j - 1;
				// PI
				while (col >= 0 && pixels[row][col] == pixels[i][j]) {
					++run_length.get(pixels[i][j])[3][i][j];
					--col;
				}
				row = i;
				col = j + 1;
				while (col < pixels[row].length && pixels[row][col] == pixels[i][j]) {
					++run_length.get(pixels[i][j])[3][i][j];
					++col;
				}
			}
		}
		return run_length;
	}
	
	/**
	 * Finds the edge track of each connected components.
	 * @param img The image into searching.
	 * @param direction_order The direction ordered list.
	 * @param mask_size The mask size.
	 * @param filled Whether to consider the filled mask or just the outer pixels.
	 * @return The edge track.
	 */
	public static int[][] edgeTrack(ImagePlus img, List<DirectionType> direction_order, int mask_size, boolean filled) {
		ImagePlus image = PreProcessingFactory.getPreProcess(PreprocessingType.Pad).execute(img, new double[]{(mask_size - 1) / 2, BACKGROUND_PIXEL_VAL});
		mask_size = MeasurementUtils.checkWindowDimension(mask_size);
		int[][] mask = new int[mask_size][mask_size];
		int size = filled ? 3 : mask_size;
		int crow = (mask_size - 1) / 2;
		int ccol = (mask_size - 1) / 2;
		for (; size <= mask_size; size += 2) {
			for (int i = 0; i < size; ++i) {
				mask[crow - (size - 1) / 2][ccol - (size - 1) / 2 + i] = 1;
			}
			for (int i = 1; i < size / 2; ++i) {
				mask[crow - (size - 1) / 2 + i][ccol - (size - 1) / 2] = 1;
			}
			for (int i = 1; i < (size + 1) / 2; ++i) {
				mask[crow - (size - 1) / 2 + i][ccol + (size - 1) / 2] = 1;
			}
		}
		int[][] pixels = new int[0][0];
		image = removeSinglePixelLines(image, 4);
		if (Hu.area(image, 0, 0, image.getWidth(), image.getHeight()) > 0.0) {
			pixels = CollectionUtils.transpose(image.getProcessor().getIntArray());
		}
		int[][] edge_tracking = new int[pixels.length][pixels[0].length];
		edge_tracking = CollectionUtils.deep_copy(pixels);
		for (int[] row : edge_tracking)
		    Arrays.fill(row, 0);
		int last_column = 0;
		while (pixels.length > 0 && pixels[0].length > 0 && last_column < pixels[0].length) {
			int col = last_column;
			int row = 0;
			boolean found = false;
			while (!found && col < pixels[0].length) {
				for (row = 0; row < pixels.length; ++row) {
					if (pixels[row][col] == FOREGROUND_PIXEL_VAL) {
						found = true;
						break;
					}
				}
				if (!found)
					++col;
			}
			if (row < pixels.length && col < pixels[0].length) {
				int cur_row = row;
				int cur_col = col;
				List<Integer> x = new ArrayList<Integer>();
				List<Integer> y = new ArrayList<Integer>();
				x.add(col);
				y.add(row);
				int loop = 0;
				int code = 0;
				boolean finish = false;
				for (; code < direction_order.size() && loop < direction_order.size(); code = (code + 1) % direction_order.size(), ++loop) {
					if (pixels[cur_row + direction_order.get(code).getRelativeRow()][cur_col + direction_order.get(code).getRelativeCol()] == FOREGROUND_PIXEL_VAL) {
						for (int i = 0; i < (mask_size + 1) / 2; ++i) {
							for (int j = 0; j < mask_size; ++j) {
								if ((mask_size - 1) / 2 != i && (mask_size - 1) / 2 != j && mask[i][j] == 1 && pixels[cur_row - (mask_size - 1) / 2 + i][cur_col - (mask_size - 1) / 2 + j] == FOREGROUND_PIXEL_VAL)
									++edge_tracking[cur_row][cur_col];
							}
						}
						x.add(cur_col);
						y.add(cur_row);
						cur_row += direction_order.get(code).getRelativeRow();
						cur_col += direction_order.get(code).getRelativeCol();
						loop = -1;
						code = (code + direction_order.size() / 2) % direction_order.size();
						finish = true;
					}
					if (cur_row == row && cur_col == col && finish)
						break;
				}
				Polygon p = new Polygon(ArrayUtils.toPrimitive(x.toArray(new Integer[0])), ArrayUtils.toPrimitive(y.toArray(new Integer[0])), x.size());
				image.getProcessor().setColor(BACKGROUND_PIXEL_VAL);
				image.getProcessor().fillPolygon(p);
				image = removeSinglePixelLines(image, 4);
				if (Hu.area(image, 0, 0, image.getWidth(), image.getHeight()) > 0.0) {
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.HORIZONTAL_JOIN).execute(image, new double[]{});
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.VERTICAL_JOIN).execute(image, new double[]{});
					//image = PreProcessingFactory.getPreProcess(PreprocessingType.PAD).execute(image, new double[]{1.0, BACKGROUND_PIXEL_VAL});
					pixels = CollectionUtils.transpose(image.getProcessor().getIntArray());
				} else {
					pixels = new int[0][0];
				}
			}
			last_column = 0;
		}
		return edge_tracking;
	}
}
