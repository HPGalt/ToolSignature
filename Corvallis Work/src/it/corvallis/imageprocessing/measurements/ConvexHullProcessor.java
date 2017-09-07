package it.corvallis.imageprocessing.measurements;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.measure.Measurements;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

/*
Convex Hull Plus by Gabriel Landini, 12/Sep/2004.  G.Landini at bham. ac. uk

Minimum bounding circle as suggested by:
From:         Xavier Draye <draye@ECOP.UCL.AC.BE>
Sender:       ImageJ Interest Group <IMAGEJ@LIST.NIH.GOV>
Subject:      Re: Bounding circle

If you are still interested in getting the smallest circle enclosing all
your points, I think the following would do the job:

REASONING: the number of points that will be exactly on the circle border
will be at least 2.
-If there are 2 points on the circle, the circle center will be the
midpoint of the segment joining the two points.
-If there are 3 points on the circle, we can find for each side of the
triangle the unique line that contains the midpoint the side segment and is
orthogonal to the segment. There will be 3 such lines, and the center of
the circle is the intersection of any two of them.
-If there are more than three points, then the situation reduces to the 3
points (taking any three of them, although there will be choices of three
that may lead to greater numerical errors). Actually, I am not sure the >3
points case will not be already solved by the 2 or 3 points case.

ONE implementation of this would be:
Let us calculate the distance between all possible pairs of points. We then
take the pair of points (A & B) that are the most distant. Let P be the
midpoint of segment AB. Then find the point C which is neither A nor B and
whose distance to P is the greatest.
-If the distance CP is smaller than the distance PA (or PB), we are in the
2 points case and P is the center of the circle we are looking for (I will
call it the 2-points case circle).

-If the distance CP is greater than the distance PA (or PB), we are in
the >2 points case. 

------
Note: Adrian Daerr pointed out that the original suggestion (below) does 
not always work to find the minimal bounding circle:
------

  Geometry tells us that A and B must also be on the (new) circle border 
  (because A and B are the most distant points). Also, we know that the 
  third point we are looking for is outside the circle of the 2 points 
  case. It would seem logical to me that C is that third point which
  we can check by calculating the new circle and checking all points are
  within it.


So, instead of using the test above, one can search for the smallest 
osculating circle defined by 3 points in the convex hull that contains 
all convex hull points (and therefore all points in the set since these
are contained within the convex hull).

*/

// 26/12/2004 returns floating values.
// 22/5/2005 changed test for the case  of 3 points defining the osculating circle.
// 12/4/2007 added "width" of the convex polygon

public class ConvexHullProcessor implements PlugInFilter, Measurements {
    ImagePlus imp;
	int counter=0;
	public int setup(String arg, ImagePlus imp) {
            ImageStatistics stats;
            stats = imp.getStatistics();
            if (stats.histogram[0] + stats.histogram[255] != stats.pixelCount){
                IJ.error("8-bit binary image (0 and 255) required.");
                return DONE;
            }


            if (arg.equals("about"))
                    {showAbout(); return DONE;}
            this.imp = imp;
            return DOES_8G;
	}

    @Override
    public void run(ImageProcessor ip) {
        String myMode = "Draw Convex Hull";
        boolean white = false;

        int i, j, k=0, m, colour=0;

        if (white) 
            colour=255;
        
        //IJ.run("Set Scale...", "distance=0 known=1 pixel=1 unit=pixels"); // OK?
        imp.setCalibration(null); // OK?

        for (j=0;j<imp.getHeight();j++){
            for (i=0;i<imp.getWidth();i++){
                if (ip.getPixel(i,j) == colour)
                    counter++;
            }
        }

        int[] x = new int[counter+1];
        int[] y = new int[counter+1];

        for (j=0;j<imp.getHeight();j++){
            for (i=0;i<imp.getWidth();i++){
                if (ip.getPixel(i,j) == colour){
                    x[k] = i;
                    y[k] = j;
                    k++;
                }
            }
        }

        //cnvx hull
        int n=counter, min = 0, ney=0, px, py, h, h2, dx, dy, temp, ax, ay;
        double minangle, th, t, v, zxmi=0;

        for (i=1;i<n;i++){
            if (y[i] < y[min]) 
                min = i;
        }

        temp = x[0]; x[0] = x[min]; x[min] = temp;
        temp = y[0]; y[0] = y[min]; y[min] = temp;
        min = 0;

        for (i=1;i<n;i++){
            if (y[i] == y[0]){
                ney ++;
                if (x[i] < x[min]) min = i;
            }
        }
        temp = x[0]; x[0] = x[min]; x[min] = temp;
        temp = y[0]; y[0] = y[min]; y[min] = temp;
        ip.setColor(127);

        //first point x(0), y(0)
        px = x[0];
        py = y[0];

        min = 0;
        m = -1; 
        x[n] = x[min];
        y[n] = y[min];
        if (ney > 0) 
            minangle = -1;
        else
            minangle = 0;

        while (min != n+0 ){
            m = m + 1;
            temp = x[m]; x[m] = x[min]; x[min] = temp;
            temp = y[m]; y[m] = y[min]; y[min] = temp;

            min = n ; //+1
            v = minangle;
            minangle = 360.0;
            h2 = 0;

            for (i = m + 1;i<n+1;i++){
                dx = x[i] - x[m];
                ax = Math.abs(dx);
                dy = y[i] - y[m];
                ay = Math.abs(dy);

                if (dx == 0 && dy == 0) 
                    t = 0.0;
                else 
                    t = (double)dy / (double)(ax + ay);

                if (dx < 0)
                    t = 2.0 - t;
                else {
                    if (dy < 0)
                        t = 4.0 + t;
                }
                th = t * 90.0;

                if(th > v){
                    if(th < minangle){
                        min = i;
                        minangle = th;
                        h2 = dx * dx + dy * dy;
                    }
                    else{
                        if (th == minangle){
                            h = dx * dx + dy * dy;
                            if (h > h2){
                                min = i;
                                h2 = h;
                            }
                        }
                    }
                }
            }
            if (myMode.equals("Draw Convex Hull") || myMode.equals("Draw both"))
                ip.drawLine(px,py,x[min],y[min]);	
            px = x[min];
            py = y[min];
            zxmi = zxmi + Math.sqrt(h2);
        }
        m++;

        int[] hx = new int[m];// ROI polygon array 
        int[] hy = new int[m];

        for (i=0;i<m;i++){
            hx[i] =  x[i]; // copy to new polygon array
            hy[i] =  y[i];
        }		

        // Chiara 25.01.2013: imposto il convex hull come roi e lo riempio di nero (per poterne calcolare l'area)

        //if (myMode.equals("Convex Hull selection"))
        imp.setRoi(new PolygonRoi(hx, hy, hx.length, 2)); // roi.POLYGON
        imp.getProcessor().fill(imp.getRoi());
    }



    void showAbout() {
        IJ.showMessage("About Convex Hull Plus...",
        "Convex Hull Plus by Gabriel Landini,  G.Landini@bham.ac.uk\n"+
        "ImageJ plugin for calculating the Convex Hull and\n"+
        "the Minimal Bounding Circle of a point set.");
    }
}



