package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.AllVariables;

public class RAllVariables {

	AllVariables predictors;
	
	public RAllVariables(AllVariables predictors) {
		// TODO Auto-generated constructor stub
		this.predictors = predictors;
	}

	public String compile() {
		// TODO Auto-generated method stub
		return predictors.getAll();
	}

}
