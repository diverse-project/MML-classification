package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.AllVariables;
import org.xtext.example.mydsl.mml.PredictorVariables;
import org.xtext.example.mydsl.mml.XFormula;

public class RXFormula {

	XFormula predictors;
	
	public RXFormula(XFormula predictors) {
		// TODO Auto-generated constructor stub
		this.predictors = predictors;
	}

	public String compile() {
		// TODO Auto-generated method stub
		String result = "";
		if (predictors instanceof AllVariables) {
			RAllVariables variables = new RAllVariables((AllVariables) predictors);
			result += variables.compile();
		}
		if (predictors instanceof PredictorVariables) {
			RPredictorVariables variables = new RPredictorVariables((PredictorVariables) predictors);
			result += variables.compile();
		}
		return result;
	}

}
