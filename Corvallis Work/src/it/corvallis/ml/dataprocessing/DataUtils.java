/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package it.corvallis.ml.dataprocessing;

import it.corvallis.commons.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;


/**
 * This class implements methods to manipulate data.
 * @author ChiarellaG
 */
public class DataUtils {
    public enum Preprocessing {
        Normal("normal", 0),
        ZScore("zscore", 1),
        MinMax("minmax", 2),
        
        /**
         * Iglewicz and Hoaglin (1993)
         */
        ModifiedZScore("ModifiedZScore", 3);
        
        private String name = null;
        private int id = 0;
        
        private Preprocessing(String name, int id) {
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
        public static Preprocessing getPreprocessingFromString(String type) {
        	for (int i = 0; i < Preprocessing.values().length; ++i) {
				if (type.trim().toLowerCase().compareTo(Preprocessing.values()[i].getName()) == 0)
					return Preprocessing.values()[i];
			}
        	return Normal;
        }
        
        /**
         * Convert an id into a Preprocessing, if the id reflect a known preprocessing type.
         * @param id The id to convert.
         * @return The string correspondent enum of BaseClassifierType, null if the string doesn't reflect a known classifier.
         */
        public static Preprocessing getPreprocessingFromId(int id) {
        	for (int i = 0; i < Preprocessing.values().length; ++i) {
				if (id == Preprocessing.values()[i].getId())
					return Preprocessing.values()[i];
			}
        	return Normal;
        }
    }
    
    /**
     * This method finds the min/max of a vector.
     * @param data The data into which to find the min/max.
     * @param op The operation: false = min, true = max.
     * @return The min/max.
     */
    public static double findMinMax(double[] data, boolean op) {
        double temp = data[0];
        for (int i = 1; i < data.length; ++i) {
            if (op == false && data[i] < temp) {
                temp = data[i];
            } else if (op == true && data[i] > temp) {
                temp = data[i];
            }
        }
        return temp;
    }
    
    /**
     * This method finds the min/max of a matrix.
     * @param data The data into which to find the min/max.
     * @param op The operation: false = min, true = max.
     * @param direction The direction along which finding the min/max: false = column search, true = row search.
     * @return The min/max vector.
     */
    public static double[] findMinMax(double[][] data, boolean op, boolean direction) {
        double[] m = new double[direction == false ? data[0].length : data.length];
        if (direction == false) {
            for (int i = 0; i < m.length; ++i) {
                m[i] = data[0][i];
            }
            for (int i = 1; i < data.length; ++i) {
                for (int j = 0; j < data[i].length; ++j) {
                    if (op == false) {
                        if (data[i][j] < m[j])
                            m[j] = data[i][j];
                    } else {
                        if (data[i][j] > m[j])
                            m[j] = data[i][j];
                    }
                }
            }
        } else {
            for (int i = 0; i < m.length; ++i) {
                m[i] = data[i][0];
            }
            for (int i = 0; i < data.length; ++i) {
                for (int j = 1; j < data[i].length; ++j) {
                    if (op == false) {
                        if (data[i][j] < m[i])
                            m[i] = data[i][j];
                    } else {
                        if (data[i][j] > m[i])
                            m[i] = data[i][j];
                    }
                }
            }
        }
        return m;
    }
    
    /**
     * This method finds the min/max of a vector.
     * @param data The data into which to find the min/max.
     * @param op The operation: false = min, true = max.
     * @return The min/max.
     */
    public static int findMinMaxIndex(int[] data, boolean op) {
        int temp = 0;
        for (int i = 1; i < data.length; ++i) {
            if (op == false && data[i] < data[temp]) {
                temp = i;
            } else if (op == true && data[i] > data[temp]) {
                temp = i;
            }
        }
        return temp;
    }
    
