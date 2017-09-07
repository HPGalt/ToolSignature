/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package it.corvallis.imageprocessing.measurements;

import ij.ImagePlus;
import it.corvallis.commons.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 *
 * @author ChiarellaG
 */
public class Projections {
	
	public static int[] histogram(int[][] mat) {
		int min = (int) CollectionUtils.min(CollectionUtils.int_to_double(mat));
		int max = (int) CollectionUtils.max(CollectionUtils.int_to_double(mat));
		int[] hist = new int[max - min + 1];
		for (int i = 0; i < mat.length; ++i) {
			for (int j = 0; j < mat[i].length; ++j) {
				++hist[mat[i][j] - min];
			}
		}
		return hist;
	}
	
    /**
     * Computes the maximum of the horizontal histogram.
     * @param img The input binary image.
     * @return The maximum of the horizontal histogram.
     */
    public static int maximumHorizontalProjection(ImagePlus img) {
        int[] hp = horizontalProjection(img);
        int max = 0;
        for (int i = 0; i < hp.length; ++i) {
            if (hp[i] > max)
                max = hp[i];
        }
        return max;
    }
    
    /**
     * Computes the maximum of the vertical histogram.
     * @param img The input binary image.
     * @return The maximum of the vertical histogram.
     */
    public static int maximumVerticalProjection(ImagePlus img) {
        int[] vp = verticalProjection(img);
        int max = 0;
        for (int i = 0; i < vp.length; ++i) {
            if (vp[i] > max)
                max = vp[i];
        }
        return max;
    }
    
