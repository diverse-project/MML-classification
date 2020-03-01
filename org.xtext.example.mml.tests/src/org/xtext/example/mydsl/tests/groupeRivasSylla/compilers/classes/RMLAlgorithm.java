package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;

public class RMLAlgorithm {

	MLAlgorithm mml;
	
	public RMLAlgorithm(MLAlgorithm algorithm) {
		// TODO Auto-generated constructor stub
		this.mml = algorithm;
	}

	public String compile() {
		// TODO Auto-generated method stub
		String result = "";
		if (mml instanceof SVM) {
			 RSVM svm = new RSVM((SVM) mml);
			 result += svm.compile();
		}
		if (mml instanceof DT) {
			 RDT dt = new RDT((DT) mml);
			 result += dt.compile();
		}
		if (mml instanceof RandomForest) {
			 RRandomForest randomForest = new RRandomForest((RandomForest) mml);
			 result += randomForest.compile();
		}
		if (mml instanceof LogisticRegression) {
			 RLogisticRegression logisticRegression = new RLogisticRegression((RandomForest) mml);
			 result += logisticRegression.compile();
		}
		return result;
	}

}
