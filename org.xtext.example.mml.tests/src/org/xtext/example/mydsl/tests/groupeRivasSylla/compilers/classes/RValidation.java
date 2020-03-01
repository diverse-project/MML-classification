package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import java.util.Iterator;
import java.util.List;

import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;

public class RValidation {
	
	Validation mml;

	public RValidation(Validation validation) {
		// TODO Auto-generated constructor stub
		this.mml = validation;
	}
	
	public String compile() {
		String result = "";

		RStratificationMethod stratification = new RStratificationMethod(mml.getStratification());
		result += stratification.compile();

		List<ValidationMetric> metrics = mml.getMetric();
		Iterator<ValidationMetric> it = metrics.iterator();
		while (it.hasNext()) {
			RValidationMetric metric = new RValidationMetric(it.next());
			result += metric.compile();
		}
				
		return result;
	}

}
