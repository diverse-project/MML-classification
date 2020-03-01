package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;

public class RMLChoiceAlgorithm {
	
	MLChoiceAlgorithm mml;

	public RMLChoiceAlgorithm(MLChoiceAlgorithm mml) {
		// TODO Auto-generated constructor stub
		this.mml = mml;
	}

	public String compile () {
		RFrameworkLang framework = new RFrameworkLang(mml.getFramework());
		RMLAlgorithm algorithm = new RMLAlgorithm(mml.getAlgorithm());
		
		String result = "";
		result += framework.compile();
		result += algorithm.compile();
		
		return result;
	}
}
