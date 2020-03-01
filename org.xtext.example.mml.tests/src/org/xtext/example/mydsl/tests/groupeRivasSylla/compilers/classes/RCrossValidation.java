package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.CrossValidation;

public class RCrossValidation {

	CrossValidation stratification;
	
	public RCrossValidation(CrossValidation stratification) {
		// TODO Auto-generated constructor stub
		this.stratification = stratification;
	}

	public String compile() {
		// TODO Auto-generated method stub
		return "" + stratification.getNumber();
	}

}
