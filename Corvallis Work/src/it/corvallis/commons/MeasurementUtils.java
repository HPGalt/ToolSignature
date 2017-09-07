package it.corvallis.commons;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A set of measurement functions.
 * 
 * @author ChiarellaG
 *
 */
public class MeasurementUtils {

    /**
     * Checks if a dimension is odd and greater than or equal to three.
     * @param dimension The dimension to check.
     * @return the dimension if it is odd and greater than three or the dimension increased by one if it is greater than three but not odd or three if the dimension is less than three.
     */
    public static int checkWindowDimension(int dimension) {
    	if (dimension < 3)
    		return 3;
    	if (dimension % 2 == 0)
    		return dimension + 1;
    	return dimension;
    }
    
	/**
	 * Rounds a double at sixteenth decimal digit.
	 * @param d The number of round.
	 * @return The rounded number.
	 */
    public static double round16Decimals(double d) {
        DecimalFormatSymbols ds = new DecimalFormatSymbols();
        ds.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.0000000000000000", ds);
        return Double.valueOf(df.format(d));
    }
    
    /**
	 * Rounds a double at n-th decimal digit.
	 * @param d The number of round.
	 * @param n The number of decimal digits. If it is less then 2 n is set to 2, n otherwise.
	 * @return The rounded number.
	 */
    public static double roundDecimals(double d, int n) {
        DecimalFormatSymbols ds = new DecimalFormatSymbols();
        ds.setDecimalSeparator('.');
        String format = "0.";
        for (int i = 0; i < (n <= 2 ? 2 : n); ++i) {
			format += "0";
		}
        DecimalFormat df = new DecimalFormat(format, ds);
        return Double.valueOf(df.format(d));
    }
    
    /**
     * Performs array subtraction.
     * @param array1 The first term.
     * @param array2 The second term.
     * @return The subtraction of the two arrays.
     */
    public static double[] arrayDifference(double[] array1, double[] array2) {
        int length = Math.max(array1.length, array2.length);
        double[] result = new double[length];
        double[] newArray1 = Arrays.copyOf(array1, length);
        double[] newArray2 = Arrays.copyOf(array2, length);
        for (int i = 0; i < length; i++) {
            result[i] = newArray1[i] - newArray2[i];
        }
        return result;
    }

    /**
     * Performs array subtraction.
     * @param array1 The first term.
     * @param array2 The second term.
     * @return The subtraction of the two arrays.
     */
    public static int[] arrayDifference(int[] array1, int[] array2) {
        int length = Math.max(array1.length, array2.length);
        int[] result = new int[length];
        int[] newArray1 = Arrays.copyOf(array1, length);
        int[] newArray2 = Arrays.copyOf(array2, length);
        for (int i = 0; i < length; i++) {
            result[i] = newArray1[i] - newArray2[i];
        }
        return result;
    }
    
    /**
     * Finds the index of the highest number.
     * @param array The array into which find.
     * @return The index of the highest element.
     */
    public static int highestElementIndex(final int[] array) {
        int highest = Integer.MIN_VALUE;
        int index = -1;
        for(int i = 0; i < array.length; ++i) {
            if (array[i] > highest) {
                highest = array[i];
                index = i;
            }
        }
        
        return index;
    }
    
    /**
     * Finds the most frequent element in an array.
     * @param array The array into which find.
     * @return The most frequent element.
     */
    public static int mostFrequentElement(int[] array){
        Map<Integer, Integer> m = new HashMap<Integer, Integer>();
        for (int a : array) {
            Integer freq = m.get(a);
            m.put(a, (freq == null) ? 1 : freq + 1);
        }
        int max = -1;
        int mostFrequent = -1;
        for (Map.Entry<Integer, Integer> e : m.entrySet()) {
            if (e.getValue() > max) {
                mostFrequent = e.getKey();
                max = e.getValue();
            }
        }
        return mostFrequent;
    }
    
    /**
     * Performs the L2 norm of an array.
     * @param array The array of which compute the L2 norm.
     * @return The norm of the input array.
     */
    public static double calculateNorm(double[] array) {
    	double sum = 0.0;
    	for (int i = 0; i < array.length; ++i) {
			sum += array[i] * array[i];
		}
        return Math.sqrt(sum);
    }
    
    /**
     * Count the number of the not zet components of an array.
     * @param arrayToCheck The array into which find.
     * @return The number of the not zero components.
     */
    public static int numberOfNotZeroComponents(int[] arrayToCheck) {
        int numberOfNonZeroElements = 0;
        for (int currentSlant : arrayToCheck) {
            if (currentSlant != 0) {
                numberOfNonZeroElements++;
            }
        }
        return numberOfNonZeroElements;
    }
    
    /**
     * Computes the n-k binomial coefficient.
     * @param n The number of elements.
     * @param k The group elements number.
     * @return The n-k binomial coefficient.
     */
    public static long combinations(int n, int k) {
        long coeff = 1;
        for (int i = n - k + 1; i <= n; i++) {
            coeff *= i;
        }
        for (int i = 1; i <= k; i++) {
            coeff /= i;
        }
        return coeff;
    }
    
    /**
     * Logistic function.
     * @param x The x value.
     * @return The logistic output.
     */
	public static double logistic(double x) {
        return 1.0 / (1.0 + Math.exp(- x));
    }
	
	/**
	 * Computes the nearest forth power of x rounded up to the next forth power.
	 * @param x The input integer.
	 * @return The rounded up next foth power.
	 */
	public static int nearestForthPower(int x) {
		int pow = 0;
		while (x >> 2 > 1) {
			++pow;
			x = x >> 2;
		}
		++pow;
		return pow;
	}
	
	/**
	 * Computes the nearest square power of x rounded up to the next forth power.
	 * @param x The input integer.
	 * @return The rounded up next foth power.
	 */
	public static int nearestSquarePower(int x) {
		int pow = 0;
		while (x >> 1 > 1) {
			++pow;
			x = x >> 1;
		}
		++pow;
		return pow;
	}
}
