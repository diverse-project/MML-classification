package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.InputStreamReader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaTest {

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	@Test
	public void loadModel() throws Exception {
		MMLModel result = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "recall\n"
				+ "");
		Assertions.assertNotNull(result);
		EList<Resource.Diagnostic> errors = result.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");			
		Assertions.assertEquals("foo.csv", result.getInput().getFilelocation());			
		
	}
	
	@Test
	public void model1() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM\n"
				+ "formula 5 ~ 1+2+3+4 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	public void model2() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm DT\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	public void model3() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm RandomForest\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	/* Problème avec LogisticRegression */
	@Test
	public void model4() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm LogisticRegression\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	public void model5() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	public void model6() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	public void model7() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm RandomForest\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	/* Problemes avec LogisticRegression */
	@Test
	public void model8() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm LogisticRegression\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	/* Problemes avec la classification nu de SVM */
	@Test
	public void model9() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM classification nu-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	/* Problemes avec la classification one de SVM */
	@Test
	public void model10() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM classification one-classification\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	public void model11() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM classification C-classification\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	private void compileDataInput(MMLModel model) throws Exception {
		
		MLChoiceAlgorithm[] algos = (MLChoiceAlgorithm[]) model.getAlgorithms().toArray();

		for(int i = 0; i < algos.length; i++) {
			MLAlgorithm al = (MLAlgorithm) algos[i].getAlgorithm();
			MmlParsingJavaCompilerR compiler = new MmlParsingJavaCompilerR();
			Boolean executionReussie = compiler.compileDataInput(model,al,i+1);
			assertTrue(executionReussie);
		}
		
	}

	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}


}