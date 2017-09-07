package it.corvallis.features.calculator.impl;

import it.corvallis.commons.MeasurementUtils;
import it.corvallis.features.calculator.CalculatorAbstract;
import it.corvallis.features.calculator.CalculatorType;


public class VectorialCalculator extends CalculatorAbstract {
	
	public VectorialCalculator() {
		super(CalculatorType.Vectorial);
	}
	
	public VectorialCalculator(VectorialCalculator v) {
		super(v);
	}
	
	@Override
	public double calculateDistance(Object val1, Object val2) {
		double[] difference = MeasurementUtils.arrayDifference((double[]) val1, (double[]) val2);
		return MeasurementUtils.calculateNorm(difference) / Math.sqrt(difference.length);
	}

	@Override
	public double calculateFixedTolerance(Object meanVector) {
		double[] newVector = new double[((double[]) meanVector).length];
		for (int i = 0; i < newVector.length; ++i) {
			newVector[i] = ((double[]) meanVector)[i] * 0.1;
		}
		return MeasurementUtils.calculateNorm(newVector) / Math.sqrt(newVector.length);
	}
}
