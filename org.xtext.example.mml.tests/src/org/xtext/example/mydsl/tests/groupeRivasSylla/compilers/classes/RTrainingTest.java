package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.TrainingTest;

public class RTrainingTest {

	TrainingTest stratification;
	
	public RTrainingTest(TrainingTest stratification) {
		// TODO Auto-generated constructor stub
		this.stratification = stratification;
	}

	public String compile() {
		// TODO Auto-generated method stub
		return "" + stratification.getNumber();
	}

}
