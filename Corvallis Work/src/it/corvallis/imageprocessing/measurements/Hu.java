/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package it.corvallis.imageprocessing.measurements;

import ij.ImagePlus;
import java.awt.Rectangle;
import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author ChiarellaG
 */
public class Hu {
    
    /**
     * Computes the moment of a binary image.
     * Examples:
     * - Area = moment(img, 0, 0);
     * - Centroid X = moment(img, 1, 0) / moment(img, 0, 0)
     * - Centroid Y = moment(img, 0, 1) / moment(img, 0, 0)
     * @param img The input binary image.
     * @param p The pth order of the moment.
     * @param q The qth order of the moment.
     * @param top_left_x The top left x-axis of the rectangle to consider.
     * @param top_left_y The top left y-axis of the rectangle to consider.
     * @param width The width of the rectangle to consider.
     * @param height The height of the rectangle to consider.
     * @return The p-q order moment.
     */
    public static double moment(ImagePlus img, int p, int q, int top_left_x, int top_left_y, int width, int height) {
        double moment = 0.0;
        int[][] pixels = img.getProcessor().getIntArray();
        for (int i = top_left_y; i < (img.getHeight() >  top_left_y + height ?  top_left_y + height : img.getHeight()); ++i) {
            double temp = 0.0;
            for (int j = top_left_x; j < (img.getWidth() > top_left_x + width ? top_left_x + width : img.getWidth()); ++j) {
                temp += Math.pow(j, p) * (pixels[j][i] == 0 ? 1.0 : 0.0);
            }
            moment += Math.pow((double) i, q) * temp;
        }
        return moment;
    }
    
    /**
     * Computes the central moment.
     * @param img The input binary image.
     * @param p The pth order of the moment.
     * @param q The qth order of the moment.
     * @param top_left_x The top left x-axis of the rectangle to consider.
     * @param top_left_y The top left y-axis of the rectangle to consider.
     * @param width The width of the rectangle to consider.
     * @param height The height of the rectangle to consider.
     * @return The p-q order moment.
     */
    public static double centralMoment(ImagePlus img, int p, int q, int top_left_x, int top_left_y, int width, int height) {
        double m = 0.0;
        double[] com = new double[2];
        com[0] = moment(img, 1, 0, top_left_x, top_left_y, width, height) / moment(img, 0, 0, top_left_x, top_left_y, width, height);
        com[1] = moment(img, 0, 1, top_left_x, top_left_y, width, height) / moment(img, 0, 0, top_left_x, top_left_y, width, height);
        int[][] pixels = img.getProcessor().getIntArray();
        for (int i = top_left_y; i < (img.getHeight() > top_left_y + height ? top_left_y + height : img.getHeight()); ++i) {
            double temp = 0.0;
            for (int j = top_left_x; j < (img.getWidth() > top_left_x + width ? top_left_x + width : img.getWidth()); ++j) {
                temp += Math.pow((double) j - com[0], p) * (pixels[j][i] == 0 ? 1.0 : 0.0);
            }
            m += Math.pow((double) i - com[1], q) * temp;
        }
        return m;
    }
    
    /**
     * Computes the seven invariant moments based on the seven Hu moments.
     * @param img The input binary image.
     * @param top_left_x The top left x-axis of the rectangle to consider.
     * @param top_left_y The top left y-axis of the rectangle to consider.
     * @param width The width of the rectangle to consider.
     * @param height The height of the rectangle to consider.
     * @return The seven invariant array of the moments.
     */
    public static double[] getInvariantMoment(ImagePlus img, int top_left_x, int top_left_y, int width, int height) {
        double[] moments = new double[7];
        double eta20 = huMoment(img, 2, 0, top_left_x, top_left_y, width, height);
        double eta02 = huMoment(img, 0, 2, top_left_x, top_left_y, width, height);
        double eta11 = huMoment(img, 1, 1, top_left_x, top_left_y, width, height);
        double eta30 = huMoment(img, 3, 0, top_left_x, top_left_y, width, height);
        double eta12 = huMoment(img, 1, 2, top_left_x, top_left_y, width, height);
        double eta21 = huMoment(img, 2, 1, top_left_x, top_left_y, width, height);
        double eta03 = huMoment(img, 0, 3, top_left_x, top_left_y, width, height);
        moments[0] = eta20 + eta02;
        moments[1] = Math.pow(eta20 - eta02, 2.0) + 4.0 * eta11 * eta11;
        moments[2] = Math.pow(eta30 - 3.0 * eta12, 2.0) + Math.pow(3.0 * eta21 - eta03, 2.0);
        moments[3] = Math.pow(eta30 + eta12, 2.0) + Math.pow(eta21 + eta03, 2.0);
        moments[4] = (eta30 - 3.0 * eta12) * (eta30 + eta12) * (Math.pow(eta30 + eta12, 2.0) - 3.0 * Math.pow(eta21 + eta03, 2.0)) + (3.0 * eta21 - eta03) * (eta21 + eta03) * (3.0 * Math.pow(eta30 + eta12, 2.0) - Math.pow(eta21 + eta03, 2.0));
        moments[5] = (eta20 - eta02) * (Math.pow(eta30 + eta12, 2.0) - Math.pow(eta21 + eta03, 2.0)) + 4.0 * eta11 * (eta30 + eta12) * (eta21 + eta03);
        moments[6] = (3.0 * eta21 - eta03) * (eta30 + eta12) * (Math.pow(eta30 + eta12, 2.0) - 3.0 * Math.pow(eta21 + eta03, 2.0)) - (eta30 - 3.0 * eta12) * (eta21 + eta03) * (3.0 * Math.pow(eta30 + eta12, 2.0) - Math.pow(eta21 + eta03, 2.0));
        return moments;
    }
    