    /**
     * Computes the horizontal histogram.
     * @param img The input binary image.
     * @return The horizontal histogram.
     */
    public static int[] horizontalProjection(ImagePlus img) {
        int[] hp = new int[img.getHeight()];
        Arrays.fill(hp, 0);
        int[][] pixels = img.getProcessor().getIntArray();
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                if(pixels[j][i] == 0) {
                    ++hp[i];
                }
            }
        }
        return hp;
    }
    
    /**
     * Computes the vertical histogram.
     * @param img The input binary image.
     * @return The vertical histogram.
     */
    public static int[] verticalProjection(ImagePlus img) {
        int[] vp = new int[img.getWidth()];
        Arrays.fill(vp, 0);
        int[][] pixels = img.getProcessor().getIntArray();
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                if(pixels[j][i] == 0) {
                    ++vp[j];
                }
            }
        }
        return vp;
    }
    
    /**
     * Computes the number of peaks in the horizontal histogram.
     * @param img The input binary image.
     * @return The number of peaks in the horizontal histogram.
     */
    public static int horizontalProjectionPeaks(ImagePlus img) {
        int num = 0;
        int[] hp = horizontalProjection(img);
        for (int i = 1; i < hp.length - 1; ++i) {
            if (hp[i] > hp[i - 1] && hp[i] > hp[i + 1])
                ++num;
        }
        return num;
    }
    
    /**
     * Computes the number of peaks in the vertical histogram.
     * @param img The input binary image.
     * @return The number of peaks in the vertical histogram.
     */
    public static int verticalProjectionPeaks(ImagePlus img) {
        int num = 0;
        int[] vp = verticalProjection(img);
        for (int i = 1; i < vp.length - 1; ++i) {
            if (vp[i] > vp[i - 1] && vp[i] > vp[i + 1])
                ++num;
        }
        return num;
    }
    
    public static HashMap<Integer, Double> findPeaks(double[] s, boolean valley, int window) {
    	HashMap<Integer, Double> result = new HashMap<Integer, Double>();
    	List<Double> peaks = new ArrayList<Double>();
    	List<Integer> x_indexes = new ArrayList<Integer>();
    	int index = 0;
    	boolean monotone_increasing = s[index] < s[index + 1];
    	boolean monotone_decreasing = s[index] > s[index + 1];
    	boolean constant = s[index] == s[index + 1];
    	if (s.length < 3) {
    		if (monotone_increasing) {
        		if (valley)
        			result.put(0, s[0]);
        		else
        			result.put(s.length - 1, s[s.length - 1]);
        		return result;
        	} else if (monotone_decreasing) {
        		if (valley)
        			result.put(s.length - 1, s[s.length - 1]);
        		else
        			result.put(0, s[0]);
        		return result;
        	} else if (constant) {
        		result.put(0, s[0]);
        		return result;
        	}
    	}
    	++index;
    	while (((monotone_increasing && !monotone_decreasing && !constant) ||
    			(!monotone_increasing && monotone_decreasing && !constant) ||
    			(!monotone_increasing && !monotone_decreasing && constant)) && index < s.length - 1) {
    		if (s[index] > s[index + 1]) {
    			monotone_decreasing = true;
    			constant = false;
    		} else if (s[index] < s[index + 1]) {
    			monotone_increasing = true;
    			constant = false;
    		}
			++index;
    	}
    	if (monotone_increasing && !monotone_decreasing && !constant) {
    		if (valley)
    			result.put(0, s[0]);
    		else
    			result.put(s.length - 1, s[s.length - 1]);
    		return result;
    	} else if (!monotone_increasing && monotone_decreasing && !constant) {
    		if (valley)
    			result.put(s.length - 1, s[s.length - 1]);
    		else
    			result.put(0, s[0]);
    		return result;
    	} else if (!monotone_increasing && !monotone_decreasing && constant) {
    		result.put(0, s[0]);
    		return result;
    	}
    	double[] x = new double[s.length + 1];
    	x[0] = 0.0;
    	if (valley) {
    		for (int i = 1; i < x.length; ++i)
				x[i] = - s[i - 1];
    	} else {
    		for (int i = 1; i < x.length; ++i)
				x[i] = s[i - 1];
    	}
    	double[] dx = new double[x.length - 1];
    	dx[0] = 0.0;
    	for (int i = 1; i < x.length; ++i)
			dx[i - 1] = x[i] - x[i - 1];
    	List<Integer> rises = new ArrayList<Integer>();
    	List<Integer> falls = new ArrayList<Integer>();
    	rises.add(0);
    	falls.add(0);
    	for (int i = 1; i < dx.length; ++i) {
			if (dx[i] > 0.0)
				rises.add(i);
			else if (dx[i] < 0.0)
				falls.add(i);
		}
    	if (!rises.isEmpty() && !falls.isEmpty()) {
    		int[] dr = new int[rises.size()];
    		dr[0] = 0;
    		for (int i = 1; i < dr.length; ++i) {
				dr[i] = rises.get(i) - rises.get(i - 1);
			}
    		int[] rc = new int[x.length];
    		Arrays.fill(rc, 1);
    		for (int i = 1; i < rises.size(); ++i) {
				rc[rises.get(i) + 1] = 1 - dr[i];
    		}
    		rc[0] = 0;
    		rc[1] = 0;
    		int[] rs = new int[rc.length];
    		rs[0] = rc[0];
    		for (int i = 1; i < rs.length; ++i) {
				rs[i] = rs[i - 1] + rc[i];
			}
    		
    		int[] df = new int[falls.size()];
    		df[0] = 0;
    		for (int i = 1; i < df.length; ++i) {
				df[i] = falls.get(i) - falls.get(i - 1);
			}
    		int[] fc = new int[x.length];
    		Arrays.fill(fc, 1);
    		for (int i = 1; i < falls.size(); ++i) {
				fc[falls.get(i) + 1] = 1 - df[i];
    		}
    		fc[0] = 0;
    		fc[1] = 0;
    		int[] fs = new int[fc.length];
    		fs[0] = fc[0];
    		for (int i = 1; i < fs.length; ++i) {
				fs[i] = fs[i - 1] + fc[i];
			}
    		
    		int[] rp = new int[x.length];
    		Arrays.fill(rp, -1);
    		rp[0] = 0;
    		rp[1] = dr[1] - 1;
    		rp[rises.get(rises.size() - 1) + 1] = x.length - 1 - rises.get(rises.size() - 1);
    		for (int i = 1; i < rises.size() - 1; ++i) {
				rp[rises.get(i) + 1] = dr[i + 1] - 1;
			}
    		int[] rq = new int[rp.length];
    		rq[0] = rp[0];
    		for (int i = 1; i < rq.length; ++i) {
				rq[i] = rq[i - 1] + rp[i];
			}
    		
    		int[] fp = new int[x.length + 1];
    		Arrays.fill(fp, -1);
    		fp[0] = 0;
    		fp[1] = df[1] - 1;
    		fp[falls.get(falls.size() - 1) + 1] = x.length - 1 - falls.get(falls.size() - 1);
    		for (int i = 1; i < falls.size() - 1; ++i) {
				fp[falls.get(i) + 1] = df[i + 1] - 1;
			}
    		int[] fq = new int[rp.length];
    		fq[0] = fp[0];
    		for (int i = 1; i < fq.length; ++i) {
				fq[i] = fq[i - 1] + fp[i];
			}
    		
    		for (int i = 0; i < x.length; ++i) {
				if (rs[i] < fs[i] && fq[i] < rq[i] && Math.floor((fq[i] - rs[i]) / 2.0) == 0.0) {
					peaks.add(x[i]);
					x_indexes.add(i);
				}
			}
    		
    		if (window > 1) {
    			List<Integer> is = new ArrayList<Integer>();
    			for (int i = 1; i < x_indexes.size(); ++i) {
					if (x_indexes.get(i) - x_indexes.get(i - 1) <= window)
						is.add(i - 1);
				}
    			while (!is.isEmpty()) {
    				for (int i = 0; i < is.size(); ++i) {
						if (peaks.get(i) >= peaks.get(i + 1))
							is.set(i, is.get(i) + 1);
					}
    				NavigableSet<Integer> removing = new TreeSet<Integer>(is).descendingSet();

    				for (Integer integer : removing) {
						peaks.remove((int) integer);
						x_indexes.remove((int) integer);
					}
    				is.clear();
    				for (int i = 1; i < x_indexes.size(); ++i) {
    					if (x_indexes.get(i) - x_indexes.get(i - 1) <= window)
    						is.add(i - 1);
    				}
    			}
    		}
    		if (valley) {
    			for (int i = 0; i < peaks.size(); ++i) {
					peaks.set(i, - peaks.get(i));
				}
    		}
    	}
    	if (!x_indexes.isEmpty()) {
			for (int i = 0; i < x_indexes.size(); ++i) {
				result.put(x_indexes.get(i), peaks.get(i));
			}
    	}
    	return result;
    }
    
    /**
     * Generates the histogram of the input data.
     * @param data The data onto which computing the histogram.
     * @param lower_bound The lower bound of the interval into which computing the histogram.
     * @param upper_bound The upper bound of the interval into which computing the histogram.
     * @param bins The number of bins the interval is divided in.
     * @return The histogram of the input data array bins + 1 long.
     */
    public static int[] generateHistogram(double[] data, double lower_bound, double upper_bound, int bins) {
    	int[] hist = new int[bins + 1];
    	Arrays.fill(hist, 0);
    	if (data == null || data.length == 0)
    		return hist;
    	double delta = (upper_bound - lower_bound) / (double) bins;
    	for (int i = 0; i < data.length; ++i) {
    		if (data[i] < lower_bound || data[i] > upper_bound)
    			continue;
			if (data[i] == upper_bound)
				++hist[hist.length - 1];
			else {
				++hist[(int) ((data[i] - lower_bound) / (delta))];
			}
		}
    	return hist;
    }
}
