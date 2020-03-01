package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.ValidationMetric;

public class RValidationMetric {

	ValidationMetric metric;

	public RValidationMetric(ValidationMetric metric) {
		// TODO Auto-generated constructor stub
		this.metric = metric;
	}

	public String compile() {
		// TODO Auto-generated method stub
		switch (metric.toString()) {
			case "balanced_accuracy" : break;
			case "recall" : break;
			case "precision" : break;
			case "F1" : break;
			case "accuracy" : break;
			case "macro_recall" : break;
			case "macro_precision" : break;
			case "macro_F1" : break;
			case "macro_accuracy" : break;
		}
		return null;
	}

}