    /**
     * Computes the Hu moment of p-q order.
     * @param img The input binary image.
     * @param p The pth order of the moment.
     * @param q The qth order of the moment.
     * @param top_left_x The top left x-axis of the rectangle to consider.
     * @param top_left_y The top left y-axis of the rectangle to consider.
     * @param width The width of the rectangle to consider.
     * @param height The height of the rectangle to consider.
     * @return The p-q order Hu moment.
     */
    public static double huMoment(ImagePlus img, int p, int q, int top_left_x, int top_left_y, int width, int height) {
        return centralMoment(img, p, q, top_left_x, top_left_y, width, height) / Math.pow(centralMoment(img, 0, 0, top_left_x, top_left_y, width, height), (double) (p + q) / 2.0 + 1.0);
        //return centralMoment(img, p, q) / Math.pow(moment(img, 0, 0), (double) (p + q) / 2.0 + 1.0);
    }
    
    /**
     * Computes the complex moment of a binary image.
     * @param img The input binary image.
     * @param p The pth order of the moment.
     * @param q The qth order of the moment.
     * @param top_left_x The top left x-axis of the rectangle to consider.
     * @param top_left_y The top left y-axis of the rectangle to consider.
     * @param width The width of the rectangle to consider.
     * @param height The height of the rectangle to consider.
     * @return The p-q order moment.
     */
    public static Complex complexMoment(ImagePlus img, int p, int q, int top_left_x, int top_left_y, int width, int height) {
        int[][] pixels = img.getProcessor().getIntArray();
        Complex moment = new Complex(0.0, 0.0);
        for (int i = top_left_y; i < (img.getHeight() > height ? height : img.getHeight()); ++i) {
            for (int j = top_left_x; j < (img.getWidth() > width ? width : img.getWidth()); ++j) {
                Complex temp1 = new Complex(j, i).pow(p);
                Complex temp2 = new Complex(j, i).conjugate().pow(q);
                moment = moment.add(temp1.multiply(temp2).multiply(pixels[j][i] == 0 ? 1.0 : 0.0));
            }
        }
        return moment;
    }
    
    /**
     * Computes the area of the image corresponding to the number of the black pixels.
     * @param img The input binary image.
     * @param top_left_x The top left x-axis of the rectangle to consider.
     * @param top_left_y The top left y-axis of the rectangle to consider.
     * @param width The width of the rectangle to consider.
     * @param height The height of the rectangle to consider.
     * @return The area of the input image by means of the moments.
     */
    public static double area(ImagePlus img, int top_left_x, int top_left_y, int width, int height) {
        /*int signatureArea = 0;
        for (int i = 0; i < img.getHeight(); ++i) {
        for (int j = 0; j < img.getWidth(); ++j) {
        if(img.getProcessor().getPixel(j, i) == 0) {
        ++signatureArea;
        }
        }
        }*/
        return Hu.moment(img, 0, 0, top_left_x, top_left_y, width, height);
    }
    
    /**
     * Computes the centre of gravity by means of the moments.
     * @param imagePlus The input image.
     * @param top_left_x The top left x-axis of the rectangle to consider.
     * @param top_left_y The top left y-axis of the rectangle to consider.
     * @param width The width of the rectangle to consider.
     * @param height The height of the rectangle to consider.
     * @return the x-y axis coordiantes of the centre of gravity.
     */
    public static double[] getCentreOfGravity(ImagePlus imagePlus, int top_left_x, int top_left_y, int width, int height) {
        ImagePlus roi = imagePlus.duplicate();
        roi.setRoi(new Rectangle(top_left_x, top_left_y, width, height));
        roi = new ImagePlus("", roi.getProcessor().crop());
        double[] com = new double[2];
        com[0] = moment(roi, 1, 0, top_left_x, top_left_y, width, height) / moment(roi, 0, 0, top_left_x, top_left_y, width, height);
        com[1] = moment(roi, 0, 1, top_left_x, top_left_y, width, height) / moment(roi, 0, 0, top_left_x, top_left_y, width, height);
        return com;
    }
}
