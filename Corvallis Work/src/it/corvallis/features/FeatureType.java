package it.corvallis.features;


/**
 * The feature enumerator. To define a new feature you need to follow the following pattern:
 * 							NAME(string_name_of_the_feature_to_use_in_the_propeties_file, default_value, unique_id)
 * 																	OR
 * 					NAME(string_name_of_the_feature_to_use_in_the_propeties_file, default_value, unique_id, number_of_component)
 * 
 * @author ChiarellaG
 *
 */
public enum FeatureType {

	AreaFeature("AreaFeature", 0.0, 0),
	AspectFeature("AspectFeature", 0.0, 1),
	HistogramPosFeature("HistogramPosFeature", 0.0, 2, 2),
	HistogramValFeature("HistogramValFeature", 0.0, 3, 2),
	CentreOfGravityFeature("CentreOfGravityFeature", 0.0, 4, 2),
	TriSurfaceFeature("TriSurfaceFeature", 0.0, 5, 3),
	ConvexHullAreaFeature("ConvexHullAreaFeature", 0.0, 6),
	ConvexHullPerimeterFeature("ConvexHullPerimeterFeature", 0.0, 7),
	AreaClosePartsFeature("AreaClosePartsFeature", 0.0, 8),
	NumberOfClosePartsFeature("NumberOfClosePartsFeature", 0.0, 9),
	NumberOfTransitionsFeature("NumberOfTransitionsFeature", 0.0, 10, 2),
	PositionsOfTransitionsFeature("PositionsOfTransitionsFeature", 0.0, 11, 8),
	AreaRatioFeature("AreaRatioFeature", 0.0, 12),
	LineJunctionsFeature("LineJunctionsFeature", 0.0, 13),
	LineEndsFeature("LineEndsFeature", 0.0, 14),
	SlantPdfFeature("SlantPdfFeature", 0.0, 15, 4),
	DistributionFeature("DistributionFeature", 0.0, 16, 4),
	SixFoldSurfaceFeature("SixFoldSurfaceFeature", 0.0, 17, 6),
	CentroidFeature("CentroidFeature", 0.0, 18),
	AlternativeSixFoldSurfaceFeature("AlternativeSixFoldSurfaceFeature", 0.0, 19, 6),
	DirectionChangesFeature("DirectionChangesFeature", 0.0, 20),
	WidthAreaRatioFeature("WidthAreaRatioFeature", 0.0, 21),
	HeightAreaRatioFeature("HeightAreaRatioFeature", 0.0, 22),
	ZeroFeature("ZeroFeature", 0.0, 23),
	CentroidIncidenceAngleFeature("CentroidIncidenceAngleFeature", 0.0, 24),
	HistogramOfOrientedGradientsPdfFeature("HistogramOfOrientedGradientsPdfFeature", 0.0, 25),
	PixelsIncidenceAngleFeature("PixelsIncidenceAngleFeature", 0.0, 26),
	CurvaturePdfFeature("CurvaturePdfFeature", 0.0, 27),
	LocalBinaryPatternPdfFeature("LocalBinaryPatternPdfFeature", 0.0, 28),
	DCTFeature("DCTFeature", 0.0, 29),
	ChainCodePdfFeature("ChainCodePdfFeature", 0.0, 30),
	TortuosityFeature("TortuosityFeature", 0.0, 31),
	DirectionsPdfFeature("DirectionsPdfFeature", 0.0, 32),
	RunLengthPdfFeature("RunLengthPdfFeature", 0.0, 33),
	EdgeTrackPdfFeature("EdgeTrackPdfFeature", 0.0, 34),
	SizeAreaRatioFeature("SizeAreaRatioFeature", 0.0, 35);
	
	private String name;
	private double default_value;
	private int components_num;
	private int id;
	
	private FeatureType(String name, double default_value, int id) {
		this.name = name.trim().toLowerCase();
		this.default_value = default_value;
		this.id = id;
		this.components_num = 1;
	}
	
	private FeatureType(String name, double default_value, int id, int components) {
		this.name = name.trim().toLowerCase();
		this.default_value = default_value;
		this.id = id;
		this.components_num = components;
	}
	
    public int getNumComponents() {
        return components_num;
    }
    
    public String getName() {
    	return name;
    }
    
    public double getDefaultValue() {
    	return default_value;
    }
    
    public int getId() {
    	return id;
    }
    
    public static FeatureType getFeatureTypeFromString(String type) {
    	for (int i = 0; i < FeatureType.values().length; ++i) {
			if (type.trim().toLowerCase().compareTo(FeatureType.values()[i].getName()) == 0)
				return FeatureType.values()[i];
		}
    	return ZeroFeature;
    }
    
    public static FeatureType getFeatureTypeFromId(int id) {
    	for (int i = 0; i < FeatureType.values().length; ++i) {
			if (id == FeatureType.values()[i].getId())
				return FeatureType.values()[i];
		}
    	return ZeroFeature;
    }
}
