package it.corvallis.features;

import it.corvallis.features.calculator.CalculatorAbstract;
import it.corvallis.features.calculator.CalculatorFactory;
import it.corvallis.features.calculator.CalculatorType;


public class Feature {
	private String name = "";
	private int components = 0;
	private CalculatorType calculator = null;

	public Feature(String name, int components) {
		this.name = name;
		this.components = components;
		if (components == 1)
			this.calculator = CalculatorType.Scalar;
		else
			this.calculator = CalculatorType.Vectorial;
	}

	public Feature(Feature f) {
		name = f.name;
		components = f.components;
		calculator = f.calculator;
	}
	
	public String getName() {
		return name;
	}

	public int getComponents() {
		return components;
	}
	
	public CalculatorAbstract getCalculator() {
		return CalculatorFactory.getCalculator(calculator);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}