    /**
     * This method finds the min/max of a vector.
     * @param data The data into which to find the min/max.
     * @param op The operation: false = min, true = max.
     * @return The min/max.
     */
    public static int findMinMaxIndex(double[] data, boolean op) {
        int temp = 0;
        for (int i = 1; i < data.length; ++i) {
            if (op == false && data[i] < data[temp]) {
                temp = i;
            } else if (op == true && data[i] > data[temp]) {
                temp = i;
            }
        }
        return temp;
    }
    
    
    /**
     * This method finds the min/max of a matrix.
     * @param data The data into which to find the min/max.
     * @param op The operation: false = min, true = max.
     * @param direction The direction along which finding the min/max: false = column search, true = row search.
     * @return The min/max vector.
     */
    public static int[] findMinMaxIndexes(double[][] data, boolean op, boolean direction) {
        int[] m = new int[direction == false ? data[0].length : data.length];
        if (direction == false) {
            for (int i = 0; i < m.length; ++i) {
                m[i] = 0;
            }
            for (int i = 1; i < data.length; ++i) {
                for (int j = 0; j < data[i].length; ++j) {
                    if (op == false) {
                        if (data[i][j] < data[m[j]][j])
                            m[j] = i;
                    } else {
                        if (data[i][j] > data[m[j]][j])
                            m[j] = i;
                    }
                }
            }
        } else {
            for (int i = 0; i < m.length; ++i) {
                m[i] = 0;
            }
            for (int i = 0; i < data.length; ++i) {
                for (int j = 1; j < data[i].length; ++j) {
                    if (op == false) {
                        if (data[i][j] < data[i][m[i]])
                            m[i] = j;
                    } else {
                        if (data[i][j] > data[i][m[i]])
                            m[i] = j;
                    }
                }
            }
        }
        return m;
    }
    
    /**
     * Computes the mean columnwise.
     * @param dataset The input data.
     * @return The means vector.
     */
    public static double[] mean(double[][] dataset) {
        double[] mean = new double[dataset[0].length];
        Arrays.fill(mean, 0.0);
        for (int i = 0; i < dataset.length; ++i) {
            for (int j = 0; j < dataset[i].length; ++j) {
                mean[j] += dataset[i][j];
            }
        }
        for (int i = 0; i < mean.length; ++i) {
            mean[i] /= (double) dataset.length;
        }
        return mean;
    }
    
    /**
     * Computes the variance columnwise.
     * @param dataset The input data.
     * @return The variances vector.
     */
    public static double[] variance(double[][] dataset) {
        double[] mean = mean(dataset);
        double[] var = new double[mean.length];
        Arrays.fill(var, 0.0);
        for (int i = 0; i < dataset.length; ++i) {
            for (int j = 0; j < dataset[i].length; ++j) {
                var[j] += (dataset[i][j] - mean[j]) * (dataset[i][j] - mean[j]);
            }
        }
        for (int i = 0; i < var.length; ++i) {
            var[i] /= (double) dataset.length - 1.0;
        }
        return var;
    }

    /**
     * Computes the standard deviation columnwise.
     * @param dataset The input data.
     * @return The standard deviations vector.
     */
    public static double[] std(double[][] dataset) {
        double[] std = variance(dataset);
        for (int i = 0; i < std.length; ++i) {
            std[i] = Math.sqrt(std[i]);
        }
        return std;
    }
    
    /**
     * Performes the Z score normalization.
     * @param dataset The dataset to normalize.
     * @return The normalized dataset.
     */
    public static double[][] zScore(double[][] dataset) {
        double[][] data = CollectionUtils.deep_copy(dataset);
        // Compute the mean vector
        double[] mean = mean(data);
        // Standard deviation vector
        double[] std = std(data);
        /*for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
            	if (std[j] > 0.0)
            		data[i][j] = (data[i][j] - mean[j]) / std[j];
            	else
            		data[i][j] = 0.0;
            }
        }*/
        return normalize(dataset, mean, std);
    }
    
    /**
     * Compute the columnwise median value.
     * @param dataset The input data.
     * @return The median vector.
     */
    public static double[] median(double[][] dataset) {
    	double[] median = new double[dataset[0].length];
    	for (int i = 0; i < median.length; ++i) {
    		median[i] = median(CollectionUtils.getColumn(dataset, i));
		}
    	return median;
    }
    
    /**
     * Returns the median value of an array
     * @param arr The array to search in.
     * @return The meadian value.
     */
    public static double median(double[] arr) {
    	if (arr == null || arr.length == 0)
    		return Double.NaN;
    	Arrays.sort(arr);
		return arr[arr.length / 2];
    }
    
