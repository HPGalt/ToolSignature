package it.corvallis.features.calculator.impl;

import it.corvallis.features.calculator.CalculatorAbstract;
import it.corvallis.features.calculator.CalculatorType;

public class ScalarCalculator extends CalculatorAbstract {

	public ScalarCalculator() {
		super(CalculatorType.Scalar);
	}
	
	public ScalarCalculator(ScalarCalculator s) {
		super(s);
	}
	
	@Override
	public double calculateDistance(Object val1, Object val2) {
		return Math.abs(((Double) val1).doubleValue() - ((Double) val2).doubleValue());
	}
	
	@Override
	public double calculateFixedTolerance(Object meanValue) {
		return ((Double) meanValue) * 0.1;
	}
}
