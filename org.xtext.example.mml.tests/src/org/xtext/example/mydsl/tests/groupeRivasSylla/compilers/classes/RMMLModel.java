package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import java.util.Iterator;
import java.util.List;

import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;

public class RMMLModel {
	
	MMLModel mml;
	
	public RMMLModel(MMLModel mml) {
		// TODO Auto-generated constructor stub
		this.mml = mml;
	}

	public String compile() {
		RDataImput input = new RDataImput(mml.getInput());
		List<MLChoiceAlgorithm> algorithms = mml.getAlgorithms();
		RRFormula formula = new RRFormula(mml.getFormula());
		RValidation validation = new RValidation(mml.getValidation());
		
		String result = "";
		result += input.compile();
		
		Iterator<MLChoiceAlgorithm> it = algorithms.iterator();
		while (it.hasNext()) {
			RMLChoiceAlgorithm algorithm = new RMLChoiceAlgorithm(it.next());
			result += algorithm.compile();
		}
		
		if (!formula.isEmpty()) {
			result += formula.compile();
		}
		
		result += validation.compile();
		
		return result;
	}
}
