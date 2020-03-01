package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.SVM;

public class RSVM {

	SVM mml;
	public RSVM(SVM mml) {
		// TODO Auto-generated constructor stub
		this.mml = mml;
	}

	public String compile() {
		// TODO Auto-generated method stub
		
		String result = "";
		RSVMKernel kernel = new RSVMKernel(mml.getKernel());
		RSVMClassification svmclassification = new RSVMClassification(mml.getSvmclassification());
		
		if (!kernel.isEmpty()) {
			result += kernel.compile();
		}
		if (!svmclassification.isEmpty()) {
			result += svmclassification.compile();
		}
		
		return result;
	}

}
