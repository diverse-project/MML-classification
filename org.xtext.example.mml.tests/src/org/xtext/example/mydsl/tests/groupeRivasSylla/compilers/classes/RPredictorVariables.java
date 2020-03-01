package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import java.util.Iterator;
import java.util.List;

import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.PredictorVariables;

public class RPredictorVariables {

	PredictorVariables predictors;

	public RPredictorVariables(PredictorVariables predictors) {
		// TODO Auto-generated constructor stub
		this.predictors = predictors;
	}

	public String compile() {
		// TODO Auto-generated method stub
		String result = "";
		List<FormulaItem> vars = predictors.getVars();
		Iterator<FormulaItem> it = vars.iterator();
		while (it.hasNext()) {
			RFormulaItem var = new RFormulaItem(it.next());
			result += var.compile();
		}
		return result;
	}

}