    /**
     * Performs the MinMax normalization considering the whole dataset.
     * @param dataset The dataset to normalize.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The normalized matrix data.
     */
    public static double[][] minmax(double[][] dataset, double min, double max) {
        double[] new_min = new double[dataset[0].length];
        double[] new_max = new double[new_min.length];
        if (min > max) {
            double t = min;
            min = max;
            max = t;
        }
        Arrays.fill(new_min, min);
        Arrays.fill(new_max, max);
        double[][] temp = CollectionUtils.deep_copy(dataset);
        double[] old_min = findMinMax(dataset, false, false);
        double[] old_max = findMinMax(dataset, true, false);
        for (int i = 0; i < dataset.length; ++i) {
            for (int j = 0; j < dataset[i].length; ++j) {
                if (old_max[j] - old_min[j] == 0.0) {
                	temp[i][j] = new_max[j];
                } else {
                	temp[i][j] = (dataset[i][j] - old_min[j]) * (new_max[j] - new_min[j]) / (old_max[j] - old_min[j]) + new_min[j];
                }
            }
        }
        return temp;
    }
    
    /**
     * Performs the MinMax normalization considering the whole dataset.
     * @param data The dataset to normalize.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The normalized matrix data.
     */
    public static double[][] globalMinmax(double[][] data, double min, double max) {
        double[][] temp = CollectionUtils.deep_copy(data);
        if (min > max) {
            double t = min;
            min = max;
            max = t;
        }
        double maximum = Double.NEGATIVE_INFINITY;
        double minimum = Double.POSITIVE_INFINITY;
        for (int i = 0; i < temp.length; ++i) {
            for (int j = 0; j < temp[i].length; ++j) {
                if (temp[i][j] > maximum)
                    maximum = temp[i][j];
                else if (temp[i][j] < minimum)
                    minimum = temp[i][j];
            }
        }
        if (minimum != maximum) {
	        for (int i = 0; i < temp.length; ++i) {
	            for (int j = 0; j < temp[i].length; ++j) {
	                temp[i][j] = (int) ((double) (temp[i][j] - minimum) * (double) (max - min) / (double) (maximum - minimum)) + min;
	            }
	        }
        } else {
        	for (int i = 0; i < temp.length; ++i) {
	            Arrays.fill(temp[i], max);
	        }
        }
        return temp;
    }
    
    /**
     * Performs the MinMax normalization considering the whole dataset.
     * @param data The dataset to normalize.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The normalized matrix data.
     */
    public static int[][] globalMinmax(int[][] data, int min, int max) {
        int[][] temp = CollectionUtils.deep_copy(data);
        if (min > max) {
            int t = min;
            min = max;
            max = t;
        }
        int maximum = Integer.MIN_VALUE;
        int minimum = Integer.MAX_VALUE;
        for (int i = 0; i < temp.length; ++i) {
            for (int j = 0; j < temp[i].length; ++j) {
                if (temp[i][j] > maximum)
                    maximum = temp[i][j];
                else if (temp[i][j] < minimum)
                    minimum = temp[i][j];
            }
        }
        if (minimum != maximum) {
	        for (int i = 0; i < temp.length; ++i) {
	            for (int j = 0; j < temp[i].length; ++j) {
	                temp[i][j] = (int) (((double) (temp[i][j] - minimum) * (double) (max - min) / (double) (maximum - minimum)) + (double) min);
	            }
	        }
        } else {
        	for (int i = 0; i < temp.length; ++i) {
	            Arrays.fill(temp[i], max);
	        }
        }
        return temp;
    }
    
    /**
     * Generates a set of instances drawn from an Independent Gaussian Distributions.
     * @param mean The mean vector.
     * @param std The standard deviations of the Gaussian distributions.
     * @param instances_num The number of instances.
     * @return A matrix into which are saved the instances row-wise.
     */
    public static double[][] generateIndependentElementwiseGaussianVectors(double[] mean, double[] std, int instances_num) {
        if (mean.length != std.length) {
            if (mean.length > std.length) {
                double[] temp = new double[std.length];
                System.arraycopy(mean, 0, temp, 0, temp.length);
                mean = new double[temp.length];
                System.arraycopy(temp, 0, mean, 0, temp.length);
            } else {
                double[] temp = new double[mean.length];
                System.arraycopy(std, 0, temp, 0, temp.length);
                std = new double[temp.length];
                System.arraycopy(temp, 0, std, 0, temp.length);
            }
        }
        double[][] instances = new double[instances_num][mean.length];
        Random randomGenerator = new Random();
        for (int i = 0; i < instances_num; ++i) {
            for (int j = 0; j < instances[i].length; ++j) {
                //instances[i][j] = Erf.erfInv(randomGenerator.nextDouble() * 2.0 - 1.0) * std[j] * Math.sqrt(2.0) + mean[j];
                instances[i][j] = randomGenerator.nextDouble() * std[j] + mean[j];
            }
        }
        return instances;
    }
    
