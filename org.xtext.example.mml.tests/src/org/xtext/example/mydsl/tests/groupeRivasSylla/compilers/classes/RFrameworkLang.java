package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.FrameworkLang;

public class RFrameworkLang {

	FrameworkLang mml;
	
	public RFrameworkLang(FrameworkLang framework) {
		// TODO Auto-generated constructor stub
		this.mml = framework;
	}

	public String compile() {
		// TODO Auto-generated method stub
		switch (mml.toString()) {
			case "scikit-learn" : break;
			case "R" : break;
			case "Weka" : break;
		};
		return "";
	}

}
