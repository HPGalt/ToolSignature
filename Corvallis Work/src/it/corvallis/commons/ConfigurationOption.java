package it.corvallis.commons;

/**
 * The interface ConfigurationOption is implemented by the enumerator class which implements the properties in the configuration files.
 * 
 * @author ChiarellaG
 *
 */
public interface ConfigurationOption {
    
	/**
	 * It is the method that gets the property name.
	 * @return The property name.
	 */
    public String getPropertyName();
    
    /**
     * It is the method that gets the default value.
     * @return The default value as string.
     */
    public String getDefaultValue();
    
}