    /**
     * Generates a set of instances drawn from a Multivariate Gaussian Distribution.
     * @param mean The mean vector.
     * @param covariance The covariance matrix.
     * @param instances_num The number of instances.
     * @return A matrix into which are saved the instances row-wise.
     */
    public static double[][] generateMultivariateGaussianVectors(double[] mean, double[][] covariance, int instances_num) {
        double[][] instances = new double[instances_num][mean.length];
        for (int i = 0; i < instances_num; ++i) {
            instances[i] = new MultivariateNormalDistribution(mean, covariance).sample();
        }
        return instances;
    }
    
    
    /*public static double[][] generateIndependentElementwiseGaussianVectorsMultidistribution(double[][] means, double[] std, int instances_num) {
    	double[][] data = new double[0][0];
    	for (int i = 0; i < means.length; ++i) {
			data = CollectionUtils.concat(data, generateIndependentElementwiseGaussianVectors(means[i], std, instances_num));
		}
    	return data;
    }

    
    public static double[][] generateMultivariateGaussianVectorsMultidistribution(double[][] means, double[][] covariance, int instances_num) {
    	double[][] data = new double[0][0];
    	for (int i = 0; i < means.length; ++i) {
			data = CollectionUtils.concat(data, generateMultivariateGaussianVectors(means[i], covariance, instances_num));
		}
    	return data;
    }*/
    
    /**
     * @param data The data from which generate other data.
     * @param percentage The data percentage to generate.
     * @param nearest_neighbors The number of the vector from which generate the synthetic sample.
     * @return The data generated plus the original data.
     * Algorithm SMOTE: Synthetic Minority Over-sampling Technique
     */
    public static double[][] generateKNearestVectors(double[][] data, double percentage, int nearest_neighbors) {
    	if (percentage < 1.0)
    		percentage = 1.0;
    	double[][] copy = CollectionUtils.deep_copy(data);
    	List<double[]> new_data = new ArrayList<double[]>();
    	for (int i = 0; i < copy.length; ++i) {
			new_data.add(CollectionUtils.getRow(copy, i));
		}
    	Random rand = new Random();
    	for (int i = 0; i < copy.length; ++i) {
    		Set<Integer> generated = new LinkedHashSet<Integer>();
			while (generated.size() < nearest_neighbors) {
			    generated.add(rand.nextInt(copy.length));
			}
			List<Integer> selected = new ArrayList<Integer>(generated);
			for (int j = 0; j < (int) percentage; ++j) {
				int nn = rand.nextInt(nearest_neighbors);
				double[] synthetic = new double[copy[selected.get(nn)].length];
				for (int k = 0; k < synthetic.length; ++k) {
					synthetic[k] = copy[i][k] + rand.nextDouble() * (copy[selected.get(nn)][k] - copy[i][k]);
				}
				new_data.add(synthetic);
			}
		}
    	return new_data.toArray(new double[][]{});
    }
    
    /**
     * Finds the zero variance columns.
     * @param dataset The dataset to search in.
     * @return The indexes of the zero variance columns.
     */
    public static int[] zeroVarianceColumns(double[][] dataset) {
        double[] variance = variance(dataset);
        List<Integer> min_variance_cols = new ArrayList<Integer>();
        for (int i = 0; i < variance.length; ++i) {
            if (variance[i] == 0.0)
                min_variance_cols.add(i);
        }
        return ArrayUtils.toPrimitive(min_variance_cols.toArray(new Integer[min_variance_cols.size()]));
    }
    
