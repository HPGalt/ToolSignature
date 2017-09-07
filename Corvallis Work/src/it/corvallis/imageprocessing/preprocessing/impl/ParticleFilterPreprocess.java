package it.corvallis.imageprocessing.preprocessing.impl;

import ij.ImagePlus;
import it.corvallis.MainClass;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.preprocessing.AbstractPreprocess;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;

/**
 * This class performs the particle filtering of an image by means of #{@link Filter} class methods.
 * It removes the dots in an image.
 * 
 * @author ChiarellaG
 *
 */
public class ParticleFilterPreprocess extends AbstractPreprocess {

	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		minimum_particle_size("minimum_particle_size", "0.0"),
		maximum_particle_size("maximum_particle_size", "70.0"),
		minimum_particle_circularity("minimum_particle_circularity", "0.0"),
		maximum_particle_circularity("maximum_particle_circularity", "1.0");
        
        private String name;
        private String value;
        
        private CONFIGURATION_OPTIONS(String name, String default_value) {
            this.name = name.trim().toLowerCase();
            this.value = default_value.trim().toLowerCase();
        }
        
        @Override
        public String getPropertyName() {
            return name;
        }
        
        @Override
        public String getDefaultValue() {
            return value;
        }
        
    }

	/**
	 * Default constructor. It only calls the super() constructor.
	 * @param p The preprocessing type.
	 */
	public ParticleFilterPreprocess(PreprocessingType p) {
		super(p);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractPreprocess}) constructor.
	 * @param p The object to copy.
	 */
	public ParticleFilterPreprocess(ParticleFilterPreprocess p) {
		super(p);
	}

	@Override
	public ImagePlus performPreprocess(ImagePlus img, double... params) {
		double minimum_particle_size = params[0];
		double maximum_particle_size = params[1];
		double minimum_particle_circularity = params[2];
		double maximum_particle_circularity = params[3];
		img = Filter.ParticleFilter(img, minimum_particle_size, maximum_particle_size, minimum_particle_circularity, maximum_particle_circularity);
		return img;
	}

}
