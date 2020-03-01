package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.SVMClassification;

public class RSVMClassification {

	SVMClassification svmclassification;

	public RSVMClassification(SVMClassification svmclassification) {
		// TODO Auto-generated constructor stub
		this.svmclassification = svmclassification;
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	public String compile() {
		// TODO Auto-generated method stub
		switch (svmclassification.toString()) {
			case "C-classification" : break;
			case "nu-classification" : break;
			case "one-classification" : break;
		};
		return "";
	}

}
