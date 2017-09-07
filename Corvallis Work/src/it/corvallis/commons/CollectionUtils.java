package it.corvallis.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;

/**
 * A set of collection service functions.
 * 
 * @author ChiarellaG
 * 
 */
public class CollectionUtils {
    
    /** Computes the ascending version of a collection of Double.
     * @param coll Collection source.
     * @return Sorted version of the parameter without duplicates.
     */
    public static double[] doubleCollectionOrder(Collection<Double> coll) {
        HashSet<Double> set = new HashSet<Double>();
        set.addAll(coll);
        int size = set.size();
        double[] tmpArray = new double[size];
        int i = 0;
        for (Double stDev : set) {
            tmpArray[i] = stDev;
            ++i;
        }
        Arrays.sort(tmpArray);
        return tmpArray;
    }
    
    /**
     * Merges two array in one.
     * @param a First array.
     * @param b Second array.
     * @return A array which is the result of merging the two parameters.
     */
    public static double[] concat(double[] a, double[] b) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0))
            return new double[0];
        else if (a == null || a.length == 0) {
            double[] c = deep_copy(b);
            return c;
        } else if (b == null || b.length == 0) {
            double[] c = deep_copy(a);
            return c;
        }
        double[] c = new double[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
    
    /**
     * Merges two array in one.
     * @param a First array.
     * @param b Second array.
     * @return A array which is the result of merging the two parameters.
     */
    public static int[] concat(int[] a, int[] b) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0))
            return new int[0];
        else if (a == null || a.length == 0) {
        	int[] c = deep_copy(b);
            return c;
        } else if (b == null || b.length == 0) {
        	int[] c = deep_copy(a);
            return c;
        }
        int[] c = new int[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
    
    /**
     * Merges two matrices in one which have the two matrices in column.
     * @param a First matrix.
     * @param b Second matrix.
     * @return A matrix which have the two parameter matrices in column.
     */
    public static double[][] concat(double[][] a, double[][] b) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0))
            return new double[0][0];
        else if (a == null || a.length == 0) {
            return deep_copy(b);
        } else if (b == null || b.length == 0) {
            return deep_copy(a);
        }
        double[][] c = new double[a.length + b.length][];
        for (int i = 0; i < a.length; ++i) {
            c[i] = deep_copy(a[i]);
        }
        for (int i = 0; i < b.length; ++i) {
            c[i + a.length] = deep_copy(b[i]);
        }
        return c;
    }
    
    /**
     * Merges two matrices in one which have the two matrices in column.
     * @param a First matrix.
     * @param b Second matrix.
     * @return A matrix which have the two parameter matrices in column.
     */
    public static double[][] columnConcat(double[][] a, double[][] b) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0))
            return new double[0][0];
        else if ((a == null || a.length == 0)) {
            return deep_copy(b);
        } else if ((b == null || b.length == 0)) {
            return deep_copy(a);
        }
        double[][] c = new double[a.length + b.length][];
        for (int i = 0; i < a.length; ++i) {
            c[i] = deep_copy(a[i]);
        }
        for (int i = 0; i < b.length; ++i) {
            c[i + a.length] = deep_copy(b[i]);
        }
        return c;
    }
    
    /**
     * Merges two matrices in one which have the two matrices in column.
     * @param a First matrix.
     * @param b Second matrix.
     * @return A matrix which have the two parameter matrices in column.
     */
    public static int[][] columnConcat(int[][] a, int[][] b) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0))
            return new int[0][0];
        else if ((a == null || a.length == 0)) {
            return deep_copy(b);
        } else if ((b == null || b.length == 0)) {
            return deep_copy(a);
        }
        int[][] c = new int[a.length + b.length][];
        for (int i = 0; i < a.length; ++i) {
            c[i] = deep_copy(a[i]);
        }
        for (int i = 0; i < b.length; ++i) {
            c[i + a.length] = deep_copy(b[i]);
        }
        return c;
    }
    
    /**
     * Merges two matrices in one which have the two matrices in column.
     * @param a First matrix.
     * @param b Second matrix.
     * @return A matrix which have the two parameter matrices in column.
     */
    public static double[][] rowConcat(double[][] a, double[][] b) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0))
            return new double[0][0];
        else if ((a == null || a.length == 0)) {
            return deep_copy(b);
        } else if ((b == null || b.length == 0)) {
            return deep_copy(a);
        }
        double[][] c = new double[Math.max(a.length, b.length)][];
        for (int i = 0; i < c.length; ++i) {
        	if (i < a.length && i < b.length)
        		c[i] = concat(a[i], b[i]);
        	else if (i < a.length)
        		c[i] = deep_copy(a[i]);
        	else
        		c[i] = deep_copy(b[i]);
        }
        return c;
    }
    
    /**
     * Merges two matrices in one which have the two matrices in column.
     * @param a First matrix.
     * @param b Second matrix.
     * @return A matrix which have the two parameter matrices in column.
     */
    public static int[][] rowConcat(int[][] a, int[][] b) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0))
            return new int[0][0];
        else if ((a == null || a.length == 0)) {
            return deep_copy(b);
        } else if ((b == null || b.length == 0)) {
            return deep_copy(a);
        }
        int[][] c = new int[Math.max(a.length, b.length)][];
        for (int i = 0; i < c.length; ++i) {
        	if (i < a.length && i < b.length)
        		c[i] = concat(a[i], b[i]);
        	else if (i < a.length)
        		c[i] = deep_copy(a[i]);
        	else
        		c[i] = deep_copy(b[i]);
        }
        return c;
    }
    
    /**
     * Removes a list of columns from a matrix.
     * @param a Matrix from which remove columns.
     * @param column Array that indicates the columns to remove.
     * @return The matrix 'a' without the columns listed in 'column'.
     */
    public static double[][] removeColumns(double[][] a, int[] column) {
    	if (a == null || a.length == 0)
    		return new double[0][0];
    	double[][] temp;
    	Arrays.sort(column);
        if (column.length == 0) {
        	temp = deep_copy(a);
        } else if (column.length == 1) {
            temp = new double[a.length][];
            for (int i = 0; i < temp.length; ++i) {
            	if (column[0] >= a[i].length) {
            		temp[i] = deep_copy(a[i]);
            	} else {
            		temp[i] = new double[a[i].length - 1];
            		System.arraycopy(a[i], 0, temp[i], 0, column[0]);
            		System.arraycopy(a[i], column[0] + 1, temp[i], column[0], a[i].length - column[0] - 1);
            	}
			}
        } else {
        	temp = deep_copy(a);
        	for (int i = 0; i < column.length; ++i) {
				temp = removeColumns(temp, new int[]{column[i] - i});
			}
        }
        return temp;
    }
    
    /**
     * Removes a list of rows from a matrix.
     * @param a Matrix from which remove rows.
     * @param row Array that indicates the rows to remove.
     * @return The matrix 'a' without the rows listed in 'row'.
     */
    public static double[][] removeRows(double[][] a, int[] row) {
        List<double[]> temp = new ArrayList<double[]>();
        for (int i = 0; i < a.length; ++i) {
            if (isContained(Arrays.asList(ArrayUtils.toObject(int_to_double(row))), i))
                continue;
            temp.add(deep_copy(a[i]));
        }
        return temp.toArray(new double[][]{});
    }
    
    /**
     * Removes a list of elements from a vector.
     * @param a Array from which remove elements.
     * @param elements Array that indicates the elements to remove.
     * @return The array 'a' without the elements listed in 'elements'.
     */
    public static int[] removeElements(int[] a, int[] elements) {
    	int[] temp = new int[a.length - elements.length];
        if (elements.length == 0) {
            temp = deep_copy(a);
        } else {
            for (int i = 0; i < elements[0]; ++i) {
                temp[i] = a[i];
            }
            for (int i = 1; i < elements.length; ++i) {
                for (int j = elements[i - 1] + 1; j < elements[i]; ++j) {
                    temp[j - i] = a[j];
                }
            }
            for (int i = elements[elements.length - 1] + 1; i < a.length; ++i) {
                temp[i - elements.length] = a[i];
            }
        }
        return temp;
    }
    
    /**
     * Removes a list of elements from a vector.
     * @param a Array from which remove elements.
     * @param elements Array that indicates the elements to remove.
     * @return The array 'a' without the elements listed in 'elements'.
     */
    public static double[] removeElements(double[] a, int[] elements) {
        double[] temp = new double[a.length - elements.length];
        if (elements.length == 0) {
            temp = deep_copy(a);
        } else {
            for (int i = 0; i < elements[0]; ++i) {
                temp[i] = a[i];
            }
            for (int i = 1; i < elements.length; ++i) {
                for (int j = elements[i - 1] + 1; j < elements[i]; ++j) {
                    temp[j - i] = a[j];
                }
            }
            for (int i = elements[elements.length - 1] + 1; i < a.length; ++i) {
                temp[i - elements.length] = a[i];
            }
        }
        return temp;
    }
    
    /**
     * Verifies whether or not an element is contained in the list.
     * @param list The list of elements.
     * @param elem The element to looking for in the list.
     * @return Whether or not the element is contained in the list.
     */
    public static boolean isContained(List<Double> list, double elem) {
        if (list.isEmpty())
            return false;
        return list.parallelStream().anyMatch((object) -> (elem == object));
    }
    
    /**
     * Verifies whether or not an element is contained in the list.
     * @param list The list of elements.
     * @param elem The element to looking for in the list.
     * @return Whether or not the element is contained in the list.
     */
    public static boolean isContained(List<Integer> list, int elem) {
        if (list.isEmpty())
            return false;
        return list.parallelStream().anyMatch((object) -> (elem == object));
    }
    
    /**
     * Retrieves the number of distinct values in a column. If the column index is negative or greater than the available columns It returns the distinct values of the last column.
     * @param data The table into which looking for.
     * @param column The column of the table 0-indexed.
     * @return The list of all distinct values.
     */
    public static List<Double> getDistinctValues(double[][] data, int column) {
        List<Double> distinct_values = new ArrayList<Double>();
        if (column < 0 || column >= data[0].length) {
            column = data[0].length - 1;
        }
        if (column < data[0].length) {
            for (int i = 0; i < data.length; ++i) {
                if (!isContained(distinct_values, data[i][column]))
                    distinct_values.add(data[i][column]);
            }
        }
        
        return getDistinctValues(ArrayUtils.toPrimitive(distinct_values.toArray(new Double[0])));
    }
    
    /**
     * Retrieves the number of distinct values in a column. If the column index is negative or greater than the available columns It returns the distinct values of the last column.
     * @param data The table into which looking for.
     * @param column The column of the table 0-indexed.
     * @return The list of all distinct values.
     */
    public static List<Integer> getDistinctValues(int[][] data, int column) {
        List<Integer> distinct_values = new ArrayList<Integer>();
        if (column < 0 || column >= data[0].length) {
            column = data[0].length - 1;
        }
        if (column < data[0].length) {
            for (int i = 0; i < data.length; ++i) {
                if (!isContained(distinct_values, data[i][column]))
                    distinct_values.add(data[i][column]);
            }
        }
        
        return getDistinctValues(ArrayUtils.toPrimitive(distinct_values.toArray(new Integer[0])));
    }
    
    /**
     * Retrieves the number of distinct values in a column.
     * @param data The table into which looking for.
     * @return The list of all distinct values.
     */
    public static List<Double> getDistinctValues(double[] data) {
        HashSet<Double> set = new HashSet<Double>();
        set.addAll(Arrays.asList(ArrayUtils.toObject(data)));
        Double[] temp = set.toArray(new Double[0]);
        Arrays.sort(temp);
        return Arrays.asList(temp);
    }
    
    /**
     * Retrieves the number of distinct values in a column.
     * @param data The table into which looking for.
     * @return The list of all distinct values.
     */
    public static List<Integer> getDistinctValues(int[] data) {
    	HashSet<Integer> set = new HashSet<Integer>();
        set.addAll(Arrays.asList(ArrayUtils.toObject(data)));
        Integer[] temp = set.toArray(new Integer[0]);
        Arrays.sort(temp);
        return Arrays.asList(temp);
    }

    /**
     * Transpose a matrix.
     * @param data The input data.
     * @return  The transposed input data.
     */
    public static double[][] transpose(double[][] data) {
        double[][] t = new double[data[0].length][data.length];
        for (int i = 0; i < t.length; ++i) {
            for (int j = 0; j < t[i].length; ++j) {
                t[i][j] = data[j][i];
            }
        }
        return t;
    }
    
    /**
     * Transpose a matrix.
     * @param data The input data.
     * @return  The transposed input data.
     */
    public static int[][] transpose(int[][] data) {
    	int[][] t = new int[data[0].length][data.length];
        for (int i = 0; i < t.length; ++i) {
            for (int j = 0; j < t[i].length; ++j) {
                t[i][j] = data[j][i];
            }
        }
        return t;
    }
    
    /**
     * Extracts a column from a matrix. If the column index is negative or greater than the available columns It returns null.
     * @param data The source matrix.
     * @param column The column to extract.
     * @return The column of the matrix.
     */
    public static double[] getColumn(double[][] data, int column) {
        List<Double> col = new ArrayList<Double>();
        if (column < 0 || column >= data[0].length) {
            return new double[0];
        }
        for (int i = 0; i < data.length; ++i) {
            col.add(data[i][column]);
        }
        return ArrayUtils.toPrimitive(col.toArray(new Double[]{}));
    }
    
    /**
     * Extracts columns from a matrix. If there is no columns to extract It returns null.
     * @param data The source matrix.
     * @param columns The columns to extract.
     * @return The columns of the matrix.
     */
    public static double[][] getColumns(double[][] data, int[] columns) {
        List<double[]> cols = new ArrayList<double[]>();
        for (int i = 0; i < columns.length; ++i) {
            cols.add(getColumn(data, columns[i]));
            if (cols.get(cols.size() - 1) == null)
                cols.remove(cols.size() - 1);
        }
        if (cols.isEmpty())
            return null;
        return transpose(cols.toArray(new double[][]{}));
    }
    
    /**
     * Adds a column to the end of a matrix. If the number of rows in the matrix don't match to the rows in the column, It is truncated the biggest.
     * @param data It is the matrix to which add the column.
     * @param col It is the column to add.
     * @return The input matrix plus the column in a single matrix.
     */
    public static double[][] addColumn(double[][] data, double[] col) {
        double[][] new_mat = new double[data.length][];
        for (int i = 0; i < (new_mat.length >= col.length ? col.length : new_mat.length); ++i) {
            new_mat[i] = new double[data[0].length + 1];
            new_mat[i][new_mat[i].length - 1] = col[i];
        }
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                new_mat[i][j] = data[i][j];
            }
        }
        return new_mat;
    }
    
    /**
     * Extracts a row from a matrix. If the row index is negative or greater than the available rows It returns null.
     * @param data The source matrix.
     * @param index_row The row to extract.
     * @return The column of the matrix.
     */
    public static double[] getRow(double[][] data, int index_row) {
        if (index_row < 0 || index_row >= data.length) {
            return null;
        }
        double[] temp = new double[data[0].length];
        System.arraycopy(data[index_row], 0, temp, 0, temp.length);
        return temp;
    }
    
    /**
     * Extracts rows from a matrix. If there is no rows to extract It returns null.
     * @param data The source matrix.
     * @param indexes The rows to extract.
     * @param invert_selection If true inverts the selection.
     * @return The rows of the matrix.
     */
    public static double[][] getRows(double[][] data, int[] indexes, boolean invert_selection) {
        List<Integer> rows_ = new ArrayList<Integer>();
        if (!invert_selection) {
            rows_ = Arrays.asList(ArrayUtils.toObject(indexes));
        } else {
            for (int i = 0; i < data.length; ++i) {
                boolean is_in = false;
                for (int j = 0; j < indexes.length; ++j) {
                    if (indexes[j] == i) {
                        is_in = true;
                        break;
                    }
                }
                if (!is_in)
                    rows_.add(i);
            }
        }
        if (rows_.isEmpty())
            return null;
        int[] index_rows = ArrayUtils.toPrimitive(rows_.toArray(new Integer[]{}));
        List<double[]> rows = new ArrayList<double[]>();
        for (int i = 0; i < index_rows.length; ++i) {
            rows.add(getRow(data, index_rows[i]));
            if (rows.get(rows.size() - 1) == null)
                rows.remove(rows.size() - 1);
        }
        if (rows.isEmpty())
            return null;
        return rows.toArray(new double[][]{});
    }
    
    /**
     * Adds a row to the end of a matrix. If the number of cols in the matrix don't match to the cols in the column, It is truncated the biggest.
     * @param data It is the matrix to which add the column.
     * @param row It is the row to add.
     * @return The input matrix plus the row in a single matrix.
     */
    public static double[][] addRow(double[][] data, double[] row) {
    	double[][] new_mat = new double[data.length + 1][];
        for (int i = 0; i < data.length; ++i) {
            new_mat[i] = deep_copy(data[i]);
        }
        new_mat[new_mat.length - 1] = deep_copy(row);
        return new_mat;
    }
    
    /**
     * Performs a deep copy of a matrix.
     * @param dataset The matrix to copy.
     * @return A copy of the matrix parameter.
     */
    public static double[][] deep_copy(double[][] dataset) {
        double[][] new_dataset = new double[dataset.length][dataset[0].length];
        for (int i = 0; i < new_dataset.length; ++i) {
            for (int j = 0; j < new_dataset[i].length; ++j) {
                new_dataset[i][j] = dataset[i][j];
            }
        }
        return new_dataset;
    }
    
    /**
     * Performs a deep copy of a matrix.
     * @param dataset The matrix to copy.
     * @return A copy of the matrix parameter.
     */
    public static double[] deep_copy(double[] dataset) {
        double[] new_dataset = new double[dataset.length];
        System.arraycopy(dataset, 0, new_dataset, 0, new_dataset.length);
        return new_dataset;
    }
    
    /**
     * Performs a deep copy of a matrix.
     * @param dataset The matrix to copy.
     * @return A copy of the matrix parameter.
     */
    public static int[][] deep_copy(int[][] dataset) {
        int[][] new_dataset = new int[dataset.length][dataset[0].length];
        for (int i = 0; i < new_dataset.length; ++i) {
        	new_dataset[i] = deep_copy(dataset[i]);
        }
        return new_dataset;
    }
    
    /**
     * Performs a deep copy of a matrix.
     * @param dataset The matrix to copy.
     * @return A copy of the matrix parameter.
     */
    public static int[] deep_copy(int[] dataset) {
    	int[] new_dataset = new int[dataset.length];
        System.arraycopy(dataset, 0, new_dataset, 0, new_dataset.length);
        return new_dataset;
    }
    
    /**
     * Performs the conversion from integer to double of a matrix.
     * @param data The matrix to convert.
     * @return The double version of the matrix parameter.
     */
    public static double[][] int_to_double(int[][] data) {
        double[][] converted = new double[data.length][data[0].length];
        for (int i = 0; i < converted.length; ++i) {
            for (int j = 0; j < converted[i].length; ++j) {
                converted[i][j] = data[i][j];
            }
        }
        return converted;
    }
    
    /**
     * Performs the conversion from integer to double of an array.
     * @param data The array to convert.
     * @return The double version of the array parameter.
     */
    public static double[] int_to_double(int[] data) {
        double[] converted = new double[data.length];
        for (int i = 0; i < converted.length; ++i) {
            converted[i] = data[i];
        }
        return converted;
    }
    
    /**
     * Performs the conversion from double to integer of a matrix.
     * @param data The matrix to convert.
     * @param nearest_integer Whether to ceil (false) or floor (true).
     * @return The integer version of the matrix parameter.
     */
    public static int[][] double_to_int(double[][] data, boolean nearest_integer) {
        int[][] converted = new int[data.length][data[0].length];
        for (int i = 0; i < converted.length; ++i) {
            for (int j = 0; j < converted[i].length; ++j) {
                converted[i][j] = (int) (data[i][j] + (nearest_integer == true ? 0.5 : 0.0));
            }
        }
        return converted;
    }
    
    /**
     * Performs the conversion from double to integer of an array.
     * @param data The array to convert.
     * @param nearest_integer Whether to ceil (false) or floor (true).
     * @return The integer version of the array parameter.
     */
    public static int[] double_to_int(double[] data, boolean nearest_integer) {
        int[] converted = new int[data.length];
        for (int i = 0; i < converted.length; ++i) {
            converted[i] = (int) (data[i] + (nearest_integer == true ? 0.5 : 0.0));
        }
        return converted;
    }
    
    /**
     * Gets the principle diagonal of a matrix. It works either for a rectangular matrix.
     * @param dataset The matrix from which taking the diagonal.
     * @return The principal diagonal of the matrix parameter in an array.
     */
    public static double[] getDiagonal(double[][] dataset) {
        double[] diagonal = new double[dataset.length <= dataset[0].length ? dataset.length : dataset[0].length];
        for (int i = 0; i < diagonal.length; ++i) {
            diagonal[i] = dataset[i][i];
        }
        return diagonal;
    }
    
    /**
     * Shuffles the rows of a matrix.
     * @param data The data matrix of which shuffling thw rows.
     * @return The shuffled version of the matrix parameter.
     */
    public static double[][] shuffle(double[][] data) {
        Random rand = new Random();
        double[][] shuffled = new double[data.length][];
        List<Integer> indexes = new ArrayList<Integer>();
        for(int i = 0; i < data.length; ++i) {
            indexes.add(i);
        }
        Collections.shuffle(indexes, rand);
        for (int i = 0; i < indexes.size(); ++i) {
            shuffled[indexes.get(i)] = deep_copy(data[i]);
        }
        return shuffled;
    }
    
    /**
     * Performs the product of a matrix by a vector.
     * @param a The matrix.
     * @param b The vector.
     * @return The result of the product if it can be performed, null otherwise.
     */
    public static double[] matrixByVector(double[][] a, double[] b) {
        if (a[0].length != b.length)
            return null;
        double[] result = new double[a.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = 0.0;
            for (int j = 0; j < a[i].length; ++j) {
                result[i] += a[i][j] * b[j];
            }
        }
        return result;
    }
    
    /**
     * Performs the product of a vector by a matrix.
     * @param a The vector.
     * @param b The matrix.
     * @return The result of the product if it can be performed, null otherwise.
     */
    public static double[] vectorByMatrix(double[] a, double[][] b) {
        if (a.length != b[0].length)
            return null;
        double[] result = new double[b.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = 0.0;
            for (int j = 0; j < b[i].length; ++j) {
                result[i] += a[j] * b[i][j];
            }
        }
        return result;
    }
    
    /**
     * Arranges a matrix NxM in an array of lenght N*M.
     * @param data The matrix.
     * @param direction Whether to get the elements of the matrix by row (false) or by column (true).
     * @return An array containing the trace of the input matrix.
     */
    public static double[] toTrace(double[][] data, boolean direction) {
        double[] trace = new double[data.length * data[0].length];
        if (!direction) {
            for (int i = 0; i < data.length; ++i) {
                for (int j = 0; j < data[i].length; ++j) {
                    trace[i * data[i].length + j] = data[i][j];
                }
            }
        } else {
            for (int i = 0; i < data.length; ++i) {
                for (int j = 0; j < data[i].length; ++j) {
                    trace[j * data.length + i] = data[i][j];
                }
            }
        }
        return trace;
    }
    
    /**
     * Arranges a matrix NxM in an array of lenght N*M.
     * @param data The matrix.
     * @param direction Whether to get the elements of the matrix by row (false) or by column (true).
     * @return An array containing the trace of the input matrix.
     */
    public static int[] toTrace(int[][] data, boolean direction) {
        int[] trace = new int[data.length * data[0].length];
        if (!direction) {
            for (int i = 0; i < data.length; ++i) {
                for (int j = 0; j < data[i].length; ++j) {
                    trace[i * data[i].length + j] = data[i][j];
                }
            }
        } else {
            for (int i = 0; i < data.length; ++i) {
                for (int j = 0; j < data[i].length; ++j) {
                    trace[j * data.length + i] = data[i][j];
                }
            }
        }
        return trace;
    }
    
    /**
     * Returns the maximum value in a matrix.
     * @param data The matrix.
     * @return The maximum value in the matrix.
     */
    public static double max(double[][] data) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                if (data[i][j] > max)
                    max = data[i][j];
            }
        }
        return max;
    }
    
    /**
     * Returns the minimum value of a matrix.
     * @param data The matrix.
     * @return The minimum value in the matrix.
     */
    public static double min(double[][] data) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                if (data[i][j] < min)
                    min = data[i][j];
            }
        }
        return min;
    }
    
    /**
     * Performs the 2D convolution.
     * @param data The data to convolve.
     * @param kernel The kernel of the convolution.
     * @param padding Whether to pad the input matrix.
     * @param padding_value The value to use for pad the matrix.
     * @return The result of the convolution.
     */
    public static double[][] convolve2D(double[][] data, double[][] kernel, boolean padding, double padding_value) {
        double[][] data_padded = new double[0][0];
        if (kernel.length % 2 == 0 || kernel[0].length % 2 == 0)
            return data_padded;
        int row_padding = (int) ((double) kernel.length / 2.0);
        int col_padding = (int) ((double) kernel[0].length / 2.0);
        if (padding) {
            data_padded = new double[data.length + 2 * row_padding][data[0].length + 2 * col_padding];
            for (int i = 0; i < data_padded.length; ++i) {
                for (int j = 0; j < data_padded[i].length; ++j) {
                    if (i >= row_padding && i < data_padded.length - row_padding && j >= col_padding && j < data_padded[i].length - col_padding)
                        data_padded[i][j] = data[i - row_padding][j - col_padding];
                    else
                        data_padded[i][j] = padding_value;
                }
            }
        } else {
            data_padded = deep_copy(data);
        }

        double[][] result = new double[data_padded.length - 2 * row_padding][];
        for(int i = row_padding; i < data_padded.length - row_padding; ++i) {
            result[i - row_padding] = new double[data_padded[i].length - 2 * col_padding];
            for(int j = col_padding; j < data_padded[i].length - col_padding; ++j) {
                double res = 0.0;
                for(int k = 0; k < kernel.length; ++k) {
                    for(int l = 0; l < kernel[k].length; ++l) {
                        res += data_padded[i - row_padding + k][j - col_padding + l] * kernel[kernel.length - 1 - k][kernel[k].length - 1 - l];
                    }
                }
                result[i - row_padding][j - col_padding] = res;
            }
        }
        return result;
    }
    
    /**
     * Performs a 2D filtering.
     * @param data The data to filter.
     * @param kernel_rows The rows of the kernel.
     * @param kernel_cols The columns of the kernel.
     * @param filter The function to apply in the neighbor.
     * @param padding Whether to pad the input matrix.
     * @param padding_value The value to use for pad the matrix.
     * @return The result of the convolution.
     */
    public static double[][] filter2D(double[][] data, int kernel_rows, int kernel_cols, Function<double[][], Double> filter, boolean padding, double padding_value) {
        double[][] data_padded = new double[0][0];
        if (kernel_rows % 2 == 0 || kernel_cols % 2 == 0)
            return data_padded;
        int row_padding = (int) ((double) kernel_rows / 2.0);
        int col_padding = (int) ((double) kernel_cols / 2.0);
        if (padding) {
            data_padded = new double[data.length + 2 * row_padding][data[0].length + 2 * col_padding];
            for (int i = 0; i < data_padded.length; ++i) {
                for (int j = 0; j < data_padded[i].length; ++j) {
                    if (i >= row_padding && i < data_padded.length - row_padding && j >= col_padding && j < data_padded[i].length - col_padding)
                        data_padded[i][j] = data[i - row_padding][j - col_padding];
                    else
                        data_padded[i][j] = padding_value;
                }
            }
        } else {
            data_padded = deep_copy(data);
        }
        double[][] result = new double[data_padded.length - 2 * row_padding][];
        for(int i = row_padding; i < data_padded.length - row_padding; ++i) {
            result[i - row_padding] = new double[data_padded[i].length - 2 * col_padding];
            for(int j = col_padding; j < data_padded[i].length - col_padding; ++j) {
                double[][] square = new double[kernel_rows][kernel_cols];
                for(int k = 0; k < square.length; ++k) {
                    for(int l = 0; l < square[k].length; ++l) {
                    	square[k][l] = data_padded[i - row_padding + k][j - col_padding + l];
                    }
                }
                result[i - row_padding][j - col_padding] = filter.apply(square);
            }
        }
        return result;
    }
    
    /**
     * Performs the 1D convolution.
     * @param data The data to convolve.
     * @param kernel The kernel of the convolution.
     * @param padding Whether to pad the input matrix.
     * @param padding_value The value to use for pad the matrix.
     * @return The result of the convolution.
     */
    public static double[] convolve1D(double[] data, double[] kernel, boolean padding, double padding_value) {
    	double[] data_padded = new double[0];
        if (kernel.length % 2 == 0)
            return data_padded;
        int row_padding = (int) ((double) kernel.length / 2.0);
        if (padding) {
            data_padded = new double[data.length + 2 * row_padding];
            for (int i = 0; i < data_padded.length; ++i) {
                if (i >= row_padding && i < data_padded.length - row_padding)
                    data_padded[i] = data[i - row_padding];
                else
                    data_padded[i] = padding_value;
            }
        } else {
            data_padded = deep_copy(data);
        }
        double[] result = new double[data_padded.length - 2 * row_padding];
        for(int i = row_padding; i < data_padded.length - row_padding; ++i) {
            double res = 0.0;
            for(int k = 0; k < kernel.length; ++k) {
                res += data_padded[i - row_padding + k] * kernel[kernel.length - 1 - k];
            }
            result[i - row_padding] = res;
        }
    	return result;
    }

    /**
     * Performs a 1D filtering.
     * @param data The data to filter.
     * @param kernel_size The rows of the kernel.
     * @param filter The function to apply in the neighbor.
     * @param padding Whether to pad the input matrix.
     * @param padding_value The value to use for pad the matrix.
     * @return The result of the convolution.
     */
    public static double[] filter1D(double[] data, int kernel_size, Function<double[], Double> filter, boolean padding, double padding_value) {
        double[] data_padded = new double[0];
        if (kernel_size % 2 == 0)
            return data_padded;
        int row_padding = (int) ((double) kernel_size / 2.0);
        if (padding) {
            data_padded = new double[data.length + 2 * row_padding];
            for (int i = 0; i < data_padded.length; ++i) {
                if (i >= row_padding && i < data_padded.length - row_padding)
                    data_padded[i] = data[i - row_padding];
                else
                    data_padded[i] = padding_value;
            }
        } else {
            data_padded = deep_copy(data);
        }
        double[] result = new double[data_padded.length - 2 * row_padding];
        for(int i = row_padding; i < data_padded.length - row_padding; ++i) {
            double[] square = new double[kernel_size];
            for(int k = 0; k < square.length; ++k) {
            	square[k] = data_padded[i - row_padding + k];
            }
            result[i - row_padding] = filter.apply(square);
        }
        return result;
    }
    
    /**
     * Performs the inner product between two arrays.
     * @param a The first array.
     * @param b The second array.
     * @return The result of the inner product.
     */
    public static double innerProduct(double[] a, double[] b) {
        double res = 0.0;
        for (int i = 0; i < a.length; ++i) {
            res += a[i] * b[i];
        }
        return res;
    }
    
    /**
     * Generates a matrix of random values in a range.
     * @param rows The number of the rows.
     * @param cols The number of the columns.
     * @param min The minimum of the range.
     * @param max The maximum of the range.
     * @return A random matrix which has the values in the range [min, max).
     */
    public static double[][] generateRandomMatrix(int rows, int cols, double min, double max) {
        Random rand = new Random();
        double[][] mat = new double[rows][cols];
        if (min >= max) {
            double t = min;
            min = max;
            max = t;
        }
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                mat[i][j] = rand.nextDouble() * (max - min) + min;
            }
        }
        return mat;
    }
    
    /**
     * Performs the product of a matrix by a scalar.
     * @param data The matrix.
     * @param scalar The scalar.
     * @return The matrix which is the result of the product.
     */
    public static double[][] scalarProduct(double[][] data, double scalar) {
        double[][] mat = deep_copy(data);
        for (int i = 0; i < mat.length; ++i) {
            for (int j = 0; j < mat[i].length; ++j) {
                mat[i][j] *= scalar;
            }
        }
        return mat;
    }
    
    /**
     * Generates a matrix whose values come from a Gaussian distribution.
     * @param width The width of the matrix (the number of the rows). It is an even number.
     * @param height The height of the matrix (the number of the columns). It is an even number.
     * @param sigmax The x-axis standard deviation.
     * @param sigmay The y-axis standard deviation.
     * @return The matrix whose values come from a Gaussian distribution.
     */
    public static double[][] generate2DGaussianMatrix(int width, int height, double sigmax, double sigmay) {
    	width = MeasurementUtils.checkWindowDimension(width);
    	height = MeasurementUtils.checkWindowDimension(height);
        if (sigmax < 0.0)
            sigmax = - sigmax;
        if (sigmay < 0.0)
            sigmay = - sigmay;
        double[] mu = new double[]{(int) ((double) width / 2.0), (int) ((double) height / 2.0)};
        double[][] cov_inv = new LUDecomposition(new Array2DRowRealMatrix(new double[][]{{sigmax, 0.0}, {0.0, sigmay}})).getSolver().getInverse().getData();
        double[][] kernel = new double[width][height];
        for (int i = 0; i < kernel.length; ++i) {
            for (int j = 0; j < kernel[i].length; ++j) {
                double[] difference = new double[]{i - mu[0], j - mu[1]};
                kernel[i][j] = 1.0 / (2.0 * Math.PI * Math.sqrt(sigmax * sigmay) * Math.exp(innerProduct(vectorByMatrix(difference, cov_inv), difference)));
            }
        }
        return kernel;
    }
    
    /**
     * Generates the complete transformation matrix from a coordinates system to another coordinates system.
     * @param x The x-axis translation.
     * @param y The y-axis translation.
     * @param x_scale_factor The x-axis scaling factor.
     * @param y_scale_factor The y-axis scaling factor.
     * @param x_shear_factor The x-axis shearing factor.
     * @param y_shear_factor The y-axis shearing factor.
     * @param clockwise_angle The rotation in the x-y plane.
     * @return The transformation matrix.
     */
    public static double[][] generate2DTransformationMatrix(double x, double y, double x_scale_factor, double y_scale_factor, double x_shear_factor, double y_shear_factor, double clockwise_angle) {
        double[][] matrix = new double[3][3];
        matrix[0][0] = x_scale_factor * Math.cos(clockwise_angle) - x_scale_factor * y_shear_factor * Math.sin(clockwise_angle);
        matrix[0][1] = x_scale_factor * Math.sin(clockwise_angle) + x_scale_factor * y_shear_factor * Math.cos(clockwise_angle);
        matrix[0][2] = 0.0;
        matrix[1][0] = y_scale_factor * x_shear_factor * Math.cos(clockwise_angle) - y_scale_factor * Math.sin(clockwise_angle);
        matrix[1][1] = y_scale_factor * x_shear_factor * Math.sin(clockwise_angle) + y_scale_factor * Math.cos(clockwise_angle);
        matrix[1][2] = 0.0;
        matrix[2][0] = (x * x_scale_factor + y * y_scale_factor * x_shear_factor) * Math.cos(clockwise_angle) -
                (x * x_scale_factor * y_shear_factor + y * y_scale_factor) * Math.sin(clockwise_angle);
        matrix[2][1] = (x * x_scale_factor + y * y_scale_factor * y_shear_factor) * Math.sin(clockwise_angle) +
                (x * x_scale_factor * y_shear_factor + y * y_scale_factor) * Math.cos(clockwise_angle);
        matrix[2][2] = 1.0;
        return matrix;
    }
    
    /**
     * Parses an Object and converts it into double array.
     * @param o The Object to parse.
     * @return A double array if the input Object can be converted, null otherwise.
     */
    public static double[] objectToDoubleArray(Object o) {
    	double[] converted = null;
		if (o instanceof Double || o instanceof Integer)
			converted = new double[]{(double) o};
		else if (o instanceof Double[]) {
			converted = new double[((Double[]) o).length];
			for (int i = 0; i < converted.length; ++i)
				converted[i] = (double) ((Double[]) o)[i];
		} else if (o instanceof Integer[]) {
			converted = new double[((Integer[]) o).length];
			for (int i = 0; i < converted.length; ++i)
				converted[i] = (double) ((Integer[]) o)[i];
		} else if (o instanceof double[]) {
			converted = new double[((double[]) o).length];
			for (int i = 0; i < converted.length; ++i)
				converted[i] = ((double[]) o)[i];
		} else if (o instanceof int[]) {
			converted = new double[((int[]) o).length];
			for (int i = 0; i < converted.length; ++i)
				converted[i] = ((int[]) o)[i];
		}
		return converted;
    }
    
	public static List<List<Integer>> cartesianProduct(List<List<Integer>> set1, List<List<Integer>> set2) {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		if (set1 == null || set1.isEmpty()) {
			for (int i = 0; i < set2.size(); ++i) {
				List<Integer> tuple = new ArrayList<Integer>();
				tuple.addAll(set2.get(i));
				elements.add(tuple);
			}
		} else if (set2 == null || set2.isEmpty()) {
			for (int i = 0; i < set1.size(); ++i) {
				List<Integer> tuple = new ArrayList<Integer>();
				tuple.addAll(set1.get(i));
				elements.add(tuple);
			}
		} else {
			for (int i = 0; i < set1.size(); ++i) {
				for (int j = 0; j < set2.size(); ++j) {
					List<Integer> tuple = new ArrayList<Integer>();
					tuple.addAll(set1.get(i));
					tuple.addAll(set2.get(j));
					elements.add(tuple);
				}
			}
		}
		return elements;
	}
}