    /**
     * Filters by class.
     * @param dataset The source dataset.
     * @param label The label of the class to keep/remove. 0 is the first label present in the dataset, 1 is the second and so on.
     * @param invert_selection If false the 'label' class will be kept and will be removed all the others, if true the 'label' class will be removed and will be kept all the others.
     * @return 'dataset' filtered.
     */
    public static double[][] filterByClass(double[][] dataset, double label, boolean invert_selection) {
        double[][] new_dataset = CollectionUtils.deep_copy(dataset);
        for (int i = dataset.length - 1; i >= 0; --i) {
            if (!invert_selection) {
                if (new_dataset[i][new_dataset[i].length - 1] != label) {
                    new_dataset = CollectionUtils.removeRows(new_dataset, new int[]{i});
                }
            } else {
                if (new_dataset[i][new_dataset[i].length - 1] == label) {
                    new_dataset = CollectionUtils.removeRows(new_dataset, new int[]{i});
                }
            }
        }
        return new_dataset;
    }
    
    /**
     * Scales a matrix using formula: (x - translation) / scale.
     * @param dataset The dataset to scale.
     * @param translation The translation factor.
     * @param scale The scaling factor.
     * @return The scaled dataset.
     */
    public static double[][] normalize(double[][] dataset, double[] translation, double[] scale) {
    	double[][] copy = CollectionUtils.deep_copy(dataset);
    	for (int i = 0; i < copy.length; ++i) {
			for (int j = 0; j < copy[i].length; ++j) {
				if (scale[j] != 0.0)
					copy[i][j] = (copy[i][j] - translation[j]) / scale[j];
            	else
            		copy[i][j] = 0.0;
			}
		}
    	return copy;
    }
    
    /**
     * Translate a matrix using formula: (x - translation).
     * @param dataset The dataset to scale.
     * @param translation The translation factor.
     * @return The translated dataset.
     */
    public static double[][] translate(double[][] dataset, double[] translation) {
    	double[][] copy = CollectionUtils.deep_copy(dataset);
    	for (int i = 0; i < copy.length; ++i) {
			for (int j = 0; j < copy[i].length; ++j) {
				copy[i][j] = (copy[i][j] - translation[j]);
			}
		}
    	return copy;
    }
    
    /**
     * Translate a matrix using formula: (x - translation).
     * @param dataset The dataset to scale.
     * @param scale The scale factor.
     * @return The translated dataset.
     */
    public static double[][] scale(double[][] dataset, double[] scale) {
    	double[][] copy = CollectionUtils.deep_copy(dataset);
    	for (int i = 0; i < copy.length; ++i) {
			for (int j = 0; j < copy[i].length; ++j) {
				if (scale[j] != 0.0)
					copy[i][j] = copy[i][j] / scale[j];
            	else
            		copy[i][j] = 0.0;
			}
		}
    	return copy;
    }
    
    /**
	 * Thresholds a matrix given a threshold.
	 * @param data The matrix to threshold.
	 * @param threshold The threshold.
	 * @param low_value The value to set if the current value is less than the threshold.
	 * @param high_value The value to set if the current value is greater than the threshold.
	 * @return The thresholded image.
	 */
	public static int[][] threshold(int[][] data, int threshold, int low_value, int high_value) {
		int[][] pixels = CollectionUtils.deep_copy(data);
		for (int i = 0; i < pixels.length; ++i) {
			for (int j = 0; j < pixels[i].length; ++j) {
				if (pixels[i][j] <= threshold)
					pixels[i][j] = low_value;
				else
					pixels[i][j] = high_value;
			}
		}
		return pixels;
	}
	
	/**
	 * Thresholds a matrix given a threshold.
	 * @param data The matrix to threshold.
	 * @param threshold The threshold.
	 * @param low_value The value to set if the current value is less than the threshold.
	 * @param high_value The value to set if the current value is greater than the threshold.
	 * @return The thresholded image.
	 */
	public static double[][] threshold(double[][] data, double threshold, double low_value, double high_value) {
		double[][] pixels = CollectionUtils.deep_copy(data);
		for (int i = 0; i < pixels.length; ++i) {
			for (int j = 0; j < pixels[i].length; ++j) {
				if (pixels[i][j] < threshold)
					pixels[i][j] = low_value;
				else
					pixels[i][j] = high_value;
			}
		}
		return pixels;
	}
}
