package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.FormulaItem;

public class RFormulaItem {

	FormulaItem predictive;

	public RFormulaItem(FormulaItem predictive) {
		// TODO Auto-generated constructor stub
		this.predictive = predictive;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public String compile() {
		// TODO Auto-generated method stub
		return predictive.getColumn() + predictive.getColName();
	}

}
