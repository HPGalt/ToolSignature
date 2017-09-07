package it.corvallis.features.calculator;

import it.corvallis.features.calculator.impl.ScalarCalculator;
import it.corvallis.features.calculator.impl.VectorialCalculator;

/**
 * Calculator factory. It builds a requested calculator object based on the
 * enumerator.
 * 
 * @author ChiarellaG
 *
 */
public class CalculatorFactory {

	/**
	 * Builds the requested calculator.
	 * @param calculator The enumerator of the calculator to build.
	 * @return The object of the requested calculator.
	 */
	public static CalculatorAbstract getCalculator(CalculatorType calculator) {
		switch (calculator) {
			case Scalar:
				return new ScalarCalculator();
			case Vectorial:
				return new VectorialCalculator();
			default:
				return null;
		}

	}

}
