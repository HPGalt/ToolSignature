package it.corvallis.imageprocessing.preprocessing.impl;

import fiji.selection.Select_Bounding_Box;
import ij.ImagePlus;
import ij.gui.Roi;
import it.corvallis.MainClass;
import it.corvallis.commons.ConfigurationOption;
import it.corvallis.imageprocessing.filtering.Filter;
import it.corvallis.imageprocessing.preprocessing.AbstractPreprocess;
import it.corvallis.imageprocessing.preprocessing.PreprocessingType;

/**
 * This class performs the resizing of an image by means of #{@link Filter} class methods.
 * 
 * @author ChiarellaG
 *
 */
public class CropPreprocess extends AbstractPreprocess {
	
	public enum CONFIGURATION_OPTIONS implements ConfigurationOption {

		;
        
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
	public CropPreprocess(PreprocessingType p) {
		super(p);
	}

	/**
	 * Copy constructor. It calls the super({@link AbstractPreprocess}) constructor.
	 * @param p The object to copy.
	 */
	public CropPreprocess(CropPreprocess p) {
		super(p);
	}

	@Override
	public ImagePlus performPreprocess(ImagePlus img, double... params) {
		Select_Bounding_Box cropper = new Select_Bounding_Box();
		cropper.setup("autoselect", img);
		cropper.run(img.getProcessor());
		Roi cropRoi = null;
		cropRoi = img.getRoi();
		if (cropRoi != null) {
			img.getProcessor().setRoi(cropRoi.getBounds());
			img.setProcessor(img.getProcessor().crop());
		}
		return img;
	
	}

}
