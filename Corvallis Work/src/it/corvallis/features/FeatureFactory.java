package it.corvallis.features;

import it.corvallis.features.impl.AlternativeSixFoldSurfaceFeature;
import it.corvallis.features.impl.AreaClosePartsFeature;
import it.corvallis.features.impl.AreaFeature;
import it.corvallis.features.impl.AreaRatioFeature;
import it.corvallis.features.impl.AspectFeature;
import it.corvallis.features.impl.CentreOfGravityFeature;
import it.corvallis.features.impl.CentroidFeature;
import it.corvallis.features.impl.CentroidIncidenceAngleFeature;
import it.corvallis.features.impl.ChainCodePdfFeature;
import it.corvallis.features.impl.ConvexHullAreaFeature;
import it.corvallis.features.impl.ConvexHullPerimeterFeature;
import it.corvallis.features.impl.CurvaturePdfFeature;
import it.corvallis.features.impl.DCTFeature;
import it.corvallis.features.impl.DirectionChangesFeature;
import it.corvallis.features.impl.DirectionsPdfFeature;
import it.corvallis.features.impl.DistributionFeature;
import it.corvallis.features.impl.EdgeTrackPdfFeature;
import it.corvallis.features.impl.HeightAreaRatioFeature;
import it.corvallis.features.impl.HistogramOfOrientedGradientsPdfFeature;
import it.corvallis.features.impl.HistogramPosFeature;
import it.corvallis.features.impl.HistogramValFeature;
import it.corvallis.features.impl.LineEndsFeature;
import it.corvallis.features.impl.LineJunctionsFeature;
import it.corvallis.features.impl.LocalBinaryPatternPdfFeature;
import it.corvallis.features.impl.NumberOfClosePartsFeature;
import it.corvallis.features.impl.NumberOfTransitionsFeature;
import it.corvallis.features.impl.PixelsIncidenceAngleFeature;
import it.corvallis.features.impl.PositionsOfTransitionsFeature;
import it.corvallis.features.impl.RunLengthPdfFeature;
import it.corvallis.features.impl.SixFoldSurfaceFeature;
import it.corvallis.features.impl.SizeAreaRatioFeature;
import it.corvallis.features.impl.SlantPdfFeature;
import it.corvallis.features.impl.TortuosityFeature;
import it.corvallis.features.impl.TriSurfaceFeature;
import it.corvallis.features.impl.WidthAreaRatioFeature;
import it.corvallis.features.impl.ZeroFeature;

import org.apache.log4j.Logger;

/**
 * Factory class to construct feature objects.
 * When a new feature is added to the software You need to add the switch condition in the {@link #getFeature(FeatureType)} method to build that feature.
 * 
 * @author ChiarellaG
 *
 */
public class FeatureFactory {
	
	 Logger log = Logger.getLogger(FeatureFactory.class);
	 
	 /**
	  * It builds the object of the requested feature.
	  * @param feature The feature enumerator to build.
	  * @return The feature object if the enum exists, null otherwise.
	  */
	 public static AbstractFeature getFeature(FeatureType feature) {
		switch (feature) {
		case AlternativeSixFoldSurfaceFeature:
			return new AlternativeSixFoldSurfaceFeature(feature);
		case AreaFeature:
			return new AreaFeature(feature);
		case AreaClosePartsFeature:
			return new AreaClosePartsFeature(feature);
		case AreaRatioFeature:
			return new AreaRatioFeature(feature);
		case AspectFeature:
			return new AspectFeature(feature);
		case CentreOfGravityFeature:
			return new CentreOfGravityFeature(feature);
		case CentroidFeature:
			return new CentroidFeature(feature);
		case ConvexHullAreaFeature:
			return new ConvexHullAreaFeature(feature);
		case ConvexHullPerimeterFeature:
			return new ConvexHullPerimeterFeature(feature);
		case DirectionChangesFeature:
			return new DirectionChangesFeature(feature);
		case DistributionFeature:
			return new DistributionFeature(feature);
		case HistogramPosFeature:
			return new HistogramPosFeature(feature);
		case HistogramValFeature:
			return new HistogramValFeature(feature);
		case LineEndsFeature:
			return new LineEndsFeature(feature);
		case LineJunctionsFeature:
			return new LineJunctionsFeature(feature);
		case NumberOfClosePartsFeature:
			return new NumberOfClosePartsFeature(feature);
		case NumberOfTransitionsFeature:
			return new NumberOfTransitionsFeature(feature);
		case PositionsOfTransitionsFeature:
			return new PositionsOfTransitionsFeature(feature);
		case SixFoldSurfaceFeature:
			return new SixFoldSurfaceFeature(feature);
		case SlantPdfFeature:
			return new SlantPdfFeature(feature);
		case TriSurfaceFeature:
			return new TriSurfaceFeature(feature);
		case ZeroFeature:
			return new ZeroFeature(feature);
		case WidthAreaRatioFeature:
			return new WidthAreaRatioFeature(feature);
		case HeightAreaRatioFeature:
			return new HeightAreaRatioFeature(feature);
		case HistogramOfOrientedGradientsPdfFeature:
			return new HistogramOfOrientedGradientsPdfFeature(feature);
		case CentroidIncidenceAngleFeature:
			return new CentroidIncidenceAngleFeature(feature);
		case PixelsIncidenceAngleFeature:
			return new PixelsIncidenceAngleFeature(feature);
		case CurvaturePdfFeature:
			return new CurvaturePdfFeature(feature);
		case LocalBinaryPatternPdfFeature:
			return new LocalBinaryPatternPdfFeature(feature);
		case DCTFeature:
			return new DCTFeature(feature);
		case ChainCodePdfFeature:
			return new ChainCodePdfFeature(feature);
		case TortuosityFeature:
			return new TortuosityFeature(feature);
		case DirectionsPdfFeature:
			return new DirectionsPdfFeature(feature);
		case RunLengthPdfFeature:
			return new RunLengthPdfFeature(feature);
		case EdgeTrackPdfFeature:
			return new EdgeTrackPdfFeature(feature);
		case SizeAreaRatioFeature:
			return new SizeAreaRatioFeature(feature);
		default:
			return new ZeroFeature(feature);
		}
	 }
	 
}
