package org.xtext.example.mydsl.tests.groupMYST;

import java.io.IOException;

import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.XFormula;

public interface Compilateur {
	
	void execute() throws IOException;
	void configure(MLAlgorithm algo, DataInput dataInput, Validation validation, String separator, FormulaItem predictive, XFormula predictore, String fileResult);
}
