package it.corvallis.imageprocessing.preprocessing;



/**
 * The preprocessing enumerator. To define a new preprocessing you need to follow the following pattern:
 * 							NAME(string_name_of_the_feature_to_use_in_the_propeties_file, prefix_to_append_to_the_image_file_name, unique_id)
 * 
 * @author ChiarellaG
 *
 */
public enum PreprocessingType {
	HorizontalJoin("horizontaljoin", "horizontal_joined_", 1),
	Resize("resize", "resized_", 2),
	Rotate("rotate", "rotated_", 3),
	ParticleFilter("PARTICLEFILTER", "particle_filtered_", 5),
	Skeletonize("SKELETONIZE", "skeletonized_", 6),
	Binarize("binarize", "binarized_", 7),
	Negative("negative", "negatived_", 8),
	None("none", "noned_", 9),
	VerticalJoin("verticaljoin", "vertical_joined_", 10),
	MeanFilter("meanfilter", "mean_filter_", 11),
	Crop("crop","crop_",12),
	Dilate("dilate", "dilate_", 13),
	Pad("pad", "pad_", 14);

	private String name;
	private String prefix;
	private int id;

	private PreprocessingType(String name, String prefix, int id) {
		this.name = name.trim().toLowerCase();
		this.prefix = prefix.trim().toLowerCase();
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}
	
	public String getName() {
    	return name;
    }
	
	public int getId() {
    	return id;
    }
	
	public static PreprocessingType getPreprocessingTypeFromString(String type) {
		for (int i = 0; i < PreprocessingType.values().length; ++i) {
			if (type.trim().toLowerCase().compareTo(PreprocessingType.values()[i].getName()) == 0)
				return PreprocessingType.values()[i];
		}
    	return None;
    }

	public static PreprocessingType getPreprocessingTypeFromIndex(int id) {
		for (int i = 0; i < PreprocessingType.values().length; ++i) {
			if (id == PreprocessingType.values()[i].getId())
				return PreprocessingType.values()[i];
		}
    	return None;
    }
}
