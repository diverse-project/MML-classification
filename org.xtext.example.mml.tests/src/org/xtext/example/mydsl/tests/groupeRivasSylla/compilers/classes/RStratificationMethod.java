package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.StratificationMethod;
import org.xtext.example.mydsl.mml.TrainingTest;

public class RStratificationMethod {

	StratificationMethod stratification;

	public RStratificationMethod(StratificationMethod stratification) {
		// TODO Auto-generated constructor stub
		this.stratification = stratification;
	}

	public String compile() {
		// TODO Auto-generated method stub
		String result = "";
		if (stratification instanceof CrossValidation) {
			RCrossValidation crossValidation = new RCrossValidation((CrossValidation) stratification); 
			result += crossValidation.compile();
		}
		if (stratification instanceof TrainingTest) {
			RTrainingTest trainingTest = new RTrainingTest((TrainingTest)stratification);
			result += trainingTest.compile();
		}
		return result;
	}

}
