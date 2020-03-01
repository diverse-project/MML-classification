package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.RFormula;

public class RRFormula {
	
	RFormula mml;

	public RRFormula(RFormula formula) {
		// TODO Auto-generated constructor stub
		this.mml = formula;
	}
	
	public String compile() {
		RFormulaItem formulaItem = new RFormulaItem(mml.getPredictive());
		RXFormula predictors = new RXFormula(mml.getPredictors());
		
		String result = "";
		if (!formulaItem.isEmpty()) {
			result += formulaItem.compile();
		}
		result += predictors.compile();
		
		return result;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

}
