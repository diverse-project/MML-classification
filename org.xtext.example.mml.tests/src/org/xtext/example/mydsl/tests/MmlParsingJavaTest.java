package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaTest {

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	String data = "iris.csv";
	//String data = "new-thyroid.csv";
	
	String framework = "R";
	
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
	/* Random Forest en CrossValidation - RF */
	/* Test planté avec R et new-thyroid.csv */
	public void RandomForest1() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en CrossValidation - RandomForest */
	/* Test planté avec R et new-thyroid.csv */
	public void RandomForest2() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RandomForest\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en TrainingTest - RF */
	/* Test planté avec R et new-thyroid.csv */
	public void RandomForest3() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	
	@Test
	/* Decision Tree en TrainingTest - DT */
	public void decisionTree1() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm DT \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en TrainingTest - DecisionTree */
	public void decisionTree2() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm DecisionTree \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en CrossValidation */
	public void decisionTree3() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm DT \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree avec parametre max depth en CrossValidation */
	public void decisionTree4() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm DT 10 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree avec parametre max depth en TrainingTest */
	public void decisionTree5() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm DT 4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	
	@Test
	/* Logistic Regression en CrossValidation */
	/* Test planté avec R */
	public void logisticRegression1() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm LogisticRegression\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* Logistic Regression en Training test */
	/* Test planté avec R */
	public void logisticRegression2() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm LogisticRegression\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	
	@Test
	/* SVM sans parametres en CrossValidation */
	public void SVM1() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R \n"
				+ "algorithm SVM \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* SVM sans parametres en TrainingTest */
	public void SVM2() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R \n"
				+ "algorithm SVM \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification C */
	public void SVM3() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm SVM classification C-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification nu */
	/* Non supporté par R */
	public void SVM4() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm SVM classification nu-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification one */
	/* Non supporté par R */
	public void SVM5() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm SVM classification one-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel linear */
	public void SVM6() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm SVM kernel=linear \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel radial */
	public void SVM7() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm SVM kernel=radial \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel polynomial */
	public void SVM8() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm SVM kernel=polynomial \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec C */
	public void SVM9() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm SVM C=0.8 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec C */
	public void SVM10() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm SVM gamma=1.5 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + ajout separator , */
	public void Separator1() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\" separator ,\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + ajout separator ; */
	public void Separator2() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris2.csv\" separator ;\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée ; */
	public void Formula1() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "formula 5 ~ 1+2+3+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée ; */
	public void Formula2() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "formula 5 ~ 1+2+3 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée ; */
	public void Formula3() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "formula 1 ~ 2+3+4+5 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée ; */
	public void Formula4() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "formula \"variety\" ~ 1+2+3+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée ; */
	public void Formula5() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework " + framework + "\n"
				+ "algorithm RF\n"
				+ "formula \"variety\" ~ 1+2+\"petal.length\"+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
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