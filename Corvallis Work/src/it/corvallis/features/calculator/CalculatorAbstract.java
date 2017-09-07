package it.corvallis.features.calculator;

import org.apache.log4j.Logger;

/**
 * It is the base class extended by a new calculator object. A calculator is a class that implements some methods which change basing on the feature type.
 * This class is needed to the DistanceClassifierX.
 * 
 * @author ChiarellaG
 *
 */
public abstract class CalculatorAbstract {
	
	protected Logger log = Logger.getLogger(CalculatorAbstract.class.getName());
	private CalculatorType calculator = null;
	
	/**
	 * Default constructor. Do nothing.
	 * @param c The copying object.
	 */
	public CalculatorAbstract(CalculatorType c) {
		calculator = c;
	}
	
	/**
	 * Copy constructor. Do Nothing.
	 * @param c The object to copy.
	 */
	public CalculatorAbstract(CalculatorAbstract c) {
		calculator = c.calculator;
	}
	
	public CalculatorType getType() {
		return calculator;
	}
	
	/**
	 * This method implements the distance algorithm between the features which has to use this calculator.
	 * @param val1 The first operand.
	 * @param val2 The second operand.
	 * @return The distance between operands.
	 */
	public abstract double calculateDistance(Object val1, Object val2);
	
	/**
	 * This method implements a fixed tolerance computing algorithm depending on the type of the feature which will use it.
	 * @param val The values Object of which compute the fixed tolerance.
	 * @return The fixed tolerance.
	 */
	public abstract double calculateFixedTolerance(Object val);

}